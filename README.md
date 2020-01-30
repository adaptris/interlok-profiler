# interlok-profiler [![GitHub tag](https://img.shields.io/github/tag/adaptris/interlok-profiler.svg)](https://github.com/adaptris/interlok-profiler/tags) [![Build Status](https://travis-ci.org/adaptris/interlok-profiler.svg?branch=develop)](https://travis-ci.org/adaptris/interlok-profiler) [![codecov](https://codecov.io/gh/adaptris/interlok-profiler/branch/develop/graph/badge.svg)](https://codecov.io/gh/adaptris/interlok-profiler) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/a04aaca3525a4c9083e15be97e99baeb)](https://www.codacy.com/app/adaptris/interlok-profiler?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=adaptris/interlok-profiler&amp;utm_campaign=Badge_Grade)

The source code contains a client and server component.

## Pre-requisites


Use the build script to do an `gradle clean jar`.

This will build a jar file that should be copied into the lib directory of your Interlok installation.

Copy the `interlok-profiler.properties` file into your "config" directory of your Interlok installation.

Copy the jar files (found in this projects lib directory) into the lib directory of your Interlok installation.



## The Client

You will need to start an Interlok instance up with a java agent.  A script has been provided to demonstrate (paths will need to be modified in the script).
Copy the script named "StartWithProfiler.bat" into the root of your Interlok installation and start the instance with this script.
This script will force the instrumentation of Interlok before launching it.

## Dependant jar files

- [aspectjwearver.jar - 1.9.5](https://mvnrepository.com/artifact/org.aspectj/aspectjweaver/1.9.5)
- [aspectjrt.jar - 1.9.5](https://mvnrepository.com/artifact/org.aspectj/aspectjrt/1.9.5)
- [aspectjtools.jar - 1.9.5](aspectjtools.jar%20-%201.9.5)
