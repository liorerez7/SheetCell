package dto.small_parts;

import dto.components.DtoCell;

public class UpdateCellInfo {

    String previousValue;
    String newValue;
    String previousOriginalValue;
    String newOriginalValue;
    int versionNumberThatItWasChanged;
    String newUserName;
    CellLocation location;

//    public UpdateCellInfo(DtoCell newCell, DtoCell previousCell, int version, String newUsername) {
//        this.newValue = newCell.getEffectiveValue().getValue();
//        this.newOriginalValue = newCell.getOriginalValue();
//        this.versionNumberThatItWasChanged = version;
//        this.newUserName = newUsername;
//        this.location = newCell.getLocation();
//
//        if(previousCell == null){
//            this.previousValue = null;
//            this.previousOriginalValue = null;
//        }else{
//            this.previousValue = previousCell.getEffectiveValue().getValue();
//            this.previousOriginalValue = previousCell.getOriginalValue();
//        }
//    }

    public UpdateCellInfo(String previousValue, String newValue, String previousOriginalValue, String newOriginalValue
            , int versionNumberThatItWasChanged, String newUserName, CellLocation location) {

        this.previousValue = previousValue;
        this.newValue = newValue;
        this.previousOriginalValue = previousOriginalValue;
        this.newOriginalValue = newOriginalValue;
        this.versionNumberThatItWasChanged = versionNumberThatItWasChanged;
        this.newUserName = newUserName;
        this.location = location;
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

    public CellLocation getLocation() {
        return location;
    }
}
