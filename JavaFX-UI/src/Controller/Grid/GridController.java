package Controller.Grid;

import Controller.Main.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class GridController {
    @FXML
    private GridPane grid;
    private MainController mainController;

    @FXML
    public void initializeGrid(int numRows, int numCols) {
        // Clear existing constraints and children
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        grid.getChildren().clear();

        // Create column constraints
        for (int i = 0; i < numCols + 1; i++) { // +1 for header column
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setMinWidth(30); // Adjust width for better visibility
            colConstraints.setPrefWidth(100);
            colConstraints.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
            grid.getColumnConstraints().add(colConstraints);
        }

        // Create row constraints
        for (int i = 0; i < numRows + 1; i++) { // +1 for header row
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(30); // Adjust height for better visibility
            rowConstraints.setPrefHeight(30);
            rowConstraints.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
            grid.getRowConstraints().add(rowConstraints);
        }

        // Add column headers
        for (int col = 1; col <= numCols; col++) {
            Label header = new Label(String.valueOf((char) ('A' + col - 1)));
            grid.add(header, col, 0);
        }

        // Add row headers
        for (int row = 1; row <= numRows; row++) {
            Label header = new Label(String.valueOf(row));
            grid.add(header, 0, row);
        }

        // Add cells with AnchorPane and TextField
        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {
                AnchorPane anchorPane = new AnchorPane();
                TextField textField = new TextField();
                anchorPane.getChildren().add(textField);
                GridPane.setConstraints(anchorPane, col, row);
                grid.add(anchorPane, col, row);
            }
        }
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
