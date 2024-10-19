package controller.customize;

import controller.main.MainController;
import utilities.javafx.Utilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class CustomizeController {

    MainController mainController;

    @FXML private VBox customize;
    @FXML private MenuButton makeGraphButton;
    @FXML private GridPane customizeGridPane;
    @FXML private Button runtimeButton;
    @FXML private MenuItem linearGraphButton;
    @FXML private MenuItem ChartGraphButton;
    @FXML private Button filterDataButton;
    @FXML private Button sortRowsButton;


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        setupBindings();
    }

    private void setupBindings() {
        makeGraphButton.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
        runtimeButton.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
        filterDataButton.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
        sortRowsButton.disableProperty().bind(mainController.getReadingXMLSuccessProperty().not());
    }

    @FXML
    public void initialize() {
    }


    @FXML
    void runtimeAnalysisClicked(ActionEvent event) {
        mainController.runtimeAnalysisClicked();
    }

    private void setComboBoxCellFactory(ComboBox<Label> comboBox, String defaultText) {
        comboBox.setCellFactory(cb -> new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getText());
            }
        });
        comboBox.setButtonCell(new ListCell<Label>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? defaultText : item.getText());
            }
        });
        comboBox.setPromptText(defaultText);
    }

    private String getSelectedComboBoxText(ComboBox<Label> comboBox) {
        Label selectedLabel = comboBox.getSelectionModel().getSelectedItem();
        return selectedLabel != null ? selectedLabel.getText() : null;
    }

    public void changeToDarkTheme() {

        Utilities.switchStyleClass(customizeGridPane, "DarkUserInterfaceSection", "UserInterfaceSection", "SunUserInterfaceSection");
        Utilities.switchStyleClass(runtimeButton, "DarkModernButton", "ModernButton", "SunModernButton");
        Utilities.switchStyleClass(makeGraphButton, "DarkModernButton", "ModernButton", "SunModernButton");

        adjustTextButtonColor(Color.WHITE);
    }

    public void changeToClassicTheme() {

        Utilities.switchStyleClass(customizeGridPane, "UserInterfaceSection", "DarkUserInterfaceSection", "SunUserInterfaceSection");
        Utilities.switchStyleClass(runtimeButton, "ModernButton", "DarkModernButton", "SunModernButton");
        Utilities.switchStyleClass(makeGraphButton, "ModernButton", "DarkModernButton", "SunModernButton");


        adjustTextButtonColor(Color.WHITE);
    }

    public void changeToSunBurstTheme() {

        Utilities.switchStyleClass(customizeGridPane, "SunUserInterfaceSection", "DarkUserInterfaceSection", "UserInterfaceSection");
        Utilities.switchStyleClass(runtimeButton, "SunModernButton", "DarkModernButton", "ModernButton");
        Utilities.switchStyleClass(makeGraphButton, "SunModernButton", "DarkModernButton", "ModernButton");

        adjustTextButtonColor(Color.BLACK);
    }

    public void adjustTextButtonColor(Color color) {
        Utilities.setMenuButtonTextColor(makeGraphButton, color);
        makeGraphButton.setTextFill(color);
    }

    @FXML
    void ChartGraphClicked(ActionEvent event) {
        mainController.ChartGraphClicked();
    }
    @FXML
    void linearGraphClicked(ActionEvent event) {
        mainController.linearGraphClicked();
    }

    public void setGraphButtonColor() {
        makeGraphButton.setTextFill(Color.WHITE);
        Utilities.setMenuButtonTextColor(makeGraphButton, Color.WHITE);
    }

    @FXML
    void filterDataClicked(ActionEvent event) {
        mainController.filterDataButtonClicked();

    }

    @FXML
    void sortRowsClicked(ActionEvent event) {
        mainController.sortRowsButtonClicked();

    }
}