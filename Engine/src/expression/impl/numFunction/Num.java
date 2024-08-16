package expression.impl.numFunction;

import expression.api.Expression;
import expression.api.ExpressionVisitor;

public class Num implements Expression {
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }
    private double num;

    public Num(double num) {
        this.num = num;
    }

    @Override
    public Object evaluate() {
        return num;
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    @Override
    public String toString() {
        return num < 0 ?
                "(" + num + ")" :
                Double.toString(num);
    }

}
