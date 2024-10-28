package controller.popup.versions;

import controller.grid.GridController;
import dto.components.DtoSheetCell;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Map;

public class VersionsPopupHandler {

    private final Map<Integer, DtoSheetCell> allDtoSheetVersions;
    private final int lastVersion;
    private final GridController gridScrollerController;
    private Stage popupStage;

    public VersionsPopupHandler(Map<Integer, DtoSheetCell> allDtoSheetVersions, int lastVersion, GridController gridScrollerController) {
        this.allDtoSheetVersions = allDtoSheetVersions;
        this.lastVersion = lastVersion;
        this.gridScrollerController = gridScrollerController;
    }

    public void show() {
        initializePopupStage();
        Label versionLabel = createVersionLabel();
        GridPane popupGrid = createPopupGrid();
        ScrollPane gridScrollPane = wrapGridInScrollPane(popupGrid);
        initializeGridWithLatestVersion(popupGrid);

        Slider valueSlider = createSlider(versionLabel, popupGrid);
        VBox contentBox = createContentBox(versionLabel, gridScrollPane, valueSlider);
        setupScene(contentBox);

        popupStage.showAndWait();
    }

    private void initializePopupStage() {
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Versions");
        popupStage.setWidth(1200);
        popupStage.setHeight(700);
    }

    private Label createVersionLabel() {
        Label versionLabel = new Label("Version Number " + lastVersion);
        versionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        return versionLabel;
    }

    private GridPane createPopupGrid() {
        GridPane popupGrid = new GridPane();
        popupGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");
        return popupGrid;
    }

    private ScrollPane wrapGridInScrollPane(GridPane popupGrid) {
        ScrollPane gridScrollPane = new ScrollPane(popupGrid);
        gridScrollPane.setFitToWidth(true);
        gridScrollPane.setFitToHeight(true);
        return gridScrollPane;
    }

    private void initializeGridWithLatestVersion(GridPane popupGrid) {
        DtoSheetCell initialVersion = allDtoSheetVersions.get(lastVersion);
        gridScrollerController.initializeVersionPopupGrid(popupGrid, initialVersion);
    }

    private Slider createSlider(Label versionLabel, GridPane popupGrid) {
        Slider valueSlider = new Slider(1, lastVersion, lastVersion);
        valueSlider.setBlockIncrement(1);
        valueSlider.setMajorTickUnit(1);
        valueSlider.setMinorTickCount(0);
        valueSlider.setShowTickMarks(true);
        valueSlider.setShowTickLabels(true);
        valueSlider.setStyle("-fx-control-inner-background: #e8f0f6; -fx-tick-mark-fill: #4a4a4a; -fx-tick-label-fill: #4a4a4a; -fx-border-color: #4a4a4a; -fx-border-radius: 5px;");

        setupSliderListener(valueSlider, versionLabel, popupGrid);
        return valueSlider;
    }

    private void setupSliderListener(Slider valueSlider, Label versionLabel, GridPane popupGrid) {
        final int[] lastVersionNumber = {lastVersion};
        final long[] lastUpdateTime = {System.currentTimeMillis()};

        valueSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int versionNumber = newVal.intValue();
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime[0] > 200 || versionNumber != lastVersionNumber[0]) {
                lastVersionNumber[0] = versionNumber;
                lastUpdateTime[0] = currentTime;
                versionLabel.setText("Version Number " + versionNumber);

                DtoSheetCell selectedSheetCellVersion = allDtoSheetVersions.get(versionNumber);
                if (selectedSheetCellVersion != null) {
                    Platform.runLater(() -> gridScrollerController.initializeVersionPopupGrid(popupGrid, selectedSheetCellVersion));
                } else {
                    Platform.runLater(() -> createErrorPopup("Version not available", "Error"));
                }
            }
        });
    }

    private VBox createContentBox(Label versionLabel, ScrollPane gridScrollPane, Slider valueSlider) {
        VBox contentBox = new VBox(10, versionLabel, gridScrollPane, valueSlider);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        contentBox.setPadding(new Insets(10));
        contentBox.setStyle("-fx-background-color: #e8f0f6;");
        return contentBox;
    }

    private void setupScene(VBox contentBox) {
        ScrollPane contentScrollPane = new ScrollPane(contentBox);
        contentScrollPane.setFitToWidth(true);
        contentScrollPane.setFitToHeight(true);
        contentScrollPane.setStyle("-fx-background-color: #e8f0f6;");
        Scene popupScene = new Scene(contentScrollPane);
        popupStage.setScene(popupScene);
    }

    private void createErrorPopup(String message, String title) {
        // Implement the error popup display
    }
}
