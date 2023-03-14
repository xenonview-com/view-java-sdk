# xenon-view-sdk
The Xenon View Java SDK is the Java SDK to interact with [XenonView](https://xenonview.com).

**Table of contents:** <a id="contents"></a>

* [What"s New](#whats-new)
* [Introduction](#intro)
* [Steps To Get Started](#getting-started)
    * [Identify Business Outcomes](#step-1)
    * [Identify Customer Journey Milestones](#step-2)
    * [Enumerate Technical Stack](#step-3)
    * [Installation](#step-4)
    * [Instrument Business Outcomes](#step-5)
    * [Instrument Customer Journey Milestones](#step-6)
    * [Determine Commit Points](#step-7)
    * [(Optional) Group Customer Journeys](#step-8)
    * [Analysis](#step-9)
    * [Perform Experiments](#step-10)
* [Detailed Usage](#detailed-usage)
    * [Installation](#installation)
    * [Initialization](#instantiation)
    * [Service/Subscription/SaaS Business Outcomes](#saas)
    * [Ecommerce Business Outcomes](#ecom)
    * [Customer Journey Milestones](#milestones)
        * [Features Usage](#feature-usage)
        * [Content Interaction](#content-interaction)
    * [Commit Points](#commiting)
    * [Heartbeats](#heartbeat)
    * [Platforming](#platforming)
    * [Experiments](#experiments)
    * [Customer Journey Grouping](#deanonymizing-journeys)
    * [Other Considerations](#other)
        * [(Optional) Error Handling](#errors)
        * [(Optional) Custom Customer Journey Milestones](#custom)
        * [(Optional) Journey Identification](#cuuid)
* [License](#license)

<br/>

## What"s New <a id="whats-new"></a>
* v0.1.6 - Add initial subscriptions options for term/price.
* v0.1.5 - Fix typo
* v0.1.4 - Rename tag to variant
* v0.1.3 - Correct name for untag
* v0.1.2 - Allow tags to take String []
* v0.1.1 - Android Fixups
* v0.1.0 - SDK redesign

<br/>


## Introduction <a id="intro"></a>
Everyone should have access to world-class customer telemetry.

You should be able to identify the most pressing problems affecting your business quickly.
You should be able to determine if messaging or pricing, or technical challenges are causing friction for your customers.
You should be able to answer questions like:
1. Is my paywall wording or the price of my subscriptions causing my customers to subscribe less?
2. Is my website performance or my application performance driving retention?
3. Is purchasing a specific product or the product portfolio driving referrals?

With the correct approach to instrumentation coupled with AI-enhanced analytics, you can quickly answer these questions and much more.

<br/>

[back to top](#contents)

## Get Started With The Following Steps: <a id="getting-started"></a>
The Xenon View SDK can be used in your application to provide a new level of customer telemetry. You"ll need to embed the instrumentation into your website/application via this SDK.

Instrumentation will vary based on your use case; are you offering a service/subscription (SaaS) or selling products (Ecom)?

In a nutshell, the steps to get started are as follows:
1. Identify Business Outcomes and Customer Journey Milestones leading to those Outcomes.
2. Instrument the Outcomes/Milestones.
3. Analyze the results.

<br/>


### Step 1 - Business Outcomes <a id="step-1"></a>

Regardless of your business model, your first step will be identifying your desired business outcomes.

**Example - Service/Subscription/SaaS**:
1. Lead Capture
2. Account Signup
3. Initial Subscription
4. Renewed Subscription
5. Upsold Subscription
6. Referral

**Example - Ecom**:
1. Place the product in the cart
2. Checkout
3. Upsold
4. Purchase

> :memo: Note: Each outcome has an associated success and failure.

<br/>


### Step 2 - Customer Journey Milestones <a id="step-2"></a>

For each Business Outcome, identify potential customer journey milestones leading up to that business outcome.

**Example - Service/Subscription/SaaS for _Lead Capture_**:
1. View informational content
2. Asks question in the forum
3. Views FAQs
4. Views HowTo
5. Requests info product

**Example - Ecom for _Place product in cart_** :
1. Search for product information
2. Learns about product
3. Read reviews

<br/>

### Step 3 - Enumerate Technical Stack <a id="step-3"></a>

Next, you will want to figure out which SDK to use. We have some of the most popular languages covered.

Start by listing the technologies involved and what languages your company uses. For example:
1. Front end - UI (Javascript - react)
2. Back end - API server (Java)
3. Mobile app - iPhone (Swift)
4. Mobile app - Android (Android Java)

Next, figure out how your outcomes spread across those technologies. Below are pointers to our currently supported languages:
* [React](https://github.com/xenonview-com/view-js-sdk)
* [Angular](https://github.com/xenonview-com/view-js-sdk)
* [HTML](https://github.com/xenonview-com/view-js-sdk)
* [Plain JavaScript](https://github.com/xenonview-com/view-js-sdk)
* [iPhone/iPad](https://github.com/xenonview-com/view-swift-sdk)
* [Mac](https://github.com/xenonview-com/view-swift-sdk)
* [Java](https://github.com/xenonview-com/view-java-sdk)
* [Android Java](https://github.com/xenonview-com/view-java-sdk)
* [Python](https://github.com/xenonview-com/view-python-sdk)

Finally, continue the steps below for each technology and outcome.


### Step 4 - Installation <a id="step-4"></a>

After you have done the prework of [Step 1](#step-1) and [Step 2](#step-2), you are ready to [install Xenon View](#installation).
Once installed, you"ll need to [initialize the SDK](#instantiation) and get started instrumenting.


<br/>
<br/>


### Step 5 - Instrument Business Outcomes <a id="step-5"></a>

We have provided several SDK calls to shortcut your instrumentation and map to the outcomes identified in [Step 1](#step-1).  
These calls will roll up into the associated Categories during analysis. These rollups allow you to view each Category in totality.
As you view the categories, you can quickly identify issues (for example, if there are more Failures than Successes for a Category).

**[Service/Subscription/SaaS Related Outcome Calls](#saas)**  (click on a call to see usage)

| Category | Success | Failure | 
| --- | --- | --- |
| Lead Capture | [`leadCaptured()`](#saas-lead-capture) | [`leadCaptureDeclined()`](#saas-lead-capture-fail) | 
| Account Signup | [`accountSignup()`](#saas-account-signup) | [`accountSignupDeclined()`](#saas-account-signup-fail) | 
| Application Installation | [`applicationInstalled()`](#saas-application-install) |  [`applicationNotInstalled()`](#saas-application-install-fail) | 
| Initial Subscription | [`initialSubscription()`](#saas-initial-subscription) | [`subscriptionDeclined()`](#saas-initial-subscription-fail) |
| Subscription Renewed | [`subscriptionRenewed()`](#saas-renewed-subscription) | [`subscriptionCanceled()`](#saas-renewed-subscription-fail) | 
| Subscription Upsell | [`subscriptionUpsold()`](#saas-upsell-subscription) | [`subscriptionUpsellDeclined()`](#saas-upsell-subscription-fail) | 
| Referral | [`referral()`](#saas-referral) | [`referralDeclined()`](#saas-referral-fail) | 


**[Ecom Related Outcome Calls](#ecom)** (click on a call to see usage)

| Category | Success | Failure |
| --- | --- | --- | 
| Lead Capture | [`leadCaptured()`](#ecom-lead-capture) | [`leadCaptureDeclined()`](#ecom-lead-capture-fail) | 
| Account Signup | [`accountSignup()`](#ecom-account-signup) | [`accountSignupDeclined()`](#ecom-account-signup-fail) | 
| Add To Cart | [`productAddedToCart()`](#ecom-product-to-cart) | [`productNotAddedToCart()`](#ecom-product-to-cart-fail) |
| Product Upsell | [`upsold()`](#ecom-upsell) | [`upsellDismissed()`](#ecom-upsell-fail) | 
| Checkout | [`checkedOut()`](#ecom-checkout) | [`checkoutCanceled()`](#ecom-checkout-fail)/[`productRemoved()`](#ecom-checkout-remove) | 
| Purchase | [`purchased()`](#ecom-purchase) | [`purchaseCanceled()`](#ecom-purchase-fail) | 
| Promise Fulfillment | [`promiseFulfilled()`](#ecom-promise-fulfillment) | [`promiseUnfulfilled()`](#ecom-promise-fulfillment-fail) | 
| Product Disposition | [`productKept()`](#ecom-product-outcome) | [`productReturned()`](#ecom-product-outcome-fail) |
| Referral | [`referral()`](#ecom-referral) | [`referralDeclined()`](#ecom-referral-fail) |

<br/>

### Step 6 - Instrument Customer Journey Milestones <a id="step-6"></a>

Next, you will want to instrument your website/application/backend/service for the identified Customer Journey Milestones [Step 2](#step-2).
We have provided several SDK calls to shortcut your instrumentation here as well.

During analysis, each Milestone is chained together with the proceeding and following Milestones.
That chain terminates with an Outcome (described in [Step 4](#step-4)).
AI/ML is employed to determine Outcome correlation and predictability for the chains and individual Milestones.
During the [analysis step](#step-8), you can view the correlation and predictability as well as the Milestone chains
(called Customer Journeys in this guide).

Milestones break down into two types (click on a call to see usage):

| Features | Content |
| --- | --- |
| [`featureAttempted()`](#feature-started) | [`contentViewed()`](#content-viewed) |
| [`featureFailed()`](#feature-failed) | [`contentEdited()`](#content-edited) |
| [`featureCompleted()`](#feature-complete) | [`contentCreated()`](#content-created) |
| | [`contentDeleted()`](#content-deleted) |
| | [`contentRequested()`](#content-requested)|
| | [`contentSearched()`](#content-searched)|

<br/>

### Step 7 - Commit Points <a id="step-7"></a>


Once instrumented, you"ll want to select appropriate [commit points](#commit). Committing will initiate the analysis on your behalf by Xenon View.

<br/>
<br/>

### Step 8 (Optional) - Group Customer Journeys <a id="step-8"></a>

All the customer journeys (milestones and outcomes) are anonymous by default.
For example, if a Customer interacts with your brand in the following way:
1. Starts on your marketing website.
2. Downloads and uses an app.
3. Uses a feature requiring an API call.


*Each of those journeys will be unconnected and not grouped.*

To associate those journeys with each other, you can [deanonymize](#deanonymizing-journeys) the Customer. Deanonymizing will allow for a deeper analysis of a particular user.

Deanonymizing is optional. Basic matching of the customer journey with outcomes is valuable by itself. Deanonymizing will add increased insight as it connects Customer Journeys across devices.

<br/>

### Step 9 - Analysis <a id="step-9"></a>


Once you have released your instrumented code, you can head to [XenonView](https://xenonview.com/) to view the analytics.

<br/>

### Step 10 - Perform Experiments <a id="step-10"></a>

There are multiple ways you can experiment using XenonView. We"ll focus here on three of the most common: time, platform, and variant based cohorts.

#### Time-based cohorts
Each Outcome and Milestone is timestamped. You can use this during the analysis phase to compare timeframes. A typical example is making a feature change.
Knowing when the feature went to production, you can filter in the XenonView UI based on the timeframe before and the timeframe after to observe the results.

#### Variant-based cohorts
You can identify a journey collection as an [experiment](#experiments) before collecting data. This will allow you to run A/B testing-type experiments (of course not limited to two).
As an example, let"s say you have two alternate content/feature variants and you have a way to direct half of the users to Variant A and the other half to Variant B.
You can name each variant before the section of code that performs that journey. After collecting the data, you can filter in the XenonView UI based on each variant to
observe the results.

#### Platform-based cohorts
You can [Platform](#platforming) any journey collection before collecting data. This will allow you to experiment against different platforms:
* Operating System Name
* Operating System version
* Device model (Pixel, iPhone 14, Docker Container, Linux VM, Dell Server, etc.)
* A software version of your application.

As an example, let"s say you have an iPhone and Android mobile application and you want to see if an outcome is more successful on one device verse the other.
You can platform before the section of code that performs that flow. After collecting the data, you can filter in the XenonView UI based on each platform to
observe the results.

<br/>
<br/>
<br/>

[back to top](#contents)

## Detailed Usage <a id="detailed-usage"></a>
The following section gives detailed usage instructions and descriptions.
It provides code examples for each of the calls.

<br/>

### Installation <a id="installation"></a>

<br/>


You can install the Xenon View SDK from [maven central](https://search.maven.org/artifact/io.github.xenonview-com/xenon-view-sdk/0.1.6/jar):

#### <a name="maven"></a>
Via maven:
```xml
<dependency>
  <groupId>io.github.xenonview-com</groupId>
  <artifactId>xenon-view-sdk</artifactId>
  <version>0.1.6</version>
</dependency>
```

#### <a name="gradle-groovy"></a>
Via gradle (groovy):
```groovy
implementation "io.github.xenonview-com:xenon-view-sdk:0.1.6"
```

#### <a name="gradle-kotlin"></a>
Via gradle (kolin):
```kotlin
implementation("io.github.xenonview-com:xenon-view-sdk:0.1.6")
```

#### <a name="download-jar"></a>
Via jar download (maven central):

Download required Jars and import as libraries into your project:  
[Download Jar](https://s01.oss.sonatype.org/content/repositories/releases/io/github/xenonview-com/xenon-view-sdk/0.1.6/xenon-view-sdk-0.1.6.jar)  
[Download Dependencies Jar](https://github.com/xenonview-com/view-java-sdk/releases/download/v0.1.6/xenon-view-sdk-0.1.6-dependencies.jar)

<br/>

[back to top](#contents)

### Instantiation <a id="instantiation"></a>

The View SDK is a Java module you"ll need to include in your application. After inclusion, you"ll need to init the singleton object:

```java
import xenon.view.sdk.Xenon;

// start by initializing Xenon View
final Xenon xenon = new Xenon("<API KEY>");
```

-OR-

```java
import xenon.view.sdk.Xenon;

// to initialize Xenon View after final Stringruction
final Xenon xenon = new Xenon();
xenon.init("<API KEY>");
```
Of course, you"ll have to make the following modifications to the above code:
- Replace `<API KEY>` with your [api key](https://xenonview.com/api-get)

<br/>

[back to top](#contents)

### Service/Subscription/SaaS Related Business Outcomes <a id="saas"></a>

<br/>

#### Lead Capture  <a id="saas-lead-capture"></a>
Use this call to track Lead Capture (emails, phone numbers, etc.)
You can add a specifier string to the call to differentiate as follows:

<br/>

##### ```leadCaptured()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String emailSpecified = "Email";
final String phoneSpecified = "Phone Number";

// Successful Lead Capture of an email
xenon.leadCaptured(emailSpecified);
//...
// Successful Lead Capture of a phone number
xenon.leadCaptured(phoneSpecified);
```

<br/>

##### ```leadCaptureDeclined()``` <a id="saas-lead-capture-fail"></a>
> :memo: Note: You want to be consistent between success and failure and match the specifiers
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String emailSpecified = "Email";
final String phoneSpecified = "Phone Number"; 

// Unsuccessful Lead Capture of an email
xenon.leadCaptureDeclined(emailSpecified);
// ...
// Unsuccessful Lead Capture of a phone number
xenon.leadCaptureDeclined(phoneSpecified);
```

<br/>

#### Account Signup  <a id="saas-account-signup"></a>
Use this call to track when customers signup for an account.
You can add a specifier string to the call to differentiate as follows:

<br/>

##### ```accountSignup()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String viaFacebook = "Facebook";
final String viaGoogle = "Facebook";
final String viaEmail = "Email";

// Successful Account Signup with Facebook
xenon.accountSignup(viaFacebook);
// ...
// Successful Account Signup with Google
xenon.accountSignup(viaGoogle);
// ...
// Successful Account Signup with an Email
xenon.accountSignup(viaEmail);
```

<br/>

##### ```accountSignupDeclined()``` <a id="saas-account-signup-fail"></a>
> :memo: Note: You want to be consistent between success and failure and match the specifiers
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String viaFacebook = "Facebook";
final String viaGoogle = "Facebook";
final String viaEmail = "Email";

// Unsuccessful Account Signup with Facebook
xenon.accountSignupDeclined(viaFacebook);
// ...
// Unsuccessful Account Signup with Google
xenon.accountSignupDeclined(viaGoogle);
// ...
// Unsuccessful Account Signup with an Email
xenon.accountSignupDeclined(viaEmail);
```

<br/>

#### Application Installation  <a id="saas-application-install"></a>
Use this call to track when customers install your application.

<br/>

##### ```applicationInstalled()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

// Successful Application Installation
xenon.applicationInstalled();
```

<br/>

##### ```applicationNotInstalled()``` <a id="saas-application-install-fail"></a>
> :memo: Note: You want consistency between success and failure.
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

// Unsuccessful or not completed Application Installation
xenon.applicationNotInstalled();
```

<br/>

#### Initial Subscription  <a id="saas-initial-subscription"></a>
Use this call to track when customers initially subscribe.
You can add a specifier string to the call to differentiate as follows:

<br/>

##### ```initialSubscription()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String tierSilver = "Silver Monthly";
final String tierGold = "Gold";
final String tierPlatium = "Platium";
final String annualSilver = "Silver Annual";
final String method = "Stripe"; // optional

// Successful subscription of the lowest tier with Stripe
xenon.initialSubscription(tierSilver, method);
// ...
// Successful subscription of the middle tier
xenon.initialSubscription(tierGold);
// ...
// Successful subscription to the top tier
xenon.initialSubscription(tierPlatium);
// ...
// Successful subscription of an annual period
xenon.initialSubscription(annualSilver);
```

<br/>

##### ```subscriptionDeclined()``` <a id="saas-initial-subscription-fail"></a>
> :memo: Note: You want to be consistent between success and failure and match the specifiers
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String tierSilver = "Silver Monthly";
final String tierGold = "Gold";
final String tierPlatium = "Platium";
final String annualSilver = "Silver Annual";
final String method = "Stripe"; // optional

// Unsuccessful subscription of the lowest tier
xenon.subscriptionDeclined(tierSilver);
// ...
// Unsuccessful subscription of the middle tier
xenon.subscriptionDeclined(tierGold);
// ...
// Unsuccessful subscription to the top tier
xenon.subscriptionDeclined(tierPlatium);
// ...
// Unsuccessful subscription of an annual period
xenon.subscriptionDeclined(annualSilver, method);
```

<br/>

#### Subscription Renewal  <a id="saas-renewed-subscription"></a>
Use this call to track when customers renew.
You can add a specifier string to the call to differentiate as follows:

<br/>

##### ```subscriptionRenewed()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String tierSilver = "Silver Monthly";
final String tierGold = "Gold";
final String tierPlatium = "Platium";
final String annualSilver = "Silver Annual";
final String method = "Stripe"; //optional

// Successful renewal of the lowest tier with Stripe
xenon.subscriptionRenewed(tierSilver, method);
// ...
// Successful renewal of the middle tier
xenon.subscriptionRenewed(tierGold);
// ...
// Successful renewal of the top tier
xenon.subscriptionRenewed(tierPlatium);
// ...
// Successful renewal of an annual period
xenon.subscriptionRenewed(annualSilver);
```

<br/>

##### ```subscriptionCanceled()``` <a id="saas-renewed-subscription-fail"></a>
> :memo: Note: You want to be consistent between success and failure and match the specifiers
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String tierSilver = "Silver Monthly";
final String tierGold = "Gold";
final String tierPlatium = "Platium";
final String annualSilver = "Silver Annual";
final String method = "Stripe"; //optional

// Canceled subscription of the lowest tier
xenon.subscriptionCanceled(tierSilver);
// ...
// Canceled subscription of the middle tier
xenon.subscriptionCanceled(tierGold);
// ...
// Canceled subscription of the top tier
xenon.subscriptionCanceled(tierPlatium);
// ...
// Canceled subscription of an annual period with Stripe
xenon.subscriptionCanceled(annualSilver, method);
```

<br/>

#### Subscription Upsold  <a id="saas-upsell-subscription"></a>
Use this call to track when a Customer upgrades their subscription.  
You can add a specifier string to the call to differentiate as follows:

<br/>

##### ```subscriptionUpsold()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String tierGold = "Gold Monthly";
final String tierPlatium = "Platium";
final String annualGold = "Gold Annual";
final String method = "Stripe"; // optional

// Assume already subscribed to Silver

// Successful upsell of the middle tier with Stripe
xenon.subscriptionUpsold(tierGold, method);
// ...
// Successful upsell of the top tier
xenon.subscriptionUpsold(tierPlatium);
// ...
// Successful upsell of middle tier - annual period
xenon.subscriptionUpsold(annualGold);
```


<br/>

##### ```subscriptionUpsellDeclined()``` <a id="saas-upsell-subscription-fail"></a>
> :memo: Note: You want to be consistent between success and failure and match the specifiers
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String tierGold = "Gold Monthly";
final String tierPlatium = "Platium";
final String annualGold = "Gold Annual";
final String method = "Stripe"; //optional

// Assume already subscribed to Silver

// Rejected upsell of the middle tier
xenon.subscriptionUpsellDeclined(tierGold);
// ...
// Rejected upsell of the top tier
xenon.subscriptionUpsellDeclined(tierPlatium);
// ...
// Rejected upsell of middle tier - annual period with Stripe
xenon.subscriptionUpsellDeclined(annualGold, method);
```

<br/>

#### Referrals  <a id="saas-referral"></a>
Use this call to track when customers refer someone to your offering.
You can add a specifier string to the call to differentiate as follows:

<br/>

##### ```referral()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String kind = "Share";
final String detail = "Review"; // optional

// Successful referral by sharing a review
xenon.referral(kind, detail);
// -OR-
xenon.referral(kind);
```

<br/>

##### ```referralDeclined()``` <a id="saas-referral-fail"></a>
> :memo: Note: You want to be consistent between success and failure and match the specifiers
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String kind = "Share";
final String detail = "Review"; // optional

//Customer declined referral 
xenon.referralDeclined(kind, detail);
// -OR-
xenon.referralDeclined(kind);
```

<br/>

[back to top](#contents)

### Ecommerce Related Outcomes <a id="ecom"></a>


<br/>

#### Lead Capture  <a id="ecom-lead-capture"></a>
Use this call to track Lead Capture (emails, phone numbers, etc.)
You can add a specifier string to the call to differentiate as follows:

<br/>

##### ```leadCaptured()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String emailSpecified = "Email";
final String phoneSpecified = "Phone Number";

// Successful Lead Capture of an email
xenon.leadCaptured(emailSpecified);
// ...
// Successful Lead Capture of a phone number
xenon.leadCaptured(phoneSpecified);
```

<br/>

##### ```leadCaptureDeclined()``` <a id="ecom-lead-capture-fail"></a>
> :memo: Note: You want to be consistent between success and failure and match the specifiers
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String emailSpecified = "Email";
final String phoneSpecified = "Phone Number"; 

// Unsuccessful Lead Capture of an email
xenon.leadCaptureDeclined(emailSpecified);
// ...
// Unsuccessful Lead Capture of a phone number
xenon.leadCaptureDeclined(phoneSpecified);
```

<br/>

#### Account Signup  <a id="ecom-account-signup"></a>
Use this call to track when customers signup for an account.
You can add a specifier string to the call to differentiate as follows:

<br/>

##### ```accountSignup()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String viaFacebook = "Facebook";
final String viaGoogle = "Facebook";
final String viaEmail = "Email";

// Successful Account Signup with Facebook
xenon.accountSignup(viaFacebook);
// ...
// Successful Account Signup with Google
xenon.accountSignup(viaGoogle);
// ...
// Successful Account Signup with an Email
xenon.accountSignup(viaEmail);
```

<br/>

##### ```accountSignupDeclined()``` <a id="ecom-account-signup-fail"></a>
> :memo: Note: You want to be consistent between success and failure and match the specifiers
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String viaFacebook = "Facebook";
final String viaGoogle = "Facebook";
final String viaEmail = "Email";

// Unsuccessful Account Signup with Facebook
xenon.accountSignupDeclined(viaFacebook);
// ...
// Unsuccessful Account Signup with Google
xenon.accountSignupDeclined(viaGoogle);
// ...
// Unsuccessful Account Signup with an Email
xenon.accountSignupDeclined(viaEmail);
```

<br/>

#### Add Product To Cart  <a id="ecom-product-to-cart"></a>
Use this call to track when customers add a product to the cart.
You can add a specifier string to the call to differentiate as follows:

<br/>

##### ```productAddedToCart()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String laptop = "Dell XPS";
final String keyboard = "Apple Magic Keyboard";

// Successful adds a laptop to the cart
xenon.productAddedToCart(laptop);
// ...
// Successful adds a keyboard to the cart
xenon.productAddedToCart(keyboard);
```

<br/>

##### ```productNotAddedToCart()``` <a id="ecom-product-to-cart-fail"></a>
> :memo: Note: You want to be consistent between success and failure and match the specifiers
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String laptop = "Dell XPS";
final String keyboard = "Apple Magic Keyboard";

// Doesn"t add a laptop to the cart
xenon.productNotAddedToCart(laptop);
// ...
// Doesn"t add a keyboard to the cart
xenon.productNotAddedToCart(keyboard);
```


<br/>

#### Upsold Additional Products  <a id="ecom-upsell"></a>
Use this call to track when you upsell additional product(s) to customers.
You can add a specifier string to the call to differentiate as follows:

<br/>

##### ```upsold()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String laptop = "Dell XPS";
final String keyboard = "Apple Magic Keyboard";

// upsold a laptop
xenon.upsold(laptop);
// ...
// upsold a keyboard
xenon.upsold(keyboard);
```


<br/>

##### ```upsellDismissed()``` <a id="ecom-upsell-fail"></a>
> :memo: Note: You want to be consistent between success and failure and match the specifiers
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String laptop = "Dell XPS";
final String keyboard = "Apple Magic Keyboard";

// Doesn"t add a laptop during upsell
xenon.upsellDismissed(laptop);
// ...
// Doesn"t add a keyboard during upsell
xenon.upsellDismissed(keyboard);
```


<br/>

#### Customer Checks Out  <a id="ecom-checkout"></a>
Use this call to track when your Customer is checking out.

<br/>

##### ```checkedOut()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

// Successful Checkout
xenon.checkedOut();
```

<br/>

##### ```checkoutCanceled()``` <a id="ecom-checkout-fail"></a>
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

//Customer cancels check out.
xenon.checkoutCanceled();

```


<br/>

##### ```productRemoved()``` <a id="ecom-checkout-remove"></a>
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String laptop = "Dell XPS";
final String keyboard = "Apple Magic Keyboard";

// Removes a laptop during checkout
xenon.productRemoved(laptop);
// ...
// Removes a keyboard during checkout
xenon.productRemoved(keyboard);
```


<br/>

#### Customer Completes Purchase  <a id="ecom-purchase"></a>
Use this call to track when your Customer completes a purchase.

<br/>

##### ```purchased()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String method = "Stripe";

// Successful Purchase
xenon.purchased(method);
```


<br/>

##### ```purchaseCanceled()``` <a id="ecom-purchase-fail"></a>
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String method = "Stripe"; // optional

//Customer cancels the purchase.
xenon.purchaseCanceled();
// -OR-
xenon.purchaseCanceled(method);

```


<br/>

#### Purchase Shipping  <a id="ecom-promise-fulfillment"></a>
Use this call to track when your Customer receives a purchase.

<br/>

##### ```promiseFulfilled()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

// Successfully Delivered Purchase
xenon.promiseFulfilled();
```


<br/>

##### ```promiseUnfulfilled(()``` <a id="ecom-promise-fulfillment-fail"></a>
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

// Problem Occurs During Shipping And No Delivery
xenon.promiseUnfulfilled();
```


<br/>

#### Customer Keeps or Returns Product  <a id="ecom-product-outcome"></a>
Use this call to track if your Customer keeps the product.
You can add a specifier string to the call to differentiate as follows:

<br/>

##### ```productKept()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String laptop = "Dell XPS";
final String keyboard = "Apple Magic Keyboard";

//Customer keeps a laptop
xenon.productKept(laptop);
// ...
//Customer keeps a keyboard
xenon.productKept(keyboard);
```


<br/>

##### ```productReturned()``` <a id="ecom-product-outcome-fail"></a>
> :memo: Note: You want to be consistent between success and failure and match the specifiers
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String laptop = "Dell XPS";
final String keyboard = "Apple Magic Keyboard";

//Customer returns a laptop
xenon.productReturned(laptop);
// ...
//Customer returns a keyboard
xenon.productReturned(keyboard);
```


<br/>

#### Referrals  <a id="ecom-referral"></a>
Use this call to track when customers refer someone to your offering.
You can add a specifier string to the call to differentiate as follows:

<br/>

##### ```referral()```
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String kind = "Share Product";
final String detail = "Dell XPS";

// Successful referral by sharing a laptop
xenon.referral(kind, detail);
```


<br/>

##### ```referralDeclined()``` <a id="ecom-referral-fail"></a>
> :memo: Note: You want to be consistent between success and failure and match the specifiers
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String kind = "Share Product";
final String detail = "Dell XPS";

//Customer declined referral 
xenon.referralDeclined(kind, detail);
```


<br/>

[back to top](#contents)

### Customer Journey Milestones <a id="milestones"></a>

As a customer interacts with your brand (via Advertisements, Marketing Website, Product/Service, etc.), they journey through a hierarchy of interactions.
At the top level are business outcomes. In between Outcomes, they may achieve other milestones, such as interacting with content and features.
Proper instrumentation of these milestones can establish correlation and predictability of business outcomes.

As of right now, Customer Journey Milestones break down into two categories:
1. [Feature Usage](#feature-usage)
2. [Content Interaction](#content-interaction)

<br/>

#### Feature Usage  <a id="feature-usage"></a>
Features are your product/application/service"s traits or attributes that deliver value to your customers.
They differentiate your offering in the market. Typically, they are made up of and implemented by functions.

<br/>

##### ```featureAttempted()``` <a id="feature-started"></a>
Use this function to indicate the start of feature usage.
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String name = "Scale Recipe";
final String detail = "x2"; // optional

//Customer initiated using a feature 
xenon.featureAttempted(name, detail);
// -OR-
xenon.featureAttempted(name);
```


<br/>

##### ```featureCompleted()``` <a id="feature-complete"></a>
Use this function to indicate the successful completion of the feature.
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String name = "Scale Recipe";
final String detail = "x2"; // optional

// ...
// Customer used a feature 
xenon.featureCompleted(name, detail);

// -OR-

// Customer initiated using a feature 
xenon.featureAttempted(name, detail);
// ...
// feature code/function calls
// ...
// feature completes successfully 
xenon.featureCompleted(name, detail);
// -OR-
xenon.featureCompleted(name);
```


<br/>

##### ```featureFailed()``` <a id="feature-failed"></a>
Use this function to indicate the unsuccessful completion of a feature being used (often in the exception handler).
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();


final String name = "Scale Recipe";
final String detail = "x2"; // optional


//Customer initiated using a feature 
xenon.featureAttempted(name, detail);
try {
  // feature code that could fail
}
catch(err) {
  //feature completes unsuccessfully 
  xenon.featureFailed(name, detail);
  // -OR-
  xenon.featureFailed(name);
}

```


<br/>

[back to top](#contents)

#### Content Interaction  <a id="content-interaction"></a>
Content is created assets/resources for your site/service/product.
It can be static or dynamic. You will want to mark content that contributes to your Customer"s experience or buying decision.
Typical examples:
* Blog
* Blog posts
* Video assets
* Comments
* Reviews
* HowTo Guides
* Charts/Graphs
* Product/Service Descriptions
* Surveys
* Informational product

<br/>

##### ```contentViewed()``` <a id="content-viewed"></a>
Use this function to indicate a view of specific content.
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String contentType = "Blog Post";
final String identifier = "how-to-install-xenon-view"; // optional

// Customer view a blog post 
xenon.contentViewed(contentType, identifier);
// -OR-
xenon.contentViewed(contentType);
```


<br/>

##### ```contentEdited()``` <a id="content-edited"></a>
Use this function to indicate the editing of specific content.
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String contentType = "Review";
final String identifier = "Dell XPS"; //optional
final String detail = "Rewrote"; //optional

//Customer edited their review about a laptop
xenon.contentEdited(contentType, identifier, detail);
// -OR-
xenon.contentEdited(contentType, identifier);
// -OR-
xenon.contentEdited(contentType);
```


<br/>

##### ```contentCreated()``` <a id="content-created"></a>
Use this function to indicate the creation of specific content.
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String contentType = "Blog Comment";
final String identifier = "how-to-install-xenon-view"; // optional

//Customer wrote a comment on a blog post
xenon.contentCreated(contentType, identifier);
// -OR- 
xenon.contentCreated(contentType);
```


<br/>

##### ```contentDeleted()``` <a id="content-deleted"></a>
Use this function to indicate the deletion of specific content.
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String contentType = "Blog Comment";
final String identifier = "how-to-install-xenon-view"; // optional

//Customer deleted their comment on a blog post 
xenon.contentDeleted(contentType, identifier);
// -OR- 
xenon.contentDeleted(contentType);
```


<br/>

##### ```contentRequested()``` <a id="content-requested"></a>
Use this function to indicate the request for specific content.
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String contentType = "Info Product";
final String identifier = "how-to-efficiently-use-google-ads"; // optional

//Customer requested some content
xenon.contentRequested(contentType, identifier);
// -OR- 
xenon.contentRequested(contentType);
```


<br/>

##### ```contentSearched()``` <a id="content-searched"></a>
Use this function to indicate when a user searches.
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String contentType = "Info Product";

// Customer searched for some content
xenon.contentSearched(contentType);
```


<br/>

[back to top](#contents)

### Commit Points   <a id="commiting"></a>


Business Outcomes and Customer Journey Milestones are tracked locally in memory until you commit them to the Xenon View system.
After you have created (by either calling a milestone or outcome) a customer journey, you can commit it to Xenon View for analysis as follows:

<br/>

#### `commit()`
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

// you can commit a journey to Xenon View
xenon.commit();
// -OR-
final Json json = xenon.commit().get();
```

This call commits a customer journey to Xenon View for analysis.



<br/>

[back to top](#contents)

### Heartbeats   <a id="heartbeat"></a>


Business Outcomes and Customer Journey Milestones are tracked locally in memory until you commit them to the Xenon View system.
You can use the heartbeat call if you want to commit in batch.
Additionally, the heartbeat call will update a last-seen metric for customer journeys that have yet to arrive at Business Outcome. The last-seen metric is useful when analyzing stalled Customer Journeys.

Usage is as follows:

<br/>

#### `heartbeat()`
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

// you can heartbeat to Xenon View
xenon.heartbeat();
// -OR-
final Json json = xenon.heartbeat().get();
```


This call commits any uncommitted journeys to Xenon View for analysis and updates the last accessed time.


<br/>

[back to top](#contents)

### Platforming  <a id="platforming"></a>

After you have initialized Xenon View, you can optionally specify platform details such as:

- Operating System Name
- Operating System version
- Device model (Pixel, Docker Container, Linux VM, Dell Server, etc.)
- A software version of your application.

<br/>

#### `platform()`
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String softwareVersion = "5.1.5";
final String deviceModel = "Pixel 4 XL";
final String operatingSystemVersion = "12.0";
final String operatingSystemName = "Android";

// you can add platform details to outcomes
xenon.platform(softwareVersion, deviceModel, operatingSystemName, operatingSystemVersion);
```
This adds platform details for each outcome ([Saas](#saas)/[Ecom](#ecom)). Typically, this would be set once at initialization:
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

xenon.init("<API KEY>");

final String softwareVersion = "5.1.5";
final String deviceModel = "Pixel 4 XL";
final String operatingSystemVersion = "12.0";
final String operatingSystemName = "Android";
xenon.platform(softwareVersion, deviceModel, operatingSystemName, operatingSystemVersion);
```

<br/>

[back to top](#contents)

### Experiments  <a id="experiments"></a>

After you have initialized Xenon View, you can optionally name variants of customer journeys.
Named variants facilitate running experiments such as A/B or split testing.

> :memo: Note: You are not limited to just 2 (A or B); there can be many. Additionally, you can have multiple variant names.

<br/>

#### `variant()`
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

final String variantName = "subscription-variant-A";
final String[] variantNames = {variantName};

// you can name variants for to outcomes
xenon.variant(variantNames);
```

This adds variant names to each outcome while the variant in play ([Saas](#saas)/[Ecom](#ecom)).
Typically, you would name a variant once you know the active experiment for this Customer:
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

xenon.init("<API KEY>");
let experimentName = getExperiment();
xenon.variant([experimentName]);
```

<br/>

#### `resetVariants()`
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

// you can clear all variant names with the resetVariants method
xenon.resetVariants();
```

<br/>

[back to top](#contents)

### Customer Journey Grouping <a id="deanonymizing-journeys"></a>


Xenon View supports both anonymous and grouped (known) journeys.

All the customer journeys (milestones and outcomes) are anonymous by default.
For example, if a Customer interacts with your brand in the following way:
1. Starts on your marketing website.
2. Downloads and uses an app.
3. Uses a feature requiring an API call.

*Each of those journeys will be unconnected and not grouped.*

To associate those journeys with each other, you can use `deanonymize()`. Deanonymizing will allow for a deeper analysis of a particular user.

Deanonymizing is optional. Basic matching of the customer journey with outcomes is valuable by itself. Deanonymizing will add increased insight as it connects Customer Journeys across devices.

Usage is as follows:

<br/>

#### `deanonymize()`
```java
import xenon.view.sdk.Xenon;
import org.json.JSONObject;

final Xenon xenon = new Xenon();


// you can deanonymize before or after you have committed journey (in this case after):
JSONObject person = new JSONObject(){{
    put("name","Java Test");
    put("email","javatest@example.com");
}};
xenon.deanonymize(person);
// -OR-
final Json json = xenon.deanonymize().get();

// you can also deanonymize with a user ID:
JSONObject person = new JSONObject(){{
    put("UUID","<some unique ID>");
}};
xenon.deanonymize(person);
```

This call deanonymizes every journey committed to a particular user.

> **:memo: Note:** With journeys that span multiple platforms (e.g., Website->Android->API backend), you can group the Customer Journeys by deanonymizing each.


<br/>

[back to top](#contents)

### Other Operations <a id="other"></a>

There are various other operations that you might find helpful:

<br/>
<br/>

#### Error handling <a id="errors"></a>
In the event of an API error when committing, the method returns a [promise](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise).

> **:memo: Note:** The default handling of this situation will restore the journey (appending newly added pageViews, events, etc.) for future committing. If you want to do something special, you can do so like this:

```java
import xenon.view.sdk.Xenon;
import xenon.xenon.sdk.api.fetch.Json;

final Xenon xenon = new Xenon();

// you can handle errors if necessary
new Xenon().commit().exceptionally((err)->{
    // handle error
    return Json("{}");
});
```

<br/>

#### Custom Milestones <a id="custom"></a>

You can add custom milestones if you need more than the current Customer Journey Milestones.

<br/>

##### `milestone()`
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();

// you can add a custom milestone to the customer journey
let category = "Function";
let operation = "Called";
let name = "Query Database";
let detail = "User Lookup";
xenon.milestone(category, operation, name, detail);
```

This call adds a custom milestone to the customer journey.

<br/>

#### Journey IDs <a id="cuuid"></a>
Each Customer Journey has an ID akin to a session.
After committing an Outcome, the ID remains the same to link all the Journeys.
If you have a previous Customer Journey in progress and would like to append to that, you can get/set the ID.

>**:memo: Note:** For JavaScript, the Journey ID is a persistent session variable.
> Therefore, subsequent Outcomes will reuse the Journey ID if the Customer had a previous browser session.


After you have initialized the Xenon singleton, you can:
1. Use the default UUID
2. Set the Customer Journey (Session) ID
3. Regenerate a new UUID
4. Retrieve the Customer Journey (Session) ID

<br/>

##### `id()`
```java
import xenon.view.sdk.Xenon;

final Xenon xenon = new Xenon();
// by default has Journey ID
expect(xenon.id()).not.toBeNull();
expect(xenon.id()).not.toEqual("");

// you can also set the id
let testId = "<some random uuid>";
xenon.id(testId);
expect(xenon.id()).toEqual(testId);

// Lastly, you can generate a new Journey ID (useful for serialized async operations that are for different customers)
xenon.newId();
expect(xenon.id()).not.toBeNull();
expect(xenon.id()).not.toEqual("");
```


<br/>

[back to top](#contents)

## License  <a name="license"></a>

Apache Version 2.0

See [LICENSE](https://github.com/xenonview-com/view-js-sdk/blob/main/LICENSE)

[back to top](#contents)

