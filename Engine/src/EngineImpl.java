public class EngineImpl implements Engine
{
    SheetCellImp sheetCellImp = new SheetCellImp(4,5);



    @Override
    public void updateGivenCell() {

    }

    @Override
    public CellImp getRequestedCell(char row, char col) {
        return null;
    }

    @Override
    public SheetCellImp getSheetCell() {
        return null;
    }

    @Override
    public SheetCellImp getSheetCell(int versionNumber) {
        return null;
    }

    @Override
    public void readSheetCellFromXML() {

    }

    @Override
    public void updateCell(String newValue, char row, char col) {
        String newValuee = "{Sum A3, A5}";
        String funcName = "Sum";

        Cell newCell1 = newCell(A3);
        Cell newCell2 = newCell(A5);

        if(funcName == "Sum")
        {
            Function function = new SumFunction(newCell1, newCell);
            int.Prase(function.evaluate()); // returned example : "37"

        }
        else if (funcName == "concat")
        {
            Function function = new ConcateFunction();

        }
    }
}
