package expression.impl.numFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.EffectiveValueImpl;
import expression.impl.variantImpl.UnaryExpression;

public abstract class NumericUnaryExpression extends UnaryExpression {

    public NumericUnaryExpression(Expression expression) {
        super(expression);
    }
    protected abstract Double applyOperation(Double value1);

    @Override
    protected EffectiveValue evaluate(EffectiveValue evaluate) {

        try {
            Double result = applyOperation((Double) evaluate.getValue());
            return new EffectiveValueImpl(ReturnedValueType.NUMERIC, result);

        } catch (ClassCastException e)
        {
            if(evaluate.getCellType() == ReturnedValueType.EMPTY)
                return new EffectiveValueImpl(ReturnedValueType.EMPTY,"");

            if (evaluate.getCellType() == ReturnedValueType.UNKNOWN)
                return new EffectiveValueImpl(ReturnedValueType.UNKNOWN, Double.NaN);

            else{
                return new EffectiveValueImpl(ReturnedValueType.UNKNOWN, Double.NaN);
            }
        }
    }
}
