package CoreParts.impl;

import CoreParts.api.Cell;
import CoreParts.api.SheetCell;
import CoreParts.smallParts.CellLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: Implement the SheetCell interface
public class SheetCellImp implements SheetCell
{
    private Map<CellLocation, Cell> sheetCell = new HashMap<>(); // Changed to map of CellLocation to Cell
    int version;
    private static final int maxRows = 50;
    private static final int maxCols = 100;
    private int currentRowLength;
    private int currentColLength;

    public SheetCellImp(int row, int col) {
        if (row > maxRows || col > maxCols) {
            throw new IllegalArgumentException("Row and column numbers exceed maximum allowed limits.");
        }
        currentRowLength = row;
        currentColLength = col;

    }

    @Override
    public SheetCell restoreSheetCell(int versionNumber) {
        return null;
    }

    // Get a cell based on its CellLocation
    // Method to get a cell or create it if it doesn't exist
    @Override
    public Cell getCell(CellLocation location) {
        // If the cell does not exist, create and add it to the map dynamically
        return sheetCell.computeIfAbsent(location, loc -> new CellImp(loc));
    }


    // Helper method to check if a location is valid
    private boolean isValidLocation(CellLocation location) {
        int row = location.getRealRow();
        int col = location.getRealColumn();

        return row >= 0 && row < maxRows && col >= 0 && col < maxCols;
    }

    // Update a cell's value at a specific location
    public void updateCell(CellLocation location, Cell newCell) {
        if (!isValidLocation(location)) {
            throw new IllegalArgumentException("Invalid cell location");
        }
        sheetCell.put(location, newCell);
    }
}
