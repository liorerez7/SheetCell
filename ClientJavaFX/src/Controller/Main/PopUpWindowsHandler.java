package Controller.Main;

import Controller.Grid.GridController;
import Controller.HttpUtility.Constants;
import Controller.HttpUtility.HttpRequestManager;
import Controller.JavaFXUtility.FilterGridData;
import Controller.JavaFXUtility.RangeStringsData;
import Controller.JavaFXUtility.RunTimeAnalysisData;
import Controller.JavaFXUtility.SortRowsData;

import DtoComponents.DtoContainerData;
import DtoComponents.DtoSheetCell;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.Response;
import smallParts.CellLocation;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PopUpWindowsHandler {


    public void createErrorPopUpCircularDependency(DtoSheetCell dtoSheetCell, GridController gridScrollerController , List<CellLocation> cycle) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Circular Dependency Error");

        // Create a new GridPane and initialize it with data
        GridPane popupGrid = new GridPane();

        //popupGrid.getStylesheets().add(Objects.requireNonNull(getClass().getResource("../Grid/ExelBasicGrid.css")).toExternalForm());
        popupGrid.getStylesheets().add("Controller/Grid/ExelBasicGrid.css");


        // Initialize the grid with the DtoSheetCell and cycle
        // Assuming gridScrollerController is accessible in this context
        gridScrollerController.initializeCirclePopUp(popupGrid, dtoSheetCell, cycle);

        // Wrap the grid with a ScrollPane
        ScrollPane scrollPane = new ScrollPane(popupGrid);

        // Create a Scene and show the popup
        Scene popupScene = new Scene(scrollPane);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    public void createErrorPopup(String message, String title) {
        Stage popupStage = new Stage();
        popupStage.setTitle(title);

        // Create a Label with the error message
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true); // Enable text wrapping
        messageLabel.setPadding(new Insets(10));

        // Create a layout and add the Label
        StackPane layout = new StackPane();
        layout.getChildren().add(messageLabel);

        // Wrap the layout with a ScrollPane
        ScrollPane scrollPane = new ScrollPane(layout);

        // Create the Scene with the ScrollPane
        Scene scene = new Scene(scrollPane);

        // Adjust size based on content
        scene.widthProperty().addListener((obs, oldVal, newVal) -> popupStage.sizeToScene());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> popupStage.sizeToScene());

        scene.getStylesheets().add("Controller/Main/ErrorPopUp.css");


        //scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ErrorPopup.css")).toExternalForm());
        messageLabel.getStyleClass().add("popup-label");
        layout.getStyleClass().add("popup-container");

        // Set the scene and modality
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(scene);
        popupStage.show();
    }

//    public RangeStringsData openDeleteRangeWindow() {
//
//        Stage popupStage = new Stage();
//        popupStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this one is closed
//        popupStage.setTitle("Insert range to delete");
//
//        // Create a GridPane layout for the popup
//        GridPane gridPane = new GridPane();
//        gridPane.setHgap(10);
//        gridPane.setVgap(10);
//
//        // Create and add the labels and text fields
//        Label nameLabel = new Label("Range Name:");
//        TextField nameField = new TextField();
//
//        gridPane.add(nameLabel, 0, 0);
//        gridPane.add(nameField, 1, 0);
//
//        // Create and add the submit button
//        Button submitButton = new Button("Submit");
//        gridPane.add(submitButton, 1, 2);
//        RangeStringsData rangeStringsData = new RangeStringsData();
//        submitButton.setOnAction(e -> {
//            String rangeName = nameField.getText();
//            // Handle the submission of the range name and range here
//            // For now, we just close the popup
//            rangeStringsData.setName(rangeName);
//            rangeStringsData.setRange(null);
//            popupStage.close();
//        });
//        // Set the scene and show the popup window
//        Scene scene = new Scene(gridPane, 300, 200);
//        popupStage.setScene(scene);
//        popupStage.showAndWait();
//        return rangeStringsData;
//    }


    public RangeStringsData openDeleteRangeWindow(Set<String> rangeNames) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this one is closed
        popupStage.setTitle("Insert range to delete");

        // Create a GridPane layout for the popup
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Create and add the label and ComboBox
        Label nameLabel = new Label("Select Range:");
        ComboBox<String> rangeComboBox = new ComboBox<>();
        rangeComboBox.getItems().addAll(rangeNames); // Add range names to ComboBox

        gridPane.add(nameLabel, 0, 0);
        gridPane.add(rangeComboBox, 1, 0);

        // Create and add the submit button
        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 1, 2);

        RangeStringsData rangeStringsData = new RangeStringsData();
        submitButton.setOnAction(e -> {
            String selectedRangeName = rangeComboBox.getValue();
            // Handle the submission of the selected range name
            if (selectedRangeName != null) {
                rangeStringsData.setName(selectedRangeName);
                rangeStringsData.setRange(null); // Assuming you still want to set this to null
                popupStage.close();
            } else {
                // Optionally, handle the case when no selection is made
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a range to delete.", ButtonType.OK);
                alert.showAndWait();
            }
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

        // Label for columns selection
        Label columnsSortLabel = new Label("Select columns to sort by:");

        // ComboBox for selecting columns (initially empty)
        ComboBox<String> columnsComboBox = new ComboBox<>();
        columnsComboBox.setPromptText("Select a column");

        // ListView to display selected columns in order
        ListView<String> selectedColumnsListView = new ListView<>();
        selectedColumnsListView.setPrefHeight(100); // Set a preferred height for the ListView

        // Button to add selected column
        Button addColumnButton = new Button("Add Column");

        // Button to remove selected column
        Button removeColumnButton = new Button("Remove Column");

        // Button to finalize the range
        Button finalizeRangeButton = new Button("Finalize Range");

        // Add the elements to the grid
        gridPane.add(rangeFromLabel, 0, 0);
        gridPane.add(rangeFromField, 1, 0);
        gridPane.add(rangeToLabel, 0, 1);
        gridPane.add(rangeToField, 1, 1);
        gridPane.add(finalizeRangeButton, 0, 2, 2, 1); // Place the Finalize button
        gridPane.add(columnsSortLabel, 0, 3);
        gridPane.add(columnsComboBox, 1, 3);
        gridPane.add(addColumnButton, 1, 4);
        gridPane.add(selectedColumnsListView, 1, 5);
        gridPane.add(removeColumnButton, 1, 6);

        // Create and add the submit button, initially disabled
        Button submitButton = new Button("Submit");
        submitButton.setDisable(true); // Initially disabled
        GridPane.setMargin(submitButton, new Insets(10)); // Add margin around the submit button
        gridPane.add(submitButton, 1, 7);

        // Disable all elements initially except for the Finalize button
        columnsComboBox.setDisable(true);
        addColumnButton.setDisable(true);
        removeColumnButton.setDisable(true);
        submitButton.setDisable(true);

        // Handle clicking the Finalize button
        finalizeRangeButton.setOnAction(e -> {
            // Get the range values
            String rangeFrom = rangeFromField.getText().trim();
            String rangeTo = rangeToField.getText().trim();

            rangeTo = rangeTo.toUpperCase();
            rangeFrom = rangeFrom.toUpperCase();

            // Extract columns from the range (e.g., A2 to C7)
            String startColumn = rangeFrom.substring(0, 1); // Get the starting column letter (e.g., A)
            String endColumn = rangeTo.substring(0, 1); // Get the ending column letter (e.g., C)

            // Populate the combo box with the range columns
            columnsComboBox.getItems().clear();
            for (char c = startColumn.charAt(0); c <= endColumn.charAt(0); c++) {
                columnsComboBox.getItems().add(String.valueOf(c));
            }

            // Enable combo box and buttons after filling the range
            columnsComboBox.setDisable(false);
            addColumnButton.setDisable(false);
            removeColumnButton.setDisable(false);
            submitButton.setDisable(true); // Ensure submit is disabled until a column is added
        });

        // Handle adding columns to the ListView
        addColumnButton.setOnAction(e -> {
            String selectedColumn = columnsComboBox.getSelectionModel().getSelectedItem();
            if (selectedColumn != null && !selectedColumnsListView.getItems().contains(selectedColumn)) {
                selectedColumnsListView.getItems().add(selectedColumn);
                columnsComboBox.getSelectionModel().clearSelection(); // Clear the selection
                // Enable the submit button if at least one column is selected
                submitButton.setDisable(selectedColumnsListView.getItems().isEmpty());
            }
        });

        // Handle removing selected columns from the ListView
        removeColumnButton.setOnAction(e -> {
            String selectedItem = selectedColumnsListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                selectedColumnsListView.getItems().remove(selectedItem);
                // Disable the submit button if no columns are left
                submitButton.setDisable(selectedColumnsListView.getItems().isEmpty());
            }
        });

        submitButton.setOnAction(e -> {
            // Get the range values
            String rangeFrom = rangeFromField.getText();
            String rangeTo = rangeToField.getText();
            String range = rangeFrom + ".." + rangeTo; // Combine Range-From and Range-To into the range format

            // Collect the selected columns from ListView
            List<String> selectedColumns = selectedColumnsListView.getItems();

            // Join selected columns into "A,C,B" format based on their order
            String columns = String.join(",", selectedColumns);

            // Handle the submission of the range and selected columns
            sortRowsData.setRange(range);  // Store the combined range
            sortRowsData.setColumnsToSortBy(columns); // Store the selected columns
            popupStage.close();
        });

        // Set the scene and show the popup window
        Scene scene = new Scene(gridPane, 500, 400); // Adjusted size for ListView
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

        Label columnSortLabel = new Label("Columns to filter by:");
        HBox checkBoxContainer = new HBox(10); // Container for dynamic checkboxes

        // Add the elements to the grid
        gridPane.add(rangeFromLabel, 0, 0);
        gridPane.add(rangeFromField, 1, 0);
        gridPane.add(rangeToLabel, 0, 1);
        gridPane.add(rangeToField, 1, 1);
        gridPane.add(columnSortLabel, 0, 2);
        gridPane.add(checkBoxContainer, 1, 2); // Empty at first, will be populated later

        // Create and add the submit button, initially disabled
        Button submitButton = new Button("Submit");
        submitButton.setDisable(true); // Initially disabled
        gridPane.add(submitButton, 1, 4);

        // Button to dynamically generate checkboxes
        Button chooseColumnsButton = new Button("Choose Columns");
        gridPane.add(chooseColumnsButton, 1, 3);

        FilterGridData filterGridData = new FilterGridData();

        chooseColumnsButton.setOnAction(e -> {
            String rangeFrom = rangeFromField.getText();
            String rangeTo = rangeToField.getText();

            // Extract column letters from the Range-From and Range-To (e.g., A from A2, C from C5)
            char startColumn = rangeFrom.charAt(0); // Assuming format like A2
            char endColumn = rangeTo.charAt(0); // Assuming format like C5

            // Clear any previously generated checkboxes
            checkBoxContainer.getChildren().clear();

            // Create checkboxes dynamically for each column from startColumn to endColumn
            for (char col = startColumn; col <= endColumn; col++) {
                CheckBox checkBox = new CheckBox(String.valueOf(col));
                checkBoxContainer.getChildren().add(checkBox);
            }

            // Enable the submit button after columns are chosen
            submitButton.setDisable(false);
        });

        submitButton.setOnAction(e -> {
            // Get the range values
            String rangeFrom = rangeFromField.getText();
            String rangeTo = rangeToField.getText();
            String range = rangeFrom + ".." + rangeTo; // Combine Range-From and Range-To into the range format

            // Collect the selected columns from dynamically generated checkboxes
            List<String> selectedColumns = new ArrayList<>();
            for (Node node : checkBoxContainer.getChildren()) {
                if (node instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) node;
                    if (checkBox.isSelected()) {
                        selectedColumns.add(checkBox.getText());
                    }
                }
            }

            String column = String.join(",", selectedColumns); // Join selected columns into "A,C,D" format

            // Handle the submission of the range and selected columns
            filterGridData.setRange(range);  // Store the combined range
            filterGridData.setColumnsToFilterBy(column); // Store the selected columns
            popupStage.close();
        });

        // Set the scene and show the popup window
        Scene scene = new Scene(gridPane, 400, 200); // Adjusted size for checkboxes
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

                if(value.equals("")){
                    continue;
                }

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
        popupGrid.getStylesheets().add("Controller/Grid/ExelBasicGrid.css");

        //popupGrid.getStylesheets().add(Objects.requireNonNull(getClass().getResource("../Grid/ExelBasicGrid.css")).toExternalForm());

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

    public RunTimeAnalysisData openRunTimeAnalysisWindow() {

        RunTimeAnalysisData runTimeAnalysisData = new RunTimeAnalysisData("", 0, 0, 0);

        // Create a new stage (window)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this one is closed
        popupStage.setTitle("Insert runtime analysis parameters");

        // Create a GridPane layout for the popup
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20)); // Add padding around the grid

        // Create and add the labels and text fields for Cell ID, Starting Value, and Last Value
        Label cellIdLabel = new Label("Cell ID (example, B3):");
        TextField cellIdField = new TextField();
        Label startingValueLabel = new Label("Starting Value:");
        TextField startingValueField = new TextField();
        Label lastValueLabel = new Label("Last Value:");
        TextField lastValueField = new TextField();
        Label stepValueLabel = new Label("Step Value:");
        TextField stepValueField = new TextField();

        // Add the elements to the grid
        gridPane.add(cellIdLabel, 0, 0);
        gridPane.add(cellIdField, 1, 0);
        gridPane.add(startingValueLabel, 0, 1);
        gridPane.add(startingValueField, 1, 1);
        gridPane.add(lastValueLabel, 0, 2);
        gridPane.add(lastValueField, 1, 2);
        gridPane.add(stepValueLabel, 0, 3);
        gridPane.add(stepValueField, 1, 3);

        // Create and add the submit button
        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 1, 4);

        // Set the action when the submit button is clicked
        submitButton.setOnAction(e -> {
            try {
                // Get the data from the fields
                String cellId = cellIdField.getText();
                int startingValue = Integer.parseInt(startingValueField.getText());
                int endingValue = Integer.parseInt(lastValueField.getText());
                int stepValue = Integer.parseInt(stepValueField.getText());

                // Set the data in the RunTimeAnalysisData object
                runTimeAnalysisData.setCellId(cellId);
                runTimeAnalysisData.setStartingValue(startingValue);
                runTimeAnalysisData.setEndingValue(endingValue);
                runTimeAnalysisData.setStepValue(stepValue);

                // Close the popup window
                popupStage.close();
            } catch (NumberFormatException ex) {
                // Handle the case where the input is not a valid integer
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Please enter valid integer values for Starting Value and Last Value.");
                alert.showAndWait();
            }
        });

        // Set the scene and show the popup window
        Scene scene = new Scene(gridPane, 350, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();

        // Return the collected data
        return runTimeAnalysisData;
    }

//    public void showRuntimeAnalysisPopup(
//            DtoSheetCell sheetCellRunTime,
//            int startingValue, int endingValue, int stepValue,
//            double currentVal, char col, String row,
//            Model model, GridController gridScrollerController) {
//
//        String title = "Run Time Analysis";
//        Stage popupStage = new Stage();
//        popupStage.initModality(Modality.APPLICATION_MODAL);
//        popupStage.setTitle(title);
//
//        // Create a new GridPane for the popup
//        GridPane popupGrid = new GridPane();
//        popupGrid.getStylesheets().add("Controller/Grid/ExelBasicGrid.css");
//
//        // Initialize the grid and bind the model to the grid's labels
//        Platform.runLater(() -> {
//            Map<CellLocation, Label> cellLocationLabelMap = gridScrollerController.initializeRunTimeAnalysisPopupGrid(popupGrid, sheetCellRunTime);
//            model.setCellLabelToPropertiesRunTimeAnalysis(cellLocationLabelMap);
//            model.bindCellLabelToPropertiesRunTimeAnalysis();
//            model.setPropertiesByDtoSheetCellRunTimeAnalsys(sheetCellRunTime);
//
//            // Create a VBox to hold the Cell ID label, slider, and the current value label
//            VBox sliderBox = new VBox(10);
//            sliderBox.setAlignment(Pos.CENTER);
//
//            // Create a Label for the Cell ID above the Slider
//            Label cellIdLabel = new Label("Cell ID: " + col + row);
//            cellIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
//
//            // Create a Slider with dynamic range and step values
//            Slider valueSlider = new Slider(startingValue, endingValue, currentVal);
//            valueSlider.setBlockIncrement(stepValue);
//            valueSlider.setMajorTickUnit(stepValue);
//
//            if ((endingValue - startingValue) / stepValue - 1 > 5) {
//                valueSlider.setMinorTickCount((endingValue - startingValue) / stepValue - 1);
//            } else {
//                valueSlider.setMinorTickCount(5);
//            }
//            valueSlider.setSnapToTicks(true);
//            valueSlider.setShowTickMarks(true);
//            valueSlider.setShowTickLabels(true);
//
//            // Create a Label below the Slider to display the current value
//            Label valueLabel = new Label("Value: " + currentVal);
//            valueLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
//
//            // Add a listener to the slider to update the label and grid in real time
//            valueSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
//                int newValue = (int) Math.round(newVal.doubleValue() / stepValue) * stepValue;
//                if (newValue > endingValue) {
//                    newValue -= stepValue;
//                }
//                valueSlider.setValue(newValue);  // Snap slider to nearest step value
//                valueLabel.setText("Value: " + newValue);
//
//                String newValueStr = String.valueOf(newValue);
//                CompletableFuture.runAsync(() -> {
//                    try {
//                        Map<String, String> map = new HashMap<>();
//                        map.put("newValue", newValueStr);
//                        map.put("colLocation", col + "");
//                        map.put("rowLocation", row);
//
//                        updateCellRequest(map);
//
//                        DtoSheetCell updatedSheetCell = fetchDtoSheetCell();
//
//                        // Update the UI with the new sheet cell information
//                        Platform.runLater(() -> model.setPropertiesByDtoSheetCellRunTimeAnalsys(updatedSheetCell));
//
//                    } catch (Exception e) {
//                        Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error updating cell"));
//                    }
//                });
//            });
//
//            // Add the Cell ID Label, Slider, and Value Label to the VBox
//            sliderBox.getChildren().addAll(cellIdLabel, valueSlider, valueLabel);
//
//            // Create a VBox to hold both the grid and the sliderBox
//            VBox contentBox = new VBox(10, popupGrid, sliderBox);
//            contentBox.setAlignment(Pos.CENTER_LEFT);
//            contentBox.setPadding(new Insets(10));
//
//            // Wrap the content (grid and sliderBox) inside a ScrollPane
//            ScrollPane contentScrollPane = new ScrollPane(contentBox);
//            contentScrollPane.setFitToWidth(true);
//            contentScrollPane.setFitToHeight(true);
//
//            // Create a Scene with the ScrollPane
//            Scene popupScene = new Scene(contentScrollPane);
//            popupStage.setScene(popupScene);
//
//            // Show the popup window
//            popupStage.showAndWait();
//        });
//    }

    public void showRuntimeAnalysisPopup(
            DtoSheetCell sheetCellRunTime,
            int startingValue, int endingValue, int stepValue,
            double currentVal, char col, String row,
            Model model, GridController gridScrollerController,
            Runnable onCloseCallback) {

        String title = "Run Time Analysis";
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        // Create a new GridPane for the popup
        GridPane popupGrid = new GridPane();
        popupGrid.getStylesheets().add("Controller/Grid/ExelBasicGrid.css");

        Platform.runLater(() -> {
            Map<CellLocation, Label> cellLocationLabelMap = gridScrollerController.initializeRunTimeAnalysisPopupGrid(popupGrid, sheetCellRunTime);
            model.setCellLabelToPropertiesRunTimeAnalysis(cellLocationLabelMap);
            model.bindCellLabelToPropertiesRunTimeAnalysis();
            model.setPropertiesByDtoSheetCellRunTimeAnalsys(sheetCellRunTime);

            VBox sliderBox = new VBox(10);
            sliderBox.setAlignment(Pos.CENTER);

            Label cellIdLabel = new Label("Cell ID: " + col + row);
            cellIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

            Slider valueSlider = new Slider(startingValue, endingValue, currentVal);
            valueSlider.setBlockIncrement(stepValue);
            valueSlider.setMajorTickUnit(stepValue);

            if ((endingValue - startingValue) / stepValue - 1 > 5) {
                valueSlider.setMinorTickCount((endingValue - startingValue) / stepValue - 1);
            } else {
                valueSlider.setMinorTickCount(5);
            }
            valueSlider.setSnapToTicks(true);
            valueSlider.setShowTickMarks(true);
            valueSlider.setShowTickLabels(true);

            Label valueLabel = new Label("Value: " + currentVal);
            valueLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");

            valueSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                int newValue = (int) Math.round(newVal.doubleValue() / stepValue) * stepValue;
                if (newValue > endingValue) {
                    newValue -= stepValue;
                }
                valueSlider.setValue(newValue);
                valueLabel.setText("Value: " + newValue);

                String newValueStr = String.valueOf(newValue);
                CompletableFuture.runAsync(() -> {
                    try {
                        Map<String, String> map = new HashMap<>();
                        map.put("newValue", newValueStr);
                        map.put("colLocation", col + "");
                        map.put("rowLocation", row);

                        updateCellRequest(map);

                        DtoSheetCell updatedSheetCell = fetchDtoSheetCell();

                        Platform.runLater(() -> model.setPropertiesByDtoSheetCellRunTimeAnalsys(updatedSheetCell));

                    } catch (Exception e) {
                        Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error updating cell"));
                    }
                });
            });

            sliderBox.getChildren().addAll(cellIdLabel, valueSlider, valueLabel);

            VBox contentBox = new VBox(10, popupGrid, sliderBox);
            contentBox.setAlignment(Pos.CENTER_LEFT);
            contentBox.setPadding(new Insets(10));

            ScrollPane contentScrollPane = new ScrollPane(contentBox);
            contentScrollPane.setFitToWidth(true);
            contentScrollPane.setFitToHeight(true);

            Scene popupScene = new Scene(contentScrollPane);
            popupStage.setScene(popupScene);

            // Set an action when the popup window is closed
            popupStage.setOnHidden(event -> {
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
            });

            popupStage.showAndWait();
        });
    }




    private boolean updateCellRequest(Map<String, String> map) {

//        try (Response updateCellResponse = HttpRequestManager.sendPostRequestSync(Constants.UPDATE_CELL_ENDPOINT, map)) {
//            if (!updateCellResponse.isSuccessful()) {
//                Platform.runLater(() -> createErrorPopup("Failed to update cell", "Error"));
//                return false;
//            }
//            return true;

        try (Response updateCellResponse = HttpRequestManager.sendPostRequestSync(Constants.UPDATE_CELL_ENDPOINT, map)) {
            if (!updateCellResponse.isSuccessful()) {
                String responseBody = updateCellResponse.body().string();
                System.err.println("Error response: " + responseBody);
                System.err.println("Status code: " + updateCellResponse.code());
                Platform.runLater(() -> createErrorPopup("Failed to update cell: " + responseBody, "Error"));
                return false;
            }
            return true;
        } catch (IOException e) {
            Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error saving current sheet cell state"));
            return false;
        }
    }

    // Helper method to fetch the DtoSheetCell from the server
    private DtoSheetCell fetchDtoSheetCell() {
        try (Response response = HttpRequestManager.sendGetRequestSync(Constants.GET_SHEET_CELL_ENDPOINT, new HashMap<>())) {
            if (!response.isSuccessful()) {
                Platform.runLater(() -> createErrorPopup("Failed to load sheet", "Error"));
                return null;
            }
            String dtoSheetCellAsJson = response.body().string();
            return Constants.GSON_INSTANCE.fromJson(dtoSheetCellAsJson, DtoSheetCell.class);
        } catch (IOException e) {
            Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error fetching sheet cell data"));
            return null;
        }
    }

    public List<String> openGraphWindow(){

        List<String > data = new ArrayList<>();

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this one is closed
        popupStage.setTitle("Insert columns for graph's X and Y axis");

        // Create a GridPane layout for the popup
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Create and add the labels and text fields for Range-From and Range-To
        Label XaxisFromLabel = new Label("column for X axis:");
        TextField XaxisFromField = new TextField();
        Label YaxisToLabel = new Label("column for Y axis:");
        TextField YaxisToField = new TextField();
        Label xTitle = new Label("X Axis Title:");
        TextField xTitleField = new TextField();
        Label yTitle = new Label("Y Axis Title:");
        TextField yTitleField = new TextField();

        // Add the elements to the grid
        gridPane.add(XaxisFromLabel, 0, 0);
        gridPane.add(XaxisFromField, 1, 0);
        gridPane.add(YaxisToLabel, 0, 1);
        gridPane.add(YaxisToField, 1, 1);
        gridPane.add(xTitle, 0, 2);
        gridPane.add(xTitleField, 1, 2);
        gridPane.add(yTitle, 0, 3);
        gridPane.add(yTitleField, 1, 3);

        // Create and add the submit button
        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 1, 4);

        submitButton.setOnAction(e -> {

            String Xaxis = XaxisFromField.getText();
            String Yaxis = YaxisToField.getText();
            String xTitle1 = xTitleField.getText();
            String yTitle1 = yTitleField.getText();

            // Handle the submission of the range name and column here
            data.add(Xaxis.toUpperCase());
            data.add(Yaxis.toUpperCase());
            data.add(xTitle1);
            data.add(yTitle1);
            popupStage.close();
        });

        // Set the scene and show the popup window
        Scene scene = new Scene(gridPane, 500, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
        return data;
    }

    public void openGraphPopUp(char xAxis, String xTitle, String yTitle, Map<Character, List<String>> columnsForXYaxis, boolean isBarChart) {

        char yAxis = ' ';

        // Extract X axis data from the Map
        List<String> xAxisListAsString = columnsForXYaxis.get(xAxis);
        List<String> yAxisListAsString = new ArrayList<>();

        // Variable to store Y axis data
        for (Map.Entry<Character, List<String>> entry : columnsForXYaxis.entrySet()) {
            if (entry.getKey() != xAxis) {
                yAxis = entry.getKey();
                yAxisListAsString = entry.getValue();
                break;  // Assume only one Y axis column exists, so break after finding it
            }
        }

        // Ensure both X and Y lists have the same size (optional check)
        if (xAxisListAsString.size() != yAxisListAsString.size()) {
            throw new IllegalArgumentException("X and Y axis data sets must be of equal size.");
        }

        // Prepare the Y axis values as Doubles
        List<Double> yAxisList = yAxisListAsString.stream()
                .map(Double::parseDouble)  // Convert String to Double
                .collect(Collectors.toList());

        // Call the method to create and display the graph popup
        showGraphPopup(xAxisListAsString, yAxisList, xTitle, yTitle, isBarChart);
    }

    private void showGraphPopup(List<String> xValues, List<Double> yValues, String xTitle, String yTitle, boolean isColumnChart) {
        // Create Y-axis as NumberAxis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yTitle);

        if (isColumnChart) {
            // For Column Chart, use CategoryAxis for X-axis
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel(xTitle);

            // Create a BarChart (Column Chart)
            BarChart<String, Number> columnChart = new BarChart<>(xAxis, yAxis);
            columnChart.setTitle("Column Chart");

            // Create a data series and populate it with the given X and Y values
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Data Series");

            // Define colors for different bars
            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.PURPLE};

            for (int i = 0; i < xValues.size(); i++) {
                final int index = i; // Create a final variable to capture the current index
                XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(xValues.get(i), yValues.get(i));
                dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle("-fx-background-color: " + toHexString(colors[index % colors.length]) + ";");
                    }
                });
                series.getData().add(dataPoint);
            }

            // Add the series to the column chart
            columnChart.getData().add(series);

            // Create a scene with the chart
            javafx.scene.Scene scene = new javafx.scene.Scene(columnChart, 800, 600);

            // Create a new stage (popup window)
            Stage popupStage = new Stage();
            popupStage.setTitle("Column Chart Popup");
            popupStage.setScene(scene);

            // Show the popup stage
            popupStage.show();

        } else {
            // For Line Chart, use NumberAxis for both X and Y axes
            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel(xTitle);

            // Create a LineChart
            LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Line Chart");

            // Create a data series and populate it with the given X and Y values
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Data Series");

            for (int i = 0; i < yValues.size(); i++) {
                series.getData().add(new XYChart.Data<>(Double.parseDouble(xValues.get(i)), yValues.get(i)));
            }

            // Add the series to the line chart
            lineChart.getData().add(series);

            // Create a scene with the chart
            javafx.scene.Scene scene = new javafx.scene.Scene(lineChart, 800, 600);

            // Create a new stage (popup window)
            Stage popupStage = new Stage();
            popupStage.setTitle("Line Chart Popup");
            popupStage.setScene(scene);

            // Show the popup stage
            popupStage.show();
        }
    }

    // Helper method to convert Color to hex string
    private String toHexString(Color color) {
        return String.format("#%02x%02x%02x",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public Map<Character, List<String>> openFilterDataWithOrderPopUp(char xAxis, char yAxis, String xTitle, String yTitle, Map<Character, Set<String>> stringsInChosenColumn) {
        // Create a new Map to store the user's selected values
        Map<Character, List<String>> selectedValues = new HashMap<>();

        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block other windows until this one is closed
        popupStage.setTitle("Filter Data with Order");

        // Create a GridPane layout for the popup
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        // Create column constraints for centering
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        column1.setHalignment(HPos.CENTER);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS);
        column2.setHalignment(HPos.CENTER);

        gridPane.getColumnConstraints().addAll(column1, column2);

        // To keep track of current selection order
        List<String> selectedXValues = new ArrayList<>();
        List<String> selectedYValues = new ArrayList<>();

        // Track the current pair index
        int rowIndex = 0;

        // Add title labels
        Label xAxisLabel = new Label("X-Axis: " + xTitle);
        Label yAxisLabel = new Label("Y-Axis: " + yTitle);
        gridPane.add(xAxisLabel, 0, rowIndex);
        gridPane.add(yAxisLabel, 1, rowIndex);
        rowIndex++;

        // Get the initial values for X and Y
        List<String> xValues = new ArrayList<>(stringsInChosenColumn.get(xAxis));
        List<String> yValues = new ArrayList<>(stringsInChosenColumn.get(yAxis));

        // Create ComboBoxes for X and Y values
        for (int i = 0; i < Math.min(xValues.size(), yValues.size()); i++) {
            ComboBox<String> xComboBox = new ComboBox<>();
            ComboBox<String> yComboBox = new ComboBox<>();

            // Populate ComboBoxes with available values
            xComboBox.getItems().addAll(xValues);
            yComboBox.getItems().addAll(yValues);

            // Add to grid
            gridPane.add(xComboBox, 0, rowIndex);
            gridPane.add(yComboBox, 1, rowIndex);

            // Add listeners to remove selected values from other ComboBoxes for X-axis
            xComboBox.setOnAction(e -> {
                String selectedX = xComboBox.getSelectionModel().getSelectedItem();
                if (!selectedXValues.contains(selectedX)) {
                    selectedXValues.add(selectedX);
                    xComboBox.setDisable(true); // Disable the comboBox after selection
                    // Remove selected X value from all other X ComboBoxes
                    for (Node node : gridPane.getChildren()) {
                        if (node instanceof ComboBox && GridPane.getColumnIndex(node) == 0) { // Only X-axis ComboBoxes
                            ComboBox<?> otherComboBox = (ComboBox<?>) node;
                            if (otherComboBox != xComboBox) {
                                otherComboBox.getItems().remove(selectedX);
                            }
                        }
                    }
                }
            });

            // Add listeners to remove selected values from other ComboBoxes for Y-axis
            yComboBox.setOnAction(e -> {
                String selectedY = yComboBox.getSelectionModel().getSelectedItem();
                if (!selectedYValues.contains(selectedY)) {
                    selectedYValues.add(selectedY);
                    yComboBox.setDisable(true); // Disable the comboBox after selection
                    // Remove selected Y value from all other Y ComboBoxes
                    for (Node node : gridPane.getChildren()) {
                        if (node instanceof ComboBox && GridPane.getColumnIndex(node) == 1) { // Only Y-axis ComboBoxes
                            ComboBox<?> otherComboBox = (ComboBox<?>) node;
                            if (otherComboBox != yComboBox) {
                                otherComboBox.getItems().remove(selectedY);
                            }
                        }
                    }
                }
            });

            rowIndex++;
        }

        // Create and add the submit button
        Button submitButton = new Button("Submit");
        gridPane.add(submitButton, 0, rowIndex, 2, 1); // Span the button across two columns
        GridPane.setHalignment(submitButton, HPos.CENTER);

        // Close the popup when the submit button is clicked and store the selections
        submitButton.setOnAction(e -> {
            selectedValues.put(xAxis, selectedXValues);
            selectedValues.put(yAxis, selectedYValues);
            popupStage.close();
        });

        // Set the scene and show the popup window
        Scene scene = new Scene(gridPane, 500, 400); // Adjust window size as needed
        popupStage.setScene(scene);
        popupStage.showAndWait();

        // Return the map of selected values
        return selectedValues;
    }
}








