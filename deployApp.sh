#!/bin/sh

frontName=stfront
webName=stweb
backName=stback
domain=cedemo.fe.pivotal.io
sqlName=tradersql
messagingName=tradermessaging
appDweb=app-dynamics-ST-web
appDAPI=app-dynamics-ST-rest
appDDB=app-dynamics-ST-asynch

date

echo creating services
cf create-service p-rabbitmq standard tradermessaging
cf create-service p-mysql 100mb-dev tradersql

echo Deploying front end services tier
cf push -p dist/spring-nanotrader-services-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $frontName -b https://github.com/cloudfoundry/java-buildpack.git --no-start $frontName
cf bind-service $frontName $sqlName
cf bind-service $frontName $messagingName
cf bind-service $frontName $appDAPI
cf set-env $frontName JBP_CONFIG_OPEN_JDK_JRE '[version: 1.7.0_+]'
cf set-env $frontName JBP_CONFIG_APP_DYNAMICS_AGENT '[version: 4.0.1_+]'
cf push -p dist/spring-nanotrader-services-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $frontName -b https://github.com/cloudfoundry/java-buildpack.git $frontName

echo Making this app available as a service instance
cf cups $frontName -p '{"uri":"http://'$frontName'.'$domain'/api/"}'

echo Deploying the web tier
cf push -p dist/spring-nanotrader-web-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $webName -b https://github.com/cloudfoundry/java-buildpack.git --no-start $webName
cf bind-service $webName $frontName
cf bind-service $webName $appDweb
cf set-env $webName JBP_CONFIG_OPEN_JDK_JRE '[version: 1.7.0_+]'
cf push -p dist/spring-nanotrader-web-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $webName -b https://github.com/cloudfoundry/java-buildpack.git $webName

echo Deploying back end services tier
cf push -p dist/spring-nanotrader-asynch-services-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $backName -b https://github.com/cloudfoundry/java-buildpack.git --no-start $backName
cf bind-service $backName $sqlName
cf bind-service $backName $messagingName
cf bind-service $backName $appDDB
cf set-env $backName JBP_CONFIG_OPEN_JDK_JRE '[version: 1.7.0_+]'
cf push -p dist/spring-nanotrader-asynch-services-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $backName -b https://github.com/cloudfoundry/java-buildpack.git $backName

date
