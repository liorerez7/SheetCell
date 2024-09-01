package Controller.Main;

import Controller.Customize.CustomizeController;
import Controller.Grid.GridController;
import Controller.MenuBar.MenuBarController;
import Controller.actionLine.ActionLineController;
import CoreParts.api.Engine;
import javafx.fxml.FXML;

import java.awt.*;

public class MainController {
    Engine engine;
    @FXML
    private MenuBarController menuBarController;
    @FXML
    private ActionLineController actionLineController;
    @FXML
    private MenuBar MenuBar;
    @FXML
    private GridController gridController;
    @FXML
    private CustomizeController customizeController;
    @FXML
    public void initialize() {
        customizeController.setMainController(this);
        menuBarController.setMainController(this);
        actionLineController.setMainController(this);
        gridController.setMainController(this);
        gridController.initializeGrid(20, 20);
    }
}

