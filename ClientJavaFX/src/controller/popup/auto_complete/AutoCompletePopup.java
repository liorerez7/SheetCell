package controller.popup.auto_complete;

import controller.grid.CustomCellLabel;
import controller.grid.GridController;
import controller.main.MainController;
import controller.popup.PopUpWindowsManager;
import dto.components.DtoCell;
import dto.components.DtoSheetCell;
import dto.small_parts.CellLocation;
import dto.small_parts.CellLocationFactory;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utilities.Constants;
import utilities.http.manager.HttpRequestManager;
import utilities.javafx.Model;

import java.io.IOException;
import java.util.*;

public class AutoCompletePopup {

    private final DtoSheetCell dtoSheetCell;
    private final GridController gridController;
    private final Model model;
    private final MainController mainController;
    private final PopUpWindowsManager popUpWindowsManager;
    private AutoCompleteResult autoCompleteResult;
    private Stage stage;

    // UI components
    private TextField cellLocationInput;
    private Button submitButton;
    private ComboBox<String> rowOrColChoice;
    private ComboBox<String> dataOptions;
    private VBox mainContainer;
    private VBox cellLocationSection;
    private VBox rowColSection;
    private VBox thirdSection;
    private VBox originalGridContainer;
    private Button backButton;
    private Button finalizeButton;
    private ComboBox<String> extendedRangeOptions;
    private Button backToSecondSectionButton;
    private Button predictValuesButton;
    private final static char firstGridColumn = 'A';
    private final static String firstGridRow = "1";
    private final static int AT_LEAST_TWO_CELLS_FOR_PREDICTION = 2;
    private final String lastRowGrid;
    private final char lastColumnGrid;
    private Map<CellLocation, CustomCellLabel> cellLocationCustomOriginalCellLabelMap = new HashMap<>();
    private Map<CellLocation, CustomCellLabel> cellLocationCustomNewCellLabelMap = new HashMap<>();
    private CellLocation startingRangeCellLocation;
    private CellLocation endingRangeCellLocation;
    private CellLocation extendedRangeCellLocation;
    private DtoSheetCell newPredictedDtoSheetCell;


    public AutoCompletePopup(DtoSheetCell dtoSheetCell,
                             GridController gridController, Model model, MainController mainController, PopUpWindowsManager popUpWindowsManager) {

        this.dtoSheetCell = dtoSheetCell;
        this.gridController = gridController;
        this.model = model;
        this.mainController = mainController;
        this.popUpWindowsManager = popUpWindowsManager;

        lastColumnGrid = (char)(dtoSheetCell.getNumberOfColumns() - 1 + 'A');
        lastRowGrid = String.valueOf(dtoSheetCell.getNumberOfRows());

        initializeUI();
    }

    private void initializeUI() {
        // Stage setup
        stage = new Stage();

        // Main container
        mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(15));

        // Section 1 - Cell Location Input with Submit Button
        cellLocationSection = new VBox(5);
        cellLocationSection.setPadding(new Insets(10));
        cellLocationSection.setStyle("-fx-border-color: grey; -fx-border-width: 1; -fx-padding: 10;");

        Label cellLocationLabel = new Label("Enter Starting Cell Location (e.g., A3):");
        cellLocationInput = new TextField();
        cellLocationInput.setPromptText("A3");

        submitButton = new Button("Submit");
        submitButton.setDisable(true); // Initially disabled

        // Add TextField and Button to Section 1
        cellLocationSection.getChildren().addAll(cellLocationLabel, cellLocationInput, submitButton);

        // Section 2 - Row or Column Selection
        rowColSection = new VBox(5);
        rowColSection.setPadding(new Insets(10));
        rowColSection.setStyle("-fx-border-color: grey; -fx-border-width: 1; -fx-padding: 10;");

        Label choiceLabel = new Label("Choose Row or Column:");
        rowOrColChoice = new ComboBox<>();
        rowOrColChoice.getItems().addAll("Row", "Column");
        rowOrColChoice.setDisable(true); // Initially disabled

        dataOptions = new ComboBox<>();
        dataOptions.setDisable(true); // Initially disabled
        dataOptions.setVisibleRowCount(10); // Allow scrolling

        // Back button to return to the starting cell input section
        backButton = new Button("Back To Choosing Starting Cell");
        backButton.setOnAction(event -> resetToCellLocationSelection());

        // Finalize button to complete the initial range selection
        finalizeButton = new Button("Finalize Initial Range");
        finalizeButton.setOnAction(event -> finalizeInitialRange());

        // Add to Section 2
        rowColSection.getChildren().addAll(choiceLabel, rowOrColChoice, dataOptions, backButton, finalizeButton);
        rowColSection.setVisible(false); // Hidden initially

        // Section 3 - Extended Range Selection
        thirdSection = new VBox(5);
        thirdSection.setPadding(new Insets(10));
        thirdSection.setStyle("-fx-border-color: grey; -fx-border-width: 1; -fx-padding: 10;");

        Label extendedRangeLabel = new Label("Select Cells within Extended Range:");
        extendedRangeOptions = new ComboBox<>();
        extendedRangeOptions.setDisable(true); // Initially disabled
        extendedRangeOptions.setVisibleRowCount(10); // Allow scrolling

        // Back to Section 2 Button
        backToSecondSectionButton = new Button("Back to Range Selection");
        backToSecondSectionButton.setOnAction(event -> returnToSecondSection());

        // Predict Values Button
        predictValuesButton = new Button("Predict Values");
        predictValuesButton.setOnAction(event -> predictValues());

        // Add components to the third section
        thirdSection.getChildren().addAll(extendedRangeLabel, extendedRangeOptions, backToSecondSectionButton, predictValuesButton);
        thirdSection.setVisible(false); // Hidden initially

        // Original Grid View
        originalGridContainer = new VBox(8, new Label("Original Grid"), createOriginalGrid());
        originalGridContainer.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");
        originalGridContainer.setPadding(new Insets(10));

        // Arrange sections and grid in main layout
        HBox mainLayout = new HBox(15, mainContainer, originalGridContainer);
        mainLayout.setPadding(new Insets(15));

        // Add sections to main container
        mainContainer.getChildren().addAll(cellLocationSection, rowColSection, thirdSection);

        // Event Listeners
        cellLocationInput.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(newValue.trim().isEmpty());
        });

        submitButton.setOnAction(event -> handleCellLocationSubmission());
        rowOrColChoice.setOnAction(event -> handleRowOrColChoice());

        // Display the popup with enlarged scene
        Scene scene = new Scene(mainLayout, 1510, 750);
        stage.setScene(scene);
    }

    public AutoCompleteResult show() {
        stage.showAndWait();
        return autoCompleteResult;
    }

    private VBox createOriginalGrid() {
        GridPane originalGrid = new GridPane();
        originalGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");

        cellLocationCustomOriginalCellLabelMap = gridController.initializeOriginalPopupGrid(originalGrid, dtoSheetCell);

        ScrollPane gridScrollPane = new ScrollPane(originalGrid);
        gridScrollPane.setFitToWidth(true);
        gridScrollPane.setFitToHeight(true);
        gridScrollPane.setPrefSize(1120, 335);

        VBox gridContainer = new VBox(gridScrollPane);
        gridContainer.setPadding(new Insets(10));

        return gridContainer;
    }

    private void handleCellLocationSubmission() {
        String cellLocation = cellLocationInput.getText().toUpperCase(); // Convert to uppercase
        if (isValidCellLocation(cellLocation)) {
            cellLocationInput.setText(cellLocation); // Update TextField with uppercase
            startingRangeCellLocation = CellLocationFactory.fromCellId(cellLocation); // Set starting range
            cellLocationInput.setDisable(true);
            submitButton.setDisable(true);
            rowColSection.setVisible(true); // Show row/column section
            rowOrColChoice.setDisable(false); // Enable choice ComboBox
        } else {
            // Display validation error
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid cell location. Please enter a valid location like A3.");
            alert.show();
        }
    }

    private void handleRowOrColChoice() {
        String choice = rowOrColChoice.getValue();
        if (choice == null) return; // Prevent null pointer exception if no choice is selected

        dataOptions.getItems().clear();
        if (choice.equals("Row")) {
            populateRowOptions(cellLocationInput.getText());
            dataOptions.setDisable(false);
        } else if (choice.equals("Column")) {
            populateColumnOptions(cellLocationInput.getText());
            dataOptions.setDisable(false);
        }
    }

    private void populateRowOptions(String startCell) {
        dataOptions.getItems().clear();
        for (int i = (Integer.parseInt(startCell.substring(1)) + AT_LEAST_TWO_CELLS_FOR_PREDICTION); i <= Integer.parseInt(lastRowGrid); i++) {
            dataOptions.getItems().add(startCell.charAt(0) + Integer.toString(i));
        }
    }

    private void populateColumnOptions(String startCell) {
        dataOptions.getItems().clear();
        char startColumn = startCell.charAt(0);
        for (char column = (char)(startColumn + AT_LEAST_TWO_CELLS_FOR_PREDICTION); column <= lastColumnGrid; column++) {
            dataOptions.getItems().add(column + startCell.substring(1));
        }
    }

    private void finalizeInitialRange() {
        // Set endingRangeCellLocation based on selected data option
        String selectedEndingCell = dataOptions.getValue();
        if (selectedEndingCell != null) {
            endingRangeCellLocation = CellLocationFactory.fromCellId(selectedEndingCell);
        }

        cellLocationCustomOriginalCellLabelMap.forEach((cellLocation, customCellLabel) -> {
            if (isWithinBoundaries(cellLocation, startingRangeCellLocation, endingRangeCellLocation)) {
                customCellLabel.setBackgroundColor(Color.LIGHTGRAY);
            }
        });

        // Disable all controls in the second section
        rowOrColChoice.setDisable(true);
        dataOptions.setDisable(true);
        backButton.setDisable(true);
        finalizeButton.setDisable(true);

        // Show the third section
        populateExtendedRangeOptions();
        thirdSection.setVisible(true);
        extendedRangeOptions.setDisable(false); // Enable extended range options
    }


    public boolean isWithinBoundaries(CellLocation cellLocation, CellLocation startingRangeCellLocation, CellLocation endingRangeCellLocation) {
        char startColumn = startingRangeCellLocation.getVisualColumn();
        char endColumn = endingRangeCellLocation.getVisualColumn();
        int startRow = startingRangeCellLocation.getRealRow();
        int endRow = endingRangeCellLocation.getRealRow();
        char cellColumn = cellLocation.getVisualColumn();
        int cellRow = cellLocation.getRealRow();

        // Case 1: Row-based range (same row, spanning columns)
        if (startRow == endRow && cellRow == startRow) {
            // Check if the cell's column is within the start and end columns
            return cellColumn >= startColumn && cellColumn <= endColumn;
        }

        // Case 2: Column-based range (same column, spanning rows)
        if (startColumn == endColumn && cellColumn == startColumn) {
            // Check if the cell's row is within the start and end rows
            return cellRow >= startRow && cellRow <= endRow;
        }

        // If neither condition is met, it's outside the specified range
        return false;
    }

    private void populateExtendedRangeOptions() {
        extendedRangeOptions.getItems().clear();
        String startCell = dataOptions.getValue();



        if (startCell == null || rowOrColChoice.getValue() == null) return;

        if (rowOrColChoice.getValue().equals("Row")) {
            int startRow = Integer.parseInt(startCell.substring(1));
            char column = startCell.charAt(0);
            for (int i = startRow + 1; i <= Integer.parseInt(lastRowGrid); i++) {
                extendedRangeOptions.getItems().add(column + Integer.toString(i));
            }
        } else if (rowOrColChoice.getValue().equals("Column")) {
            int row = Integer.parseInt(startCell.substring(1));
            char startColumn = startCell.charAt(0);
            for (char column = (char)(startColumn + 1); column <= lastColumnGrid; column++) {
                extendedRangeOptions.getItems().add(column + Integer.toString(row));
            }
        }
    }

    private void returnToSecondSection() {
        // Hide third section, enable second section components
        thirdSection.setVisible(false);
        rowColSection.setVisible(true);
        rowOrColChoice.setDisable(false);
        dataOptions.setDisable(false);
        backButton.setDisable(false);
        finalizeButton.setDisable(false);
    }

    private void resetToCellLocationSelection() {
        // Hide row/column section and third section, clear combo boxes
        rowColSection.setVisible(false);
        thirdSection.setVisible(false);
        rowOrColChoice.setValue(null);
        dataOptions.getItems().clear();
        dataOptions.setDisable(true);
        extendedRangeOptions.getItems().clear();
        extendedRangeOptions.setDisable(true);

        // Re-enable cell location input and submit button
        cellLocationInput.setDisable(false);
        submitButton.setDisable(false);
    }

    private void predictValues() {

        String extendedVal = extendedRangeOptions.getValue();
        if (extendedVal != null) {
            extendedRangeCellLocation = CellLocationFactory.fromCellId(extendedVal);
        }

        Map<String,String> originalValuesByOrder = getOriginalValuesFromCurrentDtoSheetCell();

        getNewDtoSheetCellFromServer(originalValuesByOrder);

        if(newPredictedDtoSheetCell != null){

            GridPane newGridContainer = new GridPane();
            newGridContainer.getStylesheets().add("controller/grid/ExelBasicGrid.css");
            cellLocationCustomNewCellLabelMap = gridController.initializeOriginalPopupGrid(newGridContainer, newPredictedDtoSheetCell);
        }

    }

    private void getNewDtoSheetCellFromServer(Map<String,String> originalValuesByOrder) {

        Map<String,String> params = new HashMap<>();
        params.put("originalValues", Constants.GSON_INSTANCE.toJson(originalValuesByOrder));
        params.put("startingRangeCellLocation", startingRangeCellLocation.getCellId());
        params.put("endingRangeCellLocation", endingRangeCellLocation.getCellId());
        params.put("extendedRangeCellLocation", extendedRangeCellLocation.getCellId());

        HttpRequestManager.sendPostAsyncRequest(Constants.GET_PREDICTION_VALUES, params, new Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBodyString = null;

                try (response) {
                    if (response.isSuccessful()) {
                        responseBodyString = response.body().string();  // Read the response body once
                    } else {
                        // Handle unsuccessful response here, if needed
                    }
                }

                // Run JavaFX UI update on the JavaFX Application Thread
                if (responseBodyString != null) {
                    final String finalResponseBodyString = responseBodyString;  // Make it effectively final for use in Platform.runLater
                    Platform.runLater(() -> {
                        // Parse JSON string and update UI
                        try {
                            newPredictedDtoSheetCell = Constants.GSON_INSTANCE.fromJson(finalResponseBodyString, DtoSheetCell.class);
                            // Additional UI updates, if necessary
                        } catch (Exception e) {
                            e.printStackTrace();  // Handle JSON parsing errors here
                        }
                    });
                }
            }
        });
    }

    private Map<String,String> getOriginalValuesFromCurrentDtoSheetCell() {
        Map<String,String> originalValuesByOrder = new HashMap<>();

        dtoSheetCell.getViewSheetCell().forEach((cellLocation, cellValue) -> {
            if (isWithinBoundaries(cellLocation, startingRangeCellLocation, endingRangeCellLocation)) {
                DtoCell dtoCell = dtoSheetCell.getRequestedCell(cellLocation.getCellId());
                originalValuesByOrder.put(cellLocation.getCellId(), dtoCell.getOriginalValue());
            }
        });

        return originalValuesByOrder;
    }

    private boolean isValidCellLocation(String cellLocation) {
        return cellLocation.matches("[A-J][1-9][0-9]*");
    }
}
