package Operation.impl;

import expressions.Expression;
import expressions.impl.BinaryExpression;

public class Concat extends BinaryExpression {


    public Concat(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected Object evaluate(Object evaluate, Object evaluate2) {
        return (String)evaluate + " " + (String)evaluate2;
    }

    @Override
    public String getOperationSign() {
        return "";
    }
}
