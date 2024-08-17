package CoreParts.impl.controller;

import CoreParts.api.controller.Command;
import CoreParts.api.controller.CommandManager;
import CoreParts.api.controller.Controller;

public class ControllerImpl implements Controller {

    private final CommandManager commandManager;

    public ControllerImpl(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void handleRequest(int commandId) {
        commandManager.getCommand(commandId).execute();
    }

    @Override
    public void updateView() {

    }
}
