package expression.api;


import expression.ReturnedValueType;

public interface EffectiveValue {
    ReturnedValueType getCellType();
    Object getValue();

   // <T> T extractValueWithExpectation(Class<T> type);     //TODO: mabye add this
}


