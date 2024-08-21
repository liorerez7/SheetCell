package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;

public class LoadSheetFromXML extends SheetEngineCommand {

    public LoadSheetFromXML(Engine engine) {
        super(engine);
    }

    @Override
    public void execute() throws Exception {
        String path = inputHandler.getFilePathInput();
        if (path == null) return;

        engine.readSheetCellFromXML(path);
    }
}
