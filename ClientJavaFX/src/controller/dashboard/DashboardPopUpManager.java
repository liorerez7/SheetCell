package controller.dashboard;

import dto.permissions.RequestPermission;
import dto.permissions.RequestStatus;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utilities.Constants;
import utilities.http.manager.HttpRequestManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DashboardPopUpManager {

    private DashboardController dashboardController;
    private VBox pendingRequestsBox;  // Declare pendingRequestsBox as an instance variable

    public DashboardPopUpManager(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void showSheetAndPermissionSelectionPopup(Set<String> sheetNames) {
        // Create the popup stage
        Stage popupStage = new Stage();
        popupStage.setTitle("Select Sheet and Permission");

        // Create ListView for sheets
        ListView<String> sheetListView = createSheetListView(sheetNames);

        // Create ChoiceBox for permissions
        ChoiceBox<String> permissionChoiceBox = createPermissionChoiceBox();

        // Create the Submit button
        Button submitButton = createSubmitButton(sheetListView, permissionChoiceBox, popupStage);
        submitButton.getStyleClass().add("popup-button");

        // Create the success message label (initially hidden)
        Label successMessage = new Label("Request sent successfully \u2714"); // Checkmark icon
        successMessage.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        successMessage.setVisible(false);  // Initially hidden

        // Create VBox layout for the popup
        VBox layout = new VBox(15);  // Increase the spacing between elements
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("popup-background");  // Apply the background style from CSS

        // Create and add header
        Label headerLabel = new Label("Select Sheet and Permission");
        headerLabel.getStyleClass().add("popup-header");

        // Add elements to layout
        layout.getChildren().addAll(headerLabel, new Label("Select a sheet:"), sheetListView,
                new Label("Select permission:"), permissionChoiceBox, submitButton, successMessage);

        // Add the layout inside a ScrollPane
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);  // Ensure content width is adjusted
        scrollPane.setFitToHeight(true);  // Adjust content height
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  // Enable vertical scrollbar as needed
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  // Enable horizontal scrollbar as needed

        // Set the minimum size for the ScrollPane
        scrollPane.setMinWidth(330);  // Adjust this to control when horizontal scrollbar appears
        scrollPane.setMinHeight(430); // Adjust this to control when vertical scrollbar appears

        // Set minimum size for the popup stage (optional)
        popupStage.setMinWidth(350);  // Prevent the stage from being resized too small
        popupStage.setMinHeight(450); // Prevent the stage from being resized too small

        // Create and set the scene with default size
        Scene scene = new Scene(scrollPane, 350, 450);  // Default size remains 350x450
        scene.getStylesheets().add(getClass().getResource("dashboard.css").toExternalForm());  // Load CSS

        popupStage.setScene(scene);
        popupStage.show();
    }

    private ListView<String> createSheetListView(Set<String> sheetNames) {
        ListView<String> sheetListView = new ListView<>();
        sheetListView.getItems().addAll(sheetNames);
        sheetListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        return sheetListView;
    }

    private ChoiceBox<String> createPermissionChoiceBox() {
        ChoiceBox<String> permissionChoiceBox = new ChoiceBox<>();
        permissionChoiceBox.getItems().addAll("Writer", "Reader");
        permissionChoiceBox.setPrefWidth(150);  // Set preferred width to make it smaller
        return permissionChoiceBox;
    }

    private Button createSubmitButton(ListView<String> sheetListView, ChoiceBox<String> permissionChoiceBox, Stage popupStage) {
        Button submitButton = new Button("Submit");
        submitButton.setDisable(true);  // Initially disable the submit button

        // Add listeners to enable the button when both selections are made
        ChangeListener<Object> listener = (obs, oldSelection, newSelection) -> checkSubmitButtonState(sheetListView, permissionChoiceBox, submitButton);
        sheetListView.getSelectionModel().selectedItemProperty().addListener(listener);
        permissionChoiceBox.getSelectionModel().selectedItemProperty().addListener(listener);

        // Handle button action
        submitButton.setOnAction(e -> handleSheetAndPermissionSubmit(sheetListView, permissionChoiceBox, popupStage, submitButton));

        return submitButton;
    }

    private void handleSheetAndPermissionSubmit(ListView<String> sheetListView, ChoiceBox<String> permissionChoiceBox, Stage popupStage, Button submitButton) {
        String selectedSheet = sheetListView.getSelectionModel().getSelectedItem();
        String selectedPermission = permissionChoiceBox.getSelectionModel().getSelectedItem();

        if (selectedSheet != null && selectedPermission != null) {
            Map<String, String> params = new HashMap<>();
            params.put("sheetName", selectedSheet);
            params.put("permission", selectedPermission);

            // Get the success message label (last child of the VBox layout)
            VBox layout = (VBox) submitButton.getParent();
            Label successMessage = (Label) layout.getChildren().get(layout.getChildren().size() - 1);

            // Asynchronously send the permission request
            HttpRequestManager.sendPostAsyncRequest(Constants.REQUEST_PERMISSION, params, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    dashboardController.handleHttpFailure(e, "Failed to request permission");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        dashboardController.handleHttpResponseFailure(response, "Failed to request permission");
                    }
                    dashboardController.forceRefreshPermissionTableForSheet(selectedSheet);

                    // Show success message after the request is successfully sent
                    successMessage.setVisible(true);

                    // Hide the success message after 2 seconds
                    PauseTransition delay = new PauseTransition(Duration.seconds(1));
                    delay.setOnFinished(event -> successMessage.setVisible(false));
                    delay.play();
                }
            });
        }
    }

    private void checkSubmitButtonState(ListView<String> sheetListView, ChoiceBox<String> permissionChoiceBox, Button submitButton) {
        String selectedSheet = sheetListView.getSelectionModel().getSelectedItem();
        String selectedPermission = permissionChoiceBox.getSelectionModel().getSelectedItem();
        submitButton.setDisable(selectedSheet == null || selectedPermission == null);  // Enable only if both selections are made
    }


    public Stage showManageAccessRequestsPopup(List<RequestPermission> myRequests) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Manage Access Requests");

        // Initialize the instance variable pendingRequestsBox
        pendingRequestsBox = new VBox(15);
        pendingRequestsBox.setPadding(new Insets(10));
        pendingRequestsBox.setAlignment(Pos.CENTER);

        // Create and add header for pending requests
        Label pendingHeaderLabel = new Label("Pending Requests");
        pendingHeaderLabel.getStyleClass().add("popup-header");
        pendingRequestsBox.getChildren().add(pendingHeaderLabel);

        // Add each pending request as an HBox row
        myRequests.stream()
                .filter(req -> (!req.getWasAnswered())) // Filter unanswered requests
                .forEach(req -> pendingRequestsBox.getChildren().add(createRequestRow(req, popupStage)));

        // Create ScrollPane for pending requests
        ScrollPane pendingScrollPane = new ScrollPane(pendingRequestsBox);
        pendingScrollPane.setFitToWidth(true);
        pendingScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Show vertical scrollbar as needed
        pendingScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);     // Disable horizontal scrollbar

        // Create VBox for answered requests
        VBox answeredRequestsBox = new VBox(15);
        answeredRequestsBox.setPadding(new Insets(10));
        answeredRequestsBox.setAlignment(Pos.CENTER);

        // Create and add header for answered requests
        Label answeredHeaderLabel = new Label("Answered Requests");
        answeredHeaderLabel.getStyleClass().add("popup-header");
        answeredRequestsBox.getChildren().add(answeredHeaderLabel);

        // Initially, the answered requests section will be empty
        Label emptyMessageLabel = new Label("No answered requests at this time.");
        emptyMessageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;"); // Styled as a placeholder
        answeredRequestsBox.getChildren().add(emptyMessageLabel);

        // Create ScrollPane for answered requests (empty for now)
        ScrollPane answeredScrollPane = new ScrollPane(answeredRequestsBox);
        answeredScrollPane.setFitToWidth(true);
        answeredScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Show vertical scrollbar as needed
        answeredScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);     // Disable horizontal scrollbar

        // Main layout for both pending and answered sections
        VBox layout = new VBox(20, pendingScrollPane, answeredScrollPane);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("popup-background"); // Use consistent background styling

        // Create and set the scene with a slightly bigger size
        Scene scene = new Scene(layout, 650, 500);  // Adjusted size to fit two sections
        scene.getStylesheets().add(getClass().getResource("dashboard.css").toExternalForm()); // Load CSS for styling

        popupStage.setScene(scene);
        popupStage.show();

        return popupStage;  // Return the popupStage to add the listener for closing event
    }


    private HBox createRequestRow(RequestPermission request, Stage popupStage) {
        HBox requestBox = new HBox(10); // Reduced spacing between elements
        requestBox.setPadding(new Insets(5)); // Reduced padding to make rows more compact
        requestBox.setAlignment(Pos.CENTER_LEFT); // Align content to the left
        requestBox.getStyleClass().add("popup-small-background"); // Apply background style if needed

        // Create Labels for each detail with consistent styling
        Label usernameLabel = new Label("User: " + request.getUserNameForRequest());
        Label sheetNameLabel = new Label("Sheet: " + request.getSheetNameForRequest());
        Label statusLabel = new Label("Status: " + request.getPermissionStatusForRequest());

        // Create Approve and Deny buttons with consistent styling
        Button approveButton = new Button("Approve");
        approveButton.getStyleClass().add("popup-small-button"); // Reuse button styles
        Button denyButton = new Button("Deny");
        denyButton.getStyleClass().add("popup-small-button"); // Reuse button styles

        // Create a Region to push the buttons to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Make the spacer grow to fill available space

        // Set button actions
        approveButton.setOnAction(e -> handlePermissionResponse(request, true, popupStage));
        denyButton.setOnAction(e -> handlePermissionResponse(request, false, popupStage));

        // Add elements to the HBox
        requestBox.getChildren().addAll(usernameLabel, sheetNameLabel, statusLabel, spacer, approveButton, denyButton);

        return requestBox;
    }

    private void handlePermissionResponse(RequestPermission request, boolean isApproved, Stage popupStage) {
        Map<String, String> params = new HashMap<>();

        params.put("sheetName", request.getSheetNameForRequest());
        params.put("userName", request.getUserNameForRequest());
        params.put("permission", request.getPermissionStatusForRequest().toString());

        if (isApproved) {
            params.put("requestStatus", RequestStatus.APPROVED.toString());
        } else {
            params.put("requestStatus", RequestStatus.REJECTED.toString());
        }

        try (Response responseForSendingResponseStatus = HttpRequestManager.sendPostSyncRequest(Constants.UPDATE_RESPONSE_PERMISSION, params)) {
            dashboardController.forceRefreshPermissionTableForSheet(request.getSheetNameForRequest());

            if (!responseForSendingResponseStatus.isSuccessful()) {
                System.out.println("Failed to send response status");
            } else {
                removeRequestRow(request);  // Remove the request row after the response
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeRequestRow(RequestPermission request) {
        // Remove the request row from pendingRequestsBox
        pendingRequestsBox.getChildren().removeIf(node -> {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                // Check if the row matches the request (username, sheet name, and permission)
                Label usernameLabel = (Label) row.getChildren().get(0); // Adjust index if needed
                Label sheetNameLabel = (Label) row.getChildren().get(1); // Adjust index if needed
                Label statusLabel = (Label) row.getChildren().get(2); // Adjust index if needed
                return usernameLabel.getText().contains(request.getUserNameForRequest()) &&
                        sheetNameLabel.getText().contains(request.getSheetNameForRequest()) &&
                        statusLabel.getText().contains(request.getPermissionStatusForRequest().toString());
            }
            return false;
        });
    }
}








