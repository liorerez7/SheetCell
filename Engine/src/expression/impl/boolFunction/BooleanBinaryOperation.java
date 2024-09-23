package expression.impl.boolFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.EffectiveValueImpl;

public abstract class BooleanBinaryOperation<T> extends BinaryExpression {

    private final Class<T> type;  // To store the type (Double or Boolean)

    public BooleanBinaryOperation(Expression leftExpression, Expression rightExpression, Class<T> type) {
        super(leftExpression, rightExpression);
        this.type = type;
    }

    protected abstract Boolean applyOperation(T value1, T value2);

    @Override
    protected EffectiveValue evaluate(EffectiveValue e1, EffectiveValue e2) {

        try {
            Boolean result = applyOperation((T) e1.getValue(), (T) e2.getValue());
            return new EffectiveValueImpl(ReturnedValueType.BOOLEAN, result);
        } catch (ClassCastException e)
        {
            return new EffectiveValueImpl(ReturnedValueType.BOOLEAN,"UNDEFINED");
        }
    }
}
