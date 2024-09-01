package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.impl.UtilisUI.MenuHandler;
import CoreParts.impl.UtilisUI.MenuTypes;

public class LoadSavedSheet extends SheetEngineCommand {
    public LoadSavedSheet(Engine engine, MenuHandler menuHandler) {
        super(engine, menuHandler);
    }

    @Override
    public void execute() throws Exception {
        String path = inputHandler.getFilePathInput();
        if (path == null) {
            menuHandler.setMenuStatus(MenuTypes.FIRST_MENU);
        }
        engine.load(path);
        System.out.println("Sheet loaded successfully");
        menuHandler.setMenuStatus(MenuTypes.SECOND_MENU);
    }
}
