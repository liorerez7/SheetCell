package Utility;

import CoreParts.api.Cell;
import CoreParts.impl.InnerSystemComponents.SheetCellImp;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import Utility.Exception.RefToUnSetCell;
import expression.Operation;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.api.ExpressionVisitor;
import expression.api.processing.ExpressionParser;
import expression.impl.Processing.ExpressionParserImpl;
import expression.impl.Ref;
import expression.impl.numFunction.Num;
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
    // TODO : when cell is updated we need to delete his relayed by cells.
    public static Expression processExpressionRec(String value, Cell targetCell, SheetCellImp sheetCell) throws RefToUnSetCell {// this is a recursive function
        ExpressionParser parser = new ExpressionParserImpl(value);
        if (CellUtils.trySetNumericValue(value)) {  // base case: value is a number
            return new Num(Double.parseDouble(value));
        }
        if (!parser.isPotentialOperation()) {  // base case: value is a string
            return new Str(value);
        }
        List<String> arguments = parser.getArgumentList();
        Operation operation = Operation.fromString(parser.getFunctionName());// argument(0) = FUNCION_NAME

        if (operation == Operation.REF) {
            Cell cellThatAffects = sheetCell.getCell(CellLocationFactory.fromCellId(arguments.getFirst()));
            return handleReferenceOperation(cellThatAffects);  //argument(1) = CELL_ID
        }

        return operation.calculate(processArguments(arguments, targetCell, sheetCell));
    }

    private static Expression handleReferenceOperation(Cell cellThatAffects) throws RefToUnSetCell {

        if (cellThatAffects.getEffectiveValue() == null) {

            throw new RefToUnSetCell(cellThatAffects);
        }

        return new Ref(cellThatAffects.getLocation());
    }


    public static List<Expression> processArguments(List<String> arguments, Cell targetCell, SheetCellImp sheetCell) throws RefToUnSetCell {
        List<Expression> expressions = new ArrayList<>();
        for (String arg : arguments) {
            expressions.add(processExpressionRec(arg.trim(), targetCell, sheetCell));
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
