package smallParts;

import java.io.Serializable;
import java.util.Objects;

public class CellLocation implements Serializable { // TODO: Implement the CellLocation interface maybe change name to Location.
    //private static final long serialVersionUID = 1L; // Add serialVersionUID
    private char visualColumn;
    private String visualRow;
    private int realRow;
    private int realColumn;

    /*TODO:maybe make constructor private and crate a factory class
               that can be used to create the object
      */
    public CellLocation(char col, String row) {
        this.visualRow = row;
        this.visualColumn = col;

        realRow = (Integer.parseInt(row) -1);

        realColumn = visualColumn - 'A';

    }

    public String getCellId() {
        return "" + visualColumn + visualRow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellLocation that = (CellLocation) o;
        return visualColumn == that.visualColumn &&
                visualRow.equals(that.visualRow); // Changed to equals for Strings
    }

    @Override
    public int hashCode() {
        return Objects.hash(visualColumn, visualRow);
    }


//    @Override
//    public String toString() {
//        return "CellLocation{" +
//                "visualColumn=" + visualColumn +
//                ", visualRow='" + visualRow + '\'' +
//                ", realRow=" + realRow +
//                ", realColumn=" + realColumn +
//                '}';
//    }

    @Override
    public String toString() {
        return String.format("%c%s", visualColumn, visualRow);
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

    public String getVisualRow() {
        return visualRow;
    }
}
