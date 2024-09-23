package Utility.Exception;

import CoreParts.api.Cell;
import expression.ReturnedValueType;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.UnaryExpression;

public class CellCantBeEvaluated extends RuntimeException {

    private final String cellId;

    public CellCantBeEvaluated(Cell cell) {
        cellId = cell.getLocation().getCellId();

    }

    @Override
    public String getMessage() {

        String returnedMessage = "Cell: " + cellId + " can't be evaluated because arguments are not from the same type";
        return returnedMessage + "\nExample: '{PLUS,1,2}', without any spaces with the ','";
    }
}
