package Controller.MenuBar;

import Controller.Main.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


public class HeaderController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private Button updateSheet;


    @FXML
    private MenuItem classicDisplayButton;

    @FXML
    private MenuItem midNightDisplayButton;

    @FXML
    private MenuItem sunBurstDisplayButton;

    @FXML
    private MainController mainController;

    private StringProperty pathProperty;

//    @FXML public void initialize() {
//        path.textProperty().bind(pathProperty);
//    }

    public HeaderController() {
        pathProperty = new SimpleStringProperty("");
    }

    public void FileHasBeenLoaded(String absolutePath) {
        pathProperty.setValue(absolutePath);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        updateSheet.disableProperty().bind(mainController.getNewerVersionOfSheetProperty().not());
    }

    @FXML
    void backToMenuClicked(ActionEvent event) {
        mainController.showLoginScreen();
    }

    @FXML
    void openFileChooser(ActionEvent event) {
        // Retrieve the current stage
        Stage stage = (Stage) menuBar.getScene().getWindow();

        // Call the file chooser and get the selected file
        File selectedFile = openXMLFileChooser(stage);

        // Pass the selected file to the MainController
        if (selectedFile != null) {
            //mainController.initializeGridBasedOnXML(selectedFile.getAbsolutePath());
        } else {
            System.out.println("File selection canceled.");
        }
    }

    @FXML
    void closeMenuButton(ActionEvent event) {
        mainController.closeMenuButtonClicked();
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

    @FXML
    void midNightDisplayClicked(ActionEvent event) {
        mainController.midNightDisplayClicked();
    }
    @FXML
    void sunBurstDisplayClicked(ActionEvent event) {
        mainController.sunBurstDisplayClicked();

    }
    @FXML
    void classicDisplayClicked(ActionEvent event) {
        mainController.classicDisplayClicked();

    }

    public void changeToDarkTheme() {
        menuBar.getStylesheets().clear();
     //   path.getStylesheets().clear();


     //   Utilies.switchStyleClass(path, "DarkLabelsOfUserInterfaceSection","LabelsOfUserInterfaceSection", "SunLabelsOfUserInterfaceSection");


        menuBar.getStylesheets().add(getClass().getResource("darkTheme.css").toExternalForm());
      //  path.getStylesheets().add(getClass().getResource("darkTheme.css").toExternalForm());
    }

    public void changeToClassicTheme() {
        menuBar.getStylesheets().clear();
       // path.getStylesheets().clear();


      //  Utilies.switchStyleClass(path, "LabelsOfUserInterfaceSection","DarkLabelsOfUserInterfaceSection", "SunLabelsOfUserInterfaceSection");


     //   path.getStylesheets().add(getClass().getResource("MenuBar.css").toExternalForm());
        menuBar.getStylesheets().add(getClass().getResource("MenuBar.css").toExternalForm());
    }

    public void changeToSunBurstTheme() {
        menuBar.getStylesheets().clear();
       // path.getStylesheets().clear();

    //    Utilies.switchStyleClass(path, "SunLabelsOfUserInterfaceSection","DarkLabelsOfUserInterfaceSection", "LabelsOfUserInterfaceSection");

       // path.getStylesheets().add(getClass().getResource("sunBurstTheme.css").toExternalForm());
        menuBar.getStylesheets().add(getClass().getResource("sunBurstTheme.css").toExternalForm());
    }

    @FXML
    void updateSheetClicked(ActionEvent event) {
        mainController.updateCurrentGridSheet();
        mainController.setNewerVersionOfSheetProperty(false);
    }
}


