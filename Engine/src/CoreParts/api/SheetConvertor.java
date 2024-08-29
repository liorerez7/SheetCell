package CoreParts.api;

import CoreParts.api.sheet.SheetCell;
import GeneratedClasses.STLSheet;

public interface SheetConvertor {
    SheetCell convertSheet(STLSheet sheet);
    STLSheet convertSheet(SheetCell sheet);
}
