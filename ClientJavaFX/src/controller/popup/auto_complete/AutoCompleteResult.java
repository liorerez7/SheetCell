package controller.popup.auto_complete;

import dto.small_parts.CellLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoCompleteResult {

    List<String> oldCellLocations;
    List<String> newCellLocations;
    Map<String, String> cellLocationToOldCellValues = new HashMap<>();
    Map<String, String> cellLocationToNewCellValues = new HashMap<>();
    boolean isAppliedWasSuccessful;

    // Getters and Setters

    public List<String> getOldCellLocations() {
        return oldCellLocations;
    }

    public void setOldCellLocations(List<String> oldCellLocations) {
        this.oldCellLocations = oldCellLocations;
    }

    public List<String> getNewCellLocations() {
        return newCellLocations;
    }

    public void setNewCellLocations(List<String> newCellLocations) {
        this.newCellLocations = newCellLocations;
    }

    public Map<String, String> getCellLocationToOldCellValues() {
        return cellLocationToOldCellValues;
    }

    public void setCellLocationToOldCellValues(Map<String, String> cellLocationToOldCellValues) {
        this.cellLocationToOldCellValues = cellLocationToOldCellValues;
    }

    public Map<String, String> getCellLocationToNewCellValues() {
        return cellLocationToNewCellValues;
    }

    public void setCellLocationToNewCellValues(Map<String, String> cellLocationToNewCellValues) {
        this.cellLocationToNewCellValues = cellLocationToNewCellValues;
    }

    public boolean isAppliedWasSuccessful() {
        return isAppliedWasSuccessful;
    }

    public void setAppliedWasSuccessful(boolean appliedWasSuccessful) {
        isAppliedWasSuccessful = appliedWasSuccessful;
    }

    // Constructors

    public AutoCompleteResult(List<String> oldCellLocations, List<String> newCellLocations,
                              Map<String, String> cellLocationToOldCellValues,
                              Map<String, String> cellLocationToNewCellValues, boolean isAppliedWasSuccessful) {

        this.oldCellLocations = oldCellLocations;
        this.newCellLocations = newCellLocations;
        this.cellLocationToOldCellValues = cellLocationToOldCellValues;
        this.cellLocationToNewCellValues = cellLocationToNewCellValues;
        this.isAppliedWasSuccessful = isAppliedWasSuccessful;
    }
}
