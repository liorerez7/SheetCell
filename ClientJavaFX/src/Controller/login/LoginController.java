package Controller.login;

import Controller.HttpUtility.Constants;
import Controller.Main.MainController;
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
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import Controller.HttpUtility.http.HttpClientUtil;

import java.io.IOException;

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
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        HttpClientUtil.setCookieManagerLoggingFacility(line ->
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

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();

        updateHttpStatusLine("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback() {

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
//                            chatAppMainController.updateUserName(userName);
//                            chatAppMainController.switchToChatRoom();
                        mainController.showMainAppScreen();
                        System.out.println("Login successful");
                    });
                }
            }
        });
    }

    private void updateHttpStatusLine(String data) {
       // mainController.updateHttpLine(data);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}





//
//
//    private void userNameKeyTyped(KeyEvent event) {
//        errorMessageProperty.set("");
//    }
//
//    private void quitButtonClicked(ActionEvent e) {
//        Platform.exit();
//    }