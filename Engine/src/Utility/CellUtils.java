package Utility;

import CoreParts.api.Cell;
import CoreParts.impl.InnerSystemComponents.SheetCellImp;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import expression.Operation;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.api.ExpressionVisitor;
import expression.api.processing.ExpressionParser;
import expression.impl.Processing.ExpressionParserImpl;
import expression.impl.numFunction.Num;
import expression.impl.stringFunction.Str;
import expression.impl.variantImpl.TravarseExpTreeVisitor;

import java.util.ArrayList;
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
    // TODO : when cell is updated we need to delete his relayed by cells.

    public static Expression processExpressionRec(String value, Cell targetCell, SheetCellImp sheetCell, Set<Cell> CloneAffectedBy) {// this is a recursive function
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
            Cell cellThatBeenEffected = sheetCell.getCell(CellLocationFactory.fromCellId(arguments.getFirst()));
            return handleReferenceOperation(cellThatBeenEffected, targetCell, CloneAffectedBy);//argument(1) = CELL_ID
        }
        return operation.calculate(processArguments(arguments, targetCell, sheetCell, CloneAffectedBy));
    }

    private static Expression handleReferenceOperation(Cell cellThatBeenEffected, Cell cellThatAffects, Set<Cell> CloneAffectedBy) {

        validateCircularDependency(cellThatBeenEffected, cellThatAffects, CloneAffectedBy);
        CloneAffectedBy.add(cellThatBeenEffected);

        if (cellThatBeenEffected.getEffectiveValue() == null) {

            throw new IllegalArgumentException("Invalid expression: cell referenced before being set");
        }

        return cellThatBeenEffected.getEffectiveValue();
    }

    public static void validateCircularDependency(Cell cell, Cell targetCell, Set<Cell> CloneAffectedBy) {

        if (cell.getAffectedBy().isEmpty()) {
            return;
        }

        if (cell.isCellAffectedBy(targetCell) == true) {
            throw new IllegalArgumentException("Invalid expression: circular dependency");
        } else {
            for (Cell cell1 : cell.getAffectedBy()) {
                validateCircularDependency(cell1, targetCell, CloneAffectedBy);
            }
        }
    }

    public static List<Expression> processArguments(List<String> arguments, Cell targetCell, SheetCellImp sheetCell, Set<Cell> CloneAffectedBy) {
        List<Expression> expressions = new ArrayList<>();
        for (String arg : arguments) {
            expressions.add(processExpressionRec(arg.trim(), targetCell, sheetCell, CloneAffectedBy));
        }
        return expressions;
    }

    public static void recalculateCellsHelper(Expression expTree, Expression toFind, Expression newValue) {
        ExpressionVisitor visitor = new TravarseExpTreeVisitor(toFind, newValue);
        expTree.accept(visitor);
        validateExpression(expTree);
    }

    public static void recalculateCellsRec(Cell targetCell, Expression oldExpression) {
        for (Cell cell : targetCell.getAffectingOn()) {
            ExpressionParser parser = new ExpressionParserImpl(cell.getOriginalValue());
            if (Operation.fromString(parser.getFunctionName()) == Operation.REF) {
                cell.setEffectiveValue(targetCell.getEffectiveValue());
                cell.updateVersion(targetCell.getLatestVersion());
                recalculateCellsRec(cell, oldExpression);
            }
            Expression effectiveValue = cell.getEffectiveValue();
            recalculateCellsHelper(effectiveValue, oldExpression, targetCell.getEffectiveValue());
        }
    }

    public static void updateAffectedByAndOnLists(Cell targetCell, Set<Cell> CloneAffectedBy) {

        for (Cell cell : targetCell.getAffectedBy()) {
            cell.removeCellFromAffectingOn(targetCell);
        }

        targetCell.getAffectedBy().clear(); // clears only after unMark the ref cells recursively

        for (Cell cell : CloneAffectedBy) {
            cell.addCellToAffectingOn(targetCell);
        }

        for (Cell cell : CloneAffectedBy) {
            targetCell.addCellToAffectedBy(cell);
        }

    }

    public static void validateExpression(Expression expression) {
        expression.evaluate().getValue();
    }

    public static void formatDoubleValue(EffectiveValue value) {
        if (value.getCellType() == ReturnedValueType.NUMERIC) {
            double numericValue = (double) value.getValue();
            if (numericValue == Math.floor(numericValue))
                value.setValue((int)numericValue);
        }
    }
    public static boolean isWithinLocationBounds(int col, int row, int maxCol, int maxRow) {
        return (col >= 'A' && col <= maxCol) && (row >= '1' && row <= maxRow);
    }
}
