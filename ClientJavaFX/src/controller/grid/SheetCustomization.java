package controller.grid;

import dto.small_parts.CellLocation;
import javafx.scene.control.Label;

import java.util.HashMap;
import java.util.Map;

public class SheetCustomization {


    private Map<CellLocation, CustomCellLabel> cellLocationToCustomCellLabel = new HashMap<>();
    private Map<String, Integer> columnToWidthDelta = new HashMap<>();
    private Map<String, Integer> rowToHeightDelta = new HashMap<>();
    private Map<CellLocation, Label> cellLocationToLabel = new HashMap<>();


    public Map<CellLocation, Label> getCellLocationToLabel() {
        return cellLocationToLabel;
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
