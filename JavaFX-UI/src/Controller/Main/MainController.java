package Controller.Main;

import Controller.Customize.CustomizeController;
import Controller.Grid.GridController;
import Controller.MenuBar.HeaderController;
import Controller.actionLine.ActionLineController;
import CoreParts.api.Engine;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.awt.*;

public class MainController {
    Engine engine;
    @FXML
    private HeaderController headerController;

    @FXML
    private VBox header;

    @FXML
    private ActionLineController actionLineController;
    @FXML
    private MenuBar menuBar;
    @FXML
    private GridController gridController;
    @FXML
    private CustomizeController customizeController;

    @FXML
    public void initialize() {
        engine = new EngineImpl();
        customizeController.setMainController(this);
        headerController.setMainController(this);
        actionLineController.setMainController(this);
        gridController.setMainController(this);
    }

    public void InitlizeGridBasedOnXML(String absolutePath) {

        try{
            engine.readSheetCellFromXML(absolutePath);

        }catch (Exception e){
            e.printStackTrace();
        }

        gridController.initializeGrid(engine.getSheetCell());


    }
}

