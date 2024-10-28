package controller.popup.graph;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;


public class GraphPreWindow {

    private Stage popupStage;
    private List<String> data;

    public GraphPreWindow() {
        this.data = new ArrayList<>();
        initializeStage();
    }

    public List<String> show() {
        popupStage.showAndWait();
        return data;
    }

    private void initializeStage() {
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Insert columns for graph's X and Y axis");

        GridPane gridPane = createGridPane();
        addFieldsToGrid(gridPane);

        Scene scene = new Scene(gridPane, 500, 200);
        popupStage.setScene(scene);
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        return gridPane;
    }

    private void addFieldsToGrid(GridPane gridPane) {
        Label XaxisFromLabel = new Label("column for X axis:");
        TextField XaxisFromField = new TextField();
        Label YaxisToLabel = new Label("column for Y axis:");
        TextField YaxisToField = new TextField();
        Label xTitle = new Label("X Axis Title:");
        TextField xTitleField = new TextField();
        Label yTitle = new Label("Y Axis Title:");
        TextField yTitleField = new TextField();

        gridPane.add(XaxisFromLabel, 0, 0);
        gridPane.add(XaxisFromField, 1, 0);
        gridPane.add(YaxisToLabel, 0, 1);
        gridPane.add(YaxisToField, 1, 1);
        gridPane.add(xTitle, 0, 2);
        gridPane.add(xTitleField, 1, 2);
        gridPane.add(yTitle, 0, 3);
        gridPane.add(yTitleField, 1, 3);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> handleSubmit(XaxisFromField, YaxisToField, xTitleField, yTitleField));
        gridPane.add(submitButton, 1, 4);
    }

    private void handleSubmit(TextField XaxisFromField, TextField YaxisToField, TextField xTitleField, TextField yTitleField) {
        data.add(XaxisFromField.getText().toUpperCase());
        data.add(YaxisToField.getText().toUpperCase());
        data.add(xTitleField.getText());
        data.add(yTitleField.getText());
        popupStage.close();
    }
}

