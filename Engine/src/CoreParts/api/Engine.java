package CoreParts.api;

import CoreParts.impl.CellImp;
import CoreParts.impl.SheetCellImp;
import jakarta.xml.bind.JAXBException;

import java.io.FileNotFoundException;

public interface Engine {

    CellImp getRequestedCell(char row, char col);
    SheetCellImp getSheetCell();
    SheetCellImp getSheetCell(int versionNumber);
    void readSheetCellFromXML() throws FileNotFoundException, JAXBException;
    //TODO: Generate all classes from the xml file using JAXB unmarshal
    //TODO: Write a methods that take the class genreated from the unmarshal method and make my on class from itd
    void updateCell(String newValue, char col, char row);

}
