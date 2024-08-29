package CoreParts.api.sheet;

import Utility.RefDependencyGraph;

public interface DependencyGraphOperations {
    RefDependencyGraph getGraph();
    void createRefDependencyGraph();
    RefDependencyGraph getRefDependencyGraph();
    void performGraphOperations();

}
