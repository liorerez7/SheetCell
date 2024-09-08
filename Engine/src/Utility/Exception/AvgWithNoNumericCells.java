package Utility.Exception;

public class AvgWithNoNumericCells extends RuntimeException {

    String CellId;

    public AvgWithNoNumericCells(String cellId) {
        CellId = cellId;
    }

    @Override
    public String getMessage() {
        return "AVG function cannot be calculate if there is no numeric cells";
    }

}
