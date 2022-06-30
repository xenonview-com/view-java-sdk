# xenon-view-sdk
The Xenon View Java SDK is the Java SDK to interact with [XenonView](https://xenonview.com).

**Table of contents:**

* [What's New](#whats-new)
* [Installation](#installation)
* [How to use](#how-to-use)
* [License](#license)

## <a name="whats-new"></a>
## What's New
* v0.0.1 - Initial release (Event adding follows standard)


## <a name="installation"></a>
## Installation

You can install the Xenon View SDK from [maven central](https://search.maven.org/artifact/io.github.xenonview-com/xenon-view-sdk/0.0.1/jar):

Via maven:
```xml
<dependency>
  <groupId>io.github.xenonview-com</groupId>
  <artifactId>xenon-view-sdk</artifactId>
  <version>0.0.1</version>
</dependency>
```

Via gradle (groovy):
```groovy
implementation 'io.github.xenonview-com:xenon-view-sdk:0.0.1'
```

Via gradle (kolin):
```kotlin
implementation("io.github.xenonview-com:xenon-view-sdk:0.0.1")
```
## <a name="how-to-use"></a>
## How to use

The Xenon View SDK can be used in your application to provide a whole new level of user analysis and insights. You'll need to embed the instrumentation into your application via this SDK. The basic operation is to create a customer journey by adding steps in the journey like page views, funnel steps and other events. The journey concludes with an outcome. All of this can be committed for analysis on your behalf to Xenon View. From there you can see popular journeys that result in both successful an unsuccessful outcomes. Additionally, you can deanonymize journeys. This will allow for a deeper analysis of a particular user. This is an optional step as just tracking which journey results in what outcome is valuable.

### Instantiation
The View SDK is a Java module you'll need to include in your application. After inclusion, you'll need to init the singleton object:

```java
import xenon.view.sdk.View;

It("then creates Xenon View", () -> {
    View view = new View();
    View.init('<API KEY>');
});
```
Of course, you'll have to make the following modifications to the above code:
- Replace `<API KEY>` with your [api key](https://xenonview.com/api-get)

### Add Journeys
After you have initialized View, you can start collecting journeys.

There are a few helper methods you can use:

#### Page view
You can use this method to add page views to the journey.

```java
import xenon.view.sdk.View;

It("then adds a page view to journey", () -> {
    View view = new View()
    String page = "test/page";
    view.pageView(page);
});
```
This adds a page view step to the journey chain.


#### Funnel Stage
You can use this method to track funnel stages in the journey.

```java
import xenon.view.sdk.View;

It("then adds a funnel stage to journey", () => {
    View view = new View();
    String action = "<custom action>";
    String stage = "<stage in funnel>";
    view.funnel(stage, action);
});
```
This adds a funnel stage to the journey chain.

#### Outcome
You can use this method to add an outcome to the journey.

```java
import xenon.view.sdk.View;

It("then adds an outcome to journey",()->{
    View view = new View();
    String outcome="<outcome>";
    String action="<custom action>";
    view.outcome(outcome,action);
});
```
This adds an outcome to the journey chain effectively completing it.



#### Generic events
You can use this method to add generic events to the journey.

```java
import xenon.view.sdk.View;
import org.json.JSONObject;

It("then adds a generic event to journey",()->{
    JSONObject event = new JSONObject(){{
       put("category", "Event");
       put("action", "test");
    }};  
    View view = new View();    
    view.event(event);
});
```
This adds an event step to the journey chain.

### Committing Journeys

Journeys only exist locally until you commit them to the Xenon View system. After you have created and added to a journey, you can commit the journey to Xenon View for analysis as follows:

```java
import xenon.view.sdk.View;

It("then commits journey to Xenon View",()->{
    View view = new View();
    view.commit();
});
```
This commits a journey to Xenon View for analysis.

### Deanonymizing Journeys

Xenon View supports both anonymous and known journeys. By deanonymizing a journey you can compare a user's path to other known paths and gather insights into their progress. This is optional.

```java
import xenon.view.sdk.View;
import org.json.JSONObject;

It("then deanonymizes a committed journey to Xenon View",()->{
    View view=new View();
    JSONObject event = new JSONObject(){{
        put("category","Event");
        put("action","test");
    }};
    view.event(event);
    view.commit();
    // you can deanonymize before or after you have committed journey (in this case after):
    JSONObject person = new JSONObject(){{
        put("name","Java Test");
        put("email","javatest@example.com");
    }};
    view.deanonymize(person);
});
```
This deanonymizes every journey committed to a particular user.


### Journey IDs
Each Journey has an ID akin to a session. After an Outcome occurs the ID remains the same to link all the Journeys. If you have a previous Journey in progress and would like to append to that, you can set the ID.

*Note: For java, the Journey is a session persistent variable. If a previous browser session was created, the Journey ID will be reused.*

After you have initialized the View singleton, you can view or set the Journey (Session) ID:

```java
import xenon.view.sdk.View;

final View view = new View();

It("then has default Journey id",()->{
    assertNotNull(view.id());
    assertNotEqual("",view.id())
});

Describe("when Journey id set",()->{
final String testId="<some random uuid>";
    BeforeEach(()->{
        view.id(testId);
    });
    It("then has set id",()->{
        assertEquals(testId,view.id());
    });
});
```

### Error handling
In the event of an API error when committing, the method returns a [CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html).

Note: The default handling of this situation will restore the journey (appending newly added pageViews, events, etc.) for future committing. If you want to do something special, you can do so like this:

```java
import xenon.view.sdk.View;
import xenon.view.sdk.api.fetch.Json;

It("then commits journey to Xenon View and handles errors", () -> {
    View view = new View();
    view.commit().exceptionally((err)->{
        // handle error
        return Json("{}");
    });
});
```

## <a name="license"></a>
## License

Apache Version 2.0

See [LICENSE](https://github.com/xenonview-com/view-java-sdk/blob/main/LICENSE)
