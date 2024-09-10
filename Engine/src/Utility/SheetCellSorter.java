package Utility;

import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import expression.api.EffectiveValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SheetCellSorter {

    public DtoSheetCell sortSheetCell(String range, String args, DtoSheetCell dtoSheetCell) {
        // 1. Parse the range, e.g., "A1..C3"
        List<CellLocation> cellLocations = parseRange(range, dtoSheetCell);

        // 2. Prepare a list of rows corresponding to the range, where each row is a list of cell values
        List<List<EffectiveValue>> cols = getRowsFromRange(cellLocations, dtoSheetCell);

        char leftColumn = cellLocations.get(0).getVisualColumn(); // in our case will be: 'C'

        // 3. Sort the rows based on the columns provided in args
        sortRowsByColumns(cols, args, leftColumn);

        // 4. Update dtoSheetCell based on sorted rows
        updateDtoSheetCell(dtoSheetCell, cellLocations, cols, leftColumn);

        return dtoSheetCell;
    }

    private List<CellLocation> parseRange(String range, DtoSheetCell dtoSheetCell) {
        String[] parts = range.split("\\.\\.");
        CellLocation start = CellLocationFactory.fromCellId(parts[0]);
        CellLocation end = CellLocationFactory.fromCellId(parts[1]);

        List<CellLocation> locations = new ArrayList<>();

        for (char col = start.getVisualColumn(); col <= end.getVisualColumn(); col++) {
            for (int row = Integer.parseInt(start.getVisualRow()); row <= Integer.parseInt(end.getVisualRow()); row++) {
                locations.add(CellLocationFactory.fromCellId(col, String.valueOf(row)));
            }
        }
        return locations;
    }

    private List<List<EffectiveValue>> getRowsFromRange(List<CellLocation> cellLocations, DtoSheetCell dtoSheetCell) {
        Map<CellLocation, EffectiveValue> sheetData = dtoSheetCell.getViewSheetCell();
        List<List<EffectiveValue>> rows = new ArrayList<>();

        int width = (int) cellLocations.stream().map(CellLocation::getRealColumn).distinct().count();
        List<EffectiveValue> currentRow = new ArrayList<>();

        for (int i = 0; i < cellLocations.size(); i++) {
            CellLocation cellLocation = cellLocations.get(i);
            EffectiveValue effectiveValue = sheetData.get(cellLocation);
            currentRow.add(effectiveValue);

            if (currentRow.size() == width) {
                rows.add(new ArrayList<>(currentRow));
                currentRow.clear();
            }
        }

        return rows;
    }

    private void sortRowsByColumns(List<List<EffectiveValue>> rows, String arguments, char leftColumn) {
        List<String> args = extractLetters(arguments);
        int iteratorOfArgs = 0;
        final int colIndex = Character.toUpperCase(args.get(iteratorOfArgs).charAt(0)) - Character.toUpperCase(leftColumn);

        List<EffectiveValue> col = rows.get(colIndex);

        for (int i = 0; i < col.size() - 1; i++) {
            for (int j = 0; j < col.size() - i - 1; j++) {
                EffectiveValue value1 = col.get(j);
                EffectiveValue value2 = col.get(j + 1);

                int result = shouldSwap(value1, value2);

                if (result >= 0) {
                    if (result == 0) {
                        int innerResult = checkOnNextCollumn(rows, args, ++iteratorOfArgs, j, j + 1, leftColumn);
                        iteratorOfArgs = 0;

                        if (innerResult > 0) {
                            swapRows(rows, j, j + 1);
                        }
                    } else {
                        swapRows(rows, j, j + 1);
                    }
                }
            }
        }
    }


    private int checkOnNextCollumn(List<List<EffectiveValue>> rows, List<String> args, int iteratorOfArgs, int firstValueRow, int secondValueRow, char leftColumn) {
        if (args.size() <= iteratorOfArgs) {
            return 0;
        }

        final int colIndex = Character.toUpperCase(args.get(iteratorOfArgs).charAt(0)) - Character.toUpperCase(leftColumn);
        List<EffectiveValue> col = rows.get(colIndex);

        EffectiveValue value1 = col.get(firstValueRow);
        EffectiveValue value2 = col.get(secondValueRow);

        int result = shouldSwap(value1, value2);

        if (result >= 0) {
            if (result == 0) {
                return checkOnNextCollumn(rows, args, ++iteratorOfArgs, firstValueRow, secondValueRow, leftColumn);
            } else {
                return 1;
            }
        } else {
            return -1;
        }
    }


    private int shouldSwap(EffectiveValue value1, EffectiveValue value2) {
        if (value1 == null && value2 == null) return -1;
        if (value1 == null) return 1;
        if (value2 == null) return 0;

        Object val1 = getNumericValue(value1);
        Object val2 = getNumericValue(value2);

        if (val1 instanceof Double && val2 instanceof Double) {
            if (((Double) val1).equals((Double) val2)) {
                return 0;
            } else if ((Double) val1 < (Double) val2) {
                return -1;
            }
            return 1;
        }

        return -1;
    }

    private void swapRows(List<List<EffectiveValue>> rows, int indexOfRow1, int indexOfRow2) {
        for (List<EffectiveValue> column : rows) {
            EffectiveValue temp = column.get(indexOfRow1);
            column.set(indexOfRow1, column.get(indexOfRow2));
            column.set(indexOfRow2, temp);
        }
    }

    private Object getNumericValue(EffectiveValue effectiveValue) {
        if (effectiveValue == null) {
            return null;
        }

        try {
            Object value = effectiveValue.getValue();
            if (value instanceof Double) {
                return ((Double) value);
            }
        } catch (Exception e) {
            // Ignore non-numeric values
        }
        return null;
    }

    private void updateDtoSheetCell(DtoSheetCell dtoSheetCell, List<CellLocation> cellLocations, List<List<EffectiveValue>> cols, char leftColumn) {
        int indexOfPreferredColumn = 0;
        char firstPreferredColumn;

        int startRowOfSorting;
        String startRowOfSortingAsString;

        for (List<EffectiveValue> col : cols) {
            firstPreferredColumn = leftColumn;
            startRowOfSorting = cellLocations.get(0).getRealRow() + 1;
            startRowOfSortingAsString = Integer.toString(startRowOfSorting);

            for (EffectiveValue value : col) {
                dtoSheetCell.getViewSheetCell().put(CellLocationFactory.fromCellId(firstPreferredColumn, startRowOfSortingAsString), value);
                startRowOfSortingAsString = Integer.toString(++startRowOfSorting);
            }

            leftColumn++;
        }
    }

    private List<String> extractLetters(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty.");
        }

        String[] elements = input.split(",");
        List<String> letters = new ArrayList<>();

        for (String element : elements) {
            element = element.trim();

            if (element.length() == 1 && Character.isLetter(element.charAt(0))) {
                letters.add(element);
            } else if (!element.isEmpty()) {
                throw new IllegalArgumentException("Invalid element found: " + element);
            }
        }

        return letters;
    }
}

