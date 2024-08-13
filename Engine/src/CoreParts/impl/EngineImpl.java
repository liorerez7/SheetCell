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
import expressions.impl.stringFunction.Str;

import java.util.ArrayList;
import java.util.List;

public class EngineImpl implements Engine {

    private SheetCellImp sheetCellImp = new SheetCellImp(4, 3);

    @Override
    public CellImp getRequestedCell(char row, char col) {

        return null;
    }

    public CellImp getCell(CellLocation location) {
        return sheetCellImp.getCell(location);
    }

    @Override
    public SheetCellImp getSheetCell() {
        return sheetCellImp;
    }

    @Override
    public SheetCellImp getSheetCell(int versionNumber) {
        return null;
    }

    @Override
    public void readSheetCellFromXML() {

    }
    @Override
    //TODO :maybe change the implementaion of the function: checkIfCellsAreOfSameType,
    // explanation why is inside the function.

    public void updateCell(String newValue, char col, char row) {

        Cell targetCell = getCell(CellLocation.fromCellId(col, row));
        Expression expression = CellUtils.processExpressionRec(newValue, targetCell,getSheetCell());
        try {
            expression.evaluate();
            targetCell.setOriginalValue(newValue);
            //before change:
            Expression oldExpression = targetCell.getEffectiveValue(); // old expression
            targetCell.setEffectiveValue(expression);
            CellUtils.recalculateCellsRec(targetCell, oldExpression);
        }
        catch(Exception illegalArgumentException){
            throw new IllegalArgumentException("Invalid expression: arguments not of the same type\nValue was not changed");
        }
    }

}


