package expression.impl.numFunction;

import CoreParts.api.sheet.SheetCellViewOnly;
import Utility.Exception.AvgWithNoNumericCellsException;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.impl.Range;
import expression.impl.Ref;
import expression.impl.variantImpl.EffectiveValueImpl;

import java.util.Set;

public class Average implements Expression {

    Range range;
    String cellId;

    public Average(Range range, String cellId) {
        this.range = range;
        this.cellId = cellId;
    }

    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) throws IllegalArgumentException{
        Set<Ref> refOfRange = range.getRangeRefs();

        double sum = 0.0;
        double numberOfNumericCells = 0;

        for(Ref ref : refOfRange){

            EffectiveValue value = ref.evaluate(sheet);
            try{
                sum += (double) value.getValue();
                numberOfNumericCells++;
            }catch (Exception e){
            }
        }

        if(numberOfNumericCells == 0){
            throw new AvgWithNoNumericCellsException(cellId);
        }

        return new EffectiveValueImpl(ReturnedValueType.NUMERIC, sum/numberOfNumericCells);
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    public Range getRange(){
        return range;
    }
}

