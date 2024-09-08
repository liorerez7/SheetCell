package Controller.Main;

import CoreParts.impl.DtoComponents.DtoCell;
import CoreParts.impl.DtoComponents.DtoSheetCell;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import expression.api.EffectiveValue;
import javafx.beans.property.*;
import javafx.scene.control.Label;


import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Model {
    private final Map<Label, StringProperty> cellLebalToProperties = new HashMap<>();
    private DtoSheetCell sheetCell;
    private BooleanProperty isCellLebalClicked;
    private StringProperty latestUpdatedVersionProperty;
    private StringProperty originalValueLabelProperty;
    private StringProperty totalVersionsProperty;


    Model(DtoSheetCell sheetCell) {
        this.sheetCell = sheetCell;

        isCellLebalClicked = new SimpleBooleanProperty(false);
        latestUpdatedVersionProperty = new SimpleStringProperty("");
        originalValueLabelProperty = new SimpleStringProperty("");
        totalVersionsProperty = new SimpleStringProperty("");
    }

    public void setCellLabelToProperties(Map<CellLocation,Label> cellLocationLabelMap) {

        cellLocationLabelMap.forEach((cellLocation, label) -> {
            cellLebalToProperties.put(label, new SimpleStringProperty());

        });

    }

    public void bindCellLebelToProperties() {
        cellLebalToProperties.forEach((label, property) -> {
            label.textProperty().bind(property);

        });
    }

    public void setPropertiesByDtoSheetCell(DtoSheetCell sheetCell) {
        this.sheetCell = sheetCell;
        cellLebalToProperties.forEach((label, property) -> {
            CellLocation cellLocation = CellLocationFactory.fromCellId(label.getId());
            EffectiveValue effectiveValue = sheetCell.getViewSheetCell().get(cellLocation);

            String value = getString(effectiveValue);

            property.set(value);

        });

    }

    private String getString(EffectiveValue effectiveValue) {
        Object objectValue;
        String value = "";

        if (effectiveValue != null) {
            objectValue = effectiveValue.getValue();

            if (objectValue instanceof Double) {
                Double doubleValue = (Double) objectValue;

                // Check if the value is NaN
                if (doubleValue.isNaN()) {
                    value = "NaN";
                }
                else{
                    if (doubleValue % 1 == 0) {
                        value = Integer.toString(doubleValue.intValue());  // Convert to int if no decimal part
                    }
                    else {
                        // Format to 2 decimal places if there are decimals
                        DecimalFormat df = new DecimalFormat("#.##");
                        value = df.format(doubleValue);
                    }
                }
            }
            else {
                if(objectValue instanceof Boolean) {
                    Boolean boolValue = (Boolean) objectValue;
                    value = boolValue ? "TRUE" : "FALSE";
                }
                else {
                    // Just use the object's string value
                    value = objectValue.toString();
                }
            }
        }
        return value;
    }

    public StringProperty getLatestUpdatedVersionProperty() {
        return latestUpdatedVersionProperty;
    }

    public void setLatestUpdatedVersionProperty(DtoCell cell) {

        String latestVersion = "";

        if(cell != null) {

            latestVersion = Integer.toString(cell.getLatestVersion());
        }

        latestUpdatedVersionProperty.set(latestVersion);
    }

    public BooleanProperty getIsCellLebalClickedProperty() {
        return isCellLebalClicked;
    }

    public void setIsCellLebalClicked(boolean isCellLebalClicked) {
        this.isCellLebalClicked.set(isCellLebalClicked);
    }

    public StringProperty getOriginalValueLabelProperty() {
        return originalValueLabelProperty;
    }

    public void setOriginalValueLabelProperty(DtoCell cell) {

        String OriginalValue = "";

        if(cell != null) {
            OriginalValue = cell.getOriginalValue();
        }

        originalValueLabelProperty.set(OriginalValue);
    }

    public StringProperty getTotalVersionsProperty() {
        return totalVersionsProperty;
    }

    public void setTotalVersionsProperty(int totalVersionsInTheSystem) {
        String totalVersions = "";

        if(totalVersionsInTheSystem != 0) {
            totalVersions = Integer.toString(totalVersionsInTheSystem);
        }

        totalVersionsProperty.set(totalVersions);
    }
}
