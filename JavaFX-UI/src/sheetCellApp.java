import Controller.Grid.GridController;
import Controller.Main.MainController;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

import static javafx.application.Application.launch;

public class sheetCellApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        URL location = getClass().getResource("Controller/Main/SheetCell.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(location);
        Parent root = loader.load();
        MainController mainController = loader.getController();
        mainController.setEngine(new EngineImpl());
        Scene scene = new Scene(root, 1001, 800);
        stage.setScene(scene);
        stage.show();
    }
    private static void main(String[] args) {
        launch(args);
    }
}