package expression.impl.numFunction;

import expression.ReturnedValueType;

import expression.api.Expression;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.EffectiveValue;

public abstract class NumericBinaryOperation extends BinaryExpression {

    public NumericBinaryOperation(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    protected abstract Double applyOperation(Double value1, Double value2);

    @Override
    protected EffectiveValue evaluate(EffectiveValue e1, EffectiveValue e2) {

        try {
            Double result = applyOperation((Double) e1.getValue(), (Double) e2.getValue());
            return new EffectiveValue(ReturnedValueType.NUMERIC, result);
        } catch (ClassCastException e)
        {
            if(e1.getCellType() == ReturnedValueType.EMPTY || e2.getCellType() == ReturnedValueType.EMPTY)
                return new EffectiveValue(ReturnedValueType.NUMERIC,Double.NaN);

            if (e1.getCellType() == ReturnedValueType.UNKNOWN || e2.getCellType() == ReturnedValueType.UNKNOWN)
                return new EffectiveValue(ReturnedValueType.UNKNOWN, Double.NaN);

            else{
                return new EffectiveValue(ReturnedValueType.UNKNOWN, Double.NaN);
            }
        }
    }
}


