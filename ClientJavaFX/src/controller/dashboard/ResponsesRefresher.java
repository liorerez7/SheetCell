package controller.dashboard;

import com.google.gson.reflect.TypeToken;
import dto.permissions.RequestPermission;
import dto.permissions.ResponsePermission;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import utilities.Constants;
import utilities.http.manager.HttpRequestManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ResponsesRefresher extends TimerTask {

    private final Consumer<Integer> myPermissionResponsesConsumer;

    public ResponsesRefresher(Consumer<Integer> myPermissionResponsesConsumer) {
        this.myPermissionResponsesConsumer = myPermissionResponsesConsumer;
    }

    @Override
    public void run() {
        HttpRequestManager.sendGetAsyncRequest(Constants.MY_RESPONSE_PERMISSION, new HashMap<>(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        String myResponsesAsJson = responseBody.string();
                        Type myResponsesListType = new TypeToken<List<ResponsePermission>>() {}.getType();
                        List<ResponsePermission> myRequests = Constants.GSON_INSTANCE.fromJson(myResponsesAsJson, myResponsesListType);
                        int responsesThatWereNotAnswered = (int) myRequests.stream().filter(request -> !request.getWasAnswered()).count();
                        myPermissionResponsesConsumer.accept(responsesThatWereNotAnswered);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
