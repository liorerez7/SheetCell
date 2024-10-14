package utilities.http.manager;

import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public class HttpRequestManager {

    private static final SimpleCookieManager simpleCookieManager = new SimpleCookieManager();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(simpleCookieManager)
            .followRedirects(false)
            .build();

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }

    private static HttpUrl buildUrlWithParams(String url, Map<String, String> params) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        return urlBuilder.build();
    }

    private static void sendAsyncRequest(String url, Map<String, String> params, String method, RequestBody body, Callback callback) {
        HttpUrl finalUrl = buildUrlWithParams(url, params);

        Request.Builder requestBuilder = new Request.Builder()
                .url(finalUrl);

        if ("POST".equalsIgnoreCase(method) && body != null) {
            requestBuilder.post(body);
        }

        client.newCall(requestBuilder.build()).enqueue(callback);
    }

    public static void sendGetAsyncRequest(String endpoint, Map<String, String> params, Callback callback) {
        sendAsyncRequest(endpoint, params, "GET", null, callback);
    }

    public static void sendPostAsyncRequest(String endpoint, Map<String, String> params, Callback callback) {
        // Creating an empty body for POST if no body is provided
        RequestBody body = RequestBody.create(null, new byte[0]);
        sendAsyncRequest(endpoint, params, "POST", body, callback);
    }

    private static Response sendSyncRequest(String url, Map<String, String> params, String method, RequestBody body) throws IOException {
        HttpUrl finalUrl = buildUrlWithParams(url, params);

        Request.Builder requestBuilder = new Request.Builder().url(finalUrl);
        if ("POST".equalsIgnoreCase(method) && body != null) {
            requestBuilder.post(body);
        }

        return client.newCall(requestBuilder.build()).execute();
    }

    public static Response sendGetSyncRequest(String url, Map<String, String> params) throws IOException {
        return sendSyncRequest(url, params, "GET", null);
    }

    public static Response sendPostSyncRequest(String url, Map<String, String> params) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }
        return sendSyncRequest(url, params, "POST", formBuilder.build());
    }

    public static Response sendFileSync(String url, File file) throws IOException {
        MediaType mediaType = MediaType.parse("application/xml");

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file1", file.getName(), RequestBody.create(file, mediaType))
                .build();

        Request request = new Request.Builder().url(url).post(body).build();
        return client.newCall(request).execute();
    }
}

