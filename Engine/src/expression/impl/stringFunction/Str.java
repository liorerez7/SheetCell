package expression.impl.stringFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.api.ExpressionVisitor;
import expression.impl.variantImpl.EffectiveValueImpl;

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
    public EffectiveValue evaluate() {
        return new EffectiveValueImpl(ReturnedValueType.STRING, value);
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

