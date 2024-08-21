package CoreParts.smallParts;

import java.io.Serializable;
import java.util.Objects;

public class CellLocation implements Serializable { // TODO: Implement the CellLocation interface maybe change name to Location.
    private char visualColumn;
    private char visualRow;
    private int realRow;
    private int realColumn;

    /*TODO:maybe make constructor private and crate a factory class
               that can be used to create the object
      */
    public CellLocation(char col, char row) {
        this.visualRow = row;
        this.visualColumn = col;
        realRow = visualRow - '1';
        realColumn = visualColumn - 'A';

    }

//    public static CellLocation fromCellId(String cellId) {
//
//
//
//        if (cellId.length() != 2) {
//            throw new IllegalArgumentException("Invalid cell id");
//        }
//        return new CellLocation(cellId.charAt(0), cellId.charAt(1));
//    }


    public String getCellId() {
        return "" + visualColumn + visualRow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellLocation that = (CellLocation) o;
        return visualColumn == that.visualColumn &&
                visualRow == that.visualRow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(visualColumn, visualRow);
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
        return visualRow;
    }
}
