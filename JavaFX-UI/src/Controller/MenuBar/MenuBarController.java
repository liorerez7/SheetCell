package Controller.MenuBar;

import Controller.Main.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;

public class MenuBarController {
    @FXML
    private MenuBar MenuBar;
    @FXML
    private MainController MainController;

    public void setMainController(MainController mainController) {
        this.MainController = mainController;
    }

}
