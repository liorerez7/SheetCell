package engine.expression.impl.functions.numeric;

import engine.core_parts.api.sheet.SheetCellViewOnly;
import dto.small_parts.ReturnedValueType;
import engine.expression.api.Expression;
import engine.expression.impl.Range;
import engine.expression.impl.functions.unique.Ref;
import engine.utilities.exception.AvgWithNoNumericCellsException;
import dto.small_parts.EffectiveValue;


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

        return new EffectiveValue(ReturnedValueType.NUMERIC, sum/numberOfNumericCells);
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    public Range getRange(){
        return range;
    }
}

