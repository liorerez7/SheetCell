package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;

import java.util.List;

public class DisplaySheet extends SheetEngineCommand {
    public DisplaySheet(Engine engine) {
        super(engine);
    }
    
    @Override
    public void execute(List<String> inputForCommand) {
        
    }

    @Override
    public List<String> getAttributList() {
        return null;
    }
}

