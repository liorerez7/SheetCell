package Controller.DashboardScreen;

import Utility.Constants;
import Utility.HttpUtility.HttpRequestManager;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TimerTask;
import java.util.function.Consumer;

public class SheetNamesRefresher extends TimerTask {

    private final Consumer<Set<String>> sheetNamesConsumer;
    private final Consumer<String> errorHandler;

    public SheetNamesRefresher(Consumer<Set<String>> sheetNamesConsumer, Consumer<String> errorHandler) {
        this.sheetNamesConsumer = sheetNamesConsumer;
        this.errorHandler = errorHandler;
    }

    @Override
    public void run() {
        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_ALL_SHEET_NAMES, new HashMap<>(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Platform.runLater(() -> errorHandler.accept("Failed to get sheet names from server: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String sheetNamesAsJson = response.body().string();
                    Set<String> sheetNames = Constants.GSON_INSTANCE.fromJson(sheetNamesAsJson, Set.class);
                    Platform.runLater(() -> sheetNamesConsumer.accept(sheetNames));
                } else {
//                    String errorMessageAsJson = response.body().string();
//                    String error = Constants.GSON_INSTANCE.fromJson(errorMessageAsJson, String.class);
//                    Platform.runLater(() -> errorHandler.accept(error));
                }
            }
        });
    }
}
