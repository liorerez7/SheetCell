package expression.api.processing;

import java.io.Serializable;
import java.util.List;

public interface ExpressionParser  extends Serializable {
    public  boolean isPotentialOperation();
    public  String removeParanthesesFromString();
    public  String extractFunctionName();
    public List<String> parseArguments(boolean removeSpacesBeforeArguments);
    public  List<String> splitArguments(String cellId, boolean removeSpacesBeforeArguments);
    public List<String> getArgumentList(boolean removeSpacesBeforeArguments);
    public String getFunctionName();

}
