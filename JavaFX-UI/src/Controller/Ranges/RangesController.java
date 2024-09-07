package Controller.Ranges;

import Controller.Main.MainController;
import CoreParts.smallParts.CellLocation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import java.util.List;
import java.util.Objects;

public class RangesController {

    MainController mainController;

    @FXML
    private ComboBox<HBox> SystemRanges;


    @FXML
    private Button addRangeButton;

    @FXML
    private Button deleteRangeButton;

    @FXML
    private StackPane ranges;

    @FXML
    public void initialize() {
    }

    public void addRange(List<CellLocation> ranges, String rangeName) {
        // Create the label for the range name
        Label rangeLabel = new Label(rangeName);
        rangeLabel.setOnMouseClicked(event -> {
            // Handle click event for the range label
            System.out.println("Range label clicked: " + rangeName);
            // Additional logic for range label click can be added here
        });

        // Create the trashcan icon
        ImageView trashIcon = new ImageView(new Image(getClass().getResourceAsStream("/resources/trashcan.png")));
        trashIcon.setFitWidth(16);
        trashIcon.setFitHeight(16);
        trashIcon.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete the range '" + rangeName + "'?",
                    ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    // Remove the range from the ComboBox
                    SystemRanges.getItems().remove(rangeLabel.getParent()); // Removes the HBox containing rangeLabel and trashIcon
                }
            });
        });

        // Create the HBox to hold the label and trashcan icon
        HBox rangeItem = new HBox(10, rangeLabel, trashIcon);
        rangeItem.getStyleClass().add("range-item");

        // Add the HBox to the ComboBox
        SystemRanges.getItems().add(rangeItem);
    }
    @FXML
    void deleteRange(ActionEvent event) {

    }
    @FXML
    void addRangeClicked(ActionEvent event) {
        mainController.rangeAddClicked();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

}

