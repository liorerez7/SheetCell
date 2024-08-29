package Utility;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCellViewOnly;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RefGraphBuilder implements Serializable {

    private static final long serialVersionUID = 1L; // Add serialVersionUID
    private final RefDependencyGraph dependencyGraph;
    private SheetCellViewOnly sheetCell;
    public RefGraphBuilder(SheetCellViewOnly sheetCell) {
        this.dependencyGraph = sheetCell.getGraph();
        this.sheetCell = sheetCell;
    }
    public RefDependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }
    public void processCell(Cell cell) {

        dependencyGraph.addVertice(cell);

        String originalValue = cell.getOriginalValue();
        // Parse the original value and extract references
        List<CellLocation> references = extractReferencesFromExpression(originalValue);
        // Add edges in the graph
        for (CellLocation refLocation : references) {
            Cell refCell = sheetCell.getCell(refLocation);
            if (refCell != null) {
                dependencyGraph.addDependency(cell, refCell);
            }
        }
    }
    private List<CellLocation> extractReferencesFromExpression(String expression) {
        List<CellLocation> references = new ArrayList<>();
        // Example parsing logic for "{REF, A5}"
        int index = 0;
        while (index < expression.length()) {
            if (expression.regionMatches(true, index, "{REF", 0, 4)) {
                int start = expression.indexOf(',', index) + 1;
                int end = expression.indexOf('}', start);
                if (start != -1 && end != -1) {
                    String cellId = expression.substring(start, end).trim();
                    char col = cellId.charAt(0);
                    char row = cellId.charAt(1);
                    references.add(CellLocationFactory.fromCellId(cellId));
                }
            }
            index++;
        }
        return references;
    }

    public void build() {
        for (Cell cell : sheetCell.getSheetCell().values()) {
            processCell(cell);
        }
    }
}

