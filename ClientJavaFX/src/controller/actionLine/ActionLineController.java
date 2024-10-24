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
    @FXML private Button PreviousVersionsButton;
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

    @FXML
    void updateSheetClicked(ActionEvent event) {
        mainController.updateCurrentGridSheet();
        mainController.setNewerVersionOfSheetProperty(false);
    }

    private void initializeBindings() {
        newValueText.disableProperty().bind(mainController.getIsCellLabelClickedProperty().not());
        updateCellButton.disableProperty().bind(mainController.getNewerVersionOfSheetProperty());
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

    @FXML
    void OnPreviousVersionsButtonClicked(ActionEvent event) {
        mainController.specificVersionClicked();
    }

    public void disableWriterButtons() {
        updateCellButton.disableProperty().unbind();
        updateCellButton.setDisable(true);

        newValueText.disableProperty().unbind();
        newValueText.setDisable(true);

    }

    public void enableWriterButtons() {

        boolean b1 = mainController.getNewerVersionOfSheetProperty().getValue();
        boolean b2 = !(mainController.getIsCellLabelClickedProperty().getValue());
        boolean b3 = updateSheet.isDisabled();

//        updateCellButton.disableProperty().bind(
//                Bindings.createBooleanBinding(
//                        () -> !updateSheet.isDisabled() ||
//                                !(mainController.getIsCellLabelClickedProperty().getValue()))
//        );
        updateCellButton.disableProperty().bind(mainController.getNewerVersionOfSheetProperty());
        newValueText.disableProperty().bind(mainController.getIsCellLabelClickedProperty().not());
    }
}
