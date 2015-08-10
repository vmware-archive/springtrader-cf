# SpringTrader Part 1 (complete)
This is the readme for the branch representing the "solution" to Part 1 for the migration case study covered in the migration blog. For the "real" application documentation, please check out the master branch and refer to its README.md

To see what was done to the project to get us to a stable build, consult the diff [here](https://github.com/cf-platform-eng/springtrader-cf/compare/baseline...part1).

## The Changes
###README.md
* Edits related to the creation of this very README file, including these very words.

### build.gradle
* Changed the source and target flags from JDK6 to JDK7. This is really a formality and does not fix our problems, but does force users off of JDK 6.
* The larger issue is the vast complexity in the dependency lists for the project, as seen in this build file (looking at the whole file, versus just the diff). As discussed in the blog, we'll need to tackle this later.

### deleteDeployment.sh and deployApp.sh
* Replace "gcf" with "cf" (the Cloud Foundry command line interface). No need to make things complicated for users by requiring gcf when cf will work just fine.
* Gave the services more descriptive names.
* Added some directives to enable the app run on current buildpacks (the set-env entries). This is because the app is still not JDK 8 compatible and the buildpacks are assuming JDK 8 (but can be configured to use JDK 7 with these flags).

So, what was needed were the added flags, and that was it? 

Well, thinking about the big picture, we chose this approach versus re-writing the build to chase down JDK 8 incompatibilities and/or restructuring the entire dependency stack to make use of the latest libraries *for now*. We'll get to these larger changes later.

To build, make sure you are using JDK 7:
```bash
$ java -version
java version "1.7.0_75"
Java(TM) SE Runtime Environment (build 1.7.0_75-b13)
Java HotSpot(TM) 64-Bit Server VM (build 24.75-b04, mixed mode)
```
If you still have the project set up as per the baseline README, go to that directory.

If not refer to the instructions in the baseline branch to get you started ([here](https://github.com/cf-platform-eng/springtrader-cf/blob/baseline/README.md)).

check out the part1 branch:
```bash
git checkout part1
```

build the app:
```bash
$ ./gradlew clean build release
```
The app should build, and the tests should pass.

Edit the deployApp.sh file as described in the master branch documentation [here]((https://github.com/cf-platform-eng/springtrader-cf/wiki/Getting-Started-Guide).

deploy the app:
```bash
$ ./deployApp.sh
```

The app should deploy and start correctly.

Try the app out. The front end URL can be determined by running the cf apps command and looking at the output:

```bash
$ cf apps
Getting apps in org foo / space bar as foo@bar.bazz...
OK

name                      requested state   instances   memory   disk   urls   
mytraderback              started           1/1         1G       1G     mytraderback.cfapps.io   
mytraderfront             started           1/1         1G       1G     mytraderfront.cfapps.io   
mytraderweb               started           1/1         1G       1G     mytraderweb.cfapps.io   
```

In the above case, the UI is running under the mytraderweb url (mytraderweb.cfapps.io), so we would open this in the browser.

Full documentation on using the application can be found in the README files on the master branch [here](https://github.com/cf-platform-eng/springtrader-cf).
