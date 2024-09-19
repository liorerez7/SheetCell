package Controller.ProgressManager;

import Controller.Utility.ProgressManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    public ProgressAnimationManager(ProgressManager progressManager) {
        this.progressManager = progressManager;
        this.progressPane = progressManager.getProgressPane();
    }

    public VBox createProgressAnimationLayout() {
        // Create the welcome message label
        welcomeLabel = new Label("Loading your sheetCell...");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        welcomeLabel.setAlignment(Pos.CENTER);  // Center the label

        // Create the animation timeline
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(welcomeLabel.opacityProperty(), 0)),
                new KeyFrame(new Duration(1000), new KeyValue(welcomeLabel.opacityProperty(), 1)),  // Fade in
                new KeyFrame(new Duration(2000), new KeyValue(welcomeLabel.opacityProperty(), 1)),  // Stay fully visible
                new KeyFrame(new Duration(3000), new KeyValue(welcomeLabel.opacityProperty(), 0))   // Fade out
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Create the cancel button
        cancelButton = new Button("Cancel Animation");
        cancelButton.setStyle("-fx-font-size: 14px; -fx-background-color: #FF6F61; -fx-text-fill: white;");
        cancelButton.setOnAction(event -> cancelAnimation());

        // Create a VBox layout with the progress pane, welcome label, and cancel button
        layout = new VBox(10, progressPane, welcomeLabel, cancelButton);
        layout.setAlignment(Pos.CENTER);  // Center the elements vertically and horizontally

        // Add padding to push everything to the right and down
        layout.setPadding(new Insets(100, 40, 30, 450));  // Top, Right, Bottom, Left

        // Adjust margins to align elements correctly
        VBox.setMargin(progressPane, new Insets(0, 0, 10, 0));  // Space between the progress bar and welcome label
        VBox.setMargin(welcomeLabel, new Insets(0, 0, 10, 0));  // Space between the welcome label and the cancel button

        return layout;
    }



    public void cancelAnimation() {
        timeline.stop();
        welcomeLabel.setVisible(false);
        cancelButton.setVisible(false);
    }

    public void updateProgress(double progress) {
        progressManager.updateProgress(progress);
    }
}
