package Utility.Exception;

import CoreParts.api.Cell;

public class RefToUnSetCell extends RuntimeException {

    private final Cell cell;


    public RefToUnSetCell(Cell cell) {
        this.cell = cell;
    }

    @Override
    public String getMessage() {

        return "Cell: " + cell.getLocation().getCellId() + " is does not exist.";
    }
}
