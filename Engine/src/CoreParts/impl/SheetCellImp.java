package CoreParts.impl;

import CoreParts.interfaces.SheetCell;
import CoreParts.smallParts.CellLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SheetCellImp implements SheetCell
{
    private List<List<CellImp>> sheetCell= new ArrayList<>();
    int version;
    private static final int maxRows = 50;
    private static final int maxCols = 100;
    private int currentRowLength;
    private int currentColLength;

    public SheetCellImp(int row, int col) {
        // Initialize the outer list with 5 inner lists
        for (int i = 0; i < row; i++) {
            List<CellImp> rowNumber = new ArrayList<>();
            // Initialize each inner list with 4 'null' characters
            for (int j = 0; j < col; j++) {
                CellLocation location = new CellLocation((char) ('A' + j), (char) ('1' + i));
                rowNumber.add(new CellImp(location)); // or you can initialize with a specific character
            }

            sheetCell.add(rowNumber);
        }
        currentRowLength = row;
        currentColLength = col;
    }


    @Override
    public SheetCell restoreSheetCell(int versionNumber) {
        return null;
    }

    public CellImp getCell(CellLocation location) {
        int row = location.getRealRow();
        int col = location.getRealColumn();
        if (row < 0 || row >= maxRows || col < 0 || col >= maxCols) {
            throw new IllegalArgumentException("Invalid cell location");
        }
        return sheetCell.get(row).get(col);
    }
}
