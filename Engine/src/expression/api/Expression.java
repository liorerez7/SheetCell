package expression.api;

import CoreParts.api.sheet.SheetCellViewOnly;

import java.io.Serializable;

public interface Expression extends Serializable {

    EffectiveValue evaluate(SheetCellViewOnly sheet) throws IllegalArgumentException;
    String getOperationSign();
}
