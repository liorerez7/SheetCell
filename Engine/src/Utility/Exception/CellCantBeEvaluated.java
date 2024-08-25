package Utility.Exception;

import CoreParts.api.Cell;
import expression.ReturnedValueType;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.UnaryExpression;

public class CellCantBeEvaluated extends RuntimeException {

    private final Cell cell;
    private final String cellId;

    public CellCantBeEvaluated(Cell cell) {
        this.cell = cell;
        cellId = cell.getLocation().getCellId();

    }

    @Override
    public String getMessage() {

        return "Cell: " + cellId + " can't be evaluated because arguments are not from type";
    }
}
