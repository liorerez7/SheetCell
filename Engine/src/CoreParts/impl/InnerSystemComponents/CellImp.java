package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCellViewOnly;
import CoreParts.smallParts.CellLocation;
import Utility.Exception.AvgWithNoNumericCellsException;
import Utility.Exception.CellCantBeEvaluatedException;
import expression.api.Expression;
import expression.impl.variantImpl.EffectiveValue;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CellImp implements Cell ,Serializable
{
    private static final long serialVersionUID = 1L; // Add serialVersionUID
    private Expression effectiveValue;
    private EffectiveValue actualValue = new EffectiveValue(null,null);
    private String originalValue;
    private CellLocation location;
    private int latestVersion;
    private Set<Cell> affectedBy = new HashSet<>();
    private Set<Cell> affectingOn = new HashSet<>();

    public CellImp(Expression effectiveValue,String originalValue) {
    this.effectiveValue = effectiveValue;
    this.originalValue = originalValue;
}

public void setActualValue(SheetCellViewOnly sheet) {
    try{
       actualValue = effectiveValue.evaluate(sheet);
    }
    catch(AvgWithNoNumericCellsException e){
        throw e;
    }
    catch (Exception e){
        throw new CellCantBeEvaluatedException(this);
    }
}


public void setActualValue(EffectiveValue effectiveValue) {
    this.actualValue = effectiveValue;
}

    @Override
    public EffectiveValue getActualValue() {
        return actualValue;
    }

    public CellImp(CellLocation location) {
        this.location = location;
        originalValue = "";
        effectiveValue = null;
    }

    public CellImp(CellLocation location, String originalValue) {
        this.location = location;
        this.originalValue = originalValue;
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
    public void setEffectingOn(Set<Cell> cells) {
        affectingOn = cells;
    }

    @Override
    public void setAffectedBy(Set<Cell> cells) {
        affectedBy = cells;
    }

    @Override
    public void addCellToAffectedBy(Cell cell) {
        affectedBy.add(cell);
    }

    @Override
    public void removeCellFromAffectingOn(Cell cell) {
        affectingOn.remove(cell);
    }

    @Override
    public void removeCellFromAffectedBy(Cell cell) {
        affectedBy.remove(cell);
    }

    @Override
    public CellLocation getLocation() {
        return location;
    }

    @Override
    public void updateVersion(int latestVersion) {
        this.latestVersion = latestVersion;
    }
    @Override
    public int getLatestVersion() {
        return latestVersion;
    }


}
