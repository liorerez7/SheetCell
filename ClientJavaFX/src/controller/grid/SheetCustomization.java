package controller.grid;

import dto.small_parts.CellLocation;

import java.util.HashMap;
import java.util.Map;

public class SheetCustomization {


    private Map<CellLocation, CustomCellLabel> cellLocationToCustomCellLabel = new HashMap<>();
    private Map<String, Integer> columnToWidthDelta = new HashMap<>();
    private Map<String, Integer> rowToHeightDelta = new HashMap<>();

    public SheetCustomization(Map<CellLocation, CustomCellLabel> cellLocationToCustomCellLabel,
                              Map<String, Integer> columnToWidthDelta,
                              Map<String, Integer> rowToHeightDelta) {
        this.cellLocationToCustomCellLabel = cellLocationToCustomCellLabel;
        this.columnToWidthDelta = columnToWidthDelta;
        this.rowToHeightDelta = rowToHeightDelta;
    }

    public SheetCustomization(Map<CellLocation, CustomCellLabel> cellLocationToCustomCellLabel) {
        this.cellLocationToCustomCellLabel = cellLocationToCustomCellLabel;
    }

    public Map<CellLocation, CustomCellLabel> getCellLocationToCustomCellLabel() {
        return cellLocationToCustomCellLabel;
    }

    public Map<String, Integer> getColumnToWidthDelta() {
        return columnToWidthDelta;
    }

    public Map<String, Integer> getRowToHeightDelta() {
        return rowToHeightDelta;
    }


}
