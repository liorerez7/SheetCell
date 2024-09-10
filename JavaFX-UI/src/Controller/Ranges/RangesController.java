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
import java.util.Map;
import java.util.Objects;

public class RangesController {

    MainController mainController;

    @FXML
    private ComboBox<Label> SystemRanges;

    @FXML
    private Button addRangeButton;

    @FXML
    private Button deleteRangeButton;

    @FXML
    private StackPane ranges;

    @FXML
    private Button sortRowsButton;


    @FXML
    void filterDataClicked(ActionEvent event) {
        mainController.filterDataButtonClicked();
    }

    @FXML
    void sortRowsClicked(ActionEvent event) {
        mainController.sortRowsButtonClicked();
    }

    @FXML
    public void initialize() {
        // Handle rendering for ComboBox items
        SystemRanges.setCellFactory(comboBox -> new ListCell<Label>() {

            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); // Hide if null or empty
                } else {
                    setText(item.getText()); // Display label text
                }
            }
        });

        // Ensure the selected item is displayed correctly
        SystemRanges.setButtonCell(new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Ranges"); // Default text when nothing is selected
                } else {
                    setText(item.getText()); // Display selected label text
                }
            }
        });

        // Handle selection event
        SystemRanges.setOnAction(event -> {
            Label selectedLabel = SystemRanges.getSelectionModel().getSelectedItem();
            if (selectedLabel != null) {
                handleRangeLabelClick(selectedLabel.getText());
            }
        });
    }

    // Add a new range label to the ComboBox
    public void addRange(List<CellLocation> ranges, String rangeName) {
        // Create the label for the range
        Label rangeLabel = new Label(rangeName);

        // Add CSS or styling to the label if necessary
        rangeLabel.getStyleClass().add("range-item");

        // Add the label to the ComboBox
        SystemRanges.getItems().add(rangeLabel);
    }

    // Method that gets called when a range label is clicked/selected
    private void handleRangeLabelClick(String rangeName) {
        mainController.handleRangeClick(rangeName);
    }

    // Delete a range by its name from the ComboBox
    public void deleteRange(String rangeName) {
        // Remove the label with the specified range name from the ComboBox
        SystemRanges.getItems().removeIf(label ->
                Objects.equals(label.getText(), rangeName)
        );
    }

    @FXML
    void deleteRangeClicked(ActionEvent event) {
        mainController.rangeDeleteClicked();
    }

    @FXML
    void addRangeClicked(ActionEvent event) {
        mainController.rangeAddClicked();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // Reset the ComboBox to its default state when clicking other cell on grid
    public void resetComboBox() {
        SystemRanges.getSelectionModel().clearSelection(); // Clear the current selection
        SystemRanges.setPromptText("Ranges"); // Set default text after clearing selection

    }

    // Clear all ranges from the ComboBox when loading another XML file
    public void clearAllRanges() {
        SystemRanges.getItems().clear(); // Clear all items from the ComboBox
        resetComboBox(); // Reset the ComboBox to its default state
    }

    public void setAllRanges(Map<String, List<CellLocation>> ranges) {
        ranges.forEach((rangeName, range) -> addRange(range, rangeName));
    }
}


