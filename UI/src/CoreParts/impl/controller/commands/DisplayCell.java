package CoreParts.impl.controller.commands;

import CoreParts.api.Cell;
import CoreParts.api.Engine;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoLocation;

public class DisplayCell extends SheetEngineCommand {

    public DisplayCell(Engine engine) {
        super(engine);
    }

    @Override
    public void execute() throws Exception {

        String cellId = inputHandler.getCellInput(0);
        if (cellId == null) return;
        DtoCell cell = null;
        try {
            cell = engine.getRequestedCell(cellId, false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            execute();
        }

        System.out.println("Cell id: " + cellId);
        System.out.println("Original value: " + cell.getOriginalValue());
        System.out.println("Effective value: " + cell.getEffectiveValue().getValue());

        // Print affected cells
        printAffectedCells(cell);

        // Print affecting cells
        printAffectingCells(cell);
    }

    private void printAffectedCells(DtoCell cell) {
        if (cell.getAffectedBy().isEmpty()) {
            System.out.println("This cell is not affected by any cell");
        } else {
            System.out.println("List of cells that affects this cell:");
            for (DtoLocation dtoLocationCell : cell.getAffectedBy()) {
                System.out.println("Cell id: " + dtoLocationCell.getCellId());
            }
        }
    }

    private void printAffectingCells(DtoCell cell) {
        if (cell.getAffectingOn().isEmpty()) {
            System.out.println("This is not affecting any cell");
        } else {
            System.out.println("List of Cells that this cell affecting on:");
            for (DtoLocation dtoLocationCell : cell.getAffectingOn()) {
                System.out.println("Cell id: " + dtoLocationCell.getCellId());
            }
        }
    }
}