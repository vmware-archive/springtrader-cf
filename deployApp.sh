#!/bin/sh

frontName=traderfront
sqlName=tradersql
messagingName=tradermessaging
domain=cfapps.io

date

echo Creating service instances
cf create-service cleardb spark $sqlName
cf create-service cloudamqp lemur $messagingName

echo Deploying front end services tier
cf push -f frontManifest.yml

echo Making this app available as a service instance
cf cups $frontName -p '{"uri":"http://'$frontName'.'$domain'/api/"}'

echo Deploying the web tier
cf push -f webManifest.yml

echo Deploying back end services tier
cf push -f backManifest.yml

date
