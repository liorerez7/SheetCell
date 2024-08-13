package expressions;
import expressions.impl.numFunction.Minus;
import expressions.impl.numFunction.Plus;

import java.util.List;

//    TODO : add all the new operations to the enum

public enum Operation {

    PLUS {
        @Override
        public Expression calculate(List<Expression> expressions) {
            return new Plus(expressions.get(0), expressions.get(1));
        }
    },

    MINUS {
        @Override
        public Expression calculate(List<Expression> expressions) {
            return new Minus(expressions.get(0), expressions.get(1));
        }
    },

    REF{
      @Override
      public  Expression calculate(List<Expression> expressions){
          return (expressions.get(0));
      }
    };

//    ABS {
//        @Override
//        public Expression calculate(List<CellImp> listOfCells) {
//            return new Abs(listOfCells.get(0).getEffectiveValue());
//        }
//    };

    // Method to convert a string to the corresponding Operation enum
    public static Operation fromString(String operation) {
        return valueOf(operation.toUpperCase());
    }

    // Abstract method that each enum constant must implement
    public abstract Expression calculate(List<Expression> expressions);
}