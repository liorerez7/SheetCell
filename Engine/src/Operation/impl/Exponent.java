package Operation.impl;

import expressions.Expression;
import expressions.impl.BinaryExpression;

public class Exponent extends BinaryExpression {

    public Exponent(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationSign() {
        return "^";
    }

    @Override
    protected Object evaluate(Object e1, Object e2) {
        return Math.pow((Double) e1, (Double) e2);
    }

}
