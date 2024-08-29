package CoreParts.api;

import CoreParts.smallParts.CellLocation;
import Utility.RefDependencyGraph;
import expression.api.EffectiveValue;
import expression.api.Expression;

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
     Map<Integer, Map<CellLocation, EffectiveValue>> getVersions();
     byte[] saveSheetCellState();
    void setUpSheet();
     void updateVersions(Cell targetCell);
     void versionControl();
     void performGraphOperations();
    void applyCellUpdates(Cell targetCell, String newValue, Expression expression);
}
