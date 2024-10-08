package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCell;
import CoreParts.api.sheet.SheetCellViewOnly;
import DtoComponents.DtoSheetCell;
import Utility.CellUtils;
import Utility.Exception.*;
import Utility.RefDependencyGraph;
import Utility.RefGraphBuilder;
import expression.ReturnedValueType;
import expression.api.Expression;
import expression.impl.Range;
import expression.impl.Ref;
import expression.impl.numFunction.Average;
import expression.impl.numFunction.Sum;
import smallParts.CellLocation;
import smallParts.CellLocationFactory;
import smallParts.EffectiveValue;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class SheetCellImp implements SheetCell, Serializable, SheetCellViewOnly
{
    private static final long serialVersionUID = 1L; // Add serialVersionUID
    private Map<CellLocation, Cell> sheetCell = new HashMap<>(); // Changed to map of CellLocation to Cell
    private RefDependencyGraph refDependencyGraph = new RefDependencyGraph();
    private Set<Range> systemRanges = new HashSet<>();
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


    public SheetCellImp(int row, int col, String sheetName, int currentCellLength, int currentCellWidth, Set<Range> systemRanges) {
        if (row > maxRows || col > maxCols) {
            throw new IllegalArgumentException("Row and column numbers exceed maximum allowed limits.");
        }
        this.name = sheetName;
        currentNumberOfRows = row;
        currentNumberOfCols = col;
        this.currentCellLength = currentCellLength;
        this.currentCellWidth = currentCellWidth;
        versionControlManager = new VersionControlManager(new HashMap<>(), this);
        if(systemRanges != null)
        {
            this.systemRanges = systemRanges;
        }
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
    public Map<CellLocation, EffectiveValue> getViewSheetCell() {
        return new DtoSheetCell(this).getViewSheetCell();
    }

    @Override
    public Map<CellLocation, Cell> getSheetCell() {return sheetCell;}

    @Override
    public Map<Character,Set<String>> getUniqueStringsInColumn(String filterColumn, String range) {

        Map<Character,Set<String>> columnToUniqueStrings = new HashMap<>();
        List<Character> columns = CellUtils.processCharString(filterColumn);
        int startingRowInRange = range.charAt(1) - '0';
        int endingRowInRange = range.charAt(5) - '0'; //A3..A5

        Set<String> uniqueStrings = new HashSet<>();

        for (Character col : columns) {
            // Convert column to uppercase for consistent matching
            char upperCol = Character.toUpperCase(col);

            // Retrieve values from the view sheet and collect unique values
            getViewSheetCell().forEach((location, effectiveValue) -> {
                if (location.getVisualColumn() == upperCol && location.getRealRow() + 1 >= startingRowInRange && location.getRealRow() + 1 <= endingRowInRange) {

                    if (effectiveValue != null) {
                        String value = effectiveValue.getValue().toString();

                        try{
                            double doubleValue = Double.parseDouble(value);
                            value = CellUtils.formatNumber(doubleValue);
                        }
                        catch (NumberFormatException e) {
                        }
                        uniqueStrings.add(value);
                    }
                }
            });
            Set<String> copy = Set.copyOf(uniqueStrings);
            columnToUniqueStrings.put(upperCol, copy);
            uniqueStrings.clear();
        }

        return columnToUniqueStrings;
    }

    public Map<Character,Set<String>> getUniqueStringsInColumn(List<Character> columns, boolean isChartGraph){

        Map<Character,Set<String>> columnToUniqueStrings = new HashMap<>();

        Set<String> uniqueStrings = new HashSet<>();

        for (Character col : columns) {
            // Convert column to uppercase for consistent matching
            char upperCol = Character.toUpperCase(col);

            // Retrieve values from the view sheet and collect unique values
            getViewSheetCell().forEach((location, effectiveValue) -> {
                if (location.getVisualColumn() == upperCol ) {

                    if (effectiveValue != null) {
                        String value = effectiveValue.getValue().toString();
                        if(isChartGraph){
                            try{
                                double doubleValue = Double.parseDouble(value);
                                value = CellUtils.formatNumber(doubleValue);
                            }
                            catch (NumberFormatException e) {
                            }
                            uniqueStrings.add(value);
                        }
                        else {
                            try {
                                double doubleValue = Double.parseDouble(value);
                                value = CellUtils.formatNumber(doubleValue);
                                uniqueStrings.add(value);

                            } catch (NumberFormatException e) {
                            }
                        }

                    }
                }
            });

            Set<String> copy = Set.copyOf(uniqueStrings);
            columnToUniqueStrings.put(upperCol, copy);
            uniqueStrings.clear();
        }

        return columnToUniqueStrings;
    }


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
    public void setUpSheet() throws CycleDetectedException, CellCantBeEvaluatedException {
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
    public void updateNewRange(String name, String range) {
        // Split the range string into start and end cell IDs

        for (Range rangeInSystem : systemRanges) {
            if(rangeInSystem.getRangeName().equals(name))
            {
                throw new RangeNameAlreadyExistException(name);
            }
        }

        String[] cells = range.split("\\.\\.");

        // Validate the range format
        if (cells.length != 2) {
            throw new IllegalArgumentException("Invalid range format. Expected format: 'A1..C3'");
        }

        // Convert the start and end cell IDs to CellLocation objects
        CellLocation startCell = CellLocationFactory.fromCellId(cells[0]);
        CellLocation endCell = CellLocationFactory.fromCellId(cells[1]);

        // Get the column and row indices for the start and end cells
        char startCol = startCell.getVisualColumn();
        char endCol = endCell.getVisualColumn();
        int startRow = Integer.parseInt(startCell.getVisualRow());
        int endRow = Integer.parseInt(endCell.getVisualRow());

        // Ensure the columns and rows are in the correct order
        if (startCol > endCol || startRow > endRow) {
            throw new IllegalArgumentException("Invalid range: start cell must be before end cell.");
        }

        // Create a set to hold all CellLocation objects in the range
        Set<Ref> cellLocations = new HashSet<>();

        // Loop through the columns and rows to generate all CellLocations in the range
        for (char col = startCol; col <= endCol; col++) {
            for (int row = startRow; row <= endRow; row++) {
                String cellId = col + Integer.toString(row);
                CellLocation cellLocation = CellLocationFactory.fromCellId(cellId);
                cellLocations.add(new Ref(cellLocation));
            }
        }
        // Create a new Range object with the specified name and the set of CellLocations
        Range newRange = new Range(cellLocations, name);

        // Add the new Range to the list of ranges
        systemRanges.add(newRange);
    }

    @Override
    public boolean isRangePresent(String rangeName) {
        for (Range range : systemRanges) {
            if (range.getRangeName().equals(rangeName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Range> getSystemRanges() {
        return systemRanges;
    }

    @Override
    public Range getRange(String rangeName) {
        for (Range range : systemRanges) {
            if (range.getRangeName().equals(rangeName)) {
                return range;
            }
        }
        return null;
    }

    @Override
    public List<CellLocation> getRequestedRange(String rangeName) {
        return getRange(rangeName).getCellLocations();
    }

    @Override
    public Map<String, List<CellLocation>> getRanges() {
        return null;
    }

    @Override
    public void updateVersions(Cell targetCell) {
        versionControlManager.updateVersions(targetCell);
    }

    @Override
    public void versionControl() {versionControlManager.versionControl();}

    @Override
    public void performGraphOperations() throws CycleDetectedException, CellCantBeEvaluatedException {

        createRefDependencyGraph();

        List<Cell> cells = getRefDependencyGraph().topologicalSort();

        updateEffectedByAndOnLists();


        cells.forEach(cell ->{

            Object obj = cell.getActualValue().getValue();

            //if(cell.getActualValue().getCellType() != ReturnedValueType.EMPTY){
                cell.setActualValue(this);

         //   }

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
        if(newValue.isEmpty()){

            targetCell.setActualValue(new EffectiveValue(ReturnedValueType.EMPTY, ""));
            String className = targetCell.getEffectiveValue().getClass().getSimpleName();
            String sumClassName = Sum.class.getSimpleName();
            String avgClassName = Average.class.getSimpleName();

            if(className.equals(sumClassName)){
                Sum sumExpression = (Sum) targetCell.getEffectiveValue();
                sumExpression.getRange().removeAffectedFromThisRangeCellLocation(targetCell.getLocation());
            }

            if(className.equals(avgClassName)) {
                Average averageExpression = (Average) targetCell.getEffectiveValue();
                averageExpression.getRange().removeAffectedFromThisRangeCellLocation(targetCell.getLocation());
            }

            targetCell.setEffectiveValue(expression);
        }
        else{
            targetCell.setEffectiveValue(expression);
            targetCell.setActualValue(this);
        }
    }

    @Override
    public void deleteRange(String name) {

        Range range = getRange(name);

        if(range == null)
        {
            throw new RangeDoesntExistException(name);
        }

        if(range.canBeDeleted())
        {

            for (Range rangeInSystem : systemRanges) {
                if(rangeInSystem.getRangeName().equals(name))
                {
                    systemRanges.remove(rangeInSystem);
                    break;
                }
            }
        }
        else{
            throw new RangeCantBeDeletedException(name, range.getAffectingCellLocations());
        }

    }

    @Override
    public Set<String> getAllRangeNames() {
        Set<String> rangeNames = new HashSet<>();
        for (Range range : systemRanges) {
            rangeNames.add(range.getRangeName());
        }
        return rangeNames;
    }
}
