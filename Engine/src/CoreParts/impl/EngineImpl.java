package CoreParts.impl;

import CoreParts.Utility.CellUtils;
import CoreParts.interfaces.Engine;
import Operation.impl.Str;

public class EngineImpl implements Engine {
    private static final Object CONCAT = 1;
    private static final Object SUM = 2;

    SheetCellImp sheetCellImp = new SheetCellImp(4, 5);


    @Override
    public void updateGivenCell() {

    }

    @Override
    public CellImp getRequestedCell(char row, char col) {

        return null;
    }

    private CellImp getCell(char row, char col) {
        return null;
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
    public void updateCell(String newValue, char row, char col) {
        CellImp cellToBeUpdated = getCell(row, col);
        cellToBeUpdated.setOriginalValue(newValue);

        if (CellUtils.trySetNumericValue(cellToBeUpdated, newValue)) {
            return;  // Exit early if the value is a valid number
        }
        if (CellUtils.isPotentialOperation(newValue)) {
            CellUtils.processFunction(cellToBeUpdated, newValue);
        } else {
            cellToBeUpdated.setEffectiveValue(new Str(newValue));
        }
    }
}
