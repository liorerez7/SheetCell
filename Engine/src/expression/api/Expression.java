package expression.api;

public interface Expression {
    /**
     * evaluate the expression and return the result
     *
     * @return the results of the expression
     */
    Object evaluate() throws IllegalArgumentException;
//TODO: maybe Expressions should also know what type of expression they are. BinaryExpression, UnaryExpression, etc.
    String getOperationSign();
}