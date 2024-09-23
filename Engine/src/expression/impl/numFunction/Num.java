package expression.impl.numFunction;

import CoreParts.api.sheet.SheetCellViewOnly;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
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
    public String getOperationSign() {
        return "";
    }

    @Override
    public String toString() {
        if (num % 1 == 0) {  // Check if num is an integer (no decimal part)
            return String.format("%d", (int) num);  // Print as integer
        } else {
            return String.format("%.2f", num);  // Print as double with 2 decimal places
        }
    }

}
