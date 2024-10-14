package engine.expression.impl.functions.unique;

import engine.core_parts.api.Cell;
import engine.core_parts.api.sheet.SheetCellViewOnly;

import engine.expression.ReturnedValueType;
import engine.expression.api.Expression;
import engine.expression.impl.functions.string.Str;
import dto.small_parts.CellLocation;
import dto.small_parts.EffectiveValue;


public class Ref implements Expression {
    CellLocation location;
    public Ref(CellLocation location) {
        this.location = location;
    }

    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) throws IllegalArgumentException {
        Cell cell = sheet.getCell(location);
        //if cell is empty he will have no effective value but he will exist be cause we used getCell
        if(cell.getEffectiveValue()==null)
        {
            cell.setEffectiveValue(new Str(""));
            EffectiveValue value = new EffectiveValue(ReturnedValueType.EMPTY,"");
            cell.setActualValue(value);

           // cell.setActualValue(sheet);
            return cell.getActualValue();
        }

        EffectiveValue res = sheet.getCell(location).getActualValue();
        EffectiveValue newRes = new EffectiveValue(ReturnedValueType.UNKNOWN,res.getValue());
        return newRes;
    }
    @Override
    public String getOperationSign() {
        return "";
    }

    public CellLocation getCellLocation() {
        return location;
    }
}
