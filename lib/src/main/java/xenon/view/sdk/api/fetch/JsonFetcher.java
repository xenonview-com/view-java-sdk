/**
 * Created by lwoydziak on 06/22/22.
 *
 * JsonFetcher.js
 *
 * Class to fetch JSON APIs.
 */
package xenon.view.sdk.api.fetch;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class JsonFetcher implements Fetchable {
    private OkHttpClient client;
    private OkHttpClient.Builder builder = new OkHttpClient.Builder();

    public JsonFetcher(){
        client = new OkHttpClient();
    }

    public JsonFetcher(OkHttpClient _client){
        client = _client;
    }
    public JsonFetcher(OkHttpClient.Builder _builder, OkHttpClient _client){
        this(_client);
        builder = _builder;
    }

    public CompletableFuture<Json> fetch(JSONObject data) {
        CompletableFuture<Json> completableFuture = new CompletableFuture<>();
        Map<String, String> defaultHeaders = new Hashtable<String, String>(){{
            put("accept", "application/json");
        }};

        if(data.has("ignore-certificate-errors") && data.getBoolean("ignore-certificate-errors")){
            OkHttpClient saved = client;
            try {
                allowSelfSignedCert();
            } catch (Throwable err){
                System.out.println("Unable to use self signed cert: " + err.getMessage());
                client = saved;
            }
        }

        Request.Builder builder = new Request.Builder()
                .url(data.getString("url"));


        addHeaders(builder, defaultHeaders);
        if (data.has("requestHeaders")) addHeaders(builder, (JSONObject) data.get("requestHeaders"));

        if (data.has("method")){
            switch (data.getString("method")){
                case "POST":
                    JSONObject jsonBody = data.getJSONObject("body");
                    String body = jsonBody.toString();
                    builder.post(RequestBody.create(body, MediaType.parse("application/json")));
                    break;
                case "GET":
                default:
                    builder.get();
                    break;
            }
        }
        Request request = builder.build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //HTTP request exception
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!checkStatus(response, completableFuture)) return;
                try{
                    String body = responseBody(response);
                    Json json = new Json(body);
                    completableFuture.complete(json);
                } catch (Throwable err) {
                    completableFuture.completeExceptionally(err);
                }
            }
        });
        return completableFuture;
    }

    private void allowSelfSignedCert() throws NoSuchAlgorithmException, KeyManagementException {
        X509TrustManager TRUST_ALL_CERTS = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
        };

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[] { TRUST_ALL_CERTS }, new java.security.SecureRandom());
        client = builder
                .sslSocketFactory(sslContext.getSocketFactory(), TRUST_ALL_CERTS)
                .hostnameVerifier((hostname, session) -> true).build();
    }

    private String responseBody(@NotNull Response response) throws Throwable {
        try (ResponseBody responseBody = response.body()) {
            if (responseBody == null) throw (new Throwable("No response body."));
            return responseBody.string();
        }
    }

    private boolean checkStatus(@NotNull Response response, CompletableFuture<Json> completableFuture) {
        int status = response.code();
        if (status >= 200 && status < 400) return true;
        if (status >= 400 && status < 500) {
            try {
                completableFuture.completeExceptionally(new Throwable(responseBody(response)));
            } catch (Throwable err) {
                completableFuture.completeExceptionally(err);
            }
            return false;
        }
        try {
            completableFuture.completeExceptionally(new Throwable(response.message()));
        } catch (Throwable err) {
            completableFuture.completeExceptionally(err);
        }
        return false;
    }

    private void addHeaders(Request.Builder builder, Map<String, String> headers) {
        for(Map.Entry<String, String> entry : headers.entrySet()){
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    private void addHeaders(Request.Builder builder, JSONObject headers) {
        for(String key : headers.keySet()){
            builder.addHeader(key, headers.getString(key));
        }
    }
}
