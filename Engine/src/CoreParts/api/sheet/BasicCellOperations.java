package CoreParts.api.sheet;

import CoreParts.api.Cell;
import expression.api.Expression;
import smallParts.CellLocation;

public interface BasicCellOperations {
    Cell getCell(CellLocation location);
    void setCell(CellLocation location, Cell cell);
    void removeCell(CellLocation location);
    void applyCellUpdates(Cell targetCell, String newValue, Expression expression);
}
