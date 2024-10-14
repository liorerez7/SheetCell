package engine.core_parts.api;


import dto.components.DtoSheetCell;

import dto.small_parts.CellLocation;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

public interface SheetManager {
    DtoSheetCell getSheetCell();
    DtoSheetCell getSheetCell(int versionNumber);
    void readSheetCellFromXML(InputStream path) throws Exception;
    void updateCell(String newValue, char col, String row) throws Exception;
    void saveCurrentSheetCellState();
    void restoreSheetCellState();
    void UpdateNewRange(String name, String range) throws IllegalArgumentException;
    List<CellLocation> getRequestedRange(String name);
    void deleteRange(String name);
    Set<String> getAllRangeNames();
}
