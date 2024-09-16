package Utility;

import CoreParts.smallParts.CellLocation;
import expression.api.EffectiveValue;

public class EffectiveValueContainer {

    private EffectiveValue effectiveValue;
    private CellLocation cellLocation;

    public EffectiveValueContainer(EffectiveValue effectiveValue, CellLocation cellLocation) {
        this.effectiveValue = effectiveValue;
        this.cellLocation = cellLocation;
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public CellLocation getCellLocation() {
        return cellLocation;
    }

    public void setEffectiveValue(EffectiveValue effectiveValue) {
        this.effectiveValue = effectiveValue;
    }

    public void setCellLocation(CellLocation cellLocation) {
        this.cellLocation = cellLocation;
    }
}
