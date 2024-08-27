package CoreParts.impl.DtoComponents;

import CoreParts.api.Cell;
import CoreParts.smallParts.CellLocation;
import Utility.CellUtils;
import expression.api.EffectiveValue;
import expression.api.Expression;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DtoCell {
    private EffectiveValue effectiveValue;
    private String originalValue;
    private CellLocation  location;
    private int version;
    private Set<CellLocation > affectedBy;
    private Set<CellLocation> affectingOn;


    // Constructor to populate DtoCell from CellImp
    public DtoCell(Cell cell) {

        this.originalValue = cell.getOriginalValue();

        version = cell.getLatestVersion();

        // Convert the Expression to a string for the DTO
        if (cell.getEffectiveValue() == null) {
            this.effectiveValue = null;
        }
        else {
            //CellUtils.formatDoubleValue(cell.getActualValue());
            this.effectiveValue = cell.getActualValue();
        }
        // Convert the CellLocation to DtoLocation
        this.location = cell.getLocation();

        this.affectedBy = cell.getAffectedBy().stream()
                .map(Cell::getLocation)
                .collect(Collectors.toSet());

        this.affectingOn = cell.getAffectingOn().stream()
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
