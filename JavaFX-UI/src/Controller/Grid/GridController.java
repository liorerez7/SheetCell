package Controller.Grid;

import Controller.Main.MainController;
import CoreParts.api.Cell;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import expression.api.EffectiveValue;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

import static java.lang.Thread.sleep;

public class GridController {
    @FXML
    private GridPane grid;
    private MainController mainController;
    @FXML
    private Map<CellLocation, Label> cellLocationToLabel = new HashMap<>();
    NeighborsHandler neighborsHandler;

    public void initializeEmptyGrid(DtoSheetCell sheetCell,GridPane grid) {
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
            colConstraints.setHgrow(Priority.SOMETIMES);
            grid.getColumnConstraints().add(colConstraints);
        }
        // Create row constraints
        for (int i = 0; i < numRows + 1; i++) { // +1 for header row
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(cellLength); // Adjust height for better visibility
            rowConstraints.setPrefHeight(cellLength);
            rowConstraints.setMaxHeight(cellLength);
            rowConstraints.setVgrow(Priority.SOMETIMES);
            grid.getRowConstraints().add(rowConstraints);
        }

        for (int col = 1; col <= numCols; col++) {

            Label header = new Label(String.valueOf((char) ('A' + col - 1)));

            setLabelSize(header, cellWidth, cellLength);

            header.getStyleClass().add("header-label");
            grid.add(header, col, 0);
        }

        // Add row headers
        for (int row = 1; row <= numRows; row++) {
            Label header = new Label(String.valueOf(row));

            setLabelSize(header, cellWidth, cellLength);

            header.getStyleClass().add("header-label");
            grid.add(header, 0, row);
        }
    }
    public Map<CellLocation, Label> initializeGrid(DtoSheetCell sheetCell) {
        neighborsHandler = new NeighborsHandler();
        initializeEmptyGrid(sheetCell,grid);
        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth();
        int cellLength = sheetCell.getCellLength();
        cellWidth = cellWidth * 10;
        cellLength = cellLength * 13;

        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();
        // Add cells with Label
        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {
                Label cell = new Label();
                cell.getStyleClass().add("cell-label");
                setLabelSize(cell, cellWidth, cellLength);
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
                cellLocationToLabel.put(location, cell);
                grid.add(cell,col, row);
            }
        }
        return cellLocationToLabel;
    }

    private void setLabelSize(Label label, int width, int height) {

        label.setMinWidth(width);
        label.setMinHeight(height);
        label.setPrefWidth(width);
        label.setPrefHeight(height);
        label.setMaxWidth(width);
        label.setMaxHeight(height);
        label.setPadding(Insets.EMPTY);  // Removes padding
    }

    private void onCellClicked(String location) {
        mainController.cellClicked(location);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void showNeighbors(DtoCell cell) {
        neighborsHandler.showNeighbors(cell, cellLocationToLabel);
    }


    public void initializePopupGrid(GridPane grid, DtoSheetCell sheetCell) {
        initializeEmptyGrid(sheetCell, grid);
        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth();
        int cellLength = sheetCell.getCellLength();
        cellWidth = cellWidth * 10;
        cellLength = cellLength * 13;
        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();
        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {
                Label cell = new Label();
                cell.getStyleClass().add("cell-label");
                setLabelSize(cell, cellWidth, cellLength);

                // Bind the Label's textProperty to the EffectiveValue
                char colChar = (char) ('A' + col - 1);
                String rowString = String.valueOf(row);
                cell.setId(colChar + rowString);
                CellLocation location = new CellLocation(colChar, rowString);
                EffectiveValue effectiveValue = viewSheetCell.get(location);
                if (effectiveValue != null) {
                    cell.setText(effectiveValue.getValue().toString());
                }
                grid.add(cell, col, row);
            }
        }
    }

    public void initializeCirclePopUp(GridPane grid, DtoSheetCell sheetCell, List<CellLocation> neighbors) {
        initializeEmptyGrid(sheetCell, grid);
        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth();
        int cellLength = sheetCell.getCellLength();
        cellWidth = cellWidth * 10;
        cellLength = cellLength * 13;
        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();
        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {
                Label cell = new Label();
                cell.getStyleClass().add("cell-label");
                setLabelSize(cell, cellWidth, cellLength);

                // Bind the Label's textProperty to the EffectiveValue
                char colChar = (char) ('A' + col - 1);
                String rowString = String.valueOf(row);
                cell.setId(colChar + rowString);
                CellLocation location = new CellLocation(colChar, rowString);
                if (neighbors.contains(location)) {
                    cell.setText(colChar+rowString);
                }
                grid.add(cell, col, row);
            }
        }
        List<Label> labels = new ArrayList<>();
        neighbors.forEach(location -> labels.add(cellLocationToLabel.get(location)));
        addLinesBetweenLabels(labels);
    }
    private void addLinesBetweenLabels(List<Label> labels) {
        Platform.runLater(() -> {
            // Iterate through the list of labels
            for (int i = 0; i < labels.size() - 1; i++) {
                Label startLabel = labels.get(i);
                Label endLabel = labels.get(i + 1);

                // Get the positions of the start and end labels
                Bounds startBounds = startLabel.localToScene(startLabel.getBoundsInLocal());
                Bounds endBounds = endLabel.localToScene(endLabel.getBoundsInLocal());

                // Calculate the start and end points for the line
                double startX = startBounds.getMinX() + startBounds.getWidth() / 2;
                double startY = startBounds.getMinY() + startBounds.getHeight() / 2;
                double endX = endBounds.getMinX() + endBounds.getWidth() / 2;
                double endY = endBounds.getMinY() + endBounds.getHeight() / 2;

                // Create the line
                Line line = new Line(startX, startY, endX, endY);
                line.setStroke(Color.BLACK); // Set line color
                line.setStrokeWidth(2); // Set line width

                // Add the line to the parent of the labels (assuming it's a Pane)
                ((Pane) startLabel.getParent()).getChildren().add(line);
            }
        });
    }
}

