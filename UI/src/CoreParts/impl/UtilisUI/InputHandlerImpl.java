package CoreParts.impl.UtilisUI;

import CoreParts.api.controller.CommandManager;
import CoreParts.api.controller.InputHandler;

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
        try {
            String input = scanner.nextLine().trim();

            // Check for non-numeric input
            if (!input.matches("\\d+")) {
                throw new NumberFormatException("Input is not a valid number.");
            }

            int option = Integer.parseInt(input);
            int numOfCommands = commandManager.getNumberOfCommands();

            // Check if the input is within the valid range
            if (option > 0 && option <= numOfCommands) {
                return option;
            } else {
                System.out.println("Invalid input: Enter a number between 1 and " + numOfCommands);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: Please enter a valid number.");
        }
        return getMenuOptionInput();
    }


    @Override
    public String getCellInput(int userTryCount) throws Exception {
        try {
            System.out.println("Enter Cell ID: for example A1 ( enter BACK to go to main menu):");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("BACK")) {
                return null;
            }

            // Convert first letter to uppercase and check for valid input
            if (input.matches("[a-zA-Z][1-9][0-9]*")) {
                // Convert the first letter to uppercase and return the formatted input
                input = input.substring(0, 1).toUpperCase() + input.substring(1);
                return input;
            } else {
                throw new IllegalArgumentException("Invalid input: input should be a letter followed by a number\nExample: A1 ");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return getCellInput(userTryCount);
        }
    }


    public int getVersionInput() throws Exception {
        while (true) {
            System.out.println("Enter the version number (positive integer) to display\n(enter 0 to go to main menu): ");
            try {
                // Read the next input
                int input = scanner.nextInt();
                if (input == 0) {
                    return 0;
                }
                // Validate if the input is positive
                if (input > 0) {
                    return input;
                } else {
                    System.out.println("Invalid input: Number must be positive.");
                }
            } catch (Exception e) {
                // Handle the case where the input is not an integer
                System.out.println("Invalid input: Please enter a valid integer.");
                scanner.next(); // Clear the invalid input
            }
        }
    }

    @Override
    public boolean getExitInput() throws Exception {
        System.out.println("Are you sure you want to exit? (y/n):");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("y") || confirmation.equals("yes")) {
            // Handle save confirmation
            return true;
        } else if (confirmation.equals("n") || confirmation.equals("no")) {
            // User does not want to exit, return to the menu
            System.out.println("Returning to the menu...");
            return false; // Do not exit
        } else {
            System.out.println("Invalid input: Please enter 'y' or 'n'.");
            return getExitInput(); // Retry exit confirmation
        }
    }

    @Override
    public boolean getSaveInput() throws Exception {
        System.out.println("Do you want to save the current sheet? (y/n):");
        String saveConfirmation = scanner.nextLine().trim().toLowerCase();

        if (saveConfirmation.equals("y") || saveConfirmation.equals("yes")) {
            return true;
        } else if (saveConfirmation.equals("n") || saveConfirmation.equals("no")) {
            return false;
        } else {
            System.out.println("Invalid input: Please enter 'y' or 'n'.");
            return getSaveInput();
        }
    }

    @Override
    public String getFilePathInput() throws Exception {
        while (true) {
            System.out.println("Enter the file path or BACK to go back to main menu: ");
            String filePath = scanner.nextLine().trim();
            if (filePath.equalsIgnoreCase("BACK"))
                return null;

            if (filePath.isEmpty()) {
                System.out.println("File path cannot be empty. Please enter a valid file path.");
                continue;
            }
            return filePath;
        }
    }

    @Override
    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    //TODO: add more syntax checks, like commas.
    @Override
    public String getCommandInput(int UserTryCount) throws Exception {

            System.out.println("Enter Expression Value: ( enter BACK to go to main menu):");
        try {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("BACK")) {
                return null;
            }
            if (input.startsWith("{") && !(input.endsWith("}"))) {
                throw new Exception("Invalid input: Input should not start with '{' and end with '}'");
            }

            return input;
        }
        catch (Exception e) {
            System.out.println("Invalid input: " + e.getMessage());
        }

        return getCommandInput(UserTryCount);
    }

}
