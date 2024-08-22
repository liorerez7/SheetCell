package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.api.SheetCell;
import CoreParts.api.SheetConvertor;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import GeneratedClasses.*;
import Utility.CellUtils;

import javax.xml.stream.Location;
import java.util.List;

public class SheetConvertorImpl implements SheetConvertor {
    final static int MAX_ROWS = 50;
    final static int MAX_COLUMNS = 20;
    private void chechRowsAndColumns(STLLayout stlLayout) {
        boolean valid = (stlLayout.getColumns() > 0 && stlLayout.getRows() > 0) && (stlLayout.getColumns() <= MAX_COLUMNS && stlLayout.getRows() <= MAX_ROWS);
        if(!valid)
            throw new IllegalArgumentException("Invalid rows and columns values Max 20x50");
    }
    public SheetCell convertSheet(STLSheet sheet) {
         STLCells cells = sheet.getSTLCells();
         String sheetName = sheet.getName();
         STLLayout stlLayout = sheet.getSTLLayout();
         STLSize stlSize = stlLayout.getSTLSize();
         int numOfRows = stlLayout.getRows();
         int numOfcoulmns = stlLayout.getColumns();
        int cellWidth = stlSize.getColumnWidthUnits();
         int cellLength = stlSize.getRowsHeightUnits();// Hypothetical method
        List<STLCell> stlCellList = cells.getSTLCell();
        chechRowsAndColumns(stlLayout);
        SheetCell ourSheet = new SheetCellImp(numOfRows, numOfcoulmns, sheetName, cellLength, cellWidth);
        convertSTLCellListToCellList(stlCellList, ourSheet);  // Method to convert list of cells
        return ourSheet;
    }
    public Cell convertSTLCellToCell(STLCell stlCell)
    {
        String orignalValue = stlCell.getSTLOriginalValue();
        char column = stlCell.getColumn().charAt(0);
        int row = stlCell.getRow();
       // CellUtils.isWithinLocationBounds(column - 'A',row - '1',MAX_COLUMNS,MAX_ROWS);
        CellLocation cellLocation = CellLocationFactory.fromCellId(column,String.valueOf(row)); // Hypothetical location

        Cell ourCell = new CellImp(cellLocation, orignalValue);
        return ourCell;
    }

    public void convertSTLCellListToCellList(List<STLCell> stlCellList, SheetCell ourSheet) {
        for (STLCell stlCell : stlCellList) {  // Hypothetical method to get list of cells
            Cell cell = convertSTLCellToCell(stlCell);  // Method to convert individual cells65
            ourSheet.setCell(cell.getLocation(), cell);
        }
    }

    @Override
    public STLSheet convertSheet(SheetCell sheet) {
        return null;
    }
}
