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
import xenon.view.sdk.api.Fetchable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

class ApiType implements Api<Fetchable> {
    public Fetchable instance(String _apiUrl) {
        return null;
    }
}

@RunWith(Ginkgo4jRunner.class)
@Ginkgo4jConfiguration(threads = 1)
public class ViewTest {
    {
        Describe("View SDK", () -> {
            final String apiKey = "<token>";
            final String apiUrl = "https://localhost";
            final Fetchable JourneyFetcher = mock(Fetchable.class);
            final Api<Fetchable> JourneyApi = mock(ApiType.class);
            final CompletableFuture<JSONObject> journeyFuture = new CompletableFuture<>();
            final Fetchable DeanonFetcher = mock(Fetchable.class);
            final Api<Fetchable> DeanonApi = mock(ApiType.class);
            final CompletableFuture<JSONObject> deanonFuture = new CompletableFuture<>();
            AtomicReference<View> unit = new AtomicReference<>(null);
            AtomicReference<String> journeyStr = new AtomicReference<>("");
            BeforeEach(() -> {
                unit.set(new View(apiKey, apiUrl, JourneyApi, DeanonApi));
                unit.get().reset();
            });
            AfterEach(() -> {
                unit.set(null);
            });
            JustBeforeEach(() -> {
                journeyStr.set(unit.get().journey().toString());
            });
            It("then has default id", () -> {
                assertNotEquals("", unit.get().id());
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
                    assertEquals(testId, (new View(apiKey, apiUrl)).id());
                });
                AfterEach(() -> {
                    unit.get().id(UUID.randomUUID().toString());
                });
            });
            Describe("when initialized and previous journey", () -> {
                BeforeEach(() -> {
                    unit.get().pageView("test");
                    unit.set(new View(apiKey, apiUrl));
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
            Describe("when adding duplicate event", () -> {
                final JSONObject event = (new JSONObject())
                        .put("funnel", "funnel")
                        .put("action", "test");
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
                        journeyFuture.complete(new JSONObject());
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
                        journeyFuture.complete(new JSONObject());
                        when(JourneyFetcher.fetch(ArgumentMatchers.any())).thenReturn(journeyFuture);
                        unit.get().init(customKey, "");
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
                        journeyFuture.complete(new JSONObject());
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
                    AtomicReference<JSONObject> commitResult = new AtomicReference<>(null);
                    BeforeEach(() -> {
                        journeyFuture.completeExceptionally(new Throwable(error.toString()));
                        when(JourneyFetcher.fetch(ArgumentMatchers.any())).thenReturn(journeyFuture);
                        when(JourneyApi.instance(apiUrl)).thenReturn(JourneyFetcher);
                        commitResult.set(unit.get().commit().get());
                    });
                    It("then has correct error text", () -> {
                        assertEquals(error.toString(), commitResult.get().toString());
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
                        deanonFuture.complete(new JSONObject());
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
                        deanonFuture.complete(new JSONObject());
                        when(DeanonFetcher.fetch(ArgumentMatchers.any())).thenReturn(deanonFuture);
                        unit.get().init(customKey, "");
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
                        deanonFuture.complete(new JSONObject());
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
                    AtomicReference<JSONObject> commitResult = new AtomicReference<>(null);
                    BeforeEach(() -> {
                        when(DeanonApi.instance(apiUrl)).thenReturn(DeanonFetcher);
                        deanonFuture.completeExceptionally(new Throwable(error.toString()));
                        when(DeanonFetcher.fetch(ArgumentMatchers.any())).thenReturn(deanonFuture);
                        commitResult.set(unit.get().deanonymize(person).get());
                    });
                    It("then has correct error text", () -> {
                        assertEquals(error.toString(), commitResult.get().toString());
                    });
                });
            });
        });
    }
}
