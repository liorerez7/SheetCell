import java.beans.Expression;

public class SumFunction {

    @Override
    public Object evaluate() {
        return evaluate(Expression.evaluate(), Expression.evaluate());
    }

}
