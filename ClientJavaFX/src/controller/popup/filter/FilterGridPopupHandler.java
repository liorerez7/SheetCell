package controller.popup.filter;

import controller.grid.GridController;
import controller.popup.GridPopupBase;
import dto.components.DtoContainerData;
import dto.components.DtoSheetCell;

public class FilterGridPopupHandler extends GridPopupBase {

    private final GridController gridScrollerController;
    private final DtoContainerData dtoContainerData;
    private final DtoSheetCell dtoSheetCell;

    public FilterGridPopupHandler(GridController gridScrollerController, DtoContainerData dtoContainerData) {
        this.gridScrollerController = gridScrollerController;
        this.dtoContainerData = dtoContainerData;
        dtoSheetCell = null;
    }

    public FilterGridPopupHandler(GridController gridScrollerController, DtoSheetCell dtoSheetCell) {
        this.gridScrollerController = gridScrollerController;
        this.dtoSheetCell = dtoSheetCell;
        dtoContainerData = null;
    }

    public void show() {
        openGridPopUp("Filter Grid", popupGrid ->
                gridScrollerController.initializeFilterPopupGrid(popupGrid, dtoContainerData)
        );
    }

    public void showOriginalGrid(){
        openGridPopUp("Filter Grid", popupGrid ->
                gridScrollerController.initializeOriginalPopupGrid(popupGrid, dtoSheetCell)
        );
    }
}