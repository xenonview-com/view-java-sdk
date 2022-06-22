/**
 * Created by lwoydziak on 06/20/22.
 *
 * Fetchable.java
 *
 * Interface for API fetch.
 *
 **/
package xenon.view.sdk.api;

import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public interface Fetchable {
    CompletableFuture<JSONObject> fetch(JSONObject params);
}
