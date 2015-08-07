SpringTrader Part 1 (complete)
=============================

The changes:

build.gradle
Changed the source and target compatibilities from JDK6 to JDK7 (really a formallity, unfortunatley this does not really fix our problems for us).

deleteDeployment.sh and deployApp.sh
replace gcf with plain old cf (the vanilla Cloud Foundry command line interface).
gave the services more descriptive names.
added some directives to help the app run on current buildpacks (the set-env entries). This is because the app is still not JDK8 compatible.
