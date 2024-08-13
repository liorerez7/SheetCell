package expressions.impl;

import expressions.Expression;

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

//    public Expression findBinaryExpInTree(Expression toFind) {
//
//
//
//
//
//        if (leftExpression == toFind) {
//            return leftExpression;
//        }
//        if (rightExpression == toFind) {
//
//            return rightExpression;
//        }
//
//        // Traverse left and right subtrees
//        Expression found = findBinaryExpInTree(((BinaryExpression)leftExpression).findBinaryExpInTree(toFind));
//        if (found != null) {
//            return found;
//        }
//        return traverseBinaryExpTree(rightExpression, toFind);
//    }

    public void setExpressionLeft(Expression newExpression) {
        leftExpression = newExpression;
    }

    public void setExpressionRight(Expression newExpression) {
        rightExpression = newExpression;
    }

    abstract protected Object evaluate(Object evaluate, Object evaluate2);

}
