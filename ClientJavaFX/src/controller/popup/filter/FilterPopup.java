package controller.popup.filter;

import controller.grid.CustomCellLabel;
import controller.grid.GridController;
import dto.components.DtoContainerData;
import dto.components.DtoSheetCell;
import dto.small_parts.CellLocation;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

public class FilterPopup {

    private final Stage popupStage;
    private final VBox mainLayout;
    private final TextField rangeFromField;
    private final TextField rangeToField;
    private final ComboBox<Character> columnComboBox;
    private final ListView<String> uniqueDataListView;
    private final TableView<Map<Character, String>> filterCriteriaTable;
    private final Button applyFilterButton;
    private final Button removeButton;
    private final Button rangeSubmitButton;
    private final Button showFilteredGridButton;
    private final DtoSheetCell dtoSheetCell;
    private final GridController gridController;
    private DtoContainerData filteredData;
    private HBox lastClickedCellBox;
    private Character lastClickedColumn;
    private int lastClickedRowIndex;
    private boolean isUniqueDataSectionAdded = false;
    private boolean isCriteriaSectionAdded = false;
    private VBox originalGridContainer; // Holds the original grid
    private VBox filteredGridContainer; // Holds the filtered grid for later addition
    private Map<CellLocation, CustomCellLabel> cellLocationCustomCellLabelMap = new HashMap<>();


    public FilterPopup(DtoSheetCell dtoSheetCell, GridController gridController) {
        this.dtoSheetCell = dtoSheetCell;
        this.gridController = gridController;
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Filter Data");

        mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(10));

        rangeFromField = new TextField();
        rangeToField = new TextField();
        columnComboBox = new ComboBox<>();
        uniqueDataListView = new ListView<>();
        filterCriteriaTable = new TableView<>();
        applyFilterButton = new Button("Apply Filter");
        removeButton = new Button("Remove");
        rangeSubmitButton = new Button("Next");
        rangeSubmitButton.setDisable(true);  // Initially disabled
        showFilteredGridButton = new Button("Show Filtered Grid");

        // Add listeners to enable rangeSubmitButton only when both text fields are populated
        rangeFromField.textProperty().addListener((observable, oldValue, newValue) -> updateRangeSubmitButtonState());
        rangeToField.textProperty().addListener((observable, oldValue, newValue) -> updateRangeSubmitButtonState());

        removeButton.setOnAction(e -> handleRemoveButton());
        showFilteredGridButton.setOnAction(e -> showFilteredGrid());

        initializeStage();
    }

    private void updateRangeSubmitButtonState() {
        boolean isEnabled = !rangeFromField.getText().isEmpty() && !rangeToField.getText().isEmpty();
        rangeSubmitButton.setDisable(!isEnabled);
    }


    public void show() {
        popupStage.showAndWait();
    }

    private void initializeStage() {
        // Create original grid container with a title label
        Label originalGridTitle = new Label("Original Grid");
        originalGridTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        originalGridContainer = new VBox(10, originalGridTitle, createOriginalGrid());

        // Create filtered grid container with a title label, initially empty
        Label filteredGridTitle = new Label("Filtered Grid");
        filteredGridTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        filteredGridContainer = new VBox(10, filteredGridTitle);
        filteredGridContainer.setPadding(new Insets(10));
        filteredGridContainer.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");
        filteredGridContainer.setPrefSize(1000, 300);  // Adjusted height to give space for title

        // Layout structure to hold both grids on the right
        VBox gridSection = new VBox(20, originalGridContainer, filteredGridContainer);

        // Filter section on the left
        mainLayout.getChildren().add(createRangeSelectionPanel());

        // Wrap everything in HBox
        HBox contentLayout = new HBox(20, mainLayout, gridSection);
        contentLayout.setPadding(new Insets(10));

        // Set up the scene with the combined layout
        Scene scene = new Scene(new ScrollPane(contentLayout), 1450, 700);
        scene.getStylesheets().add(getClass().getResource("filterpopup.css").toExternalForm());
        popupStage.setScene(scene);

        filterCriteriaTable.setSelectionModel(null);
    }

    // Method to create the original grid as a VBox
    private VBox createOriginalGrid() {
        GridPane originalGrid = new GridPane();
        originalGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");

        cellLocationCustomCellLabelMap = gridController.initializeOriginalPopupGrid(originalGrid, dtoSheetCell);

        ScrollPane gridScrollPane = new ScrollPane(originalGrid);
        gridScrollPane.setFitToWidth(true);
        gridScrollPane.setFitToHeight(true);
        gridScrollPane.setPrefSize(1000, 320);

        VBox gridContainer = new VBox(gridScrollPane);
        gridContainer.setPadding(new Insets(10));
        gridContainer.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

        return gridContainer;
    }

    // Method to create the filtered grid as a VBox
    private VBox createFilterGrid(DtoContainerData dtoContainerData) {
        GridPane filterGrid = new GridPane();
        filterGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");

        gridController.initializeFilterPopupGrid(filterGrid, dtoContainerData);

        ScrollPane gridScrollPane = new ScrollPane(filterGrid);
        gridScrollPane.setFitToWidth(true);
        gridScrollPane.setFitToHeight(true);
        gridScrollPane.setPrefSize(1000, 320);

        VBox gridContainer = new VBox(gridScrollPane);
        gridContainer.setPadding(new Insets(10));
        gridContainer.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

        return gridContainer;
    }


    private GridPane createRangeSelectionPanel() {
        GridPane rangePane = new GridPane();
        rangePane.setHgap(10);
        rangePane.setVgap(10);
        rangePane.setPadding(new Insets(10));
        rangePane.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

        rangePane.add(new Label("From (e.g., A3):"), 0, 0);
        rangePane.add(rangeFromField, 1, 0);
        rangePane.add(new Label("To (e.g., E8):"), 0, 1);
        rangePane.add(rangeToField, 1, 1);

        rangeSubmitButton.setOnAction(e -> handleRangeSubmission());
        rangePane.add(rangeSubmitButton, 1, 2);

        return rangePane;
    }


    private void handleRangeSubmission() {
        if (!rangeFromField.getText().isEmpty() && !rangeToField.getText().isEmpty()) {
            rangeFromField.setText(rangeFromField.getText().toUpperCase());
            rangeToField.setText(rangeToField.getText().toUpperCase());

            setGrayBackgroundForCells(rangeFromField.getText(), rangeToField.getText());

            // Disable range fields
            rangeFromField.setDisable(true);
            rangeToField.setDisable(true);
            rangeSubmitButton.setDisable(true);

            populateColumnComboBox();
            columnComboBox.setDisable(false);

            if (!mainLayout.getChildren().contains(columnComboBox)) {
                mainLayout.getChildren().add(createColumnSelectionPanel());
            }
        }
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

    private HBox createColumnSelectionPanel() {
        HBox columnSelectionPane = new HBox(10);
        columnSelectionPane.setPadding(new Insets(10));
        columnSelectionPane.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

        columnComboBox.setPromptText("Select Column");
        columnComboBox.setDisable(false);

        columnComboBox.setOnAction(e -> handleColumnSelection());

        columnSelectionPane.getChildren().addAll(new Label("Column to filter by:"), columnComboBox);
        return columnSelectionPane;
    }


    private void handleColumnSelection() {
        Character selectedColumn = columnComboBox.getValue();
        if (selectedColumn != null) {
            populateUniqueDataList(Character.toUpperCase(selectedColumn));
            if (!isUniqueDataSectionAdded) {
                mainLayout.getChildren().add(createUniqueDataDisplay());
                isUniqueDataSectionAdded = true;
            }
        }
    }


    private void populateColumnComboBox() {
        columnComboBox.getItems().clear();
        char startCol = rangeFromField.getText().charAt(0);
        char endCol = rangeToField.getText().charAt(0);
        for (char col = startCol; col <= endCol; col++) {
            columnComboBox.getItems().add(col);
        }
    }

    private VBox createUniqueDataDisplay() {
        VBox uniqueDataPane = new VBox(5);
        uniqueDataPane.setPadding(new Insets(10));
        uniqueDataPane.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

        uniqueDataListView.setDisable(false);
        uniqueDataListView.setPrefHeight(120);
        uniqueDataPane.getChildren().addAll(new Label("Select unique data:"), uniqueDataListView);

        Button addDataButton = new Button("Add Data");
        addDataButton.setDisable(true);
        addDataButton.setOnAction(e -> handleDataSelection());

        uniqueDataListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            addDataButton.setDisable(newVal == null);
        });

        // Add "Back to Choose Ranges" button
        Button backToChooseRangesButton = new Button("Back to Choose Ranges");
        backToChooseRangesButton.setOnAction(e -> handleBackToChooseRanges());

        uniqueDataPane.getChildren().addAll(addDataButton, backToChooseRangesButton);
        return uniqueDataPane;
    }

    private void handleBackToChooseRanges() {
        // Enable range fields and the "Next" button
        rangeFromField.setDisable(false);
        rangeToField.setDisable(false);
        rangeSubmitButton.setDisable(false);

        // Clear the filter criteria table and reset the criteria section
        filterCriteriaTable.getItems().clear();
        filterCriteriaTable.getColumns().clear();
        mainLayout.getChildren().removeIf(node -> node != rangeFromField.getParent());

        // Clear the filtered grid container to hide the filtered grid
        filteredGridContainer.getChildren().clear();

        setWhiteBackgroundForCells(rangeFromField.getText(), rangeToField.getText());

        // Reset flags for section addition
        isUniqueDataSectionAdded = false;
        isCriteriaSectionAdded = false;
        removeButton.setDisable(true);
        showFilteredGridButton.setDisable(true);
    }


    private void populateUniqueDataList(char column) {
        uniqueDataListView.getItems().clear();
        String range = rangeFromField.getText() + ".." + rangeToField.getText();
        Map<Character, Set<String>> columnValues = dtoSheetCell.getUniqueStringsInColumn(String.valueOf(column), range);

        if (columnValues.containsKey(column)) {
            uniqueDataListView.getItems().addAll(columnValues.get(column));
        }
    }

    private void handleDataSelection() {
        List<String> selectedItems = uniqueDataListView.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
            Character selectedColumn = Character.toUpperCase(columnComboBox.getValue());
            addToFilterCriteriaTable(selectedColumn, selectedItems);

            // Enable the table and filter buttons only if an item was added
            filterCriteriaTable.setDisable(false);
            applyFilterButton.setDisable(false);
            showFilteredGridButton.setDisable(false); // Enable "Show Filtered Grid" button

            if (!isCriteriaSectionAdded) {
                mainLayout.getChildren().add(createFilterCriteriaPanel());
                HBox buttonBox = new HBox(10, removeButton, showFilteredGridButton); // Add both buttons together
                mainLayout.getChildren().add(buttonBox);
                isCriteriaSectionAdded = true;
            }
        }
    }

    private void addToFilterCriteriaTable(Character column, List<String> selectedValues) {
        // Check if the column already exists in the table; if not, create it
        if (filterCriteriaTable.getColumns().stream().noneMatch(col -> col.getText().equals("Column " + column))) {
            TableColumn<Map<Character, String>, String> newColumn = new TableColumn<>("Column " + column);
            newColumn.setMinWidth(100);

            filterCriteriaTable.setPrefHeight(200);

            newColumn.setCellValueFactory(data -> {
                String value = data.getValue().get(column);
                return new SimpleStringProperty(value != null ? value : "");
            });

            newColumn.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        Label label = new Label(item);
                        HBox cellBox = new HBox(label);
                        cellBox.setOnMouseClicked(e -> handleCellClick(column, item, getIndex(), cellBox));
                        cellBox.getStyleClass().add("clickable-cell");
                        setGraphic(cellBox);
                    }
                }
            });

            filterCriteriaTable.getColumns().add(newColumn);
        }

        // Populate rows based on selected values
        for (String value : selectedValues) {
            boolean rowUpdated = false;
            for (Map<Character, String> rowData : filterCriteriaTable.getItems()) {
                // Check if there is an empty cell in the current column and add the value
                if (rowData.get(column) == null) {
                    rowData.put(column, value);
                    rowUpdated = true;
                    break;
                }
            }
            if (!rowUpdated) {
                // If no existing row could be updated, add a new row
                Map<Character, String> newRow = new HashMap<>();
                newRow.put(column, value);
                filterCriteriaTable.getItems().add(newRow);
            }
        }
        filterCriteriaTable.refresh();
    }

    private void handleCellClick(Character column, String item, int rowIndex, HBox cellBox) {

        if (lastClickedCellBox != null) {
            lastClickedCellBox.getStyleClass().remove("clicked-cell");
        }

        cellBox.getStyleClass().add("clicked-cell");
        lastClickedCellBox = cellBox;
        lastClickedColumn = column;
        lastClickedRowIndex = rowIndex;
        removeButton.setDisable(false); // Enable remove button
    }

    private VBox createFilterCriteriaPanel() {
        VBox filterCriteriaPane = new VBox(5);
        filterCriteriaPane.setPadding(new Insets(10));
        filterCriteriaPane.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

        filterCriteriaTable.setPlaceholder(new Label("No filter criteria applied."));
        filterCriteriaPane.getChildren().addAll(new Label("Filter Criteria:"), filterCriteriaTable);
        return filterCriteriaPane;
    }

    // Modify handleRemoveButton to shift values up after removal
    private void handleRemoveButton() {
        if (lastClickedColumn != null && lastClickedRowIndex >= 0) {
            // Get the column and remove the item from the specified row index
            Map<Character, String> rowData = filterCriteriaTable.getItems().get(lastClickedRowIndex);
            rowData.remove(lastClickedColumn);

            // Shift all rows in the column up by one position, starting from the removed row
            for (int i = lastClickedRowIndex; i < filterCriteriaTable.getItems().size() - 1; i++) {
                Map<Character, String> currentRow = filterCriteriaTable.getItems().get(i);
                Map<Character, String> nextRow = filterCriteriaTable.getItems().get(i + 1);
                currentRow.put(lastClickedColumn, nextRow.get(lastClickedColumn));
            }

            // Remove the last row, as its data has been shifted up
            filterCriteriaTable.getItems().get(filterCriteriaTable.getItems().size() - 1).remove(lastClickedColumn);

            // Refresh the table to reflect changes
            filterCriteriaTable.refresh();

            // Remove empty rows if they no longer contain any values
            filterCriteriaTable.getItems().removeIf(row -> row.values().stream().allMatch(Objects::isNull));

            if (filterCriteriaTable.getItems().isEmpty()) {
                mainLayout.getChildren().remove(createFilterCriteriaPanel());
                isCriteriaSectionAdded = false;
                showFilteredGridButton.setDisable(true); // Disable "Show Filtered Grid" when table is empty
            }

            // Reset the last clicked cell information
            lastClickedCellBox = null;
            lastClickedColumn = null;
            lastClickedRowIndex = -1;
            removeButton.setDisable(true);
        }
    }

    private void showFilteredGrid() {
        DtoContainerData filteredData = applyFilter();
        if (filteredData != null) {
            VBox filteredGrid = createFilterGrid(filteredData);

            // Clear old content and add the filtered grid
            filteredGridContainer.getChildren().clear();
            filteredGridContainer.getChildren().addAll(
                    new Label("Filtered Grid"), filteredGrid
            );
        }
    }
    private DtoContainerData applyFilter() {
        String range = rangeFromField.getText() + ".." + rangeToField.getText();
        Map<Character, Set<String>> filterCriteria = new HashMap<>();

        // Iterate over each row in the filterCriteriaTable to collect filtering data for each column
        for (Map<Character, String> rowData : filterCriteriaTable.getItems()) {
            for (Map.Entry<Character, String> entry : rowData.entrySet()) {
                Character column = entry.getKey();
                String value = entry.getValue();

                if (value != null && !value.isEmpty()) {
                    // Add value to the set of criteria for this column
                    filterCriteria.computeIfAbsent(column, k -> new HashSet<>()).add(value);
                }
            }
        }

        // Apply filter with multiple columns and their respective values
        filteredData = dtoSheetCell.filterSheetCell(range, filterCriteria);
        return filteredData;
    }
}





