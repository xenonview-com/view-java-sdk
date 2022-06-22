package xenon.view.sdk.api;

import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ApiBase implements Fetchable{
    private final String name;
    private final String method;
    private final Map<?,?> headers;
    private final String path;
    private final String apiUrl;
    private final boolean skipName;
    private final boolean authenticated;
    private Fetchable jsonFetcher;

    ApiBase(Map<String, Object> props){
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
    }

    ApiBase(Map<String, Object> props, Fetchable _jsonFetcher ){
        this(props);
        jsonFetcher = _jsonFetcher;
    }

    public JSONObject params(JSONObject data) throws Throwable {
        return data;
    }

    public String path(JSONObject data){
        return path;
    }


    public CompletableFuture<JSONObject> fetch(JSONObject data) {
        final String fetchUrl = apiUrl +"/" + path(data);
        JSONObject fetchParameters = new JSONObject() {{
            put("url", fetchUrl);
            put("method", method);
        }};

        if (!data.isEmpty() || !skipName) {
            JSONObject bodyObject = new JSONObject();
            if (!skipName) bodyObject.put("name", name);
            JSONObject parameters;
            try {
                parameters = params(data);
            } catch (Throwable err) {
                CompletableFuture<JSONObject> rejected = new CompletableFuture<>();
                rejected.completeExceptionally(err);
                return rejected;
            }
            bodyObject.put("parameters", parameters);
            fetchParameters.put("body", bodyObject);
        }

        Map<String, String> requestHeaders = new Hashtable<>();
        for (Map.Entry<?, ?> entry : headers.entrySet()) {
            requestHeaders.put(entry.getKey().toString(), entry.getValue().toString());
        }

        if (authenticated) {
            Throwable exception = new Throwable("No token and authenticated!");
            CompletableFuture<JSONObject> rejected = new CompletableFuture<>();
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
