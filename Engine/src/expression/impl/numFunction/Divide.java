package expression.impl.numFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.EffectiveValueImpl;

public class Divide extends BinaryExpression {
    public Divide(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationSign() {
        return "/";
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue e1, EffectiveValue e2) {
        double result = ((Double) e1.getValue()/(Double)e2.getValue());

        if((Double)e2.getValue() == 0){
            result = Double.NaN;
        }

        return new EffectiveValueImpl(ReturnedValueType.NUMERIC, result);
    }
}
