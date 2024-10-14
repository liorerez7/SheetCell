package dto.components;



import dto.small_parts.CellLocation;

import java.util.Map;

public class DtoContainerData {

    private DtoSheetCell dtoSheetCell;
    private Map<CellLocation, CellLocation> afterSortCellLocationToOldCellLocation;
    private char leftColumnRange;
    private char rightColumnRange;
    private int upperRowRange;
    private int lowerRowRange;
    private int numberOfRows;
    private int numberOfColumns;

    public DtoContainerData(DtoSheetCell dtoSheetCell, Map<CellLocation, CellLocation> afterSortCellLocationToOldCellLocation) {
        this.dtoSheetCell = dtoSheetCell;
        this.afterSortCellLocationToOldCellLocation = afterSortCellLocationToOldCellLocation;
        extractDataFromCellLocations(afterSortCellLocationToOldCellLocation);
    }

    public DtoSheetCell getDtoSheetCell() {
        return dtoSheetCell;
    }

    public Map<CellLocation, CellLocation> getOldCellLocationToAfterSortCellLocation() {
        return afterSortCellLocationToOldCellLocation;
    }

    public CellLocation getOldCellLocation(CellLocation cellLocation) {

        return afterSortCellLocationToOldCellLocation.get(cellLocation);
    }

    private void extractDataFromCellLocations(Map<CellLocation, CellLocation> afterSortCellLocationToOldCellLocation) {
        // Check if the map is empty
        if (afterSortCellLocationToOldCellLocation.isEmpty()) {
            throw new IllegalArgumentException("The map is empty. Cannot extract data.");
        }

        // Initialize variables to hold the range values
        CellLocation firstCell = null;
        CellLocation lastCell = null;

        // Iterate through the map keys (new cell locations) to determine ranges
        for (CellLocation newCell : afterSortCellLocationToOldCellLocation.keySet()) {
            // Set firstCell and lastCell for the first iteration
            if (firstCell == null) {
                firstCell = newCell;
                lastCell = newCell;
            }

            // Compare for the leftmost and rightmost columns
            if (newCell.getVisualColumn() < firstCell.getVisualColumn()) {
                firstCell = newCell;
            }
            if (newCell.getVisualColumn() > lastCell.getVisualColumn()) {
                lastCell = newCell;
            }

            // Compare for the uppermost and lowermost rows (real rows)
            if (newCell.getRealRow() < firstCell.getRealRow()) {
                firstCell = newCell;
            }
            if (newCell.getRealRow() > lastCell.getRealRow()) {
                lastCell = newCell;
            }
        }

        // Extract the range and number of rows/columns
        leftColumnRange = firstCell.getVisualColumn();
        rightColumnRange = lastCell.getVisualColumn();
        upperRowRange = firstCell.getRealRow();
        lowerRowRange = lastCell.getRealRow();
        numberOfRows = lowerRowRange - upperRowRange + 1;
        numberOfColumns = rightColumnRange - leftColumnRange + 1;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }
    public int getNumberOfRows() {
        return numberOfRows;
    }
    public char getLeftColumnRange() {
        return leftColumnRange;
    }
    public char getRightColumnRange() {
        return rightColumnRange;
    }
    public int getUpperRowRange() {
        return upperRowRange;
    }
    public int getLowerRowRange() {
        return lowerRowRange;
    }
}
