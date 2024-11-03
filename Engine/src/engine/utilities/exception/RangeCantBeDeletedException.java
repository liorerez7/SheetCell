package engine.utilities.exception;



import dto.small_parts.CellLocation;

import java.util.Set;
import java.util.stream.Collectors;

public class RangeCantBeDeletedException extends RuntimeException {

    String rangeName;
    Set<CellLocation> cellsThatThisRangeAffects;

    public RangeCantBeDeletedException(String RangeName, Set<CellLocation> cellsThatThisRangeAffects) {
        this.rangeName = RangeName;
        this.cellsThatThisRangeAffects = cellsThatThisRangeAffects;
    }

    @Override
    public String getMessage() {
        String cells = cellsThatThisRangeAffects.stream()
                .map(CellLocation::getCellId)
                .collect(Collectors.joining(", "));
        return "Range: '" + rangeName + "' can't be deleted because it is being used by cells: " + cells;
    }
}
