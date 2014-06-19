#!/bin/sh

frontName=traderfront
webName=traderweb
backName=traderback
domain=cfapps.io
sqlName=stsql
messagingName=stmessaging

date

echo Deploying front end services tier
gcf push -p dist/spring-nanotrader-services-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $frontName --no-start $frontName
gcf bind-service $frontName $sqlName
gcf bind-service $frontName $messagingName
gcf push -p dist/spring-nanotrader-services-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $frontName $frontName

echo Making this app available as a service instance
gcf cups $frontName -p '{"uri":"http://'$frontName'.'$domain'/api/"}'

echo Deploying the web tier
gcf push -p dist/spring-nanotrader-web-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $webName --no-start $webName
gcf bind-service $webName $frontName
gcf push -p dist/spring-nanotrader-web-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $webName $webName

echo Deploying back end services tier
gcf push -p dist/spring-nanotrader-asynch-services-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $backName --no-start $backName
gcf bind-service $backName $sqlName
gcf bind-service $backName $messagingName
gcf push -p dist/spring-nanotrader-asynch-services-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $backName $backName

date