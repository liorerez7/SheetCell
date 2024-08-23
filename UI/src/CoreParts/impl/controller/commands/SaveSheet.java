package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.impl.UtilisUI.MenuHandler;

public class SaveSheet extends SheetEngineCommand {

    public SaveSheet(Engine engine, MenuHandler menuHandler) {
        super(engine, menuHandler);
    }

    @Override
    public void execute() throws Exception {
        boolean save = inputHandler.getSaveInput();
        String path = inputHandler.getFilePathInput();
        try {
            if (save) {
                engine.save(path);
                return;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
