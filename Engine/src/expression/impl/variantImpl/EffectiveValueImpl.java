package expression.impl.variantImpl;


import expression.ReturnedValueType;
import expression.api.EffectiveValue;

import java.io.Serializable;

import static expression.ReturnedValueType.*;

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
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void setType(ReturnedValueType returnedValueType) {
        this.cellType = returnedValueType;
    }

    public boolean isRawType() {
        return this.cellType == NUMERIC || this.cellType == STRING || this.cellType == BOOLEAN;}

    @Override
    public void assertRawType(ReturnedValueType type) throws IllegalArgumentException {
        if (isRawType() && cellType != type) {
            throw new IllegalArgumentException("The type " + this + " is a raw type but is not STRING.");
        }
    }
}

