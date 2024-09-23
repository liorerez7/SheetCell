package Utility.Exception;

import CoreParts.api.Cell;

public class RefToUnSetCellException extends RuntimeException {

    private final Cell cell;


    public RefToUnSetCellException(Cell cell) {
        this.cell = cell;
    }

    @Override
    public String getMessage() {

        return "Cell: " + cell.getLocation().getCellId() + " is does not exist.";
    }
}
