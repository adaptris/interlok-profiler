# interlok-profiler

[![GitHub tag](https://img.shields.io/github/tag/adaptris/interlok-profiler.svg)](https://github.com/adaptris/interlok-profiler/tags)
[![license](https://img.shields.io/github/license/adaptris/interlok-profiler.svg)](https://github.com/adaptris/interlok-profiler/blob/develop/LICENSE)
[![Actions Status](https://github.com/adaptris/interlok-profiler/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/adaptris/interlok-profiler/actions)
[![codecov](https://codecov.io/gh/adaptris/interlok-profiler/branch/develop/graph/badge.svg)](https://codecov.io/gh/adaptris/interlok-profiler)
[![CodeQL](https://github.com/adaptris/interlok-profiler/workflows/CodeQL/badge.svg)](https://github.com/adaptris/interlok-profiler/security/code-scanning)
[![Known Vulnerabilities](https://snyk.io/test/github/adaptris/interlok-profiler/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/adaptris/interlok-profiler?targetFile=build.gradle)
[![Closed PRs](https://img.shields.io/github/issues-pr-closed/adaptris/interlok-profiler)](https://github.com/adaptris/interlok-profiler/pulls?q=is%3Apr+is%3Aclosed)

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

- [aspectjwearver.jar - 1.9.19](https://mvnrepository.com/artifact/org.aspectj/aspectjweaver/1.9.19)
- [aspectjrt.jar - 1.9.5](https://mvnrepository.com/artifact/org.aspectj/aspectjrt/1.9.19)
- [aspectjtools.jar - 1.9.19](aspectjtools.jar%20-%201.9.19)
