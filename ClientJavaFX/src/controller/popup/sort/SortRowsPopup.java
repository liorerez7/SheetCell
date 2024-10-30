package controller.popup.sort;

import controller.grid.CustomCellLabel;
import controller.grid.GridController;
import controller.popup.PopUpWindowsManager;
import dto.components.DtoContainerData;
import dto.components.DtoSheetCell;
import dto.small_parts.CellLocation;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utilities.javafx.smallparts.SortRowsData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortRowsPopup {

    private static final double BUTTON_WIDTH = 200;
    private static final double LEFT_CONTROL_WIDTH = 220;
    private static final double GRID_CONTAINER_HEIGHT = 335;
    private static final double GRID_CONTAINER_WIDTH = 1120;
    private static final double COLUMN_COMBOBOX_WIDTH = 180;
    private static final double LIST_VIEW_HEIGHT = 100;
    private static final double VBOX_SPACING = 10;
    private static final double MAIN_LAYOUT_SPACING = 15;
    private static final double ACTION_BUTTON_SPACING = 10;
    private static final Insets MAIN_LAYOUT_PADDING = new Insets(15);
    private static final Insets ACTION_BUTTON_PADDING = new Insets(10, 0, 0, 0);
    private static final double SCENE_WIDTH = 1510;
    private static final double SCENE_HEIGHT = 750;
    private static final double PADDING = 10;

    private final Stage popupStage;
    private final VBox leftControlsLayout;
    private final GridPane rangeSelectionPane;
    private final GridPane columnSelectionPane;
    private final SortRowsData sortRowsData;
    private final TextField rangeFromField;
    private final TextField rangeToField;
    private final ComboBox<String> columnsComboBox;
    private final ListView<String> selectedColumnsListView;
    private final Button nextButton;
    private final Button addColumnButton;
    private final Button removeColumnButton;
    private final Button showSortedGridButton;
    private final Button backToChooseRangesButton;
    private final GridController gridController;
    private final DtoSheetCell dtoSheetCell;
    private VBox originalGridContainer;
    private VBox sortedGridContainer;
    private Map<CellLocation, CustomCellLabel> cellLocationCustomCellLabelMap = new HashMap<>();
    private final PopUpWindowsManager popUpWindowsManager;

    public SortRowsPopup(DtoSheetCell dtoSheetCell, GridController gridController, PopUpWindowsManager popUpWindowsManager) {

        this.dtoSheetCell = dtoSheetCell;
        this.gridController = gridController;
        this.popUpWindowsManager = popUpWindowsManager;

        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Insert Sorting Parameters");

        leftControlsLayout = new VBox(VBOX_SPACING);
        leftControlsLayout.setPadding(new Insets(10));
        leftControlsLayout.getStyleClass().add("pane");

        rangeSelectionPane = new GridPane();
        columnSelectionPane = new GridPane();

        rangeSelectionPane.setHgap(PADDING);
        rangeSelectionPane.setVgap(PADDING);
        columnSelectionPane.setHgap(PADDING);
        columnSelectionPane.setVgap(PADDING);

        rangeFromField = new TextField();
        rangeFromField.getStyleClass().add("text-field");
        rangeToField = new TextField();
        rangeToField.getStyleClass().add("text-field");

        columnsComboBox = new ComboBox<>();
        columnsComboBox.setPromptText("Select a column");
        columnsComboBox.getStyleClass().add("combo-box");

        selectedColumnsListView = new ListView<>();
        selectedColumnsListView.setPrefHeight(LIST_VIEW_HEIGHT);
        selectedColumnsListView.getStyleClass().add("list-view");

        nextButton = new Button("Next");
        nextButton.setDisable(true);
        nextButton.getStyleClass().add("button");

        addColumnButton = new Button("Add Column");
        addColumnButton.setDisable(true);
        addColumnButton.getStyleClass().add("button");

        removeColumnButton = new Button("Remove Column");
        removeColumnButton.setDisable(true);
        removeColumnButton.getStyleClass().add("button");

        showSortedGridButton = new Button("Show Sorted Grid");
        showSortedGridButton.setDisable(true);
        showSortedGridButton.getStyleClass().add("button");

        backToChooseRangesButton = new Button("Back to Ranges");
        backToChooseRangesButton.getStyleClass().add("button");

        sortRowsData = new SortRowsData();

        setupLayout();
    }

    public SortRowsData show() {
        popupStage.showAndWait();
        return sortRowsData;
    }

    private void setupLayout() {
        rangeSelectionPane.add(new Label("Range-From (e.g, A2):"), 0, 0);
        rangeSelectionPane.add(rangeFromField, 0, 1);
        rangeSelectionPane.add(new Label("Range-To (e.g, C5):"), 0, 2);
        rangeSelectionPane.add(rangeToField, 0, 3);
        rangeSelectionPane.add(nextButton, 0, 4);

        rangeFromField.textProperty().addListener((obs, oldVal, newVal) -> updateNextButtonState());
        rangeToField.textProperty().addListener((obs, oldVal, newVal) -> updateNextButtonState());

        Label columnLabel = new Label("Select columns to sort by:");
        columnSelectionPane.add(columnLabel, 0, 0);

        columnsComboBox.setPrefWidth(COLUMN_COMBOBOX_WIDTH);
        columnSelectionPane.add(columnsComboBox, 0, 1);
        addColumnButton.setPrefWidth(COLUMN_COMBOBOX_WIDTH);
        columnSelectionPane.add(addColumnButton, 0, 2);

        selectedColumnsListView.setPrefSize(COLUMN_COMBOBOX_WIDTH, LIST_VIEW_HEIGHT);
        columnSelectionPane.add(selectedColumnsListView, 0, 3);

        showSortedGridButton.setPrefWidth(BUTTON_WIDTH);
        backToChooseRangesButton.setPrefWidth(BUTTON_WIDTH);
        removeColumnButton.setPrefWidth(BUTTON_WIDTH);

        VBox actionButtonsLayout = new VBox(ACTION_BUTTON_SPACING, backToChooseRangesButton, removeColumnButton, showSortedGridButton);
        actionButtonsLayout.setPadding(ACTION_BUTTON_PADDING);
        columnSelectionPane.add(actionButtonsLayout, 0, 4);

        leftControlsLayout.setPrefWidth(LEFT_CONTROL_WIDTH);
        leftControlsLayout.getChildren().addAll(rangeSelectionPane, columnSelectionPane);
        columnSelectionPane.setVisible(false);

        originalGridContainer = new VBox(VBOX_SPACING, new Label("Original Grid"), createOriginalGrid());
        sortedGridContainer = new VBox(VBOX_SPACING, new Label("Sorted Grid"));
        sortedGridContainer.setVisible(false);

        VBox gridsDisplay = new VBox(20, originalGridContainer, sortedGridContainer);

        HBox mainLayout = new HBox(MAIN_LAYOUT_SPACING, leftControlsLayout, gridsDisplay);
        mainLayout.setPadding(MAIN_LAYOUT_PADDING);

        nextButton.setOnAction(e -> finalizeRange());
        addColumnButton.setOnAction(e -> addColumnToList());
        removeColumnButton.setOnAction(e -> removeColumnFromList());
        showSortedGridButton.setOnAction(e -> showSortedGrid());
        backToChooseRangesButton.setOnAction(e -> resetToRangeSelection());

        columnsComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            addColumnButton.setDisable(newVal == null || newVal.isEmpty());
        });

        selectedColumnsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            removeColumnButton.setDisable(newVal == null || newVal.isEmpty());
        });

        Scene scene = new Scene(mainLayout, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("sortpopup.css").toExternalForm());
        popupStage.setScene(scene);
    }


    private void updateNextButtonState() {
        nextButton.setDisable(rangeFromField.getText().isEmpty() || rangeToField.getText().isEmpty());
    }

    private void finalizeRange() {
        String rangeFrom = rangeFromField.getText().trim().toUpperCase();
        String rangeTo = rangeToField.getText().trim().toUpperCase();

        if (!isValidCellFormat(rangeFrom) || !isValidCellFormat(rangeTo)) {
            popUpWindowsManager.createErrorPopup("Please enter valid range values in the format <Column><Row> (e.g., A2).", "Invalid Input");
            return;
        }

        char fromCol = rangeFrom.charAt(0);
        char toCol = rangeTo.charAt(0);
        int fromRow = Integer.parseInt(rangeFrom.substring(1));
        int toRow = Integer.parseInt(rangeTo.substring(1));

        if (fromCol > toCol || fromRow > toRow) {
            popUpWindowsManager.createErrorPopup("Invalid range. Ensure 'To' cell is after 'From' cell.", "Invalid Input");
            return;
        }

        int numberOfCols = dtoSheetCell.getNumberOfColumns();
        int numberOfRows = dtoSheetCell.getNumberOfRows();

        if (!isWithinBounds(fromCol, toCol, fromRow, toRow, numberOfCols, numberOfRows)) {
            popUpWindowsManager.createErrorPopup("Invalid range values. Please ensure the range is within sheet bounds.", "Invalid Input");
            return;
        }

        setGrayBackgroundForCells(rangeFrom, rangeTo);

        populateColumnComboBox(fromCol, toCol);

        // Disable range fields
        rangeFromField.setDisable(true);
        rangeToField.setDisable(true);
        nextButton.setDisable(true);

        // Enable column selection and make the section visible
        columnsComboBox.setDisable(false);
        addColumnButton.setDisable(true);
        removeColumnButton.setDisable(true);
        columnSelectionPane.setVisible(true);
    }

    // Helper to validate cell format (e.g., A1, B2)
    private boolean isValidCellFormat(String cell) {
        return cell.matches("^[A-Z][1-9][0-9]*$");
    }

    // Helper to check range bounds
    private boolean isWithinBounds(char fromCol, char toCol, int fromRow, int toRow, int numberOfCols, int numberOfRows) {
        return fromCol >= 'A' && fromCol <= (char)('A' + numberOfCols - 1) &&
                toCol >= 'A' && toCol <= (char)('A' + numberOfCols - 1) &&
                fromRow >= 1 && fromRow <= numberOfRows &&
                toRow >= 1 && toRow <= numberOfRows;
    }

    // Helper to populate the columns combo box within a specified column range
    private void populateColumnComboBox(char fromCol, char toCol) {
        columnsComboBox.getItems().clear();
        for (char c = fromCol; c <= toCol; c++) {
            columnsComboBox.getItems().add(String.valueOf(c));
        }
    }


    private void addColumnToList() {
        String selectedColumn = columnsComboBox.getSelectionModel().getSelectedItem();
        if (selectedColumn != null && !selectedColumnsListView.getItems().contains(selectedColumn)) {
            selectedColumnsListView.getItems().add(selectedColumn);
            columnsComboBox.getSelectionModel().clearSelection();
            showSortedGridButton.setDisable(selectedColumnsListView.getItems().isEmpty());
            columnsComboBox.setPromptText("Select a column");
        }
    }

    private void removeColumnFromList() {
        String selectedItem = selectedColumnsListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            selectedColumnsListView.getItems().remove(selectedItem);
            showSortedGridButton.setDisable(selectedColumnsListView.getItems().isEmpty());
            columnsComboBox.setPromptText("Select a column");
        }
    }

    private void resetToRangeSelection() {
        rangeFromField.setDisable(false);
        rangeToField.setDisable(false);
        nextButton.setDisable(true);

        // Clear column selection and hide the sorted grid
        selectedColumnsListView.getItems().clear();
        columnSelectionPane.setVisible(false);
        showSortedGridButton.setDisable(true);
        sortedGridContainer.setVisible(false); // Hide sorted grid when going back to range selection

        setWhiteBackgroundForCells(rangeFromField.getText(), rangeToField.getText());

        columnsComboBox.getItems().clear();
        columnsComboBox.setDisable(true);
        addColumnButton.setDisable(true);
        removeColumnButton.setDisable(true);
    }

    private void showSortedGrid() {
        String range = rangeFromField.getText() + ".." + rangeToField.getText();
        List<String> selectedColumns = selectedColumnsListView.getItems();
        String columns = String.join(",", selectedColumns);

        sortRowsData.setRange(range);
        sortRowsData.setColumnsToSortBy(columns);

        DtoContainerData sortedData = dtoSheetCell.sortSheetCell(range, columns);
        VBox sortedGrid = createSortedGrid(sortedData);

        // Display the sorted grid in the sortedGridContainer
        sortedGridContainer.getChildren().clear();
        sortedGridContainer.getChildren().addAll(new Label("Sorted Grid"), sortedGrid);
        sortedGridContainer.setVisible(true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    private VBox createSortedGrid(DtoContainerData sortedData) {
        GridPane sortedGrid = new GridPane();
        sortedGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");

        gridController.initializeSortPopupGrid(sortedGrid, sortedData);

        ScrollPane gridScrollPane = new ScrollPane(sortedGrid);
        gridScrollPane.setFitToWidth(true);
        gridScrollPane.setFitToHeight(true);
        gridScrollPane.setPrefSize(GRID_CONTAINER_WIDTH, GRID_CONTAINER_HEIGHT);

        VBox gridContainer = new VBox(gridScrollPane);
        gridContainer.setPadding(new Insets(PADDING));
        gridContainer.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");
        return gridContainer;
    }

    private VBox createOriginalGrid() {
        GridPane originalGrid = new GridPane();
        originalGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");

        cellLocationCustomCellLabelMap = gridController.initializeOriginalPopupGrid(originalGrid, dtoSheetCell);

        ScrollPane gridScrollPane = new ScrollPane(originalGrid);
        gridScrollPane.setFitToWidth(true);
        gridScrollPane.setFitToHeight(true);
        gridScrollPane.setPrefSize(GRID_CONTAINER_WIDTH, GRID_CONTAINER_HEIGHT);

        VBox gridContainer = new VBox(gridScrollPane);
        gridContainer.setPadding(new Insets(PADDING));
        gridContainer.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");
        return gridContainer;
    }


    private void setGrayBackgroundForCells(String rangeFrom, String rangeTo) {
        rangeFrom = rangeFrom.toUpperCase();
        rangeTo = rangeTo.toUpperCase();

        char rangeFromCol = rangeFrom.charAt(0);
        int rangeFromRow = Integer.parseInt(rangeFrom.substring(1));
        char rangeToCol = rangeTo.charAt(0);
        int rangeToRow = Integer.parseInt(rangeTo.substring(1));

        cellLocationCustomCellLabelMap.forEach((cellLocation, customCellLabel) -> {
            char col = cellLocation.getVisualColumn();
            int row = Integer.parseInt(cellLocation.getVisualRow());

            // Check if the cell is within the range bounds
            boolean isWithinColumnRange = col >= rangeFromCol && col <= rangeToCol;
            boolean isWithinRowRange = row >= rangeFromRow && row <= rangeToRow;

            if (isWithinColumnRange && isWithinRowRange) {
                customCellLabel.setBackgroundColor(Color.LIGHTGRAY);
            }
        });
    }

    private void setWhiteBackgroundForCells(String rangeFrom, String rangeTo) {
        rangeFrom = rangeFrom.toUpperCase();
        rangeTo = rangeTo.toUpperCase();

        char rangeFromCol = rangeFrom.charAt(0);
        int rangeFromRow = Integer.parseInt(rangeFrom.substring(1));
        char rangeToCol = rangeTo.charAt(0);
        int rangeToRow = Integer.parseInt(rangeTo.substring(1));

        cellLocationCustomCellLabelMap.forEach((cellLocation, customCellLabel) -> {
            char col = cellLocation.getVisualColumn();
            int row = Integer.parseInt(cellLocation.getVisualRow());

            // Check if the cell is within the range bounds
            boolean isWithinColumnRange = col >= rangeFromCol && col <= rangeToCol;
            boolean isWithinRowRange = row >= rangeFromRow && row <= rangeToRow;

            if (isWithinColumnRange && isWithinRowRange) {
                customCellLabel.setBackgroundColor(Color.WHITE);
            }
        });
    }
}
