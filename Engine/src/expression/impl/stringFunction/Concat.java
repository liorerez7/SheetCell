package expression.impl.stringFunction;

import expression.api.Expression;
import expression.variantImpl.BinaryExpression;

public class Concat extends BinaryExpression {


    public Concat(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    protected Object evaluate(Object evaluate1, Object evaluate2) {

        return (String)evaluate1 + " " + (String)evaluate2;
    }


}
