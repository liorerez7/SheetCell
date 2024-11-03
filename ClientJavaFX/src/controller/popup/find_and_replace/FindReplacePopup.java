package controller.popup.find_and_replace;

import com.google.gson.reflect.TypeToken;
import controller.grid.CustomCellLabel;
import controller.grid.GridController;
import controller.main.MainController;
import controller.popup.PopUpWindowsManager;
import dto.components.DtoCell;
import dto.components.DtoSheetCell;
import dto.permissions.PermissionStatus;
import dto.small_parts.CellLocation;
import dto.small_parts.EffectiveValue;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.Callback;
import okhttp3.Response;
import utilities.Constants;
import utilities.http.manager.HttpRequestManager;
import utilities.javafx.Model;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FindReplacePopup {

    private final DtoSheetCell dtoSheetCell;
    private final GridController gridController;
    private final Stage popupStage;
    private final TextField findValueField;
    private final TextField rangeFromField;
    private final TextField rangeToField;
    private final CheckBox fullGridCheckBox;
    private final Button findButton;
    private final TextField replaceValueField;
    private final Button replaceButton;
    private final Button backToRangeButton;
    private final Button applyOnCurrentGridButton;
    private VBox originalGridContainer;
    private VBox replacedGridContainer;
    private VBox controlPanel;
    private VBox replaceSection;
    private Map<CellLocation, CustomCellLabel> cellLocationCustomOriginalCellLabelMap = new HashMap<>();
    private Map<CellLocation, CustomCellLabel> cellLocationCustomNewCellLabelMap = new HashMap<>();
    private Set<CellLocation> newValueLocations = new HashSet<>();
    private final Model model;
    private final MainController mainController;
    private boolean isApplyWasSuccessful = false;
    private FindAndReplacePopupResult result;
    private PopUpWindowsManager popUpWindowsManager;

    public FindReplacePopup(DtoSheetCell dtoSheetCell,
                            GridController gridController, Model model, MainController mainController, PopUpWindowsManager popUpWindowsManager) {

        this.dtoSheetCell = dtoSheetCell;
        this.gridController = gridController;
        this.model = model;
        this.mainController = mainController;
        this.popUpWindowsManager = popUpWindowsManager;

        // Get the current permission status
        PermissionStatus permissionStatus = mainController.getPermissionStatus();

        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Find and Replace");

        findValueField = new TextField();
        findValueField.setPromptText("Enter value to find");

        rangeFromField = new TextField();
        rangeFromField.setPromptText("Range From (e.g., A1)");

        rangeToField = new TextField();
        rangeToField.setPromptText("Range To (e.g., B10)");

        fullGridCheckBox = new CheckBox("Full Grid");
        findButton = new Button("Find");
        findButton.setDisable(true);
        findButton.setTooltip(new Tooltip("Find all instances of the value in the selected range."));


        replaceValueField = new TextField();
        replaceValueField.setPromptText("New value to replace with");

        replaceButton = new Button("Replace");
        replaceButton.setDisable(true);
        replaceButton.setTooltip(new Tooltip("Replace all found values with the new value."));


        backToRangeButton = new Button("Back to range selection");
        backToRangeButton.setTooltip(new Tooltip("Return to adjust the selection range."));


        applyOnCurrentGridButton = new Button("Apply changes on current grid");
        applyOnCurrentGridButton.setVisible(false); // Initially hidden
        applyOnCurrentGridButton.setTooltip(new Tooltip("Apply all changes to the active grid."));

        // Disable apply button if permission status is READER
        if (permissionStatus == PermissionStatus.READER) {
            applyOnCurrentGridButton.setDisable(true);
        }

        findButton.getStyleClass().add("button");
        replaceButton.getStyleClass().add("button");
        backToRangeButton.getStyleClass().add("button");
        applyOnCurrentGridButton.getStyleClass().add("button");

        initializeLayout();

        findValueField.getStyleClass().add("text-field");
        rangeFromField.getStyleClass().add("text-field");
        rangeToField.getStyleClass().add("text-field");
        replaceValueField.getStyleClass().add("text-field");

        originalGridContainer.setId("originalGridContainer");
        replacedGridContainer.setId("replacedGridContainer");

    }

    private void initializeLayout() {
        GridPane rangePane = new GridPane();
        rangePane.setHgap(10);
        rangePane.setVgap(10);
        rangePane.setPadding(new Insets(10));

        rangePane.add(new Label("Find Value:"), 0, 0);
        rangePane.add(findValueField, 1, 0);
        rangePane.add(new Label("Range From:"), 0, 1);
        rangePane.add(rangeFromField, 1, 1);
        rangePane.add(new Label("Range To:"), 0, 2);
        rangePane.add(rangeToField, 1, 2);
        rangePane.add(fullGridCheckBox, 1, 3);
        rangePane.add(findButton, 1, 4);

        rangePane.setBorder(new Border(new BorderStroke(
                Color.LIGHTGRAY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(5),
                new BorderWidths(1)
        )));

        replaceSection = new VBox(10,
                new Label("New value to replace with:"), replaceValueField, replaceButton, backToRangeButton, applyOnCurrentGridButton);
        replaceSection.setPadding(new Insets(10));
        replaceSection.setVisible(false);

        findButton.setOnAction(e -> handleFindButtonClick());
        replaceButton.setOnAction(e -> handleReplaceButtonClick());
        backToRangeButton.setOnAction(e -> handleBackToRangeSelection());
        applyOnCurrentGridButton.setOnAction(e -> handleApplyOnCurrentGridButtonClick());

        Label originalGridTitle = new Label("Original Grid");
        originalGridTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        originalGridContainer = new VBox(8, originalGridTitle, createOriginalGrid());

        Label replacedGridTitle = new Label("Replaced Grid");
        replacedGridTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        replacedGridContainer = new VBox(8, replacedGridTitle);
        replacedGridContainer.setVisible(false);

        // Adding color legend to control panel at the bottom left
        VBox infoBox = createColorLegend();
        controlPanel = new VBox(10, rangePane, replaceSection, infoBox); // Add infoBox to controlPanel
        VBox.setVgrow(infoBox, Priority.ALWAYS); // Ensure infoBox stays at the bottom
        infoBox.setPadding(new Insets(10, 0, 0, 0)); // Optional padding for bottom alignment

        VBox gridsDisplay = new VBox(20, originalGridContainer, replacedGridContainer);

        HBox mainLayout = new HBox(15, controlPanel, gridsDisplay);
        mainLayout.setPadding(new Insets(15));

        Scene scene = new Scene(mainLayout, 1510, 750);
        scene.getStylesheets().add(getClass().getResource("find_replace_popup.css").toExternalForm());
        popupStage.setScene(scene);

        addListeners();
    }

    private void handleApplyOnCurrentGridButtonClick() {

        if(model.getNewerVersionOfSheetProperty().getValue()){

           popUpWindowsManager.createErrorPopup("You can't apply changes on the" +
                   " current grid because there is a newer version of the sheet", "Error");

        }
        else{
            result = new FindAndReplacePopupResult(true, newValueLocations, replaceValueField.getText());
            isApplyWasSuccessful = true;
            popupStage.close();
        }
    }

    private VBox createOriginalGrid() {
        GridPane originalGrid = new GridPane();
        originalGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");

        cellLocationCustomOriginalCellLabelMap = gridController.initializeOriginalPopupGrid(originalGrid, dtoSheetCell);

        ScrollPane gridScrollPane = new ScrollPane(originalGrid);
        gridScrollPane.setFitToWidth(true);
        gridScrollPane.setFitToHeight(true);
        gridScrollPane.setPrefSize(1120, 335);

        VBox gridContainer = new VBox(gridScrollPane);
        gridContainer.setPadding(new Insets(10));
        gridContainer.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

        return gridContainer;
    }

    private VBox createColorLegend() {
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.BOTTOM_LEFT);

        HBox lightGrayBox = createInfoLine(Color.LIGHTGRAY, "Range selected area");
        HBox lightBlueBox = createInfoLine(Color.LIGHTBLUE, "Cells the selected cell affects");
        HBox lightGreenBox = createInfoLine(Color.LIGHTGREEN, "Cells affected by the selected cell");
        HBox lavenderBox = createInfoLine(Color.LAVENDER, "Cells with the requested value");

        infoBox.getChildren().addAll(lightGrayBox, lightBlueBox, lightGreenBox, lavenderBox);
        return infoBox;
    }

    private HBox createInfoLine(Color color, String description) {
        Circle colorCircle = new Circle(5, color);
        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("label"); // Style it if needed
        HBox line = new HBox(10, colorCircle, descriptionLabel);
        line.setAlignment(Pos.CENTER_LEFT);
        return line;
    }



    private void handleFindButtonClick() {
        String findValue = findValueField.getText().trim();
        String rangeFrom = rangeFromField.getText().trim().toUpperCase();
        String rangeTo = rangeToField.getText().trim().toUpperCase();
        boolean isFullGrid = fullGridCheckBox.isSelected();

        if (!isValidInput(findValue, rangeFrom, rangeTo, isFullGrid)) {
            popUpWindowsManager.createErrorPopup("Please provide a valid value and range.", "Invalid Input");
            return;
        }

        newValueLocations = extractLocationsOfTheFindingValue(findValue, dtoSheetCell);
        setBackgroundColorForRangeAndFindingValueCells(rangeFrom, rangeTo, newValueLocations, isFullGrid);

        // Disable range selection and show replace section
        controlPanel.getChildren().get(0).setDisable(true);
        replaceSection.setVisible(true);
    }

    // Helper to validate input values
    private boolean isValidInput(String findValue, String rangeFrom, String rangeTo, boolean isFullGrid) {
        if (findValue.isEmpty()) return false;

        // Skip range checks if full grid is selected
        if (isFullGrid) return true;

        return isValidCellFormat(rangeFrom) && isValidCellFormat(rangeTo) && isValidRange(rangeFrom, rangeTo);
    }

    // Helper to validate cell format (e.g., A1, B2)
    private boolean isValidCellFormat(String cell) {
        return cell.matches("^[A-Z][1-9][0-9]*$");
    }

    // Helper to validate that 'rangeTo' is after or the same as 'rangeFrom' and within bounds of dtoSheetCell
    private boolean isValidRange(String rangeFrom, String rangeTo) {
        char fromCol = rangeFrom.charAt(0);
        char toCol = rangeTo.charAt(0);
        int fromRow = Integer.parseInt(rangeFrom.substring(1));
        int toRow = Integer.parseInt(rangeTo.substring(1));

        // Check that rangeFrom is before or equal to rangeTo
        if (fromCol > toCol || fromRow > toRow) {
            return false;
        }

        int maxColumns = dtoSheetCell.getNumberOfColumns();
        int maxRows = dtoSheetCell.getNumberOfRows();

        // Check that rangeFrom and rangeTo are within the bounds of the sheet
        return isWithinBounds(fromCol, toCol, fromRow, toRow, maxColumns, maxRows);
    }

    private boolean isWithinBounds(char fromCol, char toCol, int fromRow, int toRow, int numberOfCols, int numberOfRows) {
        return fromCol >= 'A' && fromCol <= (char)('A' + numberOfCols - 1) &&
                toCol >= 'A' && toCol <= (char)('A' + numberOfCols - 1) &&
                fromRow >= 1 && fromRow <= numberOfRows &&
                toRow >= 1 && toRow <= numberOfRows;
    }

    private Set<CellLocation> extractLocationsOfTheFindingValue(String findValue, DtoSheetCell dtoSheetCell) {
        Map<CellLocation, EffectiveValue> cellLocationEffectiveValueMap = dtoSheetCell.getViewSheetCell();
        Set<CellLocation> locations = new HashSet<>();

        try {
            findValue = String.valueOf(Double.parseDouble(findValue));
        } catch (NumberFormatException e) {
            // Leave as original find value if parsing fails
        }

        String finalFindValue = findValue;
        cellLocationEffectiveValueMap.forEach((location, effectiveValue) -> {
            DtoCell cell = dtoSheetCell.getRequestedCell(location.getCellId());
            String cellOriginalValue = cell.getOriginalValue();

            try {
                cellOriginalValue = String.valueOf(Double.parseDouble(cellOriginalValue));
            } catch (NumberFormatException e) {
                // Leave as original cell value if parsing fails
            }

            if (finalFindValue.equals(cellOriginalValue)) {
                locations.add(location);
            }
        });

        return locations;
    }

    private void setBackgroundColorForRangeAndFindingValueCells(String rangeFrom, String rangeTo,
                                                                Set<CellLocation> findingValueLocations, boolean isFullGrid) {

        if (isFullGrid) {
            rangeFrom = "A1";
            char col = (char) (dtoSheetCell.getNumberOfColumns() + 'A' - 1);
            String row = String.valueOf(dtoSheetCell.getNumberOfRows());
            rangeTo = col + row;
        } else {
            rangeFrom = rangeFrom.toUpperCase();
            rangeTo = rangeTo.toUpperCase();
        }

        char rangeFromCol = rangeFrom.charAt(0);
        int rangeFromRow = Integer.parseInt(rangeFrom.substring(1));
        char rangeToCol = rangeTo.charAt(0);
        int rangeToRow = Integer.parseInt(rangeTo.substring(1));

        cellLocationCustomOriginalCellLabelMap.forEach((cellLocation, customCellLabel) -> {
            char col = cellLocation.getVisualColumn();
            int row = Integer.parseInt(cellLocation.getVisualRow());

            boolean isWithinColumnRange = col >= rangeFromCol && col <= rangeToCol;
            boolean isWithinRowRange = row >= rangeFromRow && row <= rangeToRow;

            if (isWithinColumnRange && isWithinRowRange) {
                customCellLabel.setBackgroundColor(findingValueLocations.contains(cellLocation) ? Color.LAVENDER : Color.LIGHTGRAY);
            }
        });
    }

    private void handleReplaceButtonClick() {
        String newValue = replaceValueField.getText();

        if(newValue.charAt(0) == '{'){
            popUpWindowsManager.createErrorPopup("You can't replace with a formula", "Error");
            return;
        }

        GridPane newValuesGrid = new GridPane();
        newValuesGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");

        DtoSheetCell newChangedDtoSheetCell = getNewValuesDtoSheetFromServer(newValue, newValueLocations);


        cellLocationCustomNewCellLabelMap = gridController.initializeGridWithChangedValues(
                newValueLocations, newValuesGrid, newValue, newChangedDtoSheetCell);

        cellLocationCustomNewCellLabelMap.forEach((cellLocation, customCellLabel) -> {
            if (newValueLocations.contains(cellLocation)) {
                customCellLabel.setBackgroundColor(Color.LAVENDER);
            }
        });

        Label replacedGridTitle = new Label("Replaced Grid");
        replacedGridTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        replacedGridContainer.getChildren().setAll(replacedGridTitle, new ScrollPane(newValuesGrid));
        replacedGridContainer.setVisible(true);

        replaceSection.setVisible(true); // Ensure replace section remains visible
        applyOnCurrentGridButton.setVisible(true); // Show "Apply on my current grid" button
        backToRangeButton.setVisible(true); // Ensure "Back" button remains visible
        replaceButton.setVisible(true); // Ensure "Replace" button remains visible
    }

    private DtoSheetCell getNewValuesDtoSheetFromServer(String newValue, Set<CellLocation> newValueLocations) {

        Map<String, String> params = new HashMap<>();
        params.put("newValue", newValue);
        params.put("newValueLocations", Constants.GSON_INSTANCE.toJson(newValueLocations)); // Convert set to JSON

        try(Response response = HttpRequestManager.sendPostSyncRequest(Constants.UPDATE_REPLACED_VALUES_URL, params)){

            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                DtoSheetCell newValuesDtoSheet = Constants.GSON_INSTANCE.fromJson(responseBody, DtoSheetCell.class);

                return newValuesDtoSheet;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private void handleBackToRangeSelection() {
        controlPanel.getChildren().get(0).setDisable(false);
        replaceValueField.clear();
        replaceButton.setDisable(true);
        replaceSection.setVisible(false);
        replacedGridContainer.setVisible(false);
        applyOnCurrentGridButton.setVisible(false); // Hide "Apply on my current grid" button only
    }

    private void updateFindButtonState() {
        boolean isEnabled = !findValueField.getText().isEmpty() &&
                (fullGridCheckBox.isSelected() ||
                        (!rangeFromField.getText().isEmpty() && !rangeToField.getText().isEmpty()));
        findButton.setDisable(!isEnabled);
    }

    private void addListeners() {
        findValueField.textProperty().addListener((observable, oldValue, newValue) -> updateFindButtonState());
        rangeFromField.textProperty().addListener((observable, oldValue, newValue) -> updateFindButtonState());
        rangeToField.textProperty().addListener((observable, oldValue, newValue) -> updateFindButtonState());

        fullGridCheckBox.selectedProperty().addListener((observable, oldValue, isSelected) -> {
            rangeFromField.setDisable(isSelected);
            rangeToField.setDisable(isSelected);
            updateFindButtonState();
        });

        replaceValueField.textProperty().addListener((observable, oldValue, newValue) ->
                replaceButton.setDisable(newValue.isEmpty()));
    }

    public FindAndReplacePopupResult show() {
        popupStage.showAndWait();
        return result;
    }
}
