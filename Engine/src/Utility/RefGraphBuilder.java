package Utility;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCellViewOnly;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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



        //Set<Cell> currentDependencies = dependencyGraph.getDependencies(cell);

        String originalValue = cell.getOriginalValue();

        // Parse the original value and extract references
        List<CellLocation> newReferences = extractReferencesFromExpression(originalValue);

        //Set<Cell> newDependencies = new HashSet<>();

        // Add edges in the graph
        for (CellLocation refLocation : newReferences) {
            Cell refCell = sheetCell.getCell(refLocation);
            if (refCell != null) {
                dependencyGraph.addDependency(cell, refCell);
               // newDependencies.add(refCell);

            }
        }

//        // Remove old dependencies that are no longer referenced
//        for (Cell oldDependency : currentDependencies) {
//            if (!newDependencies.contains(oldDependency)) {
//                dependencyGraph.removeDependency(cell, oldDependency);
//            }
//        }

        /* I want that it will also remove the dependency of the cell if there is no references
        *
        * for example if before the cell was dependent on A1 and now the cell is not dependent on A1 then it
        * should remove the dependency
        *
        *
        */


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

        dependencyGraph.resetDependenciesGraph();
        for (Cell cell : sheetCell.getSheetCell().values()) {
            processCell(cell);
        }
    }
}

