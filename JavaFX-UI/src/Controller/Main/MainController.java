package Controller.Main;

import Controller.Customize.CustomizeController;
import Controller.Grid.GridController;
import Controller.MenuBar.HeaderController;
import Controller.actionLine.ActionLineController;
import CoreParts.api.Engine;
import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.smallParts.CellLocation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Map;

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
        return model.getVersionProperty();
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
    }

    public void UpdateCell(String text, String newValue) {
        try {
            engine.updateCell(newValue, text.charAt(0), text.substring(1));
            DtoCell requestedCell = engine.getRequestedCell(text);


            model.setPropertiesByDtoSheetCell(engine.getSheetCell());
            model.setVersionProperty(requestedCell);
            model.setOriginalValueLabelProperty(requestedCell);

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
        model.setVersionProperty(requestedCell);
        model.setOriginalValueLabelProperty(requestedCell);
        actionLineController.updateCssWhenUpdatingCell(location);
    }

    public StringProperty getOriginalValueLabelProperty() {
        return model.getOriginalValueLabelProperty();
    }
}

