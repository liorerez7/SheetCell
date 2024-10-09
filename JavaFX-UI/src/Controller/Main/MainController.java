package Controller.Main;

import Controller.Customize.CustomizeController;
import Controller.Grid.GridController;
import Controller.MenuBar.HeaderController;
import Controller.ProgressManager.ProgressAnimationManager;
import Controller.Utility.*;
import Controller.Ranges.RangesController;
import Controller.actionLine.ActionLineController;
import CoreParts.api.SheetManager;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.impl.InnerSystemComponents.SheetManagerImpl;
import CoreParts.smallParts.CellLocation;
import Utility.Exception.*;
import Utility.DtoContainerData;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


import java.util.*;


public class MainController {
    SheetManager sheetManager;

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

                    // Load the XML file (this is the actual task)
                    //sheetManager.readSheetCellFromXML(absolutePath); //can throw exception

                    Platform.runLater(() -> {
                        // UI updates after loading
                        headerController.FileHasBeenLoaded(absolutePath);
                        Map<CellLocation, Label> cellLocationLabelMap = gridScrollerController.initializeGrid(sheetManager.getSheetCell());
                        rangesController.clearAllRanges();
                        model.setReadingXMLSuccess(true);
                        model.setCellLabelToProperties(cellLocationLabelMap);
                        model.bindCellLabelToProperties();
                        model.setPropertiesByDtoSheetCell(sheetManager.getSheetCell());
                        model.setTotalVersionsProperty(sheetManager.getSheetCell().getLatestVersion());
                        rangesController.setAllRanges(sheetManager.getSheetCell().getRanges());
                        customizeController.loadAllColData(sheetManager.getSheetCell().getNumberOfColumns());
                        customizeController.loadAllRowData(sheetManager.getSheetCell().getNumberOfRows());
                        themeManager.keepCurrentTheme(mainPane, leftCommands, customizeController);
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
        try {
            sheetManager.updateCell(newValue, text.charAt(0), text.substring(1));
            DtoCell requestedCell = sheetManager.getRequestedCell(text);
            model.setPropertiesByDtoSheetCell(sheetManager.getSheetCell());
            model.setLatestUpdatedVersionProperty(requestedCell);
            model.setOriginalValueLabelProperty(requestedCell);
            model.setTotalVersionsProperty(sheetManager.getSheetCell().getLatestVersion());

            gridScrollerController.showNeighbors(requestedCell);

        }catch (CycleDetectedException e) {
            createErrorPopUpCircularDependency(sheetManager.getSheetCell(), e.getCycle());
        }
        catch (Exception e) {
            createErrorPopup(e.getMessage(), "Error");
        }
    }

    public void setEngine(SheetManagerImpl engine) {
        this.sheetManager = engine;
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
        if(name != null) //in case when just shutting the window without entering anything
        {
            try {
                sheetManager.UpdateNewRange(name,rangeStringsData.getRange());
                rangesController.addRange(sheetManager.getRequestedRange(name),name);
            }
            catch (Exception e) {
                createErrorPopup(e.getMessage(), "Error");
            }
        }
    }

    public void rangeDeleteClicked() {

        Set<String> rangeNames = sheetManager.getAllRangeNames();
        RangeStringsData rangeStringsData = popUpWindowsHandler.openDeleteRangeWindow(rangeNames);
        String name = rangeStringsData.getName();

        if (name != null) //in case when just shutting the window without entering anything
        {
            try {
                sheetManager.deleteRange(name);
                rangesController.deleteRange(name);
            } catch (Exception e) {
                createErrorPopup(e.getMessage(), "Error");
            }
        }
    }

    public void cellClicked(String location) {

        DtoCell requestedCell = sheetManager.getRequestedCell(location);
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
    }

    public void handleRangeClick(String rangeName) {
        gridScrollerController.clearAllHighlights();
        gridScrollerController.showAffectedCells(sheetManager.getRequestedRange(rangeName));
    }

    public void specificVersionClicked(int versionNumber) {
        popUpWindowsHandler.openVersionGridPopUp(sheetManager.getSheetCell(versionNumber), versionNumber, gridScrollerController);
    }

    public void sortRowsButtonClicked() {

        SortRowsData sortRowsData = popUpWindowsHandler.openSortRowsWindow();
        String columns = sortRowsData.getColumnsToSortBy();
        String range = sortRowsData.getRange();

        //if one of them is null means that the user just closed the window without entering anything
        if(columns != null && range != null)
        {
            try {
                DtoContainerData dtoContainerData = sheetManager.sortSheetCell(range, columns);
                createSortGridPopUp(dtoContainerData);
            }catch (Exception e) {
                createErrorPopup(e.getMessage(), "Error");
            }
        }
    }

    private void createSortGridPopUp(DtoContainerData dtoContainerData) {
    popUpWindowsHandler.openSortGridPopUp(dtoContainerData, gridScrollerController);
    }

    public void filterDataButtonClicked() {

        boolean inputIsValid = true;

        // Open the filter data window and retrieve user input
        FilterGridData filterGridData = popUpWindowsHandler.openFilterDataWindow();
        String range = filterGridData.getRange();
        String filterColumn = filterGridData.getColumnsToFilterBy();

        // Check if the user has provided input (if window wasn't closed without action)
        if (range != null && filterColumn != null && !(filterColumn.isEmpty())) {
            try {
                // Fetch unique strings in the selected column within the given range
                Map<Character, Set<String>> columnValues = sheetManager.getUniqueStringsInColumn(filterColumn, range); // needs to return map<char,string>

                Map<Character, Set<String>> filter = popUpWindowsHandler.openFilterDataPopUp(columnValues); // also needs be map<char,string>

                boolean isFilterEmpty = filter.values().stream().allMatch(Set::isEmpty);



                if (inputIsValid && !isFilterEmpty) {
                    DtoContainerData filteredSheetCell = sheetManager.filterSheetCell(range, filter);
                    createFilterGridPopUpp(filteredSheetCell);
                }

            } catch (Exception e) {
                createErrorPopup(e.getMessage(), "Error");
            }
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

        if(runTimeAnalysisData.getCellId().isEmpty()) {
            return;
        }

        // Save current state of the sheet cell
        sheetManager.saveCurrentSheetCellState();

        // Step 2: Fetch sheet cell data and cell value
        DtoSheetCell sheetCellRunTime = sheetManager.getSheetCell();
        DtoCell dtoCell = sheetManager.getRequestedCell(runTimeAnalysisData.getCellId());

        // Extract necessary values from runtimeAnalysisData
        String cellId = runTimeAnalysisData.getCellId().toUpperCase();
        int startingValue = runTimeAnalysisData.getStartingValue();
        int endingValue = runTimeAnalysisData.getEndingValue();
        int stepValue = runTimeAnalysisData.getStepValue();
        String currentValue = dtoCell.getEffectiveValue().getValue().toString();

        double currentVal = startingValue;  // Default to starting value
        try {
            currentVal = Double.parseDouble(currentValue);
            if (currentVal < startingValue || currentVal > endingValue) {
                currentVal = startingValue;  // Out of range, reset to starting value
            }
        } catch (Exception e) {
            createErrorPopup("Cell value must be a number", "Error");
            return;
        }

        char col = cellId.charAt(0);
        String row = cellId.substring(1);

        // Step 3: Call the PopUpWindowsHandler to handle UI logic
        popUpWindowsHandler.showRuntimeAnalysisPopup(sheetCellRunTime, startingValue, endingValue, stepValue, currentVal, col, row, sheetManager, model, gridScrollerController);

        // Step 4: Restore previous state of the sheet cell after closing the popup
        sheetManager.restoreSheetCellState();
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
                  Map<Character,Set<String>> columnsXYaxisToStrings = sheetManager.getUniqueStringsInColumn(columns, isChartGraph);
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
}