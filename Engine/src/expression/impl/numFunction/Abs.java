package expression.impl.numFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.EffectiveValueImpl;
import expression.impl.variantImpl.UnaryExpression;

public class Abs extends NumericUnaryExpression {
        public Abs(Expression expression1) {
            super(expression1);
        }
    @Override
    protected Double applyOperation(Double value1) {return Math.abs(value1);}

    @Override
        public String getOperationSign() {
            return "%";
        }
    }
