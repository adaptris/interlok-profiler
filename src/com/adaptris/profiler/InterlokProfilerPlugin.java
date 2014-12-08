package com.adaptris.profiler;

/**
 * Interface defining additional lifecycle steps for profiler implementations that require it.
 * 
 * 
 */
public interface InterlokProfilerPlugin {

  /**
   * Start the profiler plugin
   * 
   */
  public void start();
  
  /**
   * Stop the plugin.
   * 
   */
  public void stop();

}
