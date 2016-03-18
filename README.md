# SpringTrader Part 4
This is the readme for the branch representing the "solution" to Part 4 for the *Refactoring a Monolith into a Cloud-Native Application* blog. For the "real" application documentation please check out the master branch and refer to its README.md

To see what was done in part 4, consult the diff: [here](https://github.com/cf-platform-eng/springtrader-cf/compare/part3...part4).

## The Changes
### README.md
* Edits related to the creation of this very README file, including these very words.

### build.gradle
* Remove JPA and other database dependencies. Remove groovy processing for tools module. Simplify build.

### database-scripts
* obsolesced.

### config.sh, deleteDeployment.sh and deployApp.sh
* Add support for new Order and Account services, remove entries related to data module JPA.

### changes to spring-nanotrader-asynch-services/...

* **xml and other config files**:  Remove JPA configurations. Add entries to support new Order and Account services.

* **tests and test configurations**: as above, remove JPA and add new services support.

### spring-nanotrader-chaos/...
* obsolesced.

### changes to spring-nanotrader-data/...
* **AccountDecoder, AccountEncoder, OrderDecoder, OrderEncoder**: code to translate JSON coming from the external microservices into our domain objects.

* **AccountprofileRepository, AccountRepository, HoldingRepository, OrderRepository**: Morph these from JPA repositories to Feign repositories that front calls to the external microservices. Move into the cloud package with the other repos.

* **Accountprofile, Account, Holding, Ordery**: Morph these from JPA entities into simple POJO domain objects.

* **CloudConfiguration**: support for the new microservices.

* **JsonUtils**: shared json processing code.

* **HoldingAggregateRepositoryImpl, PortfolioSummaryRepositoryImpl**: business code moved to HoldingServiceImpl, rest obsolesced.

* **AccountprofileService**: clean up and rationalize interfaces and remove methods not used by rest of SpringTrader.

* **AccountprofileServiceImpl, AccountServiceImpl, OrderServiceImpl**: update implementations to make use of cleaned up service interfaces. Discover related microservices via Eureka upon invocation.

* **TradingService, TradingServiceImpl**: remove no-value-add "pass through" methods and have dependent classes use underlying service interfaces instead. Move business code to appropriate service implementations.

* **PUPostProcessor, persistence.xml, applicationContext-jpa.xml**: obsolesced.

* **test classes**: some new tests for the new services, plus changes in existing tests as needed to support interface changes. Fallback services created for Account and Order to support mocks.

### spring-nanotrader-data/src/main/resources/...
* **eureka.client.properties** Note hard-coded URL for Eureka server. Could not find a way around this. Did I mention that this kind of stuff would not be needed if we were able to use Spring-Cloud?

### tools/...
* obsolesced.

### everything else...
* Changes to support modifications to service interfaces and minor adjustments to the domain classes, remove JPA references

## To build
### The microservices
There are four microservices and a simplified Eureka server that need to be deployed and running for this version of SpringTrader. These can be found at the links below. Instructions for building and deploying are included within their respective README files.

[standalone eureka service](https://github.com/cf-platform-eng/standalone-eureka)

[real-time quote service](https://github.com/cf-platform-eng/quote-service) : make sure to check out the "part3live" branch!

[db quote service](https://github.com/cf-platform-eng/quote-service) : make sure to check out the "part3db" branch!

[account service](https://github.com/cf-platform-eng/account-service) : make sure to check out the "part4" branch!

[order service](https://github.com/cf-platform-eng/order-service) : make sure to check out the "part4" branch!

Once you get these configured and deployed, "cf apps" should result in something that looks like the following:

```bash
$ cf apps
Getting apps in org your-org / space your-space as you@foo.bar...
OK

name                 requested state   instances   memory   disk   urls
your-account-service      started           1/1         512M     1G     your-account-service.cfapps.io
your-db-quote-service     started           1/1         512M     1G     your-db-quote-service.cfapps.io
your-live-quote-service   started           1/1         512M     1G     your-live-quote-service.cfapps.io
your-order-service        started           1/1         512M     1G     your-order-service.cfapps.io
your-standalone-eureka    started           1/1         512M     1G     your-standalone-eureka.cfapps.io
```

### Configuring SpringTrader
To build this version of SpringTrader, make sure you are using JDK 8. From a clean directory, check out the part4 branch:
```bash
git clone git@github.com:cf-platform-eng/springtrader-cf.git
cd springtrader-cf
git checkout part4
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
export messagingName=your-tradermessaging
export version=1.0.1.BUILD-SNAPSHOT
export liveQuoteServiceEurekaName=your-live-quote-service
export dbQuoteServiceEurekaName=your-db-quote-service
export accountServiceEurekaName=your-account-service
export orderServiceEurekaName=your-order-service
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
your-traderweb          started           1/1         1G       1G     your-traderweb.cfapps.io
...
```

In the above case, the UI is running under the your-traderweb url (your-traderweb.cfapps.io), so we would open this in a browser.

Once the application and services are running, individual services can be controlled using simple cf commands:
```
$ cf stop your-live-quote-service
```
App will fail over to using the your-db-quote-service. UI will stop showing updates to Quotes.
```
$ cf stop your-db-quote-service
```
App will fail over to the internal "foo" service. UI will reflect this.
```
$ cf start your-live-quote-service
```
App will start showing live quotes again.

Full documentation on using the application can be found in the README files on the master branch [here](https://github.com/cf-platform-eng/springtrader-cf).