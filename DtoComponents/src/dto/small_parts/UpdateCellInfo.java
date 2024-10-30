package dto.small_parts;

import java.util.Set;

public class UpdateCellInfo {

    String previousValue;
    String newValue;
    String previousOriginalValue;
    String newOriginalValue;
    int versionNumberThatItWasChanged;
    String newUserName;
    Set<CellLocation> locations;
    boolean changedInReplaceFunction = false;

    public UpdateCellInfo(String previousValue, String newValue, String previousOriginalValue, String newOriginalValue
            , int versionNumberThatItWasChanged, String newUserName, Set<CellLocation> locations) {

        this.previousValue = previousValue;
        this.newValue = newValue;
        this.previousOriginalValue = previousOriginalValue;
        this.newOriginalValue = newOriginalValue;
        this.versionNumberThatItWasChanged = versionNumberThatItWasChanged;
        this.newUserName = newUserName;
        this.locations = locations;
    }

    public UpdateCellInfo(String previousValue, String newValue, String previousOriginalValue, String newOriginalValue
            , int versionNumberThatItWasChanged, String newUserName, Set<CellLocation> locations, boolean changedInReplaceFunction) {

        this.previousValue = previousValue;
        this.newValue = newValue;
        this.previousOriginalValue = previousOriginalValue;
        this.newOriginalValue = newOriginalValue;
        this.versionNumberThatItWasChanged = versionNumberThatItWasChanged;
        this.newUserName = newUserName;
        this.locations = locations;
        this.changedInReplaceFunction = changedInReplaceFunction;
    }

    public boolean isChangedInReplaceFunction() {
        return changedInReplaceFunction;
    }

    public void setChangedInReplaceFunction(boolean changedInReplaceFunction) {
        this.changedInReplaceFunction = changedInReplaceFunction;
    }

    // create get methods for all fields
    public String getPreviousValue() {
        return previousValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getPreviousOriginalValue() {
        return previousOriginalValue;
    }

    public String getNewOriginalValue() {
        return newOriginalValue;
    }

    public int getVersionNumberThatItWasChanged() {
        return versionNumberThatItWasChanged;
    }

    public String getNewUserName() {
        return newUserName;
    }

    public Set<CellLocation> getLocations() {
        return locations;
    }
}
