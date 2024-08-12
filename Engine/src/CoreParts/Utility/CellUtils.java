package CoreParts.Utility;

import CoreParts.impl.CellImp;
import CoreParts.impl.EngineImpl;
import Operation.impl.Operation;
import Operation.impl.Number;

import java.util.ArrayList;
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
        if (newValue.startsWith("{") && newValue.endsWith("}")) {
            return true;
        }
        else{
            return false;
        }
    }

    private static String extractFunctionName(String input) {


        String content = input.substring(1, input.length() - 1).trim();
        // Split the content by space
        String[] parts = content.split(" ");

        if (parts.length > 0) {
            return parts[0]; // The function name is the first part
        }

        throw new IllegalArgumentException("Invalid function format");
    }

    private static List<CellImp> getCellsFromNewValue(String newValue) {

        //newValue can be "{Plus A5, A8}
        //newValue can be "{Plus A5, A8, A9}
        //newValue can be "{Plus A5, A8, A9, A10}

        //my goal is to create using getCell the cells A5, A8, A9, A10
        // for example : getCell('A', '5') will return the cell A5
        //then i will add it into the list of cells




        List<CellImp> cells = new ArrayList<>();
        CellImp cell = getCell('A', '7');



    }

}
