package Controller.Main;

import Controller.HttpUtility.Constants;
import Controller.HttpUtility.HttpRequestManager;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.function.Consumer;

public class SheetGridRefresher extends TimerTask {

    private final Consumer<Void> updateSheetCallback;

    public SheetGridRefresher(Consumer<Void> updateSheetCallback) {
        this.updateSheetCallback = updateSheetCallback;
    }

    @Override
    public void run() {
        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_IS_SHEET_UPDATED, new HashMap<>(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Log the error or handle it appropriately
                System.err.println("Failed to get update status: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String isSheetUpdatedAsJson = response.body().string();
                    boolean isSheetUpdated = Constants.GSON_INSTANCE.fromJson(isSheetUpdatedAsJson, Boolean.class);
                    if (isSheetUpdated) {
                        // Run the callback on the JavaFX Application Thread only if the sheet is updated
                        Platform.runLater(() -> updateSheetCallback.accept(null));
                    }
                } else {
                    // Handle non-successful HTTP response codes
                    System.err.println("Server returned an error: " + response.code());
                }
            }
        });
    }
}
