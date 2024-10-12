package Controller.DashboardScreen;

import Utility.Constants;
import Utility.HttpUtility.HttpRequestManager;
import Controller.Main.MainController;
import com.google.gson.reflect.TypeToken;
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
import loginPage.users.PermissionLine;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
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

    // Permission table columns
    @FXML
    private TableView<PermissionRow> permissionsTable;

    @FXML
    private TableColumn<PermissionRow, String> usernameColumn;

    @FXML
    private TableColumn<PermissionRow, String> permissionStatusColumn;

    @FXML
    private TableColumn<PermissionRow, String> approvedByOwnerColumn;

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

    private ObservableList<PermissionRow> permissionEntries;

    private Set<String> sheetNames = new HashSet<>();
    private Timer timer;
    private List<PermissionLine> currentPermissions = new ArrayList<>();


    @FXML
    public void initialize() {
        // Initialize the ObservableList for file entries
        fileEntries = FXCollections.observableArrayList();
        // Initialize the ObservableList for permission entries
        permissionEntries = FXCollections.observableArrayList();

        // Set up the column to display the file paths
        filePathColumn.setCellValueFactory(cellData -> cellData.getValue());

        // Set up columns for permissions table
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        permissionStatusColumn.setCellValueFactory(cellData -> cellData.getValue().permissionStatusProperty());
        approvedByOwnerColumn.setCellValueFactory(cellData -> cellData.getValue().approvedByOwnerProperty());

        // Bind the ObservableLists to the TableViews
        availableSheetsTable.setItems(fileEntries);
        permissionsTable.setItems(permissionEntries);

        availableSheetsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        permissionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        // Disable the "View Sheet" button if no row is selected
        viewSheetButton.setDisable(true);

        // Add a listener to enable the button when a row is selected
        availableSheetsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            viewSheetButton.setDisable(newSelection == null);
            if (newSelection != null) {
                String sheetName = newSelection.getValue();
                updatePermissionTableForSheet(sheetName);
            }
        });

        // Start the refresher
        startSheetNamesRefresher();
    }

    public void startSheetNamesRefresher() {
        SheetNamesRefresher refresher = new SheetNamesRefresher(
                this::addAllSheetNames,
                this::displayError);
        timer = new Timer();
        timer.schedule(refresher, 0, 2000); //
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

        viewSheetButton.setDisable(true);
    }

    // Add and remove lines from the permissions table
    public void addLine(String username, String permissionStatus, String isApproved) {
        PermissionRow newRow = new PermissionRow(username, permissionStatus, isApproved);
        permissionEntries.add(newRow);
    }

    public void removeLine(String username) {
        permissionEntries.removeIf(row -> row.getUsername().equals(username));
    }

//    // Update the permission table when a sheet is selected
//    private void updatePermissionTableForSheet(String sheetName) {
//        // Clear current table data
//        permissionEntries.clear();
//        Map<String, String> params = new HashMap<>();
//        params.put("sheetName", sheetName);
//
//        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_PERMISSIONS_FOR_SHEET, params, new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Platform.runLater(() -> mainController.createErrorPopup("Failed to get permissions for sheet", "Error"));
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    String errorMessageAsJson = response.body().string();
//                    String error = Constants.GSON_INSTANCE.fromJson(errorMessageAsJson, String.class);
//                    Platform.runLater(() -> mainController.createErrorPopup(error, "Error"));
//                }
//                String permissionsAsJson = response.body().string();
//                Type permissionListType = new TypeToken<List<PermissionLine>>() {}.getType();
//                List<PermissionLine> permissions = Constants.GSON_INSTANCE.fromJson(permissionsAsJson, permissionListType);
//
//                permissions.forEach(permission -> {
//                    String username = permission.getUserName();
//                    String permissionStatus = permission.getPermissionStatus().toString();
//                    String isApproved = permission.isApprovedByOwner() ? "Yes" : "No";
//                    Platform.runLater(() -> addLine(username, permissionStatus, isApproved));
//                });
//            }
//        });
//    }


    private void updatePermissionTableForSheet(String sheetName) {
        Map<String, String> params = new HashMap<>();
        params.put("sheetName", sheetName);

        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_PERMISSIONS_FOR_SHEET, params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> mainController.createErrorPopup("Failed to get permissions for sheet", "Error"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorMessageAsJson = response.body().string();
                    String error = Constants.GSON_INSTANCE.fromJson(errorMessageAsJson, String.class);
                    Platform.runLater(() -> mainController.createErrorPopup(error, "Error"));
                    return;
                }

                String permissionsAsJson = response.body().string();
                Type permissionListType = new TypeToken<List<PermissionLine>>() {}.getType();
                List<PermissionLine> newPermissions = Constants.GSON_INSTANCE.fromJson(permissionsAsJson, permissionListType);

                // Check if the new permissions are different from the current ones
                if (!arePermissionsEqual(currentPermissions, newPermissions)) {
                    // If they are different, update the table
                    Platform.runLater(() -> {
                        // Clear the current table data
                        permissionEntries.clear();

                        // Add the new permission entries
                        newPermissions.forEach(permission -> {
                            String username = permission.getUserName();
                            String permissionStatus = permission.getPermissionStatus().toString();
                            String isApproved = permission.isApprovedByOwner() ? "Yes" : "No";
                            addLine(username, permissionStatus, isApproved);
                        });

                        // Update the current permissions stored
                        currentPermissions.clear();
                        currentPermissions.addAll(newPermissions);
                    });
                }
            }
        });
    }

    // Helper method to compare two lists of PermissionLine objects
    private boolean arePermissionsEqual(List<PermissionLine> oldPermissions, List<PermissionLine> newPermissions) {
        if (oldPermissions.size() != newPermissions.size()) {
            return false;
        }

        for (int i = 0; i < oldPermissions.size(); i++) {
            PermissionLine oldPerm = oldPermissions.get(i);
            PermissionLine newPerm = newPermissions.get(i);

            if (!oldPerm.getUserName().equals(newPerm.getUserName()) ||
                    !oldPerm.getPermissionStatus().equals(newPerm.getPermissionStatus()) ||
                    oldPerm.isApprovedByOwner() != newPerm.isApprovedByOwner()) {
                return false;
            }
        }

        return true;
    }


    @FXML
    private void onRequestPermissionButtonClicked(ActionEvent event) {
        // Logic to request permission will go here
    }

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

    public static class PermissionRow {
        private final StringProperty username;
        private final StringProperty permissionStatus;
        private final StringProperty approvedByOwner;

        public PermissionRow(String username, String permissionStatus, String approvedByOwner) {
            this.username = new SimpleStringProperty(username);
            this.permissionStatus = new SimpleStringProperty(permissionStatus);
            this.approvedByOwner = new SimpleStringProperty(approvedByOwner);
        }

        public StringProperty usernameProperty() {
            return username;
        }

        public StringProperty permissionStatusProperty() {
            return permissionStatus;
        }

        public StringProperty approvedByOwnerProperty() {
            return approvedByOwner;
        }

        public String getUsername() {
            return username.get();
        }
    }
}
