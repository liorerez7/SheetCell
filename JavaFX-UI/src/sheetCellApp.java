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
        URL location = getClass().getResource("Controller/Main/sheetCell.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(location);
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
    private static void main(String[] args) {
        launch(args);
    }
}