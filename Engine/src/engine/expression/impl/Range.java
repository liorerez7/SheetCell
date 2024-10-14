package engine.expression.impl;



import dto.small_parts.CellLocation;
import engine.expression.impl.functions.unique.Ref;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Range implements Serializable {

    Set<Ref> refs;
    String rangeName;
    Set<CellLocation> cellsThatThisRangeAffects = new HashSet<>();

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

    public void addAffectedFromThisRangeCellLocation(CellLocation cellLocation) {
        cellsThatThisRangeAffects.add(cellLocation);
    }

    public Set<CellLocation> getAffectingCellLocations() {
        return cellsThatThisRangeAffects;
    }

    public boolean canBeDeleted() {
        return cellsThatThisRangeAffects.isEmpty();
    }

    public void removeAffectedFromThisRangeCellLocation(CellLocation cellLocation) {
        cellsThatThisRangeAffects.remove(cellLocation);
    }
}
