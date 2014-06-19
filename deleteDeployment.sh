#!/bin/sh

frontName=traderfront
webName=traderweb
backName=traderback
domain=cfapps.io
sqlName=stsql
messagingName=stmessaging

date
gcf delete -f $frontName
gcf delete -f $webName
gcf delete -f $backName
gcf delete-service -f $frontName
gcf delete-service -f $sqlName
gcf delete-service -f $messagingName
date