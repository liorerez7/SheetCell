package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;

public class SaveSheet extends SheetEngineCommand {

    public SaveSheet(Engine engine) {
        super(engine);
    }

    @Override
    public void execute() throws Exception {
        boolean save = inputHandler.getSaveInput();
        String path = inputHandler.getFilePathInput();
        try {
            if (save) {
                engine.save(path);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
