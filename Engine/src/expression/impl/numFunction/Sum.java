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

    Set<CellLocation> cellLocations;
    Range range;
    Set<Ref> refOfRange = new HashSet<>();


    public Sum(Range range) {

        this.range = range;
        this.cellLocations = range.getRangeOfCellLocation();

        for(CellLocation cellLocation : cellLocations){
            refOfRange.add(new Ref(cellLocation));
        }

        //range.AddObserver(this);
    }

    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) throws IllegalArgumentException {

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
