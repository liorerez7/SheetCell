package Controller.Utility;

public class RunTimeAnalysisData {

    private String cellId;
    private int startingValue;
    private int endingValue;

    public RunTimeAnalysisData(String cellId, int startingValue, int endingValue) {
        this.cellId = cellId;
        this.startingValue = startingValue;
        this.endingValue = endingValue;
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


}
