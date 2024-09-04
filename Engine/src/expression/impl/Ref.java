package expression.impl;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCellViewOnly;
import CoreParts.smallParts.CellLocation;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.api.ExpressionVisitor;
import expression.impl.stringFunction.Str;
import expression.impl.variantImpl.EffectiveValueImpl;


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
            EffectiveValue value = new EffectiveValueImpl(ReturnedValueType.EMPTY,"");
            cell.setActualValue(value);

           // cell.setActualValue(sheet);
            return cell.getActualValue();
        }

        EffectiveValue res = sheet.getCell(location).getActualValue();
        EffectiveValue newRes = new EffectiveValueImpl(ReturnedValueType.UNKNOWN,res.getValue());
        return newRes;
    }
    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
    }
}
