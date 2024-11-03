package engine.expression.impl.functions.unique;

import dto.small_parts.ReturnedValueType;
import engine.expression.api.Expression;

import engine.expression.impl.functions.type.TernaryExpression;
import dto.small_parts.EffectiveValue;

public class If extends TernaryExpression {

    public If(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue evaluate, EffectiveValue evaluate2, EffectiveValue evaluate3) {

        if(evaluate.getCellType() != ReturnedValueType.BOOLEAN && evaluate.getCellType() != ReturnedValueType.UNDEFINED
                && evaluate.getCellType() != ReturnedValueType.UNKNOWN){
            throw new IllegalArgumentException();
        }

        try {
            boolean value = (boolean) evaluate.getValue();
            if(value){
                return evaluate2;
            }
            else{
                return evaluate3;
            }
        }catch (Exception e){
            return new EffectiveValue(ReturnedValueType.BOOLEAN, "UNDIFINED");
        }
    }

    @Override
    public String getOperationSign() {
        return "if";
    }

}
