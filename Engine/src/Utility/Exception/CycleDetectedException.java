package Utility.Exception;

import CoreParts.api.Cell;
import smallParts.CellLocation;


import java.util.List;

public class CycleDetectedException extends RuntimeException {

    private final List<Cell> cycle;

    public CycleDetectedException(String message, List<Cell> cycle) {
        super(message);
        this.cycle = cycle;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " Cycle: " + formatCycle();
    }

    private String formatCycle() {
        if (cycle == null || cycle.isEmpty()) {
            return "No cycle information available.";
        }
        StringBuilder cycleString = new StringBuilder();
        for (Cell cell : cycle) {
            cycleString.append(cell.getLocation().getCellId()).append(" -> ");
        }
        // Remove the trailing " -> "
        if (cycleString.length() > 4) {
            cycleString.setLength(cycleString.length() - 4);
        }
        return cycleString.toString();
    }

    public List<CellLocation> getCycle() {
        if (cycle == null || cycle.isEmpty()) {
            return null;
        }
        List<CellLocation> cycleList = new java.util.ArrayList<>();
        for (Cell cell : cycle) {
            cycleList.add(cell.getLocation());
        }
        return cycleList;
    }
}
