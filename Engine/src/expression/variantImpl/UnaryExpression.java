package expression.variantImpl;

import expression.api.Expression;

public abstract class UnaryExpression implements Expression {

    private Expression expression;

    public UnaryExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object evaluate() {
        return expression.evaluate();
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    public String toString() {
        return expression.toString();
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression newExpression) {
        expression = newExpression;
    }

}
