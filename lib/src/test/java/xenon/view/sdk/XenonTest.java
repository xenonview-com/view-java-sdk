/**
 * Created by lwoydziak on 06/20/22.
 * <p>
 * ViewTest.js
 * <p>
 * Testing: SDK for interacting with the Xenon View service.
 */
package xenon.view.sdk;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
import org.json.JSONObject;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import xenon.view.sdk.api.Api;
import xenon.view.sdk.api.fetch.Fetchable;
import xenon.view.sdk.api.fetch.Json;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

class ApiType implements Api<Fetchable> {
    public Fetchable instance(String _apiUrl) {
        return null;
    }
}

@RunWith(Ginkgo4jRunner.class)
@Ginkgo4jConfiguration(threads = 1)
public class XenonTest {
    {
        Describe("View SDK", () -> {
            final String apiKey = "<token>";
            final String apiUrl = "https://localhost";
            final Fetchable JourneyFetcher = mock(Fetchable.class);
            final Api<Fetchable> JourneyApi = mock(ApiType.class);
            final CompletableFuture<Json> journeyFuture = new CompletableFuture<>();
            final Fetchable DeanonFetcher = mock(Fetchable.class);
            final Api<Fetchable> DeanonApi = mock(ApiType.class);
            final CompletableFuture<Json> deanonFuture = new CompletableFuture<>();
            AtomicReference<Xenon> unit = new AtomicReference<>(null);
            AtomicReference<String> journeyStr = new AtomicReference<>("");
            BeforeEach(() -> {
                unit.set(new Xenon(apiKey, apiUrl, JourneyApi, DeanonApi));
                unit.get().reset();
            });
            AfterEach(() -> {
                unit.set(null);
            });
            JustBeforeEach(() -> {
                journeyStr.set(unit.get().journey().toString());
            });
            It("can be default constructed", () -> {
                Xenon xenon = new Xenon();
                assertEquals(unit.get().id(), xenon.id());
            });
            It("then has default id", () -> {
                assertNotEquals("", unit.get().id());
            });
            It("can be constructed using self signed cert", () -> {
              Xenon v1 = new Xenon(apiKey, true);
              Xenon v2 = new Xenon(apiKey, apiUrl, true);
              Xenon v3 = new Xenon(apiKey, apiUrl, JourneyApi, true);
              Xenon v4 = new Xenon(apiKey, apiUrl, JourneyApi, DeanonApi, true);
              assertTrue(v1.selfSignedAllowed());
              assertTrue(v2.selfSignedAllowed());
              assertTrue(v3.selfSignedAllowed());
              assertTrue(v4.selfSignedAllowed());
            });
            Describe("when id set", () -> {
                final String testId = "<some random uuid>";
                BeforeEach(() -> {
                    unit.get().id(testId);
                });
                It("then has set id", () -> {
                    assertEquals(testId, unit.get().id());
                });
                It("then persists id", () -> {
                    assertEquals(testId, (new Xenon(apiKey, apiUrl)).id());
                });
                AfterEach(() -> {
                    unit.get().id(UUID.randomUUID().toString());
                });
            });
            Describe("when initialized and previous journey", () -> {
                BeforeEach(() -> {
                    unit.get().pageView("test");
                    unit.set(new Xenon(apiKey, apiUrl));
                });
                It("then has previous journey", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"action\":\"test\",\"category\":\"Page View\",\"timestamp\":"));
                });
            });
            Describe("when adding a page view", () -> {
                BeforeEach(() -> {
                    unit.get().pageView("test");
                });
                It("then has a journey with a page view", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"action\":\"test\",\"category\":\"Page View\",\"timestamp\":"));
                });
            });
            Describe("when adding a funnel stage", () -> {
                final String stage = "<stage in funnel>";
                final String action = "<custom action>";
                BeforeEach(() -> {
                    unit.get().funnel(stage, action);
                });
                It("then has a journey with a funnel stage", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"funnel\":\"<stage in funnel>\",\"action\":\"<custom action>\",\"timestamp\":"
                    ));
                });
            });
            Describe("when adding an outcome", () -> {
                final String outcome = "<outcome>";
                final String action = "<custom action>";
                BeforeEach(() -> {
                    unit.get().outcome(outcome, action);
                });
                It("then adds an outcome to journey", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"action\":\"<custom action>\",\"outcome\":\"<outcome>\",\"timestamp\":"
                    ));
                });
            });
            Describe("when adding an event", () -> {
                final JSONObject event = (new JSONObject())
                        .put("category", "Event")
                        .put("action", "test");
                BeforeEach(() -> {
                    unit.get().event(event);
                });
                It("then has a journey with an event", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"action\":\"test\",\"category\":\"Event\",\"timestamp\":"
                    ));
                });
            });
            Describe("when adding two events", () -> {
                final JSONObject event = (new JSONObject())
                        .put("funnel", "funnel")
                        .put("action", "test");
                final JSONObject event2 = (new JSONObject())
                        .put("category", "category")
                        .put("action", "test");
                Describe("when duplicate funnels", () -> {
                    BeforeEach(() -> {
                        unit.get().event(event);
                        unit.get().event(event);
                    });
                    It("then has a journey with a single event", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"funnel\":\"funnel\",\"action\":\"test\",\"timestamp\":"
                        ));
                        assertEquals(1, unit.get().journey().length());
                    });
                });
                Describe("when duplicate categories", () -> {
                    BeforeEach(() -> {
                        unit.get().event(event2);
                        unit.get().event(event2);
                    });
                    It("then has a journey with a single event", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"test\",\"category\":\"category\",\"timestamp\":"
                        ));
                        assertEquals(1, unit.get().journey().length());
                    });
                });
                Describe("when duplicate categories but separate actions", () -> {
                    BeforeEach(() -> {
                        unit.get().event(event2);
                        final JSONObject event3 = (new JSONObject())
                                .put("category", "category")
                                .put("action", "different");
                        unit.get().event(event3);
                    });
                    It("then has a journey with both events", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"test\",\"category\":\"category\",\"timestamp\":"
                        ));
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"different\",\"category\":\"category\",\"timestamp\":"
                        ));
                        assertEquals(2, unit.get().journey().length());
                    });
                });
                Describe("when different", () -> {
                    BeforeEach(() -> {
                        unit.get().event(event2);
                        final JSONObject event3 = (new JSONObject())
                                .put("outcome", "different")
                                .put("action", "different");
                        unit.get().event(event3);
                    });
                    It("then has a journey with both events", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"test\",\"category\":\"category\",\"timestamp\":"
                        ));
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"different\",\"outcome\":\"different\",\"timestamp\":"
                        ));
                        assertEquals(2, unit.get().journey().length());
                    });
                });
            });
            Describe("when adding generic event", () -> {
                final JSONObject event = (new JSONObject())
                        .put("action", "test");
                BeforeEach(() -> {
                    unit.get().event(event);
                });
                It("then has a journey with a generic event", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"action\":\"test\",\"category\":\"Event\",\"timestamp\":"
                    ));
                });
            });
            Describe("when adding custom event", () -> {
                final JSONObject event = (new JSONObject())
                        .put("custom", "test");
                BeforeEach(() -> {
                    unit.get().event(event);
                });
                It("then has a journey with a generic event", () -> {
                    assertThat(journeyStr.get(), containsString("\"action\":{\"custom\":\"test\"},\"category\":\"Event\",\"timestamp\":"
                    ));
                });
            });
            Describe("when resetting", () -> {
                final JSONObject event = (new JSONObject())
                        .put("category", "Event")
                        .put("action", "test");

                BeforeEach(() -> {
                    unit.get().event(event);
                    unit.get().reset();
                });
                Describe("when restoring", () -> {
                    BeforeEach(() -> {
                        unit.get().restore();
                    });
                    It("then has a journey with added event", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"test\",\"category\":\"Event\",\"timestamp\":"
                        ));
                    });
                });
                Describe("when restoring after another event was added", () -> {
                    final JSONObject anotherEvent = (new JSONObject())
                            .put("category", "Event")
                            .put("action", "another");
                    BeforeEach(() -> {
                        unit.get().event(anotherEvent);
                        unit.get().restore();
                    });

                    It("then adds new event at end of previous journey", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"test\",\"category\":\"Event\",\"timestamp\":"
                        ));
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"another\",\"category\":\"Event\",\"timestamp\":"
                        ));
                    });
                });
            });
            Describe("when committing a journey", () -> {
                final JSONObject event = (new JSONObject())
                        .put("category", "Event")
                        .put("action", "test");
                BeforeEach(() -> {
                    unit.get().event(event);
                });
                Describe("when default api key", () -> {
                    BeforeEach(() -> {
                        when(JourneyApi.instance(apiUrl)).thenReturn(JourneyFetcher);
                        journeyFuture.complete(new Json(""));
                        when(JourneyFetcher.fetch(ArgumentMatchers.any())).thenReturn(journeyFuture);
                        unit.get().commit();
                    });
                    It("then calls the view journey API", () -> {
                        verify(JourneyFetcher).fetch(argThat((JSONObject params) -> {
                            assertThat(params.get("id").toString(), instanceOf(String.class));
                            assertThat(params.get("journey").toString(),
                                    containsString("[{\"action\":\"test\",\"category\":\"Event\",\"timestamp\":"));
                            assertEquals(apiKey, params.get("token"));
                            assertThat(params.get("timestamp"), instanceOf(Double.class));
                            return true;
                        }));
                    });
                    It("then uses correct api url", () -> {
                        verify(JourneyApi).instance(apiUrl);
                    });
                    It("then resets journey", () -> {
                        assertEquals("[]", journeyStr.get());
                    });
                });
                Describe("when custom api key", () -> {
                    final String customKey = "<custom>";
                    BeforeEach(() -> {
                        when(JourneyApi.instance(apiUrl)).thenReturn(JourneyFetcher);
                        journeyFuture.complete(new Json(""));
                        when(JourneyFetcher.fetch(ArgumentMatchers.any())).thenReturn(journeyFuture);
                        unit.get().init(customKey);
                        unit.get().commit();
                    });
                    It("then calls the view journey API", () -> {
                        verify(JourneyFetcher).fetch(argThat((JSONObject params) -> {
                            assertEquals(customKey, params.get("token"));
                            return true;
                        }));
                    });
                });
                Describe("when custom api url", () -> {
                    final String customUrl = "<custom url>";
                    BeforeEach(() -> {
                        when(JourneyApi.instance(customUrl)).thenReturn(JourneyFetcher);
                        journeyFuture.complete(new Json(""));
                        when(JourneyFetcher.fetch(ArgumentMatchers.any())).thenReturn(journeyFuture);
                        unit.get().init("", customUrl);
                        unit.get().commit();
                    });
                    It("then uses correct api url", () -> {
                        verify(JourneyApi).instance(customUrl);
                    });
                });
                Describe("when API fails", () -> {
                    final JSONObject error = (new JSONObject())
                            .put("Error", "Failed");
                    AtomicReference<String> commitResult = new AtomicReference<>(null);
                    BeforeEach(() -> {
                        journeyFuture.completeExceptionally(new Throwable(error.toString()));
                        when(JourneyFetcher.fetch(ArgumentMatchers.any())).thenReturn(journeyFuture);
                        when(JourneyApi.instance(apiUrl)).thenReturn(JourneyFetcher);
                        CompletableFuture<Json> result = unit.get().commit();
                        result.exceptionally((err)->{
                            commitResult.set(err.getMessage());
                            return null;
                        });
                    });
                    It("then has correct error text", () -> {
                        assertEquals("java.lang.Throwable: " + error.toString(), commitResult.get());
                    });
                    It("then restores journey", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"test\",\"category\":\"Event\",\"timestamp\":"
                        ));
                    });
                });
            });
            Describe("when deanonymizing", () -> {
                final JSONObject person = (new JSONObject())
                        .put("name", "Test User")
                        .put("email", "test@example.com");
                Describe("when default", () -> {
                    BeforeEach(() -> {
                        when(DeanonApi.instance(apiUrl)).thenReturn(DeanonFetcher);
                        deanonFuture.complete(new Json(""));
                        when(DeanonFetcher.fetch(ArgumentMatchers.any())).thenReturn(deanonFuture);
                        unit.get().deanonymize(person);
                    });

                    It("then calls the view deanon API", () -> {
                        verify(DeanonFetcher).fetch(argThat((JSONObject params) -> {
                            assertThat(params.get("id").toString(), instanceOf(String.class));
                            assertEquals(params.get("person").toString(), person.toString());
                            assertEquals(apiKey, params.get("token"));
                            assertThat(params.get("timestamp"), instanceOf(Double.class));
                            return true;
                        }));
                    });
                });
                Describe("when custom api key", () -> {
                    final String customKey = "<custom>";
                    BeforeEach(() -> {
                        when(DeanonApi.instance(apiUrl)).thenReturn(DeanonFetcher);
                        deanonFuture.complete(new Json(""));
                        when(DeanonFetcher.fetch(ArgumentMatchers.any())).thenReturn(deanonFuture);
                        unit.get().init(customKey);
                        unit.get().deanonymize(person);
                    });
                    It("then calls the view deanon API", () -> {
                        verify(DeanonFetcher).fetch(argThat((JSONObject params) -> {
                            assertEquals(customKey, params.get("token"));
                            return true;
                        }));
                    });
                });
                Describe("when custom api url", () -> {
                    final String customUrl = "<custom url>";
                    BeforeEach(() -> {
                        when(DeanonApi.instance(customUrl)).thenReturn(DeanonFetcher);
                        deanonFuture.complete(new Json(""));
                        when(DeanonFetcher.fetch(ArgumentMatchers.any())).thenReturn(deanonFuture);
                        unit.get().init("", customUrl);
                        unit.get().deanonymize(person);
                    });
                    It("then uses correct api url", () -> {
                        verify(DeanonApi).instance(customUrl);
                    });
                });
                Describe("when API fails", () -> {
                    final JSONObject error = (new JSONObject())
                            .put("Error", "Failed");
                    AtomicReference<String> commitResult = new AtomicReference<>(null);
                    BeforeEach(() -> {
                        when(DeanonApi.instance(apiUrl)).thenReturn(DeanonFetcher);
                        deanonFuture.completeExceptionally(new Throwable(error.toString()));
                        when(DeanonFetcher.fetch(ArgumentMatchers.any())).thenReturn(deanonFuture);
                        CompletableFuture<Json> result = unit.get().deanonymize(person);
                        result.exceptionally((err)->{
                            commitResult.set(err.getMessage());
                            return null;
                        });
                    });
                    It("then has correct error text", () -> {
                        assertEquals(error.toString(), commitResult.get().toString());
                    });
                });
            });
        });
    }
}
