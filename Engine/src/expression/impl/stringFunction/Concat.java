package expression.impl.stringFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.EffectiveValueImpl;

public class Concat extends BinaryExpression {


    public Concat(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue evaluate1, EffectiveValue evaluate2) {
        String result = (String)(evaluate1.getValue()) + " " + (String)(evaluate2.getValue());
        return new EffectiveValueImpl(ReturnedValueType.STRING, result);
    }
}
