package engine.utilities.exception;

import engine.expression.Operation;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnknownOperationTypeException extends RuntimeException {

    public UnknownOperationTypeException(String operation) {
        super("Invalid operation: '" + operation + "'. Valid operations are: " + getValidOperations());
    }

    // Method to get a comma-separated list of all valid operations
    private static String getValidOperations() {
        return Stream.of(Operation.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    // Static method to validate an operation and throw the exception if invalid
    public static Operation validateOperation(String operation) {
        try {
            return Operation.valueOf(operation.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnknownOperationTypeException(operation);
        }
    }
}