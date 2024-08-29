package CoreParts.api;

import CoreParts.smallParts.CellLocation;
import Utility.RefDependencyGraph;

import java.io.Serializable;
import java.util.Map;

public interface SheetCell extends SheetCellViewOnly, Serializable {

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
    void clearVersionNumber();
    void updateVersion();
    boolean isCellPresent(CellLocation location);
    void createRefDependencyGraph();
    RefDependencyGraph getRefDependencyGraph();
    void updateEffectedByAndOnLists();
    void removeCell(CellLocation location);
    }
