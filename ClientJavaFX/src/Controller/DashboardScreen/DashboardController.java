package Controller.DashboardScreen;

import Controller.HttpUtility.Constants;
import Controller.HttpUtility.HttpRequestManager;
import Controller.Main.MainController;
import javafx.application.Platform;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

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

    private Set<String> sheetNames = new HashSet<>();
    private Timer timer;

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

        // Start the refresher
        //startSheetNamesRefresher();
    }

    public void startSheetNamesRefresher() {
        SheetNamesRefresher refresher = new SheetNamesRefresher(
                this::addAllSheetNames,
                this::displayError);
        timer = new Timer();
        timer.schedule(refresher, 0, 1000); //
    }

    public void stopSheetNamesRefresher() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void displayError(String errorMessage) {
        // Handle the error by showing a popup or logging
        Platform.runLater(() -> mainController.createErrorPopup(errorMessage, "Error"));
    }

    @FXML
    private void onLoadSheetFileButtonClicked(ActionEvent event) throws FileNotFoundException {
        // Retrieve the current stage
        Stage stage = (Stage) loadSheetFileButton.getScene().getWindow();

        // Call the file chooser and get the selected file
        File selectedFile = openXMLFileChooser(stage);

        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            mainController.initializeGridBasedOnXML(selectedFile, filePath);
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

    public void addFilePathToTable(String sheetName) {
        sheetNames.add(sheetName);
        fileEntries.add(new SimpleStringProperty(sheetName));
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
            String sheetName = selectedEntry.getValue();
            mainController.updateCurrentGridSheet(sheetName);
            mainController.showMainAppScreen();
        }
    }

//    public void addAllSheetNames(Set<String> sheetNames) {
//        fileEntries.clear();
//        this.sheetNames.clear();
//        sheetNames.forEach(this::addFilePathToTable);
//    }

public void addAllSheetNames(Set<String> sheetNames) {
    // Get the currently selected item (if any)
    StringProperty selectedEntry = availableSheetsTable.getSelectionModel().getSelectedItem();
    String selectedSheetName = (selectedEntry != null) ? selectedEntry.getValue() : null;

    // Clear and update the file entries
    fileEntries.clear();
    this.sheetNames.clear();
    sheetNames.forEach(this::addFilePathToTable);

    // Restore the selection if the previously selected item still exists
    if (selectedSheetName != null) {
        for (StringProperty entry : fileEntries) {
            if (entry.getValue().equals(selectedSheetName)) {
                availableSheetsTable.getSelectionModel().select(entry);
                viewSheetButton.setDisable(false); // Enable the button if a valid selection is restored
                return;
            }
        }
    }

    // If the previously selected item does not exist, keep the button disabled
    viewSheetButton.setDisable(true);
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

    public void getAllSheetNamesInSystem() {
        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_ALL_SHEET_NAMES, new HashMap<>(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                throw new RuntimeException("Failed to get sheet names from server");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorMessageAsJson = response.body().string();
                    String error = Constants.GSON_INSTANCE.fromJson(errorMessageAsJson, String.class);
                    mainController.createErrorPopup(error, "Error");
                }
                String sheetNamesAsJson = response.body().string();
                Set<String> sheetNames = Constants.GSON_INSTANCE.fromJson(sheetNamesAsJson, Set.class);
                addAllSheetNames(sheetNames);
            }
        });
    }
}
