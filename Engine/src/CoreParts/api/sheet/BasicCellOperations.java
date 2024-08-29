package CoreParts.api.sheet;

import CoreParts.api.Cell;
import CoreParts.smallParts.CellLocation;
import expression.api.Expression;

public interface BasicCellOperations {
    Cell getCell(CellLocation location);
    void setCell(CellLocation location, Cell cell);
    void removeCell(CellLocation location);
    boolean isCellPresent(CellLocation location);
    void applyCellUpdates(Cell targetCell, String newValue, Expression expression);
}
