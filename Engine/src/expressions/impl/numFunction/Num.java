package expressions.impl.numFunction;

import expressions.Expression;

public class Num implements Expression {

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
