package engine.expression.api;

import engine.core_parts.api.sheet.SheetCellViewOnly;
import dto.small_parts.EffectiveValue;

import java.io.Serializable;

public interface Expression extends Serializable {

    EffectiveValue evaluate(SheetCellViewOnly sheet) throws IllegalArgumentException;
    String getOperationSign();
}
