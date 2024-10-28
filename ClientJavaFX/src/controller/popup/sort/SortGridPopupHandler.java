package controller.popup.sort;


import controller.grid.GridController;
import controller.popup.GridPopupBase;
import dto.components.DtoContainerData;


public class SortGridPopupHandler extends GridPopupBase {

    private final GridController gridScrollerController;
    private final DtoContainerData dtoContainerData;

    public SortGridPopupHandler(GridController gridScrollerController, DtoContainerData dtoContainerData) {
        this.gridScrollerController = gridScrollerController;
        this.dtoContainerData = dtoContainerData;
    }

    public void show() {
        openGridPopUp("Sorted Rows", popupGrid ->
                gridScrollerController.initializeSortPopupGrid(popupGrid, dtoContainerData)
        );
    }
}