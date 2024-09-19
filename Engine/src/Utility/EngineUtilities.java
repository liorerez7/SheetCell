package Utility;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import GeneratedClassesEx2.STLSheet;
import expression.api.EffectiveValue;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.*;


public class EngineUtilities {

    public static STLSheet deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
        Unmarshaller u = jaxbContext.createUnmarshaller();
        return (STLSheet) u.unmarshal(in);
    }

    public static List<String> extractLetters(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty.");
        }

        String[] elements = input.split(",");
        List<String> letters = new ArrayList<>();

        for (String element : elements) {
            element = element.trim();
            letters.add(element);
        }

        return letters;
    }

    public static void updateDtoSheetCell(DtoSheetCell dtoSheetCell, List<CellLocation> cellLocations, List<List<EffectiveValueContainer>> cols, char leftColumn) {

        int startRowOfSorting;

        for (List<EffectiveValueContainer> col : cols) {

            startRowOfSorting = cellLocations.get(0).getRealRow() + 1;

            for (EffectiveValueContainer valueContainer : col) {

                EffectiveValue value = valueContainer.getEffectiveValue();
                dtoSheetCell.getViewSheetCell().put(CellLocationFactory.fromCellId(leftColumn, String.valueOf(startRowOfSorting++)), value);
            }

            leftColumn++;
        }
    }

    public static List<CellLocation> parseRange(String range) {
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

    public static List<List<EffectiveValueContainer>> getRangeGridAsColsFromRange(List<CellLocation> cellLocations, DtoSheetCell dtoSheetCell) {

        Map<CellLocation, EffectiveValue> sheetData = dtoSheetCell.getViewSheetCell();
        List<List<EffectiveValueContainer>> rows = new ArrayList<>();

        int length = (int) cellLocations.stream().map(CellLocation::getRealRow).distinct().count();
        List<EffectiveValueContainer> currentRow = new ArrayList<>();

        for (int i = 0; i < cellLocations.size(); i++) {
            CellLocation cellLocation = cellLocations.get(i);
            EffectiveValue effectiveValue = sheetData.get(cellLocation);
            currentRow.add(new EffectiveValueContainer(effectiveValue, cellLocation));

            if (currentRow.size() == length) {
                rows.add(new ArrayList<>(currentRow));
                currentRow.clear();
            }
        }
        return rows;
    }

    public static List<List<EffectiveValueContainer>> getRangeGridAsRowsFromRange(List<CellLocation> cellLocations, DtoSheetCell dtoSheetCell) {

        Map<CellLocation, EffectiveValue> sheetData = dtoSheetCell.getViewSheetCell();
        List<List<EffectiveValueContainer>> rows = new ArrayList<>();

        // Sort the cell locations by row and column
        cellLocations.sort(Comparator.comparing(CellLocation::getRealRow).thenComparing(CellLocation::getRealColumn));

        List<EffectiveValueContainer> currentRow = new ArrayList<>();
        int currentRowNumber = -1;

        for (CellLocation cellLocation : cellLocations) {
            int rowNumber = cellLocation.getRealRow();

            // If we're on a new row, save the previous one and start a new one
            if (rowNumber != currentRowNumber) {
                if (!currentRow.isEmpty()) {
                    rows.add(new ArrayList<>(currentRow));
                    currentRow.clear();
                }
                currentRowNumber = rowNumber;
            }

            // Add the EffectiveValueContainer to the current row
            EffectiveValue effectiveValue = sheetData.get(cellLocation);
            currentRow.add(new EffectiveValueContainer(effectiveValue, cellLocation));
        }

        // Add the last row after the loop
        if (!currentRow.isEmpty()) {
            rows.add(new ArrayList<>(currentRow));
        }

        return rows;
    }

    public static Map<CellLocation, CellLocation> getOldCellLocationToAfterSortCellLocation(List<List<EffectiveValueContainer>> newCols, char leftColumn, int upperRow, boolean keysAsOld) {

        Map<CellLocation, CellLocation> oldToNewLocationMap = new HashMap<>();

        // Iterate through columns (i represents the column index)
        for (int colIndex = 0; colIndex < newCols.size(); colIndex++) {
            List<EffectiveValueContainer> column = newCols.get(colIndex);

            // Iterate through rows (j represents the row index)
            for (int rowIndex = 0; rowIndex < column.size(); rowIndex++) {
                EffectiveValueContainer container = column.get(rowIndex);
                CellLocation oldLocation = container.getCellLocation();

                // Calculate new cell location based on current column and row indices
                char newColumn = (char) (leftColumn + colIndex); // Adjust for column letter
                int newRow = upperRow + rowIndex + 1;               // Adjust for row number
                String newRowAsString = String.valueOf(newRow);
                // Create the new CellLocation
                CellLocation newLocation = CellLocationFactory.fromCellId(newColumn, newRowAsString);

                if(keysAsOld) {
                    oldToNewLocationMap.put(oldLocation, newLocation);
                }else{
                    oldToNewLocationMap.put(newLocation, oldLocation);
                }

            }
        }

        return oldToNewLocationMap;
    }

    public static DtoContainerData filterSheetCell(String range, Map<Character, Set<String>> filter, DtoSheetCell dtoSheetCell) {
            return SheetCellFilter.filterSheetCell(range, filter, dtoSheetCell);
    }

    public static DtoContainerData sortSheetCell(String range, String args, DtoSheetCell dtoSheetCell){

        return SheetCellSorter.sortSheetCell(range, args, dtoSheetCell);
    }
}
