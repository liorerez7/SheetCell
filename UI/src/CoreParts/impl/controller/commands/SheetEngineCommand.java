package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.InputHandler;
import CoreParts.impl.UtilisUI.InputHandlerImpl;
import CoreParts.impl.UtilisUI.MenuHandler;

public abstract class SheetEngineCommand implements Command {

    protected Engine engine;
    protected InputHandler inputHandler;
    protected MenuHandler menuHandler;


    public SheetEngineCommand(Engine engine, MenuHandler menuHandler) {
        this.engine = engine;
        this.inputHandler = new InputHandlerImpl();
        this.menuHandler = menuHandler;
    }
}
