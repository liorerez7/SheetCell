package Main;

import Controller.Main.MainController;
import Controller.login.LoginController;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
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
        // Load the main FXML, but only to set up the controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/Main/SheetCell.fxml"));
        Parent root = loader.load();

        MainController mainController = loader.getController();
        mainController.setStage(stage);  // Set the stage reference
        mainController.setEngine(new EngineImpl()); // Set the engine reference
        mainController.showLoginScreen(); // Show the login screen

    }

    public static void main(String[] args) {
        launch(args);
    }
}



//        Scene scene = new Scene(root, 1515, 770);
//        stage.setScene(scene);
//        stage.show();

//    @Override
//    public void start(Stage primaryStage) throws Exception {
//
//        URL loginPage = getClass().getResource("/Controller/login/login.fxml");
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            fxmlLoader.setLocation(loginPage);
//            Parent root = fxmlLoader.load();
//            LoginController loginController = fxmlLoader.getController();
//
//            Scene scene1 = new Scene(root, 700, 600);
//            primaryStage.setScene(scene1);
//            primaryStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }