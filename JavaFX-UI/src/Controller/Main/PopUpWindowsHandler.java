package Controller.Main;

import Controller.Grid.GridController;
import Controller.Utility.FilterGridData;
import Controller.Utility.RangeStringsData;
import Controller.Utility.SortRowsData;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import Utility.DtoContainerData;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.*;
import java.util.function.Consumer;

public class PopUpWindowsHandler {

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

    public RangeStringsData openAddRangeWindow() {
        // Create a new stage (window)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this one is closed
        popupStage.setTitle("Insert Range");

        // Create a GridPane layout for the popup
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Create and add the labels and text fields for Range-From and Range-To
        Label nameLabel = new Label("Range Name:");
        TextField nameField = new TextField();
        Label rangeFromLabel = new Label("Range-From (example, A2):");
        TextField rangeFromField = new TextField();
        Label rangeToLabel = new Label("Range-To (example, C5):");
        TextField rangeToField = new TextField();

        // Add the elements to the grid
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameField, 1, 0);
        gridPane.add(rangeFromLabel, 0, 1);
        gridPane.add(rangeFromField, 1, 1);
        gridPane.add(rangeToLabel, 0, 2);
        gridPane.add(rangeToField, 1, 2);

        // Create and add the submit button
        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 1, 3);
        RangeStringsData rangeStringsData = new RangeStringsData();
        submitButton.setOnAction(e -> {
            String rangeName = nameField.getText();
            String rangeFrom = rangeFromField.getText();
            String rangeTo = rangeToField.getText();
            String range = rangeFrom + ".." + rangeTo; // Combine Range-From and Range-To into a single range

            // Set the data in the RangeStringsData object
            rangeStringsData.setRange(range);
            rangeStringsData.setName(rangeName);

            popupStage.close();
        });

        // Set the scene and show the popup window
        Scene scene = new Scene(gridPane, 350, 150);
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

        // Create and add the labels and text fields for Range-From and Range-To
        Label rangeFromLabel = new Label("Range-From (example, A2):");
        TextField rangeFromField = new TextField();
        Label rangeToLabel = new Label("Range-To (example, C5):");
        TextField rangeToField = new TextField();

        Label columnsSortLabel = new Label("By which columns:");
        TextField columnsSortField = new TextField();

        // Add the elements to the grid
        gridPane.add(rangeFromLabel, 0, 0);
        gridPane.add(rangeFromField, 1, 0);
        gridPane.add(rangeToLabel, 0, 1);
        gridPane.add(rangeToField, 1, 1);
        gridPane.add(columnsSortLabel, 0, 2);
        gridPane.add(columnsSortField, 1, 2);

        // Create and add the submit button
        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 1, 3);

        submitButton.setOnAction(e -> {

            // Combine Range-From and Range-To into the range format (e.g., "A2..C5")
            String rangeFrom = rangeFromField.getText();
            String rangeTo = rangeToField.getText();
            String range = rangeFrom + ".." + rangeTo;

            // Set the data in the SortRowsData object
            sortRowsData.setRange(range);
            sortRowsData.setColumnsToSortBy(columnsSortField.getText());

            popupStage.close();
        });

        // Set the scene and show the popup window
        Scene scene = new Scene(gridPane, 350, 150);
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

        // Create and add the labels and text fields for Range-From and Range-To
        Label rangeFromLabel = new Label("Range-From (example, A2):");
        TextField rangeFromField = new TextField();
        Label rangeToLabel = new Label("Range-To (example, C5):");
        TextField rangeToField = new TextField();

        Label columnSortLabel = new Label("By which columns:");
        TextField columnSortField = new TextField();

        // Add the elements to the grid
        gridPane.add(rangeFromLabel, 0, 0);
        gridPane.add(rangeFromField, 1, 0);
        gridPane.add(rangeToLabel, 0, 1);
        gridPane.add(rangeToField, 1, 1);
        gridPane.add(columnSortLabel, 0, 2);
        gridPane.add(columnSortField, 1, 2);

        // Create and add the submit button
        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 1, 3);
        FilterGridData filterGridData = new FilterGridData();
        submitButton.setOnAction(e -> {

            String rangeFrom = rangeFromField.getText();
            String rangeTo = rangeToField.getText();
            String range = rangeFrom + ".." + rangeTo; // Combine Range-From and Range-To into the range format

            String column = columnSortField.getText();

            // Handle the submission of the range name and column here
            filterGridData.setRange(range);  // Store the combined range
            filterGridData.setColumnsToFilterBy(column);
            popupStage.close();
        });

        // Set the scene and show the popup window
        Scene scene = new Scene(gridPane, 350, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
        return filterGridData;
    }

    public Map<Character, Set<String>> openFilterDataPopUp(Map<Character, Set<String>> stringsInChosenColumn) {
        // Create a new Map to store the user's selected values
        Map<Character, Set<String>> selectedValues = new HashMap<>();

        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this one is closed
        popupStage.setTitle("Filter Data");

        // Create a GridPane layout for the popup
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Create column constraints for centering
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        column1.setHalignment(HPos.CENTER);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS);
        column2.setHalignment(HPos.CENTER);

        gridPane.getColumnConstraints().addAll(column1, column2);

        // Iterate over the input map and create checkboxes for each unique value in the chosen column
        int rowIndex = 0;
        for (Map.Entry<Character, Set<String>> entry : stringsInChosenColumn.entrySet()) {
            Character column = entry.getKey();
            Set<String> uniqueValues = entry.getValue();

            // Add a label for the column
            Label columnLabel = new Label("Column: " + column);
            gridPane.add(columnLabel, 0, rowIndex);

            // Use a FlowPane to arrange checkboxes horizontally
            FlowPane checkBoxContainer = new FlowPane(10, 10); // Hgap and Vgap
            checkBoxContainer.setPadding(new Insets(5));
            checkBoxContainer.setPrefWrapLength(300); // Wraps when it exceeds this width

            // Set to store selected values for this column
            Set<String> selectedStrings = new HashSet<>();

            // Create a checkbox for each unique value
            for (String value : uniqueValues) {
                CheckBox checkBox = new CheckBox(value);

                // Add an event listener to the checkbox to track selections
                checkBox.setOnAction(e -> {
                    if (checkBox.isSelected()) {
                        selectedStrings.add(value); // Add selected value
                    } else {
                        selectedStrings.remove(value); // Remove deselected value
                    }
                });

                // Add checkbox to the FlowPane
                checkBoxContainer.getChildren().add(checkBox);
            }

            // Add the FlowPane of checkboxes to the GridPane
            gridPane.add(checkBoxContainer, 1, rowIndex);

            // Add the selected values for this column to the final map on submit
            selectedValues.put(column, selectedStrings);

            rowIndex++; // Move to the next row for the next column
        }

        // Create and add the submit button
        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 0, rowIndex, 2, 1); // Span the button across two columns
        GridPane.setHalignment(submitButton, HPos.CENTER);

        // Close the popup when the submit button is clicked
        submitButton.setOnAction(e -> {
            popupStage.close();
        });

        // Set the scene and show the popup window
        Scene scene = new Scene(gridPane, 500, 300); // Adjust window size as needed
        popupStage.setScene(scene);
        popupStage.showAndWait();

        // Return the map of selected values
        return selectedValues;
    }

    // Helper method to create a popup grid
    private void openGridPopUp(String title, GridController gridScrollerController,
                               Consumer<GridPane> gridInitializer) {

        // Create a new Stage (window) for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
        popupStage.setTitle(title);

        // Create a new GridPane for the popup
        GridPane popupGrid = new GridPane();
        popupGrid.getStylesheets().add(Objects.requireNonNull(getClass().getResource("../Grid/ExelBasicGrid.css")).toExternalForm());

        // Initialize the grid
        gridInitializer.accept(popupGrid);

        // Wrap the GridPane inside a ScrollPane
        ScrollPane gridScrollPane = new ScrollPane(popupGrid);
        gridScrollPane.setFitToWidth(true);  // Optionally allow the ScrollPane to fit the grid's width
        gridScrollPane.setFitToHeight(true); // Optionally allow the ScrollPane to fit the grid's height

        // Create a Scene with the ScrollPane
        Scene popupScene = new Scene(gridScrollPane); // Use ScrollPane instead of GridPane
        popupStage.setScene(popupScene);

        // Show the popup window
        popupStage.showAndWait();
    }

    // Refactored methods
    public void openFilterGridPopUp(DtoContainerData dtoContainerData, GridController gridScrollerController) {
        openGridPopUp("Filter Grid", gridScrollerController,
                popupGrid -> gridScrollerController.initializeFilterPopupGrid(popupGrid, dtoContainerData));
    }

    public void openSortGridPopUp(DtoContainerData dtoContainerData, GridController gridScrollerController) {
        openGridPopUp("Sorted Rows", gridScrollerController,
                popupGrid -> gridScrollerController.initializeSortPopupGrid(popupGrid, dtoContainerData));
    }

    public void openVersionGridPopUp(DtoSheetCell dtoSheetCell, int versionNumber, GridController gridScrollerController) {
        openGridPopUp("Version Grid " + versionNumber, gridScrollerController,
                popupGrid -> gridScrollerController.initializeVersionPopupGrid(popupGrid, dtoSheetCell));
    }

}
