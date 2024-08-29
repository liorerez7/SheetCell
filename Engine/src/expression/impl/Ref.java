package expression.impl;

import CoreParts.api.sheet.SheetCellViewOnly;
import CoreParts.smallParts.CellLocation;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.api.ExpressionVisitor;


public class Ref implements Expression {
    CellLocation location;
    public Ref(CellLocation location) {
        this.location = location;
    }
    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) throws IllegalArgumentException {
        EffectiveValue res = sheet.getCell(location).getActualValue();
        //res.setType(ReturnedValueType.UNKNOWN);
        return sheet.getCell(location).getActualValue();
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    public void accept(ExpressionVisitor visitor) {

    }
}
