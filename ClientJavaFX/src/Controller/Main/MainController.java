package Controller.Main;

import Controller.Customize.CustomizeController;
import Controller.Grid.GridController;
import Controller.HttpUtility.Constants;
import Controller.HttpUtility.HttpRequestManager;
import Controller.JavaFXUtility.*;
import Controller.MenuBar.HeaderController;
import Controller.ProgressManager.ProgressAnimationManager;
import Controller.Ranges.RangesController;
import Controller.actionLine.ActionLineController;
import CoreParts.api.Engine;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.smallParts.CellLocation;
import Main.sheetCellApp;
import Utility.DtoContainerData;
import com.google.gson.reflect.TypeToken;
import expression.impl.stringFunction.Str;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;


import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public class MainController implements Closeable {


    private Engine engine;
    private Stage stage;
    private sheetCellApp app;  // Reference to the main application

    @FXML
    private HeaderController headerController;
    @FXML
    private VBox header;

    @FXML
    private MenuBar menuBar;

    @FXML
    private BorderPane mainPane;

    @FXML
    private ActionLineController actionLineController;

    @FXML
    private GridPane actionLine;

    @FXML
    private GridController gridScrollerController;

    @FXML
    private ScrollPane gridScroller;

    @FXML
    private CustomizeController customizeController;

    @FXML
    private VBox customize;

    @FXML
    private VBox leftCommands;

    @FXML
    private RangesController rangesController;
    @FXML
    private StackPane ranges;



    private final Model model;
    private final PopUpWindowsHandler popUpWindowsHandler;
    private final ThemeManager themeManager;
    private ProgressManager progressManager;
    // Reference to the stage

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setApp(sheetCellApp app) {
        this.app = app;
    }

    @FXML
    public void initialize() {
        customizeController.setMainController(this);
        headerController.setMainController(this);
        actionLineController.setMainController(this);
        gridScrollerController.setMainController(this);
        rangesController.setMainController(this);
        progressManager = new ProgressManager();

        adjustScrollPanePosition();
    }

    private void adjustScrollPanePosition() {
        if (gridScroller != null) {
            BorderPane.setMargin(gridScroller, new Insets(20, 0, 20, 10)); // Adjust the top margin to position lower
        }
    }

    public StringProperty getVersionProperty() {
        return model.getLatestUpdatedVersionProperty();
    }

    public BooleanProperty getIsCellLabelClickedProperty() {
        return model.getIsCellLebalClickedProperty();
    }

    public MainController() {
        model = new Model(null);
        popUpWindowsHandler = new PopUpWindowsHandler();
        themeManager = new ThemeManager(mainPane, leftCommands);
    }

    public void initializeGridBasedOnXML(String absolutePath) {

        // Create a new instance of ProgressAnimationManager
        ProgressAnimationManager progressAnimationManager = new ProgressAnimationManager(progressManager);

        // Get the VBox layout that contains the progressPane, welcomeLabel, and cancelButton
        VBox layout = progressAnimationManager.createProgressAnimationLayout();

        // Set the VBox layout as the content of the gridScroller
        if (gridScroller != null) {
            gridScroller.setContent(layout); // Temporarily show progress bar, label, and cancel button
        }

        // Create a Task to handle the file loading
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Simulate progress over 2 seconds
                    gridScrollerController.hideGrid();
                    for (int i = 0; i <= 100; i++) {
                        updateProgress(i, 100);
                        progressAnimationManager.updateProgress(i / 100.0);  // Update progress via ProgressAnimationManager
                        Thread.sleep(20);  // Simulate loading time
                    }

                    Map<String, String> params = new HashMap<>();
                    params.put("xmlAddress", absolutePath);

                    HttpRequestManager.sendPostRequestASyncWithCallBack(Constants.INIT_SHEET_CELL_ENDPOINT, params, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Platform.runLater(() -> createErrorPopup("Failed to load sheet: " + e.getMessage(), "Error"));
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                            if (response.isSuccessful()) {

                                HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_SHEET_CELL_ENDPOINT, new HashMap<>(), new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        Platform.runLater(() -> createErrorPopup("Failed to load sheet: " + e.getMessage(), "Error"));
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        if (response.isSuccessful()) {

                                            String sheetCellAsJson = response.body().string();


                                            DtoSheetCell newDtoSheetCell = Constants.GSON_INSTANCE.fromJson(sheetCellAsJson, DtoSheetCell.class);
                                            System.out.println(newDtoSheetCell.getRanges().get("grades").get(0));
                                            int latestVersion = newDtoSheetCell.getLatestVersion();
                                            Map<String, List<CellLocation>> ranges = newDtoSheetCell.getRanges();
                                            int numberOfColumns = newDtoSheetCell.getNumberOfColumns();
                                            int numberOfRows = newDtoSheetCell.getNumberOfRows();

                                            Platform.runLater(() -> {
                                                headerController.FileHasBeenLoaded(absolutePath);
                                                Map<CellLocation, Label> cellLocationLabelMap = gridScrollerController.initializeGrid(newDtoSheetCell);
                                                rangesController.clearAllRanges();
                                                model.setReadingXMLSuccess(true);
                                                model.setCellLabelToProperties(cellLocationLabelMap);
                                                model.bindCellLabelToProperties();
                                                model.setPropertiesByDtoSheetCell(newDtoSheetCell);
                                                model.setTotalVersionsProperty(latestVersion);
                                                rangesController.setAllRanges(ranges);
                                                customizeController.loadAllColData(numberOfColumns);
                                                customizeController.loadAllRowData(numberOfRows);
                                                themeManager.keepCurrentTheme(mainPane, leftCommands, customizeController);

                                            });
                                        }
                                    }
                                });
                            }
                            else {
                                Platform.runLater(() -> createErrorPopup("Failed to load sheet: Server responded with code " + response.code(), "Error"));
                            }
                        }
                    });

                } catch (Exception e) {
                    gridScrollerController.showGrid();
                    Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
                } finally {
                    Platform.runLater(() -> {
                        if (gridScroller != null) {
                            gridScroller.setContent(gridScrollerController.getGrid()); // Restore grid
                        }
                    });
                }
                return null;
            }
        };

        // Bind the progress bar to the task's progress
        progressManager.getProgressBar().progressProperty().bind(task.progressProperty());

        // Run the task in a new thread
        new Thread(task).start();
    }

    public void updateCell(String text, String newValue) {
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("newValue", newValue);
                params.put("colLocation", text.charAt(0) + "");
                params.put("rowLocation", text.substring(1));

                // Send POST request synchronously and close the response
                try (Response postResponse = HttpRequestManager.sendPostRequestSync(Constants.UPDATE_CELL_ENDPOINT, params)) {
                    if (!postResponse.isSuccessful()) {
                        Platform.runLater(() -> createErrorPopup("Failed to update cell", "Error"));
                        return;
                    }
                }

                try (Response sheetCellResponse = HttpRequestManager.sendGetRequestSync(Constants.GET_SHEET_CELL_ENDPOINT, new HashMap<>())) {
                    if (!sheetCellResponse.isSuccessful()) {
                        Platform.runLater(() -> createErrorPopup("Failed to load sheet", "Error"));
                        return;
                    }

                    String sheetCellAsJson = sheetCellResponse.body().string();
                    DtoSheetCell newDtoSheetCell = Constants.GSON_INSTANCE.fromJson(sheetCellAsJson, DtoSheetCell.class);
                    int latestVersion = newDtoSheetCell.getLatestVersion();

                    // Send GET request to fetch the requested cell synchronously and close the response
                    Map<String, String> paramsForRequestedCell = new HashMap<>();
                    paramsForRequestedCell.put("cellLocation", text);
                    try (Response requestedCellResponse = HttpRequestManager.sendGetRequestSync(Constants.GET_REQUESTED_CELL_ENDPOINT, paramsForRequestedCell)) {
                        if (!requestedCellResponse.isSuccessful()) {
                            Platform.runLater(() -> createErrorPopup("Failed to load requested cell", "Error"));
                            return;
                        }

                        String dtoCellAsJson = requestedCellResponse.body().string();
                        DtoCell dtoCell = Constants.GSON_INSTANCE.fromJson(dtoCellAsJson, DtoCell.class);

                        // Update the UI on the JavaFX Application Thread
                        Platform.runLater(() -> {
                            model.setPropertiesByDtoSheetCell(newDtoSheetCell);
                            model.setLatestUpdatedVersionProperty(dtoCell);
                            model.setOriginalValueLabelProperty(dtoCell);
                            model.setTotalVersionsProperty(latestVersion);
                            gridScrollerController.showNeighbors(dtoCell);
                        });
                    }
                }

            } catch (IOException e) {
                Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
            }
        });
    }

    public void setEngine(EngineImpl engine) {
        this.engine = engine;
    }

    public StringProperty getOriginalValueLabelProperty() {
        return model.getOriginalValueLabelProperty();
    }

    public StringProperty getTotalVersionsProperty() {
        return model.getTotalVersionsProperty();
    }

    public void createErrorPopUpCircularDependency(DtoSheetCell dtoSheetCell, List<CellLocation> cycle) {
        popUpWindowsHandler.createErrorPopUpCircularDependency(dtoSheetCell, gridScrollerController, cycle);
    }

    public void createErrorPopup(String message, String title) {
        popUpWindowsHandler.createErrorPopup(message, title);
    }

    public void rangeAddClicked() {
        RangeStringsData rangeStringsData = popUpWindowsHandler.openAddRangeWindow();
        String name = rangeStringsData.getName();
        String range = rangeStringsData.getRange();

        if(name != null) //in case when just shutting the window without entering anything
        {
            try {
                Map<String,String> params = new HashMap<>();
                params.put("name",name);
                params.put("range",range);
                HttpRequestManager.sendPostRequestASyncWithCallBack(Constants.ADD_RANGE_ENDPOINT, params, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Platform.runLater(() -> createErrorPopup("Failed to update cell", "Error"));
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Platform.runLater(() -> createErrorPopup("Failed to update cell", "Error"));
                        }
                    }
                });

               HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_REQUESTED_RANGE_ENDPOINT, params, new Callback() {
                   @Override
                   public void onFailure(@NotNull Call call, @NotNull IOException e) {
                       Platform.runLater(() -> createErrorPopup("Failed to get range", "Error"));
                   }

                   @Override
                   public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                       String requestedRangeAsJson = response.body().string();
                       List<CellLocation> requestedRange = Constants.GSON_INSTANCE.fromJson(requestedRangeAsJson, new TypeToken<List<CellLocation>>(){}.getType());
                      // rangesController.addRange(engine.getRequestedRange(name),name);
                       rangesController.addRange(requestedRange,name);
                   }
               });
                //engine.UpdateNewRange(name,rangeStringsData.getRange());
            }
            catch (Exception e) {
                createErrorPopup(e.getMessage(), "Error");
            }
        }
    }

    public void rangeDeleteClicked() {

        CompletableFuture.runAsync(() -> {
            try {

                Response allRangesResponse = HttpRequestManager.sendGetRequestSync(Constants.GET_ALL_RANGES_ENDPOINT, new HashMap<>());
                String allRangesAsJson = allRangesResponse.body().string();
                Set <String> rangeNames = Constants.GSON_INSTANCE.fromJson(allRangesAsJson, new TypeToken<Set<String>>(){}.getType());

                // Use Platform.runLater to ensure UI updates happen on the JavaFX Application Thread
                Platform.runLater(() -> {
                    RangeStringsData rangeStringsData = popUpWindowsHandler.openDeleteRangeWindow(rangeNames);
                    String name = rangeStringsData.getName();

                    if (name != null) {
                        CompletableFuture.runAsync(() -> {
                            try {
                                Map<String, String> params = new HashMap<>();
                                params.put("name", name);

                                Response deleteRangeResponse = HttpRequestManager.sendPostRequestSync(Constants.DELETE_RANGE_ENDPOINT, params);

                                if (!deleteRangeResponse.isSuccessful()) {
                                    Platform.runLater(() -> createErrorPopup("Failed to delete range", "Error"));
                                } else {
                                    Platform.runLater(() -> rangesController.deleteRange(name));
                                }
                            } catch (Exception e) {
                                Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
                            }
                        });
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
            }
        });


//        Set<String> rangeNames = engine.getAllRangeNames();
//        RangeStringsData rangeStringsData = popUpWindowsHandler.openDeleteRangeWindow(rangeNames);
//        String name = rangeStringsData.getName();
//
//        if (name != null) //in case when just shutting the window without entering anything
//        {
//            try {
//                engine.deleteRange(name);
//                rangesController.deleteRange(name);
//            } catch (Exception e) {
//                createErrorPopup(e.getMessage(), "Error");
//            }
//        }
    }

    public void cellClicked(String location) {

        Map<String,String> params = new HashMap<>();
        params.put("cellLocation",location);

        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_REQUESTED_CELL_ENDPOINT, params, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> createErrorPopup("Failed to get cell", "Error"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String requestedCellAsJson = response.body().string();
                DtoCell requestedCell = Constants.GSON_INSTANCE.fromJson(requestedCellAsJson, DtoCell.class);

                Platform.runLater(() -> {
                    model.setIsCellLabelClicked(true);
                    model.setLatestUpdatedVersionProperty(requestedCell);
                    model.setOriginalValueLabelProperty(requestedCell);
                    actionLineController.updateCssWhenUpdatingCell(location);
                    gridScrollerController.clearAllHighlights();
                    gridScrollerController.showNeighbors(requestedCell);
                    rangesController.resetComboBox();
                    customizeController.resetComboBox();
                    model.setColumnSelected(false);
                    model.setRowSelected(false);
                    model.setCellLocationProperty(location);
                });
            }
        });
//
//                DtoCell requestedCell = engine.getRequestedCell(location);
//        model.setIsCellLabelClicked(true);
//        model.setLatestUpdatedVersionProperty(requestedCell);
//        model.setOriginalValueLabelProperty(requestedCell);
//        actionLineController.updateCssWhenUpdatingCell(location);
//        gridScrollerController.clearAllHighlights();
//        gridScrollerController.showNeighbors(requestedCell);
//        rangesController.resetComboBox();
//        customizeController.resetComboBox();
//        model.setColumnSelected(false);
//        model.setRowSelected(false);
//        model.setCellLocationProperty(location);
    }

    public void handleRangeClick(String rangeName) {
        gridScrollerController.clearAllHighlights();

        Map<String,String> params = new HashMap<>();
        params.put("name",rangeName);
        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_REQUESTED_RANGE_ENDPOINT, params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> createErrorPopup("Failed to get range", "Error"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String requestedRangeAsJson = response.body().string();
                List<CellLocation> requestedRange = Constants.GSON_INSTANCE.fromJson(requestedRangeAsJson, new TypeToken<List<CellLocation>>(){}.getType());
                Platform.runLater(() -> gridScrollerController.showAffectedCells(requestedRange));
            }
        });
    }

    public void specificVersionClicked(int versionNumber) {

        Map<String,String> params = new HashMap<>();
        params.put("versionNumber",versionNumber + "");

        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_SHEET_CELL_ENDPOINT, params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> createErrorPopup("Failed to get version", "Error"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String requestedVersionAsJson = response.body().string();
                DtoSheetCell requestedVersion = Constants.GSON_INSTANCE.fromJson(requestedVersionAsJson, DtoSheetCell.class);
                Platform.runLater(() -> {
                    popUpWindowsHandler.openVersionGridPopUp(requestedVersion, versionNumber, gridScrollerController);
                });
            }
        });
    }

    public void sortRowsButtonClicked() {

        SortRowsData sortRowsData = popUpWindowsHandler.openSortRowsWindow();
        String columns = sortRowsData.getColumnsToSortBy();
        String range = sortRowsData.getRange();

        Map<String,String> params = new HashMap<>();
        params.put("columns",columns);
        params.put("range",range);

        //if one of them is null means that the user just closed the window without entering anything
        if(columns != null && range != null)
        {
            try {

                HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.SORT_SHEET_CELL_ENDPOINT, params, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Platform.runLater(() -> createErrorPopup("Failed to sort rows", "Error"));
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String sortedSheetCellAsJson = response.body().string();
                        DtoContainerData dtoContainerData = Constants.GSON_INSTANCE.fromJson(sortedSheetCellAsJson, DtoContainerData.class);
                        DtoSheetCell dtoSheetCell = dtoContainerData.getDtoSheetCell();
                        Platform.runLater(() -> createSortGridPopUp(dtoContainerData));
                    }
                });
            }catch (Exception e) {
                createErrorPopup(e.getMessage(), "Error");
            }
        }
    }

    private void createSortGridPopUp(DtoContainerData dtoContainerData) {
    popUpWindowsHandler.openSortGridPopUp(dtoContainerData, gridScrollerController);
    }

    public void filterDataButtonClicked() {
        // Open the filter data window and retrieve user input
        FilterGridData filterGridData = popUpWindowsHandler.openFilterDataWindow();
        String range = filterGridData.getRange();
        String filterColumn = filterGridData.getColumnsToFilterBy();

        // Validate user input
        if (range == null || filterColumn == null || filterColumn.isEmpty()) {
            return; // Exit if input is invalid
        }

        Map<String, String> params = new HashMap<>();
        params.put("range", range);
        params.put("filterColumn", filterColumn);

        // Run the filtering process asynchronously to avoid blocking the UI thread
        CompletableFuture.runAsync(() -> {
            try {
                // Fetch unique column values
                Map<Character, Set<String>> columnValues = fetchUniqueColumnValues(params);
                if (columnValues == null) {
                    return;
                }


                // Run the UI operation on the JavaFX Application Thread and continue processing afterwards
                Platform.runLater(() -> {
                    Map<Character, Set<String>> filter = popUpWindowsHandler.openFilterDataPopUp(columnValues);
                    boolean isFilterEmpty = filter.values().stream().allMatch(Set::isEmpty);

                    // Proceed only if the filter is not empty
                    if (!isFilterEmpty) {
                        // Pass the filter and range information in the second HTTP request
                        CompletableFuture.runAsync(() -> {
                            try {
                                DtoContainerData filteredSheetCell = fetchFilteredSheetData(range, filter);
                                if (filteredSheetCell != null) {
                                    Platform.runLater(() -> createFilterGridPopUpp(filteredSheetCell));
                                }
                            } catch (Exception e) {
                                Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
                            }
                        });
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
            }
        });
    }

    // Helper method to fetch unique column values
    private Map<Character, Set<String>> fetchUniqueColumnValues(Map<String, String> params) {
        try (Response response = HttpRequestManager.sendGetRequestSync(Constants.GET_UNIQUE_STRINGS_IN_COLUMN, params)) {
            if (!response.isSuccessful()) {
                Platform.runLater(() -> createErrorPopup("Failed to retrieve column values", "Error"));
                return null;
            }

            String columnValuesAsJson = response.body().string();
            return Constants.GSON_INSTANCE.fromJson(columnValuesAsJson, new TypeToken<Map<Character, Set<String>>>(){}.getType());

        } catch (IOException e) {
            Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
            return null;
        }
    }

    // Helper method to fetch the filtered sheet data
    private DtoContainerData fetchFilteredSheetData(String range, Map<Character, Set<String>> filter) {
        // Convert filter map to JSON
        String filterJson = Constants.GSON_INSTANCE.toJson(filter);

        // Prepare the parameters for the GET request
        Map<String, String> params = new HashMap<>();
        params.put("range", range);
        params.put("filter", filterJson);

        try (Response response = HttpRequestManager.sendGetRequestSync(Constants.FILTER_SHEET_CELL_ENDPOINT, params)) {
            if (!response.isSuccessful()) {
                Platform.runLater(() -> createErrorPopup("Failed to filter data", "Error"));
                return null;
            }

            String filteredSheetCellAsJson = response.body().string();
            return Constants.GSON_INSTANCE.fromJson(filteredSheetCellAsJson, DtoContainerData.class);

        } catch (IOException e) {
            Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
            return null;
        }
    }

    private void createFilterGridPopUpp(DtoContainerData filteredSheetCell) {
        popUpWindowsHandler.openFilterGridPopUp(filteredSheetCell, gridScrollerController);
    }

    public void adjustCellSize(int toIncreaseOrDecrease,  String rowOrCol) {
        gridScrollerController.changingGridConstraints(rowOrCol,toIncreaseOrDecrease);
    }

    public void ColumnSelected() {
        model.setColumnSelected(true);
    }

    public void RowSelected() {
        model.setRowSelected(true);
    }

    public BooleanProperty getIsColumnSelectedProperty() {
        return model.getIsColumnSelectedProperty();
    }

    public BooleanProperty getIsRowSelectedProperty() {
        return model.getIsRowSelectedProperty();
    }

    public BooleanProperty getReadingXMLSuccessProperty() {
        return model.getReadingXMLSuccess();
    }

    public void changeTextAlignment(String alignment, Label selectedColumnLabel) {
    gridScrollerController.changeTextAlignment(alignment, selectedColumnLabel.getText());
    }

    public ObservableValue<String> getCellLocationProperty() {
        return model.getCellLocationProperty();
    }

    public void changeBackgroundColor(javafx.scene.paint.Color value, String location) {
        gridScrollerController.changeBackgroundTextColor(value, location);
    }

    public void changeTextColor(javafx.scene.paint.Color value, String location) {
        gridScrollerController.changeTextColor(value, location);
    }

    public void closeMenuButtonClicked() {
        System.exit(0);
    }

    public void classicDisplayClicked() {
        applyTheme(() -> themeManager.classicDisplayClicked(
                headerController::changeToClassicTheme,
                rangesController::changeToClassicTheme,
                actionLineController::changeToClassicTheme,
                customizeController::changeToClassicTheme));
    }

    public void sunBurstDisplayClicked() {
        applyTheme(() -> themeManager.sunBurstDisplayClicked(
                headerController::changeToSunBurstTheme,
                rangesController::changeToSunBurstTheme,
                actionLineController::changeToSunBurstTheme,
                customizeController::changeToSunBurstTheme));
    }

    public void midNightDisplayClicked() {
        applyTheme(() -> themeManager.midNightDisplayClicked(
                headerController::changeToDarkTheme,
                rangesController::changeToDarkTheme,
                actionLineController::changeToDarkTheme,
                customizeController::changeToDarkTheme));
    }

    private void applyTheme(Runnable themeMethod) {
        themeManager.setMainPaneStyleClass(mainPane);
        themeManager.setLeftCommandsStyleClass(leftCommands);
        themeMethod.run();
    }

    public void runtimeAnalysisClicked() {
        // Step 1: Fetch runtime analysis data
        RunTimeAnalysisData runTimeAnalysisData = popUpWindowsHandler.openRunTimeAnalysisWindow();
        if (runTimeAnalysisData.getCellId().isEmpty()) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                String cellId = runTimeAnalysisData.getCellId().toUpperCase();
                int startingValue = runTimeAnalysisData.getStartingValue();
                int endingValue = runTimeAnalysisData.getEndingValue();
                int stepValue = runTimeAnalysisData.getStepValue();
                String currentValue;

                // Step 2: Save the current sheet cell state through the server
                if (!sendSaveSheetCellRequest()) {
                    return;
                }

                // Step 3: Fetch sheet cell data from the server
                DtoSheetCell dtoSheetCell = fetchDtoSheetCell();
                if (dtoSheetCell == null) {
                    return;
                }

                // Step 4: Fetch the requested cell data from the server
                DtoCell dtoCell = fetchRequestedCell(cellId);
                if (dtoCell == null) {
                    return;
                }
                currentValue = dtoCell.getEffectiveValue().getValue().toString();

                // Step 5: Validate the cell value
                double currentVal = validateCellValue(currentValue, startingValue, endingValue);
                if (Double.isNaN(currentVal)) {
                    return;
                }

                // Step 6: Extract column and row information
                char col = cellId.charAt(0);
                String row = cellId.substring(1);

                // Step 7: Show the runtime analysis popup on the JavaFX Application Thread
                Platform.runLater(() -> popUpWindowsHandler.showRuntimeAnalysisPopup(
                        dtoSheetCell, startingValue, endingValue, stepValue, currentVal, col, row, model, gridScrollerController));

                // Step 8: Restore the sheet cell state after analysis
                if (!sendRestoreSheetCellRequest()) {
                    return;
                }

            } catch (Exception e) {
                Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error processing runtime analysis"));
            }
        });
    }

    // Helper method to send a request to save the current sheet cell state
    private boolean sendSaveSheetCellRequest() {
        try (Response response = HttpRequestManager.sendPostRequestSync(Constants.SAVE_CURRENT_SHEET_CELL_STATE_ENDPOINT, new HashMap<>())) {
            if (!response.isSuccessful()) {
                Platform.runLater(() -> createErrorPopup("Failed to save current sheet cell state", "Error"));
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

    // Helper method to fetch the requested DtoCell based on cellId
    private DtoCell fetchRequestedCell(String cellId) {
        Map<String, String> params = new HashMap<>();
        params.put("cellLocation", cellId);

        try (Response response = HttpRequestManager.sendGetRequestSync(Constants.GET_REQUESTED_CELL_ENDPOINT, params)) {
            if (!response.isSuccessful()) {
                Platform.runLater(() -> createErrorPopup("Failed to load requested cell", "Error"));
                return null;
            }
            String dtoCellAsJson = response.body().string();
            return Constants.GSON_INSTANCE.fromJson(dtoCellAsJson, DtoCell.class);
        } catch (IOException e) {
            Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error fetching requested cell"));
            return null;
        }
    }

    // Helper method to validate the cell value
    private double validateCellValue(String currentValue, int startingValue, int endingValue) {
        try {
            double currentVal = Double.parseDouble(currentValue);
            if (currentVal < startingValue || currentVal > endingValue) {
                return startingValue;  // Out of range, reset to starting value
            }
            return currentVal;
        } catch (NumberFormatException e) {
            Platform.runLater(() -> createErrorPopup("Cell value must be a number", "Error"));
            return Double.NaN;  // Return NaN to indicate an invalid value
        }
    }

    // Helper method to send a request to restore the sheet cell state
    private boolean sendRestoreSheetCellRequest() {
        try (Response response = HttpRequestManager.sendPostRequestSync(Constants.RESTORE_CURRENT_SHEET_CELL_STATE_ENDPOINT, new HashMap<>())) {
            if (!response.isSuccessful()) {
                Platform.runLater(() -> createErrorPopup("Failed to restore sheet cell state", "Error"));
                return false;
            }
            return true;
        } catch (IOException e) {
            Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error restoring sheet cell state"));
            return false;
        }
    }


    public void makeGraphClicked(boolean isChartGraph) {

          List<String> columnsForXYaxis = popUpWindowsHandler.openGraphWindow();

          char xAxis = columnsForXYaxis.get(0).charAt(0);
          char yAxis = columnsForXYaxis.get(1).charAt(0);

          List<Character> columns = new ArrayList<>();
            columns.add(xAxis);
            columns.add(yAxis);

          String xTitle = columnsForXYaxis.get(2);
          String yTitle = columnsForXYaxis.get(3);

          if(columnsForXYaxis != null && columnsForXYaxis.size() == 4 ){
              try {
                  Map<Character,Set<String>> columnsXYaxisToStrings = engine.getUniqueStringsInColumn(columns, isChartGraph);
                  Map<Character,List<String>> filteredColumnsXYaxisToStrings = popUpWindowsHandler.openFilterDataWithOrderPopUp(xAxis, yAxis, xTitle, yTitle, columnsXYaxisToStrings);
                    if(filteredColumnsXYaxisToStrings != null) {

                        popUpWindowsHandler.openGraphPopUp(xAxis, xTitle, yTitle, filteredColumnsXYaxisToStrings, isChartGraph);
                    }

              } catch (Exception e) {
                  createErrorPopup(e.getMessage(), "Error");
              }
          }
    }

    public void ChartGraphClicked() {
        makeGraphClicked(true);
    }

    public void linearGraphClicked() {
        makeGraphClicked(false);
    }

    @Override
    public void close() throws IOException {

    }

    public void showLoginScreen() {
        if (app != null) {
            app.showLoginScreen();  // Switch to login screen using the app reference
        }
    }

    public void showMainAppScreen() {
        if (app != null) {
            app.showMainAppScreen();  // Switch to main app screen using the app reference
        }
    }
}

