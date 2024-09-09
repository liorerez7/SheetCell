package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCell;
import CoreParts.api.SheetConvertor;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocationFactory;
import GeneratedClassesEx2.STLSheet;
import Utility.CellUtils;
import CoreParts.api.Engine;
import CoreParts.smallParts.CellLocation;
import Utility.EngineUtilies;
import Utility.Exception.CycleDetectedException;
import Utility.Exception.RefToUnSetCellException;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.stringFunction.Str;
import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class EngineImpl implements Engine {

    private SheetCell sheetCell;

    public EngineImpl() {
        sheetCell = new SheetCellImp(0, 0, "Sheet1", 0, 0, null);
    }

    @Override
    public DtoCell getRequestedCell(String cellId) {
        if (sheetCell.isCellPresent(CellLocationFactory.fromCellId(cellId))) {
            return new DtoCell(getCell(CellLocationFactory.fromCellId(cellId)));
        } else {
            return null;
        }
    }

    @Override
    public DtoSheetCell getSheetCell() {
        return new DtoSheetCell(sheetCell);
    }

    @Override
    public DtoSheetCell getSheetCell(int versionNumber) {
        return new DtoSheetCell(sheetCell, versionNumber);
    }

    @Override
    public void readSheetCellFromXML(String path) throws Exception {

        Path filePath = Paths.get(path);

        if (!filePath.isAbsolute()) {
            throw new IllegalArgumentException("Provided path is not absolute. Please provide a full path.");
        }
        byte[] savedSheetCellState = sheetCell.saveSheetCellState();

        try {
            sheetCell.clearVersionNumber();
            getSheetFromSTL(path);
            sheetCell.setUpSheet();

        } catch (Exception e) {
            restoreSheetCellState(savedSheetCellState);
            throw new Exception(e.getMessage());
        }
    }

    private void getSheetFromSTL(String path) throws FileNotFoundException, JAXBException {
        InputStream in = new FileInputStream(new File(path));
        STLSheet sheet = EngineUtilies.deserializeFrom(in);
        SheetConvertor convertor = new SheetConvertorImpl();
        sheetCell = (SheetCellImp) convertor.convertSheet(sheet);
    }

    @Override
    public void UpdateNewRange(String name, String range) throws IllegalArgumentException {
        sheetCell.updateNewRange(name, range);
    }

    @Override
    public void deleteRange(String name) {
        sheetCell.deleteRange(name);
    }

//    @Override
//    public DtoSheetCell sortSheetCell(String range, String... args) {
//
//        DtoSheetCell dtoSheetCell = getSheetCell();
//
//        EffectiveValue effectiveValue = dtoSheetCell.getEffectiveValue(CellLocationFactory.fromCellId("A1"));
//        effectiveValue.getValue()
//        /*
//
//        input of the method is from the type: "range" and "args"
//        which range can be: "A1..C3" and args can be: "A,B,C" or "A,b or A,B,c"
//        the range represent all the cells that need to be sorted (it create some can or square)
//        and the args represent the order of the sorting by the collumns, if there a same values for the first collumn
//        it will sort by the second collumn and so on.
//
//        the sorting will be from lower to higher.
//
//        each value of the dtoSheetCell is from type EffectiveValue
//        you can get it by using this command:
//
//        EffectiveValue effectiveValue = dtoSheetCell.getEffectiveValue(CellLocationFactory.fromCellId("A1"));
//
//        to get the actual value from the effective value you need to do:
//        effectiveValue.getValue()
//        the return type is an Object that can be String, number, boolean or null.
//
//        you can get the type of the value by using this command:
//        effectiveValue.getCellType()
//        the return type is from the type:
//
//         ReturnedValueType and can be ReturnedValueType.BOOLEAN,
//         ReturnedValueType.NUMERIC, ReturnedValueType.STRING or ReturnedValueType.UNKNOWN
//
//        but for the sorting i will take into account only  ReturnedValueType.NUMERIC and ReturnedValueType.UNKNOWN
//        i want to use the try catch method to check if the value is a number or not.
//        because also UNKNOWN types can or cannot be numeric
//
//        if there are cells without numeric numbers i just dont want it to calculate them, DONT make an exception.
//         */
//
//        return dtoSheetCell;
//    }

    @Override
    public List<CellLocation> getRequestedRange(String name) {
        return sheetCell.getRequestedRange(name);
    }

    @Override
    public void updateCell(String newValue, char col, String row) throws
            CycleDetectedException, IllegalArgumentException, RefToUnSetCellException {

        byte[] savedSheetCellState = sheetCell.saveSheetCellState();

        if (!sheetCell.isCellPresent(CellLocationFactory.fromCellId(col, row)) && newValue.isEmpty()) {
            return;
        }

        Cell targetCell = getCell(CellLocationFactory.fromCellId(col, row));

        if (newValue.isEmpty()) {
            sheetCell.updateVersions(targetCell);
            sheetCell.versionControl();
            sheetCell.removeCell(CellLocationFactory.fromCellId(col, row));
        } else {
            try {
                Expression expression = CellUtils.processExpressionRec(newValue, targetCell, getInnerSystemSheetCell(), false);
                sheetCell.applyCellUpdates(targetCell, newValue, expression);
                sheetCell.updateVersions(targetCell);
                sheetCell.performGraphOperations();
                sheetCell.versionControl();

            } catch (Exception e) {
                restoreSheetCellState(savedSheetCellState);
                throw e;
            }
        }
    }

    @Override
    public void save(String path) throws Exception, IOException, IllegalArgumentException {

        Path filePath = Paths.get(path);
        // Check if the path is absolute
        if (!filePath.isAbsolute()) {
            throw new IllegalArgumentException("Provided path is not absolute. Please provide a full path.");
        }

        if (Files.exists(filePath)) {
            if (!Files.isWritable(filePath)) {
                throw new IOException("No write permission for file: " + filePath.toString());
            }
        }
        try (FileOutputStream fileOut = new FileOutputStream(path);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(sheetCell);
            out.flush();
        } catch (IOException e) {
            throw new Exception("Error saving data to " + path + ": " + e.getMessage(), e);
        }
    }

    private void restoreSheetCellState(byte[] savedSheetCellState) throws IllegalStateException {
        try {
            if (savedSheetCellState != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(savedSheetCellState);
                ObjectInputStream ois = new ObjectInputStream(bais);
                sheetCell = (SheetCellImp) ois.readObject(); // Restore the original sheetCell
            }
        } catch (Exception restoreEx) {
            throw new IllegalStateException("Failed to restore the original sheetCell state", restoreEx);
        }
    }

    public void load(String path) throws Exception, NoSuchFieldException {
        Path filePath = Paths.get(path);
        // Check if the path is absolute
        if (!filePath.isAbsolute()) {
            throw new Exception("Provided path is not absolute. Please provide a full path.");
        }
        try (FileInputStream fileIn = new FileInputStream(new File(path));
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            sheetCell = (SheetCellImp) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new NoSuchFieldException("file not found at this path: " + path);
        }
    }

    public Cell getCell(CellLocation location) {
        // Fetch the cell directly from the map in SheetCellImp
        return sheetCell.getCell(location);
    }

    public SheetCell getInnerSystemSheetCell() {
        return sheetCell;
    }


    @Override
    public DtoSheetCell sortSheetCell(String range, String args) {
        DtoSheetCell dtoSheetCell = getSheetCell();

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

    /**
     * Parses the range, e.g., "A1..C3", to retrieve the corresponding cell locations.
     */
    private List<CellLocation> parseRange(String range, DtoSheetCell dtoSheetCell) {
        String[] parts = range.split("\\.\\.");
        CellLocation start = CellLocationFactory.fromCellId(parts[0]);
        CellLocation end = CellLocationFactory.fromCellId(parts[1]);

        List<CellLocation> locations = new ArrayList<>();

        // Get all cells in the range (assuming range forms a rectangular block)
        for (char col = start.getVisualColumn(); col <= end.getVisualColumn(); col++) {
            for (int row = Integer.parseInt(start.getVisualRow()); row <= Integer.parseInt(end.getVisualRow()); row++) {
                locations.add(CellLocationFactory.fromCellId(col, String.valueOf(row)));
            }
        }

        return locations;
    }

    /**
     * Converts the cell locations in the range into rows of EffectiveValue objects for sorting.
     */
    private List<List<EffectiveValue>> getRowsFromRange(List<CellLocation> cellLocations, DtoSheetCell dtoSheetCell) {

        Map<CellLocation, EffectiveValue> sheetData = dtoSheetCell.getViewSheetCell();

        List<List<EffectiveValue>> rows = new ArrayList<>();

        // Assuming the range is rectangular, calculate the width of the range
        int width = (int) cellLocations.stream().map(CellLocation::getRealColumn).distinct().count();

        List<EffectiveValue> currentRow = new ArrayList<>();
        for (int i = 0; i < cellLocations.size(); i++) {
            CellLocation cellLocation = cellLocations.get(i);
            EffectiveValue effectiveValue = sheetData.get(cellLocation);
            currentRow.add(effectiveValue);

            // Move to the next row after every 'width' number of columns
            if (currentRow.size() == width) {
                rows.add(new ArrayList<>(currentRow));  // Add the current row to the list
                currentRow.clear();  // Start a new row
            }
        }

        return rows;
    }

    /**
     * Sorts the rows based on the columns specified in the args.
     */
    private void sortRowsByColumns(List<List<EffectiveValue>> rows, String arguments, char leftColumn) {
        // Loop over each column given in args (in order of priority)

        List<String> args = extractLetters(arguments);
        int iteratorOfArgs = 0;


        final int colIndex = Character.toUpperCase(args.get(iteratorOfArgs).charAt(0)) - Character.toUpperCase(leftColumn); // Calculate column index

        List<EffectiveValue> col = rows.get(colIndex);

        // Bubble sort the column values and swap entire rows based on these values
        for (int i = 0; i < col.size() - 1; i++) {
            for (int j = 0; j < col.size() - i - 1; j++) {
                EffectiveValue value1 = col.get(j);
                EffectiveValue value2 = col.get(j + 1);

                int result = shouldSwap(value1, value2);

                if (result >= 0) { // value1 >= value2

                    if(result == 0){

                        int innerResult = checkOnNextCollumn(rows, args,++iteratorOfArgs,j, j + 1, leftColumn);

                        iteratorOfArgs = 0;

                        if (innerResult > 0){
                            swapRows(rows, j, j + 1);
                        }
                    }
                    else{
                        swapRows(rows, j, j + 1);
                    }
                }
            }
        }
    }

    private int checkOnNextCollumn(List<List<EffectiveValue>> rows, List<String> args, int iteratorOfArgs, int firstValueRow, int secondValueRow, char leftColumn) {


        if(args.size() <= iteratorOfArgs){
            return 0;
        }

        final int colIndex = Character.toUpperCase(args.get(iteratorOfArgs).charAt(0)) - Character.toUpperCase(leftColumn); // Calculate column index

        List<EffectiveValue> col = rows.get(colIndex);

        EffectiveValue value1 = col.get(firstValueRow);
        EffectiveValue value2 = col.get(secondValueRow);

        int result = shouldSwap(value1, value2);

        if (result >= 0) { // value1 >= value2

            if (result == 0) {

                return checkOnNextCollumn(rows, args, ++iteratorOfArgs, firstValueRow, secondValueRow, leftColumn);

            } else {
                return 1;
            }
        }
        else{
            return -1;
        }
    }


    /**
     * Returns true if row1 should be swapped with row2 based on their column values.
     */
    private int shouldSwap(EffectiveValue value1, EffectiveValue value2) {
        if (value1 == null && value2 == null) return -1;
        if (value1 == null) return 1;
        if (value2 == null) return 0;

        Object val1 = getNumericValue(value1);
        Object val2 = getNumericValue(value2);

        // If both values are numeric, compare them as doubles
        if (val1 instanceof Double && val2 instanceof Double) {

            if(((Double) val1).equals((Double) val2)){
                return 0;
            } else if ((Double) val1 < (Double) val2) {
                return -1;
            }
            return 1;
        }

        return -1;
    }

    private void swapRows(List<List<EffectiveValue>> rows, int indexOfRow1, int indexOfRow2) {
        // Iterate over all columns
        for (List<EffectiveValue> column : rows) {
            // Swap the values at the specified row indices in each column
            EffectiveValue temp = column.get(indexOfRow1);
            column.set(indexOfRow1, column.get(indexOfRow2));
            column.set(indexOfRow2, temp);
        }
    }


    /**
     * Extracts a numeric value from the EffectiveValue, returning null for non-numeric types.
     */
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

    /**
     * Updates the DtoSheetCell with the sorted values.
     */
    private void updateDtoSheetCell(DtoSheetCell dtoSheetCell, List<CellLocation> cellLocations, List<List<EffectiveValue>> cols, char leftColumn) {

        int indexOfPreferredColumn = 0;
        char firstPreferredColumn;

        int startRowOfSorting;
        String startRowOfSortingAsString;

        for (List<EffectiveValue> col : cols) {

            firstPreferredColumn = leftColumn; // E,D,C
            startRowOfSorting = cellLocations.get(0).getRealRow() + 1;  // 3
            startRowOfSortingAsString = Integer.toString(startRowOfSorting); // 3 as a string

            for (EffectiveValue value : col) {
                //needs to put here the first E3
                dtoSheetCell.getViewSheetCell().put(CellLocationFactory.fromCellId(firstPreferredColumn, startRowOfSortingAsString), value);

                startRowOfSortingAsString = Integer.toString(++startRowOfSorting);
            }

            leftColumn++;
        }
    }


    private List<String> extractLetters(String input) {
        // Check if the input is null or empty
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty.");
        }

        // Split the string by commas
        String[] elements = input.split(",");

        // Prepare the list to store valid letters
        List<String> letters = new ArrayList<>();

        // Iterate through the split elements
        for (String element : elements) {
            // Trim the element to remove extra spaces (if any)
            element = element.trim();

            // Check if the element is a single letter (one character and alphabetic)
            if (element.length() == 1 && Character.isLetter(element.charAt(0))) {
                letters.add(element);
            } else if (!element.isEmpty()) { // Handle invalid entries that are non-empty
                throw new IllegalArgumentException("Invalid element found: " + element);
            }
        }

        return letters;
    }
















}