package CoreParts.impl;

import CoreParts.interfaces.Cell;
import Utility.CellUtils;
import CoreParts.interfaces.Engine;
import CoreParts.smallParts.CellLocation;
import expressions.Expression;
import expressions.Operation;
import expressions.impl.BinaryExpression;
import expressions.impl.UnaryExpression;
import expressions.impl.numFunction.Num;
import expressions.impl.numFunction.Plus;
import expressions.impl.stringFunction.Str;
import expressions.IsBinary;
import java.util.ArrayList;
import java.util.List;

public class EngineImpl implements Engine {

    private SheetCellImp sheetCellImp = new SheetCellImp(4, 3);


    @Override
    public void updateGivenCell() {

    }

    @Override
    public CellImp getRequestedCell(char row, char col) {

        return null;
    }


    public CellImp getCell(CellLocation location) {
        return sheetCellImp.getCell(location);
    }

    @Override
    public SheetCellImp getSheetCell() {
        return null;
    }

    @Override
    public SheetCellImp getSheetCell(int versionNumber) {
        return null;
    }

    @Override
    public void readSheetCellFromXML() {

    }


    /* TODO : Number and String's effective and original value needed to be set before calling the calculate function
              String and Number shouldnt be called by the calculate function
              read properly each argument from the 'newValue' and add to List the proper cells

    */

    @Override
    //TODO :maybe change the implementaion of the function: checkIfCellsAreOfSameType,
    // explanation why is inside the function.

    public void updateCell(String newValue, char col, char row) {

        Cell targetCell = getCell(CellLocation.fromCellId(col, row));
        Expression expression = processExpression(newValue, targetCell);

        try {
            expression.evaluate();
            targetCell.setOriginalValue(newValue);
            //before change:
            Expression oldExpression = targetCell.getEffectiveValue(); // old expression
            targetCell.setEffectiveValue(expression);
            recalculateCells(targetCell, oldExpression);
        }
        catch(Exception illegalArgumentException){
            throw new IllegalArgumentException("Invalid expression: arguments not of the same type\nValue was not changed");
        }
    }

    public Expression processExpression(String value, Cell targetCell) {
        if (CellUtils.trySetNumericValue(value)) {  // base case: value is a number
            return new Num(Double.parseDouble(value));
        }

        if (!CellUtils.isPotentialOperation(value)) {  // base case: value is a string
            return new Str(value);
        }

        List<String> arguments = parseArguments(value);
        Operation operation = Operation.fromString(arguments.get(0));

        if (operation == Operation.REF) {
            return handleReferenceOperation(arguments.get(1), targetCell);
        }

        return operation.calculate(processArguments(arguments.subList(1, arguments.size()), targetCell));
    }

    private List<String> parseArguments(String value) {
        String cellId = CellUtils.removeParantecesFromString(value);
        return CellUtils.splitArguments(cellId);//for example Plus 5 6
    }

    private Expression handleReferenceOperation(String cellId, Cell targetCell) {
        CellImp cell = getCell(CellLocation.fromCellId(cellId));

        validateCircularDependency(cell, targetCell);

        if (cell.getEffectiveValue() == null) {
            throw new IllegalArgumentException("Invalid expression: cell referenced before being set");
        }

        return cell.getEffectiveValue();
    }

    private void validateCircularDependency(CellImp cell, Cell targetCell) {
        if (cell.isCellAffectedBy(targetCell)==false) {
            targetCell.addCellToAffectedBy(cell);
            cell.addCellToAffectingOn(targetCell);
        } else {
            throw new IllegalArgumentException("Invalid expression: circular dependency");
        }
    }

    private List<Expression> processArguments(List<String> arguments, Cell targetCell) {
        List<Expression> expressions = new ArrayList<>();
        for (String arg : arguments) {
            expressions.add(processExpression(arg.trim(), targetCell));
        }
        return expressions;
    }

     private void recalculateCellsHelper(Expression expTree, Expression toFind, Expression newValue) {
         if (expTree instanceof Num || expTree instanceof Str) {  // base case: value is a number
             return;
         }

         if (expTree instanceof BinaryExpression) {
             BinaryExpression expTree1 = (BinaryExpression) expTree;

             if (expTree1.getExpressionLeft() == toFind) {
                 expTree1.setExpressionLeft(newValue);
             } else if (expTree1.getExpressionRight() == toFind) {
                    expTree1.setExpressionRight(newValue);
             } else {

                 recalculateCellsHelper(expTree1.getExpressionLeft(), toFind, newValue);
                 recalculateCellsHelper(expTree1.getExpressionRight(), toFind, newValue);
             }
         } else if (expTree instanceof UnaryExpression) {
             UnaryExpression expTree1 = (UnaryExpression) expTree;
             if (expTree1.getExpression() == toFind)
                    expTree1.setExpression(newValue);
             else
                 recalculateCellsHelper(expTree1.getExpression(), toFind, newValue);
         }
         //TODO: ADD TRINARY EXPRESSION
     }
    private void recalculateCells(Cell targetCell, Expression oldExpression) {
        for (Cell cell : targetCell.getAffectingOn()) {

            Expression effectiveValue = cell.getEffectiveValue();
            recalculateCellsHelper(effectiveValue, oldExpression, targetCell.getEffectiveValue());
        }
    }

}


