package CoreParts.impl;

import CoreParts.api.Cell;
import GeneratedClasses.STLSheet;
import Utility.CellUtils;
import CoreParts.api.Engine;
import CoreParts.smallParts.CellLocation;
import Utility.EngineUtilies;
import expression.api.Expression;
import jakarta.xml.bind.JAXBException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
    public void readSheetCellFromXML(String path) throws FileNotFoundException, JAXBException {
        InputStream in = new FileInputStream(new File(path));
        STLSheet sheet = EngineUtilies.deserializeFrom(in);

    }

    @Override
    public void updateCell(String newValue, char col, char row) {
        Cell targetCell = getCell(CellLocation.fromCellId(col, row));

        CellUtils.unMarkCellRef(targetCell);

        Expression expression = CellUtils.processExpressionRec(newValue,targetCell,getSheetCell());//TODO:we are adding to the lists before we deleted the old ones. also we need to delete only when the expression is valid
        try {
            expression.evaluate().getValue();
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


