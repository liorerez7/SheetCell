package CoreParts.api.sheet;

public interface GetSheetMetaData {
    int getCellLength();
    int getCellWidth();
    int getNumberOfRows();
    int getNumberOfColumns();
    String getSheetName();
    int getLatestVersion();
    int getActiveCellsCount();
}
