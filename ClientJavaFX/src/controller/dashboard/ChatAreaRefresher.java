package controller.dashboard;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import utilities.Constants;
import utilities.http.manager.HttpRequestManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utilities.Constants.GSON_INSTANCE;

public class ChatAreaRefresher extends TimerTask {

    private final Consumer<ChatLinesWithVersion> chatlinesConsumer;
    private final IntegerProperty chatVersion;


    public ChatAreaRefresher(IntegerProperty chatVersion, Consumer<ChatLinesWithVersion> chatlinesConsumer) {
        this.chatlinesConsumer = chatlinesConsumer;
        this.chatVersion = chatVersion;
    }

    @Override
    public void run() {

        Map<String,String> params = new HashMap<>();
        params.put("chatversion", String.valueOf(chatVersion.get()));

        HttpRequestManager.sendGetAsyncRequest(Constants.SEND_CHAT_LINE, params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Something went wrong with Chat Request");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String rawBody = responseBody.string();
                        ChatLinesWithVersion chatLinesWithVersion = GSON_INSTANCE.fromJson(rawBody, ChatLinesWithVersion.class);
                        chatlinesConsumer.accept(chatLinesWithVersion);
                    }
                } catch (IOException e) {
                    System.out.println("Error processing chat response");
                }
            }
        });
    }
}
