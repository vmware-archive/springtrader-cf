#!/bin/sh

source config.sh

date

echo Creating service instances
cf create-service cloudamqp lemur ${messagingName}

echo Deploying front end services tier
cf push -b java_buildpack -p dist/spring-nanotrader-services-${version}.war -m 1G -t 180 -d ${domain} -n ${frontName} --no-start ${frontName}
cf bind-service ${frontName} ${messagingName}
cf set-env ${frontName} LIVE_QUOTE_SERVICE_NAME ${liveQuoteServiceEurekaName}
cf set-env ${frontName} DB_QUOTE_SERVICE_NAME ${dbQuoteServiceEurekaName}
cf set-env ${frontName} ACCOUNT_SERVICE_NAME ${accountServiceEurekaName}
cf set-env ${frontName} ORDER_SERVICE_NAME ${orderServiceEurekaName}
cf push -b java_buildpack -p dist/spring-nanotrader-services-${version}.war -m 1G -t 180 -d ${domain} -n ${frontName} ${frontName}

echo Making this app available as a service instance
cf cups ${frontName} -p '{"uri":"http://'${frontName}'.'${domain}'/api/"}'

date