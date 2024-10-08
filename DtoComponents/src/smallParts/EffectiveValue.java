package smallParts;

import expression.ReturnedValueType;

import java.io.Serializable;

public class EffectiveValue implements Serializable {

    private ReturnedValueType cellType;
    private Object value;

    public EffectiveValue(ReturnedValueType cellType, Object value) {
        this.cellType = cellType;
        this.value = value;
    }


    public ReturnedValueType getCellType() {
        return cellType;
    }


    public Object getValue() {
        return value;
    }


    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

