package engine.core_parts.api.sheet;

import engine.core_parts.api.Cell;
import engine.expression.impl.Range;
import dto.small_parts.CellLocation;

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

    boolean isRangePresent(String rangeName);
    Set<Range> getSystemRanges();
    Range getRange(String rangeName);

    void deleteRange(String name);

    Set<String> getAllRangeNames();
}
