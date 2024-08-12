package CoreParts.impl;

import java.util.ArrayList;
import java.util.List;

public class SheetCellImp implements SheetCell
{
    private List<List<Character>> array = new ArrayList<>();
    int version;
    private final int maxRows = 5;
    private final int maxCols = 4;


    public SheetCellImp(int row, int col) {
        // Initialize the outer list with 5 inner lists
        for (int i = 0; i < row; i++) {
            List<Character> rowNumber = new ArrayList<>();
            // Initialize each inner list with 4 'null' characters
            for (int j = 0; j < col; j++) {
                rowNumber.add(' '); // or you can initialize with a specific character
            }

            array.add(rowNumber);
        }
    }


    @Override
    public SheetCell restoreSheetCell(int versionNumber) {
        return null;
    }
}
