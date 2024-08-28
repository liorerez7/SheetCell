package expression.impl.numFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.EffectiveValueImpl;

public class Plus extends BinaryExpression {

    public Plus(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationSign() {
        return "+";
    }
    @Override
    protected EffectiveValue evaluate(EffectiveValue e1, EffectiveValue e2) {
        try {
            Double result = (Double) e1.getValue() + (Double) e2.getValue();
            return new EffectiveValueImpl(ReturnedValueType.NUMERIC, result);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid type of arguments: Both arguments must be of type Double", e);
        }
    }
}
