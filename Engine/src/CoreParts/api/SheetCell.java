package CoreParts.api;

import CoreParts.smallParts.CellLocation;

public interface SheetCell {

    public SheetCell restoreSheetCell(int versionNumber);
    public Cell getCell(CellLocation location);
}
