/**
 * Created by lwoydziak on 06/27/22.
 * <p>
 * DeanonymizeApi.java
 * <p>
 * Deanonymize API interactions with Xenon View.
 **/
package xenon.view.sdk.api;

import org.json.JSONObject;
import xenon.view.sdk.api.fetch.Fetchable;

import java.util.Hashtable;

public class DeanonymizeApi extends ApiBase {
    public DeanonymizeApi(String apiUrl) {
        this(apiUrl, null);
    }

    public DeanonymizeApi(String apiUrl, Fetchable jsonFetcher) {
        super(new Hashtable<String, Object>() {{
            put("name", "ApiDeanonymize");
            put("url", "deanonymize");
            put("apiUrl", apiUrl);
            put("authenticated", true);
        }}, jsonFetcher);
    }

    @Override
    public JSONObject params(JSONObject data) throws Throwable {
        JSONObject local = super.params(data);
        return new JSONObject() {{
            put("uuid", local.getString("id"));
            put("timestamp", local.getDouble("timestamp"));
            put("person", local.getJSONObject("person"));
        }};
    }
}