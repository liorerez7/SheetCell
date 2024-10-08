package DtoComponents;


import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCell;
import expression.impl.Range;
import smallParts.CellLocation;
import smallParts.EffectiveValue;



import java.util.*;

public class DtoSheetCell {

    private Map<CellLocation, EffectiveValue> sheetCell = new HashMap<>();
   // private Map<Integer, Map<CellLocation, EffectiveValue>> versionToCellsChanges;
    private Map<String,List<CellLocation>> ranges = new HashMap<>();
    private String name;
    private int versionNumber;
    private int currentNumberOfRows;
    private int currentNumberOfCols;
    private int currentCellLength;
    private int currentCellWidth;

    // Constructor to populate DtoSheetCell from SheetCellImp
    public DtoSheetCell(SheetCell sheetCellImp) {
        for (Map.Entry<CellLocation, Cell> entry : sheetCellImp.getSheetCell().entrySet()) {
            EffectiveValue effectiveValue;
            effectiveValue= entry.getValue().getEffectiveValue().evaluate(sheetCellImp);

            sheetCell.put(entry.getKey(), effectiveValue);
        }
        //versionToCellsChanges = sheetCellImp.getVersions();
        copyBasicTypes(sheetCellImp);
        Set<Range> systemRanges = sheetCellImp.getSystemRanges();
        systemRanges.forEach(range -> {
            ranges.put(range.getRangeName(),range.getCellLocations());
        });
    }

    public DtoSheetCell(Map<CellLocation,EffectiveValue> sheetCell, Map<String, List<CellLocation>> ranges,
                        String name, int versionNumber, int currentNumberOfRows, int currentNumberOfCols, int currentCellLength, int currentCellWidth) {

        this.sheetCell = sheetCell;
        this.ranges = ranges;
        this.name = name;
        this.versionNumber = versionNumber;
        this.currentNumberOfRows = currentNumberOfRows;
        this.currentNumberOfCols = currentNumberOfCols;
        this.currentCellLength = currentCellLength;
        this.currentCellWidth = currentCellWidth;
    }

    public DtoSheetCell(SheetCell sheetCell, int requestedVersion) {

        Set<CellLocation> markedLocations = new HashSet<>();
        copyBasicTypes(sheetCell);
        Map<CellLocation, EffectiveValue> sheetCellChanges = sheetCell.getVersions().get(requestedVersion);

        while (requestedVersion > 0) {
            for (Map.Entry<CellLocation, EffectiveValue> entry : sheetCellChanges.entrySet()) {
                CellLocation location = entry.getKey();
                if(markedLocations.contains(location)) {
                    continue;
                }
                this.sheetCell.put(location, entry.getValue());
                markedLocations.add(location);
            }

            requestedVersion--;
            sheetCellChanges = sheetCell.getVersions().get(requestedVersion);
        }
    }

    public Map<CellLocation, EffectiveValue> getViewSheetCell() {
        return sheetCell;
    }

    public Map<String,List<CellLocation>> getRanges() {
        return ranges;
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

    public int getNumberOfRows() {
        return currentNumberOfRows;
    }

    public int getNumberOfColumns() {
        return currentNumberOfCols;
    }

    public String getSheetName() {
        return name;
    }

    public int getLatestVersion() {
        return versionNumber;
    }

    public int getCellLength() {
        return currentCellLength;
    }

    public EffectiveValue getEffectiveValue(CellLocation cellLocation) {
        return sheetCell.get(cellLocation);
    }

//    public Map<Integer, Map<CellLocation, EffectiveValue>> getVersionToCellsChanges() {
//        return versionToCellsChanges;
//    }

}
