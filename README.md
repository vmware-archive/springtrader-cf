# SpringTrader Part 3
This is the readme for the branch representing the "solution" to Part 3 for the *Refactoring a Monolith into a Cloud-Native Application* blog. For the "real" application documentation please check out the master branch and refer to its README.md

To see what was done in part 3, consult the diff: [here](https://github.com/cf-platform-eng/springtrader-cf/compare/part2...part3).

## The Changes
###README.md
* Edits related to the creation of this very README file, including these very words.

### build.gradle
* Adding in the Hystrix and Eureka libraries, plus some feign functionality.

### config.sh, deleteDeployment.sh and deployApp.sh
* Moved all editable stuff into a separate config.sh file
* Remove quote service URI and user provided service: Eureka takes care of this now.
* Read in env variables to set the db and live quote service names. These are defined in the manifest and will be used to look up services in Eureka. 

### changes to spring-nanotrader-asynch-services/...

* **xml and other config files**:  Tie schema to specific version, needed because we are using a mixture of older and newer spring classes. Also register a task scheduler to update our DB Quote service with the latest info from our Live quote service once a minute. Plus some log config cleanups.

* **additional tests and test configurations**: added test and related configuration files.

### changes to spring-nanotrader-data/...
* **CloudConfiguration**: annotations for Eureka, and code to register the DiscoveryClient, Hystrix, and the names of the live and db quote services as registered in Eureka. Spring-Cloud would have made this a lot easier.

* **DBQuoteDecoder**: code to translate HATEOAS JSON coming from the DB Quote Service into our Quote and MarketSummary domain objects.

* **DBQuoteRepository**: Feign repository that fronts calls to the DB Quote microservice.

* **QuoteRepositoryConnectionCreator, QuoteWebServiceInfoCreator, WebServiceInfo**: get rid of these, no longer needed now that we are using Eureka!

* **RealTimeQuoteDecoder, RealTimeQuoteRepository**: Renamed from "QuoteDecoder" and "QuoteRepository" since we now have a bunch of different services and need to differentiate between them. Decoder translates JSON coming from the Real Time (YHOO) Quote Service into our Quote and MarketSummary domain objects. Some cleanups and updates. Repo is a feign repository that fronts calls to the Real Time Quote microservice.

* **ScheduledUpdatable**: Spring task scheduler uses this (see above).

* **HoldingAggregateRepositoryImpl, PortfolioSummaryRepositoryImpl, TradingServiceImpl**: now that we have multiple services registered that implement the QuoteService interface we need to differentiate them via "Qualifiers."

* **DBQuoteService**: the implementation code used by the monolith to interact with the DBQuote microservice. Note the use of DiscoveryClient to look up the proper service at startup, and the use of the @HystrixCommand annotation to register fallback methods if service calls fail. The mechanism used to look up the service would be a lot simpler if we could use Spring-Cloud. Maybe in the future?

* **FallBackQuoteService**: This is the "service of last resort that can't fail" that provides random quote values. Probably not useful in a real application, but kind of fun here.

* **QuoteServiceImpl**: renamed to "RealTimeQuoteService." See the comments for DBQuoteService, above. Implements "ScheduledUpdatable" so we can call this from a task scheduler. This allows us to update the DB Quote microservice's database with real time values at regular intervals.

* **xml and config changes**: register db and real time quotes services using qualified names so we can keep them straight. remove previous spring-connector configs. Fake out the tests by using the fallback service in place of the real time service for test purposes.

### spring-nanotrader-data/src/main/resources/...
* **eureka.client.properties** Note hard-coded URL for Eureka server. Could not find a way around this. Did I mention that this kind of stuff would not be needed if we were able to use Spring-Cloud?

### everything else...
* Test code, more changes related to qualifying service names (see above), log config cleanups, minor fixes, etc.

## To build
### The microservices
There are two microservices and a simplified Eureka server that need to be deployed and running for this version of SpringTrader. These can be found at the links below. Instructions for building and deploying are included within their respective README files.

[standalone eureka service] (https://github.com/cf-platform-eng/standalone-eureka)

[real-time quote service] (https://github.com/cf-platform-eng/quote-service) : make sure to check out the "part3live" branch!

[db quote service](https://github.com/cf-platform-eng/quote-service) : make sure to check out the "part3db" branch!

Once you get these configured and deployed, "cf apps" should result in something that looks like the following:

```bash
$ cf apps
Getting apps in org your-org / space your-space as you@foo.bar...
OK

name                 requested state   instances   memory   disk   urls   
your-db-quote-service     started           1/1         512M     1G     your-db-quote-service.cfapps.io   
your-live-quote-service   started           1/1         512M     1G     your-live-quote-service.cfapps.io   
your-standalone-eureka    started           1/1         512M     1G     your-standalone-eureka.cfapps.io
```

### Configuring SpringTrader
To build this version of SpringTrader, make sure you are using JDK 7:
```bash
$ java -version
java version "1.7.0_75"
Java(TM) SE Runtime Environment (build 1.7.0_75-b13)
Java HotSpot(TM) 64-Bit Server VM (build 24.75-b04, mixed mode)
```

From a clean directory, check out the part3 branch:
```bash
git clone git@github.com:cf-platform-eng/springtrader-cf.git
cd springtrader-cf
git checkout part3
```

You will need to edit the spring-nanotrader-data/src/main/resources/eureka.client.properties file to set the url of the Eureka server to your specific instance. In the example above, this might look like:

```
eureka.registration.enabled=false
eureka.serviceUrl.default=http://your-standalone-eureka.cfapps.io/eureka/
```

Then, build the app (from the root of the project):
```bash
$ ./gradlew clean build release
```

The app should build, and the tests should pass. For additional details on configuration, please refer to the main branch documentation [here](https://github.com/cf-platform-eng/springtrader-cf/wiki/Getting-Started-Guide).

Finally, edit the config.sh file (in the root of the project) to set the various service names and references. Something like the following:

```
#!/bin/sh

export frontName=your-traderfront
export webName=your-traderweb
export backName=your-traderback
export domain=cfapps.io
export sqlName=your-tradersql
export messagingName=your-tradermessaging
export version=1.0.1.BUILD-SNAPSHOT
export liveQuoteServiceEurekaName=your-live-quote-service
export dbQuoteServiceEurekaName=your-db-quote-service
```

At this point you should be ready to deploy SpringTrader:
```bash
$ ./deployApp.sh
```

The app should deploy and start correctly.

Try the app out. The front end URL can be determined by running the cf apps command and looking at the output:

```bash
$ cf apps
Getting apps in org your-org / space your-space as you@foo.bar...
OK

name                 requested state   instances   memory   disk   urls   
your-db-quote-service   started           1/1         512M     1G     your-db-quote-service.cfapps.io   
your-traderback         started           1/1         1G       1G     your-traderback.cfapps.io   
your-traderfront        started           1/1         1G       1G     your-traderfront.cfapps.io   
your-traderweb          started           1/1         1G       1G     your-traderweb.cfapps.io   
your-live-quote-service started           1/1         512M     1G     your-live-quote-service.cfapps.io   
your-standalone-eureka  started           1/1         512M     1G     your-standalone-eureka.cfapps.io   
```

In the above case, the UI is running under the your-traderweb url (your-traderweb.cfapps.io), so we would open this in a browser.

Full documentation on using the application can be found in the README files on the master branch [here](https://github.com/cf-platform-eng/springtrader-cf).
