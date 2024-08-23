package expression.api;

import CoreParts.api.SheetCell;
import CoreParts.api.SheetCellViewOnly;

import java.io.Serializable;
import java.util.Optional;

public interface Expression extends Serializable {


    EffectiveValue evaluate() throws IllegalArgumentException;

    String getOperationSign();
    void accept(ExpressionVisitor visitor);
}
