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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EngineUtilities {

    public static STLSheet deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
        Unmarshaller u = jaxbContext.createUnmarshaller();
        return (STLSheet) u.unmarshal(in);
    }


    public static SortContainerData sortSheetCell(String range, String args, DtoSheetCell dtoSheetCell){

        return SheetCellSorter.sortSheetCell(range, args, dtoSheetCell);
    }

    public static DtoSheetCell filterSheetCell(String range, String filter, DtoSheetCell dtoSheetCell, String filterColumn) {

        return SheetCellFilter.filterSheetCell(range, filter, dtoSheetCell, filterColumn);
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

    public static void updateDtoSheetCell(DtoSheetCell dtoSheetCell, List<CellLocation> cellLocations, List<List<EffectiveValue>> cols, char leftColumn) {

        int startRowOfSorting;

        for (List<EffectiveValue> col : cols) {

            startRowOfSorting = cellLocations.get(0).getRealRow() + 1;

            for (EffectiveValue value : col) {
                dtoSheetCell.getViewSheetCell().put(CellLocationFactory.fromCellId(leftColumn, String.valueOf(startRowOfSorting++)), value);
            }

            leftColumn++;
        }
    }

    public static void updateDtoSheetCelll(DtoSheetCell dtoSheetCell, List<CellLocation> cellLocations, List<List<EffectiveValueContainer>> cols, char leftColumn) {

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

    public static List<List<EffectiveValue>> getRowsFromRange(List<CellLocation> cellLocations, DtoSheetCell dtoSheetCell) {

        Map<CellLocation, EffectiveValue> sheetData = dtoSheetCell.getViewSheetCell();
        List<List<EffectiveValue>> rows = new ArrayList<>();

        int length = (int) cellLocations.stream().map(CellLocation::getRealRow).distinct().count();
        List<EffectiveValue> currentRow = new ArrayList<>();

        for (int i = 0; i < cellLocations.size(); i++) {
            currentRow.add(sheetData.get(cellLocations.get(i)));
            if (currentRow.size() == length) {
                rows.add(new ArrayList<>(currentRow));
                currentRow.clear();
            }
        }
        return rows;
    }

    public static List<List<EffectiveValueContainer>> getRowsFromRangee(List<CellLocation> cellLocations, DtoSheetCell dtoSheetCell) {

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


//    public static Map<CellLocation, CellLocation> getOldCellLocationToAfterSortCellLocation(List<List<EffectiveValueContainer>> newCols, char leftColumn, int upperRow) {
//
//
//        // newCols is an double array that can be from (0,0) to (3,4) for example
//        // but the true locations that it represent can be calculated by the leftColumn and upperRow
//        // for example if leftColumn is 'C' and upperRow is 3 then the first cell in the newCols is (C,3)
//        // and the second cell is (C,4) and so on untill we get to the next column
//        // my goal is to go over the newCols and create a map that will map the old cell location to the new cell location
//        // the old cellLocation reachable by doing this:
//        // newCols.get(0).get(0).getCellLocation(); // and in our example that will be the old cellLocation of (C,3)
//
//    }

    public static Map<CellLocation, CellLocation> getOldCellLocationToAfterSortCellLocationn(List<List<EffectiveValueContainer>> newCols, char leftColumn, int upperRow) {

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

                // Map old location to new location
              //  oldToNewLocationMap.put(oldLocation, newLocation);
                oldToNewLocationMap.put(newLocation, oldLocation);

            }
        }

        return oldToNewLocationMap;
    }
}
