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
cf unbind-service $webName $appDweb
cf unbind-service $frontName $appDAPI
cf unbind-service $backName $appDDB
cf delete -f $frontName
cf delete -f $webName
cf delete -f $backName
cf delete-service -f $frontName
cf delete-service -f $sqlName
cf delete-service -f $messagingName
date
