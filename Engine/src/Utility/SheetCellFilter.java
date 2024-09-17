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

        // Update the dtoSheetCell with the filtered grid
        EngineUtilities.updateDtoSheetCell(dtoSheetCell, parsedRange, myGrid, parsedRange.getFirst().getVisualColumn());

        return dtoSheetCell;
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
