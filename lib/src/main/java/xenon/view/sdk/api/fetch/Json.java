package xenon.view.sdk.api.fetch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Json {
    private final String underlying;

    public Json(String _underlying) {
        underlying = _underlying;
    }

    @SuppressWarnings("unchecked")
    public <T> T to(Class<T> type) throws JSONException {
        if (type.equals(JSONObject.class)) {
            return (T) (underlying.equals("") ? new JSONObject() : new JSONObject(underlying));
        }
        if (type.equals(JSONArray.class)) {
            return (T) (underlying.equals("") ? new JSONArray() : new JSONArray(underlying));
        }
        return null;
    }

    public String toString() {
        return underlying;
    }
}
