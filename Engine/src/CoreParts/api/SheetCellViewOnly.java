package CoreParts.api;

import CoreParts.smallParts.CellLocation;

public interface SheetCellViewOnly {
    Cell getCell(CellLocation location);
    int getCellLength();
    int getCellWidth();
    int getLatestVersion();
    int getNumberOfRows();
    int getNumberOfColumns();
    String getSheetName();
    int getActiveCellsCount();
}
