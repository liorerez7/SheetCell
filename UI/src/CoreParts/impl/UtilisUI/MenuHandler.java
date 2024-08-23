package CoreParts.impl.UtilisUI;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.CommandManager;
import CoreParts.api.controller.InputHandler;
import CoreParts.impl.controller.AfterLoadCommandManagerImpl;
import CoreParts.impl.controller.BeforeLoadCommandManager;


public class MenuHandler {
    private final Engine engine;
    private CommandManager secondMenuCommandManager;
    private CommandManager firstMenuCommandManager;
    private InputHandler inputHandler;
    private MenuTypes menuStatus;


    public MenuHandler(Engine engine, CommandManager secondMenuCommandManager, InputHandler inputHandler) {
        this.engine = engine;
        this.firstMenuCommandManager = new BeforeLoadCommandManager(engine, this);
        this.secondMenuCommandManager = new AfterLoadCommandManagerImpl(engine, this);
        this.inputHandler = new InputHandlerImpl();
        menuStatus = MenuTypes.FIRST_MENU;
    }

    public void setMenuStatus(MenuTypes menuType) {
        this.menuStatus = menuType;
    }

    public MenuTypes getMenuStatus() {
        return menuStatus;
    }

    public void handleFirstMenu(){
        try {
            displayLoadingMenu();
            inputHandler.setCommandManager(firstMenuCommandManager);
            int commandId = inputHandler.getMenuOptionInput();
            Command command = firstMenuCommandManager.getCommand(commandId);
            command.execute();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void handleSecondMenu(){

        inputHandler.setCommandManager(secondMenuCommandManager);
        displayMenu();
        try {
            int commandId = inputHandler.getMenuOptionInput();
            Command command = secondMenuCommandManager.getCommand(commandId);
            command.execute();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayLoadingMenu() {
        System.out.println("\n=== Loading Menu ===");
        System.out.println("1. Load Sheet From XML");
        System.out.println("2. Load Saved Sheet");
        System.out.println("3. Exit");
        System.out.println("\nSelect an option (1-3): ");
    }

    private void displayMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Display Sheet");
        System.out.println("2. Update Cell");
        System.out.println("3. Display Cell");
        System.out.println("4. Display Version - Check a specific sheet version.");
        System.out.println("5.Load a file from XML");
        System.out.println("6.load an existing file that has been saved");
        System.out.println("7. Save Sheet");
        System.out.println("8. Exit");
        System.out.println("\nSelect an option (1-8): ");
    }

    private void setSecondMenuCommandManager(CommandManager secondMenuCommandManager) {

        this.secondMenuCommandManager = secondMenuCommandManager;
    }

    private void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;

    }

}
