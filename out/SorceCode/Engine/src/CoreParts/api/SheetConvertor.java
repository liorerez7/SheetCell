package CoreParts.api;

import GeneratedClasses.STLSheet;

public interface SheetConvertor {
    SheetCell convertSheet(STLSheet sheet);
    STLSheet convertSheet(SheetCell sheet);
}
