package com.adaptris.profiler.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.profiler.client.PluginFactory;

@Aspect
public class AdapterAspect extends BaseAspect {

  protected transient Logger log = LoggerFactory.getLogger(this.getClass());
  
  @Before("execution(* com.adaptris.core.Adapter.start())")
  public synchronized void afterAdapterStart(JoinPoint jp) throws Exception {
    log.trace("Profiler : Before Adapter Start");
    PluginFactory.getInstance().getPlugin().start();
  }
  
  @Before("execution(* com.adaptris.core.Adapter.stop())")
  public synchronized void beforeAdapterStop(JoinPoint jp) throws Exception {
    log.trace("Profiler : Before Adapter Stop");
    PluginFactory.getInstance().getPlugin().stop();
  }
}
