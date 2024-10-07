package expression.impl.variantImpl;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;

public class EffectiveValueImpl implements EffectiveValue {

    private ReturnedValueType cellType;
    private Object value;

    public EffectiveValueImpl(ReturnedValueType cellType, Object value) {
        this.cellType = cellType;
        this.value = value;
    }

    @Override
    public ReturnedValueType getCellType() {
        return cellType;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

