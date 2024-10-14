package engine.expression.impl.functions.numeric;

import engine.expression.ReturnedValueType;
import engine.expression.api.Expression;
import engine.expression.impl.functions.type.UnaryExpression;

import dto.small_parts.EffectiveValue;

public abstract class NumericUnaryExpression extends UnaryExpression {

    public NumericUnaryExpression(Expression expression) {
        super(expression);
    }
    protected abstract Double applyOperation(Double value1);

    @Override
    protected EffectiveValue evaluate(EffectiveValue evaluate) {

        try {
            Double result = applyOperation((Double) evaluate.getValue());
            return new EffectiveValue(ReturnedValueType.NUMERIC, result);

        } catch (ClassCastException e)
        {
            if(evaluate.getCellType() == ReturnedValueType.EMPTY)
                return new EffectiveValue(ReturnedValueType.EMPTY,"");

            if (evaluate.getCellType() == ReturnedValueType.UNKNOWN)
                return new EffectiveValue(ReturnedValueType.UNKNOWN, Double.NaN);

            else{
                return new EffectiveValue(ReturnedValueType.UNKNOWN, Double.NaN);
            }
        }
    }
}
