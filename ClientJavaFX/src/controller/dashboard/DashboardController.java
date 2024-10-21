package controller.dashboard;

import dto.components.DtoSheetInfoLine;
import dto.permissions.PermissionLine;
import dto.permissions.PermissionStatus;
import dto.permissions.RequestPermission;
import dto.permissions.RequestStatus;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import utilities.Constants;
import utilities.http.manager.HttpRequestManager;
import controller.main.MainController;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private Button loadSheetFileButton;
    @FXML private Label helloUserLabel; // No more usernameLabel, using helloUserLabel
    @FXML private TableView<SheetInfo> sheetsTable;  // Updated to SheetInfo
    @FXML private TableColumn<SheetInfo, String> ownerColumn;  // Updated to SheetInfo
    @FXML private TableColumn<SheetInfo, String> sheetNameColumn;  // Updated to SheetInfo
    @FXML private TableColumn<SheetInfo, String> sizeColumn;  // Updated to SheetInfo
    @FXML private TableColumn<SheetInfo, String> myPermissionStatusColumn;  // Updated to SheetInfo
    @FXML private TableView<PermissionRow> permissionsTable;
    @FXML private TableColumn<PermissionRow, String> usernameColumn;
    @FXML private TableColumn<PermissionRow, String> permissionStatusColumn;
    @FXML private TableColumn<PermissionRow, String> approvedByOwnerColumn;
    @FXML private Button viewSheetButton;
    @FXML private Button requestPermissionButton;
    @FXML private Button manageAccessRequestsButton;
    @FXML private Button logoutButton;
    @FXML private Button exitButton;

    private MainController mainController;
    private ObservableList<SheetInfo> fileEntries;  // Updated to SheetInfo
    private ObservableList<PermissionRow> permissionEntries;
    private Set<String> sheetNames = new HashSet<>();
    private Timer timer;
    private List<PermissionLine> currentPermissions = new ArrayList<>();
    private Set<DtoSheetInfoLine> currentSheetInfoLines = new HashSet<>();
    private String currentUserName;
    private DashboardPopUpManager popUpManager;
//    private int myPermissionResponses = 0;
    private final IntegerProperty myPermissionResponses = new SimpleIntegerProperty(0);



    @FXML
    public void initialize() {
        initializeTables();
        startSheetNamesRefresher();
        startResponsesRefresher();
        setupSheetSelectionListener();
        popUpManager = new DashboardPopUpManager(this);
        viewSheetButton.setDisable(true);

        // Bind manageAccessRequestsButton disable property
        BooleanBinding noPermissionResponses = myPermissionResponses.isEqualTo(0);
        manageAccessRequestsButton.disableProperty().bind(noPermissionResponses);
    }



    @FXML
    private void onViewSheetButtonClicked(ActionEvent event) {
        // Get the selected entry (formatted string) from the table
        SheetInfo selectedEntry = sheetsTable.getSelectionModel().getSelectedItem();  // Updated to SheetInfo

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

    @FXML
    private void onLoadSheetFileButtonClicked(ActionEvent event) {
        File selectedFile = openXMLFileChooser((Stage) loadSheetFileButton.getScene().getWindow());
        if (selectedFile != null) {
            mainController.initializeGridBasedOnXML(selectedFile, selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void onLogoutButtonClicked(ActionEvent event) {
        // Logic for logging out the user.
        // For example, navigating back to the login screen or clearing user session data.
        mainController.showLoginScreen();
    }

    @FXML
    private void onExitButtonClicked(ActionEvent event) {
        // Logic for logging out the user.
        // For example, navigating back to the login screen or clearing user session data.
        mainController.exitApplication();
    }

    public void setUserName(String userName) {
        currentUserName = userName;
        helloUserLabel.setText("Hello " + userName); // Display "Hello <username>"
    }

    private void initializeTables() {
        fileEntries = FXCollections.observableArrayList();
        permissionEntries = FXCollections.observableArrayList();
        setupTableColumns();
        sheetsTable.setItems(fileEntries);
        permissionsTable.setItems(permissionEntries);
    }

    private void setupPermissionColumns() {
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        permissionStatusColumn.setCellValueFactory(cellData -> cellData.getValue().permissionStatusProperty());
        approvedByOwnerColumn.setCellValueFactory(cellData -> cellData.getValue().approvedByOwnerProperty());
    }

    private void setupTableColumns() {
        setupSheetInfoColumns();
        setupPermissionColumns();
        sheetsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        permissionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupSheetInfoColumns() {
        ownerColumn.setCellValueFactory(cellData -> cellData.getValue().ownerNameProperty());
        sheetNameColumn.setCellValueFactory(cellData -> cellData.getValue().sheetNameProperty());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        myPermissionStatusColumn.setCellValueFactory(cellData -> cellData.getValue().permissionStatusProperty());
    }

    private void setupSheetSelectionListener() {
        sheetsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updatePermissionTableForSheet(newSelection.getSheetName());
            }
        });
    }

    public void startSheetNamesRefresher() {
        SheetNamesRefresher refresher = new SheetNamesRefresher(this::addAllSheetInfoLines, this::displayError);
        timer = new Timer();
        timer.schedule(refresher, 0, 2000);
    }

    private void startResponsesRefresher() {
        ResponsesRefresher refresher = new ResponsesRefresher(this::setMyPermissionResponses);
        timer = new Timer();
        timer.schedule(refresher, 0, 2000);
    }

    private void setMyPermissionResponses(Integer responsesCount) {
        // Update the myPermissionResponses property in the UI thread
        Platform.runLater(() -> myPermissionResponses.set(responsesCount));
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

    private File openXMLFileChooser(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(stage);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private PermissionStatus fetchMyPermissionStatus(String sheetName) {
        Map<String, String> params = Collections.singletonMap("sheetName", sheetName);

        try (Response response = HttpRequestManager.sendGetSyncRequest(Constants.GET_MY_PERMISSION_FOR_SHEET, params)) {
            String permissionAsString = response.body().string();
            return PermissionStatus.fromString(permissionAsString);
        } catch (IOException e) {
            mainController.createErrorPopup("Failed to get permission for sheet", "Error");
            return null;
        }
    }

    public void addAllSheetInfoLines(Set<DtoSheetInfoLine> newSheetInfoLines) {
        Platform.runLater(() -> updateSheetTable(newSheetInfoLines));
    }

    private void updateSheetTable(Set<DtoSheetInfoLine> newSheetInfoLines) {
        if (!newSheetInfoLines.equals(currentSheetInfoLines)) {
            fileEntries.clear();
            newSheetInfoLines.forEach(this::addSheetInfoToTable);
            currentSheetInfoLines.clear();
            currentSheetInfoLines.addAll(newSheetInfoLines);
            viewSheetButton.setDisable(true);
        }
    }

    private void addSheetInfoToTable(DtoSheetInfoLine sheetInfoLine) {
        fileEntries.add(new SheetInfo(sheetInfoLine.getOwnerName(), sheetInfoLine.getSheetName(), sheetInfoLine.getSheetSize(), sheetInfoLine.getMyPermission()));
    }

    public void addLine(String username, String permissionStatus, String requestStatus) {
        PermissionRow newRow = new PermissionRow(username, permissionStatus, requestStatus);
        permissionEntries.add(newRow);
    }

    public void removeLine(String username) {
        permissionEntries.removeIf(row -> row.getUsername().equals(username));
    }

    private void updatePermissionTableForSheet(String sheetName) {
        Map<String, String> params = Collections.singletonMap("sheetName", sheetName);
        HttpRequestManager.sendGetAsyncRequest(Constants.GET_PERMISSIONS_LINES_FOR_SHEET, params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handlePermissionUpdateFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                handlePermissionUpdateResponse(sheetName, response);
            }
        });
    }

    private void handlePermissionUpdateFailure(IOException e) {
        Platform.runLater(() -> mainController.createErrorPopup("Failed to get permissions for sheet", "Error"));
    }

    private void handlePermissionUpdateResponse(String sheetName, Response response) throws IOException {
        if (!response.isSuccessful()) {
            handlePermissionUpdateErrorResponse(response);
            return;
        }

        List<PermissionLine> newPermissions = parsePermissionsResponse(response);
        updatePermissionsTable(sheetName, newPermissions);
    }

    private void handlePermissionUpdateErrorResponse(Response response) throws IOException {
        String errorMessage = Constants.GSON_INSTANCE.fromJson(response.body().string(), String.class);
        Platform.runLater(() -> mainController.createErrorPopup(errorMessage, "Error"));
    }

    private List<PermissionLine> parsePermissionsResponse(Response response) throws IOException {
        String permissionsAsJson = response.body().string();
        Type permissionListType = new TypeToken<List<PermissionLine>>() {}.getType();
        return Constants.GSON_INSTANCE.fromJson(permissionsAsJson, permissionListType);
    }

    private void updatePermissionsTable(String sheetName, List<PermissionLine> newPermissions) {
        PermissionStatus status = fetchMyPermissionStatus(sheetName);

        Platform.runLater(() -> viewSheetButton.setDisable(status == PermissionStatus.NONE));

        if (!arePermissionsEqual(currentPermissions, newPermissions)) {
            refreshPermissionTable(newPermissions);
            currentPermissions = newPermissions;
        }
    }

    private void refreshPermissionTable(List<PermissionLine> newPermissions) {
        Platform.runLater(() -> {
            permissionEntries.clear();
            newPermissions.forEach(permission -> {
                String username = permission.getUserName();
                String permissionStatus = permission.getPermissionStatus().toString();
                String requestStatus = permission.getRequestStatus().toString();
                addLine(username, permissionStatus, requestStatus);
            });
        });
    }

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

    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onRequestPermissionButtonClicked(ActionEvent event) {
        fetchSheetNamesAndShowPopup();
    }

    private void fetchSheetNamesAndShowPopup() {
        handleAsyncGetRequest(Constants.GET_ALL_SHEET_NAMES, new HashMap<>(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handleHttpFailure(e, "Failed to retrieve sheet names");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String sheetInfosAsJson = response.body().string();
                    Type permissionListType = new TypeToken<Set<DtoSheetInfoLine>>() {}.getType();
                    Set<DtoSheetInfoLine> sheetInfos = Constants.GSON_INSTANCE.fromJson(sheetInfosAsJson, permissionListType);

                    Set<String> sheetNamesThatAreNotTheOwner = sheetInfos.stream()
                            .filter(sheetInfo -> !sheetInfo.getOwnerName().equals(currentUserName))
                            .map(DtoSheetInfoLine::getSheetName)
                            .collect(Collectors.toSet());

                    //Platform.runLater(() -> showSheetAndPermissionSelectionPopup(sheetNamesThatAreNotTheOwner));
                    Platform.runLater(() -> popUpManager.showSheetAndPermissionSelectionPopup(sheetNamesThatAreNotTheOwner)); // Delegate the popup logic to DashboardPopUpManager

                } else {
                    handleHttpResponseFailure(response, "Failed to retrieve sheet names");
                }
            }
        });
    }

    private void handleSheetAndPermissionSubmit(ListView<String> sheetListView, ChoiceBox<String> permissionChoiceBox, Stage popupStage) {
        String selectedSheet = sheetListView.getSelectionModel().getSelectedItem();
        String selectedPermission = permissionChoiceBox.getSelectionModel().getSelectedItem();

        if (selectedSheet != null && selectedPermission != null) {
            Map<String, String> params = new HashMap<>();
            params.put("sheetName", selectedSheet);
            params.put("permission", selectedPermission);

            HttpRequestManager.sendPostAsyncRequest(Constants.REQUEST_PERMISSION, params, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    handleHttpFailure(e, "Failed to request permission");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        handleHttpResponseFailure(response, "Failed to request permission");
                    }
                    forceRefreshPermissionTableForSheet(selectedSheet);
                }
            });

            popupStage.close();
        }
    }

    private void handleAsyncGetRequest(String url, Map<String, String> params, Callback callback) {
        HttpRequestManager.sendGetAsyncRequest(url, params, callback);
    }

    protected void handleHttpFailure(IOException e, String errorMessage) {
        Platform.runLater(() -> showErrorPopup(errorMessage + ": " + e.getMessage()));
    }

    protected void handleHttpResponseFailure(Response response, String defaultErrorMessage) throws IOException {
        String errorMessage = Constants.GSON_INSTANCE.fromJson(response.body().string(), String.class);
        Platform.runLater(() -> showErrorPopup(defaultErrorMessage + ": " + errorMessage));
    }

    @FXML
    private void onManageAccessRequestsButtonClicked(ActionEvent event) {
        CompletableFuture.runAsync(() -> {

            try (Response myRequestsResponse = HttpRequestManager.sendGetSyncRequest(Constants.MY_RESPONSE_PERMISSION, new HashMap<>())) {

                String myRequestsAsJson = myRequestsResponse.body().string();
                Type myRequestsListType = new TypeToken<List<RequestPermission>>() {}.getType();
                List<RequestPermission> myRequests = Constants.GSON_INSTANCE.fromJson(myRequestsAsJson, myRequestsListType);

                Platform.runLater(() -> {
                    popUpManager.showManageAccessRequestsPopup(myRequests);  // Use popup manager to show the popup
                });

            } catch (IOException e) {
                Platform.runLater(() -> showErrorPopup("Failed to retrieve requests."));
            }
        });
    }

    private void showManageAccessRequestsPopup(List<RequestPermission> myRequests) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Request Permissions");

        VBox requestList = new VBox(10);
        requestList.setPadding(new Insets(10));
        myRequests.stream().filter(req -> !req.getWasAnswered())
                .forEach(req -> requestList.getChildren().add(createRequestRow(req, popupStage)));

        ScrollPane scrollPane = new ScrollPane(requestList);
        scrollPane.setFitToWidth(true);

        VBox layout = new VBox(10, scrollPane);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 500);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private HBox createRequestRow(RequestPermission request, Stage popupStage) {
        HBox requestBox = new HBox(10);

        Label usernameLabel = new Label("User: " + request.getUserNameForRequest());
        Label sheetNameLabel = new Label("Sheet: " + request.getSheetNameForRequest());
        Label statusLabel = new Label("Status: " + request.getPermissionStatusForRequest());

        Button approveButton = new Button("Approve");
        Button denyButton = new Button("Deny");

        approveButton.setOnAction(e -> handlePermissionResponse(request, true, popupStage));
        denyButton.setOnAction(e -> handlePermissionResponse(request, false, popupStage));

        requestBox.getChildren().addAll(usernameLabel, sheetNameLabel, statusLabel, approveButton, denyButton);
        return requestBox;
    }

    private void handlePermissionResponse(RequestPermission request, boolean isApproved, Stage popupStage) {

        Map<String,String> params = new HashMap<>();

        params.put("sheetName", request.getSheetNameForRequest());
        params.put("userName", request.getUserNameForRequest());
        params.put("permission", request.getPermissionStatusForRequest().toString());

        if(isApproved){
            params.put("requestStatus", RequestStatus.APPROVED.toString());
        } else {
            params.put("requestStatus", RequestStatus.REJECTED.toString());
        }

        try (Response responseForSendingResponseStatus = HttpRequestManager.sendPostSyncRequest(Constants.UPDATE_RESPONSE_PERMISSION, params)) {

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

    protected void forceRefreshPermissionTableForSheet(String sheetName) {
        Map<String, String> params = new HashMap<>();
        params.put("sheetName", sheetName);

        HttpRequestManager.sendGetAsyncRequest(Constants.GET_PERMISSIONS_LINES_FOR_SHEET, params, new Callback() {
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
        private final StringProperty requestStatus;

        public PermissionRow(String username, String permissionStatus, String approvedByOwner) {
            this.username = new SimpleStringProperty(username);
            this.permissionStatus = new SimpleStringProperty(permissionStatus);
            this.requestStatus = new SimpleStringProperty(approvedByOwner);
        }

        public StringProperty usernameProperty() {
            return username;
        }

        public StringProperty permissionStatusProperty() {
            return permissionStatus;
        }

        public StringProperty approvedByOwnerProperty() {
            return requestStatus;
        }

        public String getUsername() {
            return username.get();
        }

        public String getPermissionStatus() {
            return permissionStatus.get();
        }

        public String getApprovedByOwner() {
            return requestStatus.get();
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


