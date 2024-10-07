package expression.impl.variantImpl;

import CoreParts.api.sheet.SheetCellViewOnly;
import expression.api.Expression;

public abstract class BinaryExpression implements Expression{

    private Expression leftExpression;
    private Expression rightExpression;

    public BinaryExpression(Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) throws IllegalArgumentException {
        return evaluate(leftExpression.evaluate(sheet), rightExpression.evaluate(sheet));
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

    abstract protected EffectiveValue evaluate(EffectiveValue evaluate, EffectiveValue evaluate2);

}
