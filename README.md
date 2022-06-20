# xenon-view-sdk
The Xenon View Java SDK is the Java SDK to interact with [XenonView](https://xenonview.com).

**Table of contents:**

* [What's New](#whats-new)
* [Installation](#installation)
* [How to use](#how-to-use)
* [License](#license)

## <a name="whats-new"></a>
## What's New


## <a name="installation"></a>
## Installation

You can install the Xenon View SDK from [npm](https://www.npmjs.com/package/xenon-view-sdk):

Via npm:
```bash
npm install xenon-view-sdk
```

Via yarn:
```bash
yarn add xenon-view-sdk
```

## <a name="how-to-use"></a>
## How to use

The Xenon View SDK can be used in your application to provide a whole new level of user analysis and insights. You'll need to embed the instrumentation into your application via this SDK. The basic operation is to create a customer journey by adding steps in the journey like page views, funnel steps and other events. The journey concludes with an outcome. All of this can be committed for analysis on your behalf to Xenon View. From there you can see popular journeys that result in both successful an unsuccessful outcomes. Additionally, you can deanonymize journeys. This will allow for a deeper analysis of a particular user. This is an optional step as just tracking which journey results in what outcome is valuable.

### Instantiation
The View SDK is a Java module you'll need to include in your application. After inclusion, you'll need to init the singleton object:

```java
import View from 'xenon_view_sdk';

it('then creates Xenon View', () => {
  View.init('<API KEY>');
});
```
Of course, you'll have to make the following modifications to the above code:
- Replace `<API KEY>` with your [api key](https://xenonview.com/api-get)

### Add Journeys
After you have initialized the View singleton, you can start collecting journeys.

There are a few helper methods you can use:

#### Page view
You can use this method to add page views to the journey.

```java
import View from 'xenon_view_sdk';

let page = "test/page";
it('then adds a page view to journey', () => {
  View.pageView(page);
});
```
This adds a page view step to the journey chain.


#### Funnel Stage
You can use this method to track funnel stages in the journey.

```java
import View from 'xenon_view_sdk';

let stage = "<stage in funnel>";
let action = "<custom action>";
it('then adds a funnel stage to journey', () => {
  View.funnel(stage, action);
});
```
This adds a funnel stage to the journey chain.

#### Outcome
You can use this method to add an outcome to the journey.

```java
import View from 'xenon_view_sdk';

let outcome = "<outcome>";
let action = "<custom action>";
it('then adds an outcome to journey', () => {
  View.outcome(outcome, action);
});
```
This adds an outcome to the journey chain effectively completing it.



#### Generic events
You can use this method to add generic events to the journey.

```java
import View from 'xenon_view_sdk';

let event = {category: 'Event', action: 'test'};
it('then adds a generic event to journey', () => {
  View.event(event);
});
```
This adds an event step to the journey chain.

### Committing Journeys

Journeys only exist locally until you commit them to the Xenon View system. After you have created and added to a journey, you can commit the journey to Xenon View for analysis as follows:
```java
import View from 'xenon_view_sdk';

it('then commits journey to Xenon View', () => {
  await View.commit();
});
```
This commits a journey to Xenon View for analysis.

### Deanonymizing Journeys

Xenon View supports both anonymous and known journeys. By deanonymizing a journey you can compare a user's path to other known paths and gather insights into their progress. This is optional.
```java
import View from 'xenon_view_sdk';

it('then deanonymizes a committed journey to Xenon View', () => {
  let event = {category: 'Event', action: 'test'};
  View.event(event);
  await View.commit();
  // you can deanonymize before or after you have committed journey (in this case after):
  await View.deanonymize({name:'Java Test', email:'jstest@example.com'});
});
```
This deanonymizes every journey committed to a particular user.


### Journey IDs
Each Journey has an ID akin to a session. After an Outcome occurs the ID remains the same to link all the Journeys. If you have a previous Journey in progress and would like to append to that, you can set the ID.

*Note: For java, the Journey is a session persistent variable. If a previous browser session was created, the Journey ID will be reused.*

After you have initialized the View singleton, you can view or set the Journey (Session) ID:
```java
import View from 'xenon_view_sdk';
it('then has default Journey id', () => {
  expect(View.id()).not.toBeNull();
  expect(View.id()).not.toEqual('');
});
describe('when Journey id set', () => {
  let testId = '<some random uuid>';
  beforeEach(() => {
    View.id(testId);
  });
  it('then has set id', () => {
    expect(View.id()).toEqual(tes# xenon-view-sdk
The Xenon View java SDK is the java SDK to interact with [XenonView](https://xenonview.com).

**Table of contents:**

* [What's New](#whats-new)
* [Installation](#installation)
* [How to use](#how-to-use)
* [License](#license)

## <a name="whats-new"></a>
## What's New
* v0.0.16 - Event adding follows standard
* v0.0.15 - plumb timestamp through the API call
* v0.0.14 - Timestamp on commit
* v0.0.13 - Timestamps on every addition
* v0.0.12 - Add typescript definitions
* v0.0.11 - Can get and set a Journey ID
* v0.0.10 - Fully operational Outcome and Funnel methods
* v0.0.7 - Fully operational deanonymization
* v0.0.5 - Introduction of deanonymizing journeys
* v0.0.4 - Allow setting the API URL
* v0.0.3 - Basic Functionality

## <a name="installation"></a>
## Installation

You can install the Xenon View SDK from [npm](https://www.npmjs.com/package/xenon-view-sdk):

Via npm:
```bash
npm install xenon-view-sdk
```

Via yarn:
```bash
yarn add xenon-view-sdk
```

## <a name="how-to-use"></a>
## How to use

The Xenon View SDK can be used in your application to provide a whole new level of user analysis and insights. You'll need to embed the instrumentation into your application via this SDK. The basic operation is to create a customer journey by adding steps in the journey like page views, funnel steps and other events. The journey concludes with an outcome. All of this can be committed for analysis on your behalf to Xenon View. From there you can see popular journeys that result in both successful an unsuccessful outcomes. Additionally, you can deanonymize journeys. This will allow for a deeper analysis of a particular user. This is an optional step as just tracking which journey results in what outcome is valuable.

### Instantiation
The View SDK is a Java module you'll need to include in your application. After inclusion, you'll need to init the singleton object:

```java
import View from 'xenon_view_sdk';

it('then creates Xenon View', () => {
  View.init('<API KEY>');
});
```
Of course, you'll have to make the following modifications to the above code:
- Replace `<API KEY>` with your [api key](https://xenonview.com/api-get)

### Add Journeys
After you have initialized the View singleton, you can start collecting journeys.

There are a few helper methods you can use:

#### Page view
You can use this method to add page views to the journey.

```java
import View from 'xenon_view_sdk';

let page = "test/page";
it('then adds a page view to journey', () => {
  View.pageView(page);
});
```
This adds a page view step to the journey chain.


#### Funnel Stage
You can use this method to track funnel stages in the journey.

```java
import View from 'xenon_view_sdk';

let stage = "<stage in funnel>";
let action = "<custom action>";
it('then adds a funnel stage to journey', () => {
  View.funnel(stage, action);
});
```
This adds a funnel stage to the journey chain.

#### Outcome
You can use this method to add an outcome to the journey.

```java
import View from 'xenon_view_sdk';

let outcome = "<outcome>";
let action = "<custom action>";
it('then adds an outcome to journey', () => {
  View.outcome(outcome, action);
});
```
This adds an outcome to the journey chain effectively completing it.



#### Generic events
You can use this method to add generic events to the journey.

```java
import View from 'xenon_view_sdk';

let event = {category: 'Event', action: 'test'};
it('then adds a generic event to journey', () => {
  View.event(event);
});
```
This adds an event step to the journey chain.

### Committing Journeys

Journeys only exist locally until you commit them to the Xenon View system. After you have created and added to a journey, you can commit the journey to Xenon View for analysis as follows:
```java
import View from 'xenon_view_sdk';

it('then commits journey to Xenon View', () => {
  await View.commit();
});
```
This commits a journey to Xenon View for analysis.

### Deanonymizing Journeys

Xenon View supports both anonymous and known journeys. By deanonymizing a journey you can compare a user's path to other known paths and gather insights into their progress. This is optional.
```java
import View from 'xenon_view_sdk';

it('then deanonymizes a committed journey to Xenon View', () => {
  let event = {category: 'Event', action: 'test'};
  View.event(event);
  await View.commit();
  // you can deanonymize before or after you have committed journey (in this case after):
  await View.deanonymize({name:'Java Test', email:'jstest@example.com'});
});
```
This deanonymizes every journey committed to a particular user.


### Journey IDs
Each Journey has an ID akin to a session. After an Outcome occurs the ID remains the same to link all the Journeys. If you have a previous Journey in progress and would like to append to that, you can set the ID.

*Note: For java, the Journey is a session persistent variable. If a previous browser session was created, the Journey ID will be reused.*

After you have initialized the View singleton, you can view or set the Journey (Session) ID:
```java
import View from 'xenon_view_sdk';
it('then has default Journey id', () => {
  expect(View.id()).not.toBeNull();
  expect(View.id()).not.toEqual('');
});
describe('when Journey id set', () => {
  let testId = '<some random uuid>';
  beforeEach(() => {
    View.id(testId);
  });
  it('then has set id', () => {
    expect(View.id()).toEqual(testId);
  });
});
```


### Error handling
In the event of an API error when committing, the method returns a [promise](https://developer.mozilla.org/en-US/docs/Web/java/Reference/Global_Objects/Promise).

Note: The default handling of this situation will restore the journey (appending newly added pageViews, events, etc.) for future committing. If you want to do something special, you can do so like this:

```java
import View from 'xenon_view_sdk';

it('then commits journey to Xenon View and handles errors', () => {
  View.commit().catch(
    (err) =>{
      // handle error
    }
  );
});
```

## <a name="license"></a>
## License

Apache Version 2.0

See [LICENSE](https://github.com/xenonview-com/view-java-sdk/blob/main/LICENSE)
tId);
  });
});
```


### Error handling
In the event of an API error when committing, the method returns a [promise](https://developer.mozilla.org/en-US/docs/Web/java/Reference/Global_Objects/Promise).

Note: The default handling of this situation will restore the journey (appending newly added pageViews, events, etc.) for future committing. If you want to do something special, you can do so like this:

```java
import View from 'xenon_view_sdk';

it('then commits journey to Xenon View and handles errors', () => {
  View.commit().catch(
    (err) =>{
      // handle error
    }
  );
});
```

## <a name="license"></a>
## License

Apache Version 2.0

See [LICENSE](https://github.com/xenonview-com/view-java-sdk/blob/main/LICENSE)
