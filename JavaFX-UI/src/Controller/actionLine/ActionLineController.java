package Controller.actionLine;

import Controller.Main.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ActionLineController {

    MainController mainController;

    @FXML private GridPane actionLine;

    @FXML private Label cellidLabel;

    @FXML private Button UpdateCellButton;

    @FXML private MenuButton VersionScroller;

    @FXML private TextField newValueText;
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    @FXML
    void UpdateCell(ActionEvent event) {
        String newValue = newValueText.getText();
        String cellId = cellidLabel.getText();
        String id = "d4";
        mainController.UpdateCell(id , newValue);
    }

}
