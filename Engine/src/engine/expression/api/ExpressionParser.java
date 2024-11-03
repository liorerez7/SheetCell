package engine.expression.api;

import java.io.Serializable;
import java.util.List;

public interface ExpressionParser  extends Serializable {
    boolean isPotentialOperation();
    String removeParanthesesFromString();
    String extractFunctionName();
    List<String> parseArguments(boolean removeSpacesBeforeArguments);
    List<String> splitArguments(String cellId, boolean removeSpacesBeforeArguments);
    List<String> getArgumentList(boolean removeSpacesBeforeArguments);
    String getFunctionName();

}
