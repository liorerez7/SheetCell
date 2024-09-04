package Utility;

import CoreParts.api.Cell;
import CoreParts.api.sheet.SheetCellViewOnly;
import CoreParts.impl.InnerSystemComponents.SheetCellImp;
import CoreParts.smallParts.CellLocation;
import CoreParts.smallParts.CellLocationFactory;
import expression.impl.Range;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RefGraphBuilder implements Serializable {

    private static final long serialVersionUID = 1L; // Add serialVersionUID
    private final RefDependencyGraph dependencyGraph;
    private SheetCellViewOnly sheetCell;
    private SheetCellImp sheetCellImp;

    public RefGraphBuilder(SheetCellViewOnly sheetCell) {
        this.dependencyGraph = sheetCell.getGraph();
        this.sheetCell = sheetCell;
        this.sheetCellImp = sheetCell;
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


        index = 0;
        while (index < expression.length()) {
            if (expression.regionMatches(true, index, "{SUM", 0, 4)) {
                int start = expression.indexOf(',', index) + 1;
                int end = expression.indexOf('}', start);
                if (start != -1 && end != -1) {
                    String rangeName = expression.substring(start, end).trim();

                    if(sheetCellImp.isRangePresent(rangeName)){
                        Range range = sheetCellImp.getRange(rangeName);
                        if (range != null) {
                            references.addAll(range.getRangeOfCellLocation());
                        }
                    }
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

