package Controller.actionLine;

import Controller.Main.MainController;
import Controller.JavaFXUtility.Utilies;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

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
        if(newValue.equals("ActionLine")) {
            newValue = "";
        }
        mainController.updateCell(cellId , newValue);
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
        Utilies.setMenuButtonTextColor(VersionScroller, Color.WHITE);
        VersionScroller.setTextFill(Color.WHITE);
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
        VersionScroller.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
        newValueText.disableProperty().bind(mainController.getIsCellLabelClickedProperty().not());
        updateCellButton.disableProperty().bind(mainController.getIsCellLabelClickedProperty().not());
        lastUpdatedVersion.textProperty().bind(Bindings.concat("Last Version: ", mainController.getVersionProperty()));
        originalValue.textProperty().bind(mainController.getOriginalValueLabelProperty());
    }

    private void handleVersionClick(int versionNumber) {
        mainController.specificVersionClicked(versionNumber);
    }




    public void changeToDarkTheme() {

        Utilies.setMenuButtonTextColor(VersionScroller, Color.WHITE);
        VersionScroller.setTextFill(Color.WHITE);

        Utilies.switchStyleClass(VersionScroller, "DarkModernButton", "ModernButton", "SunModernButton");
        Utilies.switchStyleClass(actionLine, "DarkUserInterfaceSection", "UserInterfaceSection", "SunUserInterfaceSection");
        Utilies.switchStyleClass(updateCellButton, "DarkModernButton", "SunModernButton", "ModernButton");
    }

    public void changeToClassicTheme() {
        Utilies.setMenuButtonTextColor(VersionScroller, Color.WHITE);
        VersionScroller.setTextFill(Color.WHITE);

        Utilies.switchStyleClass(VersionScroller, "ModernButton", "DarkModernButton", "SunModernButton");
        Utilies.switchStyleClass(actionLine, "UserInterfaceSection", "DarkUserInterfaceSection", "SunUserInterfaceSection");
        Utilies.switchStyleClass(updateCellButton, "ModernButton", "SunModernButton", "DarkModernButton");
    }

    public void changeToSunBurstTheme() {
        Utilies.setMenuButtonTextColor(VersionScroller, Color.BLACK);
        VersionScroller.setTextFill(Color.BLACK);

        Utilies.switchStyleClass(actionLine, "SunUserInterfaceSection", "UserInterfaceSection", "DarkUserInterfaceSection");
        Utilies.switchStyleClass(VersionScroller, "SunModernButton", "ModernButton", "DarkModernButton");
        Utilies.switchStyleClass(updateCellButton, "SunModernButton", "ModernButton", "DarkModernButton");

    }
}
