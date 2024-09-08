package Utility.Exception;

public class RangeDoesntExist extends RuntimeException {
    String rangeName;

    public RangeDoesntExist(String rangeName) {
        this.rangeName = rangeName;
    }

    @Override
    public String getMessage() {
        return "Range: '" + rangeName + "' doesn't exist";
    }
}
