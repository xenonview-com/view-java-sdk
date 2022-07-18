/**
 * Created by lwoydziak on 06/20/22.
 *
 * ApiBase.java
 *
 * Interface for lazy instantiation of an API.
 *
 **/
package xenon.view.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;
import xenon.view.sdk.api.fetch.Fetchable;
import xenon.view.sdk.api.fetch.Fetcher;
import xenon.view.sdk.api.fetch.Json;
import xenon.view.sdk.api.fetch.JsonFetcher;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ApiBase implements Fetchable {
    private final String name;
    private final String method;
    private final Map<?,?> headers;
    private final String path;
    private final String apiUrl;
    private final boolean skipName;
    private final boolean authenticated;
    private Fetchable jsonFetcher;

    public ApiBase(Map<String, Object> props){
        name = props.get("name") != null ? props.get("name").toString():"ApiBase";
        method = props.get("method") != null ? props.get("method").toString():"POST";

        Map<String, String> defaultHeaders = new Hashtable<String, String>(){{
            put("content-type", "application/json");
        }};
        headers = props.get("headers") != null ?
                (Map<?,?>) props.get("headers") :
                defaultHeaders;
        path = props.get("url") != null ? props.get("url").toString():"";
        apiUrl = props.get("apiUrl") != null ? props.get("apiUrl").toString():"https://app.xenonview.com";
        skipName = props.get("skipName") != null && (boolean) props.get("skipName");
        authenticated = props.get("authenticated") != null && (boolean) props.get("authenticated");
        jsonFetcher = realFetcher(JsonFetcher::new);
    }

    public ApiBase(Map<String, Object> props, Fetchable _jsonFetcher ){
        this(props);
        if (_jsonFetcher != null) jsonFetcher = _jsonFetcher;
    }

    private Fetchable realFetcher(Fetcher api){
        return api.instance();
    }

    public JSONObject params(JSONObject data) throws Throwable {
        return data;
    }

    public String path(JSONObject data){
        return path;
    }


    public CompletableFuture<Json> fetch(JSONObject data) throws JSONException {
        final String fetchUrl = apiUrl +"/" + path(data);
        JSONObject fetchParameters = new JSONObject() {{
            put("url", fetchUrl);
            put("method", method);
        }};

        if (data.has("ignore-certificate-errors")){
            fetchParameters.put("ignore-certificate-errors", data.getBoolean("ignore-certificate-errors"));
        }

        if (data.length() != 0 || !skipName) {
            JSONObject bodyObject = new JSONObject();
            if (!skipName) bodyObject.put("name", name);
            JSONObject parameters;
            try {
                parameters = params(data);
            } catch (Throwable err) {
                CompletableFuture<Json> rejected = new CompletableFuture<>();
                rejected.completeExceptionally(err);
                return rejected;
            }
            bodyObject.put("parameters", parameters);
            fetchParameters.put("body", bodyObject);
        }

        JSONObject requestHeaders = new JSONObject();
        for (Map.Entry<?, ?> entry : headers.entrySet()) {
            requestHeaders.put(entry.getKey().toString(), entry.getValue().toString());
        }

        if (authenticated) {
            Throwable exception = new Throwable("No token and authenticated!");
            CompletableFuture<Json> rejected = new CompletableFuture<>();
            rejected.completeExceptionally(exception);
            if (!data.has("token")) return rejected;
            final String token = data.getString("token");
            if (token.equals("")) return rejected;
            requestHeaders.put("authorization", "Bearer " + token);
        }
        fetchParameters.put("requestHeaders", requestHeaders);
        return jsonFetcher.fetch(fetchParameters);
    }
}
