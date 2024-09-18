package Utility;

import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import expression.ReturnedValueType;
import expression.impl.variantImpl.EffectiveValueImpl;

import java.util.*;

public class SheetCellFilter {

    private static void moveNonEmptyValuesToFront(List<List<EffectiveValueContainer>> myGrid) {
        // Iterate over each column in the grid
        for (List<EffectiveValueContainer> column : myGrid) {
            // Create a new list to store the non-empty values
            List<EffectiveValueContainer> nonEmptyValues = new ArrayList<>();

            // Collect all non-empty values in the current column
            for (EffectiveValueContainer value : column) {
                if (value != null && value.getEffectiveValue() != null) {
                    if (!(value.getEffectiveValue().getValue() == "" || value.getEffectiveValue().getCellType() == ReturnedValueType.EMPTY)) {
                        nonEmptyValues.add(value);
                    }
                }
            }

            // Fill the column starting from the top with non-empty values
            int row = 0;
            for (EffectiveValueContainer nonEmptyValue : nonEmptyValues) {
                column.set(row, nonEmptyValue);
                row++;
            }

            // Fill the remaining rows with empty values
            while (row < column.size()) {
                column.set(row, new EffectiveValueContainer(new EffectiveValueImpl(ReturnedValueType.EMPTY, ""), CellLocationFactory.fromCellId('Z', "10")));
                row++;
            }
        }
    }

    private static boolean shouldKeepCellValue(Object value, List<String> filterByStrings) {
        if (value instanceof String) {
            return filterString((String) value, filterByStrings);
        } else if (value instanceof Double) {
            return filterDouble((Double) value, filterByStrings);
        } else if (value instanceof Boolean) {
            return filterBoolean((Boolean) value, filterByStrings);
        }
        return false;
    }

    private static boolean filterString(String actualValue, List<String> filterByStrings) {
        return filterByStrings.stream().anyMatch(actualValue::contains);
    }

    private static boolean filterDouble(Double actualValue, List<String> filterByStrings) {
        return filterByStrings.stream().anyMatch(filter -> {
            try {
                return Double.parseDouble(filter) == actualValue;
            } catch (NumberFormatException e) {
                return false; // Ignore non-numeric filters
            }
        });
    }

    private static boolean filterBoolean(Boolean actualValue, List<String> filterByStrings) {
        return filterByStrings.stream().anyMatch(filter -> {
            if (filter.equalsIgnoreCase("true") || filter.equalsIgnoreCase("false")) {
                return Boolean.parseBoolean(filter) == actualValue;
            }
            return false;
        });
    }

    public static DtoContainerData filterSheetCell(String range, Map<Character, Set<String>> filter, DtoSheetCell dtoSheetCell, String filterColumn) {


       // List<String> filterByStrings = EngineUtilities.extractLetters(filter);
        List<CellLocation> parsedRange = EngineUtilities.parseRange(range);
       // List<List<EffectiveValue>> myGrid = EngineUtilities.getRowsFromRange(parsedRange, dtoSheetCell);
        //List<List<EffectiveValue>> copyGrid = EngineUtilities.getRowsFromRange(parsedRange, dtoSheetCell);
        List<List<EffectiveValueContainer>> gridOfEffectiveValueContainerCopy = EngineUtilities.getRowsFromRange(parsedRange, dtoSheetCell);
        List<List<EffectiveValueContainer>> gridOfEffectiveValueContainer = EngineUtilities.getRowsFromRange(parsedRange, dtoSheetCell);

        List<List<EffectiveValueContainer>> gridOfRowsEffectiveValueContainer = EngineUtilities.getRealRowsFromRange(parsedRange, dtoSheetCell);

        List<List<EffectiveValueContainer>> resOfEffectiveValueContainer = filterGrid(gridOfEffectiveValueContainerCopy, filter);

        char leftColumn = parsedRange.get(0).getVisualColumn();
        int upperRow = parsedRange.get(0).getRealRow();


//        Set<Integer> numberOfRowsWithFilteredValues = new HashSet<>();
//        Map<Integer, String> rowToFilteredValue = new HashMap<>();
//
//        // Iterate over the grid and filter the values
//        int colsIterator = 0;
//        for (List<EffectiveValueContainer> gridCol : gridOfEffectiveValueContainer) {
//
//            for (int row = 0; row < gridCol.size(); row++) {
//                EffectiveValueContainer cellValue = gridCol.get(row);
//
//                if (cellValue != null && cellValue.getEffectiveValue() != null) {
//                    Object actualValue = cellValue.getEffectiveValue().getValue();
//                    boolean shouldKeepValue = shouldKeepCellValue(actualValue, filterByStrings);
//
//                    // If the value should not be kept, replace with an empty string
//                    if (!shouldKeepValue) {
//                        gridCol.set(
//                                row, new EffectiveValueContainer(
//                                        new EffectiveValueImpl(ReturnedValueType.EMPTY, ""),
//                                        CellLocationFactory.fromCellId('Z', "10"))); // Dummy cell location
//                    }
//                    else{
//                        numberOfRowsWithFilteredValues.add(row);
//                        rowToFilteredValue.put(row, cellValue.getEffectiveValue().getValue().toString());
//                    }
//                }
//            }
//            colsIterator++;
//        }
//
//        int numberOfCol = 0;
//        for (List<EffectiveValueContainer> gridColCopy : gridOfEffectiveValueContainerCopy) {
//            for (int row = 0; row < gridColCopy.size(); row++) {
//                EffectiveValueContainer cellValueCopy = gridColCopy.get(row);
//                if(numberOfRowsWithFilteredValues.contains(row)){
//                    gridOfEffectiveValueContainer.get(numberOfCol).set(row, cellValueCopy);
//                }
//            }
//            numberOfCol++;
//        }

        moveNonEmptyValuesToFront(gridOfEffectiveValueContainer);
        removeEmptyRowsFromBottom(gridOfEffectiveValueContainer);
        Map<CellLocation,CellLocation> oldCellLocationToAfterSortCellLocation = EngineUtilities.getOldCellLocationToAfterSortCellLocation(gridOfEffectiveValueContainer, leftColumn, upperRow, false);
        Map<CellLocation, CellLocation> newCellLocationToAfterSortCellLocation = EngineUtilities.getOldCellLocationToAfterSortCellLocation(gridOfEffectiveValueContainer, leftColumn, upperRow, true);
        EngineUtilities.updateDtoSheetCellFilter(dtoSheetCell, parsedRange, gridOfEffectiveValueContainer, parsedRange.getFirst().getVisualColumn(), oldCellLocationToAfterSortCellLocation, newCellLocationToAfterSortCellLocation);

        return new DtoContainerData(dtoSheetCell, oldCellLocationToAfterSortCellLocation);
    }

    public static List<List<EffectiveValueContainer>> filterGrid(
            List<List<EffectiveValueContainer>> gridOfEffectiveValueContainerCopy,
            Map<Character, Set<String>> filter) {

        // Iterate over the rows of the grid
        for (int row = 0; row < gridOfEffectiveValueContainerCopy.size(); row++) {
            List<EffectiveValueContainer> currentRow = gridOfEffectiveValueContainerCopy.get(row);

            // Flag to track if the row has any matching values
            boolean shouldKeepRow = false;

            // Check each column in the row against the filter
            for (Map.Entry<Character, Set<String>> filterEntry : filter.entrySet()) {
                char colChar = filterEntry.getKey();
                Set<String> allowedValues = filterEntry.getValue();

                int colIndex = colChar - 'A'; // Assuming 'A' is column 0, 'B' is column 1, etc.

                // Ensure column index is within bounds of the current row
                if (colIndex < currentRow.size()) {
                    EffectiveValueContainer cellContainer = currentRow.get(colIndex);
                    if(cellContainer == null || cellContainer.getEffectiveValue() == null){
                        continue;
                    }

                    String cellValue = cellContainer.getEffectiveValue().getValue().toString();

                    // If the cell value is in the set of allowed values, keep the row
                    if(allowedValues != null && allowedValues.size() > 0)
                    {
                        if (allowedValues.contains(cellValue)) {
                            shouldKeepRow = true;
                            break; // No need to check further, we keep this row
                        }
                    }
                }
            }

            // If the row does not meet the filter criteria, clear the row by setting dummy values
            if (!shouldKeepRow) {
                for (int col = 0; col < currentRow.size(); col++) {
                    currentRow.set(col, new EffectiveValueContainer(
                            new EffectiveValueImpl(ReturnedValueType.EMPTY, ""),
                            CellLocationFactory.fromCellId('Z', "10"))); // Dummy cell location
                }
            }
        }

        // Return the updated grid with empty rows as per the filter
        return gridOfEffectiveValueContainerCopy;
    }



    private static void removeEmptyRowsFromBottom(List<List<EffectiveValueContainer>> myGrid) {
        // Start from the bottom row and move upwards
        int totalRows = myGrid.get(0).size(); // Assuming all columns have the same number of rows
        boolean isRowEmpty;

        for (int rowIndex = totalRows - 1; rowIndex >= 0; rowIndex--) {
            isRowEmpty = true; // Assume the row is empty until proven otherwise

            // Check every column for the current row
            for (List<EffectiveValueContainer> column : myGrid) {
                EffectiveValueContainer value = column.get(rowIndex);

                // If any cell in the current row is not empty, mark the row as non-empty
                if (value != null && value.getEffectiveValue() != null) {
                    if (!(value.getEffectiveValue().getValue().equals("") ||
                            value.getEffectiveValue().getCellType() == ReturnedValueType.EMPTY)) {
                        isRowEmpty = false;
                        break;
                    }
                }
            }

            // If the row is empty, remove it from every column
            if (isRowEmpty) {
                for (List<EffectiveValueContainer> column : myGrid) {
                    column.remove(rowIndex);
                }
            } else {
                // If a non-empty row is found, stop further removals
                break;
            }
        }
    }

}
