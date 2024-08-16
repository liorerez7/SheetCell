package expression.api.processing;

import java.util.List;

//TODO: just an idea.
public interface ExpressionParser {
    public  boolean isPotentialOperation(String newValue);

    public  String removeParanthesesFromString(String input);

    public  List<String> splitArguments(String content);

    public  List<String> getCellAsStringRepresentation(String newValue);
}
