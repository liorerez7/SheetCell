package CoreParts;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.CommandManager;
import CoreParts.api.controller.InputHandler;

public class MenuHandler {

    private Engine engine;
    private CommandManager commandManager;
    private InputHandler inputHandler;

    public MenuHandler(Engine engine, CommandManager commandManager, InputHandler inputHandler) {
        this.engine = engine;
        this.commandManager = commandManager;
        this.inputHandler = inputHandler;
    }

    public void run() {

        while (true) {
            displayMenu();

            try {
                int commandId = inputHandler.getMenuOptionInput();
                Command command = commandManager.getCommand(commandId);
                command.execute();

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    void displayMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Display Sheet");
        System.out.println("2. Update Cell");
        System.out.println("3. Display Cell");
        System.out.println("4. Display Version");
    }
}
