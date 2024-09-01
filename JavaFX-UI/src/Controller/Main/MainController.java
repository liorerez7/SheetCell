package Controller.Main;

import Controller.MenuBar.MenuBarController;
import Controller.actionLine.ActionLineController;
import javafx.fxml.FXML;

import java.awt.*;

public class MainController {
    @FXML
    private MenuBarController menuBarController;
    @FXML
    private ActionLineController actionLineController;
    @FXML
    private MenuBar MenuBar;
    @FXML
    public void initialize() {
        menuBarController.setMainController(this);
    }
}

