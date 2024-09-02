package CoreParts.impl.DtoComponents;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCell;
import CoreParts.api.sheet.SheetCellViewOnly;
import CoreParts.smallParts.CellLocation;
import Utility.RefDependencyGraph;
import expression.ReturnedValueType;
import expression.api.EffectiveValue;

import java.util.*;

public class DtoSheetCell implements SheetCellViewOnly {

    private Map<CellLocation,EffectiveValue> sheetCell = new HashMap<>();
    private Map<Integer, Map<CellLocation, EffectiveValue>> versionToCellsChanges;

    private static final int maxRows = 50;
    private static final int maxCols = 20;
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

            effectiveValue = entry.getValue().getActualValue();


            //TODO: Check if this is correct
            //effectiveValue= entry.getValue().getEffectiveValue().evaluate(sheetCellImp);

            sheetCell.put(entry.getKey(), effectiveValue);
        }
        versionToCellsChanges = sheetCellImp.getVersions();
        copyBasicTypes(sheetCellImp);
    }

    public Map<Integer, Map<CellLocation, EffectiveValue>> getVersionToCellsChanges() {
        return versionToCellsChanges;
    }

    public DtoSheetCell(SheetCell sheetCell, int requestedVersion) {


        if (sheetCell.getLatestVersion() < requestedVersion) {
            throw new IllegalArgumentException("Requested version is not available latest version is " + sheetCell.getLatestVersion());
        }
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

    @Override
    public Cell getCell(CellLocation location) {
        return null;
    }

    @Override
    public int getActiveCellsCount() {
        return 0;
    }


    /* NEED TO CHANGE! */
    @Override
    public Map<CellLocation, Cell> getSheetCell() {
        return null;
    }

    @Override
    public RefDependencyGraph getGraph() {
        return null;
    }

    @Override
    public boolean isCellPresent(CellLocation location) {
        return false;
    }

    @Override
    public Map<CellLocation, EffectiveValue> getViewSheetCell() {
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

    @Override
    public int getNumberOfRows() {
        return currentNumberOfRows;
    }

    @Override
    public int getNumberOfColumns() {
        return currentNumberOfCols;
    }

    @Override
    public String getSheetName() {
        return name;
    }

    @Override
    public int getLatestVersion() {
        return versionNumber;
    }

    public int getCellLength() {
        return currentCellLength;
    }

    public EffectiveValue getEffectiveValue(CellLocation cellLocation) {
        return sheetCell.get(cellLocation);
    }

}
