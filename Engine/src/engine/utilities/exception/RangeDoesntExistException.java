package engine.utilities.exception;

public class RangeDoesntExistException extends RuntimeException {
    String rangeName;

    public RangeDoesntExistException(String rangeName) {
        this.rangeName = rangeName;
    }

    @Override
    public String getMessage() {
        return "Range: '" + rangeName + "' doesn't exist";
    }
}
