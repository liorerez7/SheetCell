package expression.impl.variantImpl;

import Utility.CellUtils;
import expression.api.Expression;
import expression.api.ExpressionVisitor;
import expression.impl.numFunction.Num;
import expression.impl.stringFunction.Str;




public class TravarseExpTreeVisitor implements ExpressionVisitor {

    private Expression toFind;
    private Expression newValue;

    public TravarseExpTreeVisitor(Expression toFind, Expression newValue)
    {
        this.toFind = toFind;
        this.newValue = newValue;
    }

    @Override
    public void visit(UnaryExpression expression) {
        if (expression == toFind)
            expression.setExpression(newValue);
        else
            CellUtils.recalculateCellsHelper(expression.getExpression(), toFind, newValue);
    }

    @Override
    public void visit(BinaryExpression expression) {

        if (expression.getExpressionLeft() == toFind) {
            expression.setExpressionLeft(newValue);
        } else if (expression.getExpressionRight() == toFind) {
            expression.setExpressionRight(newValue);
        } else {
            CellUtils.recalculateCellsHelper(expression.getExpressionLeft(), toFind, newValue);
           CellUtils.recalculateCellsHelper(expression.getExpressionRight(), toFind, newValue);
        }
    }

    @Override
    public void visit(TernaryExpression expression) {

    }

    @Override
    public void visit(Num expression) {
        return;
    }

    @Override
    public void visit(Str expression) {
        return;
    }
}
