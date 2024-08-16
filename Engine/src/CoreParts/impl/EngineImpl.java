package CoreParts.impl;

import CoreParts.api.Cell;
import Utility.CellUtils;
import CoreParts.api.Engine;
import CoreParts.smallParts.CellLocation;
import expression.api.Expression;

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

    public void updateCell(String newValue, char col, char row) {

        Cell targetCell = getCell(CellLocation.fromCellId(col, row));
        //TODO : CHECK OF WE REALLY NEED TO SEND THE SHEETCELL IN LINE 60
        Expression expression = CellUtils.processExpressionRec(newValue,targetCell,getSheetCell());
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


