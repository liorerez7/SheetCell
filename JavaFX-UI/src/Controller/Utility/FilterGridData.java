package Controller.Utility;

public class FilterGridData extends BaseRangeData {
    private String columnsToFilterBy;

    public void setColumnsToFilterBy(String columns) {
        this.columnsToFilterBy = columns;
    }

    public String getColumnsToFilterBy() {
        return columnsToFilterBy;
    }
}
