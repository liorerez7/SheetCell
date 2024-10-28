package controller.popup.filter;

import controller.grid.GridController;
import dto.components.DtoContainerData;
import dto.components.DtoSheetCell;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
    private final DtoSheetCell dtoSheetCell;
    private DtoContainerData filteredData;
    private HBox lastClickedCellBox;
    private Character lastClickedColumn;
    private int lastClickedRowIndex;
    private boolean isUniqueDataSectionAdded = false;
    private boolean isCriteriaSectionAdded = false;
    private final Button showFilteredGridButton;
    private final GridController gridController; // Add GridController instance



    public FilterPopup(DtoSheetCell dtoSheetCell, GridController gridController) {
        this.dtoSheetCell = dtoSheetCell;
        this.gridController = gridController; // Store GridController instance
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
        showFilteredGridButton = new Button("Show Filtered Grid"); // Initialize the new button
        removeButton.setDisable(true);
        showFilteredGridButton.setDisable(true); // Initially disabled

        removeButton.setOnAction(e -> handleRemoveButton());
        showFilteredGridButton.setOnAction(e -> showFilteredGrid()); // Set up the button action

        initializeStage();
    }

    public void show() {
        popupStage.showAndWait();
    }

    private void initializeStage() {
        mainLayout.getChildren().add(createRangeSelectionPanel());

        Scene scene = new Scene(new ScrollPane(mainLayout), 600, 700);
        scene.getStylesheets().add(getClass().getResource("filterpopup.css").toExternalForm());
        popupStage.setScene(scene);

        filterCriteriaTable.setSelectionModel(null); // Disable row selection
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

        Button rangeSubmitButton = new Button("Next");
        rangeSubmitButton.setOnAction(e -> handleRangeSubmission());
        rangePane.add(rangeSubmitButton, 1, 2);

        return rangePane;
    }

    private void handleRangeSubmission() {
        if (!rangeFromField.getText().isEmpty() && !rangeToField.getText().isEmpty()) {
            rangeFromField.setText(rangeFromField.getText().toUpperCase());
            rangeToField.setText(rangeToField.getText().toUpperCase());

            populateColumnComboBox();
            columnComboBox.setDisable(false);

            if (!mainLayout.getChildren().contains(columnComboBox)) {
                mainLayout.getChildren().add(createColumnSelectionPanel());
            }
        }
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
        char selectedColumn = Character.toUpperCase(columnComboBox.getValue());
        populateUniqueDataList(selectedColumn);

        if (!isUniqueDataSectionAdded) {
            mainLayout.getChildren().add(createUniqueDataDisplay());
            isUniqueDataSectionAdded = true;
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

        uniqueDataPane.getChildren().add(addDataButton);
        return uniqueDataPane;
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

    // Modify handleRemoveButton to disable "Show Filtered Grid" if the table is empty
    private void handleRemoveButton() {
        if (lastClickedColumn != null && lastClickedRowIndex >= 0) {
            Map<Character, String> rowData = filterCriteriaTable.getItems().get(lastClickedRowIndex);
            rowData.remove(lastClickedColumn);
            filterCriteriaTable.refresh();

            if (rowData.values().stream().allMatch(Objects::isNull)) {
                filterCriteriaTable.getItems().remove(rowData);
            }

            if (filterCriteriaTable.getItems().isEmpty()) {
                mainLayout.getChildren().remove(createFilterCriteriaPanel());
                isCriteriaSectionAdded = false;
                showFilteredGridButton.setDisable(true); // Disable "Show Filtered Grid" when table is empty
            }

            lastClickedCellBox = null;
            lastClickedColumn = null;
            lastClickedRowIndex = -1;
            removeButton.setDisable(true);
        }
    }

    private void showFilteredGrid() {
        DtoContainerData filteredData = applyFilter();
        if (filteredData != null) {
            FilterGridPopupHandler filterGridPopupHandler = new FilterGridPopupHandler(gridController, filteredData);
            filterGridPopupHandler.show(); // Opens the filtered grid scene
        }
    }


    // Adjusted applyFilter to use all columns from filterCriteriaTable instead of a single column
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


