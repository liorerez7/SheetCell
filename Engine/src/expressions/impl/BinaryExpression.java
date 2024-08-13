package expressions.impl;

import expressions.Expression;
import expressions.IsBinary;

public abstract class BinaryExpression implements Expression{

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

     public Expression getExpressionLeft() {
        return expression1;
    }

    public Expression getExpressionRight() {
        return expression2;
    }

    public void setExpressionLeft(Expression newExpression) {
        expression1 = newExpression;
    }

    public void setExpressionRight(Expression newExpression) {
        expression2 = newExpression;
    }

    abstract protected Object evaluate(Object evaluate, Object evaluate2);

}
