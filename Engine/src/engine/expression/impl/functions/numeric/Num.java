package engine.expression.impl.functions.numeric;

import engine.core_parts.api.sheet.SheetCellViewOnly;
import engine.expression.ReturnedValueType;
import engine.expression.api.Expression;

import dto.small_parts.EffectiveValue;


public class Num implements Expression {

    private double num;

    public Num(double num) {
        this.num = num;
    }

    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) {
        return new EffectiveValue(ReturnedValueType.NUMERIC, num);
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
