package Controller.actionLine;

import Controller.Main.MainController;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ActionLineController {

    private MainController mainController;
    private BooleanProperty isCellSelected;

    @FXML private GridPane actionLine;

    @FXML private Label cellidLabel;

    @FXML private Button updateCellButton;

    @FXML private MenuButton VersionScroller;

    @FXML private TextField newValueText;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }



    public ActionLineController() {

        isCellSelected = new SimpleBooleanProperty(false);
        // Bind the disable property of the TextField and Button to the isCellSelected property

    }


    @FXML
    public void initialize() {
        newValueText.disableProperty().bind(isCellSelected.not());
        updateCellButton.disableProperty().bind(isCellSelected.not());
    }



    @FXML
    void UpdateCell(ActionEvent event) {
        String newValue = newValueText.getText();
        String cellId = cellidLabel.getText();
        mainController.UpdateCell(cellId , newValue);
    }

    public void cellClicked(String location) {

        cellidLabel.setText(location);
        isCellSelected.setValue(true);
    }
}
