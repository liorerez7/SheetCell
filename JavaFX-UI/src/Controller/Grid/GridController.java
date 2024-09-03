package Controller.Grid;

import Controller.Main.MainController;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import expression.api.EffectiveValue;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GridController {
    @FXML
    private GridPane grid;
    private MainController mainController;
    @FXML
    private Map<CellLocation, StringProperty> cellLocToProperties = new HashMap<>();
    private Map<CellLocation, Label> cellLocationToLabel = new HashMap<>();

    public void initializeGrid(DtoSheetCell sheetCell) {
        grid.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ExelBasicGrid.css")).toExternalForm());
        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();

        int cellWidth = sheetCell.getCellWidth();
        int cellLength = sheetCell.getCellLength();

        cellWidth = cellWidth * 10;
        cellLength = cellLength * 13;

        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();
        // Clear existing constraints and children
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        grid.getChildren().clear();

        // Create column constraints
        for (int i = 0; i < numCols + 1; i++) { // +1 for header column
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setMinWidth(cellWidth); // Adjust width for better visibility
            colConstraints.setPrefWidth(cellWidth);
            colConstraints.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
            grid.getColumnConstraints().add(colConstraints);
        }

        // Create row constraints
        for (int i = 0; i < numRows + 1; i++) { // +1 for header row
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(cellLength); // Adjust height for better visibility
            rowConstraints.setPrefHeight(cellLength);
            rowConstraints.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
            grid.getRowConstraints().add(rowConstraints);
        }

        // Add column headers
        for (int col = 1; col <= numCols; col++) {
            Label header = new Label(String.valueOf((char) ('A' + col - 1)));
            header.getStyleClass().add("header-label");
            grid.add(header, col, 0);
        }

        // Add row headers
        for (int row = 1; row <= numRows; row++) {
            Label header = new Label(String.valueOf(row));
            header.getStyleClass().add("header-label");
            grid.add(header, 0, row);
        }

        // Add cells with Label
        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {
                Label cell = new Label();
                cell.getStyleClass().add("cell-label");

                cell.setMinSize(cellWidth, cellLength); // Adjust size for better visibility
                cell.setPrefSize(cellWidth, cellLength);

                cell.setStyle(
                        "-fx-background-color: white; " +             // White background for cells
                                "-fx-border-color: black; " +          // Black border for cells
                                "-fx-border-width: 1px; " +            // 1px border width for cells
                                "-fx-alignment: center; "// Center alignment for cells
                );

                // Bind the Label's textProperty to the EffectiveValue
                char colChar = (char) ('A' + col - 1);
                String rowString = String.valueOf(row);
                cell.setId(colChar + rowString);

                CellLocation location = new CellLocation(colChar, rowString);
                EffectiveValue effectiveValue = viewSheetCell.get(location);
                if (effectiveValue != null) {
                    cell.setText(effectiveValue.getValue().toString());
                }

                cell.setOnMouseClicked(event -> onCellClicked(cell.getId()));


                cellLocationToLabel.put(location,cell);
                GridPane.setConstraints(cell, col, row);
                grid.add(cell, col, row);
            }
        }
        mainController.setCellsLablesMap(cellLocationToLabel);
    }

    private void onCellClicked(String location) {
        mainController.cellClicked(location);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

}
