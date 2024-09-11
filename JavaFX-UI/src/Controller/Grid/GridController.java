
package Controller.Grid;

import Controller.Main.MainController;
import Controller.Utility.StringParser;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import expression.api.EffectiveValue;
import expression.impl.stringFunction.Str;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.*;

import static java.lang.Thread.sleep;

public class GridController {
    @FXML
    private GridPane grid;
    private MainController mainController;
    @FXML
    private Map<CellLocation, Label> cellLocationToLabel = new HashMap<>();
    NeighborsHandler neighborsHandler;
    private final static int DeltaExtensionGrid = 2;




    public void initializeEmptyGrid(DtoSheetCell sheetCell, GridPane grid) {

        grid.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ExelBasicGrid.css")).toExternalForm());

        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth() * DeltaExtensionGrid;
        int cellLength = sheetCell.getCellLength() * DeltaExtensionGrid;



        clearGrid(grid);
        setupColumnConstraints(grid, numCols, cellWidth);
        setupRowConstraints(grid, numRows, cellLength);
        addColumnHeaders(grid, numCols, cellWidth, cellLength);
        addRowHeaders(grid, numRows, cellWidth, cellLength);
    }

    public Map<CellLocation, Label> initializeGrid(DtoSheetCell sheetCell) {
        neighborsHandler = new NeighborsHandler();
        initializeEmptyGrid(sheetCell,grid);
        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth();
        int cellLength = sheetCell.getCellLength();

        cellWidth = cellWidth * DeltaExtensionGrid;
        cellLength = cellLength * DeltaExtensionGrid;


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
                    String textForLabel = StringParser.convertValueToLabelText(effectiveValue);
                    cell.setText(textForLabel);
                }
                cell.setOnMouseClicked(event -> onCellClicked(cell.getId()));
                cellLocationToLabel.put(location, cell);
                grid.add(cell,col, row);
            }
        }
        return cellLocationToLabel;
    }

    public void initializeCirclePopUp(GridPane grid, DtoSheetCell sheetCell, List<CellLocation> neighbors) {
        initializeEmptyGrid(sheetCell, grid);
        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth();
        int cellLength = sheetCell.getCellLength();
        cellWidth = cellWidth * DeltaExtensionGrid;
        cellLength = cellLength * DeltaExtensionGrid;


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
                    // Highlight the circular dependency cell by setting the background color
                    cell.setText(colChar + rowString);
                    cell.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

                    // Apply background color animation
                    applyBackgroundColorAnimation(cell);
                } else {
                    EffectiveValue effectiveValue = viewSheetCell.get(location);
                    if (effectiveValue != null) {
                        String textForLabel = StringParser.convertValueToLabelText(effectiveValue);
                        cell.setText(textForLabel);
                    }
                }

                grid.add(cell, col, row);
            }
        }
    }

    private void applyBackgroundColorAnimation(Label cell) {
        // Create a Timeline for background color animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(cell.backgroundProperty(), new Background(new BackgroundFill(Color.RED, null, null)))),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(cell.backgroundProperty(), new Background(new BackgroundFill(Color.WHITE, null, null)))),
                new KeyFrame(Duration.seconds(1), new KeyValue(cell.backgroundProperty(), new Background(new BackgroundFill(Color.RED, null, null))))
        );

        timeline.setCycleCount(Timeline.INDEFINITE);  // Loop the animation
        timeline.play();  // Start the animation
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

    public void initializePopupGrid(GridPane grid, DtoSheetCell sheetCell) {
        initializeEmptyGrid(sheetCell, grid);
        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth();
        int cellLength = sheetCell.getCellLength();
        cellWidth = cellWidth * DeltaExtensionGrid;
        cellLength = cellLength * DeltaExtensionGrid;


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
                    String textForLabel = StringParser.convertValueToLabelText(effectiveValue);
                    cell.setText(textForLabel);
                }
                grid.add(cell, col, row);
            }
        }
    }

    private void clearGrid(GridPane grid) {
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        grid.getChildren().clear();
    }

    private void setupColumnConstraints(GridPane grid, int numCols, int cellWidth) {
        for (int i = 0; i < numCols + 1; i++) { // +1 for header column
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setMinWidth(cellWidth);
            colConstraints.setPrefWidth(cellWidth);
            colConstraints.setHgrow(Priority.SOMETIMES);
            grid.getColumnConstraints().add(colConstraints);
        }
    }

    private void setupRowConstraints(GridPane grid, int numRows, int cellLength) {
        for (int i = 0; i < numRows + 1; i++) { // +1 for header row
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(cellLength);
            rowConstraints.setPrefHeight(cellLength);
            rowConstraints.setMaxHeight(cellLength);
            rowConstraints.setVgrow(Priority.SOMETIMES);
            grid.getRowConstraints().add(rowConstraints);
        }
    }

    private void addColumnHeaders(GridPane grid, int numCols, int cellWidth, int cellLength) {
        for (int col = 1; col <= numCols; col++) {
            Label header = new Label(String.valueOf((char) ('A' + col - 1)));
            setLabelSize(header, cellWidth, cellLength);
            header.getStyleClass().add("header-label");
            grid.add(header, col, 0);
        }
    }

    private void addRowHeaders(GridPane grid, int numRows, int cellWidth, int cellLength) {
        for (int row = 1; row <= numRows; row++) {
            Label header = new Label(String.valueOf(row));
            setLabelSize(header, cellWidth, cellLength);
            header.getStyleClass().add("header-label");
            grid.add(header, 0, row);
        }
    }

    public void showAffectedCells(List<CellLocation> requestedRange) {
        neighborsHandler.showAffectedCells(requestedRange, cellLocationToLabel);
    }

    public void showNeighbors(DtoCell cell) {
        neighborsHandler.showNeighbors(cell, cellLocationToLabel);
    }

    public void clearAllHighlights() {
        neighborsHandler.clearAllHighlights(cellLocationToLabel);
    }

    public void increaseColumnWidthB(String RowOrColumn, int increaseOrDecrease) {

        int valueToChange = increaseOrDecrease *  10;
        boolean isRow = false;
        boolean isColumn = false;
        int columnIndex = 0;

        char optionalCol = RowOrColumn.charAt(0);

        if((optionalCol >= 'A') && (optionalCol <= 'Z')){
            isColumn = true;
            columnIndex = optionalCol - 'A' + 1;
        }

        if(isColumn){
            ColumnConstraints columnConstraints = grid.getColumnConstraints().get(columnIndex);

            // Increase both the preferred and minimum width by 10 units
            double newWidth = columnConstraints.getPrefWidth() + valueToChange;
            columnConstraints.setPrefWidth(newWidth);
            columnConstraints.setMinWidth(newWidth);

            updateColumnHeader(columnIndex, newWidth);
            updateColumnCells(columnIndex, newWidth);
        }
        else{

            RowConstraints rowConstraints = grid.getRowConstraints().get(Integer.parseInt(RowOrColumn));

            // Increase both the preferred and minimum height by 10 units
            double newHeight = rowConstraints.getPrefHeight() + valueToChange;
            rowConstraints.setPrefHeight(newHeight);
            rowConstraints.setMinHeight(newHeight);

            updateRowHeader(Integer.parseInt(RowOrColumn), newHeight);
            updateRowCells(Integer.parseInt(RowOrColumn), newHeight);
        }
    }

    private void updateColumnCells(int columnIndex, double newWidth) {

        for (Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) == columnIndex) {
                Label label = (Label) node;
                setLabelSize(label, (int) newWidth, (int) label.getPrefHeight());
                label.setPadding(Insets.EMPTY); // Remove padding
                label.setAlignment(Pos.CENTER); // Ensure text alignment
            }
        }
    }

    private void updateColumnHeader(int columnIndex, double newWidth) {
        // Update the header label for the specific column

        for (Node node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) == 0 && GridPane.getColumnIndex(node) == columnIndex) {
                Label header = (Label) node;
                setLabelSize(header, (int) newWidth , (int) header.getPrefHeight());
                header.setPadding(Insets.EMPTY); // Remove padding
                header.setAlignment(Pos.CENTER); // Ensure text alignment
            }
        }
    }

    private void updateRowCells(int rowIndex, double newHeight) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) == rowIndex) {
                Label label = (Label) node;
                setLabelSize(label, (int) label.getPrefWidth(), (int) newHeight);
                label.setPadding(Insets.EMPTY); // Remove padding
                label.setAlignment(Pos.CENTER); // Ensure text alignment
            }
        }
    }

    private void updateRowHeader(int rowIndex, double newHeight) {
        // Update the header label for the specific row
        for (Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == rowIndex) {
                Label header = (Label) node;
                setLabelSize(header, (int) header.getPrefWidth(), (int) newHeight);
                header.setPadding(Insets.EMPTY); // Remove padding
                header.setAlignment(Pos.CENTER); // Ensure text alignment
            }
        }
    }

}


