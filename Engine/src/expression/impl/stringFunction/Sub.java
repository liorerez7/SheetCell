package expression.impl.stringFunction;

import expression.ReturnedValueType;
import expression.api.Expression;

import expression.impl.variantImpl.TernaryExpression;
import smallParts.EffectiveValue;

public class Sub  extends TernaryExpression {

    public Sub(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }
    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue source,
                                      EffectiveValue start,
                                      EffectiveValue end) throws IndexOutOfBoundsException {

        if(source.getCellType() == ReturnedValueType.NUMERIC){
            throw new IllegalArgumentException();
        }

        String sourceValue = source.getValue().toString();
        double startValue = (double) start.getValue();
        double endValue = (double) end.getValue();
        int startInt = (int) startValue;
        int endInt = (int) endValue;

        // Check for invalid ranges
        if (startInt < 0 || startInt >= sourceValue.length() ||
                endInt < 0 || endInt > sourceValue.length() || startInt > endInt) {
            return new EffectiveValue(ReturnedValueType.UNDEFINED, "UNDEFINED");
        }

        String result = sourceValue.substring(startInt,endInt);
        return new EffectiveValue(ReturnedValueType.STRING,result);
    }
}