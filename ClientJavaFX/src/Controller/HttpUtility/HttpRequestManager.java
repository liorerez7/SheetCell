package Controller.HttpUtility;

import okhttp3.*;

import java.util.Map;

public class HttpRequestManager {


    private static final OkHttpClient client = new OkHttpClient();

    public static void sendGetRequest(String endpoint, Map<String, String> params, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(endpoint).newBuilder();

        // Add query parameters
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        String finalUrl = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void sendPostRequest(String endpoint, Map<String, String> params, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(endpoint).newBuilder();

        // Add query parameters
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        String finalUrl = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(RequestBody.create(null, new byte[0]))  // Empty POST body (or add data as needed)
                .build();

        client.newCall(request).enqueue(callback);
    }
}

