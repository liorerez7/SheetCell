package CoreParts.api.UtilsUI;

import CoreParts.impl.DtoComponents.DtoSheetCell;

public interface Displayer {
    void display(DtoSheetCell sheetCell);
    void displaySpecificVersion(DtoSheetCell sheetCell, int version);
}
