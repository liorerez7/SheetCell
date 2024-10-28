//package controller.popup.filter;
//
//import dto.components.DtoContainerData;
//import dto.components.DtoSheetCell;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.*;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//import javafx.application.Platform;
//
//import java.util.*;
//
//public class FilterPopup {
//
//    private final Stage popupStage;
//    private final VBox mainLayout;
//    private final TextField rangeFromField;
//    private final TextField rangeToField;
//    private final ComboBox<Character> columnComboBox;
//    private final ListView<String> uniqueDataListView;
//    private final TableView<Map<Character, ComboBox<String>>> filterCriteriaTable;
//    private final Button applyFilterButton;
//    private final DtoSheetCell dtoSheetCell;
//    private DtoContainerData filteredData;
//
//    public FilterPopup(DtoSheetCell dtoSheetCell) {
//        this.dtoSheetCell = dtoSheetCell;
//        popupStage = new Stage();
//        popupStage.initModality(Modality.APPLICATION_MODAL);
//        popupStage.setTitle("Filter Data");
//
//        mainLayout = new VBox(10);
//        mainLayout.setPadding(new Insets(10));
//
//        // Initialize sections
//        rangeFromField = new TextField();
//        rangeToField = new TextField();
//        columnComboBox = new ComboBox<>();
//        uniqueDataListView = new ListView<>();
//        filterCriteriaTable = new TableView<>();
//        applyFilterButton = new Button("Apply Filter");
//        applyFilterButton.setDisable(true);
//
//        setupLayout();
//    }
//
//    public void show() {
//        popupStage.showAndWait();
//    }
//
//    private void setupLayout() {
//        // Add each panel section
//        mainLayout.getChildren().addAll(createRangeSelectionPanel(), createColumnSelectionPanel(), createUniqueDataDisplay(), createFilterCriteriaPanel(), applyFilterButton);
//
//        applyFilterButton.setOnAction(e -> applyFilter());
//
//        Scene scene = new Scene(new ScrollPane(mainLayout), 600, 700);
//        popupStage.setScene(scene);
//    }
//
//    private GridPane createRangeSelectionPanel() {
//        GridPane rangePane = new GridPane();
//        rangePane.setHgap(10);
//        rangePane.setVgap(10);
//        rangePane.setPadding(new Insets(10));
//
//        rangePane.add(new Label("From (e.g., A3):"), 0, 0);
//        rangePane.add(rangeFromField, 1, 0);
//        rangePane.add(new Label("To (e.g., E8):"), 0, 1);
//        rangePane.add(rangeToField, 1, 1);
//
//        Button rangeSubmitButton = new Button("Define Range");
//        rangeSubmitButton.setOnAction(e -> handleRangeSubmission());
//        rangePane.add(rangeSubmitButton, 1, 2);
//
//        return rangePane;
//    }
//
//    private void handleRangeSubmission() {
//        if (!rangeFromField.getText().isEmpty() && !rangeToField.getText().isEmpty()) {
//            populateColumnComboBox();
//            columnComboBox.setDisable(false);
//        }
//    }
//
//    private HBox createColumnSelectionPanel() {
//        HBox columnSelectionPane = new HBox(10);
//        columnSelectionPane.setPadding(new Insets(10));
//
//        columnComboBox.setPromptText("Select Column");
//        columnComboBox.setDisable(true);
//        columnComboBox.setOnAction(e -> handleColumnSelection());
//
//        columnSelectionPane.getChildren().addAll(new Label("Column to filter by:"), columnComboBox);
//        return columnSelectionPane;
//    }
//
//    private void handleColumnSelection() {
//        char selectedColumn = columnComboBox.getValue();
//        populateUniqueDataList(selectedColumn);
//        uniqueDataListView.setDisable(false);
//    }
//
//    private void populateColumnComboBox() {
//        columnComboBox.getItems().clear();
//        char startCol = rangeFromField.getText().charAt(0);
//        char endCol = rangeToField.getText().charAt(0);
//        for (char col = startCol; col <= endCol; col++) {
//            columnComboBox.getItems().add(col);
//        }
//    }
//
//    private VBox createUniqueDataDisplay() {
//        VBox uniqueDataPane = new VBox(5);
//        uniqueDataPane.setPadding(new Insets(10));
//
//        uniqueDataListView.setDisable(true);
//        uniqueDataListView.setPrefHeight(120);
//
//        uniqueDataPane.getChildren().addAll(new Label("Select unique data:"), uniqueDataListView);
//
//        Button selectDataButton = new Button("Select Data");
//        selectDataButton.setOnAction(e -> handleDataSelection());
//        uniqueDataPane.getChildren().add(selectDataButton);
//
//        return uniqueDataPane;
//    }
//
//    private void populateUniqueDataList(char column) {
//        uniqueDataListView.getItems().clear();
//        String range = rangeFromField.getText() + ".." + rangeToField.getText();
//        Map<Character, Set<String>> columnValues = dtoSheetCell.getUniqueStringsInColumn(String.valueOf(column), range);
//
//        if (columnValues.containsKey(column)) {
//            uniqueDataListView.getItems().addAll(columnValues.get(column));
//        }
//    }
//
//    private void handleDataSelection() {
//        if (!uniqueDataListView.getSelectionModel().getSelectedItems().isEmpty()) {
//            populateFilterCriteriaTable();
//            filterCriteriaTable.setDisable(false);
//            applyFilterButton.setDisable(false);
//        }
//    }
//
//    private VBox createFilterCriteriaPanel() {
//        VBox filterCriteriaPane = new VBox(5);
//        filterCriteriaPane.setPadding(new Insets(10));
//
//        filterCriteriaTable.setDisable(true);
//        filterCriteriaTable.setPlaceholder(new Label("No additional filters applied."));
//
//        filterCriteriaPane.getChildren().addAll(new Label("Filter Criteria:"), filterCriteriaTable);
//        return filterCriteriaPane;
//    }
//
//    private void populateFilterCriteriaTable() {
//        filterCriteriaTable.getColumns().clear();
//        filterCriteriaTable.getItems().clear();
//
//        char startCol = rangeFromField.getText().charAt(0);
//        char endCol = rangeToField.getText().charAt(0);
//
//        for (char col = startCol; col <= endCol; col++) {
//            if (col != columnComboBox.getValue()) { // Exclude already selected column
//                final char columnChar = col; // Create a final copy for use in the inner class
//
//                TableColumn<Map<Character, ComboBox<String>>, ComboBox<String>> column = new TableColumn<>("Column " + columnChar);
//
//                column.setCellFactory(param -> new TableCell<>() {
//                    @Override
//                    protected void updateItem(ComboBox<String> item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (empty) {
//                            setGraphic(null);
//                        } else {
//                            ComboBox<String> comboBox = new ComboBox<>();
//                            comboBox.setItems(FXCollections.observableArrayList(uniqueDataOptionsForColumn(columnChar)));
//                            comboBox.setPromptText("Select value");
//                            setGraphic(comboBox);
//                        }
//                    }
//                });
//                filterCriteriaTable.getColumns().add(column);
//            }
//        }
//    }
//
//    private List<String> uniqueDataOptionsForColumn(char column) {
//        String range = rangeFromField.getText() + ".." + rangeToField.getText();
//        String columnAsString = String.valueOf(column); // Convert column to String for method compatibility
//        Map<Character, Set<String>> columnValues = dtoSheetCell.getUniqueStringsInColumn(columnAsString, range);
//
//        // Use Character conversion for map key compatibility
//        Character columnKey = column;
//        return columnValues.containsKey(columnKey) ?
//                new ArrayList<>(columnValues.get(columnKey)) :
//                Collections.emptyList();
//    }
//
//    private void applyFilter() {
//        String range = rangeFromField.getText() + ".." + rangeToField.getText();
//        Character filterColumn = columnComboBox.getValue();
//        Set<String> selectedValues = new HashSet<>(uniqueDataListView.getSelectionModel().getSelectedItems());
//        Map<Character, Set<String>> filterCriteria = new HashMap<>();
//        filterCriteria.put(filterColumn, selectedValues);
//
//        // Retrieve additional filter criteria from the table if needed
//        for (TableColumn<Map<Character, ComboBox<String>>, ?> column : filterCriteriaTable.getColumns()) {
//            Character colChar = column.getText().charAt(column.getText().length() - 1);
//            filterCriteria.put(colChar, getComboBoxSelectionsForColumn(colChar));
//        }
//
//        filteredData = dtoSheetCell.filterSheetCell(range, filterCriteria);
//        displayFilteredData();
//    }
//
//    private Set<String> getComboBoxSelectionsForColumn(char column) {
//        Set<String> selectedValues = new HashSet<>();
//        for (Map<Character, ComboBox<String>> row : filterCriteriaTable.getItems()) {
//            ComboBox<String> comboBox = row.get(column);
//            if (comboBox != null && comboBox.getValue() != null) {
//                selectedValues.add(comboBox.getValue());
//            }
//        }
//        return selectedValues;
//    }
//
//    private void displayFilteredData() {
//        // Here, either update a main grid or show in a new window
//        if (filteredData != null) {
//            // Code to display or update data in a GridController or popup, e.g.,
//            // gridController.displayFilteredData(filteredData);
//            Platform.runLater(() -> {
//                Label previewLabel = new Label("Filtered Data Preview");
//                mainLayout.getChildren().add(previewLabel);
//            });
//        }
//    }
//}
