package CoreParts.api;

import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import Utility.DtoContainerData;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Engine {
    DtoCell getRequestedCell(String cellId);
    DtoSheetCell getSheetCell();
    DtoSheetCell getSheetCell(int versionNumber);
    void readSheetCellFromXML(String path) throws Exception;
    void updateCell(String newValue, char col, String row) throws Exception;

    void saveCurrentSheetCellState();

    void restoreSheetCellState();

    void save(String path) throws Exception;
    void load(String path) throws Exception;
    void UpdateNewRange(String name, String range) throws IllegalArgumentException;
    List<CellLocation> getRequestedRange(String name);
    void deleteRange(String name);
    DtoContainerData sortSheetCell(String range, String columnsToSortBy);

    Map<Character,Set<String>> getUniqueStringsInColumn(String filterColumn, String range);
    Map<Character,Set<String>> getUniqueStringsInColumn(List<Character> columnsForXYaxis, boolean isChartGraph);


    DtoContainerData filterSheetCell(String range, Map<Character, Set<String>> filter);

    Set<String> getAllRangeNames();
}
