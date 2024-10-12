package Utility.JavaFXUtility;

public class RunTimeAnalysisData {

    private String cellId;
    private int startingValue;
    private int endingValue;
    private int stepValue;

    public RunTimeAnalysisData(String cellId, int startingValue, int endingValue, int stepValue) {
        this.cellId = cellId;
        this.startingValue = startingValue;
        this.endingValue = endingValue;
        this.stepValue = stepValue;
    }

    public String getCellId() {
        return cellId;
    }

    public int getStartingValue() {
        return startingValue;
    }

    public int getEndingValue() {
        return endingValue;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public void setStartingValue(int startingValue) {
        this.startingValue = startingValue;
    }

    public void setEndingValue(int endingValue) {
        this.endingValue = endingValue;
    }

    public int getStepValue() {
        return stepValue;
    }

    public void setStepValue(int stepValue) {
        this.stepValue = stepValue;
    }


}
