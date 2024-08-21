package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.controller.InputHandler;

public class ExitSheet extends SheetEngineCommand{

    public ExitSheet(Engine engine) {
        super(engine);
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
                if (path == null) return;
                engine.save(path);
            }
            engine.exit();
        }
        }
    }


