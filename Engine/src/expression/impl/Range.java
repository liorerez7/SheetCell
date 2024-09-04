package expression.impl;

import CoreParts.smallParts.CellLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Range implements Serializable {

    Set<Ref> refs;
    String rangeName;

    public Range(Set<Ref> rangeOfCellLocation, String rangeName) {
        this.refs = rangeOfCellLocation;
        this.rangeName = rangeName;
    }

    public Set<Ref> getRangeRefs() {
        return refs;
    }

    public List<CellLocation> getCellLocations() {
        List<CellLocation> r = new ArrayList<>();

        refs.forEach(ref -> {
            r.add(ref.getCellLocation());
        });

        return r;
    }
    public String getRangeName() {
        return rangeName;
    }

}
