package expression.api;


import expression.ReturnedValueType;

import java.io.Serializable;

public interface EffectiveValue extends Serializable {
    ReturnedValueType getCellType();
    Object getValue();
    void setValue(int value);

    void setType(ReturnedValueType returnedValueType);

    void assertRawType(ReturnedValueType returnedValueType);

    // <T> T extractValueWithExpectation(Class<T> type);     //TODO: mabye add this
}


