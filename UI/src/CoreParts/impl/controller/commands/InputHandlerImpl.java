package CoreParts.impl.controller.commands;

import CoreParts.api.controller.CommandManager;
import CoreParts.api.controller.InputHandler;
import CoreParts.impl.controller.CommandManagerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputHandlerImpl implements InputHandler {

    private Scanner scanner;
    private CommandManager commandManager;

    public InputHandlerImpl(CommandManager commandManager) {
        scanner = new Scanner(System.in);
        this.commandManager = commandManager;
    }
    public InputHandlerImpl() {
        scanner = new Scanner(System.in);
        this.commandManager = null;
    }

    @Override
    public int getMenuOptionInput() throws Exception {

        int numOfCommads = commandManager.getNumberOfCommands();
        int input = scanner.nextInt();                        // TODO: maybe change to nextLine and add an example of input
        if(input > 0 && input <= numOfCommads) {
            return input;
        } else {
            throw new Exception("Invalid input");
        }
    }

    @Override
    public String getCellInput() throws Exception {

        System.out.println("Enter Cell ID:  ");
        String input = scanner.nextLine();

        if (input.matches("[A-Z][1-9]+")) {
            return input;
        } else {
            throw new IllegalArgumentException("Invalid input: input should be a letter followed by a number\nExample: A1");
        }
    }
    public int getVersionInput() throws Exception {
        System.out.println("Enter the version number: ");
        int input = scanner.nextInt();
        if (input > 0) {
            return input;
        } else {
            throw new Exception("Invalid input enter a positive number");
        }
    }

    //TODO: add more syntax checks, like commas.
    @Override
    public String getCommandInput() throws Exception {

        System.out.println("Enter Expression Value: ");

        String input = scanner.nextLine();

        if (input.startsWith("{") && !(input.endsWith("}"))) {
            throw new Exception("Invalid input: Input should not start with '{' and end with '}'");
        }

        return input;
    }

}
