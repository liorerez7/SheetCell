package Controller.Main;

import Controller.Customize.CustomizeController;
import Controller.Grid.GridController;
import Controller.MenuBar.HeaderController;
import Controller.actionLine.ActionLineController;
import CoreParts.api.Engine;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import CoreParts.smallParts.CellLocation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
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
    Map<CellLocation, Label> cellLocationToLabel = new HashMap<>();
    @FXML
    public void initialize() {
        customizeController.setMainController(this);
        headerController.setMainController(this);
        actionLineController.setMainController(this);
        gridController.setMainController(this);
    }

    public void InitlizeGridBasedOnXML(String absolutePath) {

        try{
            engine.readSheetCellFromXML(absolutePath);
            headerController.FileHasBeenLoaded(absolutePath);
        }catch (Exception e){
            e.printStackTrace();
        }
        gridController.initializeGrid(engine.getSheetCell());
    }

    public void UpdateCell(String text, String newValue) {
        try {
            engine.updateCell(newValue, text.charAt(0), text.substring(1));
            gridController.initializeGrid(engine.getSheetCell());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEngine(EngineImpl engine) {
        this.engine = engine;
    }

    public void setCellsLablesMap(Map<CellLocation,Label> cellLocationToLabel) {
        this.cellLocationToLabel = cellLocationToLabel;
    }

    public void cellClicked(String location) {
        actionLineController.cellClicked(location);
    }
}

