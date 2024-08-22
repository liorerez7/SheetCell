package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.api.SheetCell;
import CoreParts.api.SheetCellViewOnly;
import CoreParts.smallParts.CellLocation;
import Utility.RefDependencyGraph;
import Utility.RefGraphBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//TODO: Implement the SheetCell interface
public class SheetCellImp implements SheetCell, Serializable, SheetCellViewOnly
{
    private Map<CellLocation, Cell> sheetCell = new HashMap<>(); // Changed to map of CellLocation to Cell
    private RefDependencyGraph refDependencyGraph = new RefDependencyGraph();
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
    }

    @Override
    public SheetCell restoreSheetCell(int versionNumber) {
        return null;
    }
    public void createRefDependencyGraph() {
         refGraphBuilder = new RefGraphBuilder(this);
         refGraphBuilder.build();
    }
    public RefDependencyGraph getRefDependencyGraph() {
        return refDependencyGraph;
    }
    @Override
    public void setCell(CellLocation location, Cell cell) {
        sheetCell.put(location, cell);
    }

    @Override
    public int getActiveCellsCount() {
        return sheetCell.size();
    }

    @Override
    public RefDependencyGraph getGraph() {
        return refDependencyGraph;
    }

    // Get a cell based on its CellLocation
    // Method to get a cell or create it if it doesn't exist
    @Override
    public Cell getCell(CellLocation location) {
        // If the cell does not exist, create and add it to the map dynamically
        return sheetCell.computeIfAbsent(location, loc -> new CellImp(loc));
    }
    public void updateVersion() {
        versionNumber++;
    }
    @Override
    public int getCellLength() {
        return currentCellLength;
    }

    @Override
    public int getCellWidth() {
        return currentCellWidth;
    }

    @Override
    public int getLatestVersion() {
        return versionNumber;
    }

    @Override
    public int getNumberOfRows() {
        return currentNumberOfRows;
    }

    @Override
    public int getNumberOfColumns() {
        return currentNumberOfCols;
    }

    @Override
    public String getSheetName() {
        return name;
    }
    // Helper method to check if a location is valid
    private boolean isValidLocation(CellLocation location) {
        int row = location.getRealRow();
        int col = location.getRealColumn();

        return row >= 0 && row < maxRows && col >= 0 && col < maxCols;
    }

    // Update a cell's value at a specific location
    public void updateCell(CellLocation location, Cell newCell) {
        if (!isValidLocation(location)) {
            throw new IllegalArgumentException("Invalid cell location");
        }
        sheetCell.put(location, newCell);
    }

    public Map<CellLocation, Cell> getSheetCell() {
        return sheetCell;
    }
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
}
