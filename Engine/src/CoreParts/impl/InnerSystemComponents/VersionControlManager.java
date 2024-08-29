package CoreParts.impl.InnerSystemComponents;

import CoreParts.api.Cell;
import CoreParts.smallParts.CellLocation;
import expression.api.EffectiveValue;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class VersionControlManager implements Serializable {
    private Map<Integer, Map<CellLocation, EffectiveValue>> versionToCellsChanges;
    private SheetCellImp sheetCell;

    public VersionControlManager(Map<Integer, Map<CellLocation, EffectiveValue>> versionToCellsChanges, SheetCellImp sheetCell) {
        this.versionToCellsChanges = versionToCellsChanges;
        this.sheetCell = sheetCell;
    }

    public void setSheetCell(SheetCellImp sheetCell) {
        this.sheetCell = sheetCell;
    }
    public void setVersionToCellsChanges(Map<Integer, Map<CellLocation, EffectiveValue>> versionToCellsChanges) {
        this.versionToCellsChanges = versionToCellsChanges;
    }
    public void versionControl() {
        int sheetCellLatestVersion = sheetCell.getLatestVersion();
        versionToCellsChanges.put(sheetCellLatestVersion, new HashMap<>());
        Map<CellLocation, EffectiveValue> changedCells = versionToCellsChanges.get(sheetCellLatestVersion);
        for (Map.Entry<CellLocation, Cell> entry : sheetCell.getSheetCell().entrySet()) {
            CellLocation location = entry.getKey();
            Cell cell = entry.getValue();
            // Check if the cell's latest version matches the sheet's latest version
            if (cell.getLatestVersion() == sheetCellLatestVersion)   // Assuming Cell has a getVersion() method// Replace with your logic to calculate the effective value
                changedCells.put(location, cell.getEffectiveValue().evaluate(sheetCell));
        }
    }
    public byte[] saveVersions() throws IllegalStateException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(versionToCellsChanges);
            oos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save the versions state", e);
        }
    }

    public void restoreVersions(byte[] savedVersions) throws IllegalStateException {
        try {
            if (savedVersions != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(savedVersions);
                ObjectInputStream ois = new ObjectInputStream(bais);
                versionToCellsChanges = (Map<Integer, Map<CellLocation, EffectiveValue>>) ois.readObject();
            }
        } catch (Exception restoreEx) {
            throw new IllegalStateException("Failed to restore the versions state", restoreEx);
        }
    }
    private void updateVersions(Cell targetCell) {
        sheetCell.updateVersion();
        targetCell.updateVersion(sheetCell.getLatestVersion());
    }
}


