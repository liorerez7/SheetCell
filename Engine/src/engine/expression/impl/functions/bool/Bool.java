package engine.expression.impl.functions.bool;

import engine.core_parts.api.sheet.SheetCellViewOnly;
import dto.small_parts.ReturnedValueType;
import engine.expression.api.Expression;
import dto.small_parts.EffectiveValue;


public class Bool implements Expression {

    private boolean value;

    public Bool(boolean val) {
        this.value = val;
    }

    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) {
        return new EffectiveValue(ReturnedValueType.BOOLEAN, value);
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    public String toString() {
        return value ? "TRUE" : "FALSE";
    }
}


