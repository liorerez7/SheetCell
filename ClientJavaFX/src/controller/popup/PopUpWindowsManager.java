package controller.popup;

import com.sun.webkit.Timer;
import controller.grid.GridController;
import controller.main.MainController;
import controller.popup.filter.FilterPopup;
import controller.popup.find_and_replace.FindAndReplacePopupResult;
import controller.popup.find_and_replace.FindReplacePopup;
import controller.popup.graph.ConsolidatedGraphPopup;
import controller.popup.runtime_analysis.RunTimeAnalysisPopupHandler;
import controller.popup.sort.SortRowsPopup;
import controller.popup.versions.VersionsPopupHandler;
import dto.components.DtoSheetCell;
import dto.small_parts.UpdateCellInfo;
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

    public void openSortRowsPopup(DtoSheetCell dtoSheetCell, GridController gridController) {
        SortRowsPopup sortRowsPopup = new SortRowsPopup(dtoSheetCell, gridController, this);
        sortRowsPopup.show();
    }

    public void showVersionsPopup(
            Map<Integer, DtoSheetCell> allDtoSheetVersions,
            int lastVersion,
            GridController gridScrollerController, Map<Integer, UpdateCellInfo> versionToUpdateCellInfo) {

        VersionsPopupHandler versionsPopupHandler = new VersionsPopupHandler(allDtoSheetVersions,
                lastVersion, gridScrollerController, versionToUpdateCellInfo);

        versionsPopupHandler.show();
    }

    public void showRuntimeAnalysisPopup(DtoSheetCell sheetCellRunTime, Model model, GridController gridScrollerController) {
        RunTimeAnalysisPopupHandler runTimeAnalysisPopupHandler = new RunTimeAnalysisPopupHandler(sheetCellRunTime,
                model, gridScrollerController);

        runTimeAnalysisPopupHandler.show();
    }

    public void openConsolidatedGraphPopup(boolean isChartGraph, DtoSheetCell dtoSheetCell) {
        ConsolidatedGraphPopup graphPopup = new ConsolidatedGraphPopup(isChartGraph, dtoSheetCell, this);
        graphPopup.show();
    }

    public void openFilterPopup(DtoSheetCell dtoSheetCell, GridController gridScrollerController) {
        FilterPopup filterData = new FilterPopup(dtoSheetCell, gridScrollerController, this);
        filterData.show();
    }

    public FindAndReplacePopupResult openFindReplacePopup(DtoSheetCell dtoSheetCell, GridController gridController,
                                                          Model model, MainController mainController) {
        FindReplacePopup findReplacePopup = new FindReplacePopup(dtoSheetCell, gridController, model, mainController);
        return findReplacePopup.show();
    }
}








