package controller.popup;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

public abstract class GridPopupBase {

    protected void openGridPopUp(String title, Consumer<GridPane> gridInitializer) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        GridPane popupGrid = new GridPane();
        popupGrid.getStylesheets().add("controller/grid/ExelBasicGrid.css");

        gridInitializer.accept(popupGrid);

        ScrollPane gridScrollPane = new ScrollPane(popupGrid);
        gridScrollPane.setFitToWidth(true);
        gridScrollPane.setFitToHeight(true);

        Scene popupScene = new Scene(gridScrollPane);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }
}