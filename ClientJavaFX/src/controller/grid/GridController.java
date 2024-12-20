
package controller.grid;

import controller.main.MainController;
import dto.components.DtoCell;
import dto.components.DtoContainerData;
import utilities.javafx.smallparts.StringParser;

import dto.components.DtoSheetCell;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import dto.small_parts.CellLocation;
import dto.small_parts.CellLocationFactory;
import dto.small_parts.EffectiveValue;

import java.util.*;
import java.util.function.Consumer;


public class GridController {

    @FXML
    private ScrollPane gridScroller;  // Use the ScrollPane as a placeholder

    private GridPane grid = new GridPane();

    @FXML
    private Map<CellLocation, Label> cellLocationToLabel;
    private Map<String, Integer> columnToWidthDelta;
    private Map<String, Integer> rowToHeightDelta;
    private Map<CellLocation, CustomCellLabel> cellLocationToCustomCellLabel;
    private Map<String, SheetCustomization> sheetNameToCustomization;

    private MainController mainController;

    private NeighborsHandler neighborsHandler;
    private final static int DELTA_EXTENSION_GRID = 2;
    private final static int MIN_CELL_SIZE = 30;
    private final static int MAX_CELL_SIZE = 300;
    private final static int CELL_SIZE_CHANGE = 10;

    // create a fxml initilize method:
    @FXML
    public void initialize() {
        gridScroller.setStyle("-fx-background-color: #e8f0f6;"); // Replace with your desired color
        grid.setStyle("-fx-background-color: #e8f0f6;");
    }



    public GridController() {
        // Initialize the grid
        gridScroller = new ScrollPane();
        grid = new GridPane();
        cellLocationToLabel = new HashMap<>();
        columnToWidthDelta = new HashMap<>();
        rowToHeightDelta = new HashMap<>();
        sheetNameToCustomization = new HashMap<>();
        neighborsHandler = new NeighborsHandler();
    }

    public void setCustomization(String sheetName){

        if(!sheetNameToCustomization.containsKey(sheetName)){
            sheetNameToCustomization.put(sheetName, new SheetCustomization());
        }
        cellLocationToCustomCellLabel = sheetNameToCustomization.get(sheetName).getCellLocationToCustomCellLabel();
        columnToWidthDelta = sheetNameToCustomization.get(sheetName).getColumnToWidthDelta();
        rowToHeightDelta = sheetNameToCustomization.get(sheetName).getRowToHeightDelta();
        cellLocationToLabel = sheetNameToCustomization.get(sheetName).getCellLocationToLabel();
    }



    public GridPane getGrid() {
        return grid;
    }

    public void initializeEmptyGrid(DtoSheetCell sheetCell, GridPane grid, boolean isPopup) {
        // Apply styles and size settings
        grid.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ExelBasicGrid.css")).toExternalForm());

        // Set custom sizes for GridPane and ScrollPane
        grid.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        grid.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Adjust the height and margins of the gridScroller here

        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth() * DELTA_EXTENSION_GRID;
        int cellLength = sheetCell.getCellLength() * DELTA_EXTENSION_GRID;

        clearGrid(grid);
        setupColumnConstraints(grid, numCols, cellWidth);
        setupRowConstraints(grid, numRows, cellLength);
        addColumnHeaders(grid, numCols, cellWidth, cellLength);
        addRowHeaders(grid, numRows, cellWidth, cellLength);

        if (!isPopup) {
            gridScroller.setContent(grid);  // Set the grid content to the scroller
        }
    }

    public Map<CellLocation, Label> initializeGrid(DtoSheetCell sheetCell) {


        neighborsHandler = new NeighborsHandler();
        initializeEmptyGrid(sheetCell, grid, false);

        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int baseCellWidth = sheetCell.getCellWidth() * DELTA_EXTENSION_GRID;
        int baseCellLength = sheetCell.getCellLength() * DELTA_EXTENSION_GRID;

        int baseCellWidthForCol = 0;
        int baseCellLengthForRow = 0;

        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();

        // Set column and row sizes from deltas if available
        for (int col = 1; col <= numCols; col++) {
            String columnKey = Character.toString((char) ('A' + col - 1));
            int adjustedWidth = columnToWidthDelta.getOrDefault(columnKey, baseCellWidthForCol);
            updateColumnConstraints(col, adjustedWidth);
        }
        for (int row = 1; row <= numRows; row++) {
            String rowKey = String.valueOf(row);
            int adjustedHeight = rowToHeightDelta.getOrDefault(rowKey, baseCellLengthForRow);
            updateRowConstraints(row, adjustedHeight);
        }

        // Add labels to grid with adjusted sizes
        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {
                CellLocation location = CellLocationFactory.fromCellId((char) ('A' + col - 1), String.valueOf(row));
                Label cellLabel = cellLocationToLabel.get(location);

                if (cellLabel == null) {
                    cellLabel = createNewLabel(location, viewSheetCell, baseCellWidth, baseCellLength, row, col);
                } else {
                    updateExistingLabel(cellLabel, location, viewSheetCell);
                }

                grid.add(cellLabel, col, row);
            }
        }

        return cellLocationToLabel;
    }

    private Label createNewLabel(CellLocation location, Map<CellLocation, EffectiveValue> viewSheetCell,
                                 int cellWidth, int cellLength, int row, int col) {
        Label cellLabel = new Label();
        cellLabel.getStyleClass().add("cell-label");

        CustomCellLabel customCellLabel = cellLocationToCustomCellLabel.computeIfAbsent(location, loc -> {
            CustomCellLabel newLabel = new CustomCellLabel(cellLabel);
            newLabel.applyDefaultStyles();
            newLabel.setAlignment(Pos.CENTER);
            newLabel.setTextAlignment(TextAlignment.CENTER);
            return newLabel;
        });

        setLabelSize(cellLabel, cellWidth, cellLength);
        cellLabel.setId(createCellId(col, row));

        EffectiveValue effectiveValue = viewSheetCell.get(location);
        if (effectiveValue != null) {
            cellLabel.setText(StringParser.convertValueToLabelText(effectiveValue));
        }

        addEventHandlers(cellLabel);
        cellLocationToLabel.put(location, cellLabel);
        return cellLabel;
    }

    private void updateExistingLabel(Label cellLabel, CellLocation location, Map<CellLocation, EffectiveValue> viewSheetCell) {
        EffectiveValue effectiveValue = viewSheetCell.get(location);
        if (effectiveValue != null) {
            if (cellLabel.textProperty().isBound()) {
                cellLabel.textProperty().unbind();
            }
            cellLabel.setText(StringParser.convertValueToLabelText(effectiveValue));
        }

        CustomCellLabel customCellLabel = cellLocationToCustomCellLabel.get(location);
    }

    private void onCellMouseEntered(String location) {
        CustomCellLabel customCellLabel = cellLocationToCustomCellLabel.get(CellLocationFactory.fromCellId(location));
        customCellLabel.setBorderColor(Color.RED, true);
    }

    private void onCellMouseExited(String id) {
        CustomCellLabel customCellLabel = cellLocationToCustomCellLabel.get(CellLocationFactory.fromCellId(id));
        customCellLabel.setBorderColor(Color.BLACK, false);
    }

    private String createCellId(int col, int row) {
        return (char) ('A' + col - 1) + String.valueOf(row);
    }

    private void addEventHandlers(Label cell) {
        cell.setOnMouseEntered(event -> onCellMouseEntered(cell.getId()));
        cell.setOnMouseExited(event -> onCellMouseExited(cell.getId()));
        cell.setOnMouseClicked(event -> onCellClicked(cell.getId()));
    }

    private void onCellClicked(String location) {
        mainController.cellClicked(location);
    }

    public void changeTextAlignment(String alignment, String selectedColumnLabel) {
        int selectedColumnIndex = selectedColumnLabel.charAt(0) - 'A' + 1;
        Pos pos = determinePos(alignment);
        TextAlignment textAlign = determineTextAlign(alignment);

        for (Node node : grid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            if (colIndex != null && colIndex == selectedColumnIndex) {
                if (GridPane.getRowIndex(node) != 0) { // Skip headers
                    CellLocation cellLocation = CellLocationFactory.fromCellId(selectedColumnLabel.charAt(0), String.valueOf(GridPane.getRowIndex(node)));
                    CustomCellLabel labelCell = cellLocationToCustomCellLabel.get(cellLocation);

                    labelCell.setAlignment(pos);
                    labelCell.setTextAlignment(textAlign);
                }
            }
        }
    }

    private Pos determinePos(String alignment) {
        switch (alignment.toLowerCase()) {
            case "left":
                return Pos.CENTER_LEFT;
            case "right":
                return Pos.CENTER_RIGHT;
            default:
                return Pos.CENTER;
        }
    }

    private TextAlignment determineTextAlign(String alignment) {
        switch (alignment.toLowerCase()) {
            case "left":
                return TextAlignment.LEFT;
            case "right":
                return TextAlignment.RIGHT;
            default:
                return TextAlignment.CENTER;
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void setupColumnConstraints(GridPane grid, int numCols, int cellWidth) {
        for (int i = 0; i < numCols + 1; i++) { // +1 for header column
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setMinWidth(cellWidth);
            colConstraints.setPrefWidth(cellWidth);

            colConstraints.setHgrow(Priority.NEVER);
            grid.getColumnConstraints().add(colConstraints);
        }
    }

    private void setupRowConstraints(GridPane grid, int numRows, int cellLength) {
        for (int i = 0; i < numRows + 1; i++) { // +1 for header row
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(cellLength);
            rowConstraints.setPrefHeight(cellLength);
            rowConstraints.setMaxHeight(cellLength);
            rowConstraints.setVgrow(Priority.ALWAYS);
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

    public void initializeCirclePopUp(GridPane grid, DtoSheetCell sheetCell, List<CellLocation> neighbors) {

        initializeEmptyGrid(sheetCell, grid, true);
        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth();
        int cellLength = sheetCell.getCellLength();
        cellWidth = cellWidth * DELTA_EXTENSION_GRID;
        cellLength = cellLength * DELTA_EXTENSION_GRID;


        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();

        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {
                Label cell = new Label();

                CustomCellLabel customCellLabel = new CustomCellLabel(cell);
                customCellLabel.applyDefaultStyles();
                customCellLabel.setAlignment(Pos.CENTER);
                customCellLabel.setTextAlignment(TextAlignment.CENTER);
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

    public Map<CellLocation,CustomCellLabel> initializeVersionPopupGrid(GridPane grid, DtoSheetCell sheetCell) {

        initializeEmptyGrid(sheetCell, grid, true);
        Map<CellLocation,CustomCellLabel> cellLocationCustomCellLabelMap = new HashMap<>();
        applyGridColors(grid);

        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth();
        int cellLength = sheetCell.getCellLength();
        cellWidth = cellWidth * DELTA_EXTENSION_GRID;
        cellLength = cellLength * DELTA_EXTENSION_GRID;


        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();

        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {

                char colChar = (char) ('A' + col - 1);
                String rowString = String.valueOf(row);
                String location = colChar + rowString;
                CellLocation cellLocation = CellLocationFactory.fromCellId(location);

                Label newCellLabel = new Label();
                CustomCellLabel newCustomCellLabel = new CustomCellLabel(newCellLabel);

                newCustomCellLabel.applyDefaultStyles();
                newCustomCellLabel.setBackgroundColor(Color.WHITE);
                newCustomCellLabel.setTextColor(Color.BLACK);
                newCustomCellLabel.setAlignment(Pos.CENTER);
                newCustomCellLabel.setTextAlignment(TextAlignment.CENTER);


                setLabelSize(newCellLabel, cellWidth, cellLength);

                // Bind the Label's textProperty to the EffectiveValue
                newCellLabel.setId(location);
                EffectiveValue effectiveValue = viewSheetCell.get(cellLocation);
                if (effectiveValue != null) {
                    String textForLabel = StringParser.convertValueToLabelText(effectiveValue);
                    newCellLabel.setText(textForLabel);
                }

                cellLocationCustomCellLabelMap.put(cellLocation, newCustomCellLabel);
                grid.add(newCellLabel, col, row);
            }
        }
        return cellLocationCustomCellLabelMap;
    }

    public void initializeSortPopupGrid(GridPane grid, DtoContainerData dtoContainerData) {

        DtoSheetCell sheetCell = dtoContainerData.getDtoSheetCell();

        initializeEmptyGrid(sheetCell, grid, true);

        applyGridColors(grid);

        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth();
        int cellLength = sheetCell.getCellLength();
        cellWidth = cellWidth * DELTA_EXTENSION_GRID;
        cellLength = cellLength * DELTA_EXTENSION_GRID;


        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();

        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {

                char colChar = (char) ('A' + col - 1);
                String rowString = String.valueOf(row);
                String location = colChar + rowString;
                CellLocation cellLocation = CellLocationFactory.fromCellId(location);


                CellLocation oldCellLocation = dtoContainerData.getOldCellLocation(cellLocation);
                if (oldCellLocation == null) {
                    oldCellLocation = cellLocation;
                }

                Label newCellLabel = new Label();
                CustomCellLabel newCustomCellLabel = new CustomCellLabel(newCellLabel);

                newCustomCellLabel.applyDefaultStyles();
                newCustomCellLabel.setAlignment(cellLocationToCustomCellLabel.get(oldCellLocation).getAlignment());
                newCustomCellLabel.setTextAlignment(cellLocationToCustomCellLabel.get(oldCellLocation).getTextAlignment());
                newCustomCellLabel.setBackgroundColor(cellLocationToCustomCellLabel.get(oldCellLocation).getBackgroundColor());
                newCustomCellLabel.setTextColor(cellLocationToCustomCellLabel.get(oldCellLocation).getTextColor());


                setLabelSize(newCellLabel, cellWidth, cellLength);

                // Bind the Label's textProperty to the EffectiveValue
                newCellLabel.setId(location);
                EffectiveValue effectiveValue = viewSheetCell.get(cellLocation);
                if (effectiveValue != null) {
                    String textForLabel = StringParser.convertValueToLabelText(effectiveValue);
                    newCellLabel.setText(textForLabel);
                }

                grid.add(newCellLabel, col, row);
            }
        }
    }

    private void clearGrid(GridPane grid) {
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        grid.getChildren().clear();
    }

    public void showAffectedCells(List<CellLocation> requestedRange) {
        neighborsHandler.showAffectedCells(requestedRange, cellLocationToLabel, cellLocationToCustomCellLabel);
    }

    public void showNeighbors(DtoCell cell) {
        neighborsHandler.showNeighbors(cell, cellLocationToLabel, cellLocationToCustomCellLabel);
    }

    public void clearAllHighlights() {
        neighborsHandler.clearAllHighlights(cellLocationToLabel, cellLocationToCustomCellLabel);
    }

    public void changingGridConstraints(String RowOrColumn, int increaseOrDecrease) {

        // Calculate the value to change
        int valueToChange = increaseOrDecrease * CELL_SIZE_CHANGE;
        boolean isColumn = isColumn(RowOrColumn);

        int index = isColumn ? RowOrColumn.charAt(0) - 'A' + 1 : Integer.parseInt(RowOrColumn);

        if (isColumn) {
            updateColumnConstraints(index, valueToChange);

            if (columnToWidthDelta.containsKey(RowOrColumn)) {
                columnToWidthDelta.put(RowOrColumn, columnToWidthDelta.get(RowOrColumn) + valueToChange);
            } else {
                columnToWidthDelta.put(RowOrColumn, valueToChange);
            }

        } else {
            updateRowConstraints(index, valueToChange);

            if (rowToHeightDelta.containsKey(RowOrColumn)) {
                rowToHeightDelta.put(RowOrColumn, rowToHeightDelta.get(RowOrColumn) + valueToChange);
            } else {
                rowToHeightDelta.put(RowOrColumn, valueToChange);
            }
        }
    }

    private boolean isColumn(String RowOrColumn) {
        return RowOrColumn.charAt(0) >= 'A' && RowOrColumn.charAt(0) <= 'Z';
    }

    private void updateColumnConstraints(int columnIndex, int valueToChange) {
        ColumnConstraints columnConstraint = grid.getColumnConstraints().get(columnIndex);
        double newWidth = columnConstraint.getPrefWidth() + valueToChange;
        newWidth = Math.max(MIN_CELL_SIZE, Math.min(newWidth, MAX_CELL_SIZE));
        columnConstraint.setPrefWidth(newWidth);
        columnConstraint.setMinWidth(newWidth);
        columnConstraint.setMaxWidth(newWidth);

        updateColumnHeadersAndCells(columnIndex, (int) newWidth);
    }

    private void updateRowConstraints(int rowIndex, int valueToChange) {
        RowConstraints rowConstraint = grid.getRowConstraints().get(rowIndex);
        double newHeight = rowConstraint.getPrefHeight() + valueToChange;
        newHeight = Math.max(MIN_CELL_SIZE, Math.min(newHeight, MAX_CELL_SIZE));
        rowConstraint.setPrefHeight(newHeight);
        rowConstraint.setMinHeight(newHeight);
        rowConstraint.setMaxHeight(newHeight);

        updateRowHeadersAndCells(rowIndex, (int) newHeight);
    }

    private void updateColumnHeadersAndCells(int columnIndex, int newWidth) {
        for (Node node : grid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            if (colIndex != null && colIndex == columnIndex) {
                if (GridPane.getRowIndex(node) == 0) { // Header row
                    Label header = (Label) node;
                    setLabelSize(header, newWidth, (int) header.getPrefHeight());
                } else { // Data cells
                    Label cell = (Label) node;
                    setLabelSize(cell, newWidth, (int) cell.getPrefHeight());
                }
            }
        }
    }

    private void updateRowHeadersAndCells(int rowIndex, int newHeight) {
        for (Node node : grid.getChildren()) {
            Integer row = GridPane.getRowIndex(node);
            if (row != null && row == rowIndex) {
                if (GridPane.getColumnIndex(node) == 0) { // Header column
                    Label header = (Label) node;
                    setLabelSize(header, (int) header.getPrefWidth(), newHeight);
                } else { // Data cells
                    Label cell = (Label) node;
                    setLabelSize(cell, (int) cell.getPrefWidth(), newHeight);
                }
            }
        }
    }

    public void changeBackgroundTextColor(Color value, String location) {

        Label cell = cellLocationToLabel.get(CellLocationFactory.fromCellId(location));
        Pos alignment = cell.getAlignment();

        CustomCellLabel customCellLabel = cellLocationToCustomCellLabel.get(CellLocationFactory.fromCellId(location));
        // CustomCellLabel customCellLabel = new CustomCellLabel(cell);
        customCellLabel.setBackgroundColor(value);
        customCellLabel.setAlignment(alignment);
    }

    public void changeTextColor(Color value, String location) {
        Label cell = cellLocationToLabel.get(CellLocationFactory.fromCellId(location));
        if (cell != null) {
            CustomCellLabel customCellLabel = cellLocationToCustomCellLabel.get(CellLocationFactory.fromCellId(location));
            //CustomCellLabel customCellLabel = new CustomCellLabel(cell);
            customCellLabel.setTextColor(value);
            //cell.setTextFill(value);
        }
    }

    public void initializeFilterPopupGrid(GridPane grid, DtoContainerData dtoContainerData) {

        DtoSheetCell sheetCell = dtoContainerData.getDtoSheetCell();

        initializeEmptyGrid(sheetCell, grid, true);

        applyGridColors(grid);

        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth();
        int cellLength = sheetCell.getCellLength();
        cellWidth = cellWidth * DELTA_EXTENSION_GRID;
        cellLength = cellLength * DELTA_EXTENSION_GRID;


        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();

        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {

                char colChar = (char) ('A' + col - 1);
                String rowString = String.valueOf(row);
                String location = colChar + rowString;
                CellLocation cellLocation = CellLocationFactory.fromCellId(location);


                CellLocation oldCellLocation = dtoContainerData.getOldCellLocation(cellLocation);
                if (oldCellLocation == null) {
                    oldCellLocation = cellLocation;
                }

                Label newCellLabel = new Label();
                CustomCellLabel newCustomCellLabel = new CustomCellLabel(newCellLabel);

                newCustomCellLabel.applyDefaultStyles();
                if (cellLocationToCustomCellLabel.get(oldCellLocation) != null) {
                    newCustomCellLabel.setAlignment(cellLocationToCustomCellLabel.get(oldCellLocation).getAlignment());
                    newCustomCellLabel.setTextAlignment(cellLocationToCustomCellLabel.get(oldCellLocation).getTextAlignment());
                    newCustomCellLabel.setBackgroundColor(cellLocationToCustomCellLabel.get(oldCellLocation).getBackgroundColor());
                    newCustomCellLabel.setTextColor(cellLocationToCustomCellLabel.get(oldCellLocation).getTextColor());
                }

                setLabelSize(newCellLabel, cellWidth, cellLength);

                // Bind the Label's textProperty to the EffectiveValue
                newCellLabel.setId(location);
                EffectiveValue effectiveValue = viewSheetCell.get(cellLocation);
                if (effectiveValue != null) {
                    String textForLabel = StringParser.convertValueToLabelText(effectiveValue);
                    newCellLabel.setText(textForLabel);
                }

                grid.add(newCellLabel, col, row);
            }
        }
    }

    public Map<CellLocation, Label> initializeRunTimeAnalysisPopupGrid(GridPane grid,
                                                                       DtoSheetCell sheetCell,
                                                                       Consumer<CellLocation> labelClickConsumer, List<CellLocation> cellLocations) {

        Map<CellLocation, Label> cellLocationToLabel = new HashMap<>();
        Map<CellLocation, CustomCellLabel> cellLocationToCustomCellLabel = new HashMap<>();

        initializeEmptyGrid(sheetCell, grid, true);

        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth();
        int cellLength = sheetCell.getCellLength();
        cellWidth = cellWidth * DELTA_EXTENSION_GRID;
        cellLength = cellLength * DELTA_EXTENSION_GRID;


        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();

        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {

                char colChar = (char) ('A' + col - 1);
                String rowString = String.valueOf(row);
                String location = colChar + rowString;
                CellLocation cellLocation = CellLocationFactory.fromCellId(location);

                Label newCellLabel = new Label();
                CustomCellLabel newCustomCellLabel = new CustomCellLabel(newCellLabel);
                cellLocationToCustomCellLabel.put(cellLocation, newCustomCellLabel);

                newCustomCellLabel.applyDefaultStyles();
                newCustomCellLabel.setBackgroundColor(Color.WHITE);
                newCustomCellLabel.setTextColor(Color.BLACK);
                newCustomCellLabel.setAlignment(Pos.CENTER);
                newCustomCellLabel.setTextAlignment(TextAlignment.CENTER);

                if(cellLocations.contains(cellLocation)){
                    newCustomCellLabel.setBackgroundColor(Color.LIGHTGRAY);
                }

                setLabelSize(newCellLabel, cellWidth, cellLength);

                newCellLabel.setId(location);
                EffectiveValue effectiveValue = viewSheetCell.get(cellLocation);
                if (effectiveValue != null) {
                    String textForLabel = StringParser.convertValueToLabelText(effectiveValue);
                    newCellLabel.setText(textForLabel);
                }

                DtoCell dtoCell = sheetCell.getRequestedCell(location);

                newCellLabel.setOnMouseClicked(event -> onRunTimeCellClicked(dtoCell, cellLocationToLabel,
                        cellLocationToCustomCellLabel, labelClickConsumer, cellLocation));

                cellLocationToLabel.put(cellLocation, newCellLabel);
                grid.add(newCellLabel, col, row);
            }
        }
        return cellLocationToLabel;
    }

    private void onRunTimeCellClicked(DtoCell dtoCell, Map<CellLocation,
            Label> cellLocationToLabel,
                                      Map<CellLocation, CustomCellLabel> cellLocationToCustomCellLabel,
                                      Consumer<CellLocation> labelClickConsumer, CellLocation cellLocation) {

        if (dtoCell != null) {
            neighborsHandler.clearAllHighlights(cellLocationToLabel, cellLocationToCustomCellLabel); // clears highlighted cells
            neighborsHandler.showNeighbors(dtoCell, cellLocationToLabel, cellLocationToCustomCellLabel); // highlight neighbors in green color
        }

        labelClickConsumer.accept(cellLocation);
    }


    private void applyGridColors(GridPane grid) {
        gridScroller.setStyle("-fx-background-color: #e8f0f6;"); // Replace with your desired color
        grid.setStyle("-fx-background-color: #e8f0f6;");
    }

    public void resetCustomizationInAllSheets() {
        sheetNameToCustomization.forEach((sheetName, customization) -> {
            customization.getCellLocationToCustomCellLabel().clear();
            customization.getColumnToWidthDelta().clear();
            customization.getRowToHeightDelta().clear();
            customization.getCellLocationToLabel().clear();
            sheetName = null;
        });
    }

    public Map<CellLocation, CustomCellLabel> initializeGridWithChangedValues(Set<CellLocation> newValueLocations,
                                                                              GridPane grid, String newValue,
                                                                              DtoSheetCell sheetCell) {


            initializeEmptyGrid(sheetCell, grid, true);

            applyGridColors(grid);

            int numCols = sheetCell.getNumberOfColumns();
            int numRows = sheetCell.getNumberOfRows();
            int cellWidth = sheetCell.getCellWidth();
            int cellLength = sheetCell.getCellLength();
            cellWidth = cellWidth * DELTA_EXTENSION_GRID;
            cellLength = cellLength * DELTA_EXTENSION_GRID;


            Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();
            Map<CellLocation, Label> cellLocationToLabel = new HashMap<>();
            Map<CellLocation, CustomCellLabel> cellLocationCustomCellLabelMap = new HashMap<>();

            for (int row = 1; row <= numRows; row++) {
                for (int col = 1; col <= numCols; col++) {

                    char colChar = (char) ('A' + col - 1);
                    String rowString = String.valueOf(row);
                    String location = colChar + rowString;
                    CellLocation cellLocation = CellLocationFactory.fromCellId(location);

                    Label newCellLabel = new Label();
                    CustomCellLabel newCustomCellLabel = new CustomCellLabel(newCellLabel);

                    newCustomCellLabel.applyDefaultStyles();
                    newCustomCellLabel.setBackgroundColor(Color.WHITE);
                    newCustomCellLabel.setTextColor(Color.BLACK);
                    newCustomCellLabel.setAlignment(Pos.CENTER);
                    newCustomCellLabel.setTextAlignment(TextAlignment.CENTER);


                    setLabelSize(newCellLabel, cellWidth, cellLength);

                    // Bind the Label's textProperty to the EffectiveValue
                    newCellLabel.setId(location);
                    EffectiveValue effectiveValue = viewSheetCell.get(cellLocation);
                    if (effectiveValue != null) {
                        String textForLabel = StringParser.convertValueToLabelText(effectiveValue);
                        newCellLabel.setText(textForLabel);

                        if(newValueLocations.contains(cellLocation)){
                            newCellLabel.setText(newValue);
                        }

                    }

                    DtoCell dtoCell = sheetCell.getRequestedCell(location);
                    cellLocationToLabel.put(cellLocation, newCellLabel);
                    cellLocationCustomCellLabelMap.put(cellLocation, newCustomCellLabel);


                    newCellLabel.setOnMouseClicked(event -> onCellClickedOnNewGrid(dtoCell, cellLocationToLabel,
                            cellLocationCustomCellLabelMap));

                    grid.add(newCellLabel, col, row);
                }
            }

            return cellLocationCustomCellLabelMap;
        }



    public Map<CellLocation, CustomCellLabel> initializeOriginalPopupGrid(GridPane grid, DtoSheetCell sheetCell) {

        initializeEmptyGrid(sheetCell, grid, true);

        applyGridColors(grid);

        int numCols = sheetCell.getNumberOfColumns();
        int numRows = sheetCell.getNumberOfRows();
        int cellWidth = sheetCell.getCellWidth();
        int cellLength = sheetCell.getCellLength();
        cellWidth = cellWidth * DELTA_EXTENSION_GRID;
        cellLength = cellLength * DELTA_EXTENSION_GRID;


        Map<CellLocation, EffectiveValue> viewSheetCell = sheetCell.getViewSheetCell();
        Map<CellLocation, CustomCellLabel> cellLocationCustomCellLabelMap = new HashMap<>();
        Map<CellLocation, Label> cellLocationToLabel = new HashMap<>();

        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {

                char colChar = (char) ('A' + col - 1);
                String rowString = String.valueOf(row);
                String location = colChar + rowString;
                CellLocation cellLocation = CellLocationFactory.fromCellId(location);

                Label newCellLabel = new Label();
                CustomCellLabel newCustomCellLabel = new CustomCellLabel(newCellLabel);

                newCustomCellLabel.applyDefaultStyles();
                newCustomCellLabel.setBackgroundColor(Color.WHITE);
                newCustomCellLabel.setTextColor(Color.BLACK);
                newCustomCellLabel.setAlignment(Pos.CENTER);
                newCustomCellLabel.setTextAlignment(TextAlignment.CENTER);


                setLabelSize(newCellLabel, cellWidth, cellLength);

                // Bind the Label's textProperty to the EffectiveValue
                newCellLabel.setId(location);
                EffectiveValue effectiveValue = viewSheetCell.get(cellLocation);
                if (effectiveValue != null) {
                    String textForLabel = StringParser.convertValueToLabelText(effectiveValue);
                    newCellLabel.setText(textForLabel);
                }

                DtoCell dtoCell = sheetCell.getRequestedCell(location);
                cellLocationToLabel.put(cellLocation, newCellLabel);
                cellLocationCustomCellLabelMap.put(cellLocation, newCustomCellLabel);

                newCellLabel.setOnMouseClicked(event -> onCellClickedOnNewGrid(dtoCell, cellLocationToLabel,
                        cellLocationCustomCellLabelMap));

                grid.add(newCellLabel, col, row);
            }
        }

        return cellLocationCustomCellLabelMap;
    }



    private void onCellClickedOnNewGrid(DtoCell dtoCell, Map<CellLocation, Label> cellLocationToLabel,
                                        Map<CellLocation, CustomCellLabel> cellLocationToCustomCellLabel){

        if (dtoCell != null) {
            neighborsHandler.clearAllHighlights(cellLocationToLabel, cellLocationToCustomCellLabel); // clears highlighted cells
            neighborsHandler.showNeighbors(dtoCell, cellLocationToLabel, cellLocationToCustomCellLabel); // highlight neighbors in green color
        }
    }
}


