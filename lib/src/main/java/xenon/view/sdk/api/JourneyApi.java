/**
 * Created by lwoydziak on 06/27/22.
 * <p>
 * JourneyApi.java
 * <p>
 * Journey API interactions with Xenon View.
 **/
package xenon.view.sdk.api;

import org.json.JSONObject;
import xenon.view.sdk.api.fetch.Fetchable;

import java.util.Hashtable;

public class JourneyApi extends ApiBase {
    public JourneyApi(String apiUrl) { this(apiUrl, null); }

    public JourneyApi(String apiUrl, Fetchable jsonFetcher) {
        super(new Hashtable<String, Object>() {{
            put("name", "ApiJourney");
            put("url", "journey");
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
        if (!local.has("journey")) return formated;
        formated.put("journey", local.getJSONArray("journey"));
        return formated;
    }
}
