/**
 * Created by lwoydziak on 06/20/22.
 *
 * Fetchable.java
 *
 * Interface for API fetch.
 *
 **/
package xenon.view.sdk.api.fetch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public interface Fetchable {
    CompletableFuture<Json> fetch(JSONObject params) throws JSONException;
}

