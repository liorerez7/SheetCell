package expression.impl;

import CoreParts.api.sheet.SheetCellViewOnly;
import CoreParts.smallParts.CellLocation;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.api.ExpressionVisitor;
import expression.impl.variantImpl.EffectiveValueImpl;


public class Ref implements Expression {
    CellLocation location;
    public Ref(CellLocation location) {
        this.location = location;
    }
    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) throws IllegalArgumentException {
        if(!sheet.isCellPresent(location)) {
            return new EffectiveValueImpl(ReturnedValueType.EMPTY,"");
        }
        EffectiveValue res = sheet.getCell(location).getActualValue();
        res.setType(ReturnedValueType.UNKNOWN);
        return res;
    }
    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    public void accept(ExpressionVisitor visitor) {

    }
}
