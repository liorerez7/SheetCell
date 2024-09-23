package CoreParts.api.controller;

public interface CommandManager {
    Command getCommand(int commandId);
    int getNumberOfCommands();
}
