package expression.impl.numFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.EffectiveValueImpl;

public class Plus extends NumericBinaryOperation {

    public Plus(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }
    @Override
    protected Double applyOperation(Double value1, Double value2) {
        return value1 + value2;
    }
    @Override
    public String getOperationSign() {
        return "+";
    }
}
