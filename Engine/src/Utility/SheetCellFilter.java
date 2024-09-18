package Utility;

import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.impl.variantImpl.EffectiveValueImpl;

import java.util.*;

public class SheetCellFilter {

    public static DtoSheetCell filterSheetCell(String range, String filter, DtoSheetCell dtoSheetCell, String filterColumn) {

        List<String> filterByStrings = EngineUtilities.extractLetters(filter);
        List<CellLocation> parsedRange = EngineUtilities.parseRange(range);
        List<List<EffectiveValue>> myGrid = EngineUtilities.getRowsFromRange(parsedRange, dtoSheetCell);
        List<List<EffectiveValue>> copyGrid = EngineUtilities.getRowsFromRange(parsedRange, dtoSheetCell);
        List<List<EffectiveValueContainer>> copyGridd = EngineUtilities.getRowsFromRangee(parsedRange, dtoSheetCell);


        char leftColumn = parsedRange.get(0).getVisualColumn();
        char colToFilterBy = filterColumn.charAt(0);

        Set<Integer> numberOfRowsWithFilteredValues = new HashSet<>();
        Map<Integer, String> rowToFilteredValue = new HashMap<>();

        // Iterate over the grid and filter the values
        for (List<EffectiveValue> gridCol : myGrid) {
            for (int row = 0; row < gridCol.size(); row++) {
                EffectiveValue cellValue = gridCol.get(row);

                if (cellValue != null) {
                    Object actualValue = cellValue.getValue();
                    boolean shouldKeepValue = shouldKeepCellValue(actualValue, filterByStrings);

                    // If the value should not be kept, replace with an empty string
                    if (!shouldKeepValue) {
                        gridCol.set(row, new EffectiveValueImpl(ReturnedValueType.EMPTY, ""));
                    }
                    else{
                        numberOfRowsWithFilteredValues.add(row);
                        rowToFilteredValue.put(row, cellValue.getValue().toString());
                    }
                }
            }
        }

        int numberOfCol = 0;
        for (List<EffectiveValue> gridColCopy : copyGrid) {
            for (int row = 0; row < copyGrid.size(); row++) {
                EffectiveValue cellValueCopy = gridColCopy.get(row);
                if(numberOfRowsWithFilteredValues.contains(row)){
                   myGrid.get(numberOfCol).set(row, cellValueCopy);
                }
            }
            numberOfCol++;
        }


        moveNonEmptyValuesToFront(myGrid);



        // Update the dtoSheetCell with the filtered grid
        EngineUtilities.updateDtoSheetCell(dtoSheetCell, parsedRange, myGrid, parsedRange.getFirst().getVisualColumn());

        return dtoSheetCell;
    }

    private static void moveNonEmptyValuesToFront(List<List<EffectiveValue>> myGrid) {
        // Iterate over each column in the grid
        for (List<EffectiveValue> column : myGrid) {
            // Create a new list to store the non-empty values
            List<EffectiveValue> nonEmptyValues = new ArrayList<>();

            // Collect all non-empty values in the current column
            for (EffectiveValue value : column) {
                if (value != null && !(value.getValue() == "" || value.getCellType() == ReturnedValueType.EMPTY)) {
                    nonEmptyValues.add(value);
                }
            }

            // Fill the column starting from the top with non-empty values
            int row = 0;
            for (EffectiveValue nonEmptyValue : nonEmptyValues) {
                column.set(row, nonEmptyValue);
                row++;
            }

            // Fill the remaining rows with empty values
            while (row < column.size()) {
                column.set(row, new EffectiveValueImpl(ReturnedValueType.EMPTY, ""));
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
}
