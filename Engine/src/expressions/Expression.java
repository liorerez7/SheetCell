package expressions;

public interface Expression {

    /**
     * evaluate the expression and return the result
     *
     * @return the results of the expression
     */
    Object evaluate() throws IllegalArgumentException;

    String getOperationSign();
}