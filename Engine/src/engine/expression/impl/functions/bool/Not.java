package engine.expression.impl.functions.bool;

import engine.expression.ReturnedValueType;
import engine.expression.api.Expression;

import engine.expression.impl.functions.type.UnaryExpression;
import dto.small_parts.EffectiveValue;

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
