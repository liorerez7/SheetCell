package expression.impl.numFunction;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCellViewOnly;
import CoreParts.smallParts.CellLocation;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.api.ExpressionVisitor;
import expression.impl.Range;
import expression.impl.Ref;
import expression.impl.stringFunction.Str;
import expression.impl.variantImpl.EffectiveValueImpl;

import java.util.HashSet;
import java.util.Set;

public class Sum implements Expression {

    Range range;

    public Sum(Range range) {

        this.range = range;
    }

    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) throws IllegalArgumentException {

        Set<Ref> refOfRange = range.getRangeRefs();
        double sum = 0.0;

        for(Ref ref : refOfRange){

            EffectiveValue value = ref.evaluate(sheet);
            try{
                sum += (double) value.getValue();
            }catch (Exception e){
            }
        }

        return new EffectiveValueImpl(ReturnedValueType.NUMERIC, sum);
    }


    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    public void accept(ExpressionVisitor visitor) {

    }
}
