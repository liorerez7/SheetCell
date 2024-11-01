package controller.main;

import controller.grid.GridController;
import controller.popup.PopUpWindowsManager;
import controller.popup.auto_complete.AutoCompleteResult;
import controller.popup.find_and_replace.FindAndReplacePopupResult;
import dto.components.DtoSheetCell;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utilities.Constants;
import utilities.http.manager.HttpRequestManager;
import utilities.javafx.Model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OperationHandler {

    private PopUpWindowsManager popUpWindowsManager;
    private  GridController gridController;
    private  DtoSheetCell dtoSheetCell;
    private  Model model;
    private MainController mainController;

    public OperationHandler(PopUpWindowsManager popUpWindowsManager,
                            GridController gridController,
                            DtoSheetCell dtoSheetCell, Model model, MainController mainController) {

        this.popUpWindowsManager = popUpWindowsManager;
        this.gridController = gridController;
        this.dtoSheetCell = dtoSheetCell;
        this.model = model;
        this.mainController = mainController;
    }

    public void makeGraph(boolean isChartGraph) {
        popUpWindowsManager.openConsolidatedGraphPopup(isChartGraph, dtoSheetCell);
    }

    public void runTimeAnalysis() {

        Map<String,String> params = new HashMap<>();
        params.put("versionNumber", String.valueOf(dtoSheetCell.getLatestVersion()));

        HttpRequestManager.sendPostAsyncRequest(Constants.POST_TEMP_SHEET_IN_SERVLET, params, new Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try(response){
                    if (!response.isSuccessful()) {

                    }
                    Platform.runLater(() -> {
                        popUpWindowsManager.showRuntimeAnalysisPopup(dtoSheetCell, model, gridController);
                    });
                }
            }
        });
    }

    public void sortRows() {
        popUpWindowsManager.openSortRowsPopup(dtoSheetCell, gridController);
    }

    public void filterGrid() {
        popUpWindowsManager.openFilterPopup(dtoSheetCell, gridController);
    }

    public void applyChangesInParameters(DtoSheetCell dtoSheetCellAsDataParameter, Model model,
                                         GridController gridScrollerController, MainController main) {

        this.dtoSheetCell = dtoSheetCellAsDataParameter;
        this.model = model;
        this.gridController = gridScrollerController;
        this.mainController = main;
    }

    public void findAndReplace() {

        Map<String,String> params = new HashMap<>();
        params.put("versionNumber", String.valueOf(dtoSheetCell.getLatestVersion()));

        HttpRequestManager.sendPostAsyncRequest(Constants.POST_TEMP_SHEET_IN_SERVLET, params, new Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try(response){
                    if (!response.isSuccessful()) {

                    }
                    Platform.runLater(() -> {
                        FindAndReplacePopupResult result = popUpWindowsManager.openFindReplacePopup(dtoSheetCell, gridController, model, mainController);
                        if(result != null && result.isAppliedWasSuccessful()){
                            mainController.updateSheetAccordingToChangedCells(result);
                        }
                    });
                }
            }
        });


    }

    public void autoComplete() {

        Map<String,String> params = new HashMap<>();
        params.put("versionNumber", String.valueOf(dtoSheetCell.getLatestVersion()));

        HttpRequestManager.sendPostAsyncRequest(Constants.POST_TEMP_SHEET_IN_SERVLET, params, new Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try(response){
                    if (!response.isSuccessful()) {

                    }
                    Platform.runLater(() -> {
                        AutoCompleteResult result = popUpWindowsManager.openAutoCompletePopup(dtoSheetCell, gridController, model, mainController);
                        if(result != null && result.isAppliedWasSuccessful()){
                            mainController.updateSheetInCells(result.getCellLocationToNewCellValues());
                        }
                    });
                }
            }
        });
    }
}