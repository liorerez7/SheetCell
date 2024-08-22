package expression.impl;

import CoreParts.api.Cell;
import CoreParts.api.SheetCellViewOnly;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.UnaryExpression;

public class Ref extends UnaryExpression {
    Cell cell;
    public Ref(Expression expression,Cell cell) {
        super(expression);
        this.cell = cell;
    }
    @Override
    protected EffectiveValue evaluate(EffectiveValue evaluate) {
        return cell.getEffectiveValue().evaluate();
    }
}
