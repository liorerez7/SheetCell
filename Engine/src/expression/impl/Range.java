package expression.impl;

import CoreParts.smallParts.CellLocation;

import java.io.Serializable;
import java.util.Set;

public class Range implements Serializable {

    Set<Ref> rangeOfCellLocation;
    String rangeName;

    public Range(Set<Ref> rangeOfCellLocation, String rangeName) {
        this.rangeOfCellLocation = rangeOfCellLocation;
        this.rangeName = rangeName;
    }

    public Set<Ref> getRangeRefs() {
        return rangeOfCellLocation;
    }

    public String getRangeName() {
        return rangeName;
    }

}
