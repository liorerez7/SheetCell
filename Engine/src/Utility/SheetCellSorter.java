package Utility;

import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import expression.api.EffectiveValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetCellSorter {

    // Constants
    private static final int NO_SWAP = -1;
    private static final int EQUAL_VALUES = 0;


    public static DtoSheetCell sortSheetCell(String range, String args, DtoSheetCell dtoSheetCell) {

        List<CellLocation> cellLocations = EngineUtilities.parseRange(range);
        List<List<EffectiveValue>> cols = EngineUtilities.getRowsFromRange(cellLocations, dtoSheetCell);

        char leftColumn = cellLocations.get(0).getVisualColumn();
        int upperRow = cellLocations.get(0).getRealRow();

        sortRowsByColumns(cols, args, leftColumn, upperRow);

        EngineUtilities.updateDtoSheetCell(dtoSheetCell, cellLocations, cols, leftColumn);

        return dtoSheetCell;
    }

    private static void sortRowsByColumns(List<List<EffectiveValue>> rows, String arguments, char leftColumn, int upperRow) {

        List<String> args = EngineUtilities.extractLetters(arguments); // by which columns to sort
        int iteratorOfArgs = 0;

        // index of first column to sort relative to left column
        final int colIndex = getColumnIndex(args.get(iteratorOfArgs), leftColumn);

        List<EffectiveValue> col = rows.get(colIndex); // the first column to sort

        for (int i = 0; i < col.size() - 1; i++) {
            for (int j = 0; j < col.size() - i - 1; j++) {
                int result = shouldSwap(col.get(j), col.get(j + 1));

                if (result >= EQUAL_VALUES) {
                    if (result == EQUAL_VALUES) {
                        int innerResult = checkOnNextColumn(rows, args, ++iteratorOfArgs, j, j + 1, leftColumn);
                        iteratorOfArgs = 0;

                        if (innerResult > EQUAL_VALUES) {
                            swapRows(rows, j, j + 1);
                        }
                    } else {
                        swapRows(rows, j, j + 1);
                    }
                }
            }
        }
        System.out.println("Sorted by: " + arguments);
    }

    private static int checkOnNextColumn(List<List<EffectiveValue>> rows, List<String> args, int iteratorOfArgs, int firstValueRow, int secondValueRow, char leftColumn) {
        if (args.size() <= iteratorOfArgs) {
            return NO_SWAP;
        }

        final int colIndex = getColumnIndex(args.get(iteratorOfArgs), leftColumn);
        int result = shouldSwap(rows.get(colIndex).get(firstValueRow), rows.get(colIndex).get(secondValueRow));

        return (result == EQUAL_VALUES) ? checkOnNextColumn(rows, args, ++iteratorOfArgs, firstValueRow, secondValueRow, leftColumn) : result;
    }

    private static int getColumnIndex(String arg, char leftColumn) {
        return Character.toUpperCase(arg.charAt(0)) - Character.toUpperCase(leftColumn);
    }

    private static int shouldSwap(EffectiveValue value1, EffectiveValue value2) {

        if (value1 == null) return NO_SWAP;
        if (value2 == null) return NO_SWAP;

        Object val1 = value1.getValue();
        Object val2 = value2.getValue();

        if (val1 instanceof Double && val2 instanceof Double) {
            return Double.compare((Double) val1, (Double) val2);
        }

        return EQUAL_VALUES; //meaning they are not comparable
    }

    private static void swapRows(List<List<EffectiveValue>> rows, int indexOfRow1, int indexOfRow2) {
        for (List<EffectiveValue> column : rows) {
            EffectiveValue temp = column.get(indexOfRow1);
            column.set(indexOfRow1, column.get(indexOfRow2));
            column.set(indexOfRow2, temp);
        }
    }


    public static SortContainerData sortSheetCelll(String range, String args, DtoSheetCell dtoSheetCell) {

        List<CellLocation> cellLocations = EngineUtilities.parseRange(range);
        List<List<EffectiveValue>> cols = EngineUtilities.getRowsFromRange(cellLocations, dtoSheetCell);
        List<List<EffectiveValueContainer>> newCols = EngineUtilities.getRowsFromRangee(cellLocations, dtoSheetCell);

        char leftColumn = cellLocations.get(0).getVisualColumn();
        int upperRow = cellLocations.get(0).getRealRow();

        sortRowsByColumns(cols, args, leftColumn, upperRow);
        sortRowsByColumnss(newCols, args, leftColumn, upperRow);

        Map<CellLocation,CellLocation> oldCellLocationToAfterSortCellLocation = EngineUtilities.getOldCellLocationToAfterSortCellLocationn(newCols, leftColumn, upperRow);


        EngineUtilities.updateDtoSheetCell(dtoSheetCell, cellLocations, cols, leftColumn);

        EngineUtilities.updateDtoSheetCelll(dtoSheetCell, cellLocations, newCols, leftColumn);

        return new SortContainerData(dtoSheetCell, oldCellLocationToAfterSortCellLocation);
    }

    private static void sortRowsByColumnss(List<List<EffectiveValueContainer>> rows, String arguments, char leftColumn, int upperRow) {

        List<String> args = EngineUtilities.extractLetters(arguments); // by which columns to sort
        int iteratorOfArgs = 0;

        // index of first column to sort relative to left column
        final int colIndex = getColumnIndex(args.get(iteratorOfArgs), leftColumn);

        List<EffectiveValueContainer> col = rows.get(colIndex); // the first column to sort

        for (int i = 0; i < col.size() - 1; i++) {
            for (int j = 0; j < col.size() - i - 1; j++) {
                // Compare EffectiveValues from the EffectiveValueContainer
                int result = shouldSwap(col.get(j).getEffectiveValue(), col.get(j + 1).getEffectiveValue());

                if (result >= EQUAL_VALUES) {
                    if (result == EQUAL_VALUES) {
                        int innerResult = checkOnNextColumnn(rows, args, ++iteratorOfArgs, j, j + 1, leftColumn);
                        iteratorOfArgs = 0;

                        if (innerResult > EQUAL_VALUES) {
                            swapRowss(rows, j, j + 1);
                        }
                    } else {
                        swapRowss(rows, j, j + 1);
                    }
                }
            }
        }
        System.out.println("Sorted by: " + arguments);
    }

    private static void swapRowss(List<List<EffectiveValueContainer>> rows, int indexOfRow1, int indexOfRow2) {
        for (List<EffectiveValueContainer> column : rows) {
            EffectiveValueContainer temp = column.get(indexOfRow1);
            column.set(indexOfRow1, column.get(indexOfRow2));
            column.set(indexOfRow2, temp);
        }
    }

    private static int checkOnNextColumnn(List<List<EffectiveValueContainer>> rows, List<String> args, int iteratorOfArgs, int firstValueRow, int secondValueRow, char leftColumn) {
        if (args.size() <= iteratorOfArgs) {
            return NO_SWAP;
        }

        final int colIndex = getColumnIndex(args.get(iteratorOfArgs), leftColumn);

        EffectiveValue firstEffectiveValue = rows.get(colIndex).get(firstValueRow).getEffectiveValue();
        EffectiveValue secondEffectiveValue = rows.get(colIndex).get(secondValueRow).getEffectiveValue();

        int result = shouldSwap(firstEffectiveValue, secondEffectiveValue);

        return (result == EQUAL_VALUES) ? checkOnNextColumnn(rows, args, ++iteratorOfArgs, firstValueRow, secondValueRow, leftColumn) : result;
    }

}
