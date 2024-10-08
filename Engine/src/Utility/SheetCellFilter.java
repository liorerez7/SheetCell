package Utility;


import DtoComponents.DtoContainerData;
import DtoComponents.DtoSheetCell;
import expression.ReturnedValueType;
import smallParts.CellLocation;
import smallParts.CellLocationFactory;
import smallParts.EffectiveValue;


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
                column.set(row, new EffectiveValueContainer(new EffectiveValue(ReturnedValueType.EMPTY, ""), CellLocationFactory.fromCellId('Z', "10")));
                row++;
            }
        }
    }

    public static DtoContainerData filterSheetCell(String range, Map<Character, Set<String>> filter, DtoSheetCell dtoSheetCell) {

        List<CellLocation> parsedRange = EngineUtilities.parseRange(range);
        List<List<EffectiveValueContainer>> gridOfRowsEffectiveValueContainer = EngineUtilities.getRangeGridAsRowsFromRange(parsedRange, dtoSheetCell);

        char leftColumn = parsedRange.get(0).getVisualColumn();
        int upperRow = parsedRange.get(0).getRealRow();

        List<List<EffectiveValueContainer>> resOfEffectiveValueContainer = filterGrid(gridOfRowsEffectiveValueContainer, filter, leftColumn);

        List<List<EffectiveValueContainer>> gridOfEffectiveValueContainer = transposeGrid(resOfEffectiveValueContainer);

        moveNonEmptyValuesToFront(gridOfEffectiveValueContainer);
        Map<CellLocation,CellLocation> oldCellLocationToAfterSortCellLocation = EngineUtilities.getOldCellLocationToAfterSortCellLocation(gridOfEffectiveValueContainer, leftColumn, upperRow, false);
        EngineUtilities.updateDtoSheetCell(dtoSheetCell, parsedRange, gridOfEffectiveValueContainer, leftColumn);

        return new DtoContainerData(dtoSheetCell, oldCellLocationToAfterSortCellLocation);
    }

    private static List<List<EffectiveValueContainer>> transposeGrid(
            List<List<EffectiveValueContainer>> resOfEffectiveValueContainer) {

        if (resOfEffectiveValueContainer == null || resOfEffectiveValueContainer.isEmpty()) {
            return new ArrayList<>();  // Return empty if input is null or empty
        }

        // Get number of rows and columns
        int originalRowCount = resOfEffectiveValueContainer.size();
        int originalColCount = resOfEffectiveValueContainer.get(0).size();

        // Create a new list with size based on the number of original columns
        List<List<EffectiveValueContainer>> transposed = new ArrayList<>();

        // Initialize the transposed structure
        for (int col = 0; col < originalColCount; col++) {
            transposed.add(new ArrayList<>());
        }

        // Fill the transposed grid
        for (int row = 0; row < originalRowCount; row++) {
            for (int col = 0; col < originalColCount; col++) {
                // Get the EffectiveValueContainer from the current row and column
                EffectiveValueContainer container = resOfEffectiveValueContainer.get(row).get(col);

                // Add the container to the transposed grid at the corresponding position
                transposed.get(col).add(container);
            }
        }

        return transposed;
    }

    private static List<List<EffectiveValueContainer>> filterGrid(
            List<List<EffectiveValueContainer>> gridOfEffectiveValueContainerCopy,
            Map<Character, Set<String>> filter, char leftColumn) {

        // Iterate over the rows of the grid
        for (int row = 0; row < gridOfEffectiveValueContainerCopy.size(); row++) {
            List<EffectiveValueContainer> currentRow = gridOfEffectiveValueContainerCopy.get(row);

            // Flag to track if the row has any matching values
            boolean shouldKeepRow = true;

            // Check each column in the row against the filter
            for (Map.Entry<Character, Set<String>> filterEntry : filter.entrySet()) {
                char colChar = filterEntry.getKey();
                Set<String> allowedValues = filterEntry.getValue();

                int colIndex = colChar - leftColumn; // Assuming 'A' is column 0, 'B' is column 1, etc.

                // Ensure column index is within bounds of the current row
                if (colIndex < currentRow.size()) {
                    EffectiveValueContainer cellContainer = currentRow.get(colIndex);

                    String cellValue = "";

                    if(!(cellContainer == null || cellContainer.getEffectiveValue() == null)){
                        //continue;
                        cellValue = cellContainer.getEffectiveValue().getValue().toString();

                    }

                    try{
                        double doubleValue = Double.parseDouble(cellValue);
                        cellValue = CellUtils.formatNumber(doubleValue);
                    }
                    catch (NumberFormatException e) {
                    }

                    // If the cell value is in the set of allowed values, keep the row
                    if(allowedValues != null && allowedValues.size() > 0)
                    {
                        if (!allowedValues.contains(cellValue)) {
                            shouldKeepRow = false;
                            break; // No need to check further, we keep this row
                        }
                    }
                }
            }

            // If the row does not meet the filter criteria, clear the row by setting dummy values
            if (!shouldKeepRow) {
                for (int col = 0; col < currentRow.size(); col++) {
                    currentRow.set(col, new EffectiveValueContainer(
                            new EffectiveValue(ReturnedValueType.EMPTY, ""),
                            CellLocationFactory.fromCellId('Z', "10"))); // Dummy cell location
                }
            }
        }

        // Return the updated grid with empty rows as per the filter
        return gridOfEffectiveValueContainerCopy;
    }

}
