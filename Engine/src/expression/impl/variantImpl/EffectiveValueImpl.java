package expression.impl.variantImpl;


import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import static expression.ReturnedValueType.*;

public class EffectiveValueImpl implements EffectiveValue {

    private ReturnedValueType cellType;
    private final ObjectProperty<Object> value;

    public EffectiveValueImpl(ReturnedValueType cellType, Object value) {
        this.cellType = cellType;
        this.value = new SimpleObjectProperty<>(value);
    }



    @Override
    public ObjectProperty<Object> getValueProperty() {
        return value;
    }
    @Override
    public ReturnedValueType getCellType() {
        return cellType;
    }

    @Override
    public Object getValue() {
        return value.get();
    }

    @Override
    public void setValue(Object value) {
        this.value.set(value);
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

    @Override
    public String toString() {
        return value.toString();
    }
}

