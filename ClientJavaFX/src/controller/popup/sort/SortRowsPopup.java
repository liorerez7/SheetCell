package controller.popup.sort;

import controller.grid.CustomCellLabel;
import controller.grid.GridController;
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
    private VBox sortedGridContainer; // Holds the sorted grid when shown
    private Map<CellLocation, CustomCellLabel> cellLocationCustomCellLabelMap = new HashMap<>();

    public SortRowsPopup(DtoSheetCell dtoSheetCell, GridController gridController) {
        this.dtoSheetCell = dtoSheetCell;
        this.gridController = gridController;
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Insert Sorting Parameters");

        leftControlsLayout = new VBox(15);
        leftControlsLayout.setPadding(new Insets(10));
        leftControlsLayout.getStyleClass().add("pane");

        rangeSelectionPane = new GridPane();
        columnSelectionPane = new GridPane();

        rangeSelectionPane.setHgap(10);
        rangeSelectionPane.setVgap(10);
        columnSelectionPane.setHgap(10);
        columnSelectionPane.setVgap(10);

        rangeSelectionPane.getStyleClass().add("pane");
        columnSelectionPane.getStyleClass().add("pane");

        rangeFromField = new TextField();
        rangeFromField.getStyleClass().add("text-field");
        rangeToField = new TextField();
        rangeToField.getStyleClass().add("text-field");

        columnsComboBox = new ComboBox<>();
        columnsComboBox.setPromptText("Select a column");
        columnsComboBox.getStyleClass().add("combo-box");

        selectedColumnsListView = new ListView<>();
        selectedColumnsListView.setPrefHeight(100);
        selectedColumnsListView.getStyleClass().add("list-view");

        nextButton = new Button("Next");
        nextButton.setDisable(true);
        nextButton.getStyleClass().add("button");

        addColumnButton = new Button("Add Column");
        addColumnButton.setDisable(true);
        addColumnButton.getStyleClass().add("button");

        removeColumnButton = new Button("Remove Column");
        addColumnButton.setDisable(true);
        removeColumnButton.getStyleClass().add("button");

        showSortedGridButton = new Button("Show Sorted Grid");
        showSortedGridButton.getStyleClass().add("button");

        backToChooseRangesButton = new Button("Back to Ranges");
        backToChooseRangesButton.getStyleClass().add("button");

        showSortedGridButton.setDisable(true);
        addColumnButton.setDisable(true);
        removeColumnButton.setDisable(true);
        columnsComboBox.setDisable(true);

        sortRowsData = new SortRowsData();

        setupLayout();
    }

    public SortRowsData show() {
        popupStage.showAndWait();
        return sortRowsData;
    }

    private void setupLayout() {
        // Section 1: Range Selection Panel
        rangeSelectionPane.add(new Label("Range-From (e.g, A2):"), 0, 0);
        rangeSelectionPane.add(rangeFromField, 1, 0);
        rangeSelectionPane.add(new Label("Range-To (e.g, C5):"), 0, 1);
        rangeSelectionPane.add(rangeToField, 1, 1);
        rangeSelectionPane.add(nextButton, 1, 2);

        // Enable "Next" button only when both fields have text
        rangeFromField.textProperty().addListener((obs, oldVal, newVal) -> updateNextButtonState());
        rangeToField.textProperty().addListener((obs, oldVal, newVal) -> updateNextButtonState());

        // Section 2: Column Selection Panel
        Label columnLabel = new Label("Select columns to sort by:");
        columnSelectionPane.add(columnLabel, 0, 0);

        // Wider ComboBox and buttons for full display
        columnsComboBox.setPrefWidth(250);
        columnSelectionPane.add(columnsComboBox, 0, 1);
        addColumnButton.setPrefWidth(130);
        columnSelectionPane.add(addColumnButton, 1, 1);

        selectedColumnsListView.setPrefSize(250, 100);
        columnSelectionPane.add(selectedColumnsListView, 0, 2, 2, 1);

        // Align buttons in the specified order
        HBox topActionButtons = new HBox(15, backToChooseRangesButton, removeColumnButton);
        topActionButtons.setPadding(new Insets(10, 0, 0, 0));
        backToChooseRangesButton.setPrefWidth(150);
        removeColumnButton.setPrefWidth(150);

        // Position "Show Sorted Grid" button below
        showSortedGridButton.setPrefWidth(200);
        VBox actionButtonsLayout = new VBox(10, topActionButtons, showSortedGridButton);
        actionButtonsLayout.setPadding(new Insets(15, 0, 0, 0));
        columnSelectionPane.add(actionButtonsLayout, 0, 3, 2, 1);

        leftControlsLayout.getChildren().addAll(rangeSelectionPane, columnSelectionPane);
        columnSelectionPane.setVisible(false);

        // Original and sorted grid containers
        originalGridContainer = new VBox(10, new Label("Original Grid"), createOriginalGrid());
        sortedGridContainer = new VBox(10, new Label("Sorted Grid"));
        sortedGridContainer.setVisible(false);

        // Layout for the grids on the right
        VBox gridsDisplay = new VBox(20, originalGridContainer, sortedGridContainer);

        // Main layout with side-by-side arrangement of controls and grids
        HBox mainLayout = new HBox(30, leftControlsLayout, gridsDisplay);
        mainLayout.setPadding(new Insets(15));

        // Set event handlers
        nextButton.setOnAction(e -> finalizeRange());
        addColumnButton.setOnAction(e -> addColumnToList());
        removeColumnButton.setOnAction(e -> removeColumnFromList());
        showSortedGridButton.setOnAction(e -> showSortedGrid());
        backToChooseRangesButton.setOnAction(e -> resetToRangeSelection());

        // Enable/Disable addColumnButton based on columnsComboBox selection
        columnsComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            addColumnButton.setDisable(newVal == null || newVal.isEmpty());
        });

        // Enable/Disable removeColumnButton based on selectedColumnsListView selection
        selectedColumnsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            removeColumnButton.setDisable(newVal == null || newVal.isEmpty());
        });

        // Scene setup
        Scene scene = new Scene(mainLayout, 1530, 700);
        scene.getStylesheets().add(getClass().getResource("sortpopup.css").toExternalForm());
        popupStage.setScene(scene);
    }

    private void updateNextButtonState() {
        nextButton.setDisable(rangeFromField.getText().isEmpty() || rangeToField.getText().isEmpty());
    }

    private void finalizeRange() {
        String rangeFrom = rangeFromField.getText().trim().toUpperCase();
        String rangeTo = rangeToField.getText().trim().toUpperCase();


        if (rangeFrom.isEmpty() || rangeTo.isEmpty() || rangeFrom.length() < 2 || rangeTo.length() < 2) {
            showAlert("Invalid Input", "Please enter valid range values.");
            return;
        }

        setGrayBackgroundForCells(rangeFrom, rangeTo);

        String startColumn = rangeFrom.substring(0, 1);
        String endColumn = rangeTo.substring(0, 1);

        columnsComboBox.getItems().clear();
        for (char c = startColumn.charAt(0); c <= endColumn.charAt(0); c++) {
            columnsComboBox.getItems().add(String.valueOf(c));
        }

        // Disable range selection section
        rangeFromField.setDisable(true);
        rangeToField.setDisable(true);
        nextButton.setDisable(true);

        // Enable column selection and make the section visible
        columnsComboBox.setDisable(false);
        addColumnButton.setDisable(true);
        removeColumnButton.setDisable(true);
        columnSelectionPane.setVisible(true);
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
        gridScrollPane.setPrefSize(1100, 335);

        VBox gridContainer = new VBox(gridScrollPane);
        gridContainer.setPadding(new Insets(10));
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
        gridScrollPane.setPrefSize(1100, 335);

        VBox gridContainer = new VBox(gridScrollPane);
        gridContainer.setPadding(new Insets(10));
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
