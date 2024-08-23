package CoreParts.impl.controller;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.CommandManager;
import CoreParts.impl.UtilisUI.MenuHandler;

import java.util.HashMap;
import java.util.Map;

public class BeforeLoadCommandManager implements CommandManager {
    private final Map<Integer, Command> commandMap = new HashMap<>();
    private final int numberOfCommands;

    public BeforeLoadCommandManager(Engine engine, MenuHandler menuHandler) {
        commandMap.put(1, new CoreParts.impl.controller.commands.LoadSheetFromXML(engine, menuHandler));
        commandMap.put(2, new CoreParts.impl.controller.commands.LoadSavedSheet(engine, menuHandler));
        commandMap.put(3, new CoreParts.impl.controller.commands.SaveSheet(engine, menuHandler));
        commandMap.put(4, new CoreParts.impl.controller.commands.ExitSheet(engine, menuHandler));
        numberOfCommands = commandMap.size();
    }

    @Override
    public Command getCommand(int commandId) {
        return commandMap.get(commandId);
    }

    @Override
    public int getNumberOfCommands() {
        return numberOfCommands;
    }
}
