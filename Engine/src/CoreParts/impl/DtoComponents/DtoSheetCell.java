package CoreParts.impl.DtoComponents;

import CoreParts.api.Cell;
import CoreParts.api.SheetCell;
import CoreParts.impl.InnerSystemComponents.CellImp;
import CoreParts.impl.InnerSystemComponents.SheetCellImp;
import CoreParts.smallParts.CellLocation;
import expression.api.EffectiveValue;

import java.util.*;

public class DtoSheetCell {

    private Map<DtoLocation,EffectiveValue> sheetCell = new HashMap<>();
    private static final int maxRows = 50;
    private static final int maxCols = 20;
    private String name;
    private int versionNumber;
    private int currentNumberOfRows;
    private int currentNumberOfCols;
    private int currentCellLength;
    private int currentCellWidth;

    // Constructor to populate DtoSheetCell from SheetCellImp
    public DtoSheetCell(SheetCellImp sheetCellImp) {
        for (Map.Entry<CellLocation, Cell> entry : sheetCellImp.getSheetCell().entrySet()) {
            sheetCell.put(new DtoLocation(entry.getKey()),entry.getValue().getEffectiveValue().evaluate());
        }
        copyBasicTypes(sheetCellImp);
    }
    public DtoSheetCell(Map<Integer,Map<CellLocation, EffectiveValue>> sheetCellVersions, SheetCell sheetCell, int requestedVersion) {
        Set<DtoLocation> markedLocations = new HashSet<>();
        copyBasicTypes(sheetCell);
        Map<CellLocation, EffectiveValue> sheetCellChanges = sheetCellVersions.get(requestedVersion);
       while (markedLocations.size() < sheetCell.getActiveCellsCount()) {
           for (Map.Entry<CellLocation, EffectiveValue> entry : sheetCellChanges.entrySet()) {
               DtoLocation location = new DtoLocation(entry.getKey());
               if(markedLocations.contains(location)) {
                   continue;
               }
               this.sheetCell.put(location, entry.getValue());
               markedLocations.add(location);
           }
           versionNumber--;
           sheetCellChanges = sheetCellVersions.get(versionNumber);
       }
    }
    public Map<DtoLocation, EffectiveValue> getSheetCell() {
        return sheetCell;
    }
    public void copyBasicTypes(SheetCell sheetCell) {
        this.name = sheetCell.getSheetName();
        this.versionNumber = sheetCell.getLatestVersion();
        this.currentNumberOfRows = sheetCell.getNumberOfRows();
        this.currentNumberOfCols = sheetCell.getNumberOfColumns();
        this.currentCellLength = sheetCell.getCellLength();
        this.currentCellWidth = sheetCell.getCellWidth();
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
    public EffectiveValue getEffectiveValue(DtoLocation dtoLocation) {
        EffectiveValue value = sheetCell.get(dtoLocation);
        return sheetCell.get(dtoLocation);
    }
}
