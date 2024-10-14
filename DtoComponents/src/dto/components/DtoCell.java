package dto.components;

import engine.core_parts.api.Cell;
import dto.small_parts.CellLocation;
import dto.small_parts.EffectiveValue;


import java.util.Set;
import java.util.stream.Collectors;

public class DtoCell {
    private EffectiveValue effectiveValue;
    private String originalValue;
    private CellLocation location;
    private int version;
    private Set<CellLocation > affectedBy;
    private Set<CellLocation> affectingOn;

    // Constructor to populate DtoCell from CellImp
    public DtoCell(Cell cell) {

        // Assign the original value and latest version from the cell
        this.originalValue = cell.getOriginalValue();
        this.version = cell.getLatestVersion();

        // Convert the effective value to string if it's not null
        if (cell.getEffectiveValue() == null) {
            this.effectiveValue = null;
        } else {
            this.effectiveValue = cell.getActualValue();
        }

        // Convert the CellLocation to DtoLocation
        this.location = cell.getLocation();

        // Map the affectedBy and affectingOn to their locations using streams
        this.affectedBy = cell.getAffectedBy()
                .stream()
                .map(Cell::getLocation)
                .collect(Collectors.toSet());

        this.affectingOn = cell.getAffectingOn()
                .stream()
                .map(Cell::getLocation)
                .collect(Collectors.toSet());
    }

    public int getLatestVersion() {
        return version;
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public CellLocation getLocation() {
        return location;
    }

    public Set<CellLocation> getAffectedBy() {
        return affectedBy;
    }

    public Set<CellLocation> getAffectingOn() {
        return affectingOn;
    }
}
