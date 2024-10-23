package controller.ranges;

import controller.main.MainController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import utilities.javafx.Utilities;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import dto.small_parts.CellLocation;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RangesController {

    @FXML private ComboBox<Label> SystemRanges;
    @FXML private Button addRangeButton;
    @FXML private Button deleteRangeButton;
    @FXML private StackPane ranges;
    @FXML private VBox vBoxContainer;
    @FXML private TableView<RangeModel> rangesTable;
    @FXML private TableColumn<RangeModel, String> rangeNameColumn;
    @FXML private TextField rangeNameTextFeild;
    @FXML private TextField topLeftRangeTextFeild;
    @FXML private TextField rightButtonRangeTextFeild;

    MainController mainController;

    @FXML
    void initialize(){
        initializeComboBox(SystemRanges, "Ranges");
        Utilities.setComboBoxHeaderTextColor(SystemRanges, Color.WHITE);
        rangesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add listener to the table to detect selection
        rangesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RangeModel>() {
            @Override
            public void changed(ObservableValue<? extends RangeModel> observable, RangeModel oldValue, RangeModel newValue) {
                if (newValue != null) {
                    // Handle the selected item here
                    handleRangeSelection(newValue);
                }
            }
        });
    }



    private void handleRangeSelection(RangeModel newValue) {
        String rangeName = newValue.getRangeName();
        mainController.handleRangeClick(rangeName);

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

    // Add a new range label to the ComboBox
    public void addRange(List<CellLocation> ranges, String rangeName) {
        if(!isRangeExitsInTable(rangeName)){
            rangesTable.getItems().add(new RangeModel(rangeName));
        }
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
        addRangeButton.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
        SystemRanges.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
    }

    @FXML
    void addRangeClicked(ActionEvent event) {
        String rangeName = rangeNameTextFeild.getText();
        String topLeftRange = topLeftRangeTextFeild.getText();
        String rightButtonRange = rightButtonRangeTextFeild.getText();

        String range = topLeftRange + ".." + rightButtonRange;

        if(isRangeExitsInTable(rangeName)){
            mainController.createErrorPopup("Range name already exists", "Error");
        }

        mainController.rangeAddClicked(rangeName, range);
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

        ranges.forEach((rangeName, range) -> {
            rangesTable.getItems().add(new RangeModel(rangeName));
        });
    }

    public void changeToDarkTheme() {
        Utilities.setComboBoxHeaderTextColor(SystemRanges, Color.WHITE);
        Utilities.switchStyleClass(vBoxContainer, "DarkUserInterfaceSection", "UserInterfaceSection", "RangeButtonSun");
        Utilities.switchStyleClass(addRangeButton, "RangeButtonDark", "RangeButton", "RangeButtonSun");
        Utilities.switchStyleClass(deleteRangeButton, "RangeButtonDark", "RangeButton", "RangeButtonSun");
        Utilities.switchStyleClass(SystemRanges, "RangeButtonDark", "RangeButton", "RangeButtonSun");
    }

    public void changeToClassicTheme() {
        Utilities.setComboBoxHeaderTextColor(SystemRanges, Color.WHITE);
        Utilities.switchStyleClass(vBoxContainer, "UserInterfaceSection", "DarkUserInterfaceSection", "SunUserInterfaceSection");
        Utilities.switchStyleClass(addRangeButton, "RangeButton", "RangeButtonDark", "RangeButtonSun");
        Utilities.switchStyleClass(deleteRangeButton, "RangeButton", "RangeButtonDark", "RangeButtonSun");
        Utilities.switchStyleClass(SystemRanges, "RangeButton", "RangeButtonDark", "RangeButtonSun");
    }

    public void changeToSunBurstTheme() {
        Utilities.setComboBoxHeaderTextColor(SystemRanges, Color.BLACK);
        Utilities.switchStyleClass(vBoxContainer, "SunUserInterfaceSection", "DarkUserInterfaceSection", "UserInterfaceSection");
        Utilities.switchStyleClass(addRangeButton, "RangeButtonSun", "RangeButtonDark", "RangeButton");
        Utilities.switchStyleClass(deleteRangeButton, "RangeButtonSun", "RangeButtonDark", "RangeButton");
        Utilities.switchStyleClass(SystemRanges, "RangeButtonSun", "RangeButtonDark", "RangeButton");
    }


    public void disableWriterButtons() {
        addRangeButton.disableProperty().unbind();
        deleteRangeButton.disableProperty().unbind();

        addRangeButton.setDisable(true);
        deleteRangeButton.setDisable(true);
    }

    public void enableWriterButtons() {
        addRangeButton.disableProperty().bind(mainController.getNewerVersionOfSheetProperty());
        deleteRangeButton.disableProperty().bind(mainController.getNewerVersionOfSheetProperty());
    }

    private boolean isRangeExitsInTable(String rangeName){
        for(RangeModel rangeModel : rangesTable.getItems()){
            if(rangeModel.getRangeName().equals(rangeName)){
                return true;
            }
        }
        return false;
    }
}

