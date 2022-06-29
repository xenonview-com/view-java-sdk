/**
 * Created by lwoydziak on 06/22/22.
 * <p>
 * JsonFetcherTest.js
 * <p>
 * Testing: Class to fetch JSON APIs.
 */
package xenon.view.sdk.api.fetch;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
import okhttp3.*;
import okio.Buffer;
import org.json.JSONObject;
import org.junit.runner.RunWith;

import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(Ginkgo4jRunner.class)
@Ginkgo4jConfiguration(threads = 1)
public class JsonFetcherTest {
    {
        Describe("JsonFetcher", () -> {
            AtomicReference<JsonFetcher> unit = new AtomicReference<>(null);
            final OkHttpClient client = mock(OkHttpClient.class);
            final Call enqueuer = mock(Call.class);
            final JSONObject data = new JSONObject();
            BeforeEach(() -> {
                data.put("url", "https://example.blah/");
            });
            AfterEach(() -> {
                data.clear();
            });
            It("can be constructed", () -> {
              assertNotNull(new JsonFetcher());
            });
            Describe("when default fetch", () -> {
                AtomicReference<Callback> callback = new AtomicReference<>();
                AtomicReference<Request> request = new AtomicReference<>();
                AtomicReference<CompletableFuture<Json>> completableFuture = new AtomicReference<>();
                BeforeEach(() -> {
                    data.put("method", "GET");
                    unit.set(new JsonFetcher(client));
                    when(client.newCall(any())).thenAnswer(invocation -> {
                        request.set((Request) invocation.getArguments()[0]);
                        return enqueuer;
                    });
                    doAnswer(invocation -> {
                        callback.set((Callback) invocation.getArguments()[0]);
                        return null;
                    }).when(enqueuer).enqueue(notNull());
                    completableFuture.set(unit.get().fetch(data));
                });
                It("requests base url", () -> {
                    verify(client).newCall(argThat((Request _request) -> {
                        Headers headers = _request.headers();
                        assertEquals("application/json", headers.get("accept"));
                        assertEquals(data.getString("url"), _request.url().toString());
                        return true;
                    }));
                });
                Describe("when the request is successful", () -> {
                    final Response response = mock(Response.class);
                    final Call theCall = mock(Call.class);
                    final ResponseBody responseBody = mock(ResponseBody.class);
                    BeforeEach(() -> {
                        when(theCall.request()).thenReturn(request.get());
                        when(response.body()).thenReturn(responseBody);
                        when(response.isSuccessful()).thenReturn(true);
                        when(response.code()).thenReturn(200);
                        when(responseBody.string()).thenReturn("{\"response\":\"success\"}");
                    });
                    JustBeforeEach(() -> {
                        callback.get().onResponse(theCall, response);
                    });
                    It("resolves the promise with the response", () -> {
                        Json jsonResponse = completableFuture.get().get();
                        assertEquals("{\"response\":\"success\"}", jsonResponse.to(JSONObject.class).toString());
                    });
                    Describe("when response body is null", () -> {
                        BeforeEach(() -> {
                            when(response.body()).thenReturn(null);
                        });
                        It("then completes exceptionally", () -> {
                            completableFuture.get().exceptionally((Throwable err) -> {
                                assertEquals("No response body.", err.getMessage());
                                return new Json("");
                            });
                        });
                    });
                });
                Describe("when the request is not successful", () -> {
                    BeforeEach(() -> {
                        Call theCall = mock(Call.class);
                        when(theCall.request()).thenReturn(request.get());
                        callback.get().onFailure(theCall, new IOException("{\"response\":\"failed\"}"));
                    });

                    It("rejects the promise", () -> {
                        completableFuture.get().exceptionally((Throwable err) -> {
                            assertEquals("{\"response\":\"failed\"}", err.getMessage());
                            return new Json(err.getMessage());
                        });
                    });
                });
                Describe("when the request unauthorized", () -> {
                    final Response response = mock(Response.class);
                    final Call theCall = mock(Call.class);
                    final ResponseBody responseBody = mock(ResponseBody.class);
                    BeforeEach(() -> {
                        when(theCall.request()).thenReturn(request.get());
                        when(response.body()).thenReturn(responseBody);
                        when(response.isSuccessful()).thenReturn(true);
                        when(response.code()).thenReturn(401);
                        when(responseBody.string()).thenReturn("{\"error_message\":\"unauthorized\"}");
                    });
                    JustBeforeEach(() -> {
                        callback.get().onResponse(theCall, response);
                    });
                    It("rejects the promise", () -> {
                        completableFuture.get().exceptionally((Throwable err) -> {
                            assertEquals("{\"error_message\":\"unauthorized\"}", err.getMessage());
                            return new Json(err.getMessage());
                        });
                    });
                    Describe("when no body", () -> {
                        BeforeEach(() -> {
                            when(response.body()).thenReturn(null);
                        });
                        It("then completes exceptionally", () -> {
                            completableFuture.get().exceptionally((Throwable err) -> {
                                assertEquals("No response body.", err.getMessage());
                                return new Json("");
                            });
                        });
                    });
                });
                Describe("when the request errors", () -> {
                    BeforeEach(() -> {
                        Call theCall = mock(Call.class);
                        when(theCall.request()).thenReturn(request.get());
                        callback.get().onFailure(theCall, new IOException("No Internet Connection"));
                    });
                    It("rejects the promise", () -> {
                        completableFuture.get().exceptionally((Throwable err) -> {
                            assertEquals("No Internet Connection", err.getMessage());
                            return new Json(err.getMessage());
                        });
                    });
                });
                Describe("when the request generally errors", () -> {
                    final Response response = mock(Response.class);
                    final Call theCall = mock(Call.class);
                    final ResponseBody responseBody = mock(ResponseBody.class);
                    BeforeEach(() -> {
                        when(theCall.request()).thenReturn(request.get());
                        when(response.body()).thenReturn(responseBody);
                        when(response.isSuccessful()).thenReturn(true);
                        when(response.code()).thenReturn(503);
                        when(response.message()).thenReturn("Service Unavailable");
                    });
                    JustBeforeEach(() -> {
                        callback.get().onResponse(theCall, response);
                    });
                    It("rejects the promise", () -> {
                        completableFuture.get().exceptionally((Throwable err) -> {
                            assertEquals("Service Unavailable", err.getMessage());
                            return new Json(err.getMessage());
                        });
                    });
                    Describe("when no HTTP message", () -> {
                      BeforeEach(() -> {
                          when(response.message()).thenAnswer((invocation) -> {
                              throw new Throwable("test");
                          });
                      });
                      It("then completes exceptionally", () -> {
                        completableFuture.get().exceptionally((Throwable err)->{
                            assertEquals("test", err.getMessage());
                            return new Json("");
                        });
                      });
                    });
                });
                Describe("when the request has no data", () -> {
                    final Response response = mock(Response.class);
                    BeforeEach(() -> {
                        Call theCall = mock(Call.class);
                        when(theCall.request()).thenReturn(request.get());
                        ResponseBody responseBody = mock(ResponseBody.class);
                        when(response.body()).thenReturn(responseBody);
                        when(response.isSuccessful()).thenReturn(true);
                        when(response.code()).thenReturn(204);
                        when(response.message()).thenReturn("No Content");
                        when(responseBody.string()).thenReturn("");
                        callback.get().onResponse(theCall, response);
                    });

                    It("resolves the promise with the response", () -> {
                        Json jsonResponse = completableFuture.get().get();
                        assertEquals("{}", jsonResponse.to(JSONObject.class).toString());
                    });
                });
            });
            Describe("when posting fetch", () -> {
                AtomicReference<Callback> callback = new AtomicReference<>();
                AtomicReference<Request> request = new AtomicReference<>();
                AtomicReference<CompletableFuture<Json>> completableFuture = new AtomicReference<>();
                BeforeEach(() -> {
                    data.put("method", "POST");
                    data.put("body", new JSONObject() {{
                        put("test", "body");
                    }});
                    unit.set(new JsonFetcher(client));
                    when(client.newCall(any())).thenAnswer(invocation -> {
                        request.set((Request) invocation.getArguments()[0]);
                        return enqueuer;
                    });
                    doAnswer(invocation -> {
                        callback.set((Callback) invocation.getArguments()[0]);
                        return null;
                    }).when(enqueuer).enqueue(notNull());
                    completableFuture.set(unit.get().fetch(data));
                });
                It("requests base url using a post method", () -> {
                    verify(client).newCall(argThat((Request _request) -> {
                        assertEquals("POST", _request.method());
                        RequestBody body = _request.body();
                        assertEquals("application/json; charset=utf-8", body.contentType().toString());
                        final Buffer buffer = new Buffer();
                        try {
                            body.writeTo(buffer);
                        } catch (IOException err) {
                            return false;
                        }
                        String bodyString = buffer.readUtf8();
                        assertEquals("{\"test\":\"body\"}", bodyString);
                        return true;
                    }));
                });
            });
            Describe("when authorized fetch", () -> {
                AtomicReference<Callback> callback = new AtomicReference<>();
                AtomicReference<Request> request = new AtomicReference<>();
                AtomicReference<CompletableFuture<Json>> completableFuture = new AtomicReference<>();
                BeforeEach(() -> {
                    Map<String, String> headers = new Hashtable<>();
                    headers.put("authorization", "Bearer <token>");
                    data.put("requestHeaders", headers);
                    unit.set(new JsonFetcher(client));
                    when(client.newCall(any())).thenAnswer(invocation -> {
                        request.set((Request) invocation.getArguments()[0]);
                        return enqueuer;
                    });
                    doAnswer(invocation -> {
                        callback.set((Callback) invocation.getArguments()[0]);
                        return null;
                    }).when(enqueuer).enqueue(notNull());
                    completableFuture.set(unit.get().fetch(data));
                });
                It("requests base url with authorization header", () -> {
                    verify(client).newCall(argThat((Request _request) -> {
                        Headers headers = _request.headers();
                        assertEquals("application/json", headers.get("accept"));
                        assertEquals(data.getString("url"), _request.url().toString());
                        assertEquals("Bearer <token>", headers.get("authorization"));
                        return true;
                    }));
                });
            });
            Describe("when self signed allowed fetch", () -> {
                AtomicReference<Callback> callback = new AtomicReference<>();
                AtomicReference<Request> request = new AtomicReference<>();
                AtomicReference<CompletableFuture<Json>> completableFuture = new AtomicReference<>();
                final OkHttpClient.Builder builder = mock(OkHttpClient.Builder.class);
                BeforeEach(() -> {
                    Map<String, String> headers = new Hashtable<>();
                    headers.put("authorization", "Bearer <token>");
                    data.put("requestHeaders", headers);
                    data.put("ignore-certificate-errors", true);
                    unit.set(new JsonFetcher(builder, client));
                    when(builder.sslSocketFactory(any(), any())).thenReturn(builder);
                    when(builder.hostnameVerifier(any())).thenReturn(builder);
                    when(builder.build()).thenReturn(client);
                    when(client.newCall(any())).thenAnswer(invocation -> {
                        request.set((Request) invocation.getArguments()[0]);
                        return enqueuer;
                    });
                    doAnswer(invocation -> {
                        callback.set((Callback) invocation.getArguments()[0]);
                        return null;
                    }).when(enqueuer).enqueue(notNull());
                });
                Describe("when getting with no errors", () -> {
                    BeforeEach(() -> {
                        completableFuture.set(unit.get().fetch(data));
                    });
                    It("then creates/attaches a socket factory", () -> {
                        verify(builder).sslSocketFactory(
                                argThat((SSLSocketFactory factory) -> {
                                    assertNotNull(factory);
                                    return true;
                                }),
                                argThat((X509TrustManager manager) -> {
                                    java.security.cert.X509Certificate[] chain = new java.security.cert.X509Certificate[]{};
                                    try {
                                        manager.checkClientTrusted(chain, "testauthtype");
                                        manager.checkServerTrusted(chain, "testauthtype");
                                    } catch (Throwable err) {
                                        fail(err.getMessage());
                                    }
                                    assertEquals(0, manager.getAcceptedIssuers().length);
                                    return true;
                                })
                        );
                    });
                    It("then creates/attaches a hostname verifier", () -> {
                        verify(builder).hostnameVerifier(
                                argThat((HostnameVerifier verifier) -> {
                                    assertTrue(verifier.verify("testhost", new ExtendedSSLSession() {
                                        @Override
                                        public String[] getLocalSupportedSignatureAlgorithms() {
                                            return new String[0];
                                        }

                                        @Override
                                        public String[] getPeerSupportedSignatureAlgorithms() {
                                            return new String[0];
                                        }

                                        @Override
                                        public byte[] getId() {
                                            return new byte[0];
                                        }

                                        @Override
                                        public SSLSessionContext getSessionContext() {
                                            return null;
                                        }

                                        @Override
                                        public long getCreationTime() {
                                            return 0;
                                        }

                                        @Override
                                        public long getLastAccessedTime() {
                                            return 0;
                                        }

                                        @Override
                                        public void invalidate() {

                                        }

                                        @Override
                                        public boolean isValid() {
                                            return false;
                                        }

                                        @Override
                                        public void putValue(String s, Object o) {

                                        }

                                        @Override
                                        public Object getValue(String s) {
                                            return null;
                                        }

                                        @Override
                                        public void removeValue(String s) {

                                        }

                                        @Override
                                        public String[] getValueNames() {
                                            return new String[0];
                                        }

                                        @Override
                                        public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
                                            return new Certificate[0];
                                        }

                                        @Override
                                        public Certificate[] getLocalCertificates() {
                                            return new Certificate[0];
                                        }

                                        @Override
                                        public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
                                            return new X509Certificate[0];
                                        }

                                        @Override
                                        public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
                                            return null;
                                        }

                                        @Override
                                        public Principal getLocalPrincipal() {
                                            return null;
                                        }

                                        @Override
                                        public String getCipherSuite() {
                                            return null;
                                        }

                                        @Override
                                        public String getProtocol() {
                                            return null;
                                        }

                                        @Override
                                        public String getPeerHost() {
                                            return null;
                                        }

                                        @Override
                                        public int getPeerPort() {
                                            return 0;
                                        }

                                        @Override
                                        public int getPacketBufferSize() {
                                            return 0;
                                        }

                                        @Override
                                        public int getApplicationBufferSize() {
                                            return 0;
                                        }
                                    }));
                                    return true;
                                })
                        );
                    });
                    It("then builds a new client", () -> {
                        verify(builder).build();
                    });
                });
                Describe("when getting with self signed ignored error", () -> {
                    BeforeEach(() -> {
                        when(builder.build()).thenAnswer(invocation -> {
                            throw new KeyManagementException("self signed ignored error");
                        });
                        completableFuture.set(unit.get().fetch(data));
                    });
                    It("then issues and doesn't ignore errors", () -> {
                        verify(enqueuer).enqueue(notNull());
                    });
                });
            });
        });
    }
}
