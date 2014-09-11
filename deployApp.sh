#!/bin/sh

export GRADLE_OPTS='-Xmx1024m -Xms256m -XX:MaxPermSize=512m'

frontName=traderfront-${random-word}
webName=traderweb-${random-word}
backName=traderback-${random-word}
domain=cfapps.io
sqlName=tradersql
messagingName=tradermessaging

date

echo Deploying front end services tier
cf push -p dist/spring-nanotrader-services-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $frontName --no-start $frontName
cf bind-service $frontName $sqlName
cf bind-service $frontName $messagingName
cf push -p dist/spring-nanotrader-services-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $frontName $frontName

echo Making this app available as a service instance
cf cups $frontName -p '{"uri":"http://'$frontName'.'$domain'/api/"}'

echo Deploying the web tier
cf push -p dist/spring-nanotrader-web-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $webName --no-start $webName
cf bind-service $webName $frontName
cf push -p dist/spring-nanotrader-web-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $webName $webName

echo Deploying back end services tier
cf push -p dist/spring-nanotrader-asynch-services-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $backName --no-start $backName
cf bind-service $backName $sqlName
cf bind-service $backName $messagingName
cf push -p dist/spring-nanotrader-asynch-services-1.0.1.BUILD-SNAPSHOT.war -m 1G -t 180 -d $domain -n $backName $backName

date