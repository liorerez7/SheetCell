package Operation.impl;
import expressions.Expression;
import expressions.impl.BinaryExpression;

public class Minus extends BinaryExpression {

    public Minus(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationSign() {
        return "-";
    }

    @Override
    protected Object evaluate(Object e1, Object e2) {
        return (Double)e1 - (Double)e2;
    }
}
