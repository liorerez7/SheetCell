package expression.impl.numFunction;

import CoreParts.api.SheetCellViewOnly;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.api.ExpressionVisitor;
import expression.impl.variantImpl.EffectiveValueImpl;

public class Num implements Expression {

    private double num;

    public Num(double num) {
        this.num = num;
    }

    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) {
        return new EffectiveValueImpl(ReturnedValueType.NUMERIC, num);
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    public String toString() {
        return num < 0 ?
                "(" + num + ")" :
                Double.toString(num);
    }

}
