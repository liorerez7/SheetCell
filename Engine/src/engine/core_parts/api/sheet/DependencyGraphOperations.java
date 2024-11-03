package engine.core_parts.api.sheet;

import engine.utilities.RefDependencyGraph;

public interface DependencyGraphOperations {
    RefDependencyGraph getGraph();
    void createRefDependencyGraph();
    RefDependencyGraph getRefDependencyGraph();
    void performGraphOperations();

}
