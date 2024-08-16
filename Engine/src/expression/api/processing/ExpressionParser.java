package expression.api.processing;

import java.util.List;

//TODO: just an idea.
public interface ExpressionParser {
    public  boolean isPotentialOperation();
    public  String removeParanthesesFromString();
    public  String extractFunctionName();
    public List<String> parseArguments();
    public  List<String> splitArguments(String cellId);
    public List<String> getArgumentList();
    public String getFunctionName();

}
