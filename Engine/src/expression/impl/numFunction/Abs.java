package expression.impl.numFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.EffectiveValueImpl;
import expression.impl.variantImpl.UnaryExpression;

public class Abs extends UnaryExpression {
        public Abs(Expression expression1) {
            super(expression1);
        }

        @Override
        public String getOperationSign() {
            return "%";
        }

        @Override
        protected EffectiveValue evaluate(EffectiveValue e1) {
            Double result = (Math.abs((Double) e1.getValue()));
            return new EffectiveValueImpl(ReturnedValueType.NUMERIC, result);
        }
    }
