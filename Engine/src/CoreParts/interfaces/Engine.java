package CoreParts.interfaces;

import CoreParts.impl.CellImp;
import CoreParts.impl.SheetCellImp;

public interface Engine {

    void updateGivenCell();
    CellImp getRequestedCell(char row, char col);
    SheetCellImp getSheetCell();
    SheetCellImp getSheetCell(int versionNumber);
    void readSheetCellFromXML();
    void updateCell(String newValue, char col, char row);

}