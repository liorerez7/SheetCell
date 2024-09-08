package Utility;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCell;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import Utility.Exception.CellCantBeEvaluated;
import Utility.Exception.RangeDoesntExist;
import Utility.Exception.RefToUnSetCell;
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

    public static Expression processExpressionRec(String value, Cell targetCell, SheetCell sheetCell, boolean insideMethod) throws RefToUnSetCell {// this is a recursive function

        ExpressionParser parser = new ExpressionParserImpl(value);

        if (CellUtils.trySetNumericValue(value)) {  // base case: value is a number

            if (insideMethod) {

                String trimmedValue = value.trim();

                if (!(value.equals(trimmedValue))) {
                    throw new CellCantBeEvaluated(targetCell);
                }
            }
            else{
                value = value.trim();
            }

            return new Num(Double.parseDouble(value));
        }

        if (!parser.isPotentialOperation()) {  // base case: value is a string

            if (!insideMethod) {
                value = value.trim();  // Trim spaces for ordinary strings
            }

            if (value.equalsIgnoreCase("True") || value.equalsIgnoreCase("False")) {
                return new Bool(Boolean.parseBoolean(value.toLowerCase()));  // Parse ignoring case
            }

            return new Str(value);
        }

        List<String> arguments = parser.getArgumentList();
        Operation operation = Operation.fromString(parser.getFunctionName());// argument(0) = FUNCION_NAME

        if (operation == Operation.REF) {
            Cell cellThatAffects = sheetCell.getCell(CellLocationFactory.fromCellId(arguments.getFirst()));
            return handleReferenceOperation(cellThatAffects);  //argument(1) = CELL_ID
        }
        else if(operation == Operation.SUM || operation == Operation.AVERAGE){
            Range range = sheetCell.getRange(arguments.getFirst());
            if(range == null){
                throw new RangeDoesntExist(arguments.getFirst());
            }
            range.addAffectedFromThisRangeCellLocation(targetCell.getLocation());

            if(operation == Operation.SUM){
                return new Sum(range);
            }

            return new Average(range, targetCell.getLocation().getCellId());
        }

        return operation.calculate(processArguments(arguments, targetCell, sheetCell, true));
    }

    private static Expression handleReferenceOperation(Cell cellThatAffects) throws RefToUnSetCell {

        return new Ref(cellThatAffects.getLocation());
    }


    public static List<Expression> processArguments(List<String> arguments, Cell targetCell, SheetCell sheetCell, boolean insideMethod)
            throws RefToUnSetCell {

        List<Expression> expressions = new ArrayList<>();
        for (String arg : arguments) {

            expressions.add(processExpressionRec(arg, targetCell, sheetCell, insideMethod));
        }
        return expressions;
    }

    public static void formatDoubleValue(EffectiveValue value) {
        if (value.getCellType() == ReturnedValueType.NUMERIC) {
            double numericValue = (double) value.getValue();
            if (numericValue == Math.floor(numericValue))
                value.setValue((int)numericValue);
        }
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
}
