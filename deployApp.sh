#!/bin/sh

source config.sh

date

echo Creating service instances
cf create-service cloudamqp lemur ${messagingName}

echo Deploying front end services tier
cf push -b java_buildpack -p dist/spring-nanotrader-services-${version}.war -m 1G -t 180 -d ${domain} -n ${frontName} --no-start ${frontName}
cf bind-service ${frontName} ${messagingName}
cf set-env ${frontName} JBP_CONFIG_OPEN_JDK_JRE '[jre: {version: 1.7.0_+}]'
cf set-env ${frontName} JBP_CONFIG_TOMCAT '[tomcat: {version: 7.0.+}]'
cf set-env ${frontName} LIVE_QUOTE_SERVICE_NAME ${liveQuoteServiceEurekaName}
cf set-env ${frontName} DB_QUOTE_SERVICE_NAME ${dbQuoteServiceEurekaName}
cf set-env ${frontName} ACCOUNT_SERVICE_NAME ${accountServiceEurekaName}
cf set-env ${frontName} ORDER_SERVICE_NAME ${orderServiceEurekaName}
cf push -b java_buildpack -p dist/spring-nanotrader-services-${version}.war -m 1G -t 180 -d ${domain} -n ${frontName} ${frontName}

echo Making this app available as a service instance
cf cups ${frontName} -p '{"uri":"http://'${frontName}'.'${domain}'/api/"}'

echo Deploying the web tier
cf push -b java_buildpack -p dist/spring-nanotrader-web-${version}.war -m 1G -t 180 -d ${domain} -n ${webName} --no-start ${webName}
cf set-env ${webName} JBP_CONFIG_OPEN_JDK_JRE '[jre: {version: 1.7.0_+}]'
cf set-env ${webName} JBP_CONFIG_TOMCAT '[tomcat: {version: 7.0.+}]'
cf bind-service ${webName} ${frontName}
cf push -b java_buildpack -p dist/spring-nanotrader-web-${version}.war -m 1G -t 180 -d ${domain} -n ${webName} ${webName}

echo Deploying back end services tier
cf push -b java_buildpack -p dist/spring-nanotrader-asynch-services-${version}.war -m 1G -t 180 -d ${domain} -n ${backName} --no-start ${backName}
cf set-env ${backName} JBP_CONFIG_OPEN_JDK_JRE '[jre: {version: 1.7.0_+}]'
cf set-env ${backName} JBP_CONFIG_TOMCAT '[tomcat: {version: 7.0.+}]'
cf set-env ${backName} LIVE_QUOTE_SERVICE_NAME ${liveQuoteServiceEurekaName}
cf set-env ${backName} DB_QUOTE_SERVICE_NAME ${dbQuoteServiceEurekaName}
cf set-env ${backName} ACCOUNT_SERVICE_NAME ${accountServiceEurekaName}
cf set-env ${backName} ORDER_SERVICE_NAME ${orderServiceEurekaName}
cf bind-service ${backName} ${messagingName}
cf push -b java_buildpack -p dist/spring-nanotrader-asynch-services-${version}.war -m 1G -t 180 -d ${domain} -n ${backName} ${backName}

date
