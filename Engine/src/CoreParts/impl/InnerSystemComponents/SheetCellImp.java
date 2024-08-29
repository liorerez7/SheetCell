package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCell;
import CoreParts.api.sheet.SheetCellViewOnly;
import CoreParts.smallParts.CellLocation;
import Utility.CellUtils;
import Utility.Exception.CellCantBeEvaluated;
import Utility.Exception.CycleDetectedException;
import Utility.RefDependencyGraph;
import Utility.RefGraphBuilder;
import expression.api.EffectiveValue;
import expression.api.Expression;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//TODO: Implement the SheetCell interface
public class SheetCellImp implements SheetCell, Serializable, SheetCellViewOnly
{
    private static final long serialVersionUID = 1L; // Add serialVersionUID
    private Map<CellLocation, Cell> sheetCell = new HashMap<>(); // Changed to map of CellLocation to Cell
    private RefDependencyGraph refDependencyGraph = new RefDependencyGraph();
    VersionControlManager versionControlManager;
    private RefGraphBuilder refGraphBuilder;
    private final String name;
    private int versionNumber = 1;
    private static final int maxRows = 50;
    private static final int maxCols = 20;
    private int currentNumberOfRows;
    private int currentNumberOfCols;
    private int currentCellLength;
    private int currentCellWidth;


    public SheetCellImp(int row, int col, String sheetName, int currentCellLength, int currentCellWidth) {
        if (row > maxRows || col > maxCols) {
            throw new IllegalArgumentException("Row and column numbers exceed maximum allowed limits.");
        }
        this.name = sheetName;
        currentNumberOfRows = row;
        currentNumberOfCols = col;
        this.currentCellLength = currentCellLength;
        this.currentCellWidth = currentCellWidth;
        versionControlManager = new VersionControlManager(new HashMap<>(), this);
    }
    @Override
    public SheetCell restoreSheetCell(int versionNumber) {return null;}

    @Override
    public void createRefDependencyGraph() {
         refGraphBuilder = new RefGraphBuilder(this);
         refGraphBuilder.build();
    }
    @Override
    public RefDependencyGraph getRefDependencyGraph() {return refDependencyGraph;}
    @Override
    public void setCell(CellLocation location, Cell cell) {sheetCell.put(location, cell);}
    @Override
    public int getActiveCellsCount() {return sheetCell.size();}

    @Override
    public RefDependencyGraph getGraph() {return refDependencyGraph;}

    @Override
    public Cell getCell(CellLocation location) {

        if(location.getRealRow() >= currentNumberOfRows || location.getRealColumn() >= currentNumberOfCols) {

            throw new IllegalArgumentException("Invalid cell location");
        }
        return sheetCell.computeIfAbsent(location, loc -> new CellImp(loc));
    }
    @Override
    public void updateVersion() {versionNumber++;}
    @Override
    public void clearVersionNumber(){versionNumber = 1;}
    @Override
    public int getCellLength() {return currentCellLength;}
    @Override
    public int getCellWidth() {return currentCellWidth;}
    @Override
    public int getLatestVersion() {return versionNumber;}

    @Override
    public int getNumberOfRows() {return currentNumberOfRows;}
    @Override
    public int getNumberOfColumns() {return currentNumberOfCols;}
    @Override
    public String getSheetName() {return name;}

    @Override
    public boolean isCellPresent(CellLocation location) {return sheetCell.containsKey(location);}

    @Override
    public Map<CellLocation, Cell> getSheetCell() {return sheetCell;}

    @Override
    public void updateEffectedByAndOnLists() {
        Map<Cell,Set<Cell>> adjacencyList= refDependencyGraph.getadjacencyList();
        Map<Cell,Set<Cell>> reverseAdjacencyList = refDependencyGraph.getreverseAdjacencyList();
        sheetCell.forEach((location, cell) -> {
            if(adjacencyList.containsKey(cell))
                cell.setEffectingOn(adjacencyList.get(cell));
            if(reverseAdjacencyList.containsKey(cell))
                cell.setAffectedBy(reverseAdjacencyList.get(cell));
        });
    }

    public byte[] saveSheetCellState() throws IllegalStateException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save the sheetCell state", e);
        }
    }
    @Override
    public void removeCell(CellLocation cellLocation) {sheetCell.remove(cellLocation);}

    @Override
    public Map<Integer, Map<CellLocation, EffectiveValue>> getVersions() {return versionControlManager.getVersions();}

    @Override
    public void setUpSheet() throws CycleDetectedException, CellCantBeEvaluated {
        createRefDependencyGraph();
        List<Cell> topologicalOrder = refDependencyGraph.getTopologicalSortOfExpressions();
        topologicalOrder.forEach(cell -> {
            Expression expression = CellUtils.processExpressionRec(cell.getOriginalValue(), cell, this, false);
            expression.evaluate(this);
            cell.setEffectiveValue(expression);
            cell.setActualValue(this);
            cell.updateVersion(getLatestVersion());
        });
        versionControlManager.versionControl();
        updateEffectedByAndOnLists();
    }

    @Override
    public void updateVersions(Cell targetCell) {
        versionControlManager.updateVersions(targetCell);
    }
    @Override
    public void versionControl() {versionControlManager.versionControl();}
    @Override

    public void performGraphOperations() throws CycleDetectedException, CellCantBeEvaluated {

        createRefDependencyGraph();

        List<Cell> cells = getRefDependencyGraph().topologicalSort();

        updateEffectedByAndOnLists();

        cells.forEach(cell ->{

            Object obj = cell.getActualValue().getValue();

            cell.setActualValue(this);

            if(!obj.equals(cell.getActualValue().getValue()))
            {
                cell.updateVersion(this.getLatestVersion());
            }
        } );
    }

    @Override
    public void applyCellUpdates(Cell targetCell, String newValue, Expression expression)
    {
        targetCell.setOriginalValue(newValue);
        targetCell.setEffectiveValue(expression);
        targetCell.setActualValue(this);
    }

}
