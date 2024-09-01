package CoreParts.impl.controller.commands;

import CoreParts.api.Cell;
import CoreParts.api.Engine;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.UtilisUI.MenuHandler;
import CoreParts.smallParts.CellLocation;

public class DisplayCell extends SheetEngineCommand {

    public DisplayCell(Engine engine, MenuHandler menuHandler) {
        super(engine, menuHandler);
    }

    @Override
    public void execute() throws Exception {

        boolean inputIsValid = true;

        while (inputIsValid) {

            String cellId = inputHandler.getCellInput(0);

            // Check if the user wants to return to the main menu
            if (cellId == null) {
                return;
            }

            // Get the requested cell
            DtoCell cell = engine.getRequestedCell(cellId);

            // Handle invalid cell
            if (cell == null) {
                System.out.println("Can't display a cell that doesn't exist. Use the update cell method.");
            } else {
                // If a valid cell is found, display its information
                System.out.println("Cell id: " + cellId);
                System.out.println("Original value: " + cell.getOriginalValue());
                System.out.println("Effective value: " + cell.getEffectiveValue().getValue());

                // Print affected and affecting cells
                printAffectedCells(cell);
                printAffectingCells(cell);

                inputIsValid = false;  // Valid cell, exit the loop
            }
        }
    }

    private void printAffectedCells(DtoCell cell) {
        if (cell.getAffectedBy().isEmpty()) {
            System.out.println("This cell is not affected by any cell");
        } else {
            System.out.println("List of cells that affects this cell:");
            for (CellLocation cellLocation : cell.getAffectedBy()) {
                System.out.println("Cell id: " + cellLocation.getCellId());
            }
        }
    }

    private void printAffectingCells(DtoCell cell) {
        if (cell.getAffectingOn().isEmpty()) {
            System.out.println("This is not affecting any cell");
        } else {
            System.out.println("List of Cells that this cell affecting on:");
            for (CellLocation cellLocation : cell.getAffectingOn()) {
                System.out.println("Cell id: " + cellLocation.getCellId());
            }
        }
    }
}