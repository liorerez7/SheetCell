package Controller.HttpUtility;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public class HttpRequestManager {


    private static final OkHttpClient client = new OkHttpClient();

    public static void sendGetRequestASyncWithCallBack(String endpoint, Map<String, String> params, Callback callback) {
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

    public static void sendPostRequestASyncWithCallBack(String endpoint, Map<String, String> params, Callback callback) {
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

    public static Response sendPostRequestSync(String url, Map<String, String> params) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        Request request = new Request.Builder()
                .url(url)
                .post(formBuilder.build())
                .build();

        return client.newCall(request).execute();
    }

    public static Response sendGetRequestSync(String url, Map<String, String> params) throws IOException {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            httpBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .get()
                .build();

        return client.newCall(request).execute();
    }
}
