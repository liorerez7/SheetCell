package app;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SlideOutPopupExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a label for opening chat
        Label openChatLabel = new Label("▼ Open Chat");
        openChatLabel.setStyle("-fx-padding: 10px; -fx-font-size: 14px; -fx-text-fill: white; -fx-background-color: #4a4a4a; -fx-cursor: hand;");

        // Create the chat area overlay
        VBox chatPane = new VBox(10);
        chatPane.setStyle("-fx-background-color: rgba(232, 240, 246, 0.95); -fx-padding: 15px; -fx-border-color: #d3d3d3; -fx-border-width: 1px;");
        chatPane.setPrefSize(250, 150);  // Set chat overlay to small size
        chatPane.setMaxSize(250, 150);   // Constrain it to a small area
        chatPane.setVisible(false);      // Initially hidden

        // Chat area content
        TextArea chatMessagesArea = new TextArea();
        chatMessagesArea.setEditable(false);
        chatMessagesArea.setPrefHeight(80);  // Limit chat messages area height

        TextField messageField = new TextField();
        messageField.setPromptText("Type your message here...");

        Button sendButton = new Button("Send Message");
        sendButton.setOnAction(event -> {
            if (!messageField.getText().trim().isEmpty()) {
                chatMessagesArea.appendText("You: " + messageField.getText() + "\n");
                messageField.clear();
            }
        });

        Button closeButton = new Button("Close Chat");
        closeButton.setOnAction(event -> {
            ScaleTransition collapse = new ScaleTransition(Duration.millis(300), chatPane);
            collapse.setFromY(1);
            collapse.setToY(0);
            collapse.setOnFinished(e -> chatPane.setVisible(false));
            collapse.play();
        });

        // Add chat components to the chat pane
        chatPane.getChildren().addAll(chatMessagesArea, messageField, sendButton, closeButton);

        // Animation for expanding/collapsing the chat pane
        ScaleTransition expand = new ScaleTransition(Duration.millis(300), chatPane);
        expand.setFromY(0);
        expand.setToY(1);

        openChatLabel.setOnMouseClicked(event -> {
            if (chatPane.isVisible()) {
                ScaleTransition collapse = new ScaleTransition(Duration.millis(300), chatPane);
                collapse.setFromY(1);
                collapse.setToY(0);
                collapse.setOnFinished(e -> chatPane.setVisible(false));
                collapse.play();
            } else {
                chatPane.setVisible(true);
                expand.play();
            }
        });

        // Main content area of the application
        Label mainContentLabel = new Label("Main Content Here");
        mainContentLabel.setStyle("-fx-font-size: 16px; -fx-padding: 20px;");

        // Layout setup for main content
        VBox mainContent = new VBox(openChatLabel, mainContentLabel);
        mainContent.setStyle("-fx-background-color: #ffffff; -fx-padding: 20px;");

        // StackPane to layer chatPane over mainContent in a small area
        StackPane root = new StackPane(mainContent, chatPane);
        root.setStyle("-fx-background-color: #f0f0f0;");

        // Position the chat pane in the bottom right corner
        StackPane.setAlignment(chatPane, Pos.BOTTOM_RIGHT);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Chat Overlay Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
