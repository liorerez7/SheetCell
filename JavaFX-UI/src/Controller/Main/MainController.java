package Controller.Main;

import Controller.Customize.CustomizeController;
import Controller.Grid.GridController;
import Controller.MenuBar.HeaderController;
import Controller.actionLine.ActionLineController;
import CoreParts.api.Engine;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import expression.api.EffectiveValue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Objects;

public class MainController {
    Engine engine;

    @FXML
    private HeaderController headerController;
    @FXML private MenuBar menuBar;
    @FXML private VBox header;

    @FXML private ActionLineController actionLineController;
    @FXML private GridPane actionLine;
    @FXML
    private GridController gridController;
    @FXML
    private CustomizeController customizeController;
    private Model model;

    public StringProperty getVersionProperty() {
        return model.getLatestUpdatedVersionProperty();
    }

    public BooleanProperty getIsCellLebalClickedProperty() {
        return model.getIsCellLebalClickedProperty();
    }


    @FXML
    public void initialize() {
        customizeController.setMainController(this);
        headerController.setMainController(this);
        actionLineController.setMainController(this);
        gridController.setMainController(this);
    }

    public MainController() {
        model = new Model(null);
    }
    public void InitlizeGridBasedOnXML(String absolutePath) {

        try{
            engine.readSheetCellFromXML(absolutePath);
            headerController.FileHasBeenLoaded(absolutePath);
        }catch (Exception e){
            e.printStackTrace();
        }

        Map<CellLocation, Label> cellLocationLabelMap = gridController.initializeGrid(engine.getSheetCell());
        model.setCellLabelToProperties(cellLocationLabelMap);
        model.bindCellLebelToProperties();
        model.setPropertiesByDtoSheetCell(engine.getSheetCell());
        model.setTotalVersionsProperty(engine.getSheetCell().getLatestVersion());
    }

    public void UpdateCell(String text, String newValue) {
        try {
            engine.updateCell(newValue, text.charAt(0), text.substring(1));
            DtoCell requestedCell = engine.getRequestedCell(text);

            model.setPropertiesByDtoSheetCell(engine.getSheetCell());
            model.setLatestUpdatedVersionProperty(requestedCell);
            model.setOriginalValueLabelProperty(requestedCell);
            model.setTotalVersionsProperty(engine.getSheetCell().getLatestVersion());

            gridController.showNeighbors(requestedCell);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEngine(EngineImpl engine) {
        this.engine = engine;
    }


    public void cellClicked(String location) {

        DtoCell requestedCell = engine.getRequestedCell(location);
        model.setIsCellLebalClicked(true);
        model.setLatestUpdatedVersionProperty(requestedCell);
        model.setOriginalValueLabelProperty(requestedCell);
        actionLineController.updateCssWhenUpdatingCell(location);
        gridController.showNeighbors(requestedCell);
    }
    public StringProperty getOriginalValueLabelProperty() {
        return model.getOriginalValueLabelProperty();
    }

    public StringProperty getTotalVersionsProperty() {
        return model.getTotalVersionsProperty();
    }

    public void speceifcVersionClicked(int versionNumber) {

        DtoSheetCell dtoSheetCell = engine.getSheetCell(versionNumber);
        createPopUpVersionGrid(dtoSheetCell, versionNumber);
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
}

