package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCell;
import CoreParts.api.SheetConvertor;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import GeneratedClassesEx2.*;
import expression.impl.Range;
import expression.impl.Ref;
import Utility.CellUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SheetConvertorImpl implements SheetConvertor {
    final static int MAX_ROWS = 50;
    final static int MAX_COLUMNS = 20;

    private void checkRowsAndColumns(STLLayout stlLayout) {
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
        int cellLength = stlSize.getRowsHeightUnits();

        List<STLCell> stlCellList = cells.getSTLCell();
        checkRowsAndColumns(stlLayout);

        STLRanges stlRanges = null;
        Set<Range> ourRanges = null;

        stlRanges = sheet.getSTLRanges();

        if(stlRanges != null)
        {
            List<STLRange> stlRangeList = stlRanges.getSTLRange();
            validateRanges(stlRangeList, numOfRows, numOfcoulmns); // implement
            ourRanges = convertSTLRangesToRanges(stlRangeList, numOfRows, numOfcoulmns);  // implement

        }

        SheetCell ourSheet = new SheetCellImp(numOfRows, numOfcoulmns, sheetName, cellLength, cellWidth, ourRanges);
        convertSTLCellListToCellList(stlCellList, ourSheet);  // Method to convert list of cells
        return ourSheet;
    }

    // Validates ranges within the sheet
    private void validateRanges(List<STLRange> stlRangeList, int numOfRows, int numOfColumns) {
        for (STLRange range : stlRangeList) {
            STLBoundaries boundaries = range.getSTLBoundaries();
            String from = boundaries.getFrom();
            String to = boundaries.getTo();

            if (!CellUtils.isWithinBounds(from, to, numOfRows, numOfColumns)) {
                throw new IllegalArgumentException("Range boundaries are out of bounds.");
            }
        }
    }

    // Converts STLRange list to Range set
    private Set<Range> convertSTLRangesToRanges(List<STLRange> stlRangeList, int numOfRows, int numOfColumns) {
        Set<Range> ranges = new HashSet<>();
        for (STLRange stlRange : stlRangeList) {
            String rangeName = stlRange.getName();
            Set<Ref> rangeRefs = getRangeRefsFromBoundaries(stlRange.getSTLBoundaries(), numOfRows, numOfColumns);
            ranges.add(new Range(rangeRefs, rangeName));
        }
        return ranges;
    }

    // Retrieves range references from STLBoundaries
    private Set<Ref> getRangeRefsFromBoundaries(STLBoundaries boundaries, int numOfRows, int numOfColumns) {
        Set<Ref> rangeRefs = new HashSet<>();
        String from = boundaries.getFrom();
        String to = boundaries.getTo();

        Set<CellLocation> cellsInRange = CellUtils.getCellsInRange(from, to, numOfRows, numOfColumns);
        for (CellLocation cell : cellsInRange) {
            rangeRefs.add(new Ref(cell));
        }
        return rangeRefs;
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
}
