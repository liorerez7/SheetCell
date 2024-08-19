package CoreParts.impl.controller.commands;

import CoreParts.api.Engine;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoLocation;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import expression.api.EffectiveValue;
import expression.api.Expression;

import java.util.Map;

public class DisplaySheet extends SheetEngineCommand {

    public DisplaySheet(Engine engine) {
        super(engine);
    }



    @Override
    public void execute() throws Exception {
        // Retrieve the sheet cell data from the engine
        DtoSheetCell sheetCell = engine.getSheetCell();

        // Get the dimensions and properties of the sheet
        int numberOfRows = sheetCell.getCurrentNumberOfRows();
        int numberOfCols = sheetCell.getCurrentNumberOfCols();
        int cellLength = sheetCell.getCellLength();
        int cellWidth = sheetCell.getCellWidth();
        String sheetName = sheetCell.getName();

        // Calculate the total width of the table
        int tableWidth = (numberOfCols * (cellWidth + 3)) + 1; // 3 for " | " and 1 for leading space

        // Print spacing and sheet name centered
        int nameLength = sheetName.length() + 7; // "Sheet: " and spaces
        int spacesBefore = (((tableWidth - nameLength) / 2)+ 5);
        int spacesAfter = tableWidth - nameLength - spacesBefore;
        System.out.println("\n" + " ".repeat(spacesBefore) + "Sheet: " + sheetName + " ".repeat(spacesAfter) + "\n");

        // Print the column headers
        System.out.print("    "); // Initial space for row headers
        for (char col = 'A'; col < 'A' + numberOfCols; col++) {
            // Print the column header with center alignment
            System.out.print(" | ");
            String header = String.format("%" + (((cellWidth - 1) / 2) + 1) + "s", col);
            String trailingSpaces = String.format("%" + ((cellWidth - header.length()) + 1) + "s", "");
            System.out.print(header + trailingSpaces);
        }
        System.out.println(" |"); // Closing pipe for the last column

        // Print the row data
        for (int row = 1; row <= numberOfRows; row++) {
            // Print row header
            System.out.print(String.format("%2d", row) + "  ");

            for (char col = 'A'; col < 'A' + numberOfCols; col++) {
                // Create the location for the cell
                DtoLocation dtoLocation = new DtoLocation(col, (char) ('0' + row));
                // Get the cell from the sheet
                DtoCell cell = engine.getRequestedCell(dtoLocation.getCellId());

                // Print the cell value or an empty string if the cell is null
                Object objectValue = (cell.getEffectiveValue() != null) ? cell.getEffectiveValue().evaluate().getValue() : "";
                String value = objectValue.toString();
                System.out.print(" | ");
                // Center the cell value within the cellWidth
                String formattedValue = String.format("%" + ((cellWidth - value.length()) / 2) + "s", value);
                String trailingSpacesValue = String.format("%" + (cellWidth - formattedValue.length() + 1) + "s", "");
                System.out.print(formattedValue + trailingSpacesValue);
            }
            System.out.print(" |"); // Closing pipe for the last column
            System.out.println();

            // Print vertical spacing for cells based on cellLength
            for (int i = 1; i < cellLength; i++) {
                System.out.print("    "); // Initial space for row headers
                for (int col = 0; col < numberOfCols; col++) {
                    System.out.print(" | ");
                    // Center an empty string within the cellWidth
                    String emptySpace = String.format("%" + ((cellWidth - 0) / 2) + "s", "");
                    String trailingSpacesEmpty = String.format("%" + (cellWidth - emptySpace.length() + 1) + "s", "");
                    System.out.print(emptySpace + trailingSpacesEmpty);
                }
                System.out.print(" |"); // Closing pipe for the last column
                System.out.println();
            }
        }
    }
}
