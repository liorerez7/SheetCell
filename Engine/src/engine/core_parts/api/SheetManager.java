package engine.core_parts.api;


import dto.components.DtoSheetCell;

import dto.small_parts.CellLocation;
import engine.core_parts.impl.SheetManagerImpl;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
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
    SheetManagerImpl createSheetCellOnlyForRunTime(int versionNumber);

    void updateReplacedCells(String newValue, Set<CellLocation> newValueLocations);

    Map<String,String> getPredictionsForSheet(String startingRangeCellLocation, String endingRangeCellLocation,
                                        String extendedRangeCellLocation, Map<String, String> originalValuesByOrder);

    Map<String,String> updateMultipleCells(Map<String, String> resultStrings, Map<String, String> originalValuesByOrder);

    void updateReplacedCells(Map<String, String> newCellsToBeUpdate);
}
