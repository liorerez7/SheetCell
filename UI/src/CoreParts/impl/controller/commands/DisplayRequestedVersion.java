package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.UtilsUI.Displayer;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.impl.UtilisUI.TerminalSheet;

public class DisplayRequestedVersion extends SheetEngineCommand {
    public DisplayRequestedVersion(Engine engine) {
        super(engine);
    }

    @Override
    public void execute() throws Exception {

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

        displayer.display(sheetCell);
    }
}
