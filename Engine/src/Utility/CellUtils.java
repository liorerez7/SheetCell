package Utility;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCell;
import CoreParts.smallParts.CellLocationFactory;
import Utility.Exception.AvgWithNoNumericCells;
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
import expression.impl.numFunction.Avg;
import expression.impl.numFunction.Num;
import expression.impl.numFunction.Sum;
import expression.impl.stringFunction.Str;
import java.util.ArrayList;
import java.util.List;


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
        else if(operation == Operation.SUM || operation == Operation.AVG){
            Range range = sheetCell.getRange(arguments.getFirst());
            if(range == null){
                throw new RangeDoesntExist(arguments.getFirst());
            }
            range.addAffectedFromThisRangeCellLocation(targetCell.getLocation());

            if(operation == Operation.SUM){
                return new Sum(range);
            }

            return new Avg(range, targetCell.getLocation().getCellId());
        }

//        else if(operation == Operation.SUM){
//            Range range = sheetCell.getRange(arguments.getFirst());
//            range.addAffectedFromThisRangeCellLocation(targetCell.getLocation());
//            return new Sum(range);
//        }
//
//        else if(operation == Operation.AVG){
//            Range range = sheetCell.getRange(arguments.getFirst());
//            range.addAffectedFromThisRangeCellLocation(targetCell.getLocation());
//            return new Avg(range, targetCell.getLocation().getCellId());
//
//        }

        //isMethod = true;
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

}
