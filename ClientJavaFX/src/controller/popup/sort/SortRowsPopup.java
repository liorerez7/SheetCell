package controller.popup.sort;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utilities.javafx.smallparts.SortRowsData;

import java.util.List;

public class SortRowsPopup {

    private final Stage popupStage;
    private final GridPane gridPane;
    private final SortRowsData sortRowsData;
    private final TextField rangeFromField;
    private final TextField rangeToField;
    private final ComboBox<String> columnsComboBox;
    private final ListView<String> selectedColumnsListView;
    private final Button finalizeRangeButton;
    private final Button addColumnButton;
    private final Button removeColumnButton;
    private final Button submitButton;

    public SortRowsPopup() {
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Insert sorting parameters");

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        rangeFromField = new TextField();
        rangeToField = new TextField();
        columnsComboBox = new ComboBox<>();
        columnsComboBox.setPromptText("Select a column");
        selectedColumnsListView = new ListView<>();
        selectedColumnsListView.setPrefHeight(100);
        finalizeRangeButton = new Button("Finalize Range");
        addColumnButton = new Button("Add Column");
        removeColumnButton = new Button("Remove Column");
        submitButton = new Button("Submit");
        submitButton.setDisable(true);
        sortRowsData = new SortRowsData();

        setupLayout();
    }

    public SortRowsData show() {
        popupStage.showAndWait();
        return sortRowsData;
    }

    private void setupLayout() {
        gridPane.add(new Label("Range-From (example, A2):"), 0, 0);
        gridPane.add(rangeFromField, 1, 0);
        gridPane.add(new Label("Range-To (example, C5):"), 0, 1);
        gridPane.add(rangeToField, 1, 1);

        gridPane.add(finalizeRangeButton, 0, 2, 2, 1);
        gridPane.add(new Label("Select columns to sort by:"), 0, 3);
        gridPane.add(columnsComboBox, 1, 3);
        gridPane.add(addColumnButton, 1, 4);
        gridPane.add(selectedColumnsListView, 1, 5);
        gridPane.add(removeColumnButton, 1, 6);

        GridPane.setMargin(submitButton, new Insets(10));
        gridPane.add(submitButton, 1, 7);

        // Set initial disable state for buttons
        columnsComboBox.setDisable(true);
        addColumnButton.setDisable(true);
        removeColumnButton.setDisable(true);
        submitButton.setDisable(true);

        // Add event handlers
        finalizeRangeButton.setOnAction(e -> finalizeRange());
        addColumnButton.setOnAction(e -> addColumnToList());
        removeColumnButton.setOnAction(e -> removeColumnFromList());
        submitButton.setOnAction(e -> handleSubmit());

        Scene scene = new Scene(gridPane, 500, 400);
        popupStage.setScene(scene);
    }

    private void finalizeRange() {
        String rangeFrom = rangeFromField.getText().trim().toUpperCase();
        String rangeTo = rangeToField.getText().trim().toUpperCase();

        if (rangeFrom.isEmpty() || rangeTo.isEmpty() || rangeFrom.length() < 2 || rangeTo.length() < 2) {
            showAlert("Invalid Input", "Please enter valid range values.");
            return;
        }

        String startColumn = rangeFrom.substring(0, 1);
        String endColumn = rangeTo.substring(0, 1);

        columnsComboBox.getItems().clear();
        for (char c = startColumn.charAt(0); c <= endColumn.charAt(0); c++) {
            columnsComboBox.getItems().add(String.valueOf(c));
        }

        columnsComboBox.setDisable(false);
        addColumnButton.setDisable(false);
        removeColumnButton.setDisable(false);
        submitButton.setDisable(true); // Submit button enabled when columns are added
    }

    private void addColumnToList() {
        String selectedColumn = columnsComboBox.getSelectionModel().getSelectedItem();
        if (selectedColumn != null && !selectedColumnsListView.getItems().contains(selectedColumn)) {
            selectedColumnsListView.getItems().add(selectedColumn);
            columnsComboBox.getSelectionModel().clearSelection();
            submitButton.setDisable(selectedColumnsListView.getItems().isEmpty());
        }
    }

    private void removeColumnFromList() {
        String selectedItem = selectedColumnsListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            selectedColumnsListView.getItems().remove(selectedItem);
            submitButton.setDisable(selectedColumnsListView.getItems().isEmpty());
        }
    }

    private void handleSubmit() {
        String range = rangeFromField.getText() + ".." + rangeToField.getText();
        List<String> selectedColumns = selectedColumnsListView.getItems();
        String columns = String.join(",", selectedColumns);

        sortRowsData.setRange(range);
        sortRowsData.setColumnsToSortBy(columns);
        popupStage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
