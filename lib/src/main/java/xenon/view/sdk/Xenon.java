/**
 * Created by lwoydziak on 06/20/22.
 *
 * Xenon.java
 *
 * SDK for interacting with the Xenon View service.
 *
 **/
package xenon.view.sdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import xenon.view.sdk.api.Api;
import xenon.view.sdk.api.DeanonymizeApi;
import xenon.view.sdk.api.fetch.Fetchable;
import xenon.view.sdk.api.JourneyApi;
import xenon.view.sdk.api.fetch.Json;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Xenon {
    static volatile private String _id = UUID.randomUUID().toString();
    static volatile private JSONArray _journey = new JSONArray();
    static volatile private String apiUrl = "https://app.xenonview.com";
    static volatile private String apiKey = "";
    static volatile private boolean allowSelfSigned = false;
    private Api<Fetchable> journeyApi;
    private Api<Fetchable> deanonApi;
    private JSONArray restoreJourney;

    public Xenon(){
        journeyApi = realApi(JourneyApi::new);
        deanonApi = realApi(DeanonymizeApi::new);
    }

    public Xenon(String _apiKey){
        this();
        Xenon.apiKey = _apiKey;
        Xenon.apiUrl = "https://app.xenonview.com";
    }

    public Xenon(String _apiKey, Api<Fetchable> _journeyApi){
        this();
        Xenon.apiKey = _apiKey;
        journeyApi = _journeyApi;
    }

    public Xenon(String _apiKey, boolean _allowSelfSigned){
        this(_apiKey);
        allowSelfSigned = _allowSelfSigned;
    }

    public Xenon(String _apiKey, String _apiUrl){
        this(_apiKey);
        apiUrl = _apiUrl;
    }

    public Xenon(String _apiKey, String _apiUrl, boolean _allowSelfSigned){
        this(_apiKey, _apiUrl);
        allowSelfSigned = _allowSelfSigned;
    }

    public Xenon(String _apiKey, String _apiUrl, Api<Fetchable> _journeyApi){
        this(_apiKey, _apiUrl);
        journeyApi = _journeyApi;
    }

    public Xenon(Api<Fetchable> _journeyApi){
        this();
        journeyApi = _journeyApi;
    }

    public Xenon(String _apiKey, String _apiUrl, Api<Fetchable> _journeyApi, boolean _allowSelfSigned){
        this(_apiKey, _apiUrl, _allowSelfSigned);
        journeyApi = _journeyApi;
    }

    public Xenon(String _apiKey, String _apiUrl, Api<Fetchable> _journeyApi, Api<Fetchable> _deanonApi, boolean _allowSelfSigned){
        this(_apiKey, _apiUrl, _journeyApi, _allowSelfSigned);
        deanonApi = _deanonApi;
    }

    public Xenon(String _apiKey, String _apiUrl, Api<Fetchable> _journeyApi, Api<Fetchable> _deanonApi){
        this(_apiKey, _apiUrl, _journeyApi);
        deanonApi = _deanonApi;
    }

    private Api<Fetchable> realApi(Api<Fetchable> api){
        return api;
    }

    public String id() {
        return Xenon._id;

    }

    public String id(String _id) {
        Xenon._id = _id;
        return Xenon._id;
    }


    public JSONArray journey() {
        return Xenon._journey;
    }

    public void init(String apiKey, String apiUrl){
        if(apiUrl.length() > 0 ) Xenon.apiUrl = apiUrl;
        if(apiKey.length() > 0 ) Xenon.apiKey = apiKey;
    }

    public void init(String apiKey){
        init(apiKey, "");
    }

    public void pageView(String page) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Page View");
        content.put("action", page);
        journeyAdd(content);
    }

    public void funnel(String stage, String action) throws JSONException{
        JSONObject content = new JSONObject();
        content.put("funnel", stage);
        content.put("action", action);
        journeyAdd(content);
    }

    public void outcome(String outcome, String action) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("outcome", outcome);
        content.put("action", action);
        journeyAdd(content);
    }

    public void event(JSONObject event) throws JSONException {
        if (!event.has("action")) {
            event.put("action", (new JSONObject(event.toString())));
        }
        if (!event.has("category") &&
                !event.has("funnel") &&
                !event.has("outcome")){
            event.put("category", "Event");
        }
        journeyAdd(event);
    }

    private void journeyAdd(JSONObject content) throws JSONException {
        JSONArray journey = journey();
        content.put("timestamp", timestamp());

        if (journey.length() > 0) {
            JSONObject last = journey.getJSONObject(journey.length() - 1);

            if ((last.has("funnel") && content.has("funnel")) ||
                    (last.has("category") && content.has("category"))) {
                if (last.get("action") != content.get("action")) {
                    journey.put(content);
                } else {
                    int count = content.has("count") ? content.getInt("count") : 1;
                    content.put("count", count+1);
                }
            } else {
                journey.put(content);
            }
        } else {
            journey = (new JSONArray()).put(content);
        }
        storeJourney(journey);
    }

    private double timestamp() {
        return System.currentTimeMillis() / 1000.0;
    }

    private void storeJourney(JSONArray journey) {
        Xenon._journey = journey;
    }

    public void reset() {
        this.restoreJourney = this.journey();
        Xenon._journey = new JSONArray();
    }

    public void restore() throws JSONException {
        JSONArray currentJourney = this.journey();
        JSONArray restoreJourney = this.restoreJourney;
        if (currentJourney.length() >0 ) {
            JSONArray result = new JSONArray();
            for (int i = 0; i < restoreJourney.length(); i++) {
                result.put(restoreJourney.get(i));
            }
            for (int i = 0; i < currentJourney.length(); i++) {
                result.put(currentJourney.get(i));
            }
            restoreJourney = result;
        }
        storeJourney(restoreJourney);
        this.restoreJourney = new JSONArray();
    }

    public CompletableFuture<Json> commit() throws JSONException, Throwable {
        JSONObject params = (new JSONObject())
                .put("id", id())
                .put("journey", journey())
                .put("token", apiKey)
                .put("timestamp", timestamp())
                .put("ignore-certificate-errors", allowSelfSigned);
        if (apiKey.equals("")) throw new Throwable("API Key not set.");
        reset();
        return journeyApi.instance(apiUrl).fetch(params)
                .exceptionally(err -> {
                    try {
                        restore();
                    } catch (JSONException doNotCare) {
                        // swallow
                    }
                    throw(new CompletionException(err));
                });
    }

    public CompletableFuture<Json> deanonymize(JSONObject person) throws JSONException, Throwable {
        JSONObject params = (new JSONObject())
                .put("id", id())
                .put("person", person)
                .put("token", apiKey)
                .put("timestamp", timestamp())
                .put("ignore-certificate-errors", allowSelfSigned);
        if (apiKey.equals("")) throw new Throwable("API Key not set.");
        return deanonApi.instance(apiUrl).fetch(params);
    }

    public boolean selfSignedAllowed(){
        return allowSelfSigned;
    }
}
