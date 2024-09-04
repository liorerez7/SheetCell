package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.UtilsUI.Displayer;
import CoreParts.impl.UtilisUI.MenuHandler;
import CoreParts.impl.UtilisUI.TerminalSheet;

import java.util.Scanner;

public class UpdateRange extends SheetEngineCommand {

    public UpdateRange(Engine engine, MenuHandler menuHandler) {
        super(engine, menuHandler);
    }

    @Override
    public void execute() throws Exception {

        System.out.println("Enter the name of the range: ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.next();

        System.out.println("Enter the cell range: ");
        String cellRange = scanner.next();



        engine.UpdateNewRange(name, cellRange);
        Displayer displayer = new TerminalSheet();
        displayer.display(engine.getSheetCell());
    }
}
