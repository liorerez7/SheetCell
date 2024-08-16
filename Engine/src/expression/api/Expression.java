package expression.api;

public interface Expression {
    /**
     * evaluate the expression and return the result
     *
     * @return the results of the expression
     */
    EffectiveValue evaluate() throws IllegalArgumentException;

    String getOperationSign();
    void accept(ExpressionVisitor visitor);
}
