package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.Cell;
import CoreParts.api.UtilsUI.Displayer;
import CoreParts.api.controller.Command;
import CoreParts.api.controller.InputHandler;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.UtilisUI.MenuHandler;
import CoreParts.impl.UtilisUI.TerminalSheet;
import expression.ReturnedValueType;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UpdateCell extends SheetEngineCommand {

    public UpdateCell(Engine engine, MenuHandler menuHandler) {
        super(engine, menuHandler);
    }

    @Override
    public void execute() throws Exception {

        String cellId = inputHandler.getCellInput(0);
        if (cellId == null) return;
        DtoCell cell = engine.getRequestedCell(cellId);

        if(cell != null)
        {
            ReturnedValueType returnedValueType = cell.getEffectiveValue().getCellType();

            System.out.println("Cell id: " + cellId);
            System.out.println("Original value: " + cell.getOriginalValue());
            System.out.println("Effective value: " + cell.getEffectiveValue().getValue());

            switch (returnedValueType){
                case STRING:
                    break;
                case NUMERIC:
                    break;
                case BOOLEAN:
                    break;
            }
        }
        String newExpressionValue = inputHandler.getCommandInput(0);
        if (newExpressionValue == null) return;
        engine.updateCell(newExpressionValue, cellId.charAt(0),cellId.substring(1));
        Displayer displayer = new TerminalSheet();
        displayer.display(engine.getSheetCell());
    }
}