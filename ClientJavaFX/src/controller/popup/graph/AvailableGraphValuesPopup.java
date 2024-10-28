package controller.popup.graph;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.*;
import javafx.scene.layout.Priority;

public class AvailableGraphValuesPopup {

    private Stage popupStage;
    private Map<Character, List<String>> selectedValues;
    private char xAxis;
    private char yAxis;
    private String xTitle;
    private String yTitle;
    private Map<Character, Set<String>> stringsInChosenColumn;

    public AvailableGraphValuesPopup(char xAxis, char yAxis, String xTitle, String yTitle, Map<Character, Set<String>> stringsInChosenColumn) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.xTitle = xTitle;
        this.yTitle = yTitle;
        this.stringsInChosenColumn = stringsInChosenColumn;
        this.selectedValues = new HashMap<>();
        initializeStage();
    }

    public Map<Character, List<String>> show() {
        popupStage.showAndWait();
        return selectedValues;
    }

    private void initializeStage() {
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Filter Data with Order");

        GridPane gridPane = createGridPane();
        addContentToGrid(gridPane);

        Scene scene = new Scene(gridPane, 500, 400);
        popupStage.setScene(scene);
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        column1.setHalignment(HPos.CENTER);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS);
        column2.setHalignment(HPos.CENTER);

        gridPane.getColumnConstraints().addAll(column1, column2);
        return gridPane;
    }

    private void addContentToGrid(GridPane gridPane) {
        List<String> selectedXValues = new ArrayList<>();
        List<String> selectedYValues = new ArrayList<>();
        List<String> xValues = new ArrayList<>(stringsInChosenColumn.get(xAxis));
        List<String> yValues = new ArrayList<>(stringsInChosenColumn.get(yAxis));

        int rowIndex = 0;
        Label xAxisLabel = new Label("X-Axis: " + xTitle);
        Label yAxisLabel = new Label("Y-Axis: " + yTitle);
        gridPane.add(xAxisLabel, 0, rowIndex);
        gridPane.add(yAxisLabel, 1, rowIndex);
        rowIndex++;

        for (int i = 0; i < Math.min(xValues.size(), yValues.size()); i++) {
            ComboBox<String> xComboBox = new ComboBox<>();
            ComboBox<String> yComboBox = new ComboBox<>();

            xComboBox.getItems().addAll(xValues);
            yComboBox.getItems().addAll(yValues);

            gridPane.add(xComboBox, 0, rowIndex);
            gridPane.add(yComboBox, 1, rowIndex);

            addComboBoxListener(xComboBox, selectedXValues, xValues, gridPane, 0);
            addComboBoxListener(yComboBox, selectedYValues, yValues, gridPane, 1);

            rowIndex++;
        }

        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 0, rowIndex, 2, 1);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        submitButton.setOnAction(e -> handleSubmission(selectedXValues, selectedYValues));
    }

    private void addComboBoxListener(ComboBox<String> comboBox, List<String> selectedValues, List<String> values, GridPane gridPane, int column) {
        comboBox.setOnAction(e -> {
            String selected = comboBox.getSelectionModel().getSelectedItem();
            if (!selectedValues.contains(selected)) {
                selectedValues.add(selected);
                comboBox.setDisable(true);

                for (Node node : gridPane.getChildren()) {
                    if (node instanceof ComboBox && GridPane.getColumnIndex(node) == column) {
                        ComboBox<?> otherComboBox = (ComboBox<?>) node;
                        if (otherComboBox != comboBox) {
                            otherComboBox.getItems().remove(selected);
                        }
                    }
                }
            }
        });
    }

    private void handleSubmission(List<String> selectedXValues, List<String> selectedYValues) {
        selectedValues.put(xAxis, selectedXValues);
        selectedValues.put(yAxis, selectedYValues);
        popupStage.close();
    }
}
