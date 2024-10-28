package controller.popup.runtime_analysis;

import controller.grid.GridController;
import dto.components.DtoCell;
import dto.components.DtoSheetCell;
import dto.small_parts.CellLocation;
import dto.small_parts.CellLocationFactory;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.Response;
import utilities.Constants;
import utilities.http.manager.HttpRequestManager;
import utilities.javafx.Model;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RunTimeAnalysisPopupHandler {

    private final DtoSheetCell sheetCellRunTime;
    private final Model model;
    private final GridController gridScrollerController;
    private final AtomicReference<Character> colRef = new AtomicReference<>();
    private final AtomicReference<String> rowRef = new AtomicReference<>();
    private final AtomicReference<Double> currentCellValueRef = new AtomicReference<>();

    // Labels for dynamic values
    private Label pureNumericValueLabel;
    private Label affectsCellsLabel;
    private Label affectedByCellsLabel;

    public RunTimeAnalysisPopupHandler(DtoSheetCell sheetCellRunTime, Model model, GridController gridScrollerController) {
        this.sheetCellRunTime = sheetCellRunTime;
        this.model = model;
        this.gridScrollerController = gridScrollerController;
    }

    public void show() {
        Stage popupStage = setupPopupStage("Run Time Analysis");
        List<CellLocation> cellLocationsOfRunTimeAnalysisCells = extractCellLocations();

        GridPane popupGrid = setupPopupGrid();
        ScrollPane gridScrollPane = wrapGridInScrollPane(popupGrid);

        Platform.runLater(() -> {
            VBox infoBox = createInfoBox(cellLocationsOfRunTimeAnalysisCells);
            Label instructionLabel = createInstructionLabel();
            Slider valueSlider = createValueSlider();
            TextField startingValueField = createTextField("Enter starting value");
            TextField endingValueField = createTextField("Enter ending value");
            TextField stepValueField = createTextField("Enter step value");
            Button submitButton = createSubmitButton(valueSlider, startingValueField, endingValueField, stepValueField);
            Label valueLabel = createValueLabel();
            Label cellIdLabel = createCellIdLabel();
            Label warningLabel = createWarningLabel();

            VBox sliderBox = setupSliderBox(cellIdLabel, warningLabel, startingValueField, endingValueField, stepValueField, submitButton, valueSlider, valueLabel);

            Consumer<CellLocation> labelClickConsumer = createLabelClickConsumer(startingValueField, endingValueField, stepValueField, submitButton, valueSlider, valueLabel, cellIdLabel, warningLabel, instructionLabel, sliderBox, cellLocationsOfRunTimeAnalysisCells);



            initializeGridAndModelBinding(popupGrid, labelClickConsumer, cellLocationsOfRunTimeAnalysisCells);
            setupSliderValueListener(valueSlider, startingValueField, stepValueField, endingValueField, valueLabel);

            Scene popupScene = createPopupScene(gridScrollPane, instructionLabel, infoBox, sliderBox);
            popupStage.setScene(popupScene);
            popupStage.showAndWait();
        });
    }

    private VBox createInfoBox(List<CellLocation> cellLocationsOfRunTimeAnalysisCells) {

        StringJoiner pureNumericCells = new StringJoiner(", ");
        for (CellLocation location : cellLocationsOfRunTimeAnalysisCells) {
            pureNumericCells.add(location.getCellId());
        }
        String result = pureNumericCells.toString();


        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        HBox pureNumericBox = createInfoLine(Color.GRAY, "Pure numeric value cells:", pureNumericValueLabel = new Label(result));
        HBox affectsBox = createInfoLine(Color.LIGHTGREEN, "Cells that this cell affects:", affectsCellsLabel = new Label("N/A"));
        HBox affectedByBox = createInfoLine(Color.LIGHTBLUE, "Cells that affect this cell:", affectedByCellsLabel = new Label("N/A"));

        infoBox.getChildren().addAll(pureNumericBox, affectsBox, affectedByBox);
        return infoBox;
    }


    private HBox createInfoLine(Color color, String description, Label valueLabel) {
        Circle icon = new Circle(5, color);
        Label descriptionLabel = new Label(description);
        HBox lineBox = new HBox(10, icon, descriptionLabel, valueLabel);
        lineBox.setAlignment(Pos.CENTER_LEFT);
        return lineBox;
    }

    // Private helper methods

    private Stage setupPopupStage(String title) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);
        return popupStage;
    }

    private GridPane setupPopupGrid() {
        GridPane popupGrid = new GridPane();
        popupGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");
        return popupGrid;
    }

    private ScrollPane wrapGridInScrollPane(GridPane popupGrid) {
        ScrollPane gridScrollPane = new ScrollPane(popupGrid);
        gridScrollPane.setFitToWidth(true);
        gridScrollPane.setFitToHeight(true);
        gridScrollPane.setPrefViewportWidth(1200);
        gridScrollPane.setPrefViewportHeight(300);
        return gridScrollPane;
    }

    private Label createInstructionLabel() {
        Label instructionLabel = new Label("To begin runtime analysis, please select a numeric cell.");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555; -fx-font-style: italic;");
        return instructionLabel;
    }

    private Slider createValueSlider() {
        Slider valueSlider = new Slider();
        valueSlider.setDisable(true);
        valueSlider.getStyleClass().add("custom-slider");
        return valueSlider;
    }

    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setDisable(true);
        textField.getStyleClass().add("custom-text-field");
        return textField;
    }

    private Button createSubmitButton(Slider valueSlider, TextField... textFields) {
        Button submitButton = new Button("Submit");
        submitButton.setDisable(true);
        submitButton.getStyleClass().add("custom-button");

        Runnable updateSubmitButtonState = () -> submitButton.setDisable(
                Stream.of(textFields).anyMatch(tf -> tf.getText().trim().isEmpty())
        );

        Arrays.stream(textFields).forEach(tf -> tf.textProperty().addListener((obs, oldText, newText) -> updateSubmitButtonState.run()));

        submitButton.setOnAction(event -> configureSliderBasedOnInput(valueSlider, textFields));
        return submitButton;
    }

    private void configureSliderBasedOnInput(Slider valueSlider, TextField... textFields) {
        try {
            int startingValue = Integer.parseInt(textFields[0].getText());
            int endingValue = Integer.parseInt(textFields[1].getText());
            int stepValue = Integer.parseInt(textFields[2].getText());

            if (stepValue > 0 && startingValue < endingValue) {
                valueSlider.setMin(startingValue);
                valueSlider.setMax(endingValue);
                valueSlider.setBlockIncrement(stepValue);
                valueSlider.setMajorTickUnit(stepValue);
                valueSlider.setDisable(false);
            } else {
//                createErrorPopup("Invalid input values", "Error");
            }
        } catch (NumberFormatException e) {
//            createErrorPopup("Please enter valid numbers", "Input Error");
        }
    }

    private Label createValueLabel() {
        Label valueLabel = new Label("Value: N/A");
        valueLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        return valueLabel;
    }

    private Label createCellIdLabel() {
        Label cellIdLabel = new Label("Cell ID: ");
        cellIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        return cellIdLabel;
    }

    private Label createWarningLabel() {
        Label warningLabel = new Label("Runtime analysis is only available for pure numeric cells.");
        warningLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        warningLabel.setVisible(false);
        return warningLabel;
    }

    private VBox setupSliderBox(Label cellIdLabel, Label warningLabel, TextField startingValueField, TextField endingValueField, TextField stepValueField, Button submitButton, Slider valueSlider, Label valueLabel) {
        VBox sliderBox = new VBox(10, cellIdLabel, warningLabel, startingValueField, endingValueField, stepValueField, submitButton, valueSlider, valueLabel);
        sliderBox.setAlignment(Pos.CENTER);
        return sliderBox;
    }

    private Consumer<CellLocation> createLabelClickConsumer(TextField startingValueField, TextField endingValueField, TextField stepValueField, Button submitButton, Slider valueSlider, Label valueLabel, Label cellIdLabel, Label warningLabel, Label instructionLabel, VBox sliderBox, List<CellLocation> cellLocationsOfRunTimeAnalysisCells) {
        return cellLocation -> {
            DtoCell cell = sheetCellRunTime.getRequestedCell(cellLocation.getCellId());

            if (cell != null && cell.getEffectiveValue() != null) {
                colRef.set(cellLocation.getVisualColumn());
                rowRef.set(cellLocation.getVisualRow());
                cellIdLabel.setText("Cell ID: " + colRef.get() + rowRef.get());


                Set<CellLocation> affectedBy = cell.getAffectedBy();
                StringJoiner affectedByCells = new StringJoiner(", ");
                for (CellLocation location : affectedBy) {
                    affectedByCells.add(location.getCellId());
                }
                String resultAffectedByCells = affectedByCells.toString();

                Set<CellLocation> effectsOn = cell.getAffectingOn();
                StringJoiner effectsOnCells = new StringJoiner(", ");
                for (CellLocation location : effectsOn) {
                    effectsOnCells.add(location.getCellId());
                }
                String resultEffectsOnCells = effectsOnCells.toString();

                updateInfoLabels(null, resultEffectsOnCells, resultAffectedByCells);

                if (cellLocationsOfRunTimeAnalysisCells.contains(cellLocation)) {
                    enableNumericFields(startingValueField, endingValueField, stepValueField, submitButton, valueSlider);
                    currentCellValueRef.set((Double) cell.getEffectiveValue().getValue());
                    valueLabel.setText("Value: " + currentCellValueRef.get());
                    warningLabel.setVisible(false);
                } else {
                    disableNonNumericFields(startingValueField, endingValueField, stepValueField, submitButton, valueSlider, warningLabel);
                }
            } else {
                disableNonNumericFields(startingValueField, endingValueField, stepValueField, submitButton, valueSlider, warningLabel);
            }
            instructionLabel.setVisible(false);
        };
    }

    private void enableNumericFields(TextField startingValueField, TextField endingValueField, TextField stepValueField, Button submitButton, Slider valueSlider) {
        startingValueField.clear();
        endingValueField.clear();
        stepValueField.clear();
        startingValueField.setDisable(false);
        endingValueField.setDisable(false);
        stepValueField.setDisable(false);

    }

    private void disableNonNumericFields(TextField startingValueField, TextField endingValueField, TextField stepValueField, Button submitButton, Slider valueSlider, Label warningLabel) {
        startingValueField.clear();
        endingValueField.clear();
        stepValueField.clear();
        startingValueField.setDisable(true);
        endingValueField.setDisable(true);
        stepValueField.setDisable(true);
        submitButton.setDisable(true);
        valueSlider.setDisable(true);
        warningLabel.setVisible(true);
    }

    private void initializeGridAndModelBinding(GridPane popupGrid, Consumer<CellLocation> labelClickConsumer, List<CellLocation> cellLocationsOfRunTimeAnalysisCells) {
        Map<CellLocation, Label> cellLocationLabelMap = gridScrollerController.initializeRunTimeAnalysisPopupGrid(popupGrid, sheetCellRunTime, labelClickConsumer, cellLocationsOfRunTimeAnalysisCells);
        model.setCellLabelToPropertiesRunTimeAnalysis(cellLocationLabelMap);
        model.bindCellLabelToPropertiesRunTimeAnalysis();
        model.setPropertiesByDtoSheetCellRunTimeAnalsys(sheetCellRunTime);
    }

    private Scene createPopupScene(ScrollPane gridScrollPane, Label instructionLabel, VBox infoBox, VBox sliderBox) {
        VBox contentBox = new VBox(10, gridScrollPane, instructionLabel, infoBox, sliderBox);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        contentBox.setPadding(new Insets(10));
        ScrollPane contentScrollPane = new ScrollPane(contentBox);
        contentScrollPane.setFitToWidth(true);
        contentScrollPane.setFitToHeight(true);
        Scene popupScene = new Scene(contentScrollPane);
        popupScene.getStylesheets().add("controller/main/popup.css");
        return popupScene;
    }

    private void setupSliderValueListener(Slider valueSlider, TextField startingValueField, TextField stepValueField, TextField endingValueField, Label valueLabel) {
        valueSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int stepValue = Integer.parseInt(stepValueField.getText());
                int endingValue = Integer.parseInt(endingValueField.getText());

                int roundedValue = (int) Math.round(newVal.doubleValue() / stepValue) * stepValue;
                if (roundedValue > endingValue) {
                    roundedValue -= stepValue;
                }
                valueSlider.setValue(roundedValue);
                valueLabel.setText("Value: " + roundedValue);

                int finalRoundedValue = roundedValue;
                CompletableFuture.runAsync(() -> updateModelWithNewValue(finalRoundedValue));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for step or ending value: " + e.getMessage());
            }
        });
    }

    private void updateModelWithNewValue(int newValue) {
        Map<String, String> map = new HashMap<>();
        map.put("newValue", String.valueOf(newValue));
        map.put("colLocation", String.valueOf(colRef.get()));
        map.put("rowLocation", rowRef.get());
        map.put("versionNumber", String.valueOf(sheetCellRunTime.getLatestVersion()));

        DtoSheetCell newUpdatedSheetCell = runTimeHttpCall(map);
        if (newUpdatedSheetCell != null) {
            Platform.runLater(() -> model.setPropertiesByDtoSheetCellRunTimeAnalsys(newUpdatedSheetCell));
        } else {
            System.out.println("Failed to update model with new value: " + newValue);
        }
    }

    private List<CellLocation> extractCellLocations() {
        List<CellLocation> cellLocations = new ArrayList<>();
        sheetCellRunTime.getViewSheetCell().forEach((location, effectiveValue) -> {
            DtoCell dtoCell = sheetCellRunTime.getRequestedCell(location.getCellId());

            try {
                Double.parseDouble(dtoCell.getOriginalValue());
                cellLocations.add(CellLocationFactory.fromCellId(location.getCellId()));
            } catch (Exception e) {
                // Do nothing for non-numeric values
            }
        });
        return cellLocations;
    }

    private DtoSheetCell runTimeHttpCall(Map<String, String> map) {
        DtoSheetCell dtoSheetCell = null;
        try (Response updateCellResponse = HttpRequestManager.sendPostSyncRequest(Constants.RUNTIME_ANALYSIS, map)) {
            if (!updateCellResponse.isSuccessful()) {
//                Platform.runLater(() -> createErrorPopup("Failed to load sheet", "Error"));
                return null;
            }
            String dtoSheetCellAsJson = updateCellResponse.body().string();
            dtoSheetCell = Constants.GSON_INSTANCE.fromJson(dtoSheetCellAsJson, DtoSheetCell.class);
        } catch (IOException e) {
//            Platform.runLater(() -> createErrorPopup(e.getMessage(), "Error updating cell"));
        }
        return dtoSheetCell;
    }

    private void updateInfoLabels(String pureNumericValue, String affectsCells, String affectedByCells) {

        if(pureNumericValue != null){
            pureNumericValueLabel.setText(pureNumericValue);
        }

        if(affectsCells != null){
            affectsCellsLabel.setText(affectsCells);
        }

        if(affectedByCells != null){
            affectedByCellsLabel.setText(affectedByCells);
        }
    }
}
