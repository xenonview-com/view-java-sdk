/**
 * Created by lwoydziak on 06/20/22.
 * <p>
 * JourneyApiTest.js
 * <p>
 * Testing: Journey API interactions with Xenon View.
 */
package xenon.view.sdk.api;


import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.runner.RunWith;
import xenon.view.sdk.api.fetch.Fetchable;

import java.util.concurrent.atomic.AtomicReference;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(Ginkgo4jRunner.class)
@Ginkgo4jConfiguration(threads = 1)
public class JourneyApiTest {
    {
        Describe("JourneyApi", () -> {
            final String apiUrl = "https://app.xenonview.com";
            final JSONObject dataWithoutJourney = new JSONObject() {{
                put("id", "somevalue");
                put("token", "<testToken>");
                put("timestamp", 0.1);
            }};
            final JSONArray journeyData = new JSONArray() {{
                put("step");
            }};
            final JSONObject dataWithJourney = new JSONObject(dataWithoutJourney.toString()) {{
                put("journey", journeyData);
            }};
            final Fetchable jsonFetcher = mock(Fetchable.class);
            AtomicReference<ApiBase> unit = new AtomicReference<>(null);
            AtomicReference<JSONObject> data = new AtomicReference<>(dataWithoutJourney);
            BeforeEach(() -> {
                unit.set(new JourneyApi(apiUrl, jsonFetcher));
            });
            JustBeforeEach(() -> {
                unit.get().fetch(data.get());
            });
            It("can be default constructed", () -> {
              assertNotNull(new JourneyApi(apiUrl));
            });
            It("then requests Journey Api", () -> {
                verify(jsonFetcher).fetch(argThat((JSONObject params) -> {
                    assertEquals("POST", params.get("method").toString());
                    assertEquals(apiUrl + "/journey", params.get("url").toString());
                    JSONObject body = params.getJSONObject("body");
                    assertEquals("ApiJourney", body.get("name"));
                    JSONObject requestHeaders = params.getJSONObject("requestHeaders");
                    assertEquals("application/json", requestHeaders.get("content-type"));
                    assertEquals("Bearer <testToken>", requestHeaders.get("authorization"));
                    return true;
                }));
            });
            Describe("when parameters do not include Journey", () -> {
                BeforeEach(() -> {
                    data.set(dataWithoutJourney);
                });
                It("then creates parameters without journey", () -> {
                    JSONObject local = unit.get().params(data.get());
                    assertEquals(new JSONObject(){{
                        put("uuid", "somevalue");
                        put("timestamp", 0.1);
                    }}.toString(), local.toString());
                });
            });
            Describe("when parameters include Journey", () -> {
              BeforeEach(() -> {
                data.set(dataWithJourney);
              });
              It("then creates parameters with journey", () -> {
                  JSONObject local = unit.get().params(data.get());
                  assertEquals(new JSONObject(){{
                      put("uuid", "somevalue");
                      put("timestamp", 0.1);
                      put("journey", journeyData);
                  }}.toString(), local.toString());
              });
            });
        });
    }
}