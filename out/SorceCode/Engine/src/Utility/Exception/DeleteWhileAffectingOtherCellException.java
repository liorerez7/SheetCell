package Utility.Exception;

import CoreParts.api.Cell;

public class DeleteWhileAffectingOtherCellException extends RuntimeException {


    private final Cell cell;


    public DeleteWhileAffectingOtherCellException(Cell cell) {
        this.cell = cell;
    }

    @Override
    public String getMessage() {

        return "Cell: " + cell.getLocation().getCellId() + " cant be deleted as it is being referenced by other cell.";
    }
}
