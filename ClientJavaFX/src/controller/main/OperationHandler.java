package controller.main;

import controller.grid.GridController;
import controller.popup.PopUpWindowsManager;
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

    public OperationHandler(PopUpWindowsManager popUpWindowsManager,
                            GridController gridController, DtoSheetCell dtoSheetCell, Model model) {
        this.popUpWindowsManager = popUpWindowsManager;
        this.gridController = gridController;
        this.dtoSheetCell = dtoSheetCell;
        this.model = model;
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

    public void applyChangesInParameters(DtoSheetCell dtoSheetCellAsDataParameter, Model model, GridController gridScrollerController) {
        this.dtoSheetCell = dtoSheetCellAsDataParameter;
        this.model = model;
        this.gridController = gridScrollerController;
    }
}