package CoreParts.impl.DtoComponents;

import CoreParts.smallParts.CellLocation;

public class DtoLocation {

    private char visualColumn;
    private char visualRow;
    private int realRow;
    private int realColumn;

    public DtoLocation(char visualColumn, char visualRow) {
        this.visualColumn = visualColumn;
        this.visualRow = visualRow;
    }

    public DtoLocation(CellLocation location) {
        this.visualColumn = location.getVisualColumn();
        this.visualRow = location.getVisualRow();
        this.realColumn = location.getRealColumn();
        this.realRow = location.getRealRow();
    }

    public char getVisualColumn() {
        return visualColumn;
    }

    public char getVisualRow() {
        return visualRow;
    }

    public int getRealColumn() {
        return realColumn;
    }

    public int getRealRow() {
        return realRow;
    }

    public String getCellId() {
        return "" + visualColumn + visualRow;
    }
}
