package expression.impl.stringFunction;

import CoreParts.api.sheet.SheetCellViewOnly;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.EffectiveValueImpl;

public class Str implements Expression {

    private String value;


    public Str(String val) {
        this.value = val;
    }

    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) {
        if(value.isEmpty()){
            return new EffectiveValueImpl(ReturnedValueType.EMPTY, value);
        }
        return new EffectiveValueImpl(ReturnedValueType.STRING, value);
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

