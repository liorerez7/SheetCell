package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.Cell;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.InputHandler;
import CoreParts.impl.DtoComponents.DtoCell;
import expression.ReturnedValueType;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UpdateCell extends SheetEngineCommand {

    public UpdateCell(Engine engine) {
        super(engine);
    }

    @Override
    public void execute() throws Exception {

        String cellId = inputHandler.getCellInput();

        DtoCell cell = engine.getRequestedCell(cellId);

        if(cell.getEffectiveValue() != null)
        {
            ReturnedValueType returnedValueType = cell.getEffectiveValue().evaluate().getCellType();

            System.out.println("Cell id: " + cellId);
            System.out.println("Original value: " + cell.getOriginalValue());
            System.out.println("Effective value: " + cell.getEffectiveValue().evaluate().getValue());

            switch (returnedValueType){
                case STRING:
                    break;
                case NUMERIC:
                    break;
                case BOOLEAN:
                    break;
            }
        }

        String newExpressionValue = inputHandler.getCommandInput();

        engine.updateCell(newExpressionValue, cellId.charAt(0), cellId.charAt(1));
    }
}
