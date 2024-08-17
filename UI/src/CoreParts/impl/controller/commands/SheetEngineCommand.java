package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;

public abstract class SheetEngineCommand implements Command {
    Engine engine;

    public SheetEngineCommand(Engine engine) {
        this.engine = engine;
    }
}
