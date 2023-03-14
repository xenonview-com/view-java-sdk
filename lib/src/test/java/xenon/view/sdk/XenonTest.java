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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import xenon.view.sdk.api.Api;
import xenon.view.sdk.api.fetch.Fetchable;
import xenon.view.sdk.api.fetch.Json;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.hamcrest.CoreMatchers.*;
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
        Describe("View SDK Uninitialized", () -> {
            It("then throws upon commit", () -> {
                assertThrows(Throwable.class, () -> {
                    new Xenon().commit();
                });
            });

            It("then throws upon deanonymize", () -> {
                assertThrows(Throwable.class, () -> {
                    new Xenon().deanonymize(new JSONObject());
                });
            });

        });
        Describe("View SDK Concurrency", () -> {
            final String apiKey = "<token>";
            final String apiUrl = "https://app.xenonview.com";
            final Fetchable JourneyFetcher = mock(Fetchable.class);
            final Api<Fetchable> JourneyApi = mock(ApiType.class);
            final CompletableFuture<Json> journeyFuture = new CompletableFuture<>();

            It("then gets API key from other thread", () -> {
                when(JourneyApi.instance(apiUrl)).thenReturn(JourneyFetcher);
                journeyFuture.complete(new Json(""));
                when(JourneyFetcher.fetch(ArgumentMatchers.any())).thenReturn(journeyFuture);
                class SetsKey extends Thread {
                    public void run() {
                        new Xenon(apiKey, JourneyApi);
                    }
                }
                class ExpectsSetKey extends Thread {
                    public void run() {
                        try {
                            new Xenon(JourneyApi).commit();
                        } catch (Throwable err) {
                            System.out.println(err.getMessage());
                            System.out.println(err.toString());
                            fail();
                        }
                    }
                }
                ExpectsSetKey myThread2 = new ExpectsSetKey();
                SetsKey myThread = new SetsKey();
                myThread.start();
                myThread2.start();
                myThread.join();
                myThread2.join();
            });
        });
        Describe("View SDK", () -> {
            final String apiKey = "<token>";
            final String apiUrl = "https://localhost";
            final Fetchable JourneyFetcher = mock(Fetchable.class);
            final Api<Fetchable> JourneyApi = mock(ApiType.class);
            final CompletableFuture<Json> journeyFuture = new CompletableFuture<>();
            final Fetchable DeanonFetcher = mock(Fetchable.class);
            final Api<Fetchable> DeanonApi = mock(ApiType.class);
            final CompletableFuture<Json> deanonFuture = new CompletableFuture<>();
            final Fetchable HeartbeatFetcher = mock(Fetchable.class);
            final Api<Fetchable> HeartbeatApi = mock(ApiType.class);
            final CompletableFuture<Json> heartbeatFuture = new CompletableFuture<>();
            final String softwareVersion = "5.1.5";
            final String deviceModel = "Pixel 4 XL";
            final String operatingSystemName = "Android";
            final String operatingSystemVersion = "12.0";
            final JSONArray tags = new JSONArray() {{
                put("aTag");
            }};
            AtomicReference<Xenon> unit = new AtomicReference<>(null);
            AtomicReference<String> journeyStr = new AtomicReference<>("");
            BeforeEach(() -> {
                unit.set(new Xenon(apiKey, apiUrl, JourneyApi, DeanonApi, HeartbeatApi));
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
            It("swallows exception when adding non-existent journey step", () -> {
                JSONArray in = new JSONArray();
                JSONArray out = new JSONArray();
                unit.get().addJourneyIndexTo(out, in, 1);
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
            Describe("when regenerating an ID", () -> {
                It("then has set id", () -> {
                    final String previousId = unit.get().id();
                    unit.get().newId();
                    assertNotEquals(previousId, unit.get().id());
                    assertNotNull(unit.get().id());
                });
            });
            Describe("when initialized and previous journey", () -> {
                BeforeEach(() -> {
                    unit.get().leadCaptured("Phone Number");
                    unit.set(new Xenon(apiKey, apiUrl));
                });
                It("then has previous journey", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"success\",\"superOutcome\":\"Lead Capture\",\"outcome\":\"Phone Number\",\"timestamp\":"));
                });
            });
            Describe("when initialized with a previous id", () -> {
                final String testId = "<some random uuid>";
                BeforeEach(() -> {
                    unit.get().id(testId);
                    unit.set(new Xenon(apiKey, apiUrl));
                });
                It("then has previous id", () -> {
                    assertThat(unit.get().id(), containsString(testId));
                });
            });
            Describe("when adding outcome after platform reset", () -> {
                BeforeEach(() -> {
                    unit.get().platform(softwareVersion, deviceModel, operatingSystemName, operatingSystemVersion);
                    unit.get().removePlatform();
                    unit.get().applicationInstalled();
                });
                It("then journey doesn't contain platform", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"success\",\"superOutcome\":\"Application Installation\",\"outcome\":\"Installed\",\"timestamp\":"
                    ));
                });
            });
            Describe("when adding outcome after platform set", () -> {
                BeforeEach(() -> {
                    unit.get().platform(softwareVersion, deviceModel, operatingSystemName, operatingSystemVersion);
                    unit.get().applicationInstalled();
                });
                It("then adds an outcome to journey", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"success\",\"superOutcome\":\"Application Installation\",\"outcome\":\"Installed\","
                    ));
                    assertThat(journeyStr.get(), containsString(
                            "\"timestamp\":"
                    ));
                });
                It("then has platform details on the outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "\"platform\":{\"operatingSystemVersion\":\"12.0\",\"deviceModel\":\"Pixel 4 XL\",\"operatingSystemName\":\"Android\",\"softwareVersion\":\"5.1.5\"}"
                    ));
                });
                AfterEach(() -> {
                    unit.get().removePlatform();
                });
            });
            Describe("when adding outcome after tags reset", () -> {
                BeforeEach(() -> {
                    unit.get().variant(tags);
                    unit.get().resetVariants();
                    unit.get().applicationInstalled();
                });
                It("then adds an outcome to journey", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"success\",\"superOutcome\":\"Application Installation\",\"outcome\":\"Installed\",\"timestamp\":"
                    ));
                });
            });
            Describe("when adding outcome after tags", () -> {
                Describe("when JSONArray", () -> {
                    BeforeEach(() -> {
                        unit.get().variant(tags);
                        unit.get().applicationInstalled();
                    });
                    It("then adds an outcome to journey", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"success\",\"superOutcome\":\"Application Installation\",\"outcome\":\"Installed\","
                        ));
                        assertThat(journeyStr.get(), containsString(
                                "\"timestamp\":"
                        ));
                    });
                    It("then has tags details on the outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "\"tags\":[\"aTag\"]"
                        ));
                    });
                    AfterEach(() -> {
                        unit.get().resetVariants();
                    });
                });
                Describe("when array literal", () -> {
                    BeforeEach(() -> {
                        final String tag = "aTag";
                        final String[] tagsArray = {tag};

                        unit.get().variant(tagsArray);
                        unit.get().applicationInstalled();
                    });
                    It("then has tags details on the outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "\"tags\":[\"aTag\"]"
                        ));
                    });
                    AfterEach(() -> {
                        unit.get().resetVariants();
                    });
                });
            });
// Stock Business Outcomes tests
            Describe("when leadCaptured", () -> {
                BeforeEach(() -> {
                    unit.get().leadCaptured("Phone Number");
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"success\",\"superOutcome\":\"Lead Capture\",\"outcome\":\"Phone Number\","
                    ));
                });
            });
            Describe("when leadCaptureDeclined", () -> {
                BeforeEach(() -> {
                    unit.get().leadCaptureDeclined("Phone Number");
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"fail\",\"superOutcome\":\"Lead Capture\",\"outcome\":\"Phone Number\","
                    ));
                });
            });
            Describe("when accountSignup", () -> {
                BeforeEach(() -> {
                    unit.get().accountSignup("Facebook");
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"success\",\"superOutcome\":\"Account Signup\",\"outcome\":\"Facebook\","
                    ));
                });
            });
            Describe("when accountSignupDeclined", () -> {
                BeforeEach(() -> {
                    unit.get().accountSignupDeclined("Facebook");
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"fail\",\"superOutcome\":\"Account Signup\",\"outcome\":\"Facebook\","
                    ));
                });
            });
            Describe("when applicationInstalled", () -> {
                BeforeEach(() -> {
                    unit.get().applicationInstalled();
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"success\",\"superOutcome\":\"Application Installation\",\"outcome\":\"Installed\","
                    ));
                });
            });
            Describe("when applicationNotInstalled", () -> {
                BeforeEach(() -> {
                    unit.get().applicationNotInstalled();
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"fail\",\"superOutcome\":\"Application Installation\",\"outcome\":\"Not Installed\","
                    ));
                });
            });
            Describe("when initialSubscription", () -> {
                Describe("when no method", () -> {
                    BeforeEach(() -> {
                        unit.get().initialSubscription("Silver Monthly");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"success\",\"superOutcome\":\"Initial Subscription\",\"outcome\":\"Subscribe - Silver Monthly\","
                        ));
                    });
                });
                Describe("when method", () -> {
                    BeforeEach(() -> {
                        unit.get().initialSubscription("Silver Monthly", "Stripe");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"success\",\"method\":\"Stripe\",\"superOutcome\":\"Initial Subscription\",\"outcome\":\"Subscribe - Silver Monthly\","
                        ));
                    });
                });
                Describe("when method and term/price", () -> {
                    BeforeEach(() -> {
                        unit.get().initialSubscription("Silver", "Monthly", "$1.99", "Stripe");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"success\",\"method\":\"Stripe\",\"price\":\"$1.99\",\"superOutcome\":\"Initial Subscription\",\"term\":\"Monthly\",\"outcome\":\"Subscribe - Silver\",\"timestamp\":"
                        ));
                    });
                });
            });
            Describe("when subscriptionDeclined", () -> {
                Describe("when no method", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionDeclined("Silver Monthly");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"fail\",\"superOutcome\":\"Initial Subscription\",\"outcome\":\"Decline - Silver Monthly\","
                        ));
                    });
                });
                Describe("when method", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionDeclined("Silver Monthly", "Stripe");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"fail\",\"method\":\"Stripe\",\"superOutcome\":\"Initial Subscription\",\"outcome\":\"Decline - Silver Monthly\","
                        ));
                    });
                });
                Describe("when method and term/price", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionDeclined("Silver", "Monthly", "$1.99", "Stripe");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"fail\",\"method\":\"Stripe\",\"price\":\"$1.99\",\"superOutcome\":\"Initial Subscription\",\"term\":\"Monthly\",\"outcome\":\"Decline - Silver\",\"timestamp\":"
                        ));
                    });
                });
            });
            Describe("when subscriptionRenewed", () -> {
                Describe("when no method", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionRenewed("Silver Monthly");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"success\",\"superOutcome\":\"Subscription Renewal\",\"outcome\":\"Renew - Silver Monthly\","
                        ));
                    });
                });
                Describe("when method", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionRenewed("Silver Monthly", "Stripe");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"success\",\"method\":\"Stripe\",\"superOutcome\":\"Subscription Renewal\",\"outcome\":\"Renew - Silver Monthly\","
                        ));
                    });
                });
                Describe("when method and term/price", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionRenewed("Silver", "Monthly", "$1.99", "Stripe");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"success\",\"method\":\"Stripe\",\"price\":\"$1.99\",\"superOutcome\":\"Subscription Renewal\",\"term\":\"Monthly\",\"outcome\":\"Renew - Silver\",\"timestamp\":"
                        ));
                    });
                });
            });
            Describe("when subscriptionCanceled", () -> {
                Describe("when no method", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionCanceled("Silver Monthly");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"fail\",\"superOutcome\":\"Subscription Renewal\",\"outcome\":\"Cancel - Silver Monthly\","
                        ));
                    });
                });
                Describe("when method", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionCanceled("Silver Monthly", "Stripe");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"fail\",\"method\":\"Stripe\",\"superOutcome\":\"Subscription Renewal\",\"outcome\":\"Cancel - Silver Monthly\","
                        ));
                    });
                });

                Describe("when method and term/price", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionCanceled("Silver", "Monthly", "$1.99", "Stripe");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"fail\",\"method\":\"Stripe\",\"price\":\"$1.99\",\"superOutcome\":\"Subscription Renewal\",\"term\":\"Monthly\",\"outcome\":\"Cancel - Silver\",\"timestamp\":"
                        ));
                    });
                });
            });
            Describe("when subscriptionUpsold", () -> {
                Describe("when no method", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionUpsold("Silver Monthly");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"success\",\"superOutcome\":\"Subscription Upsold\",\"outcome\":\"Upsold - Silver Monthly\","
                        ));
                    });
                });
                Describe("when method", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionUpsold("Silver Monthly", "Stripe");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"success\",\"method\":\"Stripe\",\"superOutcome\":\"Subscription Upsold\",\"outcome\":\"Upsold - Silver Monthly\","
                        ));
                    });
                });

                Describe("when method and term/price", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionUpsold("Silver", "Monthly", "$1.99", "Stripe");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"success\",\"method\":\"Stripe\",\"price\":\"$1.99\",\"superOutcome\":\"Subscription Upsold\",\"term\":\"Monthly\",\"outcome\":\"Upsold - Silver\",\"timestamp\":"
                        ));
                    });
                });
            });
            Describe("when subscriptionUpsellDeclined", () -> {
                Describe("when no method", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionUpsellDeclined("Silver Monthly");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"fail\",\"superOutcome\":\"Subscription Upsold\",\"outcome\":\"Declined - Silver Monthly\","
                        ));
                    });
                });
                Describe("when method", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionUpsellDeclined("Silver Monthly", "Stripe");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"fail\",\"method\":\"Stripe\",\"superOutcome\":\"Subscription Upsold\",\"outcome\":\"Declined - Silver Monthly\","
                        ));
                    });
                });
                Describe("when method and term/price", () -> {
                    BeforeEach(() -> {
                        unit.get().subscriptionUpsellDeclined("Silver", "Monthly", "$1.99", "Stripe");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"fail\",\"method\":\"Stripe\",\"price\":\"$1.99\",\"superOutcome\":\"Subscription Upsold\",\"term\":\"Monthly\",\"outcome\":\"Declined - Silver\",\"timestamp\":"
                        ));
                    });
                });
            });
            Describe("when referral", () -> {
                Describe("when no detail", () -> {
                    BeforeEach(() -> {
                        unit.get().referral("Share");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"success\",\"superOutcome\":\"Referral\",\"outcome\":\"Referred - Share\","
                        ));
                    });
                });
                Describe("when detail", () -> {
                    BeforeEach(() -> {
                        unit.get().referral("Share", "Review");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"success\",\"superOutcome\":\"Referral\",\"details\":\"Review\",\"outcome\":\"Referred - Share\","
                        ));
                    });
                });
            });
            Describe("when referralDeclined", () -> {
                Describe("when no detail", () -> {
                    BeforeEach(() -> {
                        unit.get().referralDeclined("Share");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"fail\",\"superOutcome\":\"Referral\",\"outcome\":\"Declined - Share\","
                        ));
                    });
                });
                Describe("when detail", () -> {
                    BeforeEach(() -> {
                        unit.get().referralDeclined("Share", "Review");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"fail\",\"superOutcome\":\"Referral\",\"details\":\"Review\",\"outcome\":\"Declined - Share\","
                        ));
                    });
                });
            });
// Ecommerce Related Outcomes tests
            Describe("when productAddedToCart", () -> {
                BeforeEach(() -> {
                    unit.get().productAddedToCart("Dell XPS");
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"success\",\"superOutcome\":\"Add Product To Cart\",\"outcome\":\"Add - Dell XPS\","
                    ));
                });
            });
            Describe("when productNotAddedToCart", () -> {
                BeforeEach(() -> {
                    unit.get().productNotAddedToCart("Dell XPS");
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"fail\",\"superOutcome\":\"Add Product To Cart\",\"outcome\":\"Ignore - Dell XPS\","

                    ));
                });
            });
            Describe("when upsold", () -> {
                BeforeEach(() -> {
                    unit.get().upsold("Dell XPS");
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"success\",\"superOutcome\":\"Upsold Product\",\"outcome\":\"Upsold - Dell XPS\","

                    ));
                });
            });
            Describe("when upsellDismissed", () -> {
                BeforeEach(() -> {
                    unit.get().upsellDismissed("Dell XPS");
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"fail\",\"superOutcome\":\"Upsold Product\",\"outcome\":\"Dismissed - Dell XPS\","

                    ));
                });
            });
            Describe("when checkedOut", () -> {
                BeforeEach(() -> {
                    unit.get().checkedOut();
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"success\",\"superOutcome\":\"Customer Checkout\",\"outcome\":\"Checked Out\","

                    ));
                });
            });
            Describe("when checkoutCanceled", () -> {
                BeforeEach(() -> {
                    unit.get().checkoutCanceled();
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"fail\",\"superOutcome\":\"Customer Checkout\",\"outcome\":\"Canceled\","

                    ));
                });
            });
            Describe("when productRemoved", () -> {
                BeforeEach(() -> {
                    unit.get().productRemoved("Dell XPS");
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"fail\",\"superOutcome\":\"Customer Checkout\",\"outcome\":\"Product Removed - Dell XPS\","

                    ));
                });
            });
            Describe("when purchased", () -> {
                BeforeEach(() -> {
                    unit.get().purchased("Stripe");
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"success\",\"superOutcome\":\"Customer Purchase\",\"outcome\":\"Purchase - Stripe\","

                    ));
                });
            });
            Describe("when purchaseCanceled", () -> {
                Describe("with method", () -> {
                    BeforeEach(() -> {
                        unit.get().purchaseCanceled("Stripe");
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"fail\",\"superOutcome\":\"Customer Purchase\",\"outcome\":\"Canceled - Stripe\","

                        ));
                    });
                });
                Describe("without method", () -> {
                    BeforeEach(() -> {
                        unit.get().purchaseCanceled();
                    });
                    It("then creates journey with outcome", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"result\":\"fail\",\"superOutcome\":\"Customer Purchase\",\"outcome\":\"Canceled\","

                        ));
                    });
                });
            });
            Describe("when promiseFulfilled", () -> {
                BeforeEach(() -> {
                    unit.get().promiseFulfilled();
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"success\",\"superOutcome\":\"Promise Fulfillment\",\"outcome\":\"Fulfilled\","

                    ));
                });
            });
            Describe("when promiseUnfulfilled", () -> {
                BeforeEach(() -> {
                    unit.get().promiseUnfulfilled();
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"fail\",\"superOutcome\":\"Promise Fulfillment\",\"outcome\":\"Unfulfilled\","

                    ));
                });
            });
            Describe("when productKept", () -> {
                BeforeEach(() -> {
                    unit.get().productKept("Dell XPS");
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"success\",\"superOutcome\":\"Product Disposition\",\"outcome\":\"Kept - Dell XPS\","
                    ));
                });
            });
            Describe("when productReturned", () -> {
                BeforeEach(() -> {
                    unit.get().productReturned("Dell XPS");
                });
                It("then creates journey with outcome", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"result\":\"fail\",\"superOutcome\":\"Product Disposition\",\"outcome\":\"Returned - Dell XPS\","

                    ));
                });
            });
// Stock Milestones tests
            final String name = "Scale Recipe";
            final String detail = "x2";
            Describe("when featureAttempted", () -> {
                Describe("when has detail", () -> {
                    BeforeEach(() -> {
                        unit.get().featureAttempted(name, detail);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"name\":\"Scale Recipe\",\"action\":\"Attempted\",\"details\":\"x2\",\"category\":\"Feature\",\"timestamp\":"));
                    });
                });
                Describe("when has no detail", () -> {
                    BeforeEach(() -> {
                        unit.get().featureAttempted(name);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"name\":\"Scale Recipe\",\"action\":\"Attempted\",\"category\":\"Feature\",\"timestamp\":"));
                    });
                });
            });
            Describe("when featureCompleted", () -> {
                Describe("when has detail", () -> {
                    BeforeEach(() -> {
                        unit.get().featureCompleted(name, detail);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"name\":\"Scale Recipe\",\"action\":\"Completed\",\"details\":\"x2\",\"category\":\"Feature\",\"timestamp\":"));
                    });
                });
                Describe("when has no detail", () -> {
                    BeforeEach(() -> {
                        unit.get().featureCompleted(name);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"name\":\"Scale Recipe\",\"action\":\"Completed\",\"category\":\"Feature\",\"timestamp\":"));
                    });
                });
            });
            Describe("when featureFailed", () -> {
                Describe("when has detail", () -> {
                    BeforeEach(() -> {
                        unit.get().featureFailed(name, detail);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"name\":\"Scale Recipe\",\"action\":\"Failed\",\"details\":\"x2\",\"category\":\"Feature\",\"timestamp\":"));
                    });
                });
                Describe("when has no detail", () -> {
                    BeforeEach(() -> {
                        unit.get().featureFailed(name);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"name\":\"Scale Recipe\",\"action\":\"Failed\",\"category\":\"Feature\",\"timestamp\":"));
                    });
                });
            });
            final String contentType = "Blog Post";
            final String identifier = "how-to-install-xenon-view";
            Describe("when contentViewed", () -> {
                Describe("when has identifier", () -> {
                    BeforeEach(() -> {
                        unit.get().contentViewed(contentType, identifier);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"identifier\":\"how-to-install-xenon-view\",\"action\":\"Viewed\",\"category\":\"Content\",\"type\":\"Blog Post\",\"timestamp\":"));
                    });
                });
                Describe("when has no identifier", () -> {
                    BeforeEach(() -> {
                        unit.get().contentViewed(contentType);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"Viewed\",\"category\":\"Content\",\"type\":\"Blog Post\",\"timestamp\":"));
                    });
                });
            });
            Describe("when contentEdited", () -> {
                Describe("when has detail", () -> {
                    BeforeEach(() -> {
                        unit.get().contentEdited(contentType, identifier, detail);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"identifier\":\"how-to-install-xenon-view\",\"action\":\"Edited\",\"details\":\"x2\",\"category\":\"Content\",\"type\":\"Blog Post\",\"timestamp\":"));
                    });
                });
                Describe("when has identifier", () -> {
                    BeforeEach(() -> {
                        unit.get().contentEdited(contentType, identifier);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"identifier\":\"how-to-install-xenon-view\",\"action\":\"Edited\",\"category\":\"Content\",\"type\":\"Blog Post\",\"timestamp\":"));
                    });
                });
                Describe("when has no identifier", () -> {
                    BeforeEach(() -> {
                        unit.get().contentEdited(contentType);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"Edited\",\"category\":\"Content\",\"type\":\"Blog Post\",\"timestamp\":"));
                    });
                });
            });
            Describe("when contentCreated", () -> {
                Describe("when has identifier", () -> {
                    BeforeEach(() -> {
                        unit.get().contentCreated(contentType, identifier);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"identifier\":\"how-to-install-xenon-view\",\"action\":\"Created\",\"category\":\"Content\",\"type\":\"Blog Post\",\"timestamp\":"));
                    });
                });
                Describe("when has no identifier", () -> {
                    BeforeEach(() -> {
                        unit.get().contentCreated(contentType);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"Created\",\"category\":\"Content\",\"type\":\"Blog Post\",\"timestamp\":"));
                    });
                });
            });
            Describe("when contentDeleted", () -> {
                Describe("when has identifier", () -> {
                    BeforeEach(() -> {
                        unit.get().contentDeleted(contentType, identifier);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"identifier\":\"how-to-install-xenon-view\",\"action\":\"Deleted\",\"category\":\"Content\",\"type\":\"Blog Post\",\"timestamp\":"));
                    });
                });
                Describe("when has no identifier", () -> {
                    BeforeEach(() -> {
                        unit.get().contentDeleted(contentType);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"Deleted\",\"category\":\"Content\",\"type\":\"Blog Post\",\"timestamp\":"));
                    });
                });
            });
            Describe("when contentRequested", () -> {
                Describe("when has identifier", () -> {
                    BeforeEach(() -> {
                        unit.get().contentRequested(contentType, identifier);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"identifier\":\"how-to-install-xenon-view\",\"action\":\"Requested\",\"category\":\"Content\",\"type\":\"Blog Post\",\"timestamp\":"));
                    });
                });
                Describe("when has no identifier", () -> {
                    BeforeEach(() -> {
                        unit.get().contentRequested(contentType);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"Requested\",\"category\":\"Content\",\"type\":\"Blog Post\",\"timestamp\":"));
                    });
                });
            });
            Describe("when contentSearched", () -> {
                Describe("when has no identifier", () -> {
                    BeforeEach(() -> {
                        unit.get().contentSearched(contentType);
                    });
                    It("then has a journey with a page view", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"action\":\"Searched\",\"category\":\"Content\",\"type\":\"Blog Post\",\"timestamp\":"));
                    });
                });
            });
// Custom Milestones tests
            Describe("when custom milestone", () -> {
                final String category = "Function";
                final String operation = "Called";
                BeforeEach(() -> {
                    unit.get().milestone(category, operation, name, detail);
                });
                It("then has a journey with a funnel stage", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"name\":\"Scale Recipe\",\"action\":\"Called\",\"details\":\"x2\",\"category\":\"Function\",\"timestamp\":"
                    ));
                });
            });
// Internals
            Describe("when adding duplicate feature", () -> {
                final String feature = "duplicate";
                BeforeEach(() -> {
                    unit.get().featureAttempted(name);
                    unit.get().featureAttempted(name);
                });
                It("then has a journey with a single event", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"name\":\"Scale Recipe\",\"count\":2,\"action\":\"Attempted\",\"category\":\"Feature\",\"timestamp\":"));
                });
                It("has one journey", () -> {
                    assertThat(unit.get().journey().length(), equalTo(1));
                });
                Describe("when adding third duplicate", () -> {
                    BeforeEach(() -> {
                        unit.get().featureAttempted(name);
                    });
                    It("then has a journey with a single event", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"name\":\"Scale Recipe\",\"count\":3,\"action\":\"Attempted\",\"category\":\"Feature\",\"timestamp\":"));
                    });
                });
                Describe("when adding new milestone", () -> {
                    BeforeEach(() -> {
                        unit.get().milestone("category", "operation", "name", "detail");
                    });
                    It("then has a journey with a single event", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"name\":\"Scale Recipe\",\"count\":2,\"action\":\"Attempted\",\"category\":\"Feature\",\"timestamp\":"));
                    });

                    It("has two journeys", () -> {
                        assertThat(unit.get().journey().length(), equalTo(2));
                    });
                });
            });
            Describe("when adding duplicate content", () -> {
                final String contentName = "duplicate";
                BeforeEach(() -> {
                    unit.get().contentSearched(contentName);
                    unit.get().contentSearched(contentName);
                });
                It("then has a journey with a single event", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"count\":2,\"action\":\"Searched\",\"category\":\"Content\",\"type\":\"duplicate\",\"timestamp\":"));
                });
                It("has one journey", () -> {
                    assertThat(unit.get().journey().length(), equalTo(1));
                });
            });
            Describe("when adding duplicate content with identifier", () -> {
                final String contentName = "duplicate";
                final String contentId = "duplicateId";
                BeforeEach(() -> {
                    unit.get().contentEdited(contentName, contentId);
                    unit.get().contentEdited(contentName, contentId);
                });
                It("then has a journey with a single event", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"identifier\":\"duplicateId\",\"count\":2,\"action\":\"Edited\",\"category\":\"Content\",\"type\":\"duplicate\",\"timestamp\":"));
                });
                It("has one journey", () -> {
                    assertThat(unit.get().journey().length(), equalTo(1));
                });
            });
            Describe("when adding duplicate content with detail", () -> {
                final String contentName = "duplicate";
                final String contentId = "duplicateId";
                final String contentDetail = "duplicateDetail";
                BeforeEach(() -> {
                    unit.get().contentEdited(contentName, contentId, contentDetail);
                    unit.get().contentEdited(contentName, contentId, contentDetail);
                });
                It("then has a journey with a single event", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "{\"identifier\":\"duplicateId\",\"count\":2,\"action\":\"Edited\",\"details\":\"duplicateDetail\",\"category\":\"Content\",\"type\":\"duplicate\",\"timestamp\":"));
                });
                It("has one journey", () -> {
                    assertThat(unit.get().journey().length(), equalTo(1));
                });
            });
            Describe("when adding duplicate milestone", () -> {
                final String milestoneCategory = "Function";
                final String milestoneOperation = "Called";
                final String milestoneName = "Query Database";
                final String milestoneDetail = "User Lookup";
                BeforeEach(() -> {
                    unit.get().milestone(milestoneCategory, milestoneOperation, milestoneName, milestoneDetail);
                    unit.get().milestone(milestoneCategory, milestoneOperation, milestoneName, milestoneDetail);
                });
                It("then has a journey with a single event", () -> {
                    assertThat(journeyStr.get(), containsString(
                            "\"name\":\"Query Database\",\"count\":2,\"action\":\"Called\",\"details\":\"User Lookup\",\"category\":\"Function\",\"timestamp\":"));
                });
                It("has one journey", () -> {
                    assertThat(unit.get().journey().length(), equalTo(1));
                });
            });
            Describe("when adding almost duplicate feature", () -> {
                final String feature = "almostDup";
                BeforeEach(() -> {
                    unit.get().featureAttempted(feature);
                    unit.get().featureCompleted(feature);
                });
                It("has one journey", () -> {
                    assertThat(unit.get().journey().length(), equalTo(2));
                });
            });
            Describe("when adding almost duplicate content", () -> {
                final String contentName = "duplicate";
                BeforeEach(() -> {
                    unit.get().contentViewed(contentName);
                    unit.get().contentSearched(contentName);
                });
                It("has one journey", () -> {
                    assertThat(unit.get().journey().length(), equalTo(2));
                });
            });
            Describe("when adding almost duplicate content with identifier", () -> {
                final String contentName = "duplicate";
                final String contentId = "duplicateId";
                BeforeEach(() -> {
                    unit.get().contentEdited(contentName, contentId);
                    unit.get().contentEdited(contentName, contentId + "1");
                });
                It("has one journey", () -> {
                    assertThat(unit.get().journey().length(), equalTo(2));
                });
            });
            Describe("when adding almost duplicate content with detail", () -> {
                final String contentName = "duplicate";
                final String contentId = "duplicateId";
                final String contentDetail = "duplicateDetail";
                BeforeEach(() -> {
                    unit.get().contentEdited(contentName, contentId, contentDetail);
                    unit.get().contentEdited(contentName, contentId, contentDetail + "2");
                });
                It("has one journey", () -> {
                    assertThat(unit.get().journey().length(), equalTo(2));
                });
            });
            Describe("when adding almost duplicate milestone", () -> {
                final String milestoneCategory = "Function";
                final String milestoneOperation = "Called";
                final String milestoneName = "Query Database";
                final String milestoneDetail = "User Lookup";
                BeforeEach(() -> {
                    unit.get().milestone(milestoneCategory, milestoneOperation, milestoneName, milestoneDetail);
                    unit.get().milestone(milestoneCategory, milestoneOperation, milestoneName, milestoneDetail + "2");
                });
                It("has one journey", () -> {
                    assertThat(unit.get().journey().length(), equalTo(2));
                });
            });
            Describe("when resetting", () -> {
                final String feature = "resetting";
                BeforeEach(() -> {
                    unit.get().featureAttempted(feature);
                    unit.get().reset();
                });
                Describe("when restoring", () -> {
                    BeforeEach(() -> {
                        unit.get().restore();
                    });
                    It("then has a journey with added event", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"name\":\"resetting\",\"action\":\"Attempted\",\"category\":\"Feature\",\"timestamp\":"
                        ));
                    });
                });
                Describe("when restoring after another event was added", () -> {
                    final String anotherFeature = "resetting2";
                    BeforeEach(() -> {
                        unit.get().featureAttempted(anotherFeature);
                        unit.get().restore();
                    });

                    It("then adds new event at end of previous journey", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "{\"name\":\"resetting\",\"action\":\"Attempted\",\"category\":\"Feature\",\"timestamp\":"
                        ));
                        assertThat(journeyStr.get(), containsString(
                                "{\"name\":\"resetting2\",\"action\":\"Attempted\",\"category\":\"Feature\",\"timestamp\":"
                        ));
                    });
                });
            });
            Describe("when adding an event after reset", () -> {
                final String feature = "postreset";
                BeforeEach(() -> {
                    unit.get().reset();
                    unit.get().featureAttempted(feature);
                });
                It("has one journey", () -> {
                    assertThat(unit.get().journey().length(), equalTo(1));
                });
            });
            Describe("isDuplicate", () -> {
                Describe("when same keys no category", () -> {
                    It("then is not dup", () -> {
                        assertThat(unit.get().isDuplicate(
                                new JSONObject() {{
                                    put("test", "1");
                                }},
                                new JSONObject() {{
                                    put("test", "2");
                                }}
                        ), equalTo(false));
                    });
                });
                Describe("when categories are not equal", () -> {
                    It("then is not dup", () -> {
                        assertThat(unit.get().isDuplicate(
                                new JSONObject() {{
                                    put("category", "1");
                                }},
                                new JSONObject() {{
                                    put("category", "2");
                                }}
                        ), equalTo(false));
                    });
                });
                Describe("when category without action", () -> {
                    It("then is not dup", () -> {
                        assertThat(unit.get().isDuplicate(
                                new JSONObject() {{
                                    put("category", "1");
                                }},
                                new JSONObject() {{
                                    put("category", "1");
                                }}
                        ), equalTo(false));
                    });
                });
            });
            Describe("duplicateContent", () -> {
                Describe("when content with no type", () -> {
                    It("then is not dup", () -> {
                        final Set<String> noTypeKey = new HashSet<String>();
                        final JSONObject noType = new JSONObject() {{
                            put("category", "Content");
                        }};
                        assertThat(unit.get().duplicateContent(noType, noType, noTypeKey, noTypeKey), equalTo(true));
                    });
                });
                Describe("when content with mismatch type", () -> {
                    It("then is not dup", () -> {
                        final Set<String> typeKey = new HashSet<String>() {{
                            add("type");
                        }};
                        final JSONObject type1 = new JSONObject() {{
                            put("category", "Content");
                            put("type", "1");
                        }};
                        final JSONObject type2 = new JSONObject() {{
                            put("category", "Content");
                            put("type", "2");
                        }};
                        assertThat(unit.get().duplicateContent(type1, type2, typeKey, typeKey), equalTo(false));
                    });
                });
            });
            Describe("duplicateMilestone", () -> {
                Describe("when feature", () -> {
                    It("then is not dup", () -> {
                        final JSONObject onlyFeature = new JSONObject() {{
                            put("category", "Feature");
                        }};
                        final Set<String> noTypeKey = new HashSet<String>();
                        assertThat(unit.get().duplicateMilestone(onlyFeature, onlyFeature, noTypeKey, noTypeKey), equalTo(false));
                    });
                });
                Describe("when content", () -> {
                    It("then is not dup", () -> {
                        final JSONObject onlyContent = new JSONObject() {{
                            put("category", "Content");
                        }};
                        final Set<String> noTypeKey = new HashSet<String>();
                        assertThat(unit.get().duplicateMilestone(onlyContent, onlyContent, noTypeKey, noTypeKey), equalTo(false));
                    });
                });
                Describe("when name mismatch", () -> {
                    It("then is not dup", () -> {
                        final JSONObject categoryAndName = new JSONObject() {{
                            put("category", "1");
                            put("name", "1");
                        }};
                        final JSONObject categoryAndName2 = new JSONObject() {{
                            put("category", "1");
                            put("name", "2");
                        }};
                        final Set<String> noTypeKey = new HashSet<String>();
                        assertThat(unit.get().duplicateMilestone(categoryAndName, categoryAndName2, noTypeKey, noTypeKey), equalTo(false));
                    });
                });
            });
// API Communication tests
            Describe("when committing a journey", () -> {
                final String feature = "committing";
                BeforeEach(() -> {
                    unit.get().featureAttempted(feature);
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
                            try {
                                assertThat(params.get("id").toString(), instanceOf(String.class));
                                assertThat(params.get("journey").toString(),
                                        containsString("[{\"name\":\"committing\",\"action\":\"Attempted\",\"category\":\"Feature\",\"timestamp\":"));
                                assertEquals(apiKey, params.get("token"));
                                assertThat(params.get("timestamp"), instanceOf(Double.class));
                            } catch (JSONException err) {
                                return false;
                            }
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
                            try {
                                assertEquals(customKey, params.get("token"));
                            } catch (JSONException err) {
                                return false;
                            }
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
                        result.exceptionally((err) -> {
                            commitResult.set(err.getMessage());
                            return null;
                        });
                    });
                    It("then has correct error text", () -> {
                        assertEquals("java.lang.Throwable: " + error.toString(), commitResult.get());
                    });
                    It("then restores journey", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "\"name\":\"committing\",\"action\":\"Attempted\",\"category\":\"Feature\",\"timestamp\":"
                        ));
                    });
                });
            });
            Describe("when heartbeating", () -> {
                final String feature = "heartbeating";
                BeforeEach(() -> {
                    unit.get().featureAttempted(feature);
                });
                Describe("when default api key", () -> {
                    BeforeEach(() -> {
                        when(HeartbeatApi.instance(apiUrl)).thenReturn(HeartbeatFetcher);
                        heartbeatFuture.complete(new Json(""));
                        when(HeartbeatFetcher.fetch(ArgumentMatchers.any())).thenReturn(heartbeatFuture);
                        unit.get().heartbeat();
                    });
                    It("then calls the view heartbeat API", () -> {
                        verify(HeartbeatFetcher).fetch(argThat((JSONObject params) -> {
                            try {
                                assertThat(params.get("id").toString(), instanceOf(String.class));
                                assertThat(params.get("journey").toString(),
                                        containsString("[{\"name\":\"heartbeating\",\"action\":\"Attempted\",\"category\":\"Feature\",\"timestamp\":"));
                                assertEquals(apiKey, params.get("token"));
                                assertThat(params.get("timestamp"), instanceOf(Double.class));
                            } catch (JSONException err) {
                                return false;
                            }
                            return true;
                        }));
                    });
                    It("then uses correct api url", () -> {
                        verify(HeartbeatApi).instance(apiUrl);
                    });
                    It("then resets journey", () -> {
                        assertEquals("[]", journeyStr.get());
                    });
                });
                Describe("when variants", () -> {
                    Describe("when JSONArray", () -> {
                        BeforeEach(() -> {
                            when(HeartbeatApi.instance(apiUrl)).thenReturn(HeartbeatFetcher);
                            heartbeatFuture.complete(new Json(""));
                            when(HeartbeatFetcher.fetch(ArgumentMatchers.any())).thenReturn(heartbeatFuture);
                            unit.get().variant(new JSONArray() {{
                                put("variant");
                            }});
                            unit.get().heartbeat();
                        });
                        It("then calls the view heartbeat API", () -> {
                            verify(HeartbeatFetcher).fetch(argThat((JSONObject params) -> {
                                try {
                                    assertThat(params.get("id").toString(), instanceOf(String.class));
                                    assertThat(params.get("journey").toString(),
                                            containsString("[{\"name\":\"heartbeating\",\"action\":\"Attempted\",\"category\":\"Feature\",\"timestamp\":"));
                                    assertEquals(apiKey, params.get("token"));
                                    assertEquals("variant", params.getJSONArray("tags").get(0));
                                    assertThat(params.get("timestamp"), instanceOf(Double.class));
                                } catch (JSONException err) {
                                    return false;
                                }
                                return true;
                            }));
                        });
                    });
                });
                Describe("when platform", () -> {
                    BeforeEach(() -> {
                        when(HeartbeatApi.instance(apiUrl)).thenReturn(HeartbeatFetcher);
                        heartbeatFuture.complete(new Json(""));
                        when(HeartbeatFetcher.fetch(ArgumentMatchers.any())).thenReturn(heartbeatFuture);
                        unit.get().platform(softwareVersion, deviceModel, operatingSystemName, operatingSystemVersion);
                        unit.get().heartbeat();
                    });
                    It("then calls the view heartbeat API", () -> {
                        verify(HeartbeatFetcher).fetch(argThat((JSONObject params) -> {
                            try {
                                assertThat(params.get("id").toString(), instanceOf(String.class));
                                assertThat(params.get("journey").toString(),
                                        containsString("[{\"name\":\"heartbeating\",\"action\":\"Attempted\",\"category\":\"Feature\",\"timestamp\":"));
                                assertEquals(apiKey, params.get("token"));
                                assertThat(params.getJSONObject("platform").toString(), containsString(
                                        "{\"operatingSystemVersion\":\"12.0\",\"deviceModel\":\"Pixel 4 XL\",\"operatingSystemName\":\"Android\",\"softwareVersion\":\"5.1.5\"}"
                                ));
                                assertThat(params.get("timestamp"), instanceOf(Double.class));
                            } catch (JSONException err) {
                                return false;
                            }
                            return true;
                        }));
                    });
                });
                Describe("when API fails", () -> {
                    final JSONObject error = (new JSONObject())
                            .put("Error", "Failed");
                    AtomicReference<String> heartbeatResult = new AtomicReference<>(null);
                    BeforeEach(() -> {
                        heartbeatFuture.completeExceptionally(new Throwable(error.toString()));
                        when(HeartbeatFetcher.fetch(ArgumentMatchers.any())).thenReturn(heartbeatFuture);
                        when(HeartbeatApi.instance(apiUrl)).thenReturn(HeartbeatFetcher);
                        CompletableFuture<Json> result = unit.get().heartbeat();
                        result.exceptionally((err) -> {
                            heartbeatResult.set(err.getMessage());
                            return null;
                        });
                    });
                    It("then has correct error text", () -> {
                        assertEquals("java.lang.Throwable: " + error.toString(), heartbeatResult.get());
                    });
                    It("then restores journey", () -> {
                        assertThat(journeyStr.get(), containsString(
                                "\"name\":\"heartbeating\",\"action\":\"Attempted\",\"category\":\"Feature\",\"timestamp\":"
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
                            try {
                                assertThat(params.get("id").toString(), instanceOf(String.class));
                                assertEquals(params.get("person").toString(), person.toString());
                                assertEquals(apiKey, params.get("token"));
                                assertThat(params.get("timestamp"), instanceOf(Double.class));
                            } catch (JSONException err) {
                                return false;
                            }
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
                            try {
                                assertEquals(customKey, params.get("token"));
                            } catch (JSONException err) {
                                return false;
                            }
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
                        result.exceptionally((err) -> {
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
