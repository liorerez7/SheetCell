package controller.popup.find_and_replace;

import controller.grid.CustomCellLabel;
import controller.grid.GridController;
import dto.components.DtoCell;
import dto.components.DtoSheetCell;
import dto.small_parts.CellLocation;
import dto.small_parts.EffectiveValue;
import dto.small_parts.ReturnedValueType;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private VBox originalGridContainer;
    private VBox controlPanel;
    private VBox replaceSection; // Added to control replace section visibility
    private Map<CellLocation, CustomCellLabel> cellLocationCustomOriginalCellLabelMap = new HashMap<>();
    private Set<CellLocation> newValueLocations = new HashSet<>();

    public FindReplacePopup(DtoSheetCell dtoSheetCell, GridController gridController) {
        this.dtoSheetCell = dtoSheetCell;
        this.gridController = gridController;

        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Find and Replace");

        // Initializing fields
        findValueField = new TextField();
        findValueField.setPromptText("Enter value to find");

        rangeFromField = new TextField();
        rangeFromField.setPromptText("Range From (e.g., A1)");

        rangeToField = new TextField();
        rangeToField.setPromptText("Range To (e.g., B10)");

        fullGridCheckBox = new CheckBox("Full Grid");
        findButton = new Button("Find");
        findButton.setDisable(true);

        // Replace section fields (initially hidden)
        replaceValueField = new TextField();
        replaceValueField.setPromptText("New value to replace with");

        replaceButton = new Button("Replace");
        replaceButton.setDisable(true);

        backToRangeButton = new Button("Back to range selection");

        initializeLayout();
    }

    private void initializeLayout() {
        // Range selection panel
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

        // Replace section
        replaceSection = new VBox(10,
                new Label("New value to replace with:"), replaceValueField, replaceButton, backToRangeButton);
        replaceSection.setPadding(new Insets(10));
        replaceSection.setVisible(false); // Initially hidden

        // Button actions
        findButton.setOnAction(e -> handleFindButtonClick());
        replaceButton.setOnAction(e -> handleReplaceButtonClick());
        backToRangeButton.setOnAction(e -> handleBackToRangeSelection());

        // Original grid container with a title label
        Label originalGridTitle = new Label("Original Grid");
        originalGridTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        originalGridContainer = new VBox(8, originalGridTitle, createOriginalGrid());

        // Main layout: left side for input, right side for grids
        controlPanel = new VBox(10, rangePane, replaceSection);
        HBox mainLayout = new HBox(10, controlPanel, originalGridContainer);
        Scene scene = new Scene(mainLayout, 900, 600);
        popupStage.setScene(scene);

        // Listeners to manage state
        addListeners();
    }

    private VBox createOriginalGrid() {
        GridPane originalGrid = new GridPane();
        originalGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");

        cellLocationCustomOriginalCellLabelMap = gridController.initializeOriginalPopupGrid(originalGrid, dtoSheetCell);

        ScrollPane gridScrollPane = new ScrollPane(originalGrid);
        gridScrollPane.setFitToWidth(true);
        gridScrollPane.setFitToHeight(true);
        gridScrollPane.setPrefSize(600, 400);

        VBox gridContainer = new VBox(gridScrollPane);
        gridContainer.setPadding(new Insets(10));
        gridContainer.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

        return gridContainer;
    }

    private void handleFindButtonClick() {
        String findValue = findValueField.getText();
        String rangeFrom = rangeFromField.getText();
        String rangeTo = rangeToField.getText();
        boolean isFullGrid = fullGridCheckBox.isSelected();

        // Perform the find operation
        newValueLocations = extractLocationsOfTheFindingValue(findValue, dtoSheetCell);
        setBackgroundColorForRangeAndFindingValueCells(rangeFrom, rangeTo, newValueLocations, isFullGrid);

        // Disable range selection fields and show replace section
        controlPanel.getChildren().get(0).setDisable(true); // Disables the range selection panel
        replaceSection.setVisible(true); // Shows the replace section
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
                customCellLabel.setBackgroundColor(findingValueLocations.contains(cellLocation) ? Color.LIGHTBLUE : Color.LIGHTGRAY);
            }
        });
    }

    private void handleReplaceButtonClick() {
        String newValue = replaceValueField.getText();

        DtoSheetCell newValueDtoSheetCell = swapOldValuesToNew(dtoSheetCell, newValue);

        // Reset for new search if needed
        controlPanel.getChildren().get(0).setDisable(false);
        replaceValueField.clear();
        replaceButton.setDisable(true);
        replaceSection.setVisible(false);
    }

    private DtoSheetCell swapOldValuesToNew(DtoSheetCell dtoSheetCell, String newValue) {

        DtoSheetCell newDtoSheetCell = new DtoSheetCell(dtoSheetCell);

        Map<CellLocation, EffectiveValue> valueMap = newDtoSheetCell.getViewSheetCell();
        newValueLocations.forEach(location -> {
            DtoCell cell = newDtoSheetCell.getRequestedCell(location.getCellId());
            cell.setOriginalValue(newValue);
            cell.setEffectiveValue(new EffectiveValue(ReturnedValueType.STRING, newValue));
            valueMap.put(location, new EffectiveValue(ReturnedValueType.STRING, newValue));
        });

        return newDtoSheetCell;
    }

    private void handleBackToRangeSelection() {
        controlPanel.getChildren().get(0).setDisable(false);
        replaceValueField.clear();
        replaceButton.setDisable(true);
        replaceSection.setVisible(false);
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

    public void show() {
        popupStage.showAndWait();
    }
}
