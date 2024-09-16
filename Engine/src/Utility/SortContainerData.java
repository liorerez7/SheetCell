package Utility;

import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortContainerData {
    private DtoSheetCell dtoSheetCell;
    private Map<CellLocation, CellLocation> afterSortCellLocationToOldCellLocation;

    public SortContainerData(DtoSheetCell dtoSheetCell, Map<CellLocation, CellLocation> afterSortCellLocationToOldCellLocation) {
        this.dtoSheetCell = dtoSheetCell;
        this.afterSortCellLocationToOldCellLocation = afterSortCellLocationToOldCellLocation;
    }

    public DtoSheetCell getDtoSheetCell() {
        return dtoSheetCell;
    }

    public Map<CellLocation, CellLocation> getOldCellLocationToAfterSortCellLocation() {
        return afterSortCellLocationToOldCellLocation;
    }

    public CellLocation getOldCellLocation(CellLocation cellLocation) {

        return afterSortCellLocationToOldCellLocation.get(cellLocation);
    }
}
