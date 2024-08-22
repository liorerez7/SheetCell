package CoreParts.api;

import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import jakarta.xml.bind.JAXBException;

import java.io.FileNotFoundException;

public interface Engine {
    DtoCell getRequestedCell(String cellId,boolean updateCell);
    DtoSheetCell getSheetCell();
    DtoSheetCell getSheetCell(int versionNumber);
    void readSheetCellFromXML(String path) throws Exception;
    void updateCell(String newValue, char col, String row);
    void exit();
    void save(String path) throws Exception;
    void load(String path) throws Exception;
}
