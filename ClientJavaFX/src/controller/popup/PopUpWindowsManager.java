package controller.popup;

import controller.grid.GridController;
import controller.popup.filter.AvailableFilterValuesPopup;
import controller.popup.filter.FilterDataPopup;
import controller.popup.filter.FilterGridPopupHandler;
import controller.popup.graph.AvailableGraphValuesPopup;
import controller.popup.graph.ConsolidatedGraphPopup;
import controller.popup.graph.GraphPopup;
import controller.popup.graph.GraphPreWindow;
import controller.popup.runtime_analysis.RunTimeAnalysisPopupHandler;
import controller.popup.sort.SortGridPopupHandler;
import controller.popup.sort.SortRowsPopup;
import controller.popup.versions.VersionsPopupHandler;
import utilities.javafx.smallparts.FilterGridData;
import utilities.javafx.smallparts.SortRowsData;

import dto.components.DtoContainerData;
import dto.components.DtoSheetCell;
import utilities.javafx.Model;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import dto.small_parts.CellLocation;

import java.util.*;

public class PopUpWindowsManager {

    public void createErrorPopUpCircularDependency(DtoSheetCell dtoSheetCell, GridController gridScrollerController , List<CellLocation> cycle) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Circular Dependency Error");

        // Create a new GridPane and initialize it with data
        GridPane popupGrid = new GridPane();

        //popupGrid.getStylesheets().add(Objects.requireNonNull(getClass().getResource("../Grid/ExelBasicGrid.css")).toExternalForm());
        popupGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");


        // Initialize the grid with the DtoSheetCell and cycle
        // Assuming gridScrollerController is accessible in this context
        gridScrollerController.initializeCirclePopUp(popupGrid, dtoSheetCell, cycle);

        // Wrap the grid with a ScrollPane
        ScrollPane scrollPane = new ScrollPane(popupGrid);

        // Create a Scene and show the popup
        Scene popupScene = new Scene(scrollPane);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    public void createErrorPopup(String message, String title) {
        Stage popupStage = new Stage();
        popupStage.setTitle(title);

        // Create a Label with the error message
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true); // Enable text wrapping
        messageLabel.setPadding(new Insets(10));

        // Create a layout and add the Label
        StackPane layout = new StackPane();
        layout.getChildren().add(messageLabel);

        // Wrap the layout with a ScrollPane
        ScrollPane scrollPane = new ScrollPane(layout);

        // Create the Scene with the ScrollPane
        Scene scene = new Scene(scrollPane);

        // Adjust size based on content
        scene.widthProperty().addListener((obs, oldVal, newVal) -> popupStage.sizeToScene());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> popupStage.sizeToScene());

        scene.getStylesheets().add("controller/main/ErrorPopUp.css");


        //scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ErrorPopup.css")).toExternalForm());
        messageLabel.getStyleClass().add("popup-label");
        layout.getStyleClass().add("popup-container");

        // Set the scene and modality
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(scene);
        popupStage.show();
    }

    public SortRowsData openSortRowsWindow() {
        SortRowsPopup sortRowsPopup = new SortRowsPopup();
        return sortRowsPopup.show();
    }

    public FilterGridData openFilterDataWindow() {
        FilterDataPopup filterDataPopup = new FilterDataPopup();
        return filterDataPopup.show();
    }

    public Map<Character, Set<String>> openAvailableFilterValuesPopUp(Map<Character, Set<String>> stringsInChosenColumn) {
        AvailableFilterValuesPopup availableFilterValuesPopup = new AvailableFilterValuesPopup(stringsInChosenColumn);
        return availableFilterValuesPopup.show();
    }

    public void openFilterGridPopUp(DtoContainerData dtoContainerData, GridController gridScrollerController) {
        FilterGridPopupHandler filterGridPopupHandler = new FilterGridPopupHandler(gridScrollerController, dtoContainerData);
        filterGridPopupHandler.show();

    }

    public void openSortGridPopUp(DtoContainerData dtoContainerData, GridController gridScrollerController) {
        SortGridPopupHandler sortGridPopupHandler = new SortGridPopupHandler(gridScrollerController, dtoContainerData);
        sortGridPopupHandler.show();
    }

    public Map<Character, List<String>> openAvailableGraphValuesPopUp(char xAxis, char yAxis, String xTitle, String yTitle, Map<Character, Set<String>> stringsInChosenColumn) {
        AvailableGraphValuesPopup availableGraphValuesPopup = new AvailableGraphValuesPopup(xAxis, yAxis, xTitle, yTitle, stringsInChosenColumn);
        return availableGraphValuesPopup.show();
    }

    public void openGraphPopUp(char xAxis, String xTitle, String yTitle, Map<Character, List<String>> columnsForXYaxis, boolean isBarChart) {
        GraphPopup graphPopup = new GraphPopup(xAxis, xTitle, yTitle, columnsForXYaxis, isBarChart);
        graphPopup.show();
    }

    public List<String> openGraphWindow(){
        GraphPreWindow graphPreWindow = new GraphPreWindow();
        return graphPreWindow.show();
    }

    public void showVersionsPopup(
            Map<Integer, DtoSheetCell> allDtoSheetVersions,
            int lastVersion,
            GridController gridScrollerController) {

        VersionsPopupHandler versionsPopupHandler = new VersionsPopupHandler(allDtoSheetVersions, lastVersion, gridScrollerController);
        versionsPopupHandler.show();
    }

    public void showRuntimeAnalysisPopup(DtoSheetCell sheetCellRunTime, Model model, GridController gridScrollerController) {
        RunTimeAnalysisPopupHandler runTimeAnalysisPopupHandler = new RunTimeAnalysisPopupHandler(sheetCellRunTime, model, gridScrollerController);
        runTimeAnalysisPopupHandler.show();
    }

    public void openConsolidatedGraphPopup(boolean isChartGraph, DtoSheetCell dtoSheetCell) {
        ConsolidatedGraphPopup graphPopup = new ConsolidatedGraphPopup(isChartGraph, dtoSheetCell);
        graphPopup.show();
    }
}








