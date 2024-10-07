package expression.impl.numFunction;


import expression.api.Expression;

public class Divide extends NumericBinaryOperation {
    @Override
    protected Double applyOperation(Double value1, Double value2) {
        if (value2 == 0) {
            return Double.NaN;
        }
        return value1 / value2;
    }

    public Divide(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationSign() {
        return "/";
    }
}
