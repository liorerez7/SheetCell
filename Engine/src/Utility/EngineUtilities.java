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
import java.util.List;
import java.util.Map;


public class EngineUtilities {

    public static STLSheet deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
        Unmarshaller u = jaxbContext.createUnmarshaller();
        return (STLSheet) u.unmarshal(in);
    }

    public static DtoSheetCell sortSheetCell(String range, String args, DtoSheetCell dtoSheetCell){

        return SheetCellSorter.sortSheetCell(range, args, dtoSheetCell);
    }

    public static DtoSheetCell filterSheetCell(String range, String filter, DtoSheetCell dtoSheetCell) {

        return SheetCellFilter.filterSheetCell(range, filter, dtoSheetCell);
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

}
