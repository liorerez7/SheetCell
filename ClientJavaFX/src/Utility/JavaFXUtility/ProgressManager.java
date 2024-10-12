package Utility.JavaFXUtility;

import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class ProgressManager {
    private ProgressBar progressBar;
    private StackPane progressPane;

    public ProgressManager() {
        initializeProgressBar();
    }

    private void initializeProgressBar() {
        progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(200);
        progressBar.setMinWidth(200);
        progressBar.setPrefHeight(30);
        progressPane = new StackPane(progressBar);
        progressPane.setPadding(new Insets(10));

    }

    public StackPane getProgressPane() {
        return progressPane;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void updateProgress(double progress) {
        if (progressBar != null) {
            // Ensure the progress bar is not bound before setting the progress
            if (!progressBar.progressProperty().isBound()) {
                progressBar.setProgress(progress);
            }
        }
    }
}
