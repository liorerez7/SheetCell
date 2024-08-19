package CoreParts.api;

import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.impl.InnerSystemComponents.SheetCellImp;
import jakarta.xml.bind.JAXBException;

import java.io.FileNotFoundException;

public interface Engine {

    DtoCell getRequestedCell(String cellId);
    DtoSheetCell getSheetCell();
    SheetCellImp getSheetCell(int versionNumber);
    void readSheetCellFromXML(String path) throws FileNotFoundException, JAXBException;
    //TODO: Generate all classes from the xml file using JAXB unmarshal
    //TODO: Write a methods that take the class genreated from the unmarshal method and make my on class from itd
    void updateCell(String newValue, char col, char row);

}
