package CoreParts;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.CommandManager;
import CoreParts.api.controller.InputHandler;
import CoreParts.impl.UtilisUI.MenuHandler;
import CoreParts.impl.UtilisUI.MenuTypes;
import CoreParts.impl.controller.AfterLoadCommandManagerImpl;
import CoreParts.impl.UtilisUI.InputHandlerImpl;


public class engineUserInterface {

    private Engine engine;
    private CommandManager commandManager;
    private InputHandler inputHandler;
    private static final int ENGINE_ON = 1;
    private static final int ENGINE_OFF = 0;
    private int engineStatus;

    public engineUserInterface(Engine engine) {
        this.engine = engine;
        engineStatus = ENGINE_ON;
    }

    public void run() {

        MenuHandler menuHandler = new MenuHandler(engine, commandManager, inputHandler);

        while(engineStatus == ENGINE_ON){
            switch (menuHandler.getMenuStatus()) {
                case MenuTypes.FIRST_MENU:
                    menuHandler.handleFirstMenu();
                    break;
                case MenuTypes.SECOND_MENU:
                    menuHandler.handleSecondMenu();
                    break;
                case MenuTypes.EXIT_MENU:
                    engineStatus = ENGINE_OFF;
                    break;
            }
        }
    }
}

