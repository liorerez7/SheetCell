package Controller.Main;

import Controller.Ranges.RangeStringsData;
import Controller.Ranges.SortRowsData;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PopUpWindowsHandler {

    public RangeStringsData openAddRangeWindow() {
        // Create a new stage (window)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this one is closed
        popupStage.setTitle("Insert Range");

        // Create a GridPane layout for the popup
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Create and add the labels and text fields
        Label nameLabel = new Label("Range Name:");
        TextField nameField = new TextField();
        Label rangeLabel = new Label("Range:");
        TextField rangeField = new TextField();

        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameField, 1, 0);
        gridPane.add(rangeLabel, 0, 1);
        gridPane.add(rangeField, 1, 1);

        // Create and add the submit button
        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 1, 2);
        RangeStringsData rangeStringsData = new RangeStringsData();
        submitButton.setOnAction(e -> {
            String rangeName = nameField.getText();
            String range = rangeField.getText();
            // Handle the submission of the range name and range here
            // For now, we just close the popup
            rangeStringsData.setRange(range);
            rangeStringsData.setName(rangeName);
            popupStage.close();
        });
        // Set the scene and show the popup window
        Scene scene = new Scene(gridPane, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
        return rangeStringsData;
    }

    public RangeStringsData openDeleteRangeWindow() {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this one is closed
        popupStage.setTitle("Insert range to delete");

        // Create a GridPane layout for the popup
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Create and add the labels and text fields
        Label nameLabel = new Label("Range Name:");
        TextField nameField = new TextField();

        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameField, 1, 0);

        // Create and add the submit button
        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 1, 2);
        RangeStringsData rangeStringsData = new RangeStringsData();
        submitButton.setOnAction(e -> {
            String rangeName = nameField.getText();
            // Handle the submission of the range name and range here
            // For now, we just close the popup
            rangeStringsData.setName(rangeName);
            rangeStringsData.setRange(null);
            popupStage.close();
        });
        // Set the scene and show the popup window
        Scene scene = new Scene(gridPane, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
        return rangeStringsData;
    }

    public SortRowsData openSortRowsWindow() {

        SortRowsData sortRowsData = new SortRowsData();

        // Create a new stage (window)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this one is closed
        popupStage.setTitle("Insert sorting parameters");

        // Create a GridPane layout for the popup
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Create and add the labels and text fields
        Label rangeForSortingLabel = new Label("Range for sorting:");
        TextField rangeForSortingField = new TextField();
        Label columnsSortLabel = new Label("By which columns:");
        TextField columnsSortField = new TextField();

        gridPane.add(rangeForSortingLabel, 0, 0);
        gridPane.add(rangeForSortingField, 1, 0);
        gridPane.add(columnsSortLabel, 0, 1);
        gridPane.add(columnsSortField, 1, 1);

        // Create and add the submit button
        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 1, 2);

        submitButton.setOnAction(e -> {


            sortRowsData.setColumnsToSortBy(columnsSortField.getText());
            sortRowsData.setRange(rangeForSortingField.getText());


            popupStage.close();
        });

        // Set the scene and show the popup window
        Scene scene = new Scene(gridPane, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
        return sortRowsData;
    }
}
