package expression.impl.boolFunction;

import expression.ReturnedValueType;
import expression.api.Expression;
import expression.impl.variantImpl.EffectiveValue;
import expression.impl.variantImpl.UnaryExpression;

public class Not extends UnaryExpression {

    public Not(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue evaluate) {
        if(evaluate.getCellType() != ReturnedValueType.BOOLEAN && evaluate.getCellType() != ReturnedValueType.UNKNOWN) {
                return new EffectiveValue(ReturnedValueType.BOOLEAN, "UNDIFINED");
        }
        else{
            try {
                boolean value = (boolean) evaluate.getValue();
                return new EffectiveValue(ReturnedValueType.BOOLEAN, !value);
            }catch (Exception e){
                return new EffectiveValue(ReturnedValueType.BOOLEAN, "UNDIFINED");
            }
        }
    }
}
