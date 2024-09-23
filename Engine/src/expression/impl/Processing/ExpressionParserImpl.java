package expression.impl.Processing;

import Utility.CellUtils;
import expression.api.Expression;
import expression.api.processing.ExpressionParser;
import expression.impl.stringFunction.Str;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ExpressionParserImpl implements ExpressionParser {
    private String expression;
    private List<String> argumentList;
    private String FunctionName;

    public ExpressionParserImpl(String expression) {
        this.expression = expression;
        if (isPotentialOperation()) {
            FunctionName = extractFunctionName();
        }
    }

    public List<String> getArgumentList(boolean removeSpacesBeforeArguments) {
        return  parseArguments(removeSpacesBeforeArguments);
    }

    public String getFunctionName() {
        return FunctionName;
    }

    @Override
    public List<String> parseArguments(boolean removeSpacesBeforeArguments) {
        String cellId = removeParanthesesFromString();
        return splitArguments(cellId, removeSpacesBeforeArguments).subList(1, splitArguments(cellId, removeSpacesBeforeArguments).size());
    }

    @Override
    public String extractFunctionName() {
        String content = expression.substring(1, expression.length() - 1).trim();
        // Split the content by comma or space
        String[] parts = content.split("[, ]");

        if (parts.length > 0)
            return parts[0]; // The function name is the first part

        throw new IllegalArgumentException("Invalid function format");
    }

    @Override
    public boolean isPotentialOperation() {
        return expression.startsWith("{") && expression.endsWith("}");
    }

    @Override
    public String removeParanthesesFromString() {
        return expression.substring(1, expression.length() - 1).trim();
    }

    @Override
    public List<String> splitArguments(String cellId, boolean removeSpacesBeforeArguments) {
    List<String> arguments = new ArrayList<>();
    int braceLevel = 0;
    StringBuilder currentArg = new StringBuilder();

    for (char c : cellId.toCharArray()) {
        if (c == '{') braceLevel++;
        if (c == '}') braceLevel--;

        if (c == ',' && braceLevel == 0) {
            if(removeSpacesBeforeArguments){
                arguments.add(currentArg.toString().trim());
            }
            else{
                arguments.add(currentArg.toString());
            }
            currentArg.setLength(0);  // Reset currentArg
        } else {
            currentArg.append(c);
        }
    }

    if (currentArg.length() > 0) {
        if (removeSpacesBeforeArguments) {
            arguments.add(currentArg.toString().trim());  // Add the last argument
        } else {
            arguments.add(currentArg.toString());  // Add the last argument
        }
    }

    return arguments;
    }
}
