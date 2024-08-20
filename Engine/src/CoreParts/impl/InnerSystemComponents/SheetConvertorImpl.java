package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.api.SheetCell;
import CoreParts.api.SheetConvertor;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import GeneratedClasses.*;

import javax.xml.stream.Location;
import java.util.List;

public class SheetConvertorImpl implements SheetConvertor {
    @Override
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
        SheetCell ourSheet = new SheetCellImp(numOfRows, numOfcoulmns, sheetName, cellLength, cellWidth);
        convertSTLCellListToCellList(stlCellList, ourSheet);  // Method to convert list of cells
        return ourSheet;
    }


    public Cell convertSTLCellToCell(STLCell stlCell)
    {
        String orignalValue = stlCell.getSTLOriginalValue();
        CellLocation cellLocation = CellLocationFactory.fromCellId((stlCell.getColumn().charAt(0)),(char)stlCell.getRow()); // Hypothetical location
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
