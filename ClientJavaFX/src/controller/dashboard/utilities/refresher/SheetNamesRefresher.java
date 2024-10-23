package controller.dashboard.utilities.refresher;

import dto.components.DtoSheetInfoLine;
import utilities.Constants;
import utilities.http.manager.HttpRequestManager;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Set;
import java.util.TimerTask;
import java.util.function.Consumer;

public class SheetNamesRefresher extends TimerTask {

    private final Consumer<Set<DtoSheetInfoLine>> sheetNamesConsumer;
    private final Consumer<String> errorHandler;

    public SheetNamesRefresher(Consumer<Set<DtoSheetInfoLine>> sheetNamesConsumer, Consumer<String> errorHandler) {
        this.sheetNamesConsumer = sheetNamesConsumer;
        this.errorHandler = errorHandler;
    }

    @Override
    public void run() {

        HttpRequestManager.sendGetAsyncRequest(Constants.GET_ALL_SHEET_NAMES, new HashMap<>(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Handle failure
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String sheetInfosAsJson = response.body().string(); // Read the body content

                        Type permissionListType = new TypeToken<Set<DtoSheetInfoLine>>() {}.getType();
                        Set<DtoSheetInfoLine> sheetInfos = Constants.GSON_INSTANCE.fromJson(sheetInfosAsJson, permissionListType);

                        // Pass the sheet info to the UI thread
                        Platform.runLater(() -> sheetNamesConsumer.accept(sheetInfos));
                    } else {
                        // Handle the case when response is not successful
                    }
                } finally {
                    // Ensure the response is closed to avoid resource leaks
                    response.close();
                }
            }
        });


    }
}
