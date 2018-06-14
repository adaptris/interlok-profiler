package com.adaptris.profiler.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.Adapter;
import com.adaptris.core.Channel;
import com.adaptris.core.Service;
import com.adaptris.core.StandardWorkflow;
import com.adaptris.profiler.client.PluginFactory;

@Aspect
public class AdapterAspect extends BaseAspect {

  public static final String DUMMY_CHANNEL  = "dummy-shared-component-channel";
  public static final String DUMMY_WORKFLOW = "dummy-shared-component-workflow";
  
  protected transient Logger log = LoggerFactory.getLogger(this.getClass());


  @Before("execution(* com.adaptris.core.Adapter.init())")
  public synchronized void beforeAdapterInit(JoinPoint jp) throws Exception {
    log.trace("Profiler : Before Adapter Init");
    PluginFactory.getInstance().getPlugin().init(jp.getTarget());
  }
  
  @Before("execution(* com.adaptris.core.Adapter.start())")
  public synchronized void beforeAdapterStart(JoinPoint jp) throws Exception {
    log.trace("Profiler : Before Adapter Start");
    PluginFactory.getInstance().getPlugin().start(jp.getTarget());
    myAdapter = (Adapter) jp.getTarget();
    
    // Process shared services by added fake hierarchy of workflow and channel
    StandardWorkflow dummyWorkflow = new StandardWorkflow();
    dummyWorkflow.setUniqueId(DUMMY_WORKFLOW);
    Channel dummyChannel = new Channel();
    dummyChannel.setUniqueId(DUMMY_CHANNEL);
    dummyWorkflow.registerChannel(dummyChannel);
    for (Service service : getAllServices(myAdapter.getSharedComponents().getServices())) {
      serviceWorkflowMap.put(service.getUniqueId(), dummyWorkflow);
    }
    
  }
  
  @Before("execution(* com.adaptris.core.Adapter.stop())")
  public synchronized void beforeAdapterStop(JoinPoint jp) throws Exception {
    log.trace("Profiler : Before Adapter Stop");
    PluginFactory.getInstance().getPlugin().stop(jp.getTarget());
  }

  @Before("execution(* com.adaptris.core.Adapter.close())")
  public synchronized void beforeAdapterClose(JoinPoint jp) throws Exception {
    log.trace("Profiler : Before Adapter Close");
    PluginFactory.getInstance().getPlugin().close(jp.getTarget());
  }
}
