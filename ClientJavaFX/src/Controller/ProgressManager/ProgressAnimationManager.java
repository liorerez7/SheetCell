package Controller.ProgressManager;

import Utility.JavaFXUtility.ProgressManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ProgressAnimationManager {

    private final ProgressManager progressManager;
    private final StackPane progressPane;
    private Label welcomeLabel;
    private Button cancelButton;
    private Timeline timeline;
    private VBox layout;

    // Labels for the animation
    private Label[] labels;
    private HBox labelContainer; // Container for the labels

    public ProgressAnimationManager(ProgressManager progressManager) {
        this.progressManager = progressManager;
        this.progressPane = progressManager.getProgressPane();
        createLabels(); // Initialize labels
    }

    private void createLabels() {
        labels = new Label[5];
        String[] texts = {"Lior", "And", "Niv", "-", "Sheet Cell"};

        labelContainer = new HBox(10); // Space between labels
        labelContainer.setAlignment(Pos.CENTER); // Center the labels

        // Set initial positions and styles for the labels
        for (int i = 0; i < texts.length; i++) {
            labels[i] = new Label(texts[i]);
            labels[i].setStyle("-fx-font-size: 20px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
            labels[i].setOpacity(0); // Set initial opacity to 0

            // Set different initial positions
            double offset = (i - 2) * 100; // Adjust the multiplier for spacing
            labels[i].setTranslateX(offset);
            labelContainer.getChildren().add(labels[i]);
        }
    }

    public VBox createProgressAnimationLayout() {
        // Create the welcome message label
        welcomeLabel = new Label("Loading your sheetCell...");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        welcomeLabel.setAlignment(Pos.CENTER);

        // Create the animation timeline for welcomeLabel
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(welcomeLabel.opacityProperty(), 0)),
                new KeyFrame(new Duration(1000), new KeyValue(welcomeLabel.opacityProperty(), 1)),
                new KeyFrame(new Duration(2000), new KeyValue(welcomeLabel.opacityProperty(), 1)),
                new KeyFrame(new Duration(3000), new KeyValue(welcomeLabel.opacityProperty(), 0))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Create animation for the labels
        animateLabels();

        // Create the cancel button
        cancelButton = new Button("Cancel Animation");
        cancelButton.setStyle("-fx-font-size: 14px; -fx-background-color: #FF6F61; -fx-text-fill: white;");
        cancelButton.setOnAction(event -> cancelAnimation());

        // Create a VBox layout with the progress pane, welcome label, and cancel button
        layout = new VBox(10, progressPane, welcomeLabel, labelContainer, cancelButton);
        layout.setAlignment(Pos.CENTER);

        // Adjust the padding: reduce left padding (from 450 to 200, for example) to move content to the left
        layout.setPadding(new Insets(30, 80, 50, 10));  // Reduced left padding

        VBox.setMargin(progressPane, new Insets(0, 0, 10, 0));
        VBox.setMargin(welcomeLabel, new Insets(0, 0, 10, 0));

        return layout;
    }


    private void animateLabels() {
        Timeline labelAnimation = new Timeline();

        // KeyFrame for each label to move towards the center and fade in
        for (int i = 0; i < labels.length; i++) {
            // Adjust target positions for better alignment
            double targetX;
            switch (i) {
                case 0: // "Lior"
                    targetX = -15; // Closer position
                    break;
                case 1: // "And"
                    targetX = -8; // Closer position
                    break;
                case 2: // "Niv"
                    targetX = 0; // Center position
                    break;
                case 3: // "-"
                    targetX = 8; // Closer position
                    break;
                case 4: // "Sheet Cell"
                    targetX = 13; // Closer position
                    break;
                default:
                    targetX = 0; // Default case (not needed)
                    break;
            }

            // Add keyframes for the translation and opacity
            labelAnimation.getKeyFrames().addAll(
                    new KeyFrame(Duration.seconds(1.8), // Move towards final position over 1.8 seconds
                            new KeyValue(labels[i].translateXProperty(), targetX), // Move to final position
                            new KeyValue(labels[i].opacityProperty(), 1) // Fade in
                    )
            );
        }

        // Start the animation
        labelAnimation.play();
    }

    private void cancelAnimation() {
        // Stop the welcome label timeline
        timeline.stop();

        // Hide the welcome label and the cancel button
        welcomeLabel.setVisible(false);
        cancelButton.setVisible(false);

        // Hide the labels by setting their opacity to 0
        for (Label label : labels) {
            label.setOpacity(0); // Fade out
            label.setVisible(false); // Hide the labels
        }
    }

    public void updateProgress(double progress) {
        progressManager.updateProgress(progress);
    }
}


