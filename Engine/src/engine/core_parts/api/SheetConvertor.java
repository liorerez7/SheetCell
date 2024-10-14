package engine.core_parts.api;

import engine.core_parts.api.sheet.SheetCell;
import ex2.STLSheet;

public interface SheetConvertor {
    SheetCell convertSheet(STLSheet sheet);
}
