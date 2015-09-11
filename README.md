# SpringTrader Part 2
This is the readme for the branch representing the "solution" to Part 2 for the *Refactoring a Monolith into a Cloud-Native Application* blog. For the "real" application documentation, please check out the master branch and refer to its README.md

To see what was done in part 2, consult the diff: [here](https://github.com/cf-platform-eng/springtrader-cf/compare/part1...part2).

## The Changes
###README.md
* Edits related to the creation of this very README file, including these very words.

### build.gradle
* Some minor updates needed to get the build running using updated gradle wrapper (see below).

* Upgraded Spring Cloud to version 1.2.0 and added Feign and other JSON  libraries to support calls to the new quote-service.

### deleteDeployment.sh and deployApp.sh
* Added creation/deletion and binding for new user provided service for external quote-service.

### gradle changes (gradle-wrapper.jar, gradle-wrapper.properties, gradlew, gradlew.bat)
* Updates to the gradle build wrapper to bring it up to date. These were generated using the "gradle wrapper" command and can be safely ignored.

### the classes added to spring-nanotrader-data/main/java/org/springframework/nanotrader/data/cloud

* **QuoteRepository**:  replacement for the JPA version of QuoteRepository. This is using Feign and is calling out to the new quote-service microservice via the @RequestLine annotations. Feign does all of the heavy lifting so we don't need to code any HTTP interactions or JSON marshalling directly.

* **QuoteRepositoryConnectionCreator**: makes use of Spring Cloud Connector functionality to set up the Feign repository based on the URL configured within the quote-service user provided service (defined in the deployApp.sh script).

* **QuoteWebServiceInfoCreator** and **WebServiceInfo**: boilerplate needed to make use of Spring Cloud Connectors.

* **RealTimeQuoteDecoder**: Used to marshall JSON into our Quote and MarketSummary domain objects. This is where we handle some of the semantic differences between SpringTrader domain and our new quote-service domain. We can isolate 3rd party API  changes within this class.

### changes to spring-nanotrader-data/main/java/org/springframework/nanotrader/data/domain/...
* **MarketSummary**: we now get more information from the API so we don't need to calculate certain values.

* **Order**: detatch the Quote table from the Order table, and just store the quoteid (the symbol) instead.

* **Quote**: Quote is no longer a table, it's just a DTO. Keep the same structure to minimize effects on the rest of SpringTrader though, but changing the "id" to be a string, and set it to the Quote's symbol. Also implemented hash() and equals() based on symbol so we can safely add Quotes to collections.

* **HoldingAggregateRepositoryImpl**, **HoldingRepository**: get rid of embedded SQL (an anti-pattern). Holding domain objects should not need to understand persistance semantics for Quote domain objects.

* **MarketSummaryRepository**, **MarketSummaryRepositoryImpl**: more embedded SQL, but these classes are no longer needed. The functionality has been taken over by the new quote-service, so we can delete.

* **PortfolioSummaryRepositoryImpl**: replace embedded SQL with QuoteService calls.

* **QuoteRepository**: delete the JPA repository for Quotes. Replaced with the Feign repository in the cloud package.

* **QuoteService**: This is the interface the rest of SpringTrader will use to get to Quotes. The changes in the interface were to consolidate Quote functionality that was previously scattered in other classes. These were discovered as we went through the code to clean up QuoteRepository leakages.

* **QuoteServiceImpl**: changed to speak to the new external service. Mostly the implementation just defers to QuoteRepository. We continue to need  the worrying findBySymbolIn(Set<String> symbols) method since this is called throughout the rest of SpringTrader (as is the findAll() method). Ideally we would refactor this since returning large colllections via remote calls and then throwing most of the results away is not a good idea. But other existing code in the app relies on this functionality: maybe we can fix this in future interations?

* **TradingServiceImpl**: get rid of references to QuoteRepository, defer to QuoteService for market summary processing.

### spring-nanotrader-data/src/main/resources/...
* addition of files as part of the Spring Cloud Connector configuration.

* application.xml: get rid of Quote database table-loads on startup (no longer needed, no more Quote table). Register quoteServiceImpl as a bean, and tell Spring how to initialize it in the cloud (under the "default" profile) and when running test cases (under the "test" profile).

* other misc. configruation changes and file cleanups.

### spring-nanotrader-data/src/test/...
* misc test updates, replace mocked Quote data with calls to the actual service.

### spring-nanotrader-service-support/src/main/java/org/springframework/nanotrader/service/domain/...
* for some reason SpringTrader has classes (mostly) duplicated from the spring-nanotrader-data/main/java/org/springframework/nanotrader/data/domain package, but with with some changes. These are used to communicate with the UI layer and [dozer](http://dozer.sourceforge.net/) converts them back and forth. We need to update these classes as per what was done in the other domain package. Would be nice to not need these at all, maybe remediate at a future date?

* other changes were made to replace QuoteRepository with QuoteService references, and update tests.

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
