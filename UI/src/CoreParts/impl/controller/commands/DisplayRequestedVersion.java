package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.api.UtilsUI.Displayer;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.impl.UtilisUI.MenuHandler;
import CoreParts.impl.UtilisUI.TerminalSheet;
import CoreParts.smallParts.CellLocation;
import expression.api.EffectiveValue;

import java.util.Map;

public class DisplayRequestedVersion extends SheetEngineCommand {
    public DisplayRequestedVersion(Engine engine, MenuHandler menuHandler) {
        super(engine, menuHandler);
    }

    @Override
    public void execute() throws Exception {
        System.out.println("lastest version: " + engine.getSheetCell().getLatestVersion() + "\n");

        Map<Integer, Map<CellLocation, EffectiveValue>> mapOfMaps = engine.getSheetCell().getVersionToCellsChanges();
        printVersionData(mapOfMaps);

        int requestedVersion = inputHandler.getVersionInput();
        if (requestedVersion == 0) {
            return;
        }
        DtoSheetCell sheetCell = null;
        try {
            sheetCell = engine.getSheetCell(requestedVersion);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            execute();
        }
        Displayer displayer = new TerminalSheet();

        displayer.displaySpecificVersion(sheetCell, requestedVersion);
    }

    private void printVersionData(Map<Integer, Map<CellLocation, EffectiveValue>> mapOfMaps) {
        System.out.printf("\n%-10s | %-60s | %-5s%n", "Version", "Cell Locations", "Count\n");
        for (Map.Entry<Integer, Map<CellLocation, EffectiveValue>> entry : mapOfMaps.entrySet()) {
            // Extract version number and cell map
            int versionNumber = entry.getKey();
            Map<CellLocation, EffectiveValue> cellMap = entry.getValue();

            // Prepare string for cell locations
            StringBuilder cellLocations = new StringBuilder();
            for (CellLocation cellLocation : cellMap.keySet()) {
                cellLocations.append(cellLocation.getCellId()).append(" ");
            }

            // Print the version number, cell locations, and the count of cells
            System.out.printf("%-10d | %-60s | %-5d%n", versionNumber, cellLocations.toString().trim(), cellMap.size());
        }
    }
}
