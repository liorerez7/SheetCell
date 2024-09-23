package expression;

public enum ReturnedValueType {

    NUMERIC(Double.class) ,
    STRING(String.class) ,
    BOOLEAN(Boolean.class) ,
    UNKNOWN(Object.class) ,
    UNDEFINED(Void.class),
    EMPTY(Void.class) ;

    private Class<?> type;

    ReturnedValueType(Class<?> type) {
        this.type = type;
    }

}
