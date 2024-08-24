package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.impl.UtilisUI.MenuHandler;
import CoreParts.impl.UtilisUI.MenuTypes;

public class SaveSheet extends SheetEngineCommand {

    public SaveSheet(Engine engine, MenuHandler menuHandler) {
        super(engine, menuHandler);
    }

    @Override
    public void execute() throws Exception {

        boolean save = inputHandler.getSaveInput();

        if(save) {
            try {
                String path = inputHandler.getFilePathInput();

                System.out.println("Sheet saved successfully");
                engine.save(path);

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        else{
            menuHandler.setMenuStatus(MenuTypes.EXIT_MENU);
        }
    }
}
