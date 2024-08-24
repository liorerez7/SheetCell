package expression;

public enum ReturnedValueType {

    NUMERIC(Double.class) ,
    STRING(String.class) ,
    BOOLEAN(Boolean.class) ;

    private Class<?> type;

    ReturnedValueType(Class<?> type) {
        this.type = type;
    }
    //TODO : maybe add this:
    /*
    public boolean isAssignableFrom(Class<?> aType) {
        return type.isAssignableFrom(aType);
    }
    */
}
