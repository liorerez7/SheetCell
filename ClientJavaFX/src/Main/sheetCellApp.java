//package Main;
//
//import Controller.Main.MainController;
//import Controller.login.LoginController;
//import CoreParts.impl.InnerSystemComponents.EngineImpl;
//import com.sun.tools.rngom.digested.Main;
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.net.URL;
//
//public class sheetCellApp extends Application {
//
//    @Override
//    public void start(Stage stage) throws Exception {
//        // Load the main FXML
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/Main/SheetCell.fxml"));
//        Parent root = loader.load();
//
//        MainController mainController = loader.getController(); // Get the MainController instance
//        mainController.setStage(stage);          // Set the stage reference
//        mainController.setEngine(new EngineImpl()); // Set the engine reference
//
//        // Initialize and load screens
//        mainController.showLoginScreen();    // Show the login screen first
//
//
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}


package Main;

import Controller.HttpUtility.Constants;
import Controller.Main.MainController;
import Controller.login.LoginController;
import CoreParts.impl.InnerSystemComponents.EngineImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class sheetCellApp extends Application {

    private Stage stage;
    private Parent mainAppRoot;
    private Parent loginRoot;
    private MainController mainController;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        // Load the main application screen FXML and controller
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(Constants.MAIN_APP_PAGE_FXML_RESOURCE_LOCATION));
        mainAppRoot = mainLoader.load();
        mainController = mainLoader.getController();
        mainController.setStage(stage);
        mainController.setApp(this);


        // Load the login screen FXML and controller
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource(Constants.LOGIN_PAGE_FXML_RESOURCE_LOCATION));
        loginRoot = loginLoader.load();
        LoginController loginController = loginLoader.getController();
        loginController.setMainController(mainController);

        // Initially show the login screen
        showLoginScreen();

        stage.show();
    }

    public void showLoginScreen() {
        if (stage.getScene() == null) {
            stage.setScene(new Scene(loginRoot)); // Create a new scene with the login screen root
        } else {
            stage.getScene().setRoot(loginRoot);  // Switch to the login screen
        }
    }

    public void showMainAppScreen() {
        stage.getScene().setRoot(mainAppRoot);  // Switch to the main application screen
    }

    public static void main(String[] args) {
        launch(args);
    }
}
