package CoreParts.api;

import CoreParts.smallParts.CellLocation;
import Utility.RefDependencyGraph;

import java.util.Map;

public interface SheetCell {

    SheetCell restoreSheetCell(int versionNumber);
    Cell getCell(CellLocation location);
    int getCellLength();
    int getCellWidth();
    int getLatestVersion();
    int getNumberOfRows();
    int getNumberOfColumns();
    String getSheetName();
    void setCell(CellLocation location, Cell cell);
    int getActiveCellsCount();
    Map<CellLocation, Cell> getSheetCell();
    RefDependencyGraph getGraph();
    }
