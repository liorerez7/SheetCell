package CoreParts.impl.DtoComponents;

import CoreParts.api.Cell;
import expression.api.Expression;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DtoCell {
    private Expression effectiveValue;
    private String originalValue;
    private DtoLocation location;

    private Set<DtoLocation> affectedBy;
    private Set<DtoLocation> affectingOn;


    // Constructor to populate DtoCell from CellImp
    public DtoCell(Cell cell) {

        this.originalValue = cell.getOriginalValue();

        // Convert the Expression to a string for the DTO
        this.effectiveValue = cell.getEffectiveValue();

        // Convert the CellLocation to DtoLocation
        this.location = new DtoLocation(cell.getLocation());

        // Convert the affected cells to DtoLocation
        this.affectedBy = cell.getAffectedBy().stream()
                .map(c -> new DtoLocation(c.getLocation()))
                .collect(Collectors.toSet());

        // Convert the affecting cells to DtoLocation
        this.affectingOn = cell.getAffectingOn().stream()
                .map(c -> new DtoLocation(c.getLocation()))
                .collect(Collectors.toSet());
    }

    public Expression getEffectiveValue() {
        return effectiveValue;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public DtoLocation getLocation() {
        return location;
    }

    public Set<DtoLocation> getAffectedBy() {
        return affectedBy;
    }

    public Set<DtoLocation> getAffectingOn() {
        return affectingOn;
    }
}
