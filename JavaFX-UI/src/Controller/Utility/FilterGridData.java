package Controller.Utility;

public class FilterGridData {

    String columnsToFilterBy;
    String range;

    public void setColumnsToFilterBy(String columns) {
        this.columnsToFilterBy = columns;
    }

    public void setRange(String range) {
        this.range = range;
    }
    public String getColumnsToFilterBy() {
        return columnsToFilterBy;
    }

    public String getRange() {
        return range;
    }
}
