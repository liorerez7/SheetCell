package expressions;


import expressions.impl.Concat;
import expressions.impl.Exponent;
import expressions.impl.Str;
import expressions.impl.Sum;
import expressions.impl.Number;

public class Main {

    static public void main(String[] args) {
        Expression e =
                new Sum(
                    new Exponent(
                        new Number(2.0), new Number(3.0)),
                    new Sum(
                        new Number(1.0), new Number(-3.0)));

        System.out.println(e + " = " + e.evaluate());

        Expression e2 = new Concat(
                new Str("hello"), new Str("Word"));

        System.out.println(e2 + " = " + e2.evaluate());
    }
}
