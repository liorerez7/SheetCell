package Utility;
import CoreParts.api.Cell;
import Utility.Exception.CycleDetectedException;

import java.util.*;

public class RefDependencyGraph implements java.io.Serializable {

    private static final long serialVersionUID = 1L; // Add serialVersionUID
    private final Map<Cell,Set<Cell>> adjacencyList = new HashMap<>();
    private final Map<Cell,Set<Cell>> reverseAdjacencyList = new HashMap<>();

    public void addVertice(Cell cell) {
        adjacencyList.putIfAbsent(cell, new HashSet<>());
        reverseAdjacencyList.putIfAbsent(cell, new HashSet<>());
    }

    public void addDependency(Cell cellA, Cell cellB) {
        adjacencyList.computeIfAbsent(cellB, k -> new HashSet<>()).add(cellA);
        adjacencyList.computeIfAbsent(cellA, k -> new HashSet<>()); // Ensure cellB is in the map
        reverseAdjacencyList.computeIfAbsent(cellA, k -> new HashSet<>()).add(cellB);
        reverseAdjacencyList.computeIfAbsent(cellB, k -> new HashSet<>()); // Ensure cellB is in the map
    }
    public Map<Cell,Set<Cell>> getadjacencyList() {
        return adjacencyList;
    }
    public Map<Cell,Set<Cell>> getreverseAdjacencyList() {
        return reverseAdjacencyList;
    }
    // Removes a dependency edge from cellA to cellB
    public void removeDependency(Cell cellA, Cell cellB) {
        Set<Cell> dependencies = adjacencyList.get(cellA);
        if (dependencies != null) {
            dependencies.remove(cellB);
        }
        Set<Cell> reverseDependencies = reverseAdjacencyList.get(cellB);
        if (reverseDependencies != null) {
            reverseDependencies.remove(cellA);
        }
    }
    // Returns a set of cells that cell depends on
    public Set<Cell> getDependencies(Cell cell) {
        return adjacencyList.getOrDefault(cell, Collections.emptySet());
    }

    // Returns a set of cells that depend on the given cell
    public Set<Cell> getDependents(Cell cell) {
        Set<Cell> dependents = new HashSet<>();
        for (Map.Entry<Cell, Set<Cell>> entry : adjacencyList.entrySet()) {
            if (entry.getValue().contains(cell)) {
                dependents.add(entry.getKey());
            }
        }
        return dependents;
    }




    public List<Cell> topologicalSort() throws CycleDetectedException {
        Map<Cell, Boolean> visited = new HashMap<>();
        Map<Cell, Cell> parent = new HashMap<>();
        Stack<Cell> stack = new Stack<>();

        for (Cell cell : adjacencyList.keySet()) {
            if (!visited.containsKey(cell)) {
                topologicalSortUtil(cell, visited, parent, stack, new ArrayList<>());
            }
        }

        List<Cell> sortedCells = new ArrayList<>();
        while (!stack.isEmpty()) {
            sortedCells.add(stack.pop());
        }

        return sortedCells;
    }

    private void topologicalSortUtil(Cell cell, Map<Cell, Boolean> visited, Map<Cell, Cell> parent, Stack<Cell> stack, List<Cell> path) throws CycleDetectedException {
        visited.put(cell, true);
        path.add(cell);

        for (Cell dependentCell : getDependencies(cell)) {
            if (!visited.containsKey(dependentCell)) {
                parent.put(dependentCell, cell);
                topologicalSortUtil(dependentCell, visited, parent, stack, path);
            } else if (visited.get(dependentCell)) {
                dependentCell.getLocation().getCellId().toUpperCase();
                // Cycle detected: Build the cycle path
                List<Cell> cycle = new ArrayList<>();
                for (int i = path.indexOf(dependentCell); i < path.size(); i++) {
                    cycle.add(path.get(i));
                }
                cycle.add(dependentCell);  // Complete the cycle

                throw new CycleDetectedException("Cycle detected!", cycle);
            }
        }

        path.remove(cell);
        visited.put(cell, false);
        stack.push(cell);
    }


//    // Performs a topological sort and returns the cells in evaluation order
//    public List<Cell> topologicalSort() throws Exception {
//        Map<Cell, Boolean> visited = new HashMap<>();
//        Stack<Cell> stack = new Stack<>();
//
//        for (Cell cell : adjacencyList.keySet()) {
//            if (!visited.containsKey(cell)) {
//                topologicalSortUtil(cell, visited, stack);
//            }
//        }
//        List<Cell> sortedCells = new ArrayList<>();
//        while (!stack.isEmpty()) {
//            sortedCells.add(stack.pop());
//        }
//        return sortedCells;
//    }
//
//    private void topologicalSortUtil(Cell cell, Map<Cell, Boolean> visited, Stack<Cell> stack) throws Exception {
//
//        visited.put(cell, true);
//
//        for (Cell dependentCell : getDependencies(cell)) {
//            if (!visited.containsKey(dependentCell)) {
//                topologicalSortUtil(dependentCell, visited, stack);
//            } else if (visited.get(dependentCell)) {
//                throw new Exception("Cycle detected! make sure there is no self dependency cell was not updated\n for example: A5 depends on A3 and A3 depends on A5");
//            }
//        }
//
//        visited.put(cell, false); // Mark cell as fully processed
//        stack.push(cell);
//    }

    // Detects cycles in the graph (returns true if a cycle is found)
    public boolean hasCycle() {
        Map<Cell, Boolean> visited = new HashMap<>();
        for (Cell cell : adjacencyList.keySet()) {
            if (hasCycleUtil(cell, visited)) {
                return true;
            }
        }
        return false;
    }
    private boolean hasCycleUtil(Cell cell, Map<Cell, Boolean> visited) {
        if (visited.containsKey(cell)) {
            return visited.get(cell);
        }

        visited.put(cell, true);

        for (Cell dependentCell : getDependencies(cell)) {
            if (hasCycleUtil(dependentCell, visited)) {
                return true;
            }
        }

        visited.put(cell, false);
        return false;
    }

}
