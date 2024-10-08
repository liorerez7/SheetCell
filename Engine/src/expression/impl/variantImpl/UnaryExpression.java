package expression.impl.variantImpl;

import CoreParts.api.sheet.SheetCellViewOnly;
import expression.api.Expression;
import smallParts.EffectiveValue;

public abstract class UnaryExpression implements Expression {

    private Expression expression;

    public UnaryExpression(Expression expression) {
        this.expression = expression;
    }
    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) throws IllegalArgumentException {
        return evaluate(expression.evaluate(sheet));
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
    abstract protected EffectiveValue evaluate(EffectiveValue evaluate);

}
