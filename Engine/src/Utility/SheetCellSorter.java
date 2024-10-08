package Utility;



import DtoComponents.DtoContainerData;
import DtoComponents.DtoSheetCell;
import smallParts.CellLocation;
import smallParts.EffectiveValue;

import java.util.List;
import java.util.Map;

public class SheetCellSorter {

    private static final int NO_SWAP = -1;
    private static final int EQUAL_VALUES = 0;


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

    public static DtoContainerData sortSheetCell(String range, String args, DtoSheetCell dtoSheetCell) {

        List<CellLocation> cellLocations = EngineUtilities.parseRange(range);
        List<List<EffectiveValueContainer>> newCols = EngineUtilities.getRangeGridAsColsFromRange(cellLocations, dtoSheetCell);

        char leftColumn = cellLocations.get(0).getVisualColumn();
        int upperRow = cellLocations.get(0).getRealRow();


        sortRowsByColumns(newCols, args, leftColumn, upperRow);
        Map<CellLocation,CellLocation> oldCellLocationToAfterSortCellLocation = EngineUtilities.getOldCellLocationToAfterSortCellLocation(newCols, leftColumn, upperRow, false);
        EngineUtilities.updateDtoSheetCell(dtoSheetCell, cellLocations, newCols, leftColumn);

        return new DtoContainerData(dtoSheetCell, oldCellLocationToAfterSortCellLocation);
    }

    private static void sortRowsByColumns(List<List<EffectiveValueContainer>> rows, String arguments, char leftColumn, int upperRow) {

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
    }

    private static void swapRows(List<List<EffectiveValueContainer>> rows, int indexOfRow1, int indexOfRow2) {
        for (List<EffectiveValueContainer> column : rows) {
            EffectiveValueContainer temp = column.get(indexOfRow1);
            column.set(indexOfRow1, column.get(indexOfRow2));
            column.set(indexOfRow2, temp);
        }
    }

    private static int checkOnNextColumn(List<List<EffectiveValueContainer>> rows, List<String> args, int iteratorOfArgs, int firstValueRow, int secondValueRow, char leftColumn) {
        if (args.size() <= iteratorOfArgs) {
            return NO_SWAP;
        }

        final int colIndex = getColumnIndex(args.get(iteratorOfArgs), leftColumn);

        EffectiveValue firstEffectiveValue = rows.get(colIndex).get(firstValueRow).getEffectiveValue();
        EffectiveValue secondEffectiveValue = rows.get(colIndex).get(secondValueRow).getEffectiveValue();

        int result = shouldSwap(firstEffectiveValue, secondEffectiveValue);

        return (result == EQUAL_VALUES) ? checkOnNextColumn(rows, args, ++iteratorOfArgs, firstValueRow, secondValueRow, leftColumn) : result;
    }

}
