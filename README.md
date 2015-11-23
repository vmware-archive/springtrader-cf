# SpringTrader Part 3
This is the readme for the branch representing the "solution" to Part 3 for the *Refactoring a Monolith into a Cloud-Native Application* blog. For the "real" application documentation, please check out the master branch and refer to its README.md

To see what was done in part 3, consult the diff: [here](https://github.com/cf-platform-eng/springtrader-cf/compare/part2...part3).

## The Changes
###README.md
* Edits related to the acreation of this very README file, including these very words.

### build.gradle
* Adding in the Hystrix and Eureka libraries, plus some feign functionality as well.

### config.sh, deleteDeployment.sh and deployApp.sh
* Remove quote service URI and user provided service. Figuring these out is handled by Eureka now.
* Read in env variables to use to set the db and live quote service names. These are set in the manifest and used to look up the services in Eureka. 

### changes to spring-nanotrader-asynch-services/...

* **xml and other config files**:  Tie schema to specific version, needed because we are using a mixture of older and newer spring classes. Also register a task scheduler to update our DB Quote service with the latest info from our Live quote service on a regular basis ( every minute). Plus some log config cleanups.

* **additional tests and test configurations**: added test and related configuration files.

### changes to spring-nanotrader-data/...
* **CloudConfiguration**: annotations for Eureka, and code to register the discover client, hystrix, and the names of the live and db quote services as registered in Eureka (pulled from env). Spring-Cloud would have made this a lot easier.

* **DBQuoteDecoder**: code to translate HATEOAS JSON coming from the DB Quote Service into our Quote and MarketSummary domain objects.

* **DBQuoteRepository**: Feign repository that fronts calls to the DB Quote microservice.

* **QuoteRepositoryConnectionCreator, QuoteWebServiceInfoCreator, WebServiceInfo**: get rid of these, no longer needed now that we are using Eureka!

* **RealTimeQuoteDecoder, RealTimeQuoteRepository**: Renamed from "QuoteDecoder" and "QuoteRepository" since we now have a bunch of different types of these and need to differentiate them. Decoder to translate JSON coming from the Real Time (Yhoo) Quote Service into our Quote and MarketSummary domain objects. Some cleanups and updates. Repo is a feign repository that fronts calls to the Real Time Quote microservice.

* **ScheduledUpdatable**: Spring task scheduler uses this (see above).

* **HoldingAggregateRepositoryImpl, PortfolioSummaryRepositoryImpl, TradingServiceImpl**: now that we have multiple services registered that implement the QuoyeService interface we need to differentiate them via "Qualifiers."

* **DBQuoteService**: the implementation code used by the monolith to interact with the DBQuote service. Note the use of DiscoveryClient to look up the proper service at startup, and the use of the @HystrixCommand annotation to register fallback methods if service calls fail. The mechanism used to look up the service would be a lot simpler if we could use Spring-Cloud. Maybe in the future?

* **FallBackQuoteService**: This is the "service of last resort that can't fail" that provides random quote values. Probably not useful in a real application, but kind of fun here.

* **QuoteServiceImpl**: renamed to "RealTimeQuoteService." See the comments for DBQuoteService, above. Implements "ScheduledUpdatable" som we can call this from a teask scheduler. This allows us to update the DB quote database with real time values at regular intervals. Then, if we lose th real time quote service the db quote service has recent values.

* **xml and config changes**: register db and real time quotes services using qualified names so we can keep them straight. remove previous spring-connector configs. Fake out the tests by using the fallback service in place of the real time service for test purposes.

### spring-nanotrader-data/src/main/resources/...
* **eureka.client.properties** Note hard-coded URL for Eureka server. Could not find a way around this, would not be needed if we were able to use Spring-Cloud. Not a proud moment.

### Everything else...
* Some test code changes, some more changes related to qualifying the name of the service to use (see above), log config cleanups, and other minor fixes, etc.

## To build

You will need to have a quote-service microservice running locally on your machine in order for the unit tests to pass, now that the app is distributed. As we add more services it might be a good idea to have this mocked out so we don't have this dependency, but for now, please see the instructions [here](https://github.com/cf-platform-eng/quote-service/tree/part2) on how to get this running (see the "To run the service locally..." part).

Once quote-service is running locally, to build SpringTrader make sure you are using JDK 7:
```bash
$ java -version
java version "1.7.0_75"
Java(TM) SE Runtime Environment (build 1.7.0_75-b13)
Java HotSpot(TM) 64-Bit Server VM (build 24.75-b04, mixed mode)
```

From a clean directory, check out the part2 branch:
```bash
git clone git@github.com:cf-platform-eng/springtrader-cf.git
cd springtrader-cf
git checkout part2
```

build the app:
```bash
$ ./gradlew clean build release
```

The app should build, and the tests should pass. For additional details on configuration, please refer to the main branch documentation [here](https://github.com/cf-platform-eng/springtrader-cf/wiki/Getting-Started-Guide).

Once SpringTrader is built, you will need to deploy the quote-service to cloud foundry by editing its manifest to give the app a unique name, and then doing a cf push.

Then, edit the SperingTrader deployApp.sh file as described in the master branch documentation [here](https://github.com/cf-platform-eng/springtrader-cf/wiki/Getting-Started-Guide).

Make sure to change the quoteServiceuri entry on line 16 of the deployApp.sh file so that it points to the quote-service you just deployed.

Then, deploy the SpringTrader:
```bash
$ ./deployApp.sh
```

The app should deploy and start correctly.

Try the app out. The front end URL can be determined by running the cf apps command and looking at the output:

```bash
$ cf apps
Getting apps in org foo / space bar as foo@bar.bazz...
OK

name                      requested state   instances   memory   disk   urls   
mytraderback              started           1/1         1G       1G     mytraderback.cfapps.io   
mytraderfront             started           1/1         1G       1G     mytraderfront.cfapps.io   
mytraderweb               started           1/1         1G       1G     mytraderweb.cfapps.io   
```

In the above case, the UI is running under the mytraderweb url (mytraderweb.cfapps.io), so we would open this in a browser.

Full documentation on using the application can be found in the README files on the master branch [here](https://github.com/cf-platform-eng/springtrader-cf).
