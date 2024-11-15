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

    @FXML
    private Button findReplaceButton;
    @FXML
    private Button autoCompleteButton;

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
    void findReplaceClicked(ActionEvent event) {
        mainController.findReplaceClicked();
    }

    //hello

    @FXML
    void autoCompleteClicked(ActionEvent event) {
        mainController.autoCompleteClicked();
    }

    @FXML
    void runtimeAnalysisClicked(ActionEvent event) {
        mainController.runtimeAnalysisClicked();
    }

    @FXML
    void ChartGraphClicked(ActionEvent event) {
        mainController.ChartGraphClicked();
    }
    @FXML
    void linearGraphClicked(ActionEvent event) {
        mainController.linearGraphClicked();
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