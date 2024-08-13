package CoreParts.smallParts;

public class CellLocation {
    private char visualColumn;
    private char visuaRow;
    private int realRow;
    private int realColumn;

    public CellLocation(char col, char row) {
        this.visuaRow = row;
        this.visualColumn = col;
        realRow = visuaRow - '1';
        realColumn = visualColumn - 'A';
    }

    public static CellLocation fromCellId(String cellId) {
        if (cellId.length() != 2) {
            throw new IllegalArgumentException("Invalid cell id");
        }
        return new CellLocation(cellId.charAt(0), cellId.charAt(1));
    }

    public static CellLocation fromCellId(char col, char row) {
        String cellId = "" + col + row;

        return fromCellId(cellId);
    }

    public int getRealColumn() {
        return realColumn;
    }

    public int getRealRow() {
        return realRow;
    }
    public char getVisualColumn() {
        return visualColumn;
    }
    public char getVisualRow() {
        return visuaRow;
    }
}
