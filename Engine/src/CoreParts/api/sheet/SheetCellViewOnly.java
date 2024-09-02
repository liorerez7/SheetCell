package CoreParts.api.sheet;

import CoreParts.api.Cell;
import CoreParts.smallParts.CellLocation;
import Utility.RefDependencyGraph;
import expression.api.EffectiveValue;

import java.io.Serializable;
import java.util.Map;

public interface SheetCellViewOnly extends Serializable, GetSheetMetaData {
    Cell getCell(CellLocation location);
    int getActiveCellsCount();
    Map<CellLocation, Cell> getSheetCell();
    RefDependencyGraph getGraph();
    boolean isCellPresent(CellLocation location);
    Map<CellLocation, EffectiveValue> getViewSheetCell();
}
