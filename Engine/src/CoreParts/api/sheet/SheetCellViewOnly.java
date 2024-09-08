package CoreParts.api.sheet;

import CoreParts.api.Cell;
import CoreParts.smallParts.CellLocation;
import Utility.RefDependencyGraph;
import expression.api.EffectiveValue;
import expression.impl.Range;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface SheetCellViewOnly extends Serializable, GetSheetMetaData {
    Cell getCell(CellLocation location);
    int getActiveCellsCount();
    Map<CellLocation, Cell> getSheetCell();
    RefDependencyGraph getGraph();
    boolean isCellPresent(CellLocation location);
    Map<CellLocation, EffectiveValue> getViewSheetCell();
    boolean isRangePresent(String rangeName);
    Range getRange(String rangeName);
    List<CellLocation> getRequestedRange(String name);
    Map<String, List<CellLocation>> getRanges();
}
