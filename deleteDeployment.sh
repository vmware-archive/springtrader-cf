#!/bin/sh

frontName=traderfront
webName=traderweb
backName=traderback
domain=cfapps.io
sqlName=tradersql
messagingName=tradermessaging
quoteName=quoteService

date

cf delete -f $frontName
cf delete -f $webName
cf delete -f $backName
cf delete-service -f $frontName
cf delete-service -f $sqlName
cf delete-service -f $messagingName
cf delete-service -f $quoteName
cf delete-route $domain -f -n $frontName
cf delete-route $domain -f -n $webName
cf delete-route $domain -f -n $backName

date
