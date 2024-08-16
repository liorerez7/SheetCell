package expression.impl.variantImpl;

import expression.api.Expression;
import expression.api.ExpressionVisitor;

public abstract class BinaryExpression implements Expression{

    private Expression leftExpression;
    private Expression rightExpression;

    public BinaryExpression(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    @Override
    public Object evaluate() {
        return evaluate(leftExpression.evaluate(), rightExpression.evaluate());
    }

    @Override
    public String toString() {
        return "(" + leftExpression + getOperationSign() + rightExpression + ")";
    }

     public Expression getExpressionLeft() {
        return leftExpression;
    }

    public Expression getExpressionRight() {
        return rightExpression;
    }

    public void setExpressionLeft(Expression newExpression) {
        leftExpression = newExpression;
    }

    public void setExpressionRight(Expression newExpression) {
        rightExpression = newExpression;
    }

    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    abstract protected Object evaluate(Object evaluate, Object evaluate2);

}
