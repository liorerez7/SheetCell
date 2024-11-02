package dto.components;


import dto.small_parts.CellLocationFactory;
import engine.core_parts.api.Cell;
import engine.core_parts.api.sheet.SheetCell;
import engine.utilities.CellUtils;
import engine.utilities.EngineUtilities;
import engine.expression.impl.Range;
import dto.small_parts.CellLocation;
import dto.small_parts.EffectiveValue;


import java.io.*;
import java.util.*;

public class DtoSheetCell implements Serializable {

    private Map<CellLocation, EffectiveValue> cellLocationToEffectiveValue = new HashMap<>();
    private Map<String,List<CellLocation>> ranges = new HashMap<>();
    private Map<String,DtoCell> cellIdToDtoCell = new HashMap<>();
    private Map<String,String> predictedValues;
    private boolean isPredictedValuesWorked;

    private String name;
    private int versionNumber;
    private int currentNumberOfRows;
    private int currentNumberOfCols;
    private int currentCellLength;
    private int currentCellWidth;
    private String visualSheetSize;

    public DtoSheetCell(SheetCell sheetCellImp) {
        for (Map.Entry<CellLocation, Cell> entry : sheetCellImp.getSheetCell().entrySet()) {
            EffectiveValue effectiveValue;
            effectiveValue= entry.getValue().getEffectiveValue().evaluate(sheetCellImp);

            cellLocationToEffectiveValue.put(entry.getKey(), effectiveValue);
        }
        //versionToCellsChanges = sheetCellImp.getVersions();
        copyBasicTypes(sheetCellImp);
        Set<Range> systemRanges = sheetCellImp.getSystemRanges();
        systemRanges.forEach(range -> {
            ranges.put(range.getRangeName(),range.getCellLocations());
        });
        visualSheetSize = currentNumberOfRows + "x" + currentNumberOfCols;
    }

    public DtoSheetCell(SheetCell sheetCellImp, Map<String,DtoCell> cellIdToDtoCell) {
        for (Map.Entry<CellLocation, Cell> entry : sheetCellImp.getSheetCell().entrySet()) {
            EffectiveValue effectiveValue;
            effectiveValue= entry.getValue().getEffectiveValue().evaluate(sheetCellImp);

            cellLocationToEffectiveValue.put(entry.getKey(), effectiveValue);
        }
        //versionToCellsChanges = sheetCellImp.getVersions();
        copyBasicTypes(sheetCellImp);
        Set<Range> systemRanges = sheetCellImp.getSystemRanges();
        systemRanges.forEach(range -> {
            ranges.put(range.getRangeName(),range.getCellLocations());
        });

        visualSheetSize = currentNumberOfRows + "x" + currentNumberOfCols;
        this.cellIdToDtoCell = cellIdToDtoCell;
    }

    public DtoSheetCell(Map<CellLocation,EffectiveValue> cellLocationToEffectiveValue, Map<String, List<CellLocation>> ranges,
                        String name, int versionNumber, int currentNumberOfRows,
                        int currentNumberOfCols, int currentCellLength,
                        int currentCellWidth, Map<String,DtoCell> cellIdToDtoCell) {

        this.cellLocationToEffectiveValue = cellLocationToEffectiveValue;
        this.ranges = ranges;
        this.name = name;
        this.versionNumber = versionNumber;
        this.currentNumberOfRows = currentNumberOfRows;
        this.currentNumberOfCols = currentNumberOfCols;
        this.currentCellLength = currentCellLength;
        this.currentCellWidth = currentCellWidth;
        this.cellIdToDtoCell = cellIdToDtoCell;
        visualSheetSize = currentNumberOfRows + "x" + currentNumberOfCols;

    }

    public DtoSheetCell(SheetCell cellLocationToEffectiveValue, int requestedVersion) {

        Set<CellLocation> markedLocations = new HashSet<>();
        copyBasicTypes(cellLocationToEffectiveValue);
        Map<CellLocation, EffectiveValue> sheetCellChanges = cellLocationToEffectiveValue.getVersions().get(requestedVersion);

        while (requestedVersion > 0) {
            for (Map.Entry<CellLocation, EffectiveValue> entry : sheetCellChanges.entrySet()) {
                CellLocation location = entry.getKey();
                if(markedLocations.contains(location)) {
                    continue;
                }
                this.cellLocationToEffectiveValue.put(location, entry.getValue());
                markedLocations.add(location);
            }

            requestedVersion--;
            sheetCellChanges = cellLocationToEffectiveValue.getVersions().get(requestedVersion);
        }
    }

    public Map<CellLocation, EffectiveValue> getViewSheetCell() {
        return cellLocationToEffectiveValue;
    }

    public Map<String,List<CellLocation>> getRanges() {
        return ranges;
    }

    public void copyBasicTypes(SheetCell sheetCell) {
        this.name = sheetCell.getSheetName();
        this.versionNumber = sheetCell.getLatestVersion();
        this.currentNumberOfRows = sheetCell.getNumberOfRows();
        this.currentNumberOfCols = sheetCell.getNumberOfColumns();
        this.currentCellLength = sheetCell.getCellLength();
        this.currentCellWidth = sheetCell.getCellWidth();
    }

    public int getCellWidth() {
        return currentCellWidth;
    }

    public int getNumberOfRows() {
        return currentNumberOfRows;
    }

    public int getNumberOfColumns() {
        return currentNumberOfCols;
    }

    public String getSheetName() {
        return name;
    }

    public int getLatestVersion() {
        return versionNumber;
    }

    public int getCellLength() {
        return currentCellLength;
    }

    public Map<String, String> getPredictedValues() {
        return predictedValues;
    }

    public void setPredictedValues(Map<String, String> predictedValues) {
        this.predictedValues = predictedValues;
    }

    public boolean isPredictedValuesWorked() {
        return isPredictedValuesWorked;
    }

    public void setPredictedValuesWorked(boolean predictedValuesWorked) {
        isPredictedValuesWorked = predictedValuesWorked;
    }

    public EffectiveValue getEffectiveValue(CellLocation cellLocation) {
        return cellLocationToEffectiveValue.get(cellLocation);
    }

    public DtoContainerData filterSheetCell(String range, Map<Character, Set<String>> filter) {
        DtoSheetCell dtoSheetCellCopy = createCopy();
        return EngineUtilities.filterSheetCell(range, filter, dtoSheetCellCopy);
    }

    public DtoContainerData sortSheetCell(String range, String args) {
        DtoSheetCell dtoSheetCellCopy = createCopy();
        return EngineUtilities.sortSheetCell(range, args, dtoSheetCellCopy);
    }

    public Map<Character, Set<String>> getUniqueStringsInColumn(String filterColumn, String range) {

        Map<Character, Set<String>> columnToUniqueStrings = new HashMap<>();
        List<Character> columns = CellUtils.processCharString(filterColumn);

        // Split the range and parse the starting and ending row numbers
        String[] rangeParts = range.split("\\.\\.");
        String startCell = rangeParts[0];
        String endCell = rangeParts[1];

        // Extract the starting and ending row numbers
        int startingRowInRange = Integer.parseInt(startCell.substring(1));
        int endingRowInRange = Integer.parseInt(endCell.substring(1));

        for (Character col : columns) {
            // Convert column to uppercase for consistent matching
            char upperCol = Character.toUpperCase(col);

            // Retrieve values from the view sheet and collect unique values
            Set<String> uniqueStrings = new HashSet<>();
            getViewSheetCell().forEach((location, effectiveValue) -> {
                if (location.getVisualColumn() == upperCol &&
                        location.getRealRow() + 1 >= startingRowInRange &&
                        location.getRealRow() + 1 <= endingRowInRange) {

                    if (effectiveValue != null) {
                        String value = effectiveValue.getValue().toString();

                        // Try to parse as a double and format if applicable
                        try {
                            double doubleValue = Double.parseDouble(value);
                            value = CellUtils.formatNumber(doubleValue);
                        } catch (NumberFormatException e) {
                            // Ignore and keep the value as a string
                        }
                        uniqueStrings.add(value);
                    }
                }
            });

            columnToUniqueStrings.put(upperCol, Set.copyOf(uniqueStrings));
        }

        return columnToUniqueStrings;
    }

//    public Map<Character, Set<String>> getUniqueStringsInColumn(List<Character> columns, boolean isChartGraph) {
//
//        // in this method i am depending on the fact that the first column is the x-axis and the second is the y-axis
//
//        Map<Character, Set<String>> columnToUniqueStrings = new HashMap<>();
//
//        boolean isXAxis = true;
//
//        for (Character col : columns) {
//
//            // Convert column to uppercase for consistent matching
//            char upperCol = Character.toUpperCase(col);
//
//            // Retrieve values from the view sheet and collect unique values
//            Set<String> uniqueStrings = new HashSet<>();
//            boolean finalIsXAxis = isXAxis;
//            getViewSheetCell().forEach((location, effectiveValue) -> {
//                if (location.getVisualColumn() == upperCol) {
//                    if (effectiveValue != null) {
//                        String value = effectiveValue.getValue().toString();
//
//                        if (isChartGraph || !finalIsXAxis) {
//                            try {
//                                double doubleValue = Double.parseDouble(value);
//                                value = CellUtils.formatNumber(doubleValue);
//                            } catch (NumberFormatException e) {
//                                // Ignore and keep the value as a string
//                            }
//                            uniqueStrings.add(value);
//                        } else {
//                            try {
//                                double doubleValue = Double.parseDouble(value);
//                                value = CellUtils.formatNumber(doubleValue);
//                                uniqueStrings.add(value);
//                            } catch (NumberFormatException e) {
//                                // Ignore and keep the value as a string
//                            }
//                        }
//                    }
//                }
//            });
//
//            Set<String> copy = Set.copyOf(uniqueStrings);
//            columnToUniqueStrings.put(upperCol, copy);
//            uniqueStrings.clear();
//            isXAxis = false;
//        }
//
//        return columnToUniqueStrings;
//    }

    public Map<Character, Set<String>> getUniqueStringsInColumn(List<Character> columns, boolean isChartGraph) {

        // Assume the first column is the x-axis and the second is the y-axis
        Map<Character, Set<String>> columnToUniqueStrings = new HashMap<>();
        boolean isXAxis = true;

        for (Character col : columns) {

            // Convert column to uppercase for consistent matching
            char upperCol = Character.toUpperCase(col);

            // Retrieve values from the view sheet and collect unique values
            Set<String> uniqueStrings = new HashSet<>();
            boolean finalIsXAxis = isXAxis;

            getViewSheetCell().forEach((location, effectiveValue) -> {
                if (location.getVisualColumn() == upperCol && effectiveValue != null) {
                    String value = effectiveValue.getValue().toString();

                    try {
                        // Parse value to double for numeric filtering based on isChartGraph and axis type
                        double doubleValue = Double.parseDouble(value);
                        value = CellUtils.formatNumber(doubleValue);

                        // Add value only if it's a chart graph or y-axis for non-chart graphs
                        if (!isChartGraph || !finalIsXAxis) {
                            uniqueStrings.add(value);
                        } else if (finalIsXAxis) {
                            uniqueStrings.add(value);  // Add non-numeric values to the x-axis only if not a chart graph
                        }
                    } catch (NumberFormatException e) {
                        if (isChartGraph && finalIsXAxis) {
                            // Add non-numeric values for x-axis if not a chart graph
                            uniqueStrings.add(value);
                        }
                    }
                }
            });

            columnToUniqueStrings.put(upperCol, Set.copyOf(uniqueStrings));
            uniqueStrings.clear();
            isXAxis = false;  // Mark subsequent columns as y-axis
        }

        return columnToUniqueStrings;
    }



    public DtoCell getRequestedCell(String cellId) {
        if(cellIdToDtoCell != null)
        {
            return cellIdToDtoCell.get(cellId);
        }
        return null;
    }

    public Map<String, DtoCell> getCellIdToDtoCell() {
        return cellIdToDtoCell;
    }

    public String getSheetSize() {
        return visualSheetSize;
    }


    public DtoSheetCell createCopy() {
        try {
            // Serialize the current object to a byte array
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(this);

            // Deserialize to create a deep copy
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            return (DtoSheetCell) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setViewSheetCell(Map<CellLocation, EffectiveValue> result) {
        cellLocationToEffectiveValue = result;
    }
}
