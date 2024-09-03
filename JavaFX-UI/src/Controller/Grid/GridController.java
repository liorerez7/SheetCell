package Controller.Grid;

import Controller.Main.MainController;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import expression.api.EffectiveValue;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();

        // Clear existing constraints and children
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        grid.getChildren().clear();

        // Create column constraints with no gaps
        for (int i = 0; i < numCols + 1; i++) { // +1 for header column
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPrefWidth(100); // Adjust width as needed
            colConstraints.setMinWidth(30);
            grid.getColumnConstraints().add(colConstraints);
        }

        // Create row constraints with no gaps
        for (int i = 0; i < numRows + 1; i++) { // +1 for header row
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(30); // Adjust height as needed
            rowConstraints.setMinHeight(30);
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

                char colChar = (char) ('A' + col - 1);
                String rowString = String.valueOf(row);
                cell.setId(colChar + rowString);

                CellLocation location = new CellLocation(colChar, rowString);
                EffectiveValue effectiveValue = viewSheetCell.get(location);
                if (effectiveValue != null) {
                    cell.setText(effectiveValue.getValue().toString());
                }

                cell.setOnMouseClicked(event -> onCellClicked(cell.getId()));

                cellLocationToLabel.put(location, cell);
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
