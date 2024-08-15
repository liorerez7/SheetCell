package CoreParts.impl;

import CoreParts.api.Cell;
import CoreParts.smallParts.CellLocation;
import expression.api.Expression;

import java.util.HashSet;
import java.util.Set;

public class CellImp implements Cell
{
    private Expression effectiveValue;
    private String originalValue;
    private CellLocation location;
    private int latesetVersion;
    private Set<Cell> affectedBy = new HashSet<>();
    private Set<Cell> affectingOn = new HashSet<>();

    public CellImp(Expression effectiveValue,String originalValue) {
    this.effectiveValue = effectiveValue;
    this.originalValue = originalValue;
}
    public CellImp(CellLocation location) {
        this.location = location;
        originalValue = "";
        effectiveValue = null;
    }

    public boolean isCellAffectedBy(Cell cell) {
        return affectedBy.contains(cell);
    }

    public boolean isCellAffectingOn(Cell cell) {
        return affectingOn.contains(cell);
    }

    @Override
    public Set<Cell> getAffectingOn() {
        return affectingOn;
    }

    @Override
    public Set<Cell> getAffectedBy() {
        return affectedBy;
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

    @Override
    public void addCellToAffectingOn(Cell cell) {
        affectingOn.add(cell);
    }

    @Override
    public void addCellToAffectedBy(Cell cell) {
        affectedBy.add(cell);
    }



}
