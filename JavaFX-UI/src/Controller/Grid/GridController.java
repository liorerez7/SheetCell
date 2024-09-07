package Controller.Grid;

import Controller.Main.MainController;
import Controller.Utility.StringParser;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import expression.api.EffectiveValue;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import java.util.*;

public class GridController {
    @FXML
    private GridPane grid;
    private MainController mainController;
    @FXML
    private Map<CellLocation, Label> cellLocationToLabel = new HashMap<>();
    private NeighborsHandler neighborsHandler;

    // Initialize empty grid with row and column constraints
    public void initializeEmptyGrid(DtoSheetCell sheetCell, GridPane grid) {

        grid.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ExelBasicGrid.css")).toExternalForm());

        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth() * 10;
        int cellLength = sheetCell.getCellLength() * 13;

        clearGrid(grid);
        setupColumnConstraints(grid, numCols, cellWidth);
        setupRowConstraints(grid, numRows, cellLength);
        addHeaders(grid, numCols, numRows, cellWidth, cellLength);
    }

    private void addRowHeaders(GridPane grid, int numRows, int cellWidth, int cellLength) {
        for (int row = 1; row <= numRows; row++) {
            Label header = new Label(String.valueOf(row));
            setLabelSize(header, cellWidth, cellLength);
            header.getStyleClass().add("header-label");
            grid.add(header, 0, row);
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

    public Map<CellLocation, Label> initializeGrid(DtoSheetCell sheetCell) {
        neighborsHandler = new NeighborsHandler();
        initializeEmptyGrid(sheetCell, grid);
        return addCellsToGrid(sheetCell, grid);
    }

    private Map<CellLocation, Label> addCellsToGrid(DtoSheetCell sheetCell, GridPane grid) {

        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();
        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth() * 10;
        int cellLength = sheetCell.getCellLength() * 13;

        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {
                Label cell = createCellLabel(col, row, cellWidth, cellLength, viewSheetCell);
                grid.add(cell, col, row);
                cellLocationToLabel.put(new CellLocation((char) ('A' + col - 1), String.valueOf(row)), cell);
            }
        }

        return cellLocationToLabel;
    }

    private Label createCellLabel(int col, int row, int cellWidth, int cellLength, Map<CellLocation, EffectiveValue> viewSheetCell) {

        Label cell = new Label();
        cell.getStyleClass().add("cell-label");
        setLabelSize(cell, cellWidth, cellLength);

        char colChar = (char) ('A' + col - 1);
        String rowString = String.valueOf(row);
        cell.setId(colChar + rowString);

        CellLocation location = new CellLocation(colChar, rowString);
        EffectiveValue effectiveValue = viewSheetCell.get(location);

        if (effectiveValue != null) {
            cell.setText(StringParser.convertValueToLabelText(effectiveValue));
        }
        cell.setOnMouseClicked(event -> onCellClicked(cell.getId()));
        return cell;
    }

    public void initializePopupGrid(GridPane grid, DtoSheetCell sheetCell) {
        initializeEmptyGrid(sheetCell, grid);
        addCellsToGrid(sheetCell, grid);
    }

    // Initialize circle popup with neighbors
    public void initializeCirclePopUp(GridPane grid, DtoSheetCell sheetCell, List<CellLocation> neighbors) {
        initializePopupGrid(grid, sheetCell);
        highlightNeighbors(neighbors);
    }

    private void highlightNeighbors(List<CellLocation> neighbors) {
        List<Label> labels = new ArrayList<>();
        neighbors.forEach(location -> labels.add(cellLocationToLabel.get(location)));
        addLinesBetweenLabels(labels);
    }

    // Set size for label
    private void setLabelSize(Label label, int width, int height) {
        label.setMinWidth(width);
        label.setMinHeight(height);
        label.setPrefWidth(width);
        label.setPrefHeight(height);
        label.setMaxWidth(width);
        label.setMaxHeight(height);
        label.setPadding(Insets.EMPTY);
    }

    // Action when cell is clicked
    private void onCellClicked(String location) {
        mainController.cellClicked(location);
    }

    // Set main controller for handling external actions
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // Show neighbors for selected cell
    public void showNeighbors(DtoCell cell) {
        neighborsHandler.showNeighbors(cell, cellLocationToLabel);
    }

    // Add lines between labels to visualize neighbors
    private void addLinesBetweenLabels(List<Label> labels) {
        Platform.runLater(() -> {
            for (int i = 0; i < labels.size() - 1; i++) {
                Label startLabel = labels.get(i);
                Label endLabel = labels.get(i + 1);

                Bounds startBounds = startLabel.localToScene(startLabel.getBoundsInLocal());
                Bounds endBounds = endLabel.localToScene(endLabel.getBoundsInLocal());

                Line line = new Line(
                        startBounds.getMinX() + startBounds.getWidth() / 2,
                        startBounds.getMinY() + startBounds.getHeight() / 2,
                        endBounds.getMinX() + endBounds.getWidth() / 2,
                        endBounds.getMinY() + endBounds.getHeight() / 2
                );

                line.setStroke(Color.BLACK);
                line.setStrokeWidth(2);
                ((Pane) startLabel.getParent()).getChildren().add(line);
            }
        });
    }

    // Clears existing constraints and children from the grid
    private void clearGrid(GridPane grid) {
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        grid.getChildren().clear();
    }

    // Set up column constraints
    private void setupColumnConstraints(GridPane grid, int numCols, int cellWidth) {
        for (int i = 0; i < numCols + 1; i++) { // +1 for header column
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setMinWidth(cellWidth);
            colConstraints.setPrefWidth(cellWidth);
            colConstraints.setHgrow(Priority.SOMETIMES);
            grid.getColumnConstraints().add(colConstraints);
        }
    }

    // Set up row constraints
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

    // Add headers to the grid
    private void addHeaders(GridPane grid, int numCols, int numRows, int cellWidth, int cellLength) {
        addColumnHeaders(grid, numCols, cellWidth, cellLength);
        addRowHeaders(grid, numRows, cellWidth, cellLength);
    }
}












//package Controller.Grid;
//
//import Controller.Main.MainController;
//import Controller.Utility.StringParser;
//import CoreParts.api.Cell;
//import CoreParts.impl.DtoComponents.DtoCell;
//import CoreParts.impl.DtoComponents.DtoSheetCell;
//import CoreParts.smallParts.CellLocation;
//import expression.api.EffectiveValue;
//import javafx.application.Platform;
//import javafx.beans.property.StringProperty;
//import javafx.fxml.FXML;
//import javafx.geometry.Bounds;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Line;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//
//import java.util.*;
//
//import static java.lang.Thread.sleep;
//
//public class GridController {
//    @FXML
//    private GridPane grid;
//    private MainController mainController;
//    @FXML
//    private Map<CellLocation, Label> cellLocationToLabel = new HashMap<>();
//    NeighborsHandler neighborsHandler;
//
//    public void initializeEmptyGrid(DtoSheetCell sheetCell, GridPane grid) {
//
//        grid.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ExelBasicGrid.css")).toExternalForm());
//
//        int numCols = sheetCell.getNumberOfColumns();
//        int numRows = sheetCell.getNumberOfRows();
//        int cellWidth = sheetCell.getCellWidth() * 10;
//        int cellLength = sheetCell.getCellLength() * 13;
//
//        clearGrid(grid);
//        setupColumnConstraints(grid, numCols, cellWidth);
//        setupRowConstraints(grid, numRows, cellLength);
//        addColumnHeaders(grid, numCols, cellWidth, cellLength);
//        addRowHeaders(grid, numRows, cellWidth, cellLength);
//    }
//
//
//
//
//
//    public Map<CellLocation, Label> initializeGrid(DtoSheetCell sheetCell) {
//        neighborsHandler = new NeighborsHandler();
//        initializeEmptyGrid(sheetCell,grid);
//        int numCols = sheetCell.getNumberOfColumns();
//        int numRows = sheetCell.getNumberOfRows();
//        int cellWidth = sheetCell.getCellWidth();
//        int cellLength = sheetCell.getCellLength();
//        cellWidth = cellWidth * 10;
//        cellLength = cellLength * 13;
//
//        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();
//        // Add cells with Label
//        for (int row = 1; row <= numRows; row++) {
//            for (int col = 1; col <= numCols; col++) {
//                Label cell = new Label();
//                cell.getStyleClass().add("cell-label");
//                setLabelSize(cell, cellWidth, cellLength);
//                // Bind the Label's textProperty to the EffectiveValue
//                char colChar = (char) ('A' + col - 1);
//                String rowString = String.valueOf(row);
//                cell.setId(colChar + rowString);
//
//                CellLocation location = new CellLocation(colChar, rowString);
//                EffectiveValue effectiveValue = viewSheetCell.get(location);
//
//                if (effectiveValue != null) {
//                    String textForLabel = StringParser.convertValueToLabelText(effectiveValue);
//                    cell.setText(textForLabel);
//                }
//                cell.setOnMouseClicked(event -> onCellClicked(cell.getId()));
//                cellLocationToLabel.put(location, cell);
//                grid.add(cell,col, row);
//            }
//        }
//        return cellLocationToLabel;
//    }
//
//
////    public Map<CellLocation, Label> initializeGrid(DtoSheetCell sheetCell) {
////        neighborsHandler = new NeighborsHandler();
////        initializeGenericGrid(sheetCell, grid, null);  // No neighbors for the main grid
////        return cellLocationToLabel;
////    }
////
////    public void initializePopupGrid(GridPane grid, DtoSheetCell sheetCell) {
////        initializeGenericGrid(sheetCell, grid, null);  // No neighbors for popup grids
////    }
////
////    public void initializeCirclePopUp(GridPane grid, DtoSheetCell sheetCell, List<CellLocation> neighbors) {
////        initializeGenericGrid(sheetCell, grid, neighbors);  // Neighbors specified for circle popup
////    }
////
////
////    private void initializeGenericGrid(DtoSheetCell sheetCell, GridPane grid, List<CellLocation> neighbors) {
////        initializeEmptyGrid(sheetCell, grid);
////
////        int numCols = sheetCell.getNumberOfColumns();
////        int numRows = sheetCell.getNumberOfRows();
////        int cellWidth = sheetCell.getCellWidth() * 10;
////        int cellLength = sheetCell.getCellLength() * 13;
////
////        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();
////
////        for (int row = 1; row <= numRows; row++) {
////            for (int col = 1; col <= numCols; col++) {
////                Label cell = createGridCell(col, row, cellWidth, cellLength, viewSheetCell, neighbors);
////                grid.add(cell, col, row);
////                // Only add to cellLocationToLabel if not in popup grids
////                if (neighbors == null) {
////                    CellLocation location = new CellLocation((char) ('A' + col - 1), String.valueOf(row));
////                    cellLocationToLabel.put(location, cell);
////                }
////            }
////        }
////
////        // If neighbors are specified, add lines between labels
////        if (neighbors != null) {
////            List<Label> labels = new ArrayList<>();
////            neighbors.forEach(location -> labels.add(cellLocationToLabel.get(location)));
////            addLinesBetweenLabels(labels);
////        }
////    }
////
////
////    private Label createGridCell(int col, int row, int cellWidth, int cellLength, Map<CellLocation, EffectiveValue> viewSheetCell, List<CellLocation> neighbors) {
////        Label cell = new Label();
////        cell.getStyleClass().add("cell-label");
////        setLabelSize(cell, cellWidth, cellLength);
////
////        // Set ID
////        char colChar = (char) ('A' + col - 1);
////        String rowString = String.valueOf(row);
////        cell.setId(colChar + rowString);
////
////        CellLocation location = new CellLocation(colChar, rowString);
////        EffectiveValue effectiveValue = viewSheetCell.get(location);
////
////        // Set text for popup grids with neighbors
////        if (neighbors != null && neighbors.contains(location)) {
////            cell.setText(colChar + rowString);
////        } else if (effectiveValue != null) {
////            String textForLabel = StringParser.convertValueToLabelText(effectiveValue);
////            cell.setText(textForLabel);
////        }
////
////        return cell;
////    }
//
//
//
//
//
//
//
//    private void setLabelSize(Label label, int width, int height) {
//
//        label.setMinWidth(width);
//        label.setMinHeight(height);
//        label.setPrefWidth(width);
//        label.setPrefHeight(height);
//        label.setMaxWidth(width);
//        label.setMaxHeight(height);
//        label.setPadding(Insets.EMPTY);  // Removes padding
//    }
//
//    private void onCellClicked(String location) {
//        mainController.cellClicked(location);
//    }
//
//    public void setMainController(MainController mainController) {
//        this.mainController = mainController;
//    }
//
//    public void showNeighbors(DtoCell cell) {
//        neighborsHandler.showNeighbors(cell, cellLocationToLabel);
//    }
//
//
//    public void initializePopupGrid(GridPane grid, DtoSheetCell sheetCell) {
//        initializeEmptyGrid(sheetCell, grid);
//        int numCols = sheetCell.getNumberOfColumns();
//        int numRows = sheetCell.getNumberOfRows();
//        int cellWidth = sheetCell.getCellWidth();
//        int cellLength = sheetCell.getCellLength();
//        cellWidth = cellWidth * 10;
//        cellLength = cellLength * 13;
//        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();
//        for (int row = 1; row <= numRows; row++) {
//            for (int col = 1; col <= numCols; col++) {
//                Label cell = new Label();
//                cell.getStyleClass().add("cell-label");
//                setLabelSize(cell, cellWidth, cellLength);
//
//                // Bind the Label's textProperty to the EffectiveValue
//                char colChar = (char) ('A' + col - 1);
//                String rowString = String.valueOf(row);
//                cell.setId(colChar + rowString);
//                CellLocation location = new CellLocation(colChar, rowString);
//                EffectiveValue effectiveValue = viewSheetCell.get(location);
//                if (effectiveValue != null) {
//                    String textForLabel = StringParser.convertValueToLabelText(effectiveValue);
//                    cell.setText(textForLabel);
//                }
//                grid.add(cell, col, row);
//            }
//        }
//    }
//
//    public void initializeCirclePopUp(GridPane grid, DtoSheetCell sheetCell, List<CellLocation> neighbors) {
//        initializeEmptyGrid(sheetCell, grid);
//        int numCols = sheetCell.getNumberOfColumns();
//        int numRows = sheetCell.getNumberOfRows();
//        int cellWidth = sheetCell.getCellWidth();
//        int cellLength = sheetCell.getCellLength();
//        cellWidth = cellWidth * 10;
//        cellLength = cellLength * 13;
//        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();
//        for (int row = 1; row <= numRows; row++) {
//            for (int col = 1; col <= numCols; col++) {
//                Label cell = new Label();
//                cell.getStyleClass().add("cell-label");
//                setLabelSize(cell, cellWidth, cellLength);
//
//                // Bind the Label's textProperty to the EffectiveValue
//                char colChar = (char) ('A' + col - 1);
//                String rowString = String.valueOf(row);
//                cell.setId(colChar + rowString);
//                CellLocation location = new CellLocation(colChar, rowString);
//                if (neighbors.contains(location)) {
//                    cell.setText(colChar+rowString);
//                }
//                grid.add(cell, col, row);
//            }
//        }
//        List<Label> labels = new ArrayList<>();
//        neighbors.forEach(location -> labels.add(cellLocationToLabel.get(location)));
//        addLinesBetweenLabels(labels);
//    }
//
//    private void addLinesBetweenLabels(List<Label> labels) {
//
//        Platform.runLater(() -> {
//            // Iterate through the list of labels
//            for (int i = 0; i < labels.size() - 1; i++) {
//                Label startLabel = labels.get(i);
//                Label endLabel = labels.get(i + 1);
//
//                // Get the positions of the start and end labels
//                Bounds startBounds = startLabel.localToScene(startLabel.getBoundsInLocal());
//                Bounds endBounds = endLabel.localToScene(endLabel.getBoundsInLocal());
//
//                // Calculate the start and end points for the line
//                double startX = startBounds.getMinX() + startBounds.getWidth() / 2;
//                double startY = startBounds.getMinY() + startBounds.getHeight() / 2;
//                double endX = endBounds.getMinX() + endBounds.getWidth() / 2;
//                double endY = endBounds.getMinY() + endBounds.getHeight() / 2;
//
//                // Create the line
//                Line line = new Line(startX, startY, endX, endY);
//                line.setStroke(Color.BLACK); // Set line color
//                line.setStrokeWidth(2); // Set line width
//
//                // Add the line to the parent of the labels (assuming it's a Pane)
//                ((Pane) startLabel.getParent()).getChildren().add(line);
//            }
//        });
//    }
//
//    // Clears existing constraints and children from the grid
//    private void clearGrid(GridPane grid) {
//        grid.getColumnConstraints().clear();
//        grid.getRowConstraints().clear();
//        grid.getChildren().clear();
//    }
//
//    // Sets up column constraints based on the number of columns and the desired cell width
//    private void setupColumnConstraints(GridPane grid, int numCols, int cellWidth) {
//        for (int i = 0; i < numCols + 1; i++) { // +1 for header column
//            ColumnConstraints colConstraints = new ColumnConstraints();
//            colConstraints.setMinWidth(cellWidth);
//            colConstraints.setPrefWidth(cellWidth);
//            colConstraints.setHgrow(Priority.SOMETIMES);
//            grid.getColumnConstraints().add(colConstraints);
//        }
//    }
//
//    // Sets up row constraints based on the number of rows and the desired cell length
//    private void setupRowConstraints(GridPane grid, int numRows, int cellLength) {
//        for (int i = 0; i < numRows + 1; i++) { // +1 for header row
//            RowConstraints rowConstraints = new RowConstraints();
//            rowConstraints.setMinHeight(cellLength);
//            rowConstraints.setPrefHeight(cellLength);
//            rowConstraints.setMaxHeight(cellLength);
//            rowConstraints.setVgrow(Priority.SOMETIMES);
//            grid.getRowConstraints().add(rowConstraints);
//        }
//    }
//
//    // Adds column headers (A, B, C, ...) to the grid
//    private void addColumnHeaders(GridPane grid, int numCols, int cellWidth, int cellLength) {
//        for (int col = 1; col <= numCols; col++) {
//            Label header = new Label(String.valueOf((char) ('A' + col - 1)));
//            setLabelSize(header, cellWidth, cellLength);
//            header.getStyleClass().add("header-label");
//            grid.add(header, col, 0);
//        }
//    }
//
//    // Adds row headers (1, 2, 3, ...) to the grid
//    private void addRowHeaders(GridPane grid, int numRows, int cellWidth, int cellLength) {
//        for (int row = 1; row <= numRows; row++) {
//            Label header = new Label(String.valueOf(row));
//            setLabelSize(header, cellWidth, cellLength);
//            header.getStyleClass().add("header-label");
//            grid.add(header, 0, row);
//        }
//    }
//
//}
//