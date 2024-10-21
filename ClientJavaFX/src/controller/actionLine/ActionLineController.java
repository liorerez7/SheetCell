package controller.actionLine;

import controller.main.MainController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import utilities.javafx.Utilities;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class ActionLineController {

    @FXML private GridPane actionLine;
    @FXML private Label cellidLabel;
    @FXML private Button updateCellButton;
    @FXML private MenuButton VersionScroller;
    @FXML private TextField newValueText;
    @FXML private Label originalValue;
    @FXML private Label lastUpdatedVersion;
    @FXML private Label lastUpdatedUserName;
    @FXML private Button updateSheet;;
    private Timeline updateSheetAnimation;


    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        initializeBindings();
        initializeVersionScroller();
        setupUpdateSheetAnimation();
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
        Utilities.setMenuButtonTextColor(VersionScroller, Color.WHITE);
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
        lastUpdatedUserName.textProperty().bind(Bindings.concat("user name: ", mainController.getLastUpdatedUserNameProperty()));
        updateSheet.disableProperty().bind(mainController.getNewerVersionOfSheetProperty().not());

        updateSheet.disableProperty().addListener((obs, wasDisabled, isNowDisabled) -> {
            if (!isNowDisabled) {
                startUpdateSheetAnimation();
            } else {
                stopUpdateSheetAnimation();
            }
        });

    }

    private void setupUpdateSheetAnimation() {
        updateSheetAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.5), event -> {
                    // Apply the hover style (background color of hover) temporarily
                    updateSheet.setStyle("-fx-background-color: #6a6a6a"); // hover color from CSS
                }),
                new KeyFrame(Duration.seconds(1.0), event -> {
                    // Restore the normal style
                    updateSheet.setStyle("-fx-background-color: #4a4a4a"); // normal color from CSS
                })
        );
        updateSheetAnimation.setCycleCount(Timeline.INDEFINITE);
    }

    private void startUpdateSheetAnimation() {
        if (updateSheetAnimation != null) {
            updateSheetAnimation.play();
        }
    }

    private void stopUpdateSheetAnimation() {
        if (updateSheetAnimation != null) {
            updateSheetAnimation.stop();
            // Remove the spark effect when disabled
            updateSheet.getStyleClass().removeAll("SparkButton", "SparkButtonGray");
        }
    }


    private void handleVersionClick(int versionNumber) {
        mainController.specificVersionClicked(versionNumber);
    }



    public void changeToDarkTheme() {

        Utilities.setMenuButtonTextColor(VersionScroller, Color.WHITE);
        VersionScroller.setTextFill(Color.WHITE);

        Utilities.switchStyleClass(VersionScroller, "DarkModernButton", "ModernButton", "SunModernButton");
        Utilities.switchStyleClass(actionLine, "DarkUserInterfaceSection", "UserInterfaceSection", "SunUserInterfaceSection");
        Utilities.switchStyleClass(updateCellButton, "DarkModernButton", "SunModernButton", "ModernButton");
        Utilities.switchStyleClass(updateSheet, "DarkModernButton", "SunModernButton", "ModernButton");
    }

    public void changeToClassicTheme() {
        Utilities.setMenuButtonTextColor(VersionScroller, Color.WHITE);
        VersionScroller.setTextFill(Color.WHITE);

        Utilities.switchStyleClass(VersionScroller, "ModernButton", "DarkModernButton", "SunModernButton");
        Utilities.switchStyleClass(actionLine, "UserInterfaceSection", "DarkUserInterfaceSection", "SunUserInterfaceSection");
        Utilities.switchStyleClass(updateCellButton, "ModernButton", "SunModernButton", "DarkModernButton");
        Utilities.switchStyleClass(updateSheet, "ModernButton", "SunModernButton", "DarkModernButton");
    }

    public void changeToSunBurstTheme() {
        Utilities.setMenuButtonTextColor(VersionScroller, Color.BLACK);
        VersionScroller.setTextFill(Color.BLACK);

        Utilities.switchStyleClass(actionLine, "SunUserInterfaceSection", "UserInterfaceSection", "DarkUserInterfaceSection");
        Utilities.switchStyleClass(VersionScroller, "SunModernButton", "ModernButton", "DarkModernButton");
        Utilities.switchStyleClass(updateCellButton, "SunModernButton", "ModernButton", "DarkModernButton");
        Utilities.switchStyleClass(updateSheet, "SunModernButton", "ModernButton", "DarkModernButton");

    }

    public void disableWriterButtons() {
        updateCellButton.disableProperty().unbind();
        updateCellButton.setDisable(true);

        newValueText.disableProperty().unbind();
        newValueText.setDisable(true);

    }

    @FXML
    void updateSheetClicked(ActionEvent event) {
        mainController.updateCurrentGridSheet();
        mainController.setNewerVersionOfSheetProperty(false);
    }

    public void enableWriterButtons() {
        updateCellButton.disableProperty().bind(mainController.getIsCellLabelClickedProperty().not());
        newValueText.disableProperty().bind(mainController.getIsCellLabelClickedProperty().not());
    }
}
