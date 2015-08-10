# SpringTrader Part 1 (baseline)

This is the readme for the branch representing the baseline for the migration case study covered in the migration blog. For the "real" application documentation, please check out the master branch and refer to its README.md

To recreate where things stand at the baseline, clone this repository and then check out the baseline branch:
```bash
$ git clone git@github.com:cf-platform-eng/springtrader-cf.git
$ cd springtrader-cf/
$ git checkout baseline
```

Make sure you are using JDK 6
```bash
$ java -version
java version "1.6.0_65"
Java(TM) SE Runtime Environment (build 1.6.0_65-b14-466.1-11M4716)
Java HotSpot(TM) 64-Bit Server VM (build 20.65-b04-466.1, mixed mode)
```

To build, from the root director of the project run:

```bash
$ ./gradlew clean build release
```

The project should build correcty under JDK 6, and the tests should pass.

To deploy the project, edit the deployApp.sh as per the instructions in the readme file on the master branch [here](https://github.com/cf-platform-eng/springtrader-cf/wiki/Getting-Started-Guide).

Depending on your build environment, you may need to edit the "gcf" commands in the deployApp.sh script to make them "cf" (replace "gcf" with "cf").

to deploy to PCF, run the edited deployApp.sh script
```bash
$ ./deployApp.sh
```

Alas, the deploy will fail. Looking at the logs will show something like the following:  
```
org.springframework.core.NestedIOException: ASM ClassReader failed to parse class file - probably due to a new Java class file version that isn't supported yet: class path resource [java/io/Serializable.class];
```
A Class from the future? Whoa!

Maybe it's our build environment? Feel free to try using JDK 7 or JDK 8.

Using JDK 7, the app should build and the tests should pass. Deployment will fail as per above.

Using JDK 8, tests fail during the build, with the same error we see on the server at runtime.

Against best practices and common sense, let's try to build and deploy using JDK 8, while skipping those pain-in-the-neck tests.
```bash
./gradlew clean build release -x test
```
The build will fail with various errors that could *probably* be chased down and fixed... maybe...

This is our starting point.
