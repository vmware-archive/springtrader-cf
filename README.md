SpringTrader Part 1 (baseline)
=============================

This branch is the baseline for the migration case study covered in the migration blog. For the "real" application documentation, please check out the master branch and refer to its README.md

To build, from the root director of the project run:

* ./gradlew build release

The project as it currently stands will not build under JDK8. It might build using JDK7, however. The tests may or may not pass. To skip the tests add the -x test flag:

* ./gradlew build release -x test

To deploy the project, edit the deployApp.sh as per the instructions in the readme file on the master branch [here](https://github.com/cf-platform-eng/springtrader-cf/wiki/Getting-Started-Guide).

Depending on your build environment, you may need to edit the "gcf" commands in the deployApp.sh script to make them "cf" (replace "gcf" with "cf").

Once on PCF, things get worse, with errors such as:  

org.springframework.core.NestedIOException: ASM ClassReader failed to parse class file - probably due to a new Java class file version that isn't supported yet: class path resource [java/io/Serializable.class];

showing up in the logs.

This is our starting point...
