package CoreParts.api;

import CoreParts.smallParts.CellLocation;
import Utility.RefDependencyGraph;

import java.util.Map;

public interface SheetCellViewOnly {
    Cell getCell(CellLocation location);
    int getCellLength();
    int getCellWidth();
    int getLatestVersion();
    int getNumberOfRows();
    int getNumberOfColumns();
    String getSheetName();
    int getActiveCellsCount();
    Map<CellLocation, Cell> getSheetCell();
    RefDependencyGraph getGraph();
}
