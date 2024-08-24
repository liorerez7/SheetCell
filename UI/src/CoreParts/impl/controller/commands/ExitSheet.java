package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.controller.InputHandler;
import CoreParts.impl.UtilisUI.MenuHandler;
import CoreParts.impl.UtilisUI.MenuTypes;

public class ExitSheet extends SheetEngineCommand{

    public ExitSheet(Engine engine, MenuHandler menuHandler) {
        super(engine, menuHandler);
    }

    @Override
    public void execute() throws Exception {
        boolean exit = inputHandler.getExitInput();
        boolean save = false;
        String path = "";

        if (exit) {

            save = inputHandler.getSaveInput();

            if (save) {
                path = inputHandler.getFilePathInput();

                if (path == null){
                    menuHandler.setMenuStatus(MenuTypes.EXIT_MENU);
                }

                System.out.println("Sheet saved successfully");
                engine.save(path);
            }
            menuHandler.setMenuStatus(MenuTypes.EXIT_MENU);
        }
        else{
            menuHandler.setMenuStatus(MenuTypes.SECOND_MENU);
        }
    }
}
