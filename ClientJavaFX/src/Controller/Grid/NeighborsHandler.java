//package Controller.Grid;
//
//import CoreParts.impl.DtoComponents.DtoCell;
//import CoreParts.smallParts.CellLocation;
//import javafx.geometry.Pos;
//import javafx.scene.control.Label;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//public class NeighborsHandler {
//    Label cellLabel;
//
//    public void showNeighbors(DtoCell cell, Map<CellLocation, Label> cellLocationToLabel) {
//
//        Set<CellLocation> newAffectedBy = null;
//        Set<CellLocation> newAffectingOn = null;
//
//        if (cell != null) {
//            newAffectedBy = cell.getAffectedBy();
//            newAffectingOn = cell.getAffectingOn();
//        }
//
//        // Highlight the neighbors
//        if (newAffectingOn != null)
//            newAffectingOn.forEach(location -> cellLocationToLabel.get(location).getStyleClass().add("affects-target"));
//        if (newAffectedBy != null)
//            newAffectedBy.forEach(location -> cellLocationToLabel.get(location).getStyleClass().add("affected-by-source"));
//
//    }
//
//    public void showAffectedCells(List<CellLocation> requestedRange, Map<CellLocation, Label> cellLocationToLabel) {
//        requestedRange.forEach(location ->
//                cellLocationToLabel.get(location).getStyleClass().add("affected-by-source"));
//    }
//
//    // This method clears any existing highlights from all cells
//    public void clearAllHighlights(Map<CellLocation, Label> cellLocationToLabel) {
//        cellLocationToLabel.values().forEach(label -> {
//            label.getStyleClass().remove("affects-target");
//            label.getStyleClass().remove("affected-by-source");
//        });
//    }
//
//
//
//
//
//}


package Controller.Grid;


import DtoComponents.DtoCell;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import smallParts.CellLocation;
import smallParts.CellLocationFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NeighborsHandler {
    Label cellLabel;

    public void showNeighbors(DtoCell cell, Map<CellLocation, Label> cellLocationToLabel, Map<CellLocation, CustomCellLabel> cellLocationToCustomCellLabel) {

        Set<CellLocation> newAffectedBy = null;
        Set<CellLocation> newAffectingOn = null;

        if (cell != null) {
            newAffectedBy = cell.getAffectedBy();
            newAffectingOn = cell.getAffectingOn();
        }

        // Highlight the neighbors
        if (newAffectingOn != null) {
            newAffectingOn.forEach(location -> {
                Label label = cellLocationToLabel.get(location);
                CustomCellLabel customCellLabel = cellLocationToCustomCellLabel.get(location);
                customCellLabel.addAffectsTargetLayer();
                preserveAlignment(label); // Preserve alignment before adding the highlight class
            });
        }
        if (newAffectedBy != null) {
            newAffectedBy.forEach(location -> {
                Label label = cellLocationToLabel.get(location);
                CustomCellLabel customCellLabel = cellLocationToCustomCellLabel.get(location);
                customCellLabel.addAffectedlyLayer();
                preserveAlignment(label); // Preserve alignment before adding the highlight class

            });
        }
    }

    public void showAffectedCells(List<CellLocation> requestedRange, Map<CellLocation, Label> cellLocationToLabel, Map<CellLocation, CustomCellLabel> cellLocationToCustomCellLabel) {
        requestedRange.forEach(location -> {
            Label label = cellLocationToLabel.get(location);
            CustomCellLabel customCellLabel = cellLocationToCustomCellLabel.get(location);
            customCellLabel.addAffectedlyLayer();
            preserveAlignment(label); // Preserve alignment before adding the highlight class
        });
    }

    // This method clears any existing highlights from all cells
    public void clearAllHighlights(Map<CellLocation, Label> cellLocationToLabel, Map<CellLocation, CustomCellLabel> cellLocationToCustomCellLabel) {
        cellLocationToLabel.values().forEach(label -> {
            String str = label.getId();
            CustomCellLabel customCellLabel = cellLocationToCustomCellLabel.get(CellLocationFactory.fromCellId(str));
            customCellLabel.removeSpecialLayer();
            preserveAlignment(label);
        });
    }

    // Helper method to preserve text alignment
    private void preserveAlignment(Label label) {

        Pos alignment = label.getAlignment();

        if(alignment == Pos.CENTER) {
            label.setTextAlignment(TextAlignment.CENTER); // Align text to the center
        } else if(alignment == Pos.CENTER_LEFT ) {
            label.setTextAlignment(TextAlignment.LEFT); // Align text to the center
        } else if(alignment == Pos.CENTER_RIGHT) {
            label.setTextAlignment(TextAlignment.RIGHT); // Align text to the center
        }
    }
}
