package expression.api;

import expression.impl.numFunction.Num;
import expression.impl.stringFunction.Str;
import expression.impl.variantImpl.BinaryExpression;
import expression.impl.variantImpl.TernaryExpression;
import expression.impl.variantImpl.UnaryExpression;

import java.io.Serializable;

public interface ExpressionVisitor  {
    void visit(UnaryExpression expression);
    void visit(BinaryExpression expression);
    void visit(TernaryExpression expression);
    void visit(Num expression);
    void visit(Str expression);
}

