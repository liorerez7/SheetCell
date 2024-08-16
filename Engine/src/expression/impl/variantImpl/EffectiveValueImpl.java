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

    //TODO :: to do for later
    /*
    @Override
    public <T> T extractValueWithExpectation(Class<T> type) {
        if (cellType.isAssignableFrom(type)) {
            return type.cast(value);
        }
        // error handling... exception ? return null ?
        return null;
    }

     */
}

