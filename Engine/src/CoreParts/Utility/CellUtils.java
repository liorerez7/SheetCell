package CoreParts.Utility;

import CoreParts.impl.CellImp;
import Operation.impl.Operation;
import Operation.impl.Number;

import java.util.List;

public class CellUtils {

    public static boolean trySetNumericValue(CellImp cell, String value) {
        try {
            Double numericValue = Double.parseDouble(value);
            cell.setEffectiveValue(new Number(numericValue));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void processFunction(CellImp cell, String newValue) {
        String funcName = extractFunctionName(newValue);
        try {
            Operation operation = Operation.fromString(funcName);
            List<CellImp> cells = getCellsFromNewValue(newValue);
            cell.setEffectiveValue(operation.calculate(cells));
        } catch (IllegalArgumentException e) {
            // Handle invalid function names or operations here
        }
    }

    public static boolean isPotentialOperation(String newValue) // TODO : implement function
    {
        return true;
    }



}
