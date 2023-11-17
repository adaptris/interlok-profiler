/** Base package for AspectJ profiling of the adapter.

	<p>Using the profiler will require {@code aspectjweaver} to be
		listed as a javaagent when starting the JVM. The recommendation is to
		not use the bundled wrapper executables, and to roll your own script
		that provides the correct startup parameters to your JVM. The aspects
		themselves are stored in {@code META-INF/profiler-aop.xml} which means
		that you need to set the appropriate aspectj system property to enable
		the aspects</p>

	<p>
		By default, the profiler does not do anything other than inject aspects
		at the appropriate places for Workflow/Service/Producer. In order to
		get meaningful information you will need a concrete implementation of
		link com.adaptris.profiler.client.PluginFactory which needs to be
		specified in the file {@code interlok-profiler.properties} (this should be
		available on the classpath).
		<code>
			<pre>
com.adaptris.profiler.plugin.factory=my.implementation.of.com.adaptris.profiler.client.PluginFactory
		</pre>
		</code>
	</p>
	<p>
		You should switch to using SimpleBootstrap to start the adapter and build the classpath
		manually in your script.
		<code>
			<pre>
COREJARS=`ls -1 lib/*.jar`
for jar in $COREJARS
do
  CLASSPATH=$CLASSPATH:$jar
done

LOCALCLASSPATH=$CLASSPATH
export JAVA_HOME=/opt/java/jdk1.7
JAVA_ARGS="-server -XX:+UseConcMarkSweepGC -XX:+UseParNewGC"

$JAVA_HOME/bin/java -javaagent:./lib/aspectjweaver.jar \
  -Dorg.aspectj.weaver.loadtime.configuration=META-INF/profiler-aop.xml -cp "$LOCALCLASSPATH" $JAVA_ARGS \
  com.adaptris.core.management.SimpleBootstrap bootstap.properties
  </pre>
		</code>
	</p>
*/
package com.adaptris.profiler;