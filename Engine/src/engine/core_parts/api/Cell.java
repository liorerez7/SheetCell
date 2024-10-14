package engine.core_parts.api;

import engine.core_parts.api.sheet.SheetCellViewOnly;

import engine.expression.api.Expression;
import dto.small_parts.CellLocation;
import dto.small_parts.EffectiveValue;

import java.util.Set;

public interface Cell {

    void setEffectiveValue(Expression effectiveValue);
    void setOriginalValue(String originalValue);
    Expression getEffectiveValue();
    String getOriginalValue();
    void setEffectingOn(Set<Cell> cells);
    void setAffectedBy(Set<Cell> cells);
    Set<Cell> getAffectingOn();
    Set<Cell> getAffectedBy();
    CellLocation getLocation();
    void updateVersion(int latestVersion);
    int getLatestVersion();
    void setActualValue(SheetCellViewOnly sheet);
    void setActualValue(EffectiveValue value);
    EffectiveValue getActualValue();
}
