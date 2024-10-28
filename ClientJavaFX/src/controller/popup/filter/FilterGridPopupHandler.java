package controller.popup.filter;

import controller.grid.GridController;
import controller.popup.GridPopupBase;
import dto.components.DtoContainerData;

public class FilterGridPopupHandler extends GridPopupBase {

    private final GridController gridScrollerController;
    private final DtoContainerData dtoContainerData;

    public FilterGridPopupHandler(GridController gridScrollerController, DtoContainerData dtoContainerData) {
        this.gridScrollerController = gridScrollerController;
        this.dtoContainerData = dtoContainerData;
    }

    public void show() {
        openGridPopUp("Filter Grid", popupGrid ->
                gridScrollerController.initializeFilterPopupGrid(popupGrid, dtoContainerData)
        );
    }
}