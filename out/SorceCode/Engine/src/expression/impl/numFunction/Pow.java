package expression.impl.numFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.EffectiveValueImpl;

public class Pow extends BinaryExpression {
    public Pow(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationSign() {
        return "^";
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue e1, EffectiveValue e2) {
        try {
            double result = Math.pow((Double) e1.getValue(), (Double)e2.getValue());

            if((Double)e1.getValue() == 0 && (Double)e2.getValue() ==  0) {
                result = Double.NaN;
            }

            return new EffectiveValueImpl(ReturnedValueType.NUMERIC, result);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid type of arguments: Both arguments must be of type Double", e);
        }
    }
}
