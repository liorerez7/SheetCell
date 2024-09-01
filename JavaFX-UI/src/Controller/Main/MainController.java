package Controller.Main;

import Controller.MenuBar.MenuBarController;
import javafx.fxml.FXML;

import java.awt.*;

public class MainController {
    @FXML
    private MenuBarController menuBarController;
    @FXML
    private MenuBar MenuBar;
    @FXML
    public void initialize() {
        menuBarController.setMainController(this);
    }
}

