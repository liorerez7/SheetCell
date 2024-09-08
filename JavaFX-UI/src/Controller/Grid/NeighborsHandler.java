package Controller.Grid;

import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.smallParts.CellLocation;
import javafx.scene.control.Label;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NeighborsHandler {
    Label cellLabel;

    public void showNeighbors(DtoCell cell, Map<CellLocation, Label> cellLocationToLabel) {

        Set<CellLocation> newAffectedBy = null;
        Set<CellLocation> newAffectingOn = null;

        if (cell != null) {
            newAffectedBy = cell.getAffectedBy();
            newAffectingOn = cell.getAffectingOn();
        }

        // Highlight the neighbors
        if (newAffectingOn != null)
            newAffectingOn.forEach(location -> cellLocationToLabel.get(location).getStyleClass().add("affects-target"));
        if (newAffectedBy != null)
            newAffectedBy.forEach(location -> cellLocationToLabel.get(location).getStyleClass().add("affected-by-source"));

    }



    public void showAffectedCells(List<CellLocation> requestedRange, Map<CellLocation, Label> cellLocationToLabel) {
        requestedRange.forEach(location ->
                cellLocationToLabel.get(location).getStyleClass().add("affected-by-source"));
    }

    // This method clears any existing highlights from all cells
    public void clearAllHighlights(Map<CellLocation, Label> cellLocationToLabel) {
        cellLocationToLabel.values().forEach(label -> {
            label.getStyleClass().remove("affects-target");
            label.getStyleClass().remove("affected-by-source");
        });
    }
}














//public void showNeighbors(DtoCell cell, Map<CellLocation, Label> cellLocationToLabel) {
//
//        Set<CellLocation> newAffectedBy = null;
//        Set<CellLocation> newAffectingOn = null;
//
//        if(cell!=null) {
//            newAffectedBy = cell.getAffectedBy();
//            newAffectingOn = cell.getAffectingOn();
//        }
//        if (newAffectingOn!=null)
//            newAffectingOn.forEach(location -> cellLocationToLabel.get(location).getStyleClass().add("affects-target"));
//        if (newAffectedBy!=null)
//            newAffectedBy.forEach(location -> cellLocationToLabel.get(location).getStyleClass().add("affected-by-source"));
//        if (oldAffectingOn!=null)
//            oldAffectingOn.forEach(location -> cellLocationToLabel.get(location).getStyleClass().remove("affects-target"));
//        if (oldAffectedBy!=null)
//            oldAffectedBy.forEach(location -> cellLocationToLabel.get(location).getStyleClass().remove("affected-by-source"));
//
//        oldAffectedBy = newAffectedBy;
//        oldAffectingOn = newAffectingOn;
//    }