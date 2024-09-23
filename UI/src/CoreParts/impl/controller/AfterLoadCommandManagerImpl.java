package CoreParts.impl.controller;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.CommandManager;
import CoreParts.impl.UtilisUI.MenuHandler;
import CoreParts.impl.controller.commands.LoadSheetFromXML;

import java.util.HashMap;
import java.util.Map;

public class AfterLoadCommandManagerImpl implements CommandManager {

    private final Map<Integer, Command> commandMap = new HashMap<>();
    private final int numberOfCommands;

    public AfterLoadCommandManagerImpl(Engine engine, MenuHandler menuHandler) {
        commandMap.put(1, new CoreParts.impl.controller.commands.DisplaySheet(engine, menuHandler));
        commandMap.put(2, new CoreParts.impl.controller.commands.UpdateCell(engine, menuHandler));
        commandMap.put(3, new CoreParts.impl.controller.commands.DisplayCell(engine, menuHandler));
        commandMap.put(4, new CoreParts.impl.controller.commands.DisplayRequestedVersion(engine, menuHandler));
        commandMap.put(5, new LoadSheetFromXML(engine, menuHandler));
        commandMap.put(6, new CoreParts.impl.controller.commands.LoadSavedSheet(engine, menuHandler));
        commandMap.put(7, new CoreParts.impl.controller.commands.SaveSheet(engine, menuHandler));
        commandMap.put(8, new CoreParts.impl.controller.commands.ExitSheet(engine, menuHandler));
        commandMap.put(9, new CoreParts.impl.controller.commands.UpdateRange(engine, menuHandler));
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
