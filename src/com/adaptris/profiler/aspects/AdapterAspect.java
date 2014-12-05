package com.adaptris.profiler.aspects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.adaptris.profiler.client.PluginFactory;

@Aspect
public class AdapterAspect {

  protected transient Log log = LogFactory.getLog(this.getClass().getName());
  
  @Before("execution(* com.adaptris.core.Adapter.start())")
  public synchronized void afterAdapterStart(JoinPoint jp) throws Exception {
    log.trace("Profiler : Before Adapter Start");
    PluginFactory.getInstance().getPlugin().start();
  }
  
  @Before("call(void stop()) && within(com.adaptris.core.Adapter)")
  public synchronized void beforeAdapterStop(JoinPoint jp) throws Exception {
    log.trace("Profiler : Before Adapter Stop");
    PluginFactory.getInstance().getPlugin().stop();
  }
}
