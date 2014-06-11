ADP-Profiler.

The source code contains a client and server component.

..............
Pre-requisites
..............

Use the build script to do an "ant clean jar".
This will build a jar file that should be copied into the lib directory of your Adapter installation.

Copy the adp-profiler.properties file into your "config" directory of your Adapter installation.

Copy the jar files (found in the lib directory) called "aspectjrt.jar" and "aspectWeaver.jar" into the lib directory of your Adapter installation.


..............
The Server
..............
The server component should be started before the instrumented Adapter.
To start the server component simply (modify as needed) execute the script (found in ./Scripts/) StartProfiler.bat
This script should be copied into the root of your V3 adapter installation and run from there.


..............
The Client
..............
You will need to start an Adapter instance up with a java agent.  A script has been provided to demonstrate.
Copy the script named "StartWithProfiler.bat" into the root of your Adapter installation and start the Adapter with this script.
This script will force the instrumentation of the Adapter before launching it.