package engine.core_parts.api.sheet;

import engine.core_parts.api.Cell;

import engine.utilities.RefDependencyGraph;
import engine.expression.impl.Range;
import dto.small_parts.CellLocation;
import dto.small_parts.EffectiveValue;


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
