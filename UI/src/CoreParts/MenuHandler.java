package CoreParts;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.CommandManager;
import CoreParts.api.controller.InputHandler;
import CoreParts.impl.controller.AfterLoadCommandManagerImpl;
import CoreParts.impl.controller.commands.InputHandlerImpl;

import javax.sound.midi.Soundbank;

public class MenuHandler {

    private Engine engine;
    private CommandManager commandManager;
    private InputHandler inputHandler;

    public MenuHandler(Engine engine, CommandManager commandManager, InputHandler inputHandler) {
        this.engine = engine;
        this.commandManager = commandManager;
        this.inputHandler = inputHandler;
    }
    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }
    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;

    }
    public void run() {
        try {
        displayLoadingMenu();
        int commandId = inputHandler.getMenuOptionInput();
        Command command = commandManager.getCommand(commandId);
        command.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO: THIS MENU SHOULD BE REPEATED
        }
        while (true) {
            setCommandManager(new AfterLoadCommandManagerImpl(engine));
            setInputHandler(new InputHandlerImpl(commandManager));
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
    void displayLoadingMenu() {
        System.out.println("\n=== Loading Menu ===");
        System.out.println("1. Load Sheet From XML");
        System.out.println("2. Load Saved Sheet");
        System.out.println("3. Exit");
        System.out.println("\nSelect an option (1-3): ");
    }
    void displayMenu() {
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
    }

