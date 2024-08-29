package CoreParts.api.sheet;

import CoreParts.api.Cell;
import CoreParts.smallParts.CellLocation;
import Utility.RefDependencyGraph;
import expression.api.Expression;

import java.io.Serializable;
import java.util.Map;

public interface SheetCell extends
        SheetCellViewOnly, Serializable, BasicCellOperations,VersionControl,DependencyGraphOperations
{
    Map<CellLocation, Cell> getSheetCell();
    void updateEffectedByAndOnLists();
    void setUpSheet();
}
