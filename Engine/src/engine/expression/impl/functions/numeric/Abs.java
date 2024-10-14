package engine.expression.impl.functions.numeric;


import engine.expression.api.Expression;

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
