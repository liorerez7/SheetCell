package controller.main;

import controller.grid.GridController;
import dto.components.DtoCell;
import dto.small_parts.CellLocationFactory;
import dto.small_parts.EffectiveValue;
import utilities.Constants;
import utilities.http.manager.HttpRequestManager;
import utilities.javafx.smallparts.FilterGridData;
import utilities.javafx.smallparts.RunTimeAnalysisData;
import utilities.javafx.smallparts.SortRowsData;

import dto.components.DtoContainerData;
import dto.components.DtoSheetCell;
import utilities.javafx.Model;
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
import dto.small_parts.CellLocation;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
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
        popupGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");


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

        scene.getStylesheets().add("controller/main/ErrorPopUp.css");


        //scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ErrorPopup.css")).toExternalForm());
        messageLabel.getStyleClass().add("popup-label");
        layout.getStyleClass().add("popup-container");

        // Set the scene and modality
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(scene);
        popupStage.show();
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

    private void openGridPopUp(String title, GridController gridScrollerController,
                               Consumer<GridPane> gridInitializer) {

        // Create a new Stage (window) for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
        popupStage.setTitle(title);

        // Create a new GridPane for the popup
        GridPane popupGrid = new GridPane();
        popupGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");

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

    public void openFilterGridPopUp(DtoContainerData dtoContainerData, GridController gridScrollerController) {
        openGridPopUp("Filter Grid", gridScrollerController,
                popupGrid -> gridScrollerController.initializeFilterPopupGrid(popupGrid, dtoContainerData));
    }

    public void openSortGridPopUp(DtoContainerData dtoContainerData, GridController gridScrollerController) {
        openGridPopUp("Sorted Rows", gridScrollerController,
                popupGrid -> gridScrollerController.initializeSortPopupGrid(popupGrid, dtoContainerData));
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
//        popupGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");
//
//        Platform.runLater(() -> {
//            Map<CellLocation, Label> cellLocationLabelMap = gridScrollerController
//                    .initializeRunTimeAnalysisPopupGrid(popupGrid, sheetCellRunTime);
//
//            model.setCellLabelToPropertiesRunTimeAnalysis(cellLocationLabelMap);
//            model.bindCellLabelToPropertiesRunTimeAnalysis();
//            model.setPropertiesByDtoSheetCellRunTimeAnalsys(sheetCellRunTime);
//
//            VBox sliderBox = new VBox(10);
//            sliderBox.setAlignment(Pos.CENTER);
//
//            Label cellIdLabel = new Label("Cell ID: " + col + row);
//            cellIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
//
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
//            Label valueLabel = new Label("Value: " + currentVal);
//            valueLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
//
//            valueSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
//                int newValue = (int) Math.round(newVal.doubleValue() / stepValue) * stepValue;
//                if (newValue > endingValue) {
//                    newValue -= stepValue;
//                }
//                valueSlider.setValue(newValue);
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
//                        DtoSheetCell newUpdatedSheetCell = runTimeHttpCall(map);
//                        Platform.runLater(() -> model.setPropertiesByDtoSheetCellRunTimeAnalsys(newUpdatedSheetCell));
//
//                    } catch (Exception e) {
//                        Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error updating cell"));
//                    }
//                });
//            });
//
//            sliderBox.getChildren().addAll(cellIdLabel, valueSlider, valueLabel);
//
//            VBox contentBox = new VBox(10, popupGrid, sliderBox);
//            contentBox.setAlignment(Pos.CENTER_LEFT);
//            contentBox.setPadding(new Insets(10));
//
//            ScrollPane contentScrollPane = new ScrollPane(contentBox);
//            contentScrollPane.setFitToWidth(true);
//            contentScrollPane.setFitToHeight(true);
//
//            Scene popupScene = new Scene(contentScrollPane);
//            popupStage.setScene(popupScene);
//
//            popupStage.showAndWait();
//        });
//    }


    public void showRuntimeAnalysisPopup(
            DtoSheetCell sheetCellRunTime,
            Model model, GridController gridScrollerController) {

        String title = "Run Time Analysis";
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        List<CellLocation> cellLocationsOfRunTimeAnalysisCells = extractCellLocations(sheetCellRunTime);

        // Create the main grid pane and apply styles
        GridPane popupGrid = new GridPane();
        popupGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");

        // Wrap popupGrid in a ScrollPane to manage scroll within boundaries
        ScrollPane gridScrollPane = new ScrollPane(popupGrid);
        gridScrollPane.setFitToWidth(true);
        gridScrollPane.setFitToHeight(true);

        // Set preferred size for gridScrollPane; scroll bars appear if grid exceeds this size
        gridScrollPane.setPrefViewportWidth(600);  // Adjust width as needed
        gridScrollPane.setPrefViewportHeight(400); // Adjust height as needed

        Platform.runLater(() -> {

            // Initial instruction label
            Label instructionLabel = new Label("To begin runtime analysis, please select a numeric cell.");
            instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555; -fx-font-style: italic;");

            // Create slider and disable it initially
            Slider valueSlider = new Slider();
            valueSlider.setDisable(true);
            valueSlider.getStyleClass().add("custom-slider");

            // Create TextFields with prompt text and disable them initially
            TextField startingValueField = new TextField();
            startingValueField.setPromptText("Enter starting value");
            startingValueField.setDisable(true);
            startingValueField.getStyleClass().add("custom-text-field");

            TextField endingValueField = new TextField();
            endingValueField.setPromptText("Enter ending value");
            endingValueField.setDisable(true);
            endingValueField.getStyleClass().add("custom-text-field");

            TextField stepValueField = new TextField();
            stepValueField.setPromptText("Enter step value");
            stepValueField.setDisable(true);
            stepValueField.getStyleClass().add("custom-text-field");

            Button submitButton = new Button("Submit");
            submitButton.setDisable(true); // Initially disabled
            submitButton.getStyleClass().add("custom-button");

            Runnable updateSubmitButtonState = () -> submitButton.setDisable(
                    startingValueField.getText().trim().isEmpty() ||
                            endingValueField.getText().trim().isEmpty() ||
                            stepValueField.getText().trim().isEmpty()
            );

            startingValueField.textProperty().addListener((obs, oldText, newText) -> updateSubmitButtonState.run());
            endingValueField.textProperty().addListener((obs, oldText, newText) -> updateSubmitButtonState.run());
            stepValueField.textProperty().addListener((obs, oldText, newText) -> updateSubmitButtonState.run());


            // Label to display the current value of the slider
            Label valueLabel = new Label("Value: N/A");
            valueLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");

            // AtomicReferences to hold the col, row, and cell current value
            AtomicReference<Character> colRef = new AtomicReference<>();
            AtomicReference<String> rowRef = new AtomicReference<>();
            AtomicReference<Double> currentCellValueRef = new AtomicReference<>();

            // Label for cell ID (always visible)
            Label cellIdLabel = new Label("Cell ID: ");
            cellIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

            // Warning label for non-numeric cells (initially hidden)
            Label warningLabel = new Label("Runtime analysis is only available for pure numeric cells.");
            warningLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            warningLabel.setVisible(false); // Start hidden

            // VBox to hold all elements related to the slider and input fields
            VBox sliderBox = new VBox(10);
            sliderBox.setAlignment(Pos.CENTER);

            // Submit button to enable the slider

            submitButton.setOnAction(event -> {
                try {
                    int startingValue = Integer.parseInt(startingValueField.getText());
                    int endingValue = Integer.parseInt(endingValueField.getText());
                    int stepValue = Integer.parseInt(stepValueField.getText());

                    if (stepValue > 0 && startingValue < endingValue) {
                        // Configure and enable the slider
                        valueSlider.setMin(startingValue);
                        valueSlider.setMax(endingValue);
                        valueSlider.setBlockIncrement(stepValue);
                        valueSlider.setMajorTickUnit(stepValue);

                        // Set slider's initial value based on current cell value
                        double cellValue = currentCellValueRef.get();
                        double adjustedValue = Math.max(startingValue, Math.min(cellValue, endingValue));
                        valueSlider.setValue(adjustedValue); // Set to cell value within range
                        valueSlider.setDisable(false); // Enable slider after valid input
                    } else {
                        createErrorPopup("Invalid input values", "Error");
                    }
                } catch (NumberFormatException e) {
                    createErrorPopup("Please enter valid numbers", "Input Error");
                }
            });

            // Define Consumer to enable fields and update display based on cell click
            Consumer<CellLocation> labelClickConsumer = cellLocation -> {
                DtoCell cell = sheetCellRunTime.getRequestedCell(cellLocation.getCellId());

                // Retrieve cell information for ID display
                colRef.set(cellLocation.getVisualColumn());
                rowRef.set(cellLocation.getVisualRow());
                cellIdLabel.setText("Cell ID: " + colRef.get() + rowRef.get());

//                gridScrollerController.clearAllHighlights();
//                gridScrollerController.showNeighbors(cell);

                if (cell != null && cell.getEffectiveValue() != null){

                    if(cellLocationsOfRunTimeAnalysisCells.contains(cellLocation)){

                        // Enable the text fields and submit button for numeric cells
                        startingValueField.clear();
                        endingValueField.clear();
                        stepValueField.clear();
                        startingValueField.setDisable(false);
                        endingValueField.setDisable(false);
                        stepValueField.setDisable(false);

                        // Store current cell value and update display
                        double currentVal = (Double) cell.getEffectiveValue().getValue();
                        currentCellValueRef.set(currentVal);
                        valueLabel.setText("Value: " + currentVal);

                        // Hide warning label
                        warningLabel.setVisible(false);
                    }
                    else{
                        // Disable fields for non-numeric cells
                        startingValueField.clear();
                        endingValueField.clear();
                        stepValueField.clear();
                        startingValueField.setDisable(true);
                        endingValueField.setDisable(true);
                        stepValueField.setDisable(true);
                        valueSlider.setDisable(true);

                        // Show warning label for non-numeric cells
                        warningLabel.setVisible(true);
                    }
                }else{
                    startingValueField.clear();
                    endingValueField.clear();
                    stepValueField.clear();
                    startingValueField.setDisable(true);
                    endingValueField.setDisable(true);
                    stepValueField.setDisable(true);
                    valueSlider.setDisable(true);

                    // Show warning label for non-numeric cells
                    warningLabel.setVisible(true);
                }

                instructionLabel.setVisible(false);

                // Add elements to sliderBox if not already added
                if (!sliderBox.getChildren().contains(cellIdLabel)) {
                    sliderBox.getChildren().setAll(cellIdLabel, warningLabel, startingValueField, endingValueField, stepValueField, submitButton, valueSlider, valueLabel);
                }
            };

            // Initialize grid and bind model
            Map<CellLocation, Label> cellLocationLabelMap = gridScrollerController
                    .initializeRunTimeAnalysisPopupGrid(popupGrid, sheetCellRunTime, labelClickConsumer, cellLocationsOfRunTimeAnalysisCells);

            model.setCellLabelToPropertiesRunTimeAnalysis(cellLocationLabelMap);
            model.bindCellLabelToPropertiesRunTimeAnalysis();
            model.setPropertiesByDtoSheetCellRunTimeAnalsys(sheetCellRunTime);

            // Slider value listener
            valueSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                int stepValue = Integer.parseInt(stepValueField.getText());
                int newValue = (int) Math.round(newVal.doubleValue() / stepValue) * stepValue;
                if (newValue > Integer.parseInt(endingValueField.getText())) {
                    newValue -= stepValue;
                }
                valueSlider.setValue(newValue);
                valueLabel.setText("Value: " + newValue);

                String newValueStr = String.valueOf(newValue);
                CompletableFuture.runAsync(() -> {
                    try {
                        Map<String, String> map = new HashMap<>();
                        map.put("newValue", newValueStr);
                        map.put("colLocation", String.valueOf(colRef.get())); // Use updated col reference
                        map.put("rowLocation", rowRef.get()); // Use updated row reference
                        map.put("versionNumber", sheetCellRunTime.getLatestVersion() + "");

                        DtoSheetCell newUpdatedSheetCell = runTimeHttpCall(map);
                        Platform.runLater(() -> model.setPropertiesByDtoSheetCellRunTimeAnalsys(newUpdatedSheetCell));

                    } catch (Exception e) {
                        Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error updating cell"));
                    }
                });
            });

            VBox contentBox = new VBox(10, gridScrollPane, instructionLabel, sliderBox);
            contentBox.setAlignment(Pos.CENTER_LEFT);
            contentBox.setPadding(new Insets(10));

            ScrollPane contentScrollPane = new ScrollPane(contentBox);
            contentScrollPane.setFitToWidth(true);
            contentScrollPane.setFitToHeight(true);

            Scene popupScene = new Scene(contentScrollPane);
            popupScene.getStylesheets().add("controller/main/popup.css"); // Make sure this path is correct
            popupStage.setScene(popupScene);
            popupStage.showAndWait();
        });
    }

    private List<CellLocation> extractCellLocations(DtoSheetCell sheetCellRunTime) {
        List<CellLocation> cellLocations = new ArrayList<>();
        sheetCellRunTime.getViewSheetCell().forEach((location, effectiveValue) -> {
            DtoCell dtoCell = sheetCellRunTime.getRequestedCell(location.getCellId());

            try{
                Double doubleVal = Double.parseDouble(dtoCell.getOriginalValue());
                cellLocations.add(CellLocationFactory.fromCellId(location.getCellId()));
            }catch (Exception e){
                // Do nothing
            }
        });

        return cellLocations;
    }




    private DtoSheetCell runTimeHttpCall(Map<String,String> map){

        DtoSheetCell dtoSheetCell = null;

        try (Response updateCellResponse = HttpRequestManager.sendPostSyncRequest(Constants.RUNTIME_ANALYSIS, map)) {

            if (!updateCellResponse.isSuccessful()) {
                Platform.runLater(() -> createErrorPopup("Failed to load sheet", "Error"));
                return null;
            }

            String dtoSheetCellAsJson = updateCellResponse.body().string();
            dtoSheetCell = Constants.GSON_INSTANCE.fromJson(dtoSheetCellAsJson, DtoSheetCell.class);

        }catch (IOException e){
            Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error updating cell"));
        }

        return dtoSheetCell;
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

    public void showVersionsPopup(
            Map<Integer, DtoSheetCell> allDtoSheetVersions, // Map of all sheet versions
            int lastVersion,
            GridController gridScrollerController) {

        String title = "Versions";
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        // Set the size of the popup stage to be larger
        popupStage.setWidth(1200);  // Set a wider stage width
        popupStage.setHeight(700);  // Set a taller stage height

        // Create a label that will show the version number
        Label versionLabel = new Label("Version Number " + lastVersion);
        versionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Create a GridPane for the specific version selected via the slider
        GridPane popupGrid = new GridPane();
        popupGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");

        // Wrap the GridPane inside a ScrollPane
        ScrollPane gridScrollPane = new ScrollPane(popupGrid);
        gridScrollPane.setFitToWidth(true);
        gridScrollPane.setFitToHeight(true);

        // Initialize the grid with the latest version upon opening the popup
        DtoSheetCell initialVersion = allDtoSheetVersions.get(lastVersion);
        gridScrollerController.initializeVersionPopupGrid(popupGrid, initialVersion);

        // Create a slider to switch between versions, placed under the grid
        Slider valueSlider = new Slider(1, lastVersion, lastVersion);
        valueSlider.setBlockIncrement(1);
        valueSlider.setMajorTickUnit(1);  // Keep smooth sliding
        valueSlider.setMinorTickCount(0);  // No minor ticks

        valueSlider.setShowTickMarks(true);
        valueSlider.setShowTickLabels(true);

        // Style the slider with the desired background color
        valueSlider.setStyle("-fx-control-inner-background: #e8f0f6; " +
                "-fx-tick-mark-fill: #4a4a4a; " +
                "-fx-tick-label-fill: #4a4a4a; " +
                "-fx-border-color: #4a4a4a; " +
                "-fx-border-radius: 5px;");

        // Background color for the whole screen
        String backgroundColorStyle = "-fx-background-color: #e8f0f6;";

        // Apply background color to root container
        VBox contentBox = new VBox(10, versionLabel, gridScrollPane, valueSlider);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        contentBox.setPadding(new Insets(10));
        contentBox.setStyle(backgroundColorStyle);

        // Create a Scene with the ScrollPane
        ScrollPane contentScrollPane = new ScrollPane(contentBox);
        contentScrollPane.setFitToWidth(true);
        contentScrollPane.setFitToHeight(true);
        contentScrollPane.setStyle(backgroundColorStyle);

        Scene popupScene = new Scene(contentScrollPane);
        popupStage.setScene(popupScene);

        // Throttling slider changes to avoid freezing due to frequent grid updates
        final int[] lastVersionNumber = {lastVersion};
        final long[] lastUpdateTime = {System.currentTimeMillis()};

        valueSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int versionNumber = newVal.intValue();

            // Only update if the slider is not moving too fast (throttle updates)
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime[0] > 200 || versionNumber != lastVersionNumber[0]) {  // Update every 200ms
                lastVersionNumber[0] = versionNumber;
                lastUpdateTime[0] = currentTime;

                // Update the version label with the selected version number
                versionLabel.setText("Version Number " + versionNumber);

                DtoSheetCell selectedSheetCellVersion = allDtoSheetVersions.get(versionNumber);
                if (selectedSheetCellVersion != null) {
                    Platform.runLater(() -> {
                        gridScrollerController.initializeVersionPopupGrid(popupGrid, selectedSheetCellVersion);
                    });
                } else {
                    Platform.runLater(() -> createErrorPopup("Version not available", "Error"));
                }
            }
        });

        // Show the popup window
        popupStage.showAndWait();
    }

}








