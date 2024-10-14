
package engine.expression.impl.functions.bool;

import engine.expression.api.Expression;

public class Bigger extends BooleanBinaryOperation<Double> {

    public Bigger(Expression leftExpression, Expression rightExpression) {
        super(leftExpression, rightExpression, Double.class);
    }

    @Override
    protected Boolean applyOperation(Double value1, Double value2) {
        return value1 >= value2;
    }


    @Override
    public String getOperationSign() {
        return ">=";
    }
}
