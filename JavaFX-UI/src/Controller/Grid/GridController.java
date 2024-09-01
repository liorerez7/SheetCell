package Controller.Grid;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class GridController {
    @FXML
    private GridPane gridPane;

    public void initializeGrid(int numRows, int numCols) {
        // Clear existing constraints and children
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        gridPane.getChildren().clear();

        // Create column constraints
        for (int i = 0; i < numCols; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setMinWidth(10);
            colConstraints.setPrefWidth(100);
            colConstraints.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
            gridPane.getColumnConstraints().add(colConstraints);
        }
        // Create row constraints
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(10);
            rowConstraints.setPrefHeight(30);
            rowConstraints.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
            gridPane.getRowConstraints().add(rowConstraints);
        }

        // Add cells (as Labels) to the grid
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Label cellLabel = new Label("Cell " + (row + 1) + "," + (col + 1));
                gridPane.add(cellLabel, col, row);
            }
        }
    }
}
