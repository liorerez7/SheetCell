package expression.api;

import CoreParts.api.SheetCell;
import CoreParts.api.SheetCellViewOnly;

import java.util.Optional;

public interface Expression {

    EffectiveValue evaluate() throws IllegalArgumentException;

    String getOperationSign();
    void accept(ExpressionVisitor visitor);
}
