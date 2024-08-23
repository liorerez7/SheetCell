package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.SheetCell;
import CoreParts.api.UtilsUI.Displayer;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoLocation;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.impl.UtilisUI.MenuHandler;
import CoreParts.impl.UtilisUI.TerminalSheet;
import expression.api.EffectiveValue;
import expression.api.Expression;

import java.util.Map;

public class DisplaySheet extends SheetEngineCommand {

    public DisplaySheet(Engine engine, MenuHandler menuHandler) {
        super(engine, menuHandler);
    }



    @Override
    public void execute() throws Exception {
        // Retrieve the sheet cell data from the engine
        DtoSheetCell sheetCell = engine.getSheetCell();
        Displayer terminalSheet = new TerminalSheet();
        terminalSheet.display(sheetCell);
    }
}
