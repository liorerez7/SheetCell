package engine.expression.impl.functions.numeric;

import engine.expression.api.Expression;

public class Percent extends NumericBinaryOperation{

    public Percent(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected Double applyOperation(Double value1, Double value2) {
        return value1 * value2 / 100;
    }

    @Override
    public String getOperationSign() {
        return "percent";
    }
}
