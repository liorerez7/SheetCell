package CoreParts.api;

import CoreParts.impl.CellImp;
import CoreParts.impl.SheetCellImp;

public interface Engine {

    void updateGivenCell();
    CellImp getRequestedCell(char row, char col);
    SheetCellImp getSheetCell();
    SheetCellImp getSheetCell(int versionNumber);
    void readSheetCellFromXML();
    //TODO: Generate all classes from the xml file using JAXB unmarshal
    //TODO: Write a methods that take the class genreated from the unmarshal method and make my on class from itd
    void updateCell(String newValue, char col, char row);

}
