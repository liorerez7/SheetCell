package expression.impl.stringFunction;

import expression.api.Expression;
import expression.api.ExpressionVisitor;

public class Str implements Expression {

    private String value;

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

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

