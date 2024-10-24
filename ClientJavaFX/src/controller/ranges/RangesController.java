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

        // Disable addRangeButton until all text fields are filled
        addRangeButton.disableProperty().bind(
                rangeNameTextFeild.textProperty().isEmpty()
                        .or(topLeftRangeTextFeild.textProperty().isEmpty())
                        .or(rightButtonRangeTextFeild.textProperty().isEmpty())
        );

        // Disable deleteRangeButton until an item is selected in the table
        deleteRangeButton.disableProperty().bind(
                rangesTable.getSelectionModel().selectedItemProperty().isNull()
        );
    }



    private void handleRangeSelection(RangeModel newValue) {
        String rangeName = newValue.getRangeName();
        mainController.handleRangeClick(rangeName);

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
//        SystemRanges.getItems().removeIf(label ->
//                Objects.equals(label.getText(), rangeName)
//        );

        RangeModel rangeModel = rangesTable.getSelectionModel().getSelectedItem();
        if(isRangeExitsInTable(rangeModel.getRangeName())){
            rangesTable.getItems().remove(rangeModel);
        }
    }

    @FXML
    void deleteRangeClicked(ActionEvent event) {
        RangeModel rangeModel = rangesTable.getSelectionModel().getSelectedItem();
//        mainController.rangeDeleteClicked();
        mainController.rangeDeleteClicked(rangeModel.getRangeName());

    }

    private void setupBindings() {
        deleteRangeButton.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
        addRangeButton.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
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

        rangeNameTextFeild.clear();
        topLeftRangeTextFeild.clear();
        rightButtonRangeTextFeild.clear();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        setupBindings();

    }



    // Clear all ranges from the ComboBox when loading another XML file
    public void clearAllRanges() {
        rangesTable.getItems().clear();
    }

    public void setAllRanges(Map<String, List<CellLocation>> ranges) {

        ranges.forEach((rangeName, range) -> {
            if(!isRangeExitsInTable(rangeName)){
                rangesTable.getItems().add(new RangeModel(rangeName));
            }
        });
    }

    public void disableWriterButtons() {
        addRangeButton.disableProperty().unbind();
        deleteRangeButton.disableProperty().unbind();

        addRangeButton.setDisable(true);
        deleteRangeButton.setDisable(true);
    }

    public void enableWriterButtons() {
        // Reapply the original binding that disables the buttons based on the sheet property
        addRangeButton.disableProperty().bind(
                mainController.getNewerVersionOfSheetProperty()
                        .or(rangeNameTextFeild.textProperty().isEmpty())
                        .or(topLeftRangeTextFeild.textProperty().isEmpty())
                        .or(rightButtonRangeTextFeild.textProperty().isEmpty())
        );

        deleteRangeButton.disableProperty().bind(
                mainController.getNewerVersionOfSheetProperty()
                        .or(rangesTable.getSelectionModel().selectedItemProperty().isNull())
        );
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

