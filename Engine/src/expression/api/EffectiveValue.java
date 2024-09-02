package expression.api;


import expression.ReturnedValueType;
import javafx.beans.property.ObjectProperty;

import java.io.Serializable;

public interface EffectiveValue extends Serializable {
    ObjectProperty<Object> getValueProperty();
    ReturnedValueType getCellType();
    Object getValue();
    void setValue(Object value);

    void setType(ReturnedValueType returnedValueType);

    void assertRawType(ReturnedValueType returnedValueType);

    // <T> T extractValueWithExpectation(Class<T> type);     //TODO: mabye add this
}


