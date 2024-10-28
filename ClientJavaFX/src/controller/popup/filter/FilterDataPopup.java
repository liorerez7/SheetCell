package controller.popup.filter;


import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utilities.javafx.smallparts.FilterGridData;

import java.util.ArrayList;
import java.util.List;

public class FilterDataPopup {

    private final Stage popupStage;
    private final GridPane gridPane;
    private final FilterGridData filterGridData;
    private final TextField rangeFromField;
    private final TextField rangeToField;
    private final HBox checkBoxContainer;
    private final Button submitButton;

    public FilterDataPopup() {
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Insert range for filtering");

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        rangeFromField = new TextField();
        rangeToField = new TextField();
        checkBoxContainer = new HBox(10);
        submitButton = new Button("Submit");
        submitButton.setDisable(true);
        filterGridData = new FilterGridData();

        setupLayout();
    }

    public FilterGridData show() {
        popupStage.showAndWait();
        return filterGridData;
    }

    private void setupLayout() {
        gridPane.add(new Label("Range-From (example, A2):"), 0, 0);
        gridPane.add(rangeFromField, 1, 0);
        gridPane.add(new Label("Range-To (example, C5):"), 0, 1);
        gridPane.add(rangeToField, 1, 1);
        gridPane.add(new Label("Columns to filter by:"), 0, 2);
        gridPane.add(checkBoxContainer, 1, 2);

        Button chooseColumnsButton = new Button("Choose Columns");
        gridPane.add(chooseColumnsButton, 1, 3);
        gridPane.add(submitButton, 1, 4);

        chooseColumnsButton.setOnAction(e -> generateDynamicCheckboxes());
        submitButton.setOnAction(e -> handleSubmission());

        Scene scene = new Scene(gridPane, 400, 200);
        popupStage.setScene(scene);
    }

    private void generateDynamicCheckboxes() {
        checkBoxContainer.getChildren().clear();
        char startColumn = rangeFromField.getText().charAt(0);
        char endColumn = rangeToField.getText().charAt(0);

        for (char col = startColumn; col <= endColumn; col++) {
            CheckBox checkBox = new CheckBox(String.valueOf(col));
            checkBoxContainer.getChildren().add(checkBox);
        }
        submitButton.setDisable(false);
    }

    private void handleSubmission() {
        String range = rangeFromField.getText() + ".." + rangeToField.getText();
        List<String> selectedColumns = new ArrayList<>();

        for (Node node : checkBoxContainer.getChildren()) {
            if (node instanceof CheckBox checkBox && checkBox.isSelected()) {
                selectedColumns.add(checkBox.getText());
            }
        }

        filterGridData.setRange(range);
        filterGridData.setColumnsToFilterBy(String.join(",", selectedColumns));
        popupStage.close();
    }
}