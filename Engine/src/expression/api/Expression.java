package expression.api;

public interface Expression {

    EffectiveValue evaluate() throws IllegalArgumentException;

    String getOperationSign();
    void accept(ExpressionVisitor visitor);
}
