package Controller.actionLine;

import Controller.Main.MainController;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class ActionLineController {

    private MainController mainController;

    @FXML private GridPane actionLine;

    @FXML private Label cellidLabel;

    @FXML private Button updateCellButton;

    @FXML private MenuButton VersionScroller;

    @FXML private TextField newValueText;

    @FXML private Label originalValue;

    @FXML private Label lastUpdatedVersion;




    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        initializeBindings();
    }


    @FXML
    public void initialize() {

//        newValueText.disableProperty().bind(mainController.getIsCellLebalClickedProperty().not());
//        updateCellButton.disableProperty().bind(mainController.getIsCellLebalClickedProperty().not());
//        lastUpdatedVersion.textProperty().bind(Bindings.concat("Last Version: ",mainController.getVersionProperty()));
//        originalValue.textProperty().bind(mainController.getOriginalValueLabelProperty());

    }


    @FXML
    void UpdateCell(ActionEvent event) {
        String newValue = newValueText.getText();
        String cellId = cellidLabel.getText();
        newValueText.clear();
        mainController.UpdateCell(cellId , newValue);
    }


    public void updateCssWhenUpdatingCell(String location) {
        this.originalValue.getStyleClass().remove("faded");
        cellidLabel.getStyleClass().remove("faded");
        this.originalValue.getStyleClass().add("normalOpacity");
        cellidLabel.setText(location);
    }
    @FXML
    public void clearText(MouseEvent event) {
        newValueText.clear();
        newValueText.getStyleClass().remove("faded");
    }


    private void initializeBindings() {
        // Now that mainController is guaranteed to be initialized, you can safely bind properties
        newValueText.disableProperty().bind(mainController.getIsCellLebalClickedProperty().not());
        updateCellButton.disableProperty().bind(mainController.getIsCellLebalClickedProperty().not());
        lastUpdatedVersion.textProperty().bind(Bindings.concat("Last Version: ", mainController.getVersionProperty()));
        originalValue.textProperty().bind(mainController.getOriginalValueLabelProperty());
    }
}
