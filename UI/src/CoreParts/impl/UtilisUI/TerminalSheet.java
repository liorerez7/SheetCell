package CoreParts.impl.UtilisUI;

import CoreParts.api.UtilsUI.Displayer;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoLocation;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import expression.Operation;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;

public class TerminalSheet implements Displayer {

    @Override
    public void display(DtoSheetCell sheetCell) {
        displayWithVersion(sheetCell, sheetCell.getVersionNumber());
    }

    @Override
    public void displaySpecificVersion(DtoSheetCell sheetCell, int version) {
        displayWithVersion(sheetCell, version);
    }

    private void displayWithVersion(DtoSheetCell sheetCell, int versionNumber) {
        int numberOfRows = sheetCell.getCurrentNumberOfRows();
        int numberOfCols = sheetCell.getCurrentNumberOfCols();
        int cellLength = sheetCell.getCellLength();
        int cellWidth = sheetCell.getCellWidth();
        String sheetName = sheetCell.getName();

        // Calculate the total width of the table
        int tableWidth = calculateTableWidth(numberOfCols, cellWidth);

        // Print sheet name and version
        printSheetNameAndVersion(sheetName, versionNumber, tableWidth);

        // Print column headers
        printColumnHeaders(numberOfCols, cellWidth);

        // Print rows and cells
        printRows(sheetCell, numberOfRows, numberOfCols, cellLength, cellWidth);
    }

    private int calculateTableWidth(int numberOfCols, int cellWidth) {
        return (numberOfCols * (cellWidth + 3)) + 1; // 3 for " | " and 1 for leading space
    }

    private void printSheetNameAndVersion(String sheetName, int versionNumber, int tableWidth) {
        int nameLength = sheetName.length() + 7; // "Sheet: " and spaces
        int spacesBefore = (((tableWidth - nameLength) / 2) + 5);
        int spacesAfter = tableWidth - nameLength - spacesBefore;
        System.out.println("\n" + " ".repeat(spacesBefore) + "Sheet: " + sheetName + " ".repeat(spacesAfter) + "version: " + versionNumber + "\n");
    }

    private void printColumnHeaders(int numberOfCols, int cellWidth) {
        System.out.print("    "); // Initial space for row headers
        for (char col = 'A'; col < 'A' + numberOfCols; col++) {
            System.out.print(" | ");
            String header = String.format("%" + (((cellWidth - 1) / 2) + 1) + "s", col);
            String trailingSpaces = String.format("%" + ((cellWidth - header.length()) + 1) + "s", "");
            System.out.print(header + trailingSpaces);
        }
        System.out.println(" |"); // Closing pipe for the last column
    }

    private void printRows(DtoSheetCell sheetCell, int numberOfRows, int numberOfCols, int cellLength, int cellWidth) {
        for (int row = 1; row <= numberOfRows; row++) {
            System.out.print(String.format("%2d", row) + "  ");
            for (char col = 'A'; col < 'A' + numberOfCols; col++) {
                printCell(sheetCell, col, row, cellWidth);
            }
            System.out.print(" |");
            System.out.println();

            printVerticalSpacing(numberOfCols, cellLength, cellWidth);
        }
    }

    private void printCell(DtoSheetCell sheetCell, char col, int row, int cellWidth) {
        DtoLocation dtoLocation = new DtoLocation(col, (char) ('0' + row));
        EffectiveValue effectiveValue = sheetCell.getEffectiveValue(dtoLocation);
        Object objectValue = (effectiveValue != null) ? effectiveValue.getValue() : "";
        String value = objectValue.toString();
        System.out.print(" | ");
        String formattedValue = String.format("%" + ((cellWidth - value.length()) / 2) + "s", value);
        String trailingSpacesValue = String.format("%" + (cellWidth - formattedValue.length() + 1) + "s", "");
        System.out.print(formattedValue + trailingSpacesValue);
    }

    private void printVerticalSpacing(int numberOfCols, int cellLength, int cellWidth) {
        for (int i = 1; i < cellLength; i++) {
            System.out.print("    "); // Initial space for row headers
            for (int col = 0; col < numberOfCols; col++) {
                System.out.print(" | ");
                String emptySpace = String.format("%" + ((cellWidth - 0) / 2) + "s", "");
                String trailingSpacesEmpty = String.format("%" + (cellWidth - emptySpace.length() + 1) + "s", "");
                System.out.print(emptySpace + trailingSpacesEmpty);
            }
            System.out.print(" |");
            System.out.println();
        }
    }
}
