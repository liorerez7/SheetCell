package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.InputHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UpdateCell extends SheetEngineCommand {

    public UpdateCell(Engine engine) {
        super(engine);
        inputHandler = new InputHandlerImpl();
    }

    @Override
    public void execute(List<String> inputForCommand) throws Exception {

        String cellId = inputHandler.getCellInput();
        engine.DisplayMinimalCellDetails(input);

        String newExpressionValue = inputHandler.getCommandInput();

        engine.updateCell(newExpressionValue, cellId.charAt(0), cellId.charAt(1));
    }
}
