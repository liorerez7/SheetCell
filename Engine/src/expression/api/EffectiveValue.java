package expression.api;


import expression.ReturnedValueType;
import javafx.beans.property.ObjectProperty;

import java.io.Serializable;

public interface EffectiveValue extends Serializable {

    ReturnedValueType getCellType();
    Object getValue();
    void setValue(Object value);
}


