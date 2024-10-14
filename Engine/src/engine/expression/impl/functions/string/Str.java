package engine.expression.impl.functions.string;

import engine.core_parts.api.sheet.SheetCellViewOnly;
import engine.expression.ReturnedValueType;
import engine.expression.api.Expression;
import dto.small_parts.EffectiveValue;


public class Str implements Expression {

    private String value;


    public Str(String val) {
        this.value = val;
    }

    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) {
        if(value.isEmpty()){
            return new EffectiveValue(ReturnedValueType.EMPTY, value);
        }
        return new EffectiveValue(ReturnedValueType.STRING, value);
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    public String toString() {
        return value;
    }
}

