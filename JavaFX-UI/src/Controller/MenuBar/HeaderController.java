package Controller.MenuBar;

import Controller.Main.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


public class HeaderController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private Label path;

    @FXML
    private MainController MainController;

    private StringProperty pathProperty;

    @FXML public void initialize() {
        path.textProperty().bind(pathProperty);
    }

    public HeaderController() {
        pathProperty = new SimpleStringProperty("");
    }

    public void FileHasBeenLoaded(String absolutePath) {
        pathProperty.setValue(absolutePath);
    }

    public void setMainController(MainController mainController) {
        this.MainController = mainController;
    }

    @FXML
    void openFileChooser(ActionEvent event) {
        // Retrieve the current stage
        Stage stage = (Stage) menuBar.getScene().getWindow();

        // Call the file chooser and get the selected file
        File selectedFile = openXMLFileChooser(stage);

        // Pass the selected file to the MainController
        if (selectedFile != null) {
            MainController.initializeGridBasedOnXML(selectedFile.getAbsolutePath());
        } else {
            System.out.println("File selection canceled.");
        }
    }

    public File openXMLFileChooser(Stage stage) {
        // Create a new FileChooser
        FileChooser fileChooser = new FileChooser();

        // Set the title of the FileChooser dialog
        fileChooser.setTitle("Open XML File");

        // Set the initial directory to the user's home directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Add an extension filter to show only XML files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show the open file dialog and get the selected file
        return fileChooser.showOpenDialog(stage);
    }
}


