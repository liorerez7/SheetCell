package CoreParts.api.sheet;

import CoreParts.api.Cell;
import CoreParts.smallParts.CellLocation;
import Utility.RefDependencyGraph;
import expression.api.EffectiveValue;
import expression.impl.Range;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SheetCellViewOnly extends Serializable, GetSheetMetaData {
    int getActiveCellsCount();
    boolean isCellPresent(CellLocation location);
    Map<CellLocation, EffectiveValue> getViewSheetCell();
    boolean isRangePresent(String rangeName);
    Range getRange(String rangeName);
    List<CellLocation> getRequestedRange(String name);
    Map<String, List<CellLocation>> getRanges();

    Cell getCell(CellLocation location);
    RefDependencyGraph getGraph();
    Map<CellLocation, Cell> getSheetCell();

    Map<Character,Set<String>> getUniqueStringsInColumn(String filterColumn, String range);
    Map<Character,Set<String>> getUniqueStringsInColumn(List<Character> columnsForXYaxis, boolean isChartGraph);
}
