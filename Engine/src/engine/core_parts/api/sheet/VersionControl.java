package engine.core_parts.api.sheet;

import engine.core_parts.api.Cell;
import dto.small_parts.CellLocation;
import dto.small_parts.EffectiveValue;

import java.util.Map;

public interface VersionControl {
    Map<Integer, Map<CellLocation, EffectiveValue>> getVersions();
    Map<Integer, Map<CellLocation, String>> getOriginalVersions();
    void updateVersions(Cell targetCell);
    void versionControl();
    byte[] saveSheetCellState();
    void clearVersionNumber();
     void updateVersion();
}