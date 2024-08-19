package CoreParts.impl.DtoComponents;

import CoreParts.api.Cell;
import CoreParts.impl.InnerSystemComponents.SheetCellImp;
import CoreParts.smallParts.CellLocation;

import java.util.HashMap;
import java.util.Map;

public class DtoSheetCell {

    private Map<DtoLocation, DtoCell> sheetCell = new HashMap<>();
    private static final int maxRows = 50;
    private static final int maxCols = 20;
    private final String name;
    private int versionNumber;
    private int currentNumberOfRows;
    private int currentNumberOfCols;
    private int currentCellLength;
    private int currentCellWidth;

    // Constructor to populate DtoSheetCell from SheetCellImp
    public DtoSheetCell(SheetCellImp sheetCellImp) {

        for (Map.Entry<CellLocation, Cell> entry : sheetCellImp.getSheetCell().entrySet()) {
            // Convert CellLocation to DtoLocation
            DtoLocation dtoLocation = new DtoLocation(entry.getKey());
            sheetCell.put(dtoLocation, new DtoCell(entry.getValue()));
        }

        this.name = sheetCellImp.getSheetName();
        this.versionNumber = sheetCellImp.getLatestVersion();
        this.currentNumberOfRows = sheetCellImp.getNumberOfRows();
        this.currentNumberOfCols = sheetCellImp.getNumberOfColumns();
        this.currentCellLength = sheetCellImp.getCellLength();
        this.currentCellWidth = sheetCellImp.getCellWidth();
    }

    public Map<DtoLocation, DtoCell> getSheetCell() {
        return sheetCell;
    }



    public int getCellWidth() {
        return currentCellWidth;
    }
    public int getCellLength() {
        return currentCellLength;
    }
    public int getCurrentNumberOfRows() {
        return currentNumberOfRows;
    }
    public int getCurrentNumberOfCols() {
        return currentNumberOfCols;
    }
    public int getVersionNumber() {
        return versionNumber;
    }
    public String getName() {
        return name;
    }
}
