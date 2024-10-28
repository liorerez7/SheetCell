package controller.popup.filter;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AvailableFilterValuesPopup {

    private final Stage popupStage;
    private final GridPane gridPane;
    private final Map<Character, Set<String>> selectedValues;
    private final Map<Character, Set<String>> stringsInChosenColumn;

    public AvailableFilterValuesPopup(Map<Character, Set<String>> stringsInChosenColumn) {
        this.stringsInChosenColumn = stringsInChosenColumn;
        this.selectedValues = new HashMap<>();
        this.popupStage = new Stage();
        this.gridPane = new GridPane();

        initializeStage();
    }

    public Map<Character, Set<String>> show() {
        popupStage.showAndWait();
        return selectedValues;
    }

    private void initializeStage() {
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Filter Data");

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

        setupCheckboxes();

        Scene scene = new Scene(gridPane, 500, 300);
        popupStage.setScene(scene);
    }

    private void setupCheckboxes() {
        int rowIndex = 0;

        for (Map.Entry<Character, Set<String>> entry : stringsInChosenColumn.entrySet()) {
            Character column = entry.getKey();
            Set<String> uniqueValues = entry.getValue();

            Label columnLabel = new Label("Column: " + column);
            gridPane.add(columnLabel, 0, rowIndex);

            FlowPane checkBoxContainer = new FlowPane(10, 10);
            checkBoxContainer.setPadding(new Insets(5));
            checkBoxContainer.setPrefWrapLength(300);

            Set<String> selectedStrings = new HashSet<>();

            for (String value : uniqueValues) {
                if (value.isEmpty()) continue;

                CheckBox checkBox = new CheckBox(value);
                checkBox.setOnAction(e -> {
                    if (checkBox.isSelected()) {
                        selectedStrings.add(value);
                    } else {
                        selectedStrings.remove(value);
                    }
                });

                checkBoxContainer.getChildren().add(checkBox);
            }

            gridPane.add(checkBoxContainer, 1, rowIndex);
            selectedValues.put(column, selectedStrings);
            rowIndex++;
        }

        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 0, rowIndex, 2, 1);
        GridPane.setHalignment(submitButton, HPos.CENTER);

        submitButton.setOnAction(e -> popupStage.close());
    }
}