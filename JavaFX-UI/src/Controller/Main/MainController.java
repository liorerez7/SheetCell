package Controller.Main;

import Controller.Customize.CustomizeController;
import Controller.Grid.GridController;
import Controller.MenuBar.HeaderController;
import Controller.Utility.FilterGridData;
import Controller.Utility.ProgressManager;
import Controller.Utility.RangeStringsData;
import Controller.Ranges.RangesController;
import Controller.Utility.SortRowsData;
import Controller.actionLine.ActionLineController;
import CoreParts.api.Engine;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.smallParts.CellLocation;
import Utility.EngineUtilities;
import Utility.Exception.*;
import Utility.SortContainerData;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class MainController {
    Engine engine;

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

    private Model model;
    private PopUpWindowsHandler popUpWindowsHandler;
    private ProgressManager progressManager;
    private ThemeColor themeColor = ThemeColor.CLASSIC;

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
    }

    public void initializeGridBasedOnXML(String absolutePath) {

        // Use ProgressManager to get the progress pane
        StackPane progressPane = progressManager.getProgressPane();

        // Create a new StackPane to wrap the progressPane and manage its position
        StackPane wrapperPane = new StackPane(progressPane);
        // Set the margin or positioning on the wrapperPane
        StackPane.setMargin(progressPane, new Insets(125, 0, 0, 400));  // Adjust these values as needed

        // Add wrapperPane to the main layout
        if (gridScroller != null) {
            gridScroller.setContent(wrapperPane); // Temporarily show progress bar
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
                        progressManager.updateProgress(i / 100.0);  // Use ProgressManager to update progress
                        Thread.sleep(20);  // Simulate loading time
                    }

                    // Load the XML file (this is the actual task)
                    engine.readSheetCellFromXML(absolutePath); //can throw exception

                    Platform.runLater(() -> {
                        // UI updates after loading
                        headerController.FileHasBeenLoaded(absolutePath);
                        Map<CellLocation, Label> cellLocationLabelMap = gridScrollerController.initializeGrid(engine.getSheetCell());
                        rangesController.clearAllRanges();
                        model.setReadingXMLSuccess(true);
                        model.setCellLabelToProperties(cellLocationLabelMap);
                        model.bindCellLebelToProperties();
                        model.setPropertiesByDtoSheetCell(engine.getSheetCell());
                        model.setTotalVersionsProperty(engine.getSheetCell().getLatestVersion());
                        rangesController.setAllRanges(engine.getSheetCell().getRanges());
                        customizeController.loadAllColData(engine.getSheetCell().getNumberOfColumns());
                        customizeController.loadAllRowData(engine.getSheetCell().getNumberOfRows());
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
            engine.updateCell(newValue, text.charAt(0), text.substring(1));
            DtoCell requestedCell = engine.getRequestedCell(text);
            model.setPropertiesByDtoSheetCell(engine.getSheetCell());
            model.setLatestUpdatedVersionProperty(requestedCell);
            model.setOriginalValueLabelProperty(requestedCell);
            model.setTotalVersionsProperty(engine.getSheetCell().getLatestVersion());

            gridScrollerController.showNeighbors(requestedCell);

        }catch (CycleDetectedException e) {
            createErrorPopUpCircularDependency(engine.getSheetCell(), e.getCycle());
        }
        catch (Exception e) {
            createErrorPopup(e.getMessage(), "Error");
        }
//
//        } catch (CycleDetectedException e) {
//            createPopUpCircularGrid(engine.getSheetCell(), e.getCycle());
//        } catch (TooManyArgumentsException e) {
//            createErrorPopup(e.getMessage(),"Too Many Arguments Error");
//
//        } catch (UnknownOperationTypeException e) {
//            createErrorPopup(e.getMessage(),"Unknown Operation Type");
//
//        }catch(AvgWithNoNumericCellsException e) {
//            createErrorPopup(e.getMessage(), "Avg With No Numeric Cells");
//        }
//        catch (Exception e) {
//            createErrorPopup(e.getMessage(), "Error");
//        }
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
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
        //popupStage.initModality(Modality.NONE); // Block events to other windows

        popupStage.setTitle("circularDependency error");

        // Create a new GridPane for the popup
        GridPane popupGrid = new GridPane();
        popupGrid.getStylesheets().add(Objects.requireNonNull(getClass().getResource("../Grid/ExelBasicGrid.css")).toExternalForm());

        // Initialize the grid with the DtoSheetCell (using a similar method as initializeGrid)
        gridScrollerController.initializeCirclePopUp(popupGrid, dtoSheetCell, cycle);
        // Create a Scene with the popupGrid
        Scene popupScene = new Scene(popupGrid);
        popupStage.setScene(popupScene);

        // Show the popup window
        popupStage.showAndWait();

    }

    public void createErrorPopup(String message, String title) {
        // Create a new Stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle(title);
        // Create a Label and set the message
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true); // Enable text wrapping to adjust size
        messageLabel.setPadding(new Insets(10)); // Add some padding around the text
        // Create a layout and add the Label
        StackPane layout = new StackPane();
        layout.getChildren().add(messageLabel);

        // Create the Scene with the layout
        Scene scene = new Scene(layout);

        // Set the scene on the popup stage
        popupStage.setScene(scene);

        // Calculate and set the size of the popup based on the content
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            popupStage.sizeToScene(); // Adjust the size of the popup based on the content
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            popupStage.sizeToScene(); // Adjust the size of the popup based on the content
        });
        // Set modality to block input to other windows while this popup is open
        popupStage.initModality(Modality.APPLICATION_MODAL);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ErrorPopup.css")).toExternalForm());
        messageLabel.getStyleClass().add("popup-label");
        layout.getStyleClass().add("popup-container");
        // Show the popup
        popupStage.show();
    }

    public void rangeAddClicked() {

        RangeStringsData rangeStringsData = popUpWindowsHandler.openAddRangeWindow();
        String name = rangeStringsData.getName();
        if(name != null) //in case when just shutting the window without entering anything
        {
            try {
                engine.UpdateNewRange(name,rangeStringsData.getRange());
                rangesController.addRange(engine.getRequestedRange(name),name);
            }
            catch (Exception e) {
                createErrorPopup(e.getMessage(), "Error");
            }
        }
    }

    public void rangeDeleteClicked() {


        RangeStringsData rangeStringsData = popUpWindowsHandler.openDeleteRangeWindow();
        String name = rangeStringsData.getName();

        if(name != null) //in case when just shutting the window without entering anything
        {
            try {
                engine.deleteRange(name);
                rangesController.deleteRange(name);
            }
            catch (RangeCantBeDeletedException e) {
                createErrorPopup(e.getMessage(), "Error");
            }
            catch (RangeDoesntExistException e) {
                createErrorPopup(e.getMessage(), "Error");
            }
        }


    }



    public void cellClicked(String location) {

        DtoCell requestedCell = engine.getRequestedCell(location);
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
        gridScrollerController.showAffectedCells(engine.getRequestedRange(rangeName));
    }

    public void specificVersionClicked(int versionNumber) {
        DtoSheetCell dtoSheetCell = engine.getSheetCell(versionNumber);
        createPopUpVersionGrid(dtoSheetCell, versionNumber);
    }

    private void createPopUpVersionGrid(DtoSheetCell dtoSheetCell, int versionNumber) {

        popUpWindowsHandler.openVersionGridPopUp(dtoSheetCell, versionNumber, gridScrollerController);

    }

    public void sortRowsButtonClicked() {

        SortRowsData sortRowsData = popUpWindowsHandler.openSortRowsWindow();
        String columns = sortRowsData.getColumnsToSortBy();
        String range = sortRowsData.getRange();

        //if one of them is null means that the user just closed the window without entering anything
        if(columns != null && range != null)
        {
            try {
                //DtoSheetCell dtoSheetCell = engine.sortSheetCell(range, columns);
                SortContainerData sortContainerData = engine.sortSheetCell(range, columns);
                //createSortRowsPopUp(dtoSheetCell);
                createSortGridPopUp(sortContainerData);
            }catch (Exception e) {
                createErrorPopup(e.getMessage(), "Error");
            }
        }
    }

    private void createSortGridPopUp(SortContainerData sortContainerData) {
        popUpWindowsHandler.openSortGridPopUp(sortContainerData, gridScrollerController);
    }

//    public void filterDataButtonClicked() {
//
//        boolean inputNotFromOptions = true;
//        FilterGridData filterGridData = popUpWindowsHandler.openFilterDataWindow();
//        String range = filterGridData.getRange();
//        String filterColumn = filterGridData.getColumnsToFilterBy();
//
//        //if one of them is null means that the user just closed the window without entering anything
//        if(range != null && filterColumn != null)
//        {
//            try {
//                Set<String> stringsInChosenColumn = engine.getUniqueStringsInColumn(filterColumn, range);
//                // gets a filtered string from the user, for example: "banana, 5, true"
//                String filter = popUpWindowsHandler.openFilterDataPopUp(stringsInChosenColumn);
//                List<String> filteredStrings = EngineUtilities.extractLetters(filter);
//
//                filteredStrings.forEach(s -> {
//                    if(!stringsInChosenColumn.contains(s)){
//                        createErrorPopup( s + " is not in columns you chose", "Error");
//                        inputNotFromOptions = false;
//                    }
//                });
//
//                if(inputNotFromOptions) {
//                    DtoSheetCell dtoSheetCell = engine.filterSheetCell(range, filter, filterColumn);
//                    createFilterGridPopUp(dtoSheetCell);
//                }
//
//            }catch (Exception e) {
//                createErrorPopup(e.getMessage(), "Error");
//            }
//        }
//    }


    public void filterDataButtonClicked() {
        boolean inputIsValid = true;

        // Open the filter data window and retrieve user input
        FilterGridData filterGridData = popUpWindowsHandler.openFilterDataWindow();
        String range = filterGridData.getRange();
        String filterColumn = filterGridData.getColumnsToFilterBy();

        // Check if the user has provided input (if window wasn't closed without action)
        if (range != null && filterColumn != null) {
            try {
                // Fetch unique strings in the selected column within the given range
                Set<String> columnValues = engine.getUniqueStringsInColumn(filterColumn, range); // needs to return map<char,string>

                // Get user filter input, e.g., "banana, 5, true"
                String filter = popUpWindowsHandler.openFilterDataPopUp(columnValues); // also needs be map<char,string>

                if(filter == null || filter.isEmpty()) {
                    return;
                }

                List<String> filteredStrings = EngineUtilities.extractLetters(filter);
                // bigger testing to check for every char that all the given stings are in it
                for (String input : filteredStrings) {
                    if (!columnValues.contains(input)) {
                        createErrorPopup(input + " is not in the column you chose", "Error");
                        inputIsValid = false;
                        break;
                    }
                }

                // If input is valid, filter the sheet and display the result
                if (inputIsValid) {
                    DtoSheetCell filteredSheetCell = engine.filterSheetCell(range, filter, filterColumn); // instead of filter&filterColumn will be : map<char,string>
                    createFilterGridPopUp(filteredSheetCell);
                }

            } catch (Exception e) {
                createErrorPopup(e.getMessage(), "Error");
            }
        }
    }


    private void createFilterGridPopUp(DtoSheetCell dtoSheetCell) {
        popUpWindowsHandler.openFilterGridPopUp(dtoSheetCell, gridScrollerController);
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

        if(themeColor == ThemeColor.CLASSIC) {
            return;
        }

        if(themeColor == ThemeColor.MIDNIGHT) {
            mainPane.getStyleClass().remove("DarkModeBackground");
            leftCommands.getStyleClass().remove("DarkModeBackground");
        }
        else if(themeColor == ThemeColor.SUNBURST) {
            mainPane.getStyleClass().remove("SunBurstBackground");
            leftCommands.getStyleClass().remove("SunBurstBackground");
        }

        mainPane.getStyleClass().add("Background");
        leftCommands.getStyleClass().add("Background");


        headerController.changeToClassicTheme();
        rangesController.changeToClassicTheme();
        actionLineController.changeToClassicTheme();
        customizeController.changeToClassicTheme();

        themeColor = ThemeColor.CLASSIC;
    }

    public void sunBurstDisplayClicked() {

        if(themeColor == ThemeColor.SUNBURST) {
            return;
        }

        if(themeColor == ThemeColor.MIDNIGHT) {
            mainPane.getStyleClass().remove("DarkModeBackground");
            leftCommands.getStyleClass().remove("DarkModeBackground");
        }
        else if(themeColor == ThemeColor.CLASSIC) {
            mainPane.getStyleClass().remove("Background");
            leftCommands.getStyleClass().remove("Background");
        }

        mainPane.getStyleClass().add("SunBurstBackground");
        leftCommands.getStyleClass().add("SunBurstBackground");

        headerController.changeToSunBurstTheme();
        rangesController.changeToSunBurstTheme();
        actionLineController.changeToSunBurstTheme();
        customizeController.changeToSunBurstTheme();

        themeColor = ThemeColor.SUNBURST;
    }

    public void midNightDisplayClicked() {

        if(themeColor == ThemeColor.MIDNIGHT) {
            return;
        }

        if(themeColor == ThemeColor.CLASSIC) {
            mainPane.getStyleClass().remove("Background");
            leftCommands.getStyleClass().remove("Background");
        }
        else if(themeColor == ThemeColor.SUNBURST) {
            mainPane.getStyleClass().remove("SunBurstBackground");
            leftCommands.getStyleClass().remove("SunBurstBackground");
        }

        mainPane.getStyleClass().add("DarkModeBackground");
        leftCommands.getStyleClass().add("DarkModeBackground");

        headerController.changeToDarkTheme();
        actionLineController.changeToDarkTheme();
        customizeController.changeToDarkTheme();
        rangesController.changeToDarkTheme();

        themeColor = ThemeColor.MIDNIGHT;
    }
}



