package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.InputHandler;

public abstract class SheetEngineCommand implements Command {

    protected Engine engine;
    protected InputHandler inputHandler;

    public SheetEngineCommand(Engine engine) {
        this.engine = engine;
        this.inputHandler = new InputHandlerImpl();
    }
}
