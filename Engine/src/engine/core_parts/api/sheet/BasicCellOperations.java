package engine.core_parts.api.sheet;

import engine.core_parts.api.Cell;
import engine.expression.api.Expression;
import dto.small_parts.CellLocation;

public interface BasicCellOperations {
    Cell getCell(CellLocation location);
    void setCell(CellLocation location, Cell cell);
    void removeCell(CellLocation location);
    void applyCellUpdates(Cell targetCell, String newValue, Expression expression);
}
