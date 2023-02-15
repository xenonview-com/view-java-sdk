/**
 * Created by lwoydziak on 06/20/22.
 * <p>
 * Xenon.java
 * <p>
 * SDK for interacting with the Xenon View service.
 **/
package xenon.view.sdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import xenon.view.sdk.api.Api;
import xenon.view.sdk.api.DeanonymizeApi;
import xenon.view.sdk.api.HeartbeatApi;
import xenon.view.sdk.api.JourneyApi;
import xenon.view.sdk.api.fetch.Fetchable;
import xenon.view.sdk.api.fetch.Json;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Xenon {
    static volatile private String _id = UUID.randomUUID().toString();
    static volatile private JSONArray _journey = new JSONArray();
    static volatile private String apiUrl = "https://app.xenonview.com";
    static volatile private String apiKey = "";
    static volatile private boolean allowSelfSigned = false;
    static volatile private JSONObject platform = new JSONObject();
    static volatile private JSONArray tags = new JSONArray();
    private Api<Fetchable> journeyApi;
    private Api<Fetchable> heartbeatApi;
    private Api<Fetchable> deanonApi;
    private JSONArray restoreJourney;

    public Xenon() {
        journeyApi = realApi(JourneyApi::new);
        deanonApi = realApi(DeanonymizeApi::new);
        heartbeatApi = realApi(HeartbeatApi::new);
    }

    public Xenon(String _apiKey) {
        this();
        Xenon.apiKey = _apiKey;
        Xenon.apiUrl = "https://app.xenonview.com";
    }

    public Xenon(String _apiKey, Api<Fetchable> _journeyApi) {
        this();
        Xenon.apiKey = _apiKey;
        journeyApi = _journeyApi;
    }

    public Xenon(String _apiKey, boolean _allowSelfSigned) {
        this(_apiKey);
        allowSelfSigned = _allowSelfSigned;
    }

    public Xenon(String _apiKey, String _apiUrl) {
        this(_apiKey);
        apiUrl = _apiUrl;
    }

    public Xenon(String _apiKey, String _apiUrl, boolean _allowSelfSigned) {
        this(_apiKey, _apiUrl);
        allowSelfSigned = _allowSelfSigned;
    }

    public Xenon(String _apiKey, String _apiUrl, Api<Fetchable> _journeyApi) {
        this(_apiKey, _apiUrl);
        journeyApi = _journeyApi;
    }

    public Xenon(Api<Fetchable> _journeyApi) {
        this();
        journeyApi = _journeyApi;
    }

    public Xenon(String _apiKey, String _apiUrl, Api<Fetchable> _journeyApi, boolean _allowSelfSigned) {
        this(_apiKey, _apiUrl, _allowSelfSigned);
        journeyApi = _journeyApi;
    }

    public Xenon(String _apiKey, String _apiUrl, Api<Fetchable> _journeyApi, Api<Fetchable> _deanonApi, boolean _allowSelfSigned) {
        this(_apiKey, _apiUrl, _journeyApi, _allowSelfSigned);
        deanonApi = _deanonApi;
    }

    public Xenon(String _apiKey, String _apiUrl, Api<Fetchable> _journeyApi, Api<Fetchable> _deanonApi) {
        this(_apiKey, _apiUrl, _journeyApi);
        deanonApi = _deanonApi;
    }

    public Xenon(String _apiKey, String _apiUrl, Api<Fetchable> _journeyApi, Api<Fetchable> _deanonApi, Api<Fetchable> _heartbeatApi) {
        this(_apiKey, _apiUrl, _journeyApi, _deanonApi);
        heartbeatApi = _heartbeatApi;
    }

    public void init(String apiKey, String apiUrl) {
        if (apiUrl.length() > 0) Xenon.apiUrl = apiUrl;
        if (apiKey.length() > 0) Xenon.apiKey = apiKey;
    }

    public void init(String apiKey) {
        init(apiKey, "");
    }

    public void platform(String softwareVersion, String deviceModel, String operatingSystemName,
                         String operatingSystemVersion) throws JSONException {
        Xenon.platform = new JSONObject() {{
            put("softwareVersion", softwareVersion);
            put("deviceModel", deviceModel);
            put("operatingSystemName", operatingSystemName);
            put("operatingSystemVersion", operatingSystemVersion);
        }};
    }

    public void removePlatform() {
        Xenon.platform = new JSONObject();
    }

    public void tag(JSONArray tags) throws JSONException {
        Xenon.tags = tags;
    }

    public void unTag() {
        Xenon.tags = new JSONArray();
    }

// Stock Business Outcomes:

    public void leadCaptured(String specifier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Lead Capture");
        content.put("outcome", specifier);
        content.put("result", "success");
        outcomeAdd(content);
    }

    public void leadCaptureDeclined(String specifier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Lead Capture");
        content.put("outcome", specifier);
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void accountSignup(String specifier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Account Signup");
        content.put("outcome", specifier);
        content.put("result", "success");
        outcomeAdd(content);
    }

    public void accountSignupDeclined(String specifier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Account Signup");
        content.put("outcome", specifier);
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void applicationInstalled() throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Application Installation");
        content.put("outcome", "Installed");
        content.put("result", "success");
        outcomeAdd(content);
    }

    public void applicationNotInstalled() throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Application Installation");
        content.put("outcome", "Not Installed");
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void initialSubscription(String tier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Initial Subscription");
        content.put("outcome", "Subscribe - " + tier);
        content.put("result", "success");
        outcomeAdd(content);
    }

    public void initialSubscription(String tier, String method) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Initial Subscription");
        content.put("outcome", "Subscribe - " + tier);
        content.put("result", "success");
        content.put("method", method);
        outcomeAdd(content);
    }

    public void subscriptionDeclined(String tier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Initial Subscription");
        content.put("outcome", "Decline - " + tier);
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void subscriptionDeclined(String tier, String method) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Initial Subscription");
        content.put("outcome", "Decline - " + tier);
        content.put("result", "fail");
        content.put("method", method);
        outcomeAdd(content);
    }

    public void subscriptionRenewed(String tier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Subscription Renewal");
        content.put("outcome", "Renew - " + tier);
        content.put("result", "success");
        outcomeAdd(content);
    }

    public void subscriptionRenewed(String tier, String method) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Subscription Renewal");
        content.put("outcome", "Renew - " + tier);
        content.put("result", "success");
        content.put("method", method);
        outcomeAdd(content);
    }

    public void subscriptionCanceled(String tier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Subscription Renewal");
        content.put("outcome", "Cancel - " + tier);
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void subscriptionCanceled(String tier, String method) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Subscription Renewal");
        content.put("outcome", "Cancel - " + tier);
        content.put("result", "fail");
        content.put("method", method);
        outcomeAdd(content);
    }

    public void subscriptionUpsold(String tier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Subscription Upsold");
        content.put("outcome", "Upsold - " + tier);
        content.put("result", "success");
        outcomeAdd(content);
    }

    public void subscriptionUpsold(String tier, String method) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Subscription Upsold");
        content.put("outcome", "Upsold - " + tier);
        content.put("result", "success");
        content.put("method", method);
        outcomeAdd(content);
    }

    public void subscriptionUpsellDeclined(String tier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Subscription Upsold");
        content.put("outcome", "Declined - " + tier);
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void subscriptionUpsellDeclined(String tier, String method) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Subscription Upsold");
        content.put("outcome", "Declined - " + tier);
        content.put("result", "fail");
        content.put("method", method);
        outcomeAdd(content);
    }

    public void referral(String kind) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Referral");
        content.put("outcome", "Referred - " + kind);
        content.put("result", "success");
        outcomeAdd(content);
    }

    public void referral(String kind, String detail) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Referral");
        content.put("outcome", "Referred - " + kind);
        content.put("result", "success");
        content.put("details", detail);
        outcomeAdd(content);
    }

    public void referralDeclined(String kind) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Referral");
        content.put("outcome", "Declined - " + kind);
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void referralDeclined(String kind, String detail) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Referral");
        content.put("outcome", "Declined - " + kind);
        content.put("result", "fail");
        content.put("details", detail);
        outcomeAdd(content);
    }

    public void productAddedToCart(String product) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Add Product To Cart");
        content.put("outcome", "Add - " + product);
        content.put("result", "success");
        outcomeAdd(content);
    }

    public void productNotAddedToCart(String product) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Add Product To Cart");
        content.put("outcome", "Ignore - " + product);
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void upsold(String product) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Upsold Product");
        content.put("outcome", "Upsold - " + product);
        content.put("result", "success");
        outcomeAdd(content);
    }

    public void upsellDismissed(String product) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Upsold Product");
        content.put("outcome", "Dismissed - " + product);
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void checkedOut() throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Customer Checkout");
        content.put("outcome", "Checked Out");
        content.put("result", "success");
        outcomeAdd(content);
    }

    public void checkoutCanceled() throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Customer Checkout");
        content.put("outcome", "Canceled");
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void productRemoved(String product) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Customer Checkout");
        content.put("outcome", "Product Removed - " + product);
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void purchased(String method) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Customer Purchase");
        content.put("outcome", "Purchase - " + method);
        content.put("result", "success");
        outcomeAdd(content);
    }

    public void purchaseCanceled(String method) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Customer Purchase");
        content.put("outcome", "Canceled - " + method);
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void purchaseCanceled() throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Customer Purchase");
        content.put("outcome", "Canceled");
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void promiseFulfilled() throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Promise Fulfillment");
        content.put("outcome", "Fulfilled");
        content.put("result", "success");
        outcomeAdd(content);
    }

    public void promiseUnfulfilled() throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Promise Fulfillment");
        content.put("outcome", "Unfulfilled");
        content.put("result", "fail");
        outcomeAdd(content);
    }

    public void productKept(String product) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Product Disposition");
        content.put("outcome", "Kept - " + product);
        content.put("result", "success");
        outcomeAdd(content);
    }

    public void productReturned(String product) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("superOutcome", "Product Disposition");
        content.put("outcome", "Returned - " + product);
        content.put("result", "fail");
        outcomeAdd(content);
    }

// Stock Milestones:

    public void featureAttempted(String name, String detail) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Feature");
        content.put("action", "Attempted");
        content.put("name", name);
        content.put("details", detail);
        journeyAdd(content);
    }

    public void featureAttempted(String name) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Feature");
        content.put("action", "Attempted");
        content.put("name", name);
        journeyAdd(content);
    }

    public void featureCompleted(String name, String detail) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Feature");
        content.put("action", "Completed");
        content.put("name", name);
        content.put("details", detail);
        journeyAdd(content);
    }

    public void featureCompleted(String name) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Feature");
        content.put("action", "Completed");
        content.put("name", name);
        journeyAdd(content);
    }

    public void featureFailed(String name, String detail) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Feature");
        content.put("action", "Failed");
        content.put("name", name);
        content.put("details", detail);
        journeyAdd(content);
    }

    public void featureFailed(String name) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Feature");
        content.put("action", "Failed");
        content.put("name", name);
        journeyAdd(content);
    }

    public void contentViewed(String contentType, String identifier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Content");
        content.put("action", "Viewed");
        content.put("type", contentType);
        content.put("identifier", identifier);
        journeyAdd(content);
    }

    public void contentViewed(String contentType) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Content");
        content.put("action", "Viewed");
        content.put("type", contentType);
        journeyAdd(content);
    }

    public void contentEdited(String contentType, String identifier, String detail) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Content");
        content.put("action", "Edited");
        content.put("type", contentType);
        content.put("identifier", identifier);
        content.put("details", detail);
        journeyAdd(content);
    }

    public void contentEdited(String contentType, String identifier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Content");
        content.put("action", "Edited");
        content.put("type", contentType);
        content.put("identifier", identifier);
        journeyAdd(content);
    }

    public void contentEdited(String contentType) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Content");
        content.put("action", "Edited");
        content.put("type", contentType);
        journeyAdd(content);
    }

    public void contentCreated(String contentType, String identifier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Content");
        content.put("action", "Created");
        content.put("type", contentType);
        content.put("identifier", identifier);
        journeyAdd(content);
    }

    public void contentCreated(String contentType) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Content");
        content.put("action", "Created");
        content.put("type", contentType);
        journeyAdd(content);
    }

    public void contentDeleted(String contentType, String identifier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Content");
        content.put("action", "Deleted");
        content.put("type", contentType);
        content.put("identifier", identifier);
        journeyAdd(content);
    }

    public void contentDeleted(String contentType) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Content");
        content.put("action", "Deleted");
        content.put("type", contentType);
        journeyAdd(content);
    }

    public void contentRequested(String contentType, String identifier) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Content");
        content.put("action", "Requested");
        content.put("type", contentType);
        content.put("identifier", identifier);
        journeyAdd(content);
    }

    public void contentRequested(String contentType) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Content");
        content.put("action", "Requested");
        content.put("type", contentType);
        journeyAdd(content);
    }

    public void contentSearched(String contentType) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", "Content");
        content.put("action", "Searched");
        content.put("type", contentType);
        journeyAdd(content);
    }

// Custom Milestones

    public void milestone(String category, String operation, String name, String detail) throws JSONException {
        JSONObject content = new JSONObject();
        content.put("category", category);
        content.put("action", operation);
        content.put("name", name);
        content.put("details", detail);
        journeyAdd(content);
    }


// API Communication:

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
                    restore();
                    throw (new CompletionException(err));
                });
    }

    public CompletableFuture<Json> heartbeat() throws JSONException, Throwable {
        JSONObject params = (new JSONObject())
                .put("id", id())
                .put("journey", journey())
                .put("token", apiKey)
                .put("timestamp", timestamp())
                .put("ignore-certificate-errors", allowSelfSigned);
        if (apiKey.equals("")) throw new Throwable("API Key not set.");
        if (Xenon.platform.length() > 0) params.put("platform", Xenon.platform);
        if (Xenon.tags.length() > 0) params.put("tags", Xenon.tags);

        reset();

        return heartbeatApi.instance(apiUrl).fetch(params)
                .exceptionally(err -> {
                    restore();
                    throw (new CompletionException(err));
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

// Internals:

    public String id() {
        return Xenon._id;

    }

    public String id(String _id) {
        Xenon._id = _id;
        return Xenon._id;
    }

    public void newId() {
        Xenon._id = UUID.randomUUID().toString();
    }

    private void outcomeAdd(JSONObject content) throws JSONException {
        if (Xenon.platform.length() > 0) content.put("platform", Xenon.platform);
        if (Xenon.tags.length() > 0) content.put("tags", Xenon.tags);
        journeyAdd(content);
    }

    private void journeyAdd(JSONObject content) throws JSONException {
        JSONArray journey = journey();
        content.put("timestamp", timestamp());

        if (journey.length() > 0) {
            JSONObject last = journey.getJSONObject(journey.length() - 1);

            if (isDuplicate(last, content)) {
                int count = last.has("count") ? last.getInt("count") : 1;
                last.put("count", count + 1);
            } else {
                journey.put(content);
            }
        } else {
            journey = (new JSONArray()).put(content);
        }
        storeJourney(journey);
    }

    protected boolean isDuplicate(JSONObject last, JSONObject content) {

        final Set<String> lastKeys = last.keySet();
        final Set<String> contentKeys = content.keySet();

        if (!lastKeys.containsAll(contentKeys)) return false;
        if (!contentKeys.contains("category") || !lastKeys.contains("category")) return false;
        if (!content.get("category").equals(last.get("category"))) return false;
        if (!contentKeys.contains("action") || !lastKeys.contains("action")) return false;
        if (!content.get("action").equals(last.get("action"))) return false;
        return (duplicateFeature(last, content, lastKeys, contentKeys) ||
                duplicateContent(last, content, lastKeys, contentKeys) ||
                duplicateMilestone(last, content, lastKeys, contentKeys));
    }

    protected boolean duplicateFeature(JSONObject last, JSONObject content, Set<String> lastKeys, Set<String> contentKeys) {
        if (!content.get("category").equals("Feature") || !last.get("category").equals("Feature")) return false;
        return content.get("name").equals(last.get("name"));
    }

    protected boolean duplicateContent(JSONObject last, JSONObject content, Set<String> lastKeys, Set<String> contentKeys) {
        if (!content.get("category").equals("Content") || !last.get("category").equals("Content")) return false;
        if (!contentKeys.contains("type") || !lastKeys.contains("type")) return true;
        if (!content.get("type").equals(last.get("type"))) return false;
        if (!contentKeys.contains("identifier") || !lastKeys.contains("identifier")) return true;
        if (!content.get("identifier").equals(last.get("identifier"))) return false;
        if (!contentKeys.contains("details") || !lastKeys.contains("details")) return true;
        return content.get("details").equals(last.get("details"));
    }

    protected boolean duplicateMilestone(JSONObject last, JSONObject content, Set<String> lastKeys, Set<String> contentKeys) {
        if (content.get("category").equals("Feature") || last.get("category").equals("Feature")) return false;
        if (content.get("category").equals("Content") || last.get("category").equals("Content")) return false;
        if (!content.get("name").equals(last.get("name"))) return false;
        return content.get("details").equals(last.get("details"));
    }


    public JSONArray journey() {
        return Xenon._journey;
    }

    private void storeJourney(JSONArray journey) {
        Xenon._journey = journey;
    }

    public void reset() {
        this.restoreJourney = this.journey();
        Xenon._journey = new JSONArray();
    }

    public void restore() {
        JSONArray currentJourney = this.journey();
        JSONArray restoreJourney = this.restoreJourney;
        if (currentJourney.length() > 0) {
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

    public boolean selfSignedAllowed() {
        return allowSelfSigned;
    }

    private double timestamp() {
        return System.currentTimeMillis() / 1000.0;
    }

    private Api<Fetchable> realApi(Api<Fetchable> api) {
        return api;
    }
}
