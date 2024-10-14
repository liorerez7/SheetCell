package Controller.Main;

import Controller.Customize.CustomizeController;
import Controller.DashboardScreen.DashboardController;
import Controller.Grid.GridController;
import DtoComponents.PermissionStatus;
import Utility.Constants;
import Utility.HttpUtility.HttpRequestManager;
import Utility.JavaFXUtility.*;
import Controller.MenuBar.HeaderController;
import Controller.Ranges.RangesController;
import Controller.actionLine.ActionLineController;
import DtoComponents.DtoCell;
import DtoComponents.DtoContainerData;
import DtoComponents.DtoSheetCell;
import Main.sheetCellApp;
import Utility.JavaFXUtility.*;
import Utility.Model;
import Utility.ThemeManager;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import smallParts.CellLocation;


import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public class MainController implements Closeable {



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
    private DashboardController dashController;
    private Timer timer;
    private DtoSheetCell dtoSheetCellAsDataParameter;
    private String sheetName;
    private String userName;
    private PermissionStatus permissionStatus;



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

    public void initializeGridBasedOnXML(File xmlFile, String absolutePath) {

        try {
            try (Response uploadFileResponse = HttpRequestManager.sendFileSync(Constants.INIT_SHEET_CELL_ENDPOINT, xmlFile)) {
                if (uploadFileResponse.isSuccessful()) {
                    // sheetNameAsJson = uploadFileResponse.body().string();
                    //String sheetName = Constants.GSON_INSTANCE.fromJson(sheetNameAsJson, String.class);
//                    dashController.addFilePathToTable(userName, sheetName, "10x10", PermissionStatus.OWNER.toString());
                } else {
                    // Handle error response from the server
                    String errorMessageAsJson = uploadFileResponse.body().string(); // Get the error message sent by the server
                    String errorMessage = Constants.GSON_INSTANCE.fromJson(errorMessageAsJson, String.class);
                    Platform.runLater(() -> createErrorPopup(errorMessage, "Error"));
                }
            }
        } catch (IOException e) {
            Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
        }
    }

    // Helper method to fetch the DtoSheetCell asynchronously
    private void fetchDtoSheetCellAsync() {
        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_SHEET_CELL_ENDPOINT, new HashMap<>(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> createErrorPopup("Failed to load sheet: " + e.getMessage(), "Error"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (response) {
                    if (response.isSuccessful()) {
                        String sheetCellAsJson = response.body().string();
                        dtoSheetCellAsDataParameter = Constants.GSON_INSTANCE.fromJson(sheetCellAsJson, DtoSheetCell.class);
                        Platform.runLater(() -> updateUIWithNewSheetCell(dtoSheetCellAsDataParameter));
                    } else {
                        Platform.runLater(() -> createErrorPopup("Failed to load sheet: Server responded with code " + response.code(), "Error"));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error processing response"));
                }
            }
        });
    }

    private void fetchDtoSheetCellAsync(Runnable callback) {
        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_SHEET_CELL_ENDPOINT, new HashMap<>(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    createErrorPopup("Failed to load sheet: " + e.getMessage(), "Error");
                    if (callback != null) {
                        callback.run();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (response) {
                    if (response.isSuccessful()) {
                        String sheetCellAsJson = response.body().string();
                        dtoSheetCellAsDataParameter = Constants.GSON_INSTANCE.fromJson(sheetCellAsJson, DtoSheetCell.class);
                        Platform.runLater(() -> {
                            updateUIWithNewSheetCell(dtoSheetCellAsDataParameter);
                            if (callback != null) {
                                callback.run();
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            createErrorPopup("Failed to load sheet: Server responded with code " + response.code(), "Error");
                            if (callback != null) {
                                callback.run();
                            }
                        });
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        createErrorPopup(e.getMessage(), "Error processing response");
                        if (callback != null) {
                            callback.run();
                        }
                    });
                }
            }
        });
    }

    // Helper method to update the UI with the newly loaded DtoSheetCell
    private void updateUIWithNewSheetCell(DtoSheetCell newDtoSheetCell) {
        //headerController.FileHasBeenLoaded(absolutePath);
        Map<CellLocation, Label> cellLocationLabelMap = gridScrollerController.initializeGrid(newDtoSheetCell);
        rangesController.clearAllRanges();
        model.setReadingXMLSuccess(true);
        model.setCellLabelToProperties(cellLocationLabelMap);
        model.bindCellLabelToProperties();
        model.setPropertiesByDtoSheetCell(newDtoSheetCell);
        model.setTotalVersionsProperty(newDtoSheetCell.getLatestVersion());
        rangesController.setAllRanges(newDtoSheetCell.getRanges());
        customizeController.loadAllColData(newDtoSheetCell.getNumberOfColumns());
        customizeController.loadAllRowData(newDtoSheetCell.getNumberOfRows());
        themeManager.keepCurrentTheme(mainPane, leftCommands, customizeController);

        if(permissionStatus == PermissionStatus.READER){
            actionLineController.disableWriterButtons();
            rangesController.disableWriterButtons();
        }else {
            actionLineController.enableWriterButtons();
            rangesController.enableWriterButtons();
        }
    }

    public void updateCell(String text, String newValue) {

        if(model.getNewerVersionOfSheetProperty().getValue()){
            Platform.runLater(() -> createErrorPopup(Constants.CANT_UPDATE_CELL_NEWER_VERSION_MESSAGE, "Error"));
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("newValue", newValue);
                params.put("colLocation", text.charAt(0) + "");
                params.put("rowLocation", text.substring(1));

                // Send POST request synchronously and close the response
                try (Response postResponse = HttpRequestManager.sendPostRequestSync(Constants.UPDATE_CELL_ENDPOINT, params)) {
                    if (!postResponse.isSuccessful()) {
                        String errorMessageAsJson = postResponse.body().string(); // Get the error message sent by the server
                        String errorMessage = Constants.GSON_INSTANCE.fromJson(errorMessageAsJson, String.class);
                        Platform.runLater(() -> createErrorPopup(errorMessage, "Error"));
                        return;
                    }
                }

                try (Response sheetCellResponse = HttpRequestManager.sendGetRequestSync(Constants.GET_SHEET_CELL_ENDPOINT, new HashMap<>())) {
                    if (!sheetCellResponse.isSuccessful()) {
                        Platform.runLater(() -> createErrorPopup("Failed to load sheet", "Error"));
                        return;
                    }

                    String sheetCellAsJson = sheetCellResponse.body().string();
                    dtoSheetCellAsDataParameter = Constants.GSON_INSTANCE.fromJson(sheetCellAsJson, DtoSheetCell.class);

                    int latestVersion = dtoSheetCellAsDataParameter.getLatestVersion();

                    DtoCell dtoCell = dtoSheetCellAsDataParameter.getRequestedCell(text);

                    // Update the UI on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        model.setPropertiesByDtoSheetCell(dtoSheetCellAsDataParameter);
                        model.setLatestUpdatedVersionProperty(dtoCell);
                        model.setOriginalValueLabelProperty(dtoCell);
                        model.setTotalVersionsProperty(latestVersion);
                        gridScrollerController.showNeighbors(dtoCell);
                    });
                }

            } catch (IOException e) {
                Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
            }
        });
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

        if(model.getNewerVersionOfSheetProperty().getValue()){
            Platform.runLater(() -> createErrorPopup(Constants.CANT_ADD_RANGE_NEWER_VERSION_MESSAGE, "Error"));
            return;
        }

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

        if(model.getNewerVersionOfSheetProperty().getValue()){
            Platform.runLater(() -> createErrorPopup(Constants.CANT_DELETE_RANGE_NEWER_VERSION_MESSAGE, "Error"));
            return;
        }

        CompletableFuture.runAsync(() -> {

            try (Response allRangesResponse = HttpRequestManager.sendGetRequestSync(Constants.GET_ALL_RANGES_ENDPOINT, new HashMap<>())) {
                String allRangesAsJson = allRangesResponse.body().string();
                Set<String> rangeNames = Constants.GSON_INSTANCE.fromJson(allRangesAsJson, new TypeToken<Set<String>>(){}.getType());

                // Handle the next request inside Platform.runLater to stay on the UI thread
                Platform.runLater(() -> {
                    RangeStringsData rangeStringsData = popUpWindowsHandler.openDeleteRangeWindow(rangeNames);
                    String name = rangeStringsData.getName();
                    Map<String, String> params = new HashMap<>();
                    params.put("name", name);

                    if (name != null) {
                        CompletableFuture.runAsync(() -> {
                            try (Response deleteRangeResponse = HttpRequestManager.sendPostRequestSync(Constants.DELETE_RANGE_ENDPOINT, params)) {
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
    }

    public void cellClicked(String location) {

        DtoCell dtoCell = dtoSheetCellAsDataParameter.getRequestedCell(location);

        Platform.runLater(() -> {
            model.setIsCellLabelClicked(true);
            model.setLatestUpdatedVersionProperty(dtoCell);
            model.setOriginalValueLabelProperty(dtoCell);
            actionLineController.updateCssWhenUpdatingCell(location);
            gridScrollerController.clearAllHighlights();
            gridScrollerController.showNeighbors(dtoCell);
            rangesController.resetComboBox();
            customizeController.resetComboBox();
            model.setColumnSelected(false);
            model.setRowSelected(false);
            model.setCellLocationProperty(location);

        });
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
                try (response) {
                    String requestedRangeAsJson = response.body().string();
                    List<CellLocation> requestedRange = Constants.GSON_INSTANCE.fromJson(requestedRangeAsJson, new TypeToken<List<CellLocation>>(){}.getType());
                    Platform.runLater(() -> gridScrollerController.showAffectedCells(requestedRange));
                }
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
                try (response) {
                    String requestedVersionAsJson = response.body().string();
                    DtoSheetCell requestedVersion = Constants.GSON_INSTANCE.fromJson(requestedVersionAsJson, DtoSheetCell.class);
                    Platform.runLater(() -> {
                        popUpWindowsHandler.openVersionGridPopUp(requestedVersion, versionNumber, gridScrollerController);
                    });
                }
            }
        });
    }

    public void sortRowsButtonClicked() {

        SortRowsData sortRowsData = popUpWindowsHandler.openSortRowsWindow();
        String columns = sortRowsData.getColumnsToSortBy();
        String range = sortRowsData.getRange();

        if(columns == null || range == null || columns.isEmpty() || range.isEmpty()){
            return;
        }

        DtoContainerData dtoContainerData = dtoSheetCellAsDataParameter.sortSheetCell(range,columns);
        Platform.runLater(() -> createSortGridPopUp(dtoContainerData));
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

        Map<Character, Set<String>> columnValues = dtoSheetCellAsDataParameter.getUniqueStringsInColumn(filterColumn,range);

        Platform.runLater(() -> {
            Map<Character, Set<String>> filter = popUpWindowsHandler.openFilterDataPopUp(columnValues);
            boolean isFilterEmpty = filter.values().stream().allMatch(Set::isEmpty);

            // Proceed only if the filter is not empty
            if (!isFilterEmpty) {
                // Pass the filter and range information in the second HTTP request
                DtoContainerData filteredSheetCell = dtoSheetCellAsDataParameter.filterSheetCell(range, filter);
                if (filteredSheetCell != null) {
                    Platform.runLater(() -> createFilterGridPopUpp(filteredSheetCell));
                }
            }
        });
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
                //DtoSheetCell dtoSheetCell = fetchDtoSheetCell();
                DtoSheetCell dtoSheetCell = dtoSheetCellAsDataParameter;

                if (dtoSheetCell == null) {
                    return;
                }

                // Step 4: Fetch the requested cell data from the server
                //DtoCell dtoCell = fetchRequestedCell(cellId);
                DtoCell dtoCell = dtoSheetCellAsDataParameter.getRequestedCell(cellId);

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

                Platform.runLater(() -> popUpWindowsHandler.showRuntimeAnalysisPopup(
                        dtoSheetCell, startingValue, endingValue, stepValue, currentVal, col, row, model, gridScrollerController,
                        () -> CompletableFuture.runAsync(() -> {
                            if (!sendRestoreSheetCellRequest()) {
                                return;
                            }
                        })
                ));

            } catch (Exception e) {
                Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error processing runtime analysis"));
            }
        });
    }

    // Helper method to send a request to restore the sheet cell state
    private boolean sendRestoreSheetCellRequest() {
        try (Response response = HttpRequestManager.sendPostRequestSync(Constants.RESTORE_CURRENT_SHEET_CELL_STATE_ENDPOINT, new HashMap<>())) {
            if (!response.isSuccessful()) {
                Platform.runLater(() -> createErrorPopup("Failed to restore sheet cell state", "Error"));
                return false;
            }
            else{
                return true;
            }

        } catch (IOException e) {
            Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error restoring sheet cell state"));
            return false;
        }
    }

    // Helper method to send a request to save the current sheet cell state
    private boolean sendSaveSheetCellRequest() {
        try (Response response = HttpRequestManager.sendPostRequestSync(Constants.SAVE_CURRENT_SHEET_CELL_STATE_ENDPOINT, new HashMap<>())) {
            if (!response.isSuccessful()) {
                Platform.runLater(() -> createErrorPopup("Failed to save current sheet cell state", "Error"));
                return false;
            }
            else{
                return true;
            }
        } catch (IOException e) {
            Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error saving current sheet cell state"));
            return false;
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


    public void makeGraphClicked(boolean isChartGraph) {
        // Step 1: Open the Graph Window and get the selected columns and titles
        List<String> columnsForXYaxis = popUpWindowsHandler.openGraphWindow();

        if (columnsForXYaxis == null || columnsForXYaxis.size() != 4) {
            return;
        }

        char xAxis = columnsForXYaxis.get(0).charAt(0);
        char yAxis = columnsForXYaxis.get(1).charAt(0);
        List<Character> columns = new ArrayList<>();
        columns.add(xAxis);
        columns.add(yAxis);

        String xTitle = columnsForXYaxis.get(2);
        String yTitle = columnsForXYaxis.get(3);

        Map<Character, Set<String>> columnsXYaxisToStrings = dtoSheetCellAsDataParameter.getUniqueStringsInColumn(columns,isChartGraph);

        if (columnsXYaxisToStrings == null) {
            return;
        }

        // Open the filter popup and get the filtered columns
        Platform.runLater(() -> {
            Map<Character, List<String>> filteredColumnsXYaxisToStrings = popUpWindowsHandler.openFilterDataWithOrderPopUp(
                    xAxis, yAxis, xTitle, yTitle, columnsXYaxisToStrings);

            if (filteredColumnsXYaxisToStrings != null) {
                Platform.runLater(() -> popUpWindowsHandler.openGraphPopUp(
                        xAxis, xTitle, yTitle, filteredColumnsXYaxisToStrings, isChartGraph));
            }
        });
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

    public void showDashBoardScreen(String userName) {
        if (app != null) {
            app.showDashBoardScreen(userName);  // Switch to main app screen using the app reference
        }
    }

    public void setDashController(DashboardController dashboardController) {
        if (dashboardController != null) {
            this.dashController = dashboardController;
        }
    }

    public void updateCurrentGridSheet(String sheetName) {

        this.sheetName = sheetName;
        Map<String,String> params = new HashMap<>();
        params.put("sheetName",sheetName);

        CompletableFuture.runAsync(() -> {
            try(Response sheetNameResponse = HttpRequestManager.sendPostRequestSync(Constants.UPDATE_SHEET_NAME_IN_SEASSION_ENDPOINT, params)){
                if (!sheetNameResponse.isSuccessful()) {
                    Platform.runLater(() -> createErrorPopup("Failed to update sheet name", "Error"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if(dtoSheetCellAsDataParameter == null){
                fetchDtoSheetCellAsync(() -> {
                    if (dtoSheetCellAsDataParameter != null) {
                        startSheetNamesRefresher();
                    }
                });
            }
            else {
                model.setNewerVersionOfSheet(false);
                fetchDtoSheetCellAsync();
            }
        });
    }

    public void updateCurrentGridSheet() {

        updateCurrentGridSheet(sheetName);

    }

    public void updateCurrentGridSheet(String sheetName, PermissionStatus permissionStatus) {

        this.permissionStatus = permissionStatus;
        updateCurrentGridSheet(sheetName);

    }


    public void startSheetNamesRefresher() {

        SheetGridRefresher refresher = new SheetGridRefresher(v ->
                NewVersionOfSheetIsAvailable(), this::getSheetVersion);

        timer = new Timer();
        timer.schedule(refresher, 0, 2000); //
    }

    public void NewVersionOfSheetIsAvailable(){
        model.setNewerVersionOfSheet(true);
    }

    public BooleanProperty getNewerVersionOfSheetProperty(){
        return model.getNewerVersionOfSheetProperty();
    }

    public String getSheetVersion() {
        return dtoSheetCellAsDataParameter.getLatestVersion() + "";
    }

    public void setNewerVersionOfSheetProperty(boolean b) {
        model.setNewerVersionOfSheet(b);
    }

    public void setUserName(String userName){
        this.userName = userName;
        this.dashController.setUserName(userName);
    }

    public String getUserName() {
        return userName;
    }
}