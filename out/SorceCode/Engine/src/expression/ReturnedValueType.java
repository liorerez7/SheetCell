package expression;

public enum ReturnedValueType {

    NUMERIC(Double.class) ,
    STRING(String.class) ,
    BOOLEAN(Boolean.class) ,
    UNDEFINED(Void.class) ;

    private Class<?> type;

    ReturnedValueType(Class<?> type) {
        this.type = type;
    }

}
