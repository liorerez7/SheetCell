package controller.popup.versions;

import controller.grid.CustomCellLabel;
import controller.grid.GridController;
import dto.components.DtoSheetCell;
import dto.small_parts.CellLocation;
import dto.small_parts.EffectiveValue;
import dto.small_parts.UpdateCellInfo;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

public class VersionsPopupHandler {

    private final static int FIRST_VERSION = 1;
    private final Map<Integer, DtoSheetCell> allDtoSheetVersions;
    private final int lastVersion;
    private final GridController gridScrollerController;
    private Stage popupStage;
    private final Map<Integer, List<CellLocation>> versionNumberToChangesCells = new HashMap<>();
    private final Map<Integer, UpdateCellInfo> versionToUpdateCellInfo;


    public VersionsPopupHandler(Map<Integer, DtoSheetCell> allDtoSheetVersions,
                                int lastVersion, GridController gridScrollerController, Map<Integer, UpdateCellInfo> versionToUpdateCellInfo) {

        this.allDtoSheetVersions = allDtoSheetVersions;
        this.lastVersion = lastVersion;
        this.gridScrollerController = gridScrollerController;
        initializeVersionNumberToChangesCells();
        this.versionToUpdateCellInfo = versionToUpdateCellInfo;
    }

    private void initializeVersionNumberToChangesCells() {
        DtoSheetCell previousVersionSheet = null;

        for (Map.Entry<Integer, DtoSheetCell> entry : allDtoSheetVersions.entrySet()) {
            int currentVersion = entry.getKey();
            DtoSheetCell currentSheet = entry.getValue();

            // Initialize the list of changed cells for the current version
            List<CellLocation> changedCells = new ArrayList<>();

            if (previousVersionSheet != null) {
                // Get the current and previous version's cell location maps
                Map<CellLocation, EffectiveValue> currentMap = currentSheet.getViewSheetCell();
                Map<CellLocation, EffectiveValue> previousMap = previousVersionSheet.getViewSheetCell();

                // Compare cells between current and previous versions
                for (Map.Entry<CellLocation, EffectiveValue> cellEntry : currentMap.entrySet()) {
                    CellLocation cellLocation = cellEntry.getKey();
                    EffectiveValue currentEffectiveValue = cellEntry.getValue();
                    EffectiveValue previousEffectiveValue = previousMap.get(cellLocation);


                    Object val1 = currentEffectiveValue.getValue();

                    if (previousEffectiveValue == null) {
                        changedCells.add(cellLocation);
                        continue;
                    }

                    Object val2 = previousEffectiveValue.getValue();
                    if (!val1.equals(val2)) {
                        changedCells.add(cellLocation);
                    }
                }
            }

            // Add the list of changed cells to the map for the current version
            versionNumberToChangesCells.put(currentVersion, changedCells);

            // Update previous version to the current one
            previousVersionSheet = currentSheet;
        }
    }

    public void show() {
        initializePopupStage();

        // Initialize labels with icons
        Label versionLabel = createVersionLabel();
        Label usernameLabel = new Label();
        usernameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4a4a4a;");

        Label oldValueLabel = new Label("Old Value: ");
        Label newValueLabel = new Label("New Value: ");
        HBox cellLocationLabel = createInfoLineWithIcon(Color.LIGHTGRAY, "Cell Location:", new Label());
        HBox affectsCellsLabel = createInfoLineWithIcon(Color.LIGHTGREEN, "Cells impacted by this update:", new Label("N/A"));

        // Create grid and scroll pane
        GridPane popupGrid = createPopupGrid();
        ScrollPane gridScrollPane = wrapGridInScrollPane(popupGrid);

        // Initialize grid with latest version and labels
        initializeGridWithLatestVersion(popupGrid, usernameLabel, oldValueLabel, newValueLabel, cellLocationLabel, affectsCellsLabel);

        // Create slider and setup listener
        Slider valueSlider = createSlider(versionLabel, usernameLabel, oldValueLabel, newValueLabel, cellLocationLabel, affectsCellsLabel, popupGrid);

        // Create content box with all UI components
        VBox contentBox = createContentBox(versionLabel, usernameLabel, oldValueLabel, newValueLabel, cellLocationLabel, affectsCellsLabel, gridScrollPane, valueSlider);
        setupScene(contentBox);

        popupStage.showAndWait();
    }

    // Create labels with icons
    private HBox createInfoLineWithIcon(Color color, String description, Label valueLabel) {
        Circle icon = new Circle(5, color);
        Label descriptionLabel = new Label(description);
        HBox lineBox = new HBox(10, icon, descriptionLabel, valueLabel);
        lineBox.setAlignment(Pos.CENTER_LEFT);
        return lineBox;
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

    // Initialize version with new labels
    private void initializeGridWithLatestVersion(GridPane popupGrid, Label usernameLabel, Label oldValueLabel, Label newValueLabel, HBox cellLocationLabel, HBox affectsCellsLabel) {
        DtoSheetCell initialVersion = allDtoSheetVersions.get(lastVersion);
        List<CellLocation> changedCellLocation = versionNumberToChangesCells.get(lastVersion);
        Map<CellLocation, CustomCellLabel> cellLocationCustomCellLabelMap = gridScrollerController.initializeVersionPopupGrid(popupGrid, initialVersion);

        if (!changedCellLocation.isEmpty()) {
            UpdateCellInfo updateInfo = versionToUpdateCellInfo.get(lastVersion);

            String username = updateInfo.getNewUserName();
            usernameLabel.setText("Updated by username: " + username);

            oldValueLabel.setText("Old Value: " + updateInfo.getPreviousOriginalValue());
            newValueLabel.setText("New Value: " + updateInfo.getNewOriginalValue());
            ((Label) cellLocationLabel.getChildren().get(2)).setText(updateInfo.getLocations().toString());

            // Set the cells affected by this cell
            List<CellLocation> affectedCells = getAffectedCellsForVersion(lastVersion);
            ((Label) affectsCellsLabel.getChildren().get(2)).setText(formatCellLocations(affectedCells));

            highlightChangedCellsInLightGreen(cellLocationCustomCellLabelMap, changedCellLocation, updateInfo);

        }

    }

    // Get list of affected cells (example logic for data retrieval)
    private List<CellLocation> getAffectedCellsForVersion(int versionNumber) {
        List<CellLocation> affectedCells = new ArrayList<>();
        List<CellLocation> changedCells = versionNumberToChangesCells.get(versionNumber);

        for (CellLocation cellLocation : changedCells) {
            if (!(versionToUpdateCellInfo.get(versionNumber).getLocations().contains(cellLocation))) {
                affectedCells.add(cellLocation);
            }
        }
        return affectedCells;
    }

    // Format cell locations for display
    private String formatCellLocations(List<CellLocation> cellLocations) {
        return cellLocations.stream()
                .map(CellLocation::getCellId)
                .reduce((loc1, loc2) -> loc1 + ", " + loc2)
                .orElse("N/A");
    }

    private void highlightChangedCellsInLightGreen(Map<CellLocation, CustomCellLabel> cellLocationCustomCellLabelMap,
                                                   List<CellLocation> changedCellLocation, UpdateCellInfo updateCellInfo) {

        CellLocation location;
        if(!updateCellInfo.isChangedInReplaceFunction()){

            updateCellInfo.getLocations().forEach(locationOfUpdatedCell -> {

                cellLocationCustomCellLabelMap.forEach((cellLocation, customCellLabel) -> {
                    if (changedCellLocation.contains(cellLocation) && !(cellLocation.equals(locationOfUpdatedCell))) {
                        customCellLabel.setBackgroundColor(Color.LIGHTGREEN);
                    }
                    if (cellLocation.equals(locationOfUpdatedCell)) {
                        customCellLabel.setBackgroundColor(Color.LIGHTGRAY);
                    }
                });

                return;
            });
        }
        else{
            Set<CellLocation> locations = updateCellInfo.getLocations();

            cellLocationCustomCellLabelMap.forEach((cellLocation, customCellLabel) -> {
                if (changedCellLocation.contains(cellLocation) && !(locations.contains(cellLocation))) {
                    customCellLabel.setBackgroundColor(Color.LIGHTGREEN);
                }
                if (locations.contains(cellLocation)) {
                    customCellLabel.setBackgroundColor(Color.LIGHTGRAY);
                }
            });
        }
    }

    // Update createSlider method to pass all label references
    private Slider createSlider(Label versionLabel, Label usernameLabel, Label oldValueLabel, Label newValueLabel, HBox cellLocationLabel, HBox affectsCellsLabel, GridPane popupGrid) {
        Slider valueSlider = new Slider(1, lastVersion, lastVersion);
        valueSlider.setBlockIncrement(1);
        valueSlider.setMajorTickUnit(1);
        valueSlider.setMinorTickCount(0);
        valueSlider.setShowTickMarks(true);
        valueSlider.setShowTickLabels(true);
        valueSlider.setStyle("-fx-control-inner-background: #e8f0f6; -fx-tick-mark-fill: #4a4a4a; -fx-tick-label-fill: #4a4a4a; -fx-border-color: #4a4a4a; -fx-border-radius: 5px;");

        setupSliderListener(valueSlider, versionLabel, usernameLabel, oldValueLabel, newValueLabel, cellLocationLabel, affectsCellsLabel, popupGrid);
        return valueSlider;
    }



    // Modified setupSliderListener to update new labels dynamically with slider changes
    private void setupSliderListener(Slider valueSlider, Label versionLabel, Label usernameLabel, Label oldValueLabel, Label newValueLabel, HBox cellLocationLabel, HBox affectsCellsLabel, GridPane popupGrid) {
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
                    Platform.runLater(() -> {
                        Map<CellLocation, CustomCellLabel> cellLocationCustomCellLabelMap = gridScrollerController
                                .initializeVersionPopupGrid(popupGrid, selectedSheetCellVersion);

                        clearHighlights(cellLocationCustomCellLabelMap);

                        List<CellLocation> changesCells = versionNumberToChangesCells.get(versionNumber);
                        UpdateCellInfo updateInfo = versionToUpdateCellInfo.get(versionNumber);

                        if (!changesCells.isEmpty()) {
                            String username = updateInfo.getNewUserName();
                            usernameLabel.setText("Updated by username: " + username);

                            oldValueLabel.setText("Old Value: " + updateInfo.getPreviousOriginalValue());
                            newValueLabel.setText("New Value: " + updateInfo.getNewOriginalValue());
                            ((Label) cellLocationLabel.getChildren().get(2)).setText(updateInfo.getLocations().toString());

                            List<CellLocation> affectedCells = getAffectedCellsForVersion(versionNumber);
                            ((Label) affectsCellsLabel.getChildren().get(2)).setText(formatCellLocations(affectedCells));

                            highlightChangedCellsInLightGreen(cellLocationCustomCellLabelMap, changesCells, updateInfo);
                        }
                    });
                } else {
                    Platform.runLater(() -> createErrorPopup("Version not available", "Error"));
                }
            }
        });
    }


    private void clearHighlights(Map<CellLocation, CustomCellLabel> cellLocationCustomCellLabelMap) {
        cellLocationCustomCellLabelMap.forEach((cellLocation, customCellLabel) -> customCellLabel.setBackgroundColor(Color.WHITE));
    }

    private VBox createContentBox(Label versionLabel, Label usernameLabel, Label oldValueLabel, Label newValueLabel, HBox cellLocationLabel, HBox affectsCellsLabel, ScrollPane gridScrollPane, Slider valueSlider) {
        VBox contentBox = new VBox(10, versionLabel, usernameLabel, oldValueLabel, newValueLabel, cellLocationLabel, affectsCellsLabel, gridScrollPane, valueSlider);
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
