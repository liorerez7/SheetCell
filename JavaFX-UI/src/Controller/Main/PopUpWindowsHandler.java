package Controller.Main;

import Controller.Utility.FilterGridData;
import Controller.Utility.RangeStringsData;
import Controller.Utility.SortRowsData;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public FilterGridData openFilterDataWindow() {

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this one is closed
            popupStage.setTitle("Insert range for filtering");

            // Create a GridPane layout for the popup
            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            // Create and add the labels and text fields
            Label rangeForFilteringLabel = new Label("Range for filtering:");
            TextField rangeForFilteringField = new TextField();
            Label columnSortLabel = new Label("By which column:");
            TextField columnSortField = new TextField();


            gridPane.add(rangeForFilteringLabel, 0, 0);
            gridPane.add(rangeForFilteringField, 1, 0);
            gridPane.add(columnSortLabel, 0, 1);
            gridPane.add(columnSortField, 1, 1);


            // Create and add the submit button
            Button submitButton = new Button("Submit");
            gridPane.add(submitButton, 1, 2);
            FilterGridData filterGridData = new FilterGridData();
            submitButton.setOnAction(e -> {

                String range = rangeForFilteringField.getText();
                String column = columnSortField.getText();

                // Handle the submission of the range name and range here
                // For now, we just close the popup
                filterGridData.setRange(range);
                filterGridData.setColumnsToFilterBy(column);
                popupStage.close();
            });
            // Set the scene and show the popup window
            Scene scene = new Scene(gridPane, 300, 100);
            popupStage.setScene(scene);
            popupStage.showAndWait();
            return filterGridData;

    }


    public String openFilterDataPopUp(Set<String> stringsInChosenColumn) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this one is closed
        popupStage.setTitle("Insert filter values");

        // Create a GridPane layout for the popup
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Set column constraints for centering the button
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        column1.setHalignment(HPos.CENTER);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS);
        column2.setHalignment(HPos.CENTER);

        gridPane.getColumnConstraints().addAll(column1, column2);

        // Calculate the required width based on the size of the string set
        double baseWidth = 420; // Base width for window
        double additionalWidth = Math.min(stringsInChosenColumn.size() * 30, 400); // Extra width depends on the number of items
        double windowWidth = baseWidth + additionalWidth;
        double windowHeight = 100; // Adjust the height to accommodate the button positioning

        // Create and add the labels and text fields
        Label filterValueLabel = new Label("Enter values to filter (comma-separated): " + stringsInChosenColumn);
        TextField filterValueField = new TextField();

        gridPane.add(filterValueLabel, 0, 0);
        gridPane.add(filterValueField, 1, 0);

        // Create and add the submit button
        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 0, 1, 2, 1); // Span the button across two columns
        GridPane.setHalignment(submitButton, HPos.CENTER); // Center the button horizontally
        GridPane.setMargin(filterValueField, new Insets(0, 10, 0, 0)); // Right margin of 20 pixels

        List<String> stringToFilterBy = new ArrayList<>();

        submitButton.setOnAction(e -> {
            stringToFilterBy.add(filterValueField.getText()); // Capture the text from the field
            popupStage.close();
        });

        // Set the scene and show the popup window
        Scene scene = new Scene(gridPane, windowWidth, windowHeight);
        popupStage.setScene(scene);
        popupStage.showAndWait();

        if (stringToFilterBy.isEmpty()) {
            return null;
        }

        return stringToFilterBy.get(0); // Return the first entered value
    }

//    public String openFilterDataPopUp(Set<String> stringsInChosenColumn) {
//        Stage popupStage = new Stage();
//        popupStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this one is closed
//        popupStage.setTitle("Insert filter values");
//
//        // Create a GridPane layout for the popup
//        GridPane gridPane = new GridPane();
//        gridPane.setHgap(10);
//        gridPane.setVgap(10);
//
//
//        // Calculate the required width based on the size of the string set
//        double baseWidth = 400; // Base width for window
//        double additionalWidth = Math.min(stringsInChosenColumn.size() * 30, 400); // Extra width depends on the number of items
//        double windowWidth = baseWidth + additionalWidth;
//
//        double windowHeight = 100;
//
//        // Create and add the labels and text fields
//        Label filterValueLabel = new Label("Enter values to filter (comma-separated):" + stringsInChosenColumn);
//        TextField filterValueField = new TextField();
//
//        gridPane.add(filterValueLabel, 0, 0);
//        gridPane.add(filterValueField, 1, 0);
//
//        // Create and add the submit button
//        Button submitButton = new Button("Submit");
//        gridPane.add(submitButton, 1, 1);
//
//        List<String> stringToFilterBy = new ArrayList<>();
//
//
//        submitButton.setOnAction(e -> {
//            stringToFilterBy.add(filterValueField.getText()); // Capture the text from the field
//            popupStage.close();
//        });
//
//        // Set the scene and show the popup window
//        Scene scene = new Scene(gridPane, windowWidth, windowHeight);
//        popupStage.setScene(scene);
//        popupStage.showAndWait();
//
//        if(stringToFilterBy.isEmpty()) {
//            return null;
//        }
//
//        return stringToFilterBy.getFirst();
//    }
}
