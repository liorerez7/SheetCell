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

        if (CellUtils.trySetNumericValue(value)){  // base case: value is : "<number>" for example : "5"
            return new Num(Double.parseDouble(value));
        }
        if(!(CellUtils.isPotentialOperation(value))){  // base case: value is : "<string>" for example : "Hello"
            return new Str(value);
        }

        String functionName = CellUtils.extractFunctionName(value);
        Operation operation = Operation.fromString(functionName);
        String cellId = CellUtils.removeParantecesFromString(value);

        if(operation == Operation.REF){ // base case: value is : "{REF, A5}" for example : "{REF, A5}"
            cellId = CellUtils.splitArguments(cellId).get(1); // the List will be : [REF, A5] and we get the "A5"
            CellImp cell = getCell(CellLocation.fromCellId(cellId));

            if((cell.isCellAffectedBy(targetCell) == false))
            {
                targetCell.addCellToAffectedBy(cell);
                cell.addCellToAffectingOn(targetCell);
            }

            if(cell.getEffectiveValue() == null){
                throw new IllegalArgumentException("Invalid expression: cell referenced before being set");
            }

            return cell.getEffectiveValue();
        }

        // Remove the outermost parentheses and the function name
        cellId = cellId.substring(functionName.length() +1).trim();

        // Split by top-level commas (ignoring commas within nested braces)
        List<String> arguments = CellUtils.splitArguments(content);

        // Recursively process each argument
        List<Expression> expressions = new ArrayList<>();
        for (String arg : arguments) {
            expressions.add(processExpression(arg.trim(), targetCell));
        }

        // Create and return the appropriate operation with its arguments
        return operation.calculate(expressions);
    }

    private void recalculateCells(Cell targetCell, Expression oldExpression) {

        for(Cell cell : targetCell.getAffectingOn()){

            if(cell.getEffectiveValue() instanceof BinaryExpression){

                if(((BinaryExpression)cell.getEffectiveValue()).getExpressionLeft() == oldExpression){

                    ((BinaryExpression)cell.getEffectiveValue()).setExpressionLeft(targetCell.getEffectiveValue());
                }
                else if (((BinaryExpression)cell.getEffectiveValue()).getExpressionRight() == oldExpression){

                    ((BinaryExpression)cell.getEffectiveValue()).setExpressionRight(targetCell.getEffectiveValue());

                }
            }
            else{
                ((UnaryExpression)cell.getEffectiveValue()).setExpression(targetCell.getEffectiveValue());
            }
        }
    }

}


