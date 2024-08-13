package CoreParts.interfaces;

import CoreParts.smallParts.CellLocation;
import expressions.Expression;

import java.util.Set;

public interface Cell {

    void setEffectiveValue(Expression effectiveValue);
    void setOriginalValue(String originalValue);
    Expression getEffectiveValue();
    String getOriginalValue();
    void addCellToAffectingOn(Cell cell);
    void addCellToAffectedBy(Cell cell);
    boolean isCellAffectingOn(Cell cell);
    Set<Cell> getAffectingOn();
    Set<Cell> getAffectedBy();
}
