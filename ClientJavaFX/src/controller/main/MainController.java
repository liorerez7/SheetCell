package controller.main;

import controller.customize.CustomizeController;
import controller.dashboard.DashboardController;
import controller.grid.GridController;
import controller.popup.PopUpWindowsManager;
import controller.popup.find_and_replace.FindAndReplacePopupResult;
import dto.permissions.PermissionStatus;
import dto.small_parts.CellLocationFactory;
import dto.small_parts.UpdateCellInfo;
import utilities.Constants;
import utilities.http.manager.HttpRequestManager;
import controller.menu.HeaderController;
import controller.ranges.RangesController;
import controller.actionLine.ActionLineController;
import dto.components.DtoCell;
import dto.components.DtoSheetCell;
import app.SheetCellApp;
import utilities.javafx.smallparts.*;
import utilities.javafx.Model;
import utilities.javafx.ThemeManager;
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
import dto.small_parts.CellLocation;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public class MainController {

    @FXML private HeaderController headerController;
    @FXML private GridPane header;  // Updated from VBox to GridPane
    @FXML private MenuBar menuBar;
    @FXML private BorderPane mainPane;
    @FXML private ActionLineController actionLineController;
    @FXML private GridPane actionLine;
    @FXML private GridController gridScrollerController;
    @FXML private ScrollPane gridScroller;
    @FXML private CustomizeController customizeController;
    @FXML private VBox customize;
    @FXML private VBox leftCommands;
    @FXML private RangesController rangesController;
    @FXML private StackPane ranges;

    private Stage stage;
    private SheetCellApp app;  // Reference to the main application
    private final Model model;
    private final PopUpWindowsManager popUpWindowsManager;
    private final OperationHandler operationHandler;
    private final ThemeManager themeManager;
    private ProgressManager progressManager;
    private DashboardController dashController;
    private DtoSheetCell dtoSheetCellAsDataParameter;
    private String sheetName;
    private String userName;
    private PermissionStatus permissionStatus;
    private Map<CellLocation, String> cellLocationToUserName;
    private Timer sheetNameRefresher;
    private Map<Integer, UpdateCellInfo> versionToCellInfo = new HashMap<>();


    public static final int REFRESH_INTERVAL = 500;
    public static final int INITIAL_DELAY = 0;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setApp(SheetCellApp app) {
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

    public void startSheetNamesRefresher() {
        SheetGridRefresher refresher = new SheetGridRefresher(v ->
                NewVersionOfSheetIsAvailable(), this::getSheetVersion);

        sheetNameRefresher = new Timer();
        sheetNameRefresher.schedule(refresher, INITIAL_DELAY, REFRESH_INTERVAL);
    }

    private void adjustScrollPanePosition() {
        if (gridScroller != null) {
            BorderPane.setMargin(gridScroller, new Insets(20, 0, 20, 10)); // Adjust the top margin to position lower
        }
    }

    public MainController() {
        model = new Model(null);
        popUpWindowsManager = new PopUpWindowsManager();
        operationHandler = new OperationHandler(popUpWindowsManager, gridScrollerController,
                dtoSheetCellAsDataParameter, model, this);

        themeManager = new ThemeManager(mainPane, leftCommands);
    }

    public void initializeGridBasedOnXML(File xmlFile) {

        try {
            try (Response uploadFileResponse = HttpRequestManager.sendFileSync(Constants.INIT_SHEET_CELL_ENDPOINT, xmlFile)) {
                if (uploadFileResponse.isSuccessful()) {

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

    public void createNewSheet(String sheetName, int cellWidth, int cellLength, int numColumns, int numRows) {

        Map<String,String> params = new HashMap<>();
        params.put("sheetName",sheetName);
        params.put("cellWidth",cellWidth + "");
        params.put("cellLength",cellLength + "");
        params.put("numColumns",numColumns + "");
        params.put("numRows",numRows + "");

        HttpRequestManager.sendPostAsyncRequest(Constants.CREATE_NEW_SHEET_ENDPOINT, params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> createErrorPopup("Failed to create new sheet", "Error"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try(response){
                    if (!response.isSuccessful()) {
                        String errorAsJson = response.body().string();
                        String errorMessage = Constants.GSON_INSTANCE.fromJson(errorAsJson, String.class);
                        Platform.runLater(() -> createErrorPopup(errorMessage, "Error"));
                    }
                }
            }
        });
    }

    private void fetchDtoSheetCellAsync() {
        HttpRequestManager.sendGetAsyncRequest(Constants.GET_SHEET_CELL_ENDPOINT, new HashMap<>(), new Callback() {
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
        HttpRequestManager.sendGetAsyncRequest(Constants.GET_SHEET_CELL_ENDPOINT, new HashMap<>(), new Callback() {
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
                        headerController.setupName(userName);
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

    private void updateUIWithNewSheetCell(DtoSheetCell newDtoSheetCell) {
        //headerController.FileHasBeenLoaded(absolutePath);
        Map<CellLocation, Label> cellLocationLabelMap = gridScrollerController.initializeGrid(newDtoSheetCell);
//        gridScrollerController.restoreCustomizations();
        rangesController.clearAllRanges();
        model.setReadingXMLSuccess(true);
        model.setCellLabelToProperties(cellLocationLabelMap);
        model.bindCellLabelToProperties();
        model.setPropertiesByDtoSheetCell(newDtoSheetCell);
        model.setTotalVersionsProperty(newDtoSheetCell.getLatestVersion());
        rangesController.setAllRanges(newDtoSheetCell.getRanges());

        headerController.loadAllColData(newDtoSheetCell.getNumberOfColumns());
        headerController.loadAllRowData(newDtoSheetCell.getNumberOfRows());

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

        if(model.getIsCellLebalClickedProperty().getValue() == false){
            Platform.runLater(() -> createErrorPopup(Constants.CANT_UPDATE_CELL_NO_CLICK_ON_LABEL, "Error"));
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("newValue", newValue);
                params.put("colLocation", text.charAt(0) + "");
                params.put("rowLocation", text.substring(1));

                // Send POST request synchronously and close the response
                try (Response postResponse = HttpRequestManager.sendPostSyncRequest(Constants.UPDATE_CELL_ENDPOINT, params)) {
                    if (!postResponse.isSuccessful()) {
                        String errorMessageAsJson = postResponse.body().string(); // Get the error message sent by the server
                        String errorMessage = Constants.GSON_INSTANCE.fromJson(errorMessageAsJson, String.class);
                        Platform.runLater(() -> createErrorPopup(errorMessage, "Error"));
                        return;
                    }else{

                    }
                }

                try (Response sheetCellResponse = HttpRequestManager.sendGetSyncRequest(Constants.GET_SHEET_CELL_ENDPOINT, new HashMap<>())) {
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

                params.clear();
                params.put("sheetName",sheetName);

                try (Response response = HttpRequestManager.sendGetSyncRequest(Constants.GET_USER_NAME_THAT_LAST_UPDATED_CELL_ENDPOINT, params)) {

                    String userNameThatLastUpdatedTheCellMapAsJson = response.body().string();

                    cellLocationToUserName = Constants.GSON_INSTANCE.fromJson(userNameThatLastUpdatedTheCellMapAsJson,
                            new TypeToken<Map<CellLocation, String>>() {}.getType());

                    Platform.runLater(() -> {
                        model.setUserNameProperty(cellLocationToUserName.get(new CellLocation(text.charAt(0), text.substring(1))));
                    });

                }

               try(Response response = HttpRequestManager.sendGetSyncRequest(Constants.GET_VERSION_TO_CELL_INFO_MAP, params)) {

                   String versionToCellInfoAsJson = response.body().string();
                   versionToCellInfo = Constants.GSON_INSTANCE.fromJson(versionToCellInfoAsJson,
                           new TypeToken<Map<Integer, UpdateCellInfo>>() {
                           }.getType());

               }

            } catch (IOException e) {
                Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
            }
        });
    }

    public void createErrorPopup(String message, String title) {
        popUpWindowsManager.createErrorPopup(message, title);
    }

    public void rangeAddClicked(String name, String range) {

        if(name != null) //in case when just shutting the window without entering anything
        {
            Map<String,String> params = new HashMap<>();
            params.put("name",name);
            params.put("range",range);

            CompletableFuture.runAsync(() -> {

                try {

                    try (Response postResponse = HttpRequestManager.sendPostSyncRequest(Constants.ADD_RANGE_ENDPOINT, params)) {
                        if (!postResponse.isSuccessful()) {
                            String errorMessageAsJson = postResponse.body().string(); // Get the error message sent by the server
                            String errorMessage = Constants.GSON_INSTANCE.fromJson(errorMessageAsJson, String.class);
                            Platform.runLater(() -> createErrorPopup(errorMessage, "Error"));
                            return;
                        }
                    }

                    try (Response getResponse = HttpRequestManager.sendGetSyncRequest(Constants.GET_REQUESTED_RANGE_ENDPOINT, params)) {
                        if (!getResponse.isSuccessful()) {
                            String errorMessageAsJson = getResponse.body().string(); // Get the error message sent by the server
                            String errorMessage = Constants.GSON_INSTANCE.fromJson(errorMessageAsJson, String.class);
                            Platform.runLater(() -> createErrorPopup(errorMessage, "Error"));
                            return;
                        }

                        String requestedRangeAsJson = getResponse.body().string();
                        List<CellLocation> requestedRange = Constants.GSON_INSTANCE.fromJson(requestedRangeAsJson, new TypeToken<List<CellLocation>>(){}.getType());
                        rangesController.addRange(requestedRange,name);
                    }
                }
                catch (Exception e) {
                    createErrorPopup(e.getMessage(), "Error");
                }
            });
        }
    }

    public void rangeDeleteClicked(String name) {

        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        HttpRequestManager.sendPostAsyncRequest(Constants.DELETE_RANGE_ENDPOINT, params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> createErrorPopup("Failed to delete range", "Error"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorAsJson = response.body().string();
                    String errorMessage = Constants.GSON_INSTANCE.fromJson(errorAsJson, String.class);
                    Platform.runLater(() -> createErrorPopup(errorMessage, "Error"));
                }
                else {
                    Platform.runLater(() -> rangesController.deleteRange(name));
                }
            }
        });

    }

    public void handleRangeClick(String rangeName) {
        gridScrollerController.clearAllHighlights();

        Map<String,String> params = new HashMap<>();
        params.put("name",rangeName);
        HttpRequestManager.sendGetAsyncRequest(Constants.GET_REQUESTED_RANGE_ENDPOINT, params, new Callback() {
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

    public void cellClicked(String location) {

        DtoCell dtoCell = dtoSheetCellAsDataParameter.getRequestedCell(location);

        Platform.runLater(() -> {
            model.setIsCellLabelClicked(true);
            model.setLatestUpdatedVersionProperty(dtoCell);
            model.setOriginalValueLabelProperty(dtoCell);
            model.setColorProperty(true);
            actionLineController.updateCssWhenUpdatingCell(location);
            gridScrollerController.clearAllHighlights();
            gridScrollerController.showNeighbors(dtoCell);
            headerController.resetComboBox();
            model.setColumnSelected(false);
            model.setRowSelected(false);
            model.setCellLocationProperty(location);
            String userNameThatUpdatedTheCell = cellLocationToUserName.get(new CellLocation(location.charAt(0), location.substring(1)));

            if(userNameThatUpdatedTheCell != null){
                model.setUserNameProperty(userNameThatUpdatedTheCell);
            }
        });
    }

    public void previousVersionsClicked() {

        Map<String,String> parmas = new HashMap<>();
        parmas.put("versionNumber",dtoSheetCellAsDataParameter.getLatestVersion() + "");

        HttpRequestManager.sendGetAsyncRequest(Constants.GET_ALL_SHEET_CELL_VERSIONS_ENDPOINT, parmas, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> createErrorPopup("Failed to get version", "Error"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String requestedVersionAsJson = response.body().string();

                // Use TypeToken to define the structure of the Map<Integer, DtoSheetCell>
                Type mapType = new TypeToken<Map<Integer, DtoSheetCell>>() {}.getType();

                // Deserialize the JSON into a Map using the specified Type
                Map<Integer, DtoSheetCell> dtoVersions = Constants.GSON_INSTANCE.fromJson(requestedVersionAsJson, mapType);

                Platform.runLater(() -> {
                    popUpWindowsManager.showVersionsPopup(dtoVersions,
                            dtoSheetCellAsDataParameter.getLatestVersion(), gridScrollerController, versionToCellInfo);
                });
            }
        });
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

    public void runtimeAnalysisClicked() {
        operationHandler.applyChangesInParameters(dtoSheetCellAsDataParameter, model, gridScrollerController, this);
        operationHandler.runTimeAnalysis();
    }

    public void ChartGraphClicked() {
        operationHandler.applyChangesInParameters(dtoSheetCellAsDataParameter, model, gridScrollerController, this);
        operationHandler.makeGraph(true);
    }

    public void linearGraphClicked() {
        operationHandler.applyChangesInParameters(dtoSheetCellAsDataParameter, model, gridScrollerController, this);
        operationHandler.makeGraph(false);
    }

    public void sortRowsButtonClicked() {
        operationHandler.applyChangesInParameters(dtoSheetCellAsDataParameter, model, gridScrollerController, this);
        operationHandler.sortRows();
    }

    public void filterDataButtonClicked() {
        operationHandler.applyChangesInParameters(dtoSheetCellAsDataParameter, model, gridScrollerController, this);
        operationHandler.filterGrid();
    }

    public void findReplaceClicked() {
        operationHandler.applyChangesInParameters(dtoSheetCellAsDataParameter, model, gridScrollerController, this);
        operationHandler.findAndReplace();
    }

    public void autoCompleteClicked() {
        operationHandler.applyChangesInParameters(dtoSheetCellAsDataParameter, model, gridScrollerController, this);
        operationHandler.autoComplete();
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
            app.showDashBoardScreen(userName);
            dashController.setActive();
        }
    }

    public void setDashController(DashboardController dashboardController) {
        if (dashboardController != null) {
            this.dashController = dashboardController;
        }
    }

    public void updateCurrentGridSheet(String sheetName) {

        this.sheetName = sheetName;

        gridScrollerController.setCustomization(sheetName);

        Map<String,String> params = new HashMap<>();
        params.put("sheetName",sheetName);

        CompletableFuture.runAsync(() -> {

            try{

                try(Response sheetNameResponse = HttpRequestManager.sendPostSyncRequest(Constants.UPDATE_SHEET_NAME_IN_SEASSION_ENDPOINT, params)) {
                    if (!sheetNameResponse.isSuccessful()) {
                        Platform.runLater(() -> createErrorPopup("Failed to update sheet name", "Error"));
                    }
                }

                if (dtoSheetCellAsDataParameter == null) {
                    fetchDtoSheetCellAsync(() -> {
                        if (dtoSheetCellAsDataParameter != null) {
                            startSheetNamesRefresher();
                        }
                    });

                } else {
                    model.setNewerVersionOfSheet(false);
                    fetchDtoSheetCellAsync();
                }

                params.clear();
                params.put("sheetName", sheetName);

                try (Response response = HttpRequestManager.sendGetSyncRequest(Constants.GET_USER_NAME_THAT_LAST_UPDATED_CELL_ENDPOINT, params)) {

                    String userNameThatLastUpdatedTheCellMapAsJson = response.body().string();

                    cellLocationToUserName = Constants.GSON_INSTANCE.fromJson(userNameThatLastUpdatedTheCellMapAsJson,
                            new TypeToken<Map<CellLocation, String>>() {}.getType());

                }

                try(Response response = HttpRequestManager.sendGetSyncRequest(Constants.GET_VERSION_TO_CELL_INFO_MAP, params)) {

                    String versionToCellInfoAsJson = response.body().string();
                    versionToCellInfo = Constants.GSON_INSTANCE.fromJson(versionToCellInfoAsJson,
                            new TypeToken<Map<Integer, UpdateCellInfo>>() {
                            }.getType());

                }


            }catch (IOException e){
                Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
            }


        });
    }

    public void updateCurrentGridSheet() {
        updateCurrentGridSheet(sheetName);
    }

    public void updateCurrentGridSheet(String sheetName, PermissionStatus permissionStatus) {
        this.permissionStatus = permissionStatus;
        updateCurrentGridSheet(sheetName);
        headerController.setPermissionLabel(permissionStatus);

    }

    public PermissionStatus getPermissionStatus() {
        return permissionStatus;
    }

    public void NewVersionOfSheetIsAvailable(){
        model.setNewerVersionOfSheet(true);
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

    public void exitApplication() {
        System.exit(0);
    }

    public StringProperty getOriginalValueLabelProperty() {
        return model.getOriginalValueLabelProperty();
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

    public BooleanProperty getColorProperty() {
        return model.getColorProperty();
    }

    public StringProperty getVersionProperty() {
        return model.getLatestUpdatedVersionProperty();
    }

    public BooleanProperty getIsCellLabelClickedProperty() {
        return model.getIsCellLebalClickedProperty();
    }

    public StringProperty getLastUpdatedUserNameProperty() {
        return model.getUserNameProperty();
    }

    public BooleanProperty getNewerVersionOfSheetProperty(){
        return model.getNewerVersionOfSheetProperty();
    }

    public void resetCustomizationInGrid() {
        gridScrollerController.resetCustomizationInAllSheets();
    }

    public void updateSheetAccordingToChangedCells(FindAndReplacePopupResult result) {
        Set<CellLocation> locations = result.getLocations();
        String newValue = result.getNewValue();

        try {

            Map<String, String> paramsNew = new HashMap<>();
            paramsNew.put("newValue", newValue);
            paramsNew.put("newValueLocations", Constants.GSON_INSTANCE.toJson(locations)); // Convert set to JSON

            try (Response postResponse = HttpRequestManager.sendPostSyncRequest(Constants.UPDATE_SHEET_VALUES_URL, paramsNew)) {
                if (!postResponse.isSuccessful()) {
                    String errorMessageAsJson = postResponse.body().string(); // Get the error message sent by the server
                    String errorMessage = Constants.GSON_INSTANCE.fromJson(errorMessageAsJson, String.class);
                    return;
                }
            }

            // Step 1: Load the sheet cell data from the server
            dtoSheetCellAsDataParameter = loadSheetCellData();
            if (dtoSheetCellAsDataParameter == null) return;

            int latestVersion = dtoSheetCellAsDataParameter.getLatestVersion();

            // Step 2: Retrieve DtoCell objects for specified locations
            List<DtoCell> dtoCells = fetchDtoCellsForLocations(locations);

            // Step 3: Update the UI with sheet and cell information
            updateUIWithSheetData(latestVersion, dtoCells);

            // Step 4: Get the username of the last cell updater from the server
            cellLocationToUserName = fetchUserNameOfLastCellUpdater();
            if (cellLocationToUserName != null) {
                updateUIWithUserName(locations);
            }

            // Step 5: Get the version to cell info from the server
            versionToCellInfo = fetchVersionToCellInfo();

        } catch (IOException e) {
            Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
        }
    }

    private DtoSheetCell loadSheetCellData() throws IOException {
        try (Response response = HttpRequestManager.sendGetSyncRequest(Constants.GET_SHEET_CELL_ENDPOINT, new HashMap<>())) {
            if (!response.isSuccessful()) {
                Platform.runLater(() -> createErrorPopup("Failed to load sheet", "Error"));
                return null;
            }

            String sheetCellAsJson = response.body().string();
            return Constants.GSON_INSTANCE.fromJson(sheetCellAsJson, DtoSheetCell.class);
        }
    }

    private List<DtoCell> fetchDtoCellsForLocations(Set<CellLocation> locations) {
        List<DtoCell> dtoCells = new ArrayList<>();
        for (CellLocation cellLocation : locations) {
            DtoCell dtoCell = dtoSheetCellAsDataParameter.getRequestedCell(cellLocation.getCellId());
            if (dtoCell != null) {
                dtoCells.add(dtoCell);
            }
        }
        return dtoCells;
    }

    private void updateUIWithSheetData(int latestVersion, List<DtoCell> dtoCells) {
        Platform.runLater(() -> {
            model.setPropertiesByDtoSheetCell(dtoSheetCellAsDataParameter);
            model.setTotalVersionsProperty(latestVersion);

            for (DtoCell dtoCell : dtoCells) {
                model.setLatestUpdatedVersionProperty(dtoCell);
                model.setOriginalValueLabelProperty(dtoCell);
            }
        });
    }

    private Map<CellLocation, String> fetchUserNameOfLastCellUpdater() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("sheetName", sheetName);

        try (Response response = HttpRequestManager.sendGetSyncRequest(Constants.GET_USER_NAME_THAT_LAST_UPDATED_CELL_ENDPOINT, params)) {
            if (!response.isSuccessful()) return null;

            String userNameJson = response.body().string();
            return Constants.GSON_INSTANCE.fromJson(userNameJson, new TypeToken<Map<CellLocation, String>>() {}.getType());
        }
    }

    private void updateUIWithUserName(Set<CellLocation> locations) {
        Platform.runLater(() -> {
            CellLocation firstLocation = locations.iterator().next();
            String userName = cellLocationToUserName.get(firstLocation);
            if (userName != null) {
                model.setUserNameProperty(userName);
            }
        });
    }

    private Map<Integer, UpdateCellInfo> fetchVersionToCellInfo() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("sheetName", sheetName);

        try (Response response = HttpRequestManager.sendGetSyncRequest(Constants.GET_VERSION_TO_CELL_INFO_MAP, params)) {
            if (!response.isSuccessful()) return null;

            String versionToCellInfoJson = response.body().string();

            Map<Integer, UpdateCellInfo> versionToUpdateInfo = Constants.
                    GSON_INSTANCE.fromJson(versionToCellInfoJson,
                            new TypeToken<Map<Integer, UpdateCellInfo>>() {}.getType());

            int x = 5;

            return versionToUpdateInfo;

        }
    }



    public void updateSheetInCells(Map<String, String> cellLocationToNewCellValues) {
            Map<String, String> params = new HashMap<>();
            params.put("newValues", Constants.GSON_INSTANCE.toJson(cellLocationToNewCellValues));

            try {

                try (Response postResponse = HttpRequestManager.sendPostSyncRequest(Constants.UPDATE_MULTIPLE_CELLS_URL, params)) {
                    if (!postResponse.isSuccessful()) {
                        String errorMessageAsJson = postResponse.body().string(); // Get the error message sent by the server
                        String errorMessage = Constants.GSON_INSTANCE.fromJson(errorMessageAsJson, String.class);
                        return;
                    }
                }

                // Step 1: Load the sheet cell data from the server
                dtoSheetCellAsDataParameter = loadSheetCellData();
                if (dtoSheetCellAsDataParameter == null) return;

                int latestVersion = dtoSheetCellAsDataParameter.getLatestVersion();

//                List<DtoCell> dtoCells = new ArrayList<>();
                Set<CellLocation> locations = new HashSet<>();

                cellLocationToNewCellValues.keySet().forEach(string -> {
                    CellLocation cellLocation = CellLocationFactory.fromCellId(string);
                    if(cellLocation != null){
                        locations.add(cellLocation);
                    }
                });

//                Set<String> set = cellLocationToNewCellValues.keySet();
//                set.forEach(string -> {
//                    DtoCell dtoCell = dtoSheetCellAsDataParameter.getRequestedCell(string);
//                    if(dtoCell != null){
//                        dtoCells.add(dtoCell);
//                    }
//                });

                // Step 2: Retrieve DtoCell objects for specified locations
                List<DtoCell> dtoCells = fetchDtoCellsForLocations(locations);


                // Step 3: Update the UI with sheet and cell information
                updateUIWithSheetData(latestVersion, dtoCells);

                // Step 4: Get the username of the last cell updater from the server
                cellLocationToUserName = fetchUserNameOfLastCellUpdater();
                if (cellLocationToUserName != null) {
                    updateUIWithUserName(locations);
                }

                // Step 5: Get the version to cell info from the server
                versionToCellInfo = fetchVersionToCellInfo();

            } catch (IOException e) {
                Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error"));
            }
    }
}