SpringTrader on Cloud Foundry
=============================

This repository holds the SpringTrader application, slightly modified from the [original](https://github.com/vFabric/springtrader), so that the application component war files are deployed to Cloud Foundry with bindings to database and AMQP services.  The links below point to the original Application Overview, which, at a high level, continues to apply to this new version, the original Getting Started Guide and a new Getting Started Guide.  Comparing the old and new versions of this document is telling - the former requires the installation of numerous components including an application server, database, AMQP messaging server and more. For deployment to Cloud Foundry, the PaaS provides all of these things and the deployment involves creation of database and AMQP instances (no SW installation required) and deployment of the war files (again, no SW installation); all of this is accomplished with only a few commands. 

* See NEW [Getting Started Guide](https://github.com/cf-platform-eng/springtrader-cf/wiki/Getting-Started-Guide): Start here to deploy to Cloud Foundry.
* See (original) [Application Overview](https://github.com/vFabric/springtrader/wiki/Application-Overview)
* See (original) [Getting Started Guide](https://github.com/vFabric/springtrader/wiki/Getting-Started-Guide)

Known Limitations
=================

The following current limitations will be eliminated shortly (pull requests welcome!!):

* No session state caching. The original project leveraged Gemfire to do session state caching, and did so with a peer to peer Gemfire configuration. Because different instances of the web tier run in independent and isolated containers in Cloud Foundry, the peer to peer protocol cannot be used. We are planning on updating the use of Gemfire for session state caching to use a server (hence services) based configuration.
* Sample data is not yet being loaded.  The current configuration DOES load reference data (prices for different stock tickers), but does not yet load additional sample data.

SpringTrader Overview
=====================

The SpringTrader is a web application that allows users to establish an account and then manage a portfolio of stocks, buying and selling.  The architecture is fairly simple with a front end that includes the web tier talking to a set of HTTP/JSON-based services where stock quotes and portfolios can be viewed, and stock trade orders may be submitted, and a back end that fulfills orders. The communication between the front and back ends is asynchronous with the front end delivering orders to a message queue and the back end consuming from that queue. Both the front end services and the back end also access a shared relational database.  See the [Application Overview]() for the high-level architectue.

The following diagram depicts the SpringTrader application UI.

![Spring Trader Screenshot](https://raw.github.com/vFabric/springtrader/master/wiki/springtrader.png)

SpringTrader uses Gradle to Build
===================================

Either re-clone, or shut down STS after deleting projects and remove all Eclipse metadata files (.project, .classpath, .settings) - or see cleanEclipse below - but it's important to do it the first time.

Commands:

* ./gradlew build release
* ./gradlew cleanEclipse (deletes eclipse metadata files)
* ./gradlew eclipse (created metadata files)
* ./gradlew install - installs jars/poms in local maven repo

Import springtrader in Springsource Tool Suite (STS):
---

* Right click in Project Explorer and select Import -> Import...
* Choose Existing Project into Workspace
* Set root directory to full path of springtrader
* Select all Projects and click Finish
* You should see following projects in STS

    `spring-nanotrader-asynch-services`
    `spring-nanotrader-data`
    `spring-nanotrader-services`
    `spring-nanotrader-service-support`
    `spring-nanotrader-web`
    `spring-nanotrader-chaos`

  
* From STS main menu click Projects -> Build Automatically

Deploy springtrader to tc Server in STS:
---

* Right click spring-nanotrader-services and select Run As -> Run On Server
* Choose tc Server Developer Edition for server and click Next
* Drag following projects from Available to Configured column

    `spring-nanotrader-asynch-services`
    `spring-nanotrader-services`
    `spring-nanotrader-web`

* Click Finish
* Wait for server to start and then browse to http://localhost:8080/spring-nanotrader-web

-----
By downloading, installing, or using the Spring Trader software, you (the individual or legal entity) agree to be bound by the terms of the following license agreement:
[License Agreement](https://github.com/vFabric/springtrader/raw/master/license-agreement.pdf)
