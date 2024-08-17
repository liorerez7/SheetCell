package Utility;

import CoreParts.api.Cell;
import CoreParts.impl.CellImp;
import CoreParts.impl.SheetCellImp;
import CoreParts.smallParts.CellLocation;
import expression.Operation;
import expression.api.Expression;
import expression.api.ExpressionVisitor;
import expression.api.processing.ExpressionParser;
import expression.impl.Processing.ExpressionParserImpl;
import expression.impl.numFunction.Num;
import expression.impl.stringFunction.Str;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.TravarseExpTreeVisitor;

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

    public static Expression processExpressionRec(String value, Cell targetCell, SheetCellImp sheetCell) {// this is a recursive function
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
            Cell cellThatBeenEffected = sheetCell.getCell(CellLocation.fromCellId(arguments.getFirst()));
            return handleReferenceOperation(cellThatBeenEffected, targetCell);//argument(1) = CELL_ID
        }
        return operation.calculate(processArguments(arguments, targetCell, sheetCell));
    }

    private static Expression handleReferenceOperation(Cell cellThatBeenEffected, Cell cellThatAffects) {
        validateCircularDependency(cellThatBeenEffected, cellThatAffects);

        if (cellThatBeenEffected.getEffectiveValue() == null) {
            throw new IllegalArgumentException("Invalid expression: cell referenced before being set");
        }

        return cellThatBeenEffected.getEffectiveValue();
    }

    public static void validateCircularDependency(Cell cell, Cell targetCell) {
        if (cell.isCellAffectedBy(targetCell) == false) {
            targetCell.addCellToAffectedBy(cell);
            cell.addCellToAffectingOn(targetCell);
        } else {
            throw new IllegalArgumentException("Invalid expression: circular dependency");
        }
    }

    public static List<Expression> processArguments(List<String> arguments, Cell targetCell, SheetCellImp sheetCell) {
        List<Expression> expressions = new ArrayList<>();
        for (String arg : arguments) {
            expressions.add(processExpressionRec(arg.trim(), targetCell, sheetCell));
        }
        return expressions;
    }

    public static void recalculateCellsHelper(Expression expTree, Expression toFind, Expression newValue) {
        ExpressionVisitor visitor = new TravarseExpTreeVisitor(toFind, newValue);
        expTree.accept(visitor);
    }

    public static void recalculateCellsRec(Cell targetCell, Expression oldExpression) {
        for (Cell cell : targetCell.getAffectingOn()) {
            Expression effectiveValue = cell.getEffectiveValue();
            recalculateCellsHelper(effectiveValue, oldExpression, targetCell.getEffectiveValue());
        }
    }
}
