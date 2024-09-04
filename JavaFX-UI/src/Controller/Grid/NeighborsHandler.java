package Controller.Grid;

import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.smallParts.CellLocation;
import javafx.scene.control.Label;

import java.util.Map;
import java.util.Set;

public class NeighborsHandler {
    Label cellLabel;
    private Set<CellLocation> oldAffectedBy;
    private Set<CellLocation> oldAffectingOn;

    public void setOldAffectedBy(Set<CellLocation> oldAffectedBy) {
        this.oldAffectedBy = oldAffectedBy;
    }

    public void setOldAffectingOn(Set<CellLocation> oldAffectingOn) {
        this.oldAffectingOn = oldAffectingOn;
    }

    public void showNeighbors(DtoCell cell, Map<CellLocation, Label> cellLocationToLabel) {

        Set<CellLocation> newAffectedBy = null;
        Set<CellLocation> newAffectingOn = null;
        if(cell!=null) {
            newAffectedBy = cell.getAffectedBy();
            newAffectingOn = cell.getAffectingOn();
        }
        if (newAffectingOn!=null)
            newAffectingOn.forEach(location -> cellLocationToLabel.get(location).getStyleClass().add("affects-target"));
        if (newAffectedBy!=null)
            newAffectedBy.forEach(location -> cellLocationToLabel.get(location).getStyleClass().add("affected-by-source"));
        if (oldAffectingOn!=null)
            oldAffectingOn.forEach(location -> cellLocationToLabel.get(location).getStyleClass().remove("affects-target"));
        if (oldAffectedBy!=null)
            oldAffectedBy.forEach(location -> cellLocationToLabel.get(location).getStyleClass().remove("affected-by-source"));

        oldAffectedBy = newAffectedBy;
        oldAffectingOn = newAffectingOn;
    }
}
