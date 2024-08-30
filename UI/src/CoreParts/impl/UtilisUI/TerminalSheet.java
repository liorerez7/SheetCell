//package CoreParts.impl.UtilisUI;
//
//import CoreParts.api.UtilsUI.Displayer;
//import CoreParts.impl.DtoComponents.DtoCell;
//import CoreParts.impl.DtoComponents.DtoSheetCell;
//import CoreParts.smallParts.CellLocation;
//import expression.Operation;
//import expression.ReturnedValueType;
//import expression.api.EffectiveValue;
//
//public class TerminalSheet implements Displayer {
//
//    @Override
//    public void display(DtoSheetCell sheetCell) {
//        displayWithVersion(sheetCell, sheetCell.getVersionNumber());
//    }
//
//    @Override
//    public void displaySpecificVersion(DtoSheetCell sheetCell, int version) {
//        displayWithVersion(sheetCell, version);
//    }
//
//    private void displayWithVersion(DtoSheetCell sheetCell, int versionNumber) {
//        int numberOfRows = sheetCell.getCurrentNumberOfRows();
//        int numberOfCols = sheetCell.getCurrentNumberOfCols();
//        int cellLength = sheetCell.getCellLength();
//        int cellWidth = sheetCell.getCellWidth();
//        String sheetName = sheetCell.getName();
//
//        // Calculate the total width of the table
//        int tableWidth = calculateTableWidth(numberOfCols, cellWidth);
//
//        // Print sheet name and version
//        printSheetNameAndVersion(sheetName, versionNumber, tableWidth);
//
//        // Print column headers
//        printColumnHeaders(numberOfCols, cellWidth);
//
//        // Print rows and cells
//        printRows(sheetCell, numberOfRows, numberOfCols, cellLength, cellWidth);
//    }
//
//    private int calculateTableWidth(int numberOfCols, int cellWidth) {
//        return (numberOfCols * (cellWidth + 3)) + 1; // 3 for " | " and 1 for leading space
//    }
//
//    private void printSheetNameAndVersion(String sheetName, int versionNumber, int tableWidth) {
//        int nameLength = sheetName.length() + 7; // "Sheet: " and spaces
//        int spacesBefore = (((tableWidth - nameLength) / 2) + 5);
//        int spacesAfter = tableWidth - nameLength - spacesBefore;
//        System.out.println("\n" + " ".repeat(spacesBefore) + "Sheet: " + sheetName + " ".repeat(spacesAfter) + "version: " + versionNumber + "\n");
//    }
//
//    private void printColumnHeaders(int numberOfCols, int cellWidth) {
//        System.out.print("    "); // Initial space for row headers
//
//        for (char col = 'A'; col < 'A' + numberOfCols; col++) {
//            String letter = String.valueOf(col);
//
//            // Calculate padding for centering the column letter
//            int totalPadding = cellWidth - letter.length();
//            int paddingLeft = totalPadding / 2;
//            int paddingRight = totalPadding - paddingLeft;
//
//            // Print the column letter with centered padding
//            System.out.print(" | ");
//            System.out.print(" ".repeat(paddingLeft) + letter + " ".repeat(paddingRight));
//        }
//        System.out.println(" |"); // Closing pipe for the last column
//    }
//
////    private void printColumnHeaders(int numberOfCols, int cellWidth) {
////        System.out.print("    "); // Initial space for row headers
////        for (char col = 'A'; col < 'A' + numberOfCols; col++) {
////            String header = String.format("%" + cellWidth + "s", col); // Format the column header to fit the cell width
////            System.out.print(" | " + header);  // Print column header with leading delimiter
////        }
////        System.out.println(" |"); // Closing pipe for the last column
////    }
//
//    private void printRows(DtoSheetCell sheetCell, int numberOfRows, int numberOfCols, int cellLength, int cellWidth) {
//        for (int row = 1; row <= numberOfRows; row++) {
//            // Print the row header (row number) with padding
//            System.out.print(String.format("%2d", row) + "  ");
//
//            // Print each cell in the row
//            for (char col = 'A'; col < 'A' + numberOfCols; col++) {
//                printCell(sheetCell, col, row, cellWidth);
//            }
//
//            System.out.print(" |");  // Close the row with a delimiter
//            System.out.println();    // Move to the next line
//
//            // Print vertical spacing if needed
//            printVerticalSpacing(numberOfCols, cellLength, cellWidth);
//        }
//    }
//
//
////
////    private void printColumnHeaders(int numberOfCols, int cellWidth) {
////        System.out.print("    "); // Initial space for row headers
////        for (char col = 'A'; col < 'A' + numberOfCols; col++) {
////            System.out.print(" | ");
////            String header = String.format("%" + (((cellWidth - 1) / 2) + 1) + "s", col);
////            String trailingSpaces = String.format("%" + ((cellWidth - header.length()) + 1) + "s", "");
////            System.out.print(header + trailingSpaces);
////        }
////        System.out.println(" |"); // Closing pipe for the last column
////    }
////
////    private void printRows(DtoSheetCell sheetCell, int numberOfRows, int numberOfCols, int cellLength, int cellWidth) {
////        for (int row = 1; row <= numberOfRows; row++) {
////            System.out.print(String.format("%2d", row) + "  ");
////            for (char col = 'A'; col < 'A' + numberOfCols; col++) {
////                printCell(sheetCell, col, row, cellWidth);
////            }
////            System.out.print(" |");
////            System.out.println();
////
////            printVerticalSpacing(numberOfCols, cellLength, cellWidth);
////        }
////    }
//
//
//
//
//
//    private void printCell(DtoSheetCell sheetCell, char col, int row, int cellWidth) {
//        CellLocation dtoLocation = new CellLocation(col, (char) ('0' + row));
//        EffectiveValue effectiveValue = sheetCell.getEffectiveValue(dtoLocation);
//
//        Object objectValue;
//
//        if (effectiveValue != null) {
//            objectValue = effectiveValue.getValue();
//        } else {
//            objectValue = "";
//        }
//
//        String value = objectValue.toString();
//
//        // If the value length exceeds the cell width, truncate it
//        if (value.length() > cellWidth) {
//            value = value.substring(0, cellWidth - 2);  // Leave space for formatting
//        }
//
//        // Calculate padding for centering the value
//        int totalPadding = cellWidth - value.length();
//        int paddingLeft = totalPadding / 2;
//        int paddingRight = totalPadding - paddingLeft;
//
//        // Ensure that padding is non-negative
//        paddingLeft = Math.max(0, paddingLeft);
//        paddingRight = Math.max(0, paddingRight);
//
//        // Print the cell with padded value
//        System.out.print(" | ");
//        System.out.print(" ".repeat(paddingLeft) + value + " ".repeat(paddingRight));
//    }
//
//    private void printVerticalSpacing(int numberOfCols, int cellLength, int cellWidth) {
//        for (int i = 1; i < cellLength; i++) {
//            System.out.print("    "); // Initial space for row headers
//            for (int col = 0; col < numberOfCols; col++) {
//                System.out.print(" | ");
//                String emptySpace = String.format("%" + ((cellWidth - 0) / 2) + "s", "");
//                String trailingSpacesEmpty = String.format("%" + (cellWidth - emptySpace.length() + 1) + "s", "");
//                System.out.print(emptySpace + trailingSpacesEmpty);
//            }
//            System.out.print(" |");
//            System.out.println();
//        }
//    }
//}

package CoreParts.impl.UtilisUI;

import CoreParts.api.UtilsUI.Displayer;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
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
            String letter = String.valueOf(col);

            // Calculate padding for centering the column letter
            int totalPadding = cellWidth - letter.length();
            int paddingLeft = totalPadding / 2;
            int paddingRight = totalPadding - paddingLeft;

            // Print the column letter with centered padding
            System.out.print(" | ");
            System.out.print(" ".repeat(paddingLeft) + letter + " ".repeat(paddingRight));
        }
        System.out.println(" |"); // Closing pipe for the last column
    }

    private void printRows(DtoSheetCell sheetCell, int numberOfRows, int numberOfCols, int cellLength, int cellWidth) {
        for (int row = 1; row <= numberOfRows; row++) {
            // Print the row header (row number) with padding
            String formattedRow = row < 10 ? String.format("0%d", row) : String.format("%2d", row);
            System.out.print(formattedRow + "  ");
            //System.out.print(String.format("%2d", row) + "  ");

            // Print each cell in the row
            for (char col = 'A'; col < 'A' + numberOfCols; col++) {
                printCell(sheetCell, col, row, cellWidth);
            }

            System.out.print(" |");  // Close the row with a delimiter
            System.out.println();    // Move to the next line

            // Print vertical spacing if needed
            printVerticalSpacing(numberOfCols, cellLength, cellWidth);
        }
    }

    private void printCell(DtoSheetCell sheetCell, char col, int row, int cellWidth) {

        String rowStr = Integer.toString(row);

        CellLocation cellLocation = new CellLocation(col, rowStr);


        EffectiveValue effectiveValue = sheetCell.getEffectiveValue(cellLocation);

        Object objectValue;
        String value = "";

        if (effectiveValue != null) {
            objectValue = effectiveValue.getValue();

            if (objectValue instanceof Double) {
                Double doubleValue = (Double) objectValue;

                // Check if the value is NaN
                if (doubleValue.isNaN()) {
                    value = "NaN";
                } else {
                    // Convert the double to an int and then to a string
                    value = Integer.toString(doubleValue.intValue());
                }
            } else {
                // Just use the object's string value
                value = objectValue.toString();
            }
        }

        // If the value length exceeds the cell width, truncate it
        if (value.length() > cellWidth) {
            value = value.substring(0, cellWidth - 2);  // Leave space for formatting
        }



        // Calculate padding for centering the value
        int totalPadding = cellWidth - value.length();
        int paddingLeft = totalPadding / 2;
        int paddingRight = totalPadding - paddingLeft;

        // Ensure that padding is non-negative
        paddingLeft = Math.max(0, paddingLeft);
        paddingRight = Math.max(0, paddingRight);

        // Print the cell with padded value
        System.out.print(" | ");
        System.out.print(" ".repeat(paddingLeft) + value + " ".repeat(paddingRight));
    }

    private void printVerticalSpacing(int numberOfCols, int cellLength, int cellWidth) {
        for (int i = 1; i < cellLength; i++) {
            System.out.print("    "); // Initial space for row headers
            for (int col = 0; col < numberOfCols; col++) {
                System.out.print(" | ");
                System.out.print(" ".repeat(cellWidth)); // Empty space filling the cell width
            }
            System.out.print(" |");
            System.out.println();
        }
    }
}
