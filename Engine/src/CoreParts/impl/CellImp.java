package CoreParts.impl;

import CoreParts.interfaces.Cell;
import CoreParts.smallParts.CellLocation;
import expressions.Expression;

import java.util.List;

public class CellImp implements Cell
{
    private Expression effectiveValue;
    private String originalValue;
    private CellLocation location;
    private int latesetVersion;
    private List<Cell> affectedBy;
    private List<Cell> affectingOn;

    public CellImp(Expression effectiveValue,String originalValue) {
        this.effectiveValue = effectiveValue;
        this.originalValue = originalValue;
    }
    public CellImp(CellLocation location) {
        this.location = location;
        originalValue = "";
        effectiveValue = null;
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
