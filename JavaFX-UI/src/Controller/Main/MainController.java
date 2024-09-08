package Controller.Main;

import Controller.Customize.CustomizeController;
import Controller.Grid.GridController;
import Controller.MenuBar.HeaderController;
import Controller.Ranges.RangeStringsData;
import Controller.Ranges.RangesController;
import Controller.actionLine.ActionLineController;
import CoreParts.api.Engine;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.smallParts.CellLocation;
import Utility.Exception.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainController {
    Engine engine;

    @FXML
    private HeaderController headerController;
    @FXML
    private MenuBar menuBar;
    @FXML
    private VBox header;
    @FXML
    private ActionLineController actionLineController;
    @FXML
    private GridPane actionLine;
    @FXML
    private GridController gridController;
    @FXML
    private CustomizeController customizeController;
    @FXML
    private StackPane ranges;
    @FXML
    private RangesController rangesController;

    private Model model;
    private PopUpWindowsHandler popUpWindowsHandler;

    @FXML
    public void initialize() {
        customizeController.setMainController(this);
        headerController.setMainController(this);
        actionLineController.setMainController(this);
        gridController.setMainController(this);
        rangesController.setMainController(this);
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

        try {
            engine.readSheetCellFromXML(absolutePath);
            headerController.FileHasBeenLoaded(absolutePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<CellLocation, Label> cellLocationLabelMap = gridController.initializeGrid(engine.getSheetCell());
        model.setCellLabelToProperties(cellLocationLabelMap);
        model.bindCellLebelToProperties();
        model.setPropertiesByDtoSheetCell(engine.getSheetCell());
        model.setTotalVersionsProperty(engine.getSheetCell().getLatestVersion());
    }

    public void updateCell(String text, String newValue) {
        try {
            engine.updateCell(newValue, text.charAt(0), text.substring(1));
            DtoCell requestedCell = engine.getRequestedCell(text);
            model.setPropertiesByDtoSheetCell(engine.getSheetCell());
            model.setLatestUpdatedVersionProperty(requestedCell);
            model.setOriginalValueLabelProperty(requestedCell);
            model.setTotalVersionsProperty(engine.getSheetCell().getLatestVersion());

            gridController.showNeighbors(requestedCell);

        } catch (CycleDetectedException e) {
            createPopUpCircularGrid(engine.getSheetCell(), e.getCycle());
        } catch (TooManyArgumentsException e) {
            createErrorPopup(e.getMessage(),"Too Many Arguments Error");

        } catch (UnknownOperationTypeException e) {
            createErrorPopup(e.getMessage(),"Unknown Operation Type");

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void createPopUpVersionGrid(DtoSheetCell dtoSheetCell, int versionNumber) {
        // Create a new Stage (window) for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
        //popupStage.initModality(Modality.NONE); // Block events to other windows

        popupStage.setTitle("Version Grid " + versionNumber);

        // Create a new GridPane for the popup
        GridPane popupGrid = new GridPane();
        popupGrid.getStylesheets().add(Objects.requireNonNull(getClass().getResource("../Grid/ExelBasicGrid.css")).toExternalForm());

        // Initialize the grid with the DtoSheetCell (using a similar method as initializeGrid)
        gridController.initializePopupGrid(popupGrid, dtoSheetCell);

        // Create a Scene with the popupGrid
        Scene popupScene = new Scene(popupGrid);
        popupStage.setScene(popupScene);

        // Show the popup window
        popupStage.showAndWait();

        // popupStage.show();
    }

    public void createPopUpCircularGrid(DtoSheetCell dtoSheetCell, List<CellLocation> cycle) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
        //popupStage.initModality(Modality.NONE); // Block events to other windows

        popupStage.setTitle("circularDependency error");

        // Create a new GridPane for the popup
        GridPane popupGrid = new GridPane();
        popupGrid.getStylesheets().add(Objects.requireNonNull(getClass().getResource("../Grid/ExelBasicGrid.css")).toExternalForm());

        // Initialize the grid with the DtoSheetCell (using a similar method as initializeGrid)
        gridController.initializeCirclePopUp(popupGrid, dtoSheetCell, cycle);
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
            catch (IllegalArgumentException e) {
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
            catch (RangeCantBeDeleted e) {
                createErrorPopup(e.getMessage(), "Error");
            }
            catch (RangeDoesntExist e) {
                createErrorPopup(e.getMessage(), "Error");
            }
        }


    }

    public void specificVersionClicked(int versionNumber) {
        DtoSheetCell dtoSheetCell = engine.getSheetCell(versionNumber);
        createPopUpVersionGrid(dtoSheetCell, versionNumber);
    }

    public void cellClicked(String location) {

        DtoCell requestedCell = engine.getRequestedCell(location);
        model.setIsCellLebalClicked(true);
        model.setLatestUpdatedVersionProperty(requestedCell);
        model.setOriginalValueLabelProperty(requestedCell);
        actionLineController.updateCssWhenUpdatingCell(location);
        gridController.clearAllHighlights();
        gridController.showNeighbors(requestedCell);
        rangesController.resetComboBox();
    }

    public void handleRangeClick(String rangeName) {

        gridController.clearAllHighlights();
        gridController.showAffectedCells(engine.getRequestedRange(rangeName));
    }
}



