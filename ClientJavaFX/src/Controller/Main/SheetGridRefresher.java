package Controller.Main;

import Utility.Constants;
import Controller.HttpUtility.HttpRequestManager;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SheetGridRefresher extends TimerTask {

    private final Consumer<Void> updateSheetCallback;
    private final Supplier<String> getVersionNumber;

    public SheetGridRefresher(Consumer<Void> updateSheetCallback, Supplier<String> versionNumberCallBack) {
        this.updateSheetCallback = updateSheetCallback;
        this.getVersionNumber = versionNumberCallBack;
    }

    @Override
    public void run() {

        String sheetVersion = getVersionNumber.get(); // Use the supplier to get the sheet version
        Map<String,String> params = new HashMap<>();
        params.put("sheetVersion", String.valueOf(sheetVersion));

        HttpRequestManager.sendGetRequestASyncWithCallBack(Constants.GET_IS_SHEET_UPDATED, params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Log the error or handle it appropriately
                System.err.println("Failed to get update status: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                boolean isSheetUpdatedBool = false;

                if (response.isSuccessful()) {
                    String isSheetUpdatedAsJson = response.body().string();
                    String isSheetUpdated = Constants.GSON_INSTANCE.fromJson(isSheetUpdatedAsJson, String.class);
                    if(Objects.equals(isSheetUpdated, "true")) {
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
