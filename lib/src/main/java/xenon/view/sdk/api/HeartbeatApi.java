/**
 * Created by lwoydziak on 06/27/22.
 * <p>
 * HeartbeatApi.java
 * <p>
 * Heartbeat API interactions with Xenon View.
 **/
package xenon.view.sdk.api;

import org.json.JSONObject;
import xenon.view.sdk.api.fetch.Fetchable;

import java.util.Hashtable;

public class HeartbeatApi extends ApiBase {
    public HeartbeatApi(String apiUrl) { this(apiUrl, null); }

    public HeartbeatApi(String apiUrl, Fetchable jsonFetcher) {
        super(new Hashtable<String, Object>() {{
            put("name", "ApiHeartbeat");
            put("url", "heartbeat");
            put("apiUrl", apiUrl);
            put("authenticated", true);
        }}, jsonFetcher);
    }

    @Override
    public JSONObject params(JSONObject data) throws Throwable {
        JSONObject local = super.params(data);
        JSONObject formated = new JSONObject() {{
            put("uuid", local.getString("id"));
            put("timestamp", local.getDouble("timestamp"));
        }};
        if (local.has("journey")) formated.put("journey", local.getJSONArray("journey"));
        if (local.has("platform")) formated.put("platform", local.getJSONObject("platform"));
        if (local.has("tags")) formated.put("tags", local.getJSONArray("tags"));
        return formated;
    }
}
