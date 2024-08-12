package CoreParts.impl;

import CoreParts.interfaces.Cell;
import expressions.Expression;

public class CellImp implements Cell
{
    Expression effectiveValue;
    String originalValue;

    public CellImp(Expression effectiveValue,  String originalValue) {
        this.effectiveValue = effectiveValue;
        this.originalValue = originalValue;
    }

    public void setEffectiveValue(Expression effectiveValue) {
        this.effectiveValue = effectiveValue;
    }

    public void setOriginalValue(String originalValue){
        this.originalValue = originalValue;
    }

    public Expression getEffectiveValue() {
        return effectiveValue;
    }

    public String getOriginalValue() {
        return originalValue;
    }

}
