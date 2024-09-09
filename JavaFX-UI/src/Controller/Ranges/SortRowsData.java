package Controller.Ranges;

public class SortRowsData {

    String columnsToSortBy;
    String range;

    public void setColumnsToSortBy(String columns) {
        this.columnsToSortBy = columns;
    }

    public void setRange(String range) {
        this.range = range;
    }
    public String getColumnsToSortBy() {
        return columnsToSortBy;
    }

    public String getRange() {
        return range;
    }
}
