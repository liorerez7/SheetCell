package controller.login;

import javafx.animation.FadeTransition;
import javafx.util.Duration;
import utilities.Constants;
import controller.main.MainController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import utilities.http.manager.HttpRequestManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginController {


    @FXML
    private TextField userNameTextField;

    @FXML
    private Button loginButton;

    @FXML
    private Button quitButton;

    @FXML
    private Label errorMessageLabel;

    private MainController mainController;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {

        userNameTextField.setPromptText("Enter your username");
        errorMessageLabel.textProperty().bind(errorMessageProperty);

        // Start fade-in animation for welcome label
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), welcomeLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();


        HttpRequestManager.setCookieManagerLoggingFacility(line ->
                Platform.runLater(() ->
                        updateHttpStatusLine(line)));
    }

    @FXML
    void loginButtonClicked(ActionEvent event) {

        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        // Create a map for the query parameters
        Map<String, String> params = new HashMap<>();
        params.put("username", userName);

        updateHttpStatusLine("New request is launched for login");

        HttpRequestManager.sendGetAsyncRequest(Constants.LOGIN_PAGE, params, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        mainController.setUserName(userName);
                        mainController.showDashBoardScreen(userName);
                        System.out.println("Login successful");
                    });
                }
            }
        });
    }

    @FXML
    void quitButtonClicked(ActionEvent event) {
        Platform.exit();
    }

    private void updateHttpStatusLine(String data) {
       // mainController.updateHttpLine(data);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

