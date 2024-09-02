package Controller.actionLine;

import Controller.Main.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.GridPane;

public class ActionLineController {

    MainController MainController;

    @FXML
    private GridPane ActionLineGrid;

    @FXML
    private Button UpdateCellButton;

    @FXML
    private MenuButton VersionScroller;

    public void setMainController(MainController mainController) {
        this.MainController = mainController;
    }
}
