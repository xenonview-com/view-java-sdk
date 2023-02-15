/*
  Created by lwoydziak on 06/20/22.
  <p>
  ApiBaseTest.js
  <p>
  Testing: Base class for API interactions with Xenon View.
 */
package xenon.view.sdk.api;


import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.RunWith;

import xenon.view.sdk.api.fetch.Fetchable;
import xenon.view.sdk.api.fetch.Json;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Ginkgo4jRunner.class)
@Ginkgo4jConfiguration(threads = 1)
public class ApiBaseTest {
    {
        Describe("ApiBase", () -> {
            final String apiUrl = "https://app.xenonview.com";
            final Fetchable jsonFetcher = mock(Fetchable.class);
            AtomicReference<ApiBase> unit = new AtomicReference<>(null);
            Describe("when calling fetch with default api", () -> {
                BeforeEach(() -> {
                    Map<String, Object> props = new Hashtable<String, Object>() {{
                        put("apiUrl", apiUrl);
                    }};
                    unit.set(new ApiBase(props, jsonFetcher));
                    unit.get().fetch(new JSONObject());
                });
                It("requests base url", () -> {
                    verify(jsonFetcher).fetch(argThat((JSONObject params) -> {
                        try {
                            assertEquals("POST", params.get("method").toString());
                            assertEquals(apiUrl + "/", params.get("url").toString());
                            JSONObject body = params.getJSONObject("body");
                            assertEquals("ApiBase", body.get("name"));
                            assertEquals("{}", body.getJSONObject("parameters").toString());
                            JSONObject requestHeaders = params.getJSONObject("requestHeaders");
                            assertEquals("application/json", requestHeaders.get("content-type"));
                        } catch (JSONException err) {
                            return false;
                        }
                        return true;
                    }));
                });
            });
            Describe("when calling fetch with default api and ignoring self signed certs", () -> {
                BeforeEach(() -> {
                    Map<String, Object> props = new Hashtable<String, Object>() {{
                        put("apiUrl", apiUrl);
                    }};
                    unit.set(new ApiBase(props, jsonFetcher));
                    unit.get().fetch(new JSONObject() {{
                        put("ignore-certificate-errors", true);
                    }});
                });
                It("requests base url and ignores self signed certs", () -> {
                    verify(jsonFetcher).fetch(argThat((JSONObject params) -> {
                        try {
                            assertTrue(params.getBoolean("ignore-certificate-errors"));
                        } catch (JSONException err) {
                            return false;
                        }
                        return true;
                    }));
                });
            });
            Describe("when calling fetch with api parameters", () -> {
                BeforeEach(() -> {
                    Map<String, Object> params = new Hashtable<String, Object>() {{
                        put("name", "name");
                        put("method", "OPTIONS");
                        put("url", "url");
                        put("apiUrl", apiUrl);
                    }};
                    Map<String, Object> headers = new Hashtable<String, Object>() {{
                        put("header", "header");
                        put("content-type", "application/json");
                    }};
                    params.put("headers", headers);
                    unit.set(new ApiBase(params, jsonFetcher));
                    unit.get().fetch(new JSONObject());
                });
                It("requests custom url", () -> {
                    verify(jsonFetcher).fetch(argThat((JSONObject params) -> {
                        try {
                            assertEquals("OPTIONS", params.get("method").toString());
                            assertEquals(apiUrl + "/url", params.get("url").toString());
                            JSONObject body = params.getJSONObject("body");
                            assertEquals("name", body.get("name"));
                            JSONObject requestHeaders = params.getJSONObject("requestHeaders");
                            assertEquals("application/json", requestHeaders.get("content-type"));
                            assertEquals("header", requestHeaders.get("header"));
                        } catch (JSONException err) {
                            return false;
                        }
                        return true;
                    }));
                });
            });
            Describe("when calling fetch with authentication", () -> {
                Describe("when token", () -> {
                    BeforeEach(() -> {
                        Map<String, Object> params = new Hashtable<String, Object>() {{
                            put("authenticated", true);
                        }};
                        unit.set(new ApiBase(params, jsonFetcher));
                        JSONObject fetchParams = new JSONObject() {{
                            put("token", "<anAccessToken>");
                        }};
                        unit.get().fetch(fetchParams);
                    });

                    It("requests url", () -> {
                        verify(jsonFetcher).fetch(argThat((JSONObject params) -> {
                            try {
                                JSONObject requestHeaders = params.getJSONObject("requestHeaders");
                                assertEquals("application/json", requestHeaders.get("content-type"));
                                assertEquals("Bearer <anAccessToken>", requestHeaders.get("authorization"));
                            } catch (JSONException err) {
                                return false;
                            }
                            return true;
                        }));
                    });
                });
                Describe("when no token", () -> {
                    AtomicReference<Throwable> error = new AtomicReference<>();
                    BeforeEach(() -> {
                        Map<String, Object> params = new Hashtable<String, Object>() {{
                            put("authenticated", true);
                        }};
                        unit.set(new ApiBase(params, jsonFetcher));
                        JSONObject fetchParams = new JSONObject();
                        unit.get().fetch(fetchParams).exceptionally((err) -> {
                            error.set(err);
                            return new Json("");
                        }).get();
                    });
                    It("rejects the promise", () -> {
                        assertEquals("No token and authenticated!", error.get().getMessage());
                    });
                });
                Describe("when blank token", () -> {
                    AtomicReference<Throwable> error = new AtomicReference<>();
                    BeforeEach(() -> {
                        Map<String, Object> params = new Hashtable<String, Object>() {{
                            put("authenticated", true);
                        }};
                        unit.set(new ApiBase(params, jsonFetcher));
                        JSONObject fetchParams = new JSONObject() {{
                            put("token", "");
                        }};
                        unit.get().fetch(fetchParams).exceptionally((err) -> {
                            error.set(err);
                            return new Json("");
                        }).get();
                    });
                    It("rejects the promise", () -> {
                        assertEquals("No token and authenticated!", error.get().getMessage());
                    });
                });
            });
            Describe("when calling fetch with no body and get method", () -> {
                BeforeEach(() -> {
                    Map<String, Object> params = new Hashtable<String, Object>() {{
                        put("method", "GET");
                        put("skipName", true);
                    }};
                    Map<String, String> headers = new HashMap<>();
                    params.put("headers", headers);
                    unit.set(new ApiBase(params, jsonFetcher));
                    JSONObject fetchParams = new JSONObject() {{
                        put("token", "<anAccessToken>");
                    }};
                    unit.get().fetch(fetchParams);
                });

                It("requests url", () -> {
                    verify(jsonFetcher).fetch(argThat((JSONObject params) -> {
                        try {
                            assertEquals("GET", params.getString("method"));
                            JSONObject requestHeaders = params.getJSONObject("requestHeaders");
                            assertFalse(requestHeaders.has("content-type"));
                        } catch (JSONException err) {
                            return false;
                        }
                        return true;
                    }));
                });
            });
            Describe("when calling fetch with custom host", () -> {
                BeforeEach(() -> {
                    Map<String, Object> params = new Hashtable<String, Object>() {{
                        put("apiUrl", "https://example.com");
                    }};
                    unit.set(new ApiBase(params, jsonFetcher));
                    unit.get().fetch(new JSONObject());
                });

                It("requests url", () -> {
                    verify(jsonFetcher).fetch(argThat((JSONObject params) -> {
                        try {
                            assertEquals("https://example.com/", params.getString("url"));
                        } catch (JSONException err) {
                            return false;
                        }
                        return true;
                    }));
                });
            });
            Describe("when calling fetch with no name", () -> {
                final JSONObject parameters = new JSONObject() {{
                    put("hello", "world");
                }};
                BeforeEach(() -> {
                    class TestApi extends ApiBase {
                        TestApi() {
                            super(new Hashtable<String, Object>() {{
                                put("skipName", true);
                            }}, jsonFetcher);
                        }   

                        public JSONObject params(JSONObject data) {
                            return parameters;
                        }
                    }

                    unit.set(new TestApi());
                    unit.get().fetch(new JSONObject() {{
                        put("test", "value");
                    }});
                });

                It("requests base url with no name and overloaded params", () -> {
                    verify(jsonFetcher).fetch(argThat((JSONObject params) -> {
                        try {
                            JSONObject body = params.getJSONObject("body");
                            assertFalse(body.has("name"));
                            JSONObject apiParams = body.getJSONObject("parameters");
                            assertEquals("world", apiParams.getString("hello"));
                            assertFalse(apiParams.has("test"));
                        } catch (JSONException err) {
                            return false;
                        }
                        return true;
                    }));
                });
            });
            Describe("when calling fetch and params throws error", () -> {
                AtomicReference<Throwable> error = new AtomicReference<>();
                BeforeEach(() -> {
                    class TestApi extends ApiBase {
                        TestApi() {
                            super(new Hashtable<>(), jsonFetcher);
                        }

                        public JSONObject params(JSONObject data) throws Throwable {
                            throw new Throwable("This is a test");
                        }
                    }
                    unit.set(new TestApi());
                    unit.get().fetch(new JSONObject()).exceptionally((err) -> {
                        error.set(err);
                        return new Json("");
                    }).get();
                });
                It("rejects the promise", () -> {
                    assertEquals("This is a test", error.get().getMessage());
                });

            });
        });
    }
}
