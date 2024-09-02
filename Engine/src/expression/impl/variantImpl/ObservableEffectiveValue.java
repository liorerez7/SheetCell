//package expression.impl.variantImpl;
//
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import expression.ReturnedValueType;
//import expression.api.EffectiveValue;
//
//public class ObservableEffectiveValue implements EffectiveValue {
//    private final ObjectProperty<ReturnedValueType> type = new SimpleObjectProperty<>();
//    private final ObjectProperty<Object> value = new SimpleObjectProperty<>();
//
//    @Override
//    public ReturnedValueType getCellType() {
//        return type.get();
//    }
//
//    @Override
//    public void setValue(int value) {
//        this.value.set(value);
//    }
//
//    @Override
//    public Object getValue() {
//        return value.get();
//    }
//
//    @Override
//    public void setType(ReturnedValueType returnedValueType) {
//        type.set(returnedValueType);
//    }
//
//    @Override
//    public void assertRawType(ReturnedValueType returnedValueType) {
//        // Implement type assertion
//    }
//
//    public ObjectProperty<ReturnedValueType> cellTypeProperty() {
//        return type;
//    }
//
//    public ObjectProperty<Object> valueProperty() {
//        return value;
//    }
//}
