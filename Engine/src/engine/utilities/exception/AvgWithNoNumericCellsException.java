package engine.utilities.exception;

public class AvgWithNoNumericCellsException extends RuntimeException {

    String CellId;

    public AvgWithNoNumericCellsException(String cellId) {
        CellId = cellId;
    }

    @Override
    public String getMessage() {
        return "AVG function cannot be calculate if there is no numeric cells";
    }

}
