package Operation.impl;
import CoreParts.impl.CellImp;
import expressions.Expression;

import java.util.List;

//    TODO : add all the new operations to the enum

public enum Operation {

    PLUS {
        @Override
        public Expression calculate(List<CellImp> listOfCells) {
            return new Plus(listOfCells.get(0).getEffectiveValue(), listOfCells.get(1).getEffectiveValue());
        }
    },

    MINUS {
        @Override
        public Expression calculate(List<CellImp> listOfCells) {
            return new Minus(listOfCells.get(0).getEffectiveValue(), listOfCells.get(1).getEffectiveValue());
        }
    },

    ABS{
        @Override
        public Expression calculate(List<CellImp> listOfCells) {
            return new Abs(listOfCells.get(0).getEffectiveValue());
        }
    };

    // Method to convert a string to the corresponding Operation enum
    public static Operation fromString(String operation) {
        return valueOf(operation.toUpperCase());
    }

    // Abstract method that each enum constant must implement
    public abstract Expression calculate(List<CellImp> listOfCells);
}