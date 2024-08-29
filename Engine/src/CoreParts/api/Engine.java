package CoreParts.api;

import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import expression.api.EffectiveValue;
import jakarta.xml.bind.JAXBException;

import java.io.FileNotFoundException;
import java.util.Map;

public interface Engine {
    DtoCell getRequestedCell(String cellId);
    DtoSheetCell getSheetCell();
    DtoSheetCell getSheetCell(int versionNumber);
    void readSheetCellFromXML(String path) throws Exception;
    void updateCell(String newValue, char col, String row) throws Exception;
    void save(String path) throws Exception;
    void load(String path) throws Exception;
}
