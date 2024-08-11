

public interface Engine {

    void updateGivenCell();
    CellImp getRequestedCell(char row, char col);
    SheetCellImp getSheetCell();
    SheetCellImp getSheetCell(int versionNumber);
    void readSheetCellFromXML();
    void updateCell(String newValue, char row, char col);

}
