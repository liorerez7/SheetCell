package CoreParts.api;

import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import expression.impl.Range;

import java.util.List;

public interface Engine {
    DtoCell getRequestedCell(String cellId);
    DtoSheetCell getSheetCell();
    DtoSheetCell getSheetCell(int versionNumber);
    void readSheetCellFromXML(String path) throws Exception;
    void updateCell(String newValue, char col, String row) throws Exception;
    void save(String path) throws Exception;
    void load(String path) throws Exception;
    void UpdateNewRange(String name, String range) throws IllegalArgumentException;
    List<CellLocation> getRequestedRange(String name);
}
