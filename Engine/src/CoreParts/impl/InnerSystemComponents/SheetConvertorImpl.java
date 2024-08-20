package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.api.SheetCell;
import CoreParts.api.SheetConvertor;
import CoreParts.smallParts.CellLocation;
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
        List<Cell> OurCellList = convertSTLCellListToCellList(stlCellList);
        return ourSheet;
    }
    private STLSize getSTLsize (STLSize stlSize) {
    }

    public Cell convertSTLCellToCell(STLCell stlCell)
    {
        String orignalValue = stlCell.getSTLOriginalValue();
        CellLocation cellLocation = new CellLocation((stlCell.getColumn().charAt(0)),(char)stlCell.getRow()); // Hypothetical location
        Cell ourCell = new CellImp(cellLocation, orignalValue);
    }

    public List<Cell> convertSTLCellListToCellList(List<STLCell> stlCellList) {
        for (STLCell stlCell : stlCellList) {  // Hypothetical method to get list of cells
            CellLocation location = CellLocation.fromCellId(stlCell.getColumn().charAt(0), (char) stlCell.getRow()); // CellLocation((char)stlCell.getRow(), stlCell.getColumn().charAt(0));  // Hypothetical methods
            Cell cell = convertSTLCellToCell(stlCell);  // Method to convert individual cells
        }
        return List.of();
    }

    @Override
    public STLSheet convertSheet(SheetCell sheet) {
        return null;
    }
}
