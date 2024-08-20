package CoreParts.impl.controller;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.CommandManager;

import java.util.HashMap;
import java.util.Map;

public class CommandManagerImpl implements CommandManager {

    private final Map<Integer, Command> commandMap = new HashMap<>();
    private final int numberOfCommands;

    public CommandManagerImpl(Engine engine) {
        commandMap.put(1, new CoreParts.impl.controller.commands.DisplaySheet(engine));
        commandMap.put(2, new CoreParts.impl.controller.commands.UpdateCell(engine));
        commandMap.put(3, new CoreParts.impl.controller.commands.DisplayCell(engine));
        commandMap.put(4, new CoreParts.impl.controller.commands.DisplayRequestedVersion(engine));
        numberOfCommands = commandMap.size();

    }

    public Command getCommand(int commandId) {
        return commandMap.get(commandId);
    }

    @Override
    public int getNumberOfCommands() {
        return numberOfCommands;
    }
}
