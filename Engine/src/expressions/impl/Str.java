package expressions.impl;

import expressions.Expression;

public class Str implements Expression {

    private String value;

    public Str(String val) {
        this.value = val;
    }

    @Override
    public Object evaluate() {
        return value;
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    public String toString() {
        return value;
    }
}

