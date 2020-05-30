# RangeFinder

Code to find all the ranges.

# Prerequisite

There is need to install redis in order to compute unbounded ranges.
There is a setup.sh script, please run it before running the code
* ./setup.sh (Only works on mac)

# Build
This is a maven project, so all basic maven commands
would run
1) Compile via below command
   * mvn compile
2) Execute via below command
   * mvn exec:java -Dexec.mainClass=main.java.rangeFinder.RangeFinder
