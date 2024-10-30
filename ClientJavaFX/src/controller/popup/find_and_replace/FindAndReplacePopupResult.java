package controller.popup.find_and_replace;

import dto.small_parts.CellLocation;

import java.util.List;
import java.util.Set;

public class FindAndReplacePopupResult {

    private boolean isAppliedWasSuccessful;
    Set<CellLocation> locations;
    String newValue;

    // Getters and Setters

    public boolean isAppliedWasSuccessful() {
        return isAppliedWasSuccessful;
    }

    public void setAppliedWasSuccessful(boolean appliedWasSuccessful) {
        isAppliedWasSuccessful = appliedWasSuccessful;
    }

    public Set<CellLocation> getLocations() {
        return locations;
    }

    public void setLocations(Set<CellLocation> locations) {
        this.locations = locations;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    // Constructors

    public FindAndReplacePopupResult(boolean isAppliedWasSuccessful, Set<CellLocation> locations, String newValue) {
        this.isAppliedWasSuccessful = isAppliedWasSuccessful;
        this.locations = locations;
        this.newValue = newValue;
    }
}
