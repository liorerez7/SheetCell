package expression.api;

import java.util.Optional;

public interface Expression {

    EffectiveValue evaluate() throws IllegalArgumentException;

    String getOperationSign();
    void accept(ExpressionVisitor visitor);
}
