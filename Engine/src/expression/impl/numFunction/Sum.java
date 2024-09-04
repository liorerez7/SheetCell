package expression.impl.numFunction;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCellViewOnly;
import CoreParts.smallParts.CellLocation;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;
import expression.api.Expression;
import expression.api.ExpressionVisitor;
import expression.impl.stringFunction.Str;
import expression.impl.variantImpl.EffectiveValueImpl;

import java.util.Set;

public class Sum implements Expression {

    Set<CellLocation> cellLocations;

    public Sum(Set<CellLocation> cellLocations) {

        this.cellLocations = cellLocations;

    }

    @Override
    public EffectiveValue evaluate(SheetCellViewOnly sheet) throws IllegalArgumentException {

        double sum = 0.0;

        for (CellLocation cellLocation : cellLocations) {

            Cell cell = sheet.getCell(cellLocation);

            if (cell != null && cell.getEffectiveValue() != null) {

                EffectiveValue effectiveValue = cell.getEffectiveValue().evaluate(sheet);
                if (effectiveValue.getCellType() == ReturnedValueType.NUMERIC) {
                    sum += (Double) effectiveValue.getValue();
                }
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
