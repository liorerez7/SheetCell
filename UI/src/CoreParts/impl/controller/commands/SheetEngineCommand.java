package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.InputHandler;

public abstract class SheetEngineCommand implements Command {

    private Engine engine;
    private InputHandler inputHandler;

    public SheetEngineCommand(Engine engine) {
        this.engine = engine;
        this.inputHandler = new InputHandlerImpl();
    }
}
