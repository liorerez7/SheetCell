package engine.expression.impl.functions.bool;
import engine.expression.api.Expression;

public class Equal extends BooleanBinaryOperation<Boolean> {

    public Equal(Expression expression1, Expression expression2) {
        super(expression1, expression2, Boolean.class);
    }

    @Override
    protected Boolean applyOperation(Boolean value1, Boolean value2) {
        return value1.equals(value2);
    }

    @Override
    public String getOperationSign() {
        return "==";
    }
}
