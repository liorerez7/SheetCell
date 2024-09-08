package expression.impl.boolFunction;

import expression.api.Expression;

public class Less extends BooleanBinaryOperation<Double> {

    public Less(Expression leftExpression, Expression rightExpression) {
        super(leftExpression, rightExpression, Double.class);
    }

    @Override
    protected Boolean applyOperation(Double value1, Double value2) {
        return value1 <= value2;
    }

    @Override
    public String getOperationSign() {
        return "";
    }
}
