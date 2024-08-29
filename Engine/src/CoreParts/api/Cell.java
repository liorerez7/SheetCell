package CoreParts.api;

import CoreParts.api.sheet.SheetCellViewOnly;
import CoreParts.smallParts.CellLocation;
import expression.api.EffectiveValue;
import expression.api.Expression;

import java.util.Set;

public interface Cell {

    void setEffectiveValue(Expression effectiveValue);
    void setOriginalValue(String originalValue);
    Expression getEffectiveValue();
    String getOriginalValue();
    void addCellToAffectingOn(Cell cell);
    void setEffectingOn(Set<Cell> cells);
    void setAffectedBy(Set<Cell> cells);
    void addCellToAffectedBy(Cell cell);
    void removeCellFromAffectingOn(Cell cell);
    void removeCellFromAffectedBy(Cell cell);
    boolean isCellAffectingOn(Cell cell);
    boolean isCellAffectedBy(Cell cell) ;
    Set<Cell> getAffectingOn();
    Set<Cell> getAffectedBy();
    CellLocation getLocation();
    void updateVersion(int latestVersion);
    int getLatestVersion();
    void setActualValue(SheetCellViewOnly sheet);
    EffectiveValue getActualValue();
}
