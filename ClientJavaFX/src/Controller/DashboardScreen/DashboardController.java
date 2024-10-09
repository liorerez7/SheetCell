//package Controller.DashboardScreen;
//
//import Controller.Main.MainController;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//
//import java.io.File;
//
//public class DashboardController {
//
//    @FXML
//    private Button loadSheetFileButton;
//
//    @FXML
//    private Label usernameLabel;
//
//    @FXML
//    private TableView<StringProperty> availableSheetsTable;
//
//    @FXML
//    private TableColumn<StringProperty, String> filePathColumn;
//
//    @FXML
//    private TableView<?> permissionsTable;
//
//    @FXML
//    private Button viewSheetButton;
//
//    @FXML
//    private Button requestPermissionButton;
//
//    @FXML
//    private Button ackDenyPermissionButton;
//
//    private MainController mainController;
//
//    private String username;
//
//    // List to hold the file paths as StringProperty
//    private ObservableList<StringProperty> filePaths;
//
//    @FXML
//    public void initialize() {
//        // Initialize the ObservableList
//        filePaths = FXCollections.observableArrayList();
//
//        // Set up the column to display the file paths
//        filePathColumn.setCellValueFactory(cellData -> cellData.getValue());
//
//        // Bind the ObservableList to the TableView
//        availableSheetsTable.setItems(filePaths);
//    }
//
//    @FXML
//    private void onLoadSheetFileButtonClicked(ActionEvent event) {
//        // Retrieve the current stage
//        Stage stage = (Stage) loadSheetFileButton.getScene().getWindow();
//
//        // Call the file chooser and get the selected file
//        File selectedFile = openXMLFileChooser(stage);
//
//        // Pass the selected file to the MainController and add it to the TableView
//        if (selectedFile != null) {
//            try {
//                mainController.initializeGridBasedOnXML(selectedFile.getAbsolutePath());
//            }catch (Exception e){
//                //
//            }
//        } else {
//            System.out.println("File selection canceled.");
//        }
//    }
//
//    private File openXMLFileChooser(Stage stage) {
//        // Create a new FileChooser
//        FileChooser fileChooser = new FileChooser();
//
//        // Set the title of the FileChooser dialog
//        fileChooser.setTitle("Open XML File");
//
//        // Set the initial directory to the user's home directory
//        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
//
//        // Add an extension filter to show only XML files
//        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
//        fileChooser.getExtensionFilters().add(extFilter);
//
//        // Show the open file dialog and get the selected file
//        return fileChooser.showOpenDialog(stage);
//    }
//
//    public void addFilePathToTable(String filePath, String sheetName) {
//        // Add the file path as a SimpleStringProperty to the ObservableList
//
//        filePaths.add(new SimpleStringProperty(filePath));
//    }
//
//    public void setMainController(MainController mainController) {
//        this.mainController = mainController;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//        usernameLabel.setText(username);
//    }
//
//    @FXML
//    private void onViewSheetButtonClicked(ActionEvent event) {
//        mainController.showMainAppScreen();
//    }
//
//    // Placeholder for the Request Permission Button action
//    @FXML
//    private void onRequestPermissionButtonClicked(ActionEvent event) {
//        // Logic to request permission will go here
//    }
//
//    // Placeholder for the Ack/Deny Permission Request Button action
//    @FXML
//    private void onAskDenyPermissionButtonClicked(ActionEvent event) {
//        // Logic to acknowledge or deny permission request will go here
//    }
//
//}
//

package Controller.DashboardScreen;

import Controller.Main.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class DashboardController {

    @FXML
    private Button loadSheetFileButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private TableView<StringProperty> availableSheetsTable;

    @FXML
    private TableColumn<StringProperty, String> filePathColumn;

    @FXML
    private TableView<?> permissionsTable;

    @FXML
    private Button viewSheetButton;

    @FXML
    private Button requestPermissionButton;

    @FXML
    private Button ackDenyPermissionButton;

    private MainController mainController;

    private String username;

    // List to hold the file paths and sheet names
    private ObservableList<StringProperty> fileEntries;

    @FXML
    public void initialize() {
        // Initialize the ObservableList
        fileEntries = FXCollections.observableArrayList();

        // Set up the column to display the file paths
        filePathColumn.setCellValueFactory(cellData -> cellData.getValue());

        // Bind the ObservableList to the TableView
        availableSheetsTable.setItems(fileEntries);

        availableSheetsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Disable the "View Sheet" button if no row is selected
        viewSheetButton.setDisable(true);

        // Add a listener to enable the button when a row is selected
        availableSheetsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            viewSheetButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void onLoadSheetFileButtonClicked(ActionEvent event) {
        // Retrieve the current stage
        Stage stage = (Stage) loadSheetFileButton.getScene().getWindow();

        // Call the file chooser and get the selected file
        File selectedFile = openXMLFileChooser(stage);

        // Pass the selected file to the MainController
        if (selectedFile != null) {
            mainController.initializeGridBasedOnXML(selectedFile.getAbsolutePath());
        }
    }

    private File openXMLFileChooser(Stage stage) {
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

    public void addFilePathToTable(String filePath, String sheetName) {
        // Format the display string as: "File Path: <filePath> | Sheet Name: <sheetName>"
        String displayString = String.format("File Path: %s | Sheet Name: %s", filePath, sheetName);
        fileEntries.add(new SimpleStringProperty(displayString));
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setUsername(String username) {
        this.username = username;
        usernameLabel.setText(username);
    }

    @FXML
    private void onViewSheetButtonClicked(ActionEvent event) {
        // Get the selected entry (formatted string) from the table
        StringProperty selectedEntry = availableSheetsTable.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            // Proceed with showing the main application screen
            mainController.showMainAppScreen();
        }
    }

    // Placeholder for the Request Permission Button action
    @FXML
    private void onRequestPermissionButtonClicked(ActionEvent event) {
        // Logic to request permission will go here
    }

    // Placeholder for the Ack/Deny Permission Button action
    @FXML
    private void onAskDenyPermissionButtonClicked(ActionEvent event) {
        // Logic to acknowledge or deny permission request will go here
    }
}
