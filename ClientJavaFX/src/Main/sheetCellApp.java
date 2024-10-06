package Main;

import Controller.Main.MainController;
import Controller.login.LoginController;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import com.sun.tools.rngom.digested.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class sheetCellApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the main FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/Main/SheetCell.fxml"));
        Parent root = loader.load();

        MainController mainController = loader.getController(); // Get the MainController instance
        mainController.setStage(stage);          // Set the stage reference
        mainController.setEngine(new EngineImpl()); // Set the engine reference

        // Initialize and load screens
        mainController.showLoginScreen();    // Show the login screen first
    }

    public static void main(String[] args) {
        launch(args);
    }
}