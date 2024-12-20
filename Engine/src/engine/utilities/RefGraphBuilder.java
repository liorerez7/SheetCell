package engine.utilities;

import engine.core_parts.api.Cell;
import engine.core_parts.api.sheet.SheetCellViewOnly;

import engine.expression.impl.Range;
import engine.expression.impl.functions.unique.Ref;
import dto.small_parts.CellLocation;
import dto.small_parts.CellLocationFactory;

import java.io.Serializable;
import java.util.ArrayList;
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

        String originalValue = cell.getOriginalValue();

        // Parse the original value and extract references
        List<CellLocation> newReferences = extractReferencesFromExpression(originalValue);

        //Set<Cell> newDependencies = new HashSet<>();

        // Add edges in the graph
        for (CellLocation refLocation : newReferences) {
            Cell refCell = sheetCell.getCell(refLocation);
            if (refCell != null) {
                dependencyGraph.addDependency(cell, refCell);
            }
        }

    }

    private List<CellLocation> extractReferencesFromExpression(String expression) {
        List<CellLocation> references = new ArrayList<>();

        // Extract cell references for REF
        extractCellReferences(expression, references);

        // Extract range references for SUM and AVG
        extractRangeReferences(expression, "{SUM", references);
        extractRangeReferences(expression, "{AVERAGE", references);

        return references;
    }

    private void extractCellReferences(String expression, List<CellLocation> references) {
        int index = 0;
        while (index < expression.length()) {
            if (expression.regionMatches(true, index, "{REF", 0, 4)) {
                int start = expression.indexOf(',', index) + 1;
                int end = expression.indexOf('}', start);
                if (start != -1 && end != -1) {
                    String cellId = expression.substring(start, end).trim();
                    references.add(CellLocationFactory.fromCellId(cellId));
                }
            }
            index++;
        }
    }

    private void extractRangeReferences(String expression, String operation, List<CellLocation> references) {
        int index = 0;
        while (index < expression.length()) {
            if (expression.regionMatches(true, index, operation, 0, operation.length())) {
                int start = expression.indexOf(',', index) + 1;
                int end = expression.indexOf('}', start);
                if (start != -1 && end != -1) {
                    String rangeName = expression.substring(start, end).trim();
                    addRangeReferences(rangeName, references);
                }
            }
            index++;
        }
    }

    private void addRangeReferences(String rangeName, List<CellLocation> references) {
        if (sheetCell.isRangePresent(rangeName)) {
            Range range = sheetCell.getRange(rangeName);
            if (range != null) {
                Set<Ref> rangeRefs = range.getRangeRefs();
                rangeRefs.forEach(ref -> references.add(ref.getCellLocation()));
            }
        }
    }


    public void build() {

        dependencyGraph.resetDependenciesGraph();
        for (Cell cell : sheetCell.getSheetCell().values()) {
            processCell(cell);
        }
    }
}
