package expression.api;


import expression.ReturnedValueType;

import java.io.Serializable;

public interface EffectiveValue{
    ReturnedValueType getCellType();
    Object getValue();

    void setValue(int value);

    // <T> T extractValueWithExpectation(Class<T> type);     //TODO: mabye add this
}


