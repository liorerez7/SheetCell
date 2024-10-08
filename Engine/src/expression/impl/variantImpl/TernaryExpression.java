package expression.impl.variantImpl;

import CoreParts.api.sheet.SheetCellViewOnly;
import expression.api.Expression;
import smallParts.EffectiveValue;

public abstract class TernaryExpression implements Expression {
    Expression firstOperand;
    Expression secondOperand;
    Expression thirdOperand;

    public TernaryExpression() {
    }
    public Expression getFirstOperand() {return firstOperand;}

    public Expression getSecondOperand() {return secondOperand;}

    public Expression getThirdOperand() {return thirdOperand;}

    public void setFirstOperand(Expression firstOperand) {this.firstOperand = firstOperand;}

    public void setSecondOperand(Expression secondOperand) {this.secondOperand = secondOperand;}

    public void setThirdOperand(Expression thirdOperand) {this.thirdOperand = thirdOperand;}

    public TernaryExpression(Expression firstOperand, Expression secondOperand, Expression thirdOperand) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.thirdOperand = thirdOperand;
    }
    public EffectiveValue evaluate(SheetCellViewOnly sheet) throws IllegalArgumentException {
        return evaluate(firstOperand.evaluate(sheet), secondOperand.evaluate(sheet), thirdOperand.evaluate(sheet));
    }

    @Override
    public String getOperationSign() {
        return "";
    }

    abstract protected EffectiveValue evaluate(EffectiveValue evaluate, EffectiveValue evaluate2, EffectiveValue evaluate3);
}
