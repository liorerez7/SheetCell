package Controller.DashboardScreen;

import DtoComponents.DtoSheetInfoLine;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import loginPage.users.PermissionLine;
import Utility.PermissionStatus;
import loginPage.users.RequestPermission;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DashboardController {

    @FXML
    private Button loadSheetFileButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private TableView<SheetInfo> availableSheetsTable;  // Updated to SheetInfo

    @FXML
    private TableColumn<SheetInfo, String> ownerColumn;  // Updated to SheetInfo
    @FXML
    private TableColumn<SheetInfo, String> sheetNameColumn;  // Updated to SheetInfo
    @FXML
    private TableColumn<SheetInfo, String> sizeColumn;  // Updated to SheetInfo
    @FXML
    private TableColumn<SheetInfo, String> myPermissionStatusColumn;  // Updated to SheetInfo

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
    private ObservableList<SheetInfo> fileEntries;  // Updated to SheetInfo

    private ObservableList<PermissionRow> permissionEntries;

    private Set<String> sheetNames = new HashSet<>();
    private Timer timer;
    private List<PermissionLine> currentPermissions = new ArrayList<>();
    private Set<DtoSheetInfoLine> currentSheetInfoLines = new HashSet<>();
    private String currentUserName;

    public void setUserName(String userName) {
        currentUserName = userName;
    }

    @FXML
    public void initialize() {
        // Initialize the ObservableList for file entries
        fileEntries = FXCollections.observableArrayList();

        // Initialize the ObservableList for permission entries
        permissionEntries = FXCollections.observableArrayList(); // Initialize permissionEntries here


        // Set up the columns for the available sheets table
        ownerColumn.setCellValueFactory(cellData -> cellData.getValue().ownerNameProperty());
        sheetNameColumn.setCellValueFactory(cellData -> cellData.getValue().sheetNameProperty());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        myPermissionStatusColumn.setCellValueFactory(cellData -> cellData.getValue().permissionStatusProperty());


        // Set up the columns for the permissions table (new)
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        permissionStatusColumn.setCellValueFactory(cellData -> cellData.getValue().permissionStatusProperty());
        approvedByOwnerColumn.setCellValueFactory(cellData -> cellData.getValue().approvedByOwnerProperty());

        // Bind the ObservableLists to the TableViews
        availableSheetsTable.setItems(fileEntries);
        permissionsTable.setItems(permissionEntries); // Bind permissionEntries to the permissions table

        availableSheetsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        permissionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Disable the "View Sheet" button if no row is selected
        viewSheetButton.setDisable(true);

        // Add a listener to enable the button when a row is selected
        availableSheetsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String sheetName = newSelection.getSheetName();
                updatePermissionTableForSheet(sheetName);
            }
        });

        // Start the refresher
        startSheetNamesRefresher();
    }

    public void addFilePathToTable(String ownerName, String sheetName, String size, String permissionStatus) {
        fileEntries.add(new SheetInfo(ownerName, sheetName, size, permissionStatus));
    }

    public void startSheetNamesRefresher() {
        SheetNamesRefresher refresher = new SheetNamesRefresher(
                this::addAllSheetInfoLines,
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
        SheetInfo selectedEntry = availableSheetsTable.getSelectionModel().getSelectedItem();  // Updated to SheetInfo

        if (selectedEntry != null) {
            String sheetName = selectedEntry.getSheetName();
            PermissionStatus status = fetchMyPermissionStatus(sheetName);

            if (status != null) {
                switch (status) {
                    case OWNER:
                    case WRITER:
                    case READER:
                        mainController.updateCurrentGridSheet(sheetName, status);
                        mainController.showMainAppScreen();
                        break;
                    case NONE:
                        mainController.createErrorPopup("You do not have permission to view this sheet", "Error");
                        break;
                }
            } else {
                mainController.createErrorPopup("Failed to retrieve permission status", "Error");
            }
        }
    }

    private PermissionStatus fetchMyPermissionStatus(String sheetName) {
        Map<String,String> params = new HashMap<>();
        params.put("sheetName", sheetName);

        try (Response response = HttpRequestManager.sendGetRequestSync(Constants.GET_MY_PERMISSION_FOR_SHEET, params)) {
            String permissionStatus = response.body().string();

            switch (permissionStatus) {
                case "OWNER":
                    return PermissionStatus.OWNER;
                case "WRITER":
                    return PermissionStatus.WRITER;
                case "READER":
                    return PermissionStatus.READER;
                case "NONE":
                    return PermissionStatus.NONE;
                default:
                    // Handle unexpected permission status
                    return PermissionStatus.NONE;  // Or some default value
            }
        } catch (IOException e) {
            mainController.createErrorPopup("Failed to get permission for sheet", "Error");
            return PermissionStatus.NONE;  // Returning default value on error
        }
    }

    public void addAllSheetInfoLines(Set<DtoSheetInfoLine> newSheetInfoLines) {
        Platform.runLater(() -> {
            // Check if the new sheet info lines are different from the current ones
            if (!newSheetInfoLines.equals(currentSheetInfoLines)) {
                // Clear the current table data
                fileEntries.clear();
                this.sheetNames.clear();

                // Add the new sheet info lines
                newSheetInfoLines.forEach(sheetInfoLine -> addFilePathToTable(
                        sheetInfoLine.getOwnerName(),
                        sheetInfoLine.getSheetName(),
                        sheetInfoLine.getSheetSize(),
                        sheetInfoLine.getMyPermission())
                );

                // Disable the view button since the new sheet info lines are added
                viewSheetButton.setDisable(true);

                // Update the current sheet info lines
                currentSheetInfoLines.clear();
                currentSheetInfoLines.addAll(newSheetInfoLines);
            }
        });
    }

    // Add and remove lines from the permissions table
    public void addLine(String username, String permissionStatus, String isApproved) {
        PermissionRow newRow = new PermissionRow(username, permissionStatus, isApproved);
        permissionEntries.add(newRow);
    }

    public void removeLine(String username) {
        permissionEntries.removeIf(row -> row.getUsername().equals(username));
    }

    private void updatePermissionTableForSheet(String sheetName) {
        Map<String, String> params = new HashMap<>();
        params.put("sheetName", sheetName);

        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_PERMISSIONS_LINES_FOR_SHEET, params, new Callback() {
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

                PermissionStatus status = fetchMyPermissionStatus(sheetName);
                viewSheetButton.setDisable(status == PermissionStatus.NONE);



                // Check if the new permissions are different from the current ones
                if (!arePermissionsEqual(currentPermissions, newPermissions)) {
                    // If they are different, refresh the table
                    refreshPermissionTable(newPermissions);
                }
            }
        });
    }

    private void refreshPermissionTable(List<PermissionLine> newPermissions) {
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
//            currentPermissions.clear();
//            currentPermissions.addAll(newPermissions);
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
        fetchSheetNamesAndShowPopup();
    }

    private void fetchSheetNamesAndShowPopup() {
        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_ALL_SHEET_NAMES, new HashMap<>(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    // Handle failure by showing a popup or logging
                    showErrorPopup("Failed to retrieve sheet names.");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String sheetInfosAsJson = response.body().string();

                        Type permissionListType = new TypeToken<Set<DtoSheetInfoLine>>() {}.getType();
                        Set<DtoSheetInfoLine> sheetInfos = Constants.GSON_INSTANCE.fromJson(sheetInfosAsJson, permissionListType);

                        Set<String> sheetNamesThatImNotTheOwner = new HashSet<>();
                        sheetInfos.forEach(sheetInfo -> {
                            if (!sheetInfo.getOwnerName().equals(username))
                            {
                                sheetNamesThatImNotTheOwner.add(sheetInfo.getSheetName());
                            }
                        });

                        // Show the popup with sheet names on the JavaFX Application thread
                        Platform.runLater(() -> showSheetAndPermissionSelectionPopup(sheetNamesThatImNotTheOwner));
                    } else {
                        Platform.runLater(() -> showErrorPopup("Failed to retrieve sheet names."));
                    }
                } finally {
                    response.close();
                }
            }
        });
    }

    private void showSheetAndPermissionSelectionPopup(Set<String> sheetNames) {
        // Create a new stage for the popup window
        Stage popupStage = new Stage();
        popupStage.setTitle("Select Sheet and Permission");

        // Create a ListView for selecting the sheet
        ListView<String> sheetListView = new ListView<>();
        sheetListView.getItems().addAll(sheetNames);
        sheetListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Create a ChoiceBox for selecting the permission (Writer/Reader)
        ChoiceBox<String> permissionChoiceBox = new ChoiceBox<>();
        permissionChoiceBox.getItems().addAll("Writer", "Reader");

        // Initially, disable the submit button until a sheet and permission are selected
        Button submitButton = new Button("Submit");
        submitButton.setDisable(true);

        // Add listeners to enable the submit button once both a sheet and permission are selected
        sheetListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            checkSubmitButtonState(sheetListView, permissionChoiceBox, submitButton);
        });
        permissionChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            checkSubmitButtonState(sheetListView, permissionChoiceBox, submitButton);
        });

        // Handle submission of the selected sheet and permission
        submitButton.setOnAction(e -> {
            String selectedSheet = sheetListView.getSelectionModel().getSelectedItem();
            String selectedPermission = permissionChoiceBox.getSelectionModel().getSelectedItem();
            if (selectedSheet != null && selectedPermission != null) {
                // Handle the selected sheet and permission (e.g., send to the server)
                System.out.println("Selected sheet: " + selectedSheet + ", Permission: " + selectedPermission);

                Map<String,String> params = new HashMap<>();
                params.put("sheetName", selectedSheet);
                params.put("permission", selectedPermission);

                HttpRequestManager.sendPostRequestASyncWithCallBack(Constants.REQUEST_PERMISSION, params, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Platform.runLater(() -> showErrorPopup("Failed to request permission."));
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            String errorMessageAsJson = response.body().string();
                            String error = Constants.GSON_INSTANCE.fromJson(errorMessageAsJson, String.class);
                            Platform.runLater(() -> showErrorPopup(error));
                        }
                    }
                });

                popupStage.close();
            }
        });

        // Layout: VBox to hold the ListView, ChoiceBox, and Button
        VBox layout = new VBox(10);
        layout.getChildren().addAll(new Label("Select a sheet:"), sheetListView,
                new Label("Select permission:"), permissionChoiceBox, submitButton);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        // Add layout to a Scene and show the stage
        Scene scene = new Scene(layout, 300, 400);
        popupStage.setScene(scene);
        popupStage.show();
    }

    // Helper method to check if the submit button should be enabled
    private void checkSubmitButtonState(ListView<String> sheetListView, ChoiceBox<String> permissionChoiceBox, Button submitButton) {
        String selectedSheet = sheetListView.getSelectionModel().getSelectedItem();
        String selectedPermission = permissionChoiceBox.getSelectionModel().getSelectedItem();
        submitButton.setDisable(selectedSheet == null || selectedPermission == null);
    }

    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onAskDenyPermissionButtonClicked(ActionEvent event) {
        CompletableFuture.runAsync(() -> {

            try (Response myRequestsResponse = HttpRequestManager.sendGetRequestSync(Constants.MY_RESPONSE_PERMISSION, new HashMap<>())) {

                String myRequestsAsJson = myRequestsResponse.body().string();
                Type myRequestsListType = new TypeToken<List<RequestPermission>>() {}.getType();
                List<RequestPermission> myRequests = Constants.GSON_INSTANCE.fromJson(myRequestsAsJson, myRequestsListType);

                Platform.runLater(() -> {
                    showAskDenyPopup(myRequests);
                });

            } catch (IOException e) {
                Platform.runLater(() -> showErrorPopup("Failed to retrieve requests."));
            }
        });
    }

    private void showAskDenyPopup(List<RequestPermission> myRequests) {
        // Create a new stage for the popup window
        Stage popupStage = new Stage();
        popupStage.setTitle("Request Permissions");

        // Create a VBox to hold all requests
        VBox requestList = new VBox(10);
        requestList.setPadding(new Insets(10));

        // Iterate through the requests and create UI components for each one
        for (RequestPermission request : myRequests) {

            if(!request.getWasAnswered()) {


                HBox requestBox = new HBox(10);

                // Create labels for the request details
                Label usernameLabel = new Label("User: " + request.getUserNameForRequest());
                Label sheetNameLabel = new Label("Sheet: " + request.getSheetNameForRequest());
                Label statusLabel = new Label("Status: " + request.getPermissionStatusForRequest());

                // Create Approve and Deny buttons
                Button approveButton = new Button("Approve");
                Button denyButton = new Button("Deny");

                // Add action handlers for Approve and Deny buttons
                approveButton.setOnAction(e -> handlePermissionResponse(request, true, popupStage));
                denyButton.setOnAction(e -> handlePermissionResponse(request, false, popupStage));

                // Add all elements to the request box
                requestBox.getChildren().addAll(usernameLabel, sheetNameLabel, statusLabel, approveButton, denyButton);

                // Add the request box to the list of requests
                requestList.getChildren().add(requestBox);
            }
        }

        // Add the requestList to a ScrollPane to handle long lists
        ScrollPane scrollPane = new ScrollPane(requestList);
        scrollPane.setFitToWidth(true);

        // Create a VBox to hold the scrollPane
        VBox layout = new VBox(10, scrollPane);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        // Add layout to a Scene and show the stage
        Scene scene = new Scene(layout, 400, 500);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void handlePermissionResponse(RequestPermission request, boolean isApproved, Stage popupStage) {

        String action = isApproved ? "Approved" : "Denied";
        Map<String,String> params = new HashMap<>();

        params.put("sheetName", request.getSheetNameForRequest());
        params.put("userName", request.getUserNameForRequest());
        params.put("permission", request.getPermissionStatusForRequest().toString());
        params.put("isApproved", String.valueOf(isApproved));

        try (Response responseForSendingResponseStatus = HttpRequestManager.sendPostRequestSync(Constants.UPDATE_RESPONSE_PERMISSION, params)) {

            // Force refresh the permission table regardless of equality check
            forceRefreshPermissionTableForSheet(request.getSheetNameForRequest());

            if (!responseForSendingResponseStatus.isSuccessful()) {
                System.out.println("Failed to send response status");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        popupStage.close();
    }

    private void forceRefreshPermissionTableForSheet(String sheetName) {
        Map<String, String> params = new HashMap<>();
        params.put("sheetName", sheetName);

        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_PERMISSIONS_LINES_FOR_SHEET, params, new Callback() {
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

                // Always refresh the table
                refreshPermissionTable(newPermissions);
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

        public String getPermissionStatus() {
            return permissionStatus.get();
        }

        public String getApprovedByOwner() {
            return approvedByOwner.get();
        }
    }

    public static class SheetInfo {

        private final StringProperty ownerName;
        private final StringProperty sheetName;
        private final StringProperty size;
        private final StringProperty permissionStatus;

        public SheetInfo(String ownerName, String sheetName, String size, String permissionStatus) {
            this.ownerName = new SimpleStringProperty(ownerName);
            this.sheetName = new SimpleStringProperty(sheetName);
            this.size = new SimpleStringProperty(size);
            this.permissionStatus = new SimpleStringProperty(permissionStatus);
        }

        public StringProperty ownerNameProperty() {
            return ownerName;
        }

        public StringProperty sheetNameProperty() {
            return sheetName;
        }

        public StringProperty sizeProperty() {
            return size;
        }

        public StringProperty permissionStatusProperty() {
            return permissionStatus;
        }

        public String getMyPermission() {
            return permissionStatus.get();
        }

        public String getOwnerName() {
            return ownerName.get();
        }

        public String getSheetName() {
            return sheetName.get();
        }

        public String getSheetSize() {
            return size.get();
        }
    }
}