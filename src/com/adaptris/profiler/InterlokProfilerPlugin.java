package com.adaptris.profiler;

import com.adaptris.core.ComponentLifecycle;
import com.adaptris.profiler.aspects.AdapterAspect;

/**
 * Interface defining additional lifecycle steps for profiler implementations that require it.
 * <p>
 * These steps will be called during the corresponding {@link AdapterAspect} methods; and allow you to have additional behaviour
 * that happens before the corresponding {@link ComponentLifecycle} methods.
 * </p>
 * 
 */
public interface InterlokProfilerPlugin {

  /**
   * Corresponds to {@link ComponentLifecycle#start()}.
   * 
   */
  public void start(Object object);
  
  /**
   * Corresponds to {@link ComponentLifecycle#stop()}.
   * 
   */
  public void stop(Object object);

  /**
   * Corresponds to {@link ComponentLifecycle#init()}.
   * 
   */
  public void init(Object object);

  /**
   * Corresponds to {@link ComponentLifecycle#close()}.
   * 
   */
  public void close(Object object);


}
