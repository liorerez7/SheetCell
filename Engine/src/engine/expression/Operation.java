package engine.expression;
import engine.expression.api.Expression;
import engine.expression.impl.functions.unique.If;
import engine.expression.impl.functions.bool.*;
import engine.expression.impl.functions.numeric.*;
import engine.utilities.exception.TooManyArgumentsException;
import engine.utilities.exception.UnknownOperationTypeException;

import engine.expression.impl.functions.string.Concat;
import engine.expression.impl.functions.string.Sub;

import java.util.List;

//    TODO : add all the new operations to the enum

public enum Operation {

    PLUS {
        @Override
        public Expression calculate(List<Expression> expressions) {
            ThrowExceptionOnTooManyArguments("PLUS", 2, expressions.size());
            return new Plus(expressions.get(0), expressions.get(1));
        }
    },
    TIMES {
        @Override
        public Expression calculate(List<Expression> expressions) {
            ThrowExceptionOnTooManyArguments("TIMES", 2, expressions.size());
            return new Times(expressions.get(0), expressions.get(1));
        }
    },
    DIVIDE {
        @Override
        public Expression calculate(List<Expression> expressions) {
            ThrowExceptionOnTooManyArguments("DIVIDE", 2, expressions.size());
            return new Divide(expressions.get(0), expressions.get(1));
        }
    },
    POW {
        @Override
        public Expression calculate(List<Expression> expressions) {
            ThrowExceptionOnTooManyArguments("POW", 2, expressions.size());
            return new Pow(expressions.get(0), expressions.get(1));
        }
    },
    MOD {
        @Override
        public Expression calculate(List<Expression> expressions) {
            ThrowExceptionOnTooManyArguments("MOD", 2, expressions.size());
            return new Mod(expressions.get(0), expressions.get(1));
        }
    },
    ABS {
        @Override
        public Expression calculate(List<Expression> expressions) {
            ThrowExceptionOnTooManyArguments("ABS", 1, expressions.size());
            return new Abs(expressions.getFirst());
        }
    },

    MINUS {
        @Override
        public Expression calculate(List<Expression> expressions) {
            ThrowExceptionOnTooManyArguments("MINUS", 2, expressions.size());
            return new Minus(expressions.get(0), expressions.get(1));
        }
    },
    CONCAT {
        @Override
        public Expression calculate(List<Expression> expressions) {
            ThrowExceptionOnTooManyArguments("CONCAT", 2, expressions.size());
            return new Concat(expressions.get(0), expressions.get(1));
        }
    },

    SUB {
        @Override
        public Expression calculate(List<Expression> expressions) {
            ThrowExceptionOnTooManyArguments("SUB", 3, expressions.size());
            return new Sub(expressions.get(0), expressions.get(1), expressions.get(2));
        }
    },

    SUM {
        @Override
        public Expression calculate(List<Expression> expressions) {
            return null;
        }
    },

    EQUAL{
        @Override
        public Expression calculate(List<Expression> expressions){
            ThrowExceptionOnTooManyArguments("EQUAL", 2, expressions.size());
            return new Equal(expressions.get(0), expressions.get(1));
        }
    },

    OR{
        @Override
        public Expression calculate(List<Expression> expressions){
            ThrowExceptionOnTooManyArguments("OR", 2, expressions.size());
            return new Or(expressions.get(0), expressions.get(1));
        }
    },

    AND{
        @Override
        public Expression calculate(List<Expression> expressions){
            ThrowExceptionOnTooManyArguments("AND", 2, expressions.size());
            return new And(expressions.get(0), expressions.get(1));
        }
    },

    NOT{
        @Override
        public Expression calculate(List<Expression> expressions){
            ThrowExceptionOnTooManyArguments("NOT", 1, expressions.size());
            return new Not(expressions.getFirst());
        }
    },

    BIGGER{
        @Override
        public Expression calculate(List<Expression> expressions){
            ThrowExceptionOnTooManyArguments("BIGGER", 2, expressions.size());
            return new Bigger(expressions.get(0), expressions.get(1));
        }
    },

    LESS{
        @Override
        public Expression calculate(List<Expression> expressions){
            ThrowExceptionOnTooManyArguments("LESS", 2, expressions.size());
            return new Less(expressions.get(0), expressions.get(1));
        }
    },

    IF{
        @Override
        public Expression calculate(List<Expression> expressions){
            ThrowExceptionOnTooManyArguments("IF", 3, expressions.size());
            return new If(expressions.get(0), expressions.get(1), expressions.get(2));
        }
    },

    AVERAGE {
        @Override
        public Expression calculate(List<Expression> expressions){
            return null;
        }
    },

    PERCENT{
        @Override
        public Expression calculate(List<Expression> expressions){
            ThrowExceptionOnTooManyArguments("PERCENT", 2, expressions.size());
            return new Percent(expressions.get(0), expressions.get(1));
        }
    },

    REF{
      @Override
      public  Expression calculate(List<Expression> expressions){
          ThrowExceptionOnTooManyArguments("REF", 1, expressions.size());
          return (expressions.getFirst());
      }
    };

    // Method to convert a string to the corresponding Operation enum using validation in the exception class
    public static Operation fromString(String operation) {
        return UnknownOperationTypeException.validateOperation(operation);
    }

    private static void ThrowExceptionOnTooManyArguments(String operation, int expected, int actual) {
        if(actual != expected){
            throw new TooManyArgumentsException("Too many arguments for operation: " + operation , expected, actual);
        }
    }
    // Abstract method that each enum constant must implement
    public abstract Expression calculate(List<Expression> expressions);
}