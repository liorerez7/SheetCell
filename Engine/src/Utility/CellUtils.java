package Utility;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCell;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import Utility.Exception.CellCantBeEvaluatedException;
import Utility.Exception.RangeDoesntExistException;
import Utility.Exception.RefToUnSetCellException;
import expression.Operation;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.api.processing.ExpressionParser;
import expression.impl.Processing.ExpressionParserImpl;
import expression.impl.Range;
import expression.impl.Ref;
import expression.impl.boolFunction.Bool;
import expression.impl.numFunction.Average;
import expression.impl.numFunction.Num;
import expression.impl.numFunction.Sum;
import expression.impl.stringFunction.Str;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CellUtils {
    
    public static boolean trySetNumericValue(String value) {
        try {
            Double numericValue = Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Expression processExpressionRec(String value, Cell targetCell, SheetCell sheetCell, boolean insideMethod) throws RefToUnSetCellException {

        ExpressionParser parser = new ExpressionParserImpl(value);
        boolean removeSpacesBeforeArguments = true;

        // Handle numeric values (base case)
        if (CellUtils.trySetNumericValue(value)) {
            return handleNumericValue(value, targetCell, insideMethod);
        }

        // Handle string values (base case)
        if (!parser.isPotentialOperation()) {
            return handleStringValue(value, insideMethod);
        }

        // Parse operation
        Operation operation = Operation.fromString(parser.getFunctionName());
        if (operation == Operation.CONCAT) {
            removeSpacesBeforeArguments = false;
        }

        List<String> arguments = parser.getArgumentList(removeSpacesBeforeArguments);

        // Handle specific operations
        switch (operation) {
            case REF:
                return handleReferenceOperation(sheetCell, arguments.get(0));
            case SUM:
            case AVERAGE:
                return handleRangeOperation(operation, sheetCell, targetCell, arguments.get(0));
            default:
                return operation.calculate(processArguments(arguments, targetCell, sheetCell, true));
        }
    }

    private static Expression handleNumericValue(String value, Cell targetCell, boolean insideMethod) throws CellCantBeEvaluatedException {
        String trimmedValue = value.trim();

        if (insideMethod && !value.equals(trimmedValue)) {
            throw new CellCantBeEvaluatedException(targetCell);
        }

        return new Num(Double.parseDouble(trimmedValue));
    }

    private static Expression handleStringValue(String value, boolean insideMethod) {
        String trimmedValue = insideMethod ? value : value.trim();

        if (trimmedValue.equalsIgnoreCase("True") || trimmedValue.equalsIgnoreCase("False")) {
            return new Bool(Boolean.parseBoolean(trimmedValue.toLowerCase()));
        }

        return new Str(trimmedValue);
    }

    private static Expression handleReferenceOperation(SheetCell sheetCell, String cellId) {
        Cell referencedCell = sheetCell.getCell(CellLocationFactory.fromCellId(cellId));
        return handleReferenceOperation(referencedCell);
    }

    private static Expression handleRangeOperation(Operation operation, SheetCell sheetCell, Cell targetCell, String rangeId) throws RangeDoesntExistException {
        Range range = sheetCell.getRange(rangeId);

        if (range == null) {
            throw new RangeDoesntExistException(rangeId);
        }

        range.addAffectedFromThisRangeCellLocation(targetCell.getLocation());

        if (operation == Operation.SUM) {
            return new Sum(range);
        }

        return new Average(range, targetCell.getLocation().getCellId());
    }

    private static Expression handleReferenceOperation(Cell cellThatAffects) throws RefToUnSetCellException {

        return new Ref(cellThatAffects.getLocation());
    }


    public static List<Expression> processArguments(List<String> arguments, Cell targetCell, SheetCell sheetCell, boolean insideMethod)
            throws RefToUnSetCellException {

        List<Expression> expressions = new ArrayList<>();
        for (String arg : arguments) {

            expressions.add(processExpressionRec(arg, targetCell, sheetCell, insideMethod));
        }
        return expressions;
    }


    public static boolean isWithinBounds(String from, String to, int numOfRows, int numOfColumns) { // Convert from and to into CellLocation objects
        CellLocation startCell = CellLocationFactory.fromCellId(from);
        CellLocation endCell = CellLocationFactory.fromCellId(to);

        // Get the column and row indices for the start and end cells
        char startCol = startCell.getVisualColumn();
        char endCol = endCell.getVisualColumn();
        int startRow = Integer.parseInt(startCell.getVisualRow());
        int endRow = Integer.parseInt(endCell.getVisualRow());

        // Convert the columns to indices (A = 1, B = 2, etc.)
        int startColIndex = startCol - 'A' + 1;
        int endColIndex = endCol - 'A' + 1;

        // Check if the range is within the bounds of the grid
        return startRow >= 1 && endRow <= numOfRows && startColIndex >= 1 && endColIndex <= numOfColumns;
    }

    public static Set<CellLocation> getCellsInRange(String from, String to, int numOfRows, int numOfColumns) {
        Set<CellLocation> cellLocations = new HashSet<>();

        // Convert from and to into CellLocation objects
        CellLocation startCell = CellLocationFactory.fromCellId(from);
        CellLocation endCell = CellLocationFactory.fromCellId(to);

        // Get the column and row indices for the start and end cells
        char startCol = startCell.getVisualColumn();
        char endCol = endCell.getVisualColumn();
        int startRow = Integer.parseInt(startCell.getVisualRow());
        int endRow = Integer.parseInt(endCell.getVisualRow());

        // Ensure the columns and rows are in the correct order
        if (startCol > endCol || startRow > endRow) {
            throw new IllegalArgumentException("Invalid range: start cell must be before end cell.");
        }

        // Loop through the columns and rows to generate all CellLocations in the range
        for (char col = startCol; col <= endCol; col++) {
            for (int row = startRow; row <= endRow; row++) {
                // Check if the cell is within the grid bounds
                if (row <= numOfRows && (col - 'A' + 1) <= numOfColumns) {
                    String cellId = col + Integer.toString(row);
                    CellLocation cellLocation = CellLocationFactory.fromCellId(cellId);
                    cellLocations.add(cellLocation);
                }
            }
        }

        return cellLocations;
    }


    public static List<Character> processCharString(String input) throws IllegalArgumentException {
        // Trim and split the input string by commas
        String[] parts = input.trim().split("\\s*,\\s*");

        // Check if the split resulted in empty parts or incorrect format
        if (parts.length == 0 || containsEmptyOrInvalidParts(parts)) {
            throw new IllegalArgumentException("Input format is invalid.");
        }

        List<Character> result = new ArrayList<>();

        for (String part : parts) {
            if (part.length() != 1) {
                throw new IllegalArgumentException("Each part must be a single character.");
            }
            // Convert to uppercase
            result.add(part.toUpperCase().charAt(0));
        }

        return result;
    }

    private static boolean containsEmptyOrInvalidParts(String[] parts) {
        for (String part : parts) {
            if (part.isEmpty()) {
                return true; // Empty part found
            }
        }
        return false; // No empty parts found
    }

    public static String formatNumber(double num) {
        if (num % 1 == 0) {  // Check if num is an integer (no decimal part)
            return String.format("%d", (int) num);  // Print as integer
        } else {
            return String.format("%.2f", num);  // Print as double with 2 decimal places
        }
    }
}







// TODO: old version, needs to check if the new version is still good after more testing..

//    public static Expression processExpressionRec(String value, Cell targetCell, SheetCell sheetCell, boolean insideMethod) throws RefToUnSetCellException {// this is a recursive function
//
//        ExpressionParser parser = new ExpressionParserImpl(value);
//        boolean removeSpacesBeforeArguments = true;
//
//        if (CellUtils.trySetNumericValue(value)) {  // base case: value is a number
//
//            if (insideMethod) {
//
//                String trimmedValue = value.trim();
//
//                if (!(value.equals(trimmedValue))) {
//                    throw new CellCantBeEvaluatedException(targetCell);
//                }
//            }
//            else{
//                value = value.trim();
//            }
//
//            return new Num(Double.parseDouble(value));
//        }
//
//        if (!parser.isPotentialOperation()) {  // base case: value is a string
//
//            if (!insideMethod) {
//                value = value.trim();  // Trim spaces for ordinary strings
//            }
//
//            if (value.equalsIgnoreCase("True") || value.equalsIgnoreCase("False")) {
//                return new Bool(Boolean.parseBoolean(value.toLowerCase()));  // Parse ignoring case
//            }
//
//            return new Str(value);
//        }
//
//        Operation operation = Operation.fromString(parser.getFunctionName());// argument(0) = FUNCION_NAME
//
//        if(operation == Operation.CONCAT){
//            removeSpacesBeforeArguments = false;
//        }
//
//        List<String> arguments = parser.getArgumentList(removeSpacesBeforeArguments);
//
//        if (operation == Operation.REF) {
//            Cell cellThatAffects = sheetCell.getCell(CellLocationFactory.fromCellId(arguments.getFirst()));
//            return handleReferenceOperation(cellThatAffects);  //argument(1) = CELL_ID
//        }
//        else if(operation == Operation.SUM || operation == Operation.AVERAGE){
//            Range range = sheetCell.getRange(arguments.getFirst());
//            if(range == null){
//                throw new RangeDoesntExistException(arguments.getFirst());
//            }
//            range.addAffectedFromThisRangeCellLocation(targetCell.getLocation());
//
//            if(operation == Operation.SUM){
//                return new Sum(range);
//            }
//
//            return new Average(range, targetCell.getLocation().getCellId());
//        }
//
//        return operation.calculate(processArguments(arguments, targetCell, sheetCell, true));
//    }