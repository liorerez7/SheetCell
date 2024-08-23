package CoreParts;

import CoreParts.api.Engine;
import CoreParts.api.controller.CommandManager;
import CoreParts.api.controller.InputHandler;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.impl.controller.BeforeLoadCommandManager;
import CoreParts.impl.UtilisUI.InputHandlerImpl;

public class Main {

    public static void main(String[] args) {
        // Create instances of the implementations
        Engine engine = new EngineImpl();
        engineUserInterface engineUserInterface = new engineUserInterface(engine);
        engineUserInterface.run();
    }
}