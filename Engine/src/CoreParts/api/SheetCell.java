package CoreParts.api;

import CoreParts.smallParts.CellLocation;

public interface SheetCell {

    SheetCell restoreSheetCell(int versionNumber);
    Cell getCell(CellLocation location);
    int getCellLength();
    int getCellWidth();
    int getLatestVersion();
    int getNumberOfRows();
    int getNumberOfColumns();
    String getSheetName();
    void setCell(CellLocation location, Cell cell);

    }
