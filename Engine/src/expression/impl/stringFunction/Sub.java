package expression.impl.stringFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.EffectiveValueImpl;
import expression.impl.variantImpl.TernaryExpression;

public class Sub  extends TernaryExpression {

    public Sub(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    @Override//TODO:not working getting error not of the same type
    protected EffectiveValue evaluate(EffectiveValue source,
                                      EffectiveValue start,
                                      EffectiveValue end) throws IndexOutOfBoundsException {
        String sourceValue = source.getValue().toString();
        int startValue = (int) start.getValue();
        int endValue = (int) end.getValue();
        String result = sourceValue.substring(startValue,endValue); //TODO:not working getting error not of the same type
        return new EffectiveValueImpl(ReturnedValueType.STRING,result);
    }
}