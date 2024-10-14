package engine.utilities.exception;

public class TooManyArgumentsException extends RuntimeException {

    int numberOfArgumentsExpected;
    int numberOfArgumentsActual;
    String nameOfOperation;

    public TooManyArgumentsException(String nameOfOperation, int expected, int actual) {
        this.nameOfOperation = nameOfOperation;
        this.numberOfArgumentsExpected = expected;
        this.numberOfArgumentsActual = actual;
    }

    @Override
    public String getMessage() {

        return "Operation: " + nameOfOperation + " expects " + numberOfArgumentsExpected + " arguments but got " + numberOfArgumentsActual + " arguments.";
    }
}
