package expression.impl.numFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.EffectiveValueImpl;

public class Exponent extends BinaryExpression {

    public Exponent(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationSign() {
        return "^";
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue e1, EffectiveValue e2) {
        Double result = Math.pow((Double) e1.getValue(), (Double)e2.getValue());
        return new EffectiveValueImpl(ReturnedValueType.NUMERIC, result);
    }
}
