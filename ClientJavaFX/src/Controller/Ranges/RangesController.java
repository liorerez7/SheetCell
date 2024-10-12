package Controller.Ranges;

import Controller.Main.MainController;
import Utility.JavaFXUtility.Utilies;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import smallParts.CellLocation;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RangesController {

    MainController mainController;

    @FXML
    private Button filterDataButton;

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
    private VBox vBoxContainer;


    @FXML
    void filterDataClicked(ActionEvent event) {
        mainController.filterDataButtonClicked();
    }

    @FXML
    void sortRowsClicked(ActionEvent event) {
        mainController.sortRowsButtonClicked();
    }

    @FXML
    void initialize(){
        initializeComboBox(SystemRanges, "Ranges");
        Utilies.setComboBoxHeaderTextColor(SystemRanges, Color.WHITE);
    }

    private void initializeComboBox(ComboBox<Label> comboBox, String defaultText) {

        comboBox.setCellFactory(cb -> new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getText());
                    setTextFill(Color.BLACK); // Set default text color for dropdown items
                }
            }
        });
        comboBox.setButtonCell(new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(defaultText);
                } else {
                    setText(item.getText());
                }
                setTextFill(Color.WHITE); // Set text color for the ComboBox header
            }
        });
        SystemRanges.setOnAction(event -> {
            Label selectedLabel = SystemRanges.getSelectionModel().getSelectedItem();
            if (selectedLabel != null) {
                handleRangeLabelClick(selectedLabel.getText());
            }
        });
        comboBox.setPromptText(defaultText);
    }

//    private void setComboBoxHeaderTextColor(ComboBox<Label> comboBox, Color color) {
//        TextField textField = (TextField) comboBox.lookup(".text-field");
//        if (textField != null) {
//            textField.setStyle("-fx-text-fill: " + toRgbString(color) + ";");
//        }
//    }

    private String toRgbString(Color color) {
        return String.format("rgb(%d,%d,%d)", (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
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

    private void setupBindings() {
        deleteRangeButton.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
        filterDataButton.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
        sortRowsButton.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
        addRangeButton.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
        SystemRanges.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
    }

    @FXML
    void addRangeClicked(ActionEvent event) {
        mainController.rangeAddClicked();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        setupBindings();

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

    public void changeToDarkTheme() {
        Utilies.setComboBoxHeaderTextColor(SystemRanges, Color.WHITE);
        Utilies.switchStyleClass(vBoxContainer, "DarkUserInterfaceSection", "UserInterfaceSection", "RangeButtonSun");
        Utilies.switchStyleClass(filterDataButton, "RangeButtonDark", "RangeButton", "RangeButtonSun");
        Utilies.switchStyleClass(addRangeButton, "RangeButtonDark", "RangeButton", "RangeButtonSun");
        Utilies.switchStyleClass(deleteRangeButton, "RangeButtonDark", "RangeButton", "RangeButtonSun");
        Utilies.switchStyleClass(sortRowsButton, "RangeButtonDark", "RangeButton", "RangeButtonSun");
        Utilies.switchStyleClass(SystemRanges, "RangeButtonDark", "RangeButton", "RangeButtonSun");
    }

    public void changeToClassicTheme() {
        Utilies.setComboBoxHeaderTextColor(SystemRanges, Color.WHITE);
        Utilies.switchStyleClass(vBoxContainer, "UserInterfaceSection", "DarkUserInterfaceSection", "SunUserInterfaceSection");
        Utilies.switchStyleClass(filterDataButton, "RangeButton", "RangeButtonDark", "RangeButtonSun");
        Utilies.switchStyleClass(addRangeButton, "RangeButton", "RangeButtonDark", "RangeButtonSun");
        Utilies.switchStyleClass(deleteRangeButton, "RangeButton", "RangeButtonDark", "RangeButtonSun");
        Utilies.switchStyleClass(sortRowsButton, "RangeButton", "RangeButtonDark", "RangeButtonSun");
        Utilies.switchStyleClass(SystemRanges, "RangeButton", "RangeButtonDark", "RangeButtonSun");
    }

    public void changeToSunBurstTheme() {
        Utilies.setComboBoxHeaderTextColor(SystemRanges, Color.BLACK);
        Utilies.switchStyleClass(vBoxContainer, "SunUserInterfaceSection", "DarkUserInterfaceSection", "UserInterfaceSection");
        Utilies.switchStyleClass(filterDataButton, "RangeButtonSun", "RangeButtonDark", "RangeButton");
        Utilies.switchStyleClass(addRangeButton, "RangeButtonSun", "RangeButtonDark", "RangeButton");
        Utilies.switchStyleClass(deleteRangeButton, "RangeButtonSun", "RangeButtonDark", "RangeButton");
        Utilies.switchStyleClass(sortRowsButton, "RangeButtonSun", "RangeButtonDark", "RangeButton");
        Utilies.switchStyleClass(SystemRanges, "RangeButtonSun", "RangeButtonDark", "RangeButton");
    }



}

