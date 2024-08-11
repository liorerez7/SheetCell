package expressions.impl;

import expressions.Expression;

public abstract class BinaryExpression implements Expression {

    private Expression expression1;
    private Expression expression2;

    public BinaryExpression(Expression expression1, Expression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public Object evaluate() {
        return evaluate(expression1.evaluate(), expression2.evaluate());
    }

    @Override
    public String toString() {
        return "(" + expression1 + getOperationSign() + expression2 + ")";
    }

    abstract protected Object evaluate(Object evaluate, Object evaluate2);

}
