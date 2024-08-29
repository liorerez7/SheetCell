package expression.impl.numFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.EffectiveValueImpl;

public abstract class NumericBinaryOperation extends BinaryExpression {

    public NumericBinaryOperation(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }
    protected abstract Double applyOperation(Double value1, Double value2);
    @Override
    protected EffectiveValue evaluate(EffectiveValue e1, EffectiveValue e2) {
        try {
            Double result = applyOperation((Double) e1.getValue(), (Double) e2.getValue());
            return new EffectiveValueImpl(ReturnedValueType.NUMERIC, result);
        } catch (ClassCastException e) {
            if(e1.getCellType() == ReturnedValueType.EMPTY || e2.getCellType() == ReturnedValueType.EMPTY){ {
                return new EffectiveValueImpl(ReturnedValueType.EMPTY,"");
            }
            }
            if (e1.getCellType() == ReturnedValueType.UNKNOWN || e2.getCellType() == ReturnedValueType.UNKNOWN) {
                return new EffectiveValueImpl(ReturnedValueType.UNKNOWN, Double.NaN);
            }
            throw new IllegalArgumentException("Invalid type of arguments: Both arguments must be of type Double", e);
        }
    }
}

