package Utility.Exception;

import CoreParts.api.Cell;

public class CellCantBeEvaluatedException extends RuntimeException {

    private final String cellId;

    public CellCantBeEvaluatedException(Cell cell) {
        cellId = cell.getLocation().getCellId();

    }

    @Override
    public String getMessage() {

        String returnedMessage = "Cell: " + cellId + " can't be evaluated because arguments are not from the same type";
        return returnedMessage + "\nExample: '{PLUS,1,2}', without any spaces with the ','";
    }
}
