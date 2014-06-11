package com.adaptris.profiler.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.adaptris.profiler.client.PluginFactory;

@Aspect
public class AdapterAspect {

  
  @After("call(void start()) && within(com.adaptris.core.Adapter)")
  public synchronized void afterAdapterStart(JoinPoint jp) throws Exception {
    PluginFactory.getInstance().getPlugin().start();
  }
  
  @Before("call(void stop()) && within(com.adaptris.core.Adapter)")
  public synchronized void beforeAdapterStop(JoinPoint jp) throws Exception {
    PluginFactory.getInstance().getPlugin().stop();
  }
}
