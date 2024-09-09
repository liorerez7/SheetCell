package Controller.Customize;

import Controller.Main.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;



public class CustomizeController {

    MainController mainController;

    @FXML
    private Button sortRowsButton;

    @FXML
    private VBox customize;

    @FXML
    void sortRowsClicked(ActionEvent event) {
        mainController.sortRowsBottomClicked();

    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
