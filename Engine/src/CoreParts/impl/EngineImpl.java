package CoreParts.impl;

import Utility.CellUtils;
import CoreParts.interfaces.Engine;
import CoreParts.smallParts.CellLocation;
import expressions.Expression;
import expressions.Operation;
import expressions.impl.numFunction.Num;
import expressions.impl.stringFunction.Str;

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

        Expression expression = processExpression(newValue);

        try {
            expression.evaluate();
            CellImp cellToBeUpdated = getCell(CellLocation.fromCellId(col, row));
            cellToBeUpdated.setOriginalValue(newValue);
            cellToBeUpdated.setEffectiveValue(expression);
        }
        catch(Exception illegalArgumentException){
            throw new IllegalArgumentException("Invalid expression: arguments not of the same type\nValue was not changed");
        }
    }


    public Expression processExpression(String value) {

        if (CellUtils.trySetNumericValue(value)){  // base case: value is : "<number>" for example : "5"
            return new Num(Double.parseDouble(value));
        }
        if(!(CellUtils.isPotentialOperation(value))){  // base case: value is : "<string>" for example : "Hello"
            return new Str(value);
        }

        String functionName = CellUtils.extractFunctionName(value);
        Operation operation = Operation.fromString(functionName);


        if(operation == Operation.REF){ // base case: value is : "{REF, A5}" for example : "{REF, A5}"

            String cellId = CellUtils.removeParantecesFromString(value);
            cellId = CellUtils.splitArguments(cellId).get(1); // the List will be : [REF, A5] and we get the "A5"
            CellImp cell = getCell(CellLocation.fromCellId(cellId));
            return cell.getEffectiveValue();
        }

        // Remove the outermost parentheses and the function name
        String content = CellUtils.removeParantecesFromString(value);
        content = content.substring(functionName.length() +1).trim();

        // Split by top-level commas (ignoring commas within nested braces)
        List<String> arguments = CellUtils.splitArguments(content);

        // Recursively process each argument
        List<Expression> expressions = new ArrayList<>();
        for (String arg : arguments) {
            expressions.add(processExpression(arg.trim()));
        }

        // Create and return the appropriate operation with its arguments
        return operation.calculate(expressions);
    }

}


