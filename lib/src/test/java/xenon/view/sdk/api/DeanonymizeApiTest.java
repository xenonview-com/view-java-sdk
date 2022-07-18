/**
 * Created by lwoydziak on 06/20/22.
 * <p>
 * DeanonymizeApiTest.js
 * <p>
 * Testing: Deanonymize API interactions with Xenon View.
 */
package xenon.view.sdk.api;


import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;

import org.json.JSONException;
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
public class DeanonymizeApiTest {
    {
        Describe("DeanonymizeApi", () -> {
            final String apiUrl = "https://app.xenonview.com";
            final JSONObject dataWithoutPerson = new JSONObject() {{
                put("id", "somevalue");
                put("token", "<testToken>");
                put("timestamp", 0.1);
            }};
            final JSONObject personData = new JSONObject() {{
                put("name", "Test Name");
                put("email", "test@example.com");
            }};
            final JSONObject dataWithPerson = new JSONObject(dataWithoutPerson.toString()) {{
                put("person", personData);
            }};
            final Fetchable jsonFetcher = mock(Fetchable.class);
            AtomicReference<DeanonymizeApi> unit = new AtomicReference<>(null);
            AtomicReference<JSONObject> data = new AtomicReference<>(dataWithPerson);
            BeforeEach(() -> {
                unit.set(new DeanonymizeApi(apiUrl, jsonFetcher));
            });
            JustBeforeEach(() -> {
                unit.get().fetch(data.get());
            });
            It("can be default constructed", () -> {
                assertNotNull(new DeanonymizeApi(apiUrl));
            });
            It("then requests deanonymize", () -> {
                verify(jsonFetcher).fetch(argThat((JSONObject params) -> {
                    try {
                        assertEquals("POST", params.get("method").toString());
                        assertEquals(apiUrl + "/deanonymize", params.get("url").toString());
                        JSONObject body = params.getJSONObject("body");
                        assertEquals("ApiDeanonymize", body.get("name"));
                        JSONObject requestHeaders = params.getJSONObject("requestHeaders");
                        assertEquals("application/json", requestHeaders.get("content-type"));
                        assertEquals("Bearer <testToken>", requestHeaders.get("authorization"));
                    } catch (JSONException err) {
                        return false;
                    }
                    return true;
                }));
            });

            It("then creates parameters with person", () -> {
                JSONObject local = unit.get().params(data.get());
                assertEquals(new JSONObject() {{
                    put("uuid", "somevalue");
                    put("timestamp", 0.1);
                    put("person", personData);
                }}.toString(), local.toString());
            });
        });
    }
}