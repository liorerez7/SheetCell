package engine.expression.impl.functions.numeric;

import engine.expression.api.Expression;

public class Pow extends NumericBinaryOperation {
    @Override
    protected Double applyOperation(Double value1, Double value2) {
        if (value1 == 0 && value2 == 0) {
            return Double.NaN;
        }
        return Math.pow(value1, value2);
    }
    public Pow(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationSign() {
        return "^";
    }

}
