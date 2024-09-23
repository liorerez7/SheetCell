package Main;

import Controller.Main.MainController;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class sheetCellApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        URL location = getClass().getResource("/Controller/Main/SheetCell.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(location);
        Parent root = loader.load();
        MainController mainController = loader.getController();
        mainController.setEngine(new EngineImpl());


        Scene scene = new Scene(root, 1550, 770);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

