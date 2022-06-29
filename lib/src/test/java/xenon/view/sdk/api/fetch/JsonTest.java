package xenon.view.sdk.api.fetch;


import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.RunWith;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.junit.Assert.*;


@RunWith(Ginkgo4jRunner.class)
@Ginkgo4jConfiguration(threads = 1)
public class JsonTest {
    {
        Describe("Json", () -> {
            It("can convert to JSONObject", () -> {
                Json json = new Json("{}");
                JSONObject obj = json.to(JSONObject.class);
                assertEquals("{}", obj.toString());
            });
            It("can convert to JSONArray", () -> {
                Json json = new Json("[]");
                JSONArray obj = json.to(JSONArray.class);
                assertEquals("[]", obj.toString());
            });
            It("can't covert to JSONObject", () -> {
                Json json = new Json("[]");
                assertThrows(JSONException.class, ()-> json.to(JSONObject.class));
            });
            It("can't covert to JSONArray", () -> {
                Json json = new Json("{}");
                assertThrows(JSONException.class, ()-> json.to(JSONArray.class));
            });
            It("can return string", () -> {
                Json json = new Json("{}");
                assertEquals("{}", json.toString());
            });
            It("can handle empty string", () -> {
                Json json = new Json("");
                JSONObject obj = json.to(JSONObject.class);
                assertEquals("{}", obj.toString());
                JSONArray obj2 = json.to(JSONArray.class);
                assertEquals("[]", obj2.toString());
            });
            It("can foreign type", () -> {
                Json json = new Json("");
                Object obj = json.to(Object.class);
                assertNull(obj);
            });
        });
    }
}
