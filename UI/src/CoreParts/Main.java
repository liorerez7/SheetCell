package CoreParts;

import CoreParts.api.Engine;
import CoreParts.api.controller.CommandManager;
import CoreParts.api.controller.InputHandler;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.impl.controller.BeforeLoadCommandManager;
import CoreParts.impl.controller.commands.InputHandlerImpl;
import CoreParts.impl.controller.AfterLoadCommandManagerImpl;

public class Main {

    public static void main(String[] args) {
        // Create instances of the implementations
        Engine engine = new EngineImpl();
        CommandManager commandManager = new BeforeLoadCommandManager(engine);
        InputHandler inputHandler = new InputHandlerImpl(commandManager);
        // Create MenuHandler with the Engine and CommandManager
        MenuHandler menuHandler = new MenuHandler(engine,commandManager, inputHandler);
        menuHandler.run();
    }

}