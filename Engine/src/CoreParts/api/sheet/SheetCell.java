package CoreParts.api.sheet;

import CoreParts.api.Cell;
import CoreParts.smallParts.CellLocation;
import expression.impl.Range;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface SheetCell extends
        SheetCellViewOnly, Serializable, BasicCellOperations,VersionControl,DependencyGraphOperations
{
    Map<CellLocation, Cell> getSheetCell();
    void updateEffectedByAndOnLists();
    void setUpSheet();

    void updateNewRange(String name, String range);

    public boolean isRangePresent(String rangeName);


    public Set<CellLocation> getRange(String rangeName);
}
