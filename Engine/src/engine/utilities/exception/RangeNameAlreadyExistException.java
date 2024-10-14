package engine.utilities.exception;

public class RangeNameAlreadyExistException extends RuntimeException {
    String rangeName;

    public RangeNameAlreadyExistException(String rangeName) {
        this.rangeName = rangeName;
    }

    @Override
    public String getMessage() {
        return "Range: '" + rangeName + "' already exist";
    }
}
