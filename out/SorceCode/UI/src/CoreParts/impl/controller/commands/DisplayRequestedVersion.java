package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.UtilsUI.Displayer;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.impl.UtilisUI.MenuHandler;
import CoreParts.impl.UtilisUI.TerminalSheet;

public class DisplayRequestedVersion extends SheetEngineCommand {
    public DisplayRequestedVersion(Engine engine, MenuHandler menuHandler) {
        super(engine, menuHandler);
    }

    @Override
    public void execute() throws Exception {
        System.out.println("lastest version: " + engine.getSheetCell().getVersionNumber());
        int requestedVersion = inputHandler.getVersionInput();
        if (requestedVersion == 0) {
            return;
        }
        DtoSheetCell sheetCell = null;
        try {
            sheetCell = engine.getSheetCell(requestedVersion);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            execute();
        }
        Displayer displayer = new TerminalSheet();

        displayer.displaySpecificVersion(sheetCell, requestedVersion);
    }
}
