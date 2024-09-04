package expression.impl;

import CoreParts.smallParts.CellLocation;

import java.io.Serializable;
import java.util.Set;

public class Range implements Serializable {

    Set<CellLocation> rangeOfCellLocation;
    String rangeName;

    public Range(Set<CellLocation> rangeOfCellLocation, String rangeName) {
        this.rangeOfCellLocation = rangeOfCellLocation;
        this.rangeName = rangeName;
    }

    public Set<CellLocation> getRangeOfCellLocation() {
        return rangeOfCellLocation;
    }

    public String getRangeName() {
        return rangeName;
    }

    public void setRangeOfCellLocation(Set<CellLocation> rangeOfCellLocation) {
        this.rangeOfCellLocation = rangeOfCellLocation;
    }

    public void setRangeName(String rangeName) {
        this.rangeName = rangeName;
    }

}
