package expression;
import expression.api.Expression;
import expression.impl.numFunction.*;
import expression.impl.stringFunction.Concat;
import expression.impl.stringFunction.Sub;

import java.util.List;

//    TODO : add all the new operations to the enum

public enum Operation {

    PLUS {
        @Override
        public Expression calculate(List<Expression> expressions) {
            return new Plus(expressions.get(0), expressions.get(1));
        }
    },
    TIMES {
        @Override
        public Expression calculate(List<Expression> expressions) {
            return new Times(expressions.get(0), expressions.get(1));
        }
    },
    DIVIDE {
        @Override
        public Expression calculate(List<Expression> expressions) {
            return new Divide(expressions.get(0), expressions.get(1));
        }
    },
    POW {
        @Override
        public Expression calculate(List<Expression> expressions) {
            return new Pow(expressions.get(0), expressions.get(1));
        }
    },
    MOD {
        @Override
        public Expression calculate(List<Expression> expressions) {
            return new Mod(expressions.get(0), expressions.get(1));
        }
    },
    ABS {
        @Override
        public Expression calculate(List<Expression> expressions) {
            return new Abs(expressions.getFirst());
        }
    },

    MINUS {
        @Override
        public Expression calculate(List<Expression> expressions) {
            return new Minus(expressions.get(0), expressions.get(1));
        }
    },
    CONCAT {
        @Override
        public Expression calculate(List<Expression> expressions) {
            return new Concat(expressions.get(0), expressions.get(1));
        }
    },

    SUB {
        @Override
        public Expression calculate(List<Expression> expressions) {
            return new Sub(expressions.get(0), expressions.get(1), expressions.get(2));
        }
    },

    REF{
      @Override
      public  Expression calculate(List<Expression> expressions){
          return (expressions.getFirst());
      }
    };

    // Method to convert a string to the corresponding Operation enum
    public static Operation fromString(String operation) {
        return valueOf(operation.toUpperCase());
    }

    // Abstract method that each enum constant must implement
    public abstract Expression calculate(List<Expression> expressions);
}