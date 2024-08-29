package expression.impl.stringFunction;

import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.EffectiveValueImpl;

public class Concat extends BinaryExpression {


    public Concat(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue evaluate1, EffectiveValue evaluate2) {
        evaluate1.assertRawType(ReturnedValueType.STRING);
        evaluate2.assertRawType(ReturnedValueType.STRING);

        if (evaluate1.getCellType() == ReturnedValueType.UNDEFINED || evaluate2.getCellType() == ReturnedValueType.UNDEFINED)
            return new EffectiveValueImpl(ReturnedValueType.UNDEFINED, "UNDEFINED");
        else {
            try {
                String result = (String) (evaluate1.getValue()) + " " + (String) evaluate2.getValue();
                return new EffectiveValueImpl(ReturnedValueType.STRING, result);
            } catch (ClassCastException e) {
                if (evaluate1.getCellType() == ReturnedValueType.EMPTY || evaluate2.getCellType() == ReturnedValueType.EMPTY)
                    return new EffectiveValueImpl(ReturnedValueType.EMPTY, "");

                if (evaluate1.getCellType() == ReturnedValueType.UNKNOWN || evaluate2.getCellType() == ReturnedValueType.UNKNOWN)
                    return new EffectiveValueImpl(ReturnedValueType.UNKNOWN, "UNDEFINED");

                throw new IllegalArgumentException("Invalid type of arguments: Both arguments must be of type String", e);
            }
        }
    }
}

