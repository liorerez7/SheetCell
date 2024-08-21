package CoreParts.impl.controller;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.CommandManager;
import CoreParts.impl.controller.commands.LoadSheetFromXML;

import java.util.HashMap;
import java.util.Map;

public class AfterLoadCommandManagerImpl implements CommandManager {

    private final Map<Integer, Command> commandMap = new HashMap<>();
    private final int numberOfCommands;

    public AfterLoadCommandManagerImpl(Engine engine) {
        commandMap.put(1, new CoreParts.impl.controller.commands.DisplaySheet(engine));
        commandMap.put(2, new CoreParts.impl.controller.commands.UpdateCell(engine));
        commandMap.put(3, new CoreParts.impl.controller.commands.DisplayCell(engine));
        commandMap.put(4, new CoreParts.impl.controller.commands.DisplayRequestedVersion(engine));
        commandMap.put(5, new LoadSheetFromXML(engine));
        commandMap.put(6, new CoreParts.impl.controller.commands.LoadSavedSheet(engine));
        commandMap.put(7, new CoreParts.impl.controller.commands.SaveSheet(engine));
        commandMap.put(8, new CoreParts.impl.controller.commands.ExitSheet(engine));
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
