package Operation.impl;
import CoreParts.impl.CellImp;
import expressions.Expression;

import java.util.List;

/*
   TODO : 1.extract the Cells from listofCells and pass them to the operations
          2. add all the new operations to the enum

 */
public enum Operation {

    PLUS {
        @Override
        public Expression calculate(List<CellImp> listOfCells) {
            return new Plus(cell1.getEffectiveValue(), cell2.getEffectiveValue());
        }
    },

    MINUS {
        @Override
        public Expression calculate(List<CellImp> listOfCells) {

            return new Minus(cell1.getEffectiveValue(), cell2.getEffectiveValue());
        }
    };

    // Method to convert a string to the corresponding Operation enum
    public static Operation fromString(String operation) {
        return valueOf(operation.toUpperCase());
    }

    // Abstract method that each enum constant must implement
    public abstract Expression calculate(List<CellImp> listOfCells);
}