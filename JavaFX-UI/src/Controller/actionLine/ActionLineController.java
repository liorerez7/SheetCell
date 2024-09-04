package Controller.actionLine;

import Controller.Main.MainController;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
        initializeVersionScroller();
    }


    @FXML
    public void initialize() {

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


    private void initializeVersionScroller() {
        mainController.getTotalVersionsProperty().addListener((observable, oldValue, newValue) -> {
            int latestVersion = Integer.parseInt(newValue);
            updateVersionMenuItems(latestVersion);
        });
    }

    private void updateVersionMenuItems(int latestVersion) {
        VersionScroller.getItems().clear();
        for (int i = 1; i <= latestVersion; i++) {
            final int versionNumber = i;
            MenuItem menuItem = new MenuItem("Version " + versionNumber);
            menuItem.setOnAction(e -> {
                handleVersionClick(versionNumber);
            });
            VersionScroller.getItems().add(menuItem);
        }
    }


    private void initializeBindings() {
        // Now that mainController is guaranteed to be initialized, you can safely bind properties
        newValueText.disableProperty().bind(mainController.getIsCellLebalClickedProperty().not());
        updateCellButton.disableProperty().bind(mainController.getIsCellLebalClickedProperty().not());
        lastUpdatedVersion.textProperty().bind(Bindings.concat("Last Version: ", mainController.getVersionProperty()));
        originalValue.textProperty().bind(mainController.getOriginalValueLabelProperty());
    }


    private void handleVersionClick(int versionNumber) {

        mainController.speceifcVersionClicked(versionNumber);

    }
}
