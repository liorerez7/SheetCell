package expression.impl.boolFunction;

import CoreParts.api.sheet.SheetCellViewOnly;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.EffectiveValueImpl;

public class Bool implements Expression {

    private boolean value;

    public Bool(boolean val) {
        this.value = val;
    }




    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) {
        return new EffectiveValueImpl(ReturnedValueType.BOOLEAN, value);
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


