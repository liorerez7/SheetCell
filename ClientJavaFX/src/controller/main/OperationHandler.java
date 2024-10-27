package controller.main;

import controller.grid.GridController;
import dto.components.DtoContainerData;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OperationHandler {

    private  PopUpWindowsHandler popUpWindowsHandler;
    private  GridController gridController;
    private  DtoSheetCell dtoSheetCell;
    private  Model model;

    public OperationHandler(PopUpWindowsHandler popUpWindowsHandler,
                            GridController gridController, DtoSheetCell dtoSheetCell, Model model) {
        this.popUpWindowsHandler = popUpWindowsHandler;
        this.gridController = gridController;
        this.dtoSheetCell = dtoSheetCell;
        this.model = model;
    }

    public void makeGraph(boolean isChartGraph) {
        List<String> columnsForXYaxis = popUpWindowsHandler.openGraphWindow();

        if (columnsForXYaxis == null || columnsForXYaxis.size() != 4) {
            return;
        }

        char xAxis = columnsForXYaxis.get(0).charAt(0);
        char yAxis = columnsForXYaxis.get(1).charAt(0);
        String xTitle = columnsForXYaxis.get(2);
        String yTitle = columnsForXYaxis.get(3);

        Map<Character, Set<String>> columnsXYaxisToStrings = dtoSheetCell.getUniqueStringsInColumn(List.of(xAxis, yAxis), isChartGraph);

        Platform.runLater(() -> {
            Map<Character, List<String>> filteredColumns = popUpWindowsHandler.openFilterDataWithOrderPopUp(
                    xAxis, yAxis, xTitle, yTitle, columnsXYaxisToStrings);

            if (filteredColumns != null) {
                popUpWindowsHandler.openGraphPopUp(xAxis, xTitle, yTitle, filteredColumns, isChartGraph);
            }
        });
    }

//    public void runTimeAnalysis() {
//        var runTimeAnalysisData = popUpWindowsHandler.openRunTimeAnalysisWindow();
//        String cellId = runTimeAnalysisData.getCellId().toUpperCase();
//
//        if (cellId.isEmpty() || dtoSheetCell == null) {
//            return;
//        }
//
//        Platform.runLater(() -> {
//            double currentVal = validateCellValue(dtoSheetCell.getRequestedCell(cellId).getEffectiveValue().getValue().toString(),
//                    runTimeAnalysisData.getStartingValue(), runTimeAnalysisData.getEndingValue());
//
//            if (!Double.isNaN(currentVal)) {
//                char col = cellId.charAt(0);
//                String row = cellId.substring(1);
//
//                popUpWindowsHandler.showRuntimeAnalysisPopup(dtoSheetCell, runTimeAnalysisData.getStartingValue(),
//                        runTimeAnalysisData.getEndingValue(), runTimeAnalysisData.getStepValue(),
//                        currentVal, col, row, model, gridController);
//            }
//        });
//    }

    public void runTimeAnalysis() {

        Map<String,String> params = new HashMap<>();
        params.put("versionNumber", String.valueOf(dtoSheetCell.getLatestVersion()));
        params.put("createOrDelete", "create");


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
                        popUpWindowsHandler.showRuntimeAnalysisPopup(dtoSheetCell, model, gridController);
                    });
                }
            }
        });




//        var runTimeAnalysisData = popUpWindowsHandler.openRunTimeAnalysisWindow();
//        String cellId = runTimeAnalysisData.getCellId().toUpperCase();
//
//        if (cellId.isEmpty() || dtoSheetCell == null) {
//            return;
//        }
//
//        Platform.runLater(() -> {
//            double currentVal = validateCellValue(dtoSheetCell.getRequestedCell(cellId).getEffectiveValue().getValue().toString(),
//                    runTimeAnalysisData.getStartingValue(), runTimeAnalysisData.getEndingValue());
//
//            if (!Double.isNaN(currentVal)) {
//                char col = cellId.charAt(0);
//                String row = cellId.substring(1);
//
//                popUpWindowsHandler.showRuntimeAnalysisPopup(dtoSheetCell, runTimeAnalysisData.getStartingValue(),
//                        runTimeAnalysisData.getEndingValue(), runTimeAnalysisData.getStepValue(),
//                        currentVal, col, row, model, gridController);
////                popUpWindowsHandler.showRuntimeAnalysisPopup(dtoSheetCell, model, gridController);
//            }
//        });
    }

    public void sortRows() {
        var sortRowsData = popUpWindowsHandler.openSortRowsWindow();
        String columns = sortRowsData.getColumnsToSortBy();
        String range = sortRowsData.getRange();

        if (columns != null && !columns.isEmpty() && range != null && !range.isEmpty()) {
            DtoContainerData sortedData = dtoSheetCell.sortSheetCell(range, columns);
            Platform.runLater(() -> popUpWindowsHandler.openSortGridPopUp(sortedData, gridController));
        }
    }

    public void filterGrid() {
        var filterGridData = popUpWindowsHandler.openFilterDataWindow();
        String range = filterGridData.getRange();
        String filterColumn = filterGridData.getColumnsToFilterBy();

        if (range == null || filterColumn == null || filterColumn.isEmpty()) {
            return;
        }

        Map<Character, Set<String>> columnValues = dtoSheetCell.getUniqueStringsInColumn(filterColumn, range);
        Platform.runLater(() -> {
            Map<Character, Set<String>> filter = popUpWindowsHandler.openFilterDataPopUp(columnValues);
            boolean isFilterEmpty = filter.values().stream().allMatch(Set::isEmpty);

            if (!isFilterEmpty) {
                DtoContainerData filteredData = dtoSheetCell.filterSheetCell(range, filter);
                Platform.runLater(() -> popUpWindowsHandler.openFilterGridPopUp(filteredData, gridController));
            }
        });
    }

    private double validateCellValue(String currentValue, int startingValue, int endingValue) {
        try {
            double currentVal = Double.parseDouble(currentValue);
            return (currentVal >= startingValue && currentVal <= endingValue) ? currentVal : startingValue;
        } catch (NumberFormatException e) {
            Platform.runLater(() -> popUpWindowsHandler.createErrorPopup("Cell value must be a number", "Error"));
            return Double.NaN;
        }
    }

    public void applyChangesInParameters(DtoSheetCell dtoSheetCellAsDataParameter, Model model, GridController gridScrollerController) {
        this.dtoSheetCell = dtoSheetCellAsDataParameter;
        this.model = model;
        this.gridController = gridScrollerController;
    }
}