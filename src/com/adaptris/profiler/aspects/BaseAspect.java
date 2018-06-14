package com.adaptris.profiler.aspects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.Adapter;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.DefaultSerializableMessageTranslator;
import com.adaptris.core.SerializableAdaptrisMessage;
import com.adaptris.core.SerializableMessageTranslator;
import com.adaptris.core.Service;
import com.adaptris.core.ServiceCollection;
import com.adaptris.core.ServiceWrapper;
import com.adaptris.core.WorkflowImp;
import com.adaptris.profiler.MessageProcessStep;
import com.adaptris.profiler.ProcessStep;
import com.adaptris.profiler.ReflectionHelper;
import com.adaptris.profiler.StepType;
import com.adaptris.profiler.client.EventReceiver;
import com.adaptris.profiler.client.PluginFactory;

abstract class BaseAspect {

  protected static Adapter myAdapter;
  
  protected static Map<String, WorkflowImp> serviceWorkflowMap = new HashMap<>();
  protected static Map<String, WorkflowImp> knownWorkflows = new HashMap<>();
  
  private static final ExecutorService threadPool = Executors.newCachedThreadPool();
  private SerializableMessageTranslator translator = new DefaultSerializableMessageTranslator();
  private StepIncrementor sequenceGenerator = new MessageStepIncrementor();
  
  protected transient Logger log = LoggerFactory.getLogger(this.getClass());

  public BaseAspect() {
  }

  protected void sendEvent(final ProcessStep step) {
    for (final EventReceiver receiver : PluginFactory.getInstance().getPlugin().getReceivers()) {
      threadPool.execute(new Runnable() {
        public void run() {
          Thread.currentThread().setName("Profiler-Event@" + hashCode());
          receiver.onEvent(step);
        }
      });
    }
  }

  protected SerializableAdaptrisMessage serialize(AdaptrisMessage msg) throws Exception {
    return new SerializableAdaptrisMessage(translator.translate(msg));
  }

  protected long getNextSequenceNumber(String msgId) {
    return sequenceGenerator.generate(msgId);
  }

  protected MessageProcessStep createStep(StepType type, Object o, SerializableAdaptrisMessage serializedMsg) {
    // add the parent map here!
    MessageProcessStep step = new MessageProcessStep();
    step.setInterlokComponent(new InterlokComponent().build(o, serviceWorkflowMap, myAdapter));
    step.setMessageId(serializedMsg.getUniqueId());
    step.setStepType(type);
    step.setOrder(getNextSequenceNumber(serializedMsg.getUniqueId()));
    step.setMessage(serializedMsg);
    step.setStepName(o.getClass().getSimpleName());
    step.setStepInstanceId(ReflectionHelper.getUniqueId(o));
    log.trace("Creating Step for {} {}", type, serializedMsg.getUniqueId());
    return step;
  }

  // Return a unique key for the step which is messageUniqueId-JoinpointTargetClass-JoinPointTargetUniqueId
  protected static String generateStepKey(JoinPoint jp) {
    return ReflectionHelper.getUniqueId(jp.getArgs()[0]) + jp.getTarget().getClass().getSimpleName()
        + ReflectionHelper.getUniqueId(jp.getTarget());
  }

  // Do logging using the {} notation because SLF4J is clever enough not to call the methods if the logger isn't traceEnabled
  protected void log(String prefix, JoinPoint jp) {
    log.trace("{} ({}({})) : {}", prefix, jp.getTarget().getClass().getSimpleName(), ReflectionHelper.getUniqueId(jp.getTarget()),
        ReflectionHelper.getUniqueId(jp.getArgs()[0]));
  }
  
  /**
   * Get a list of all services including nested ones for a workflow
   * @param workflow
   * @return
   */
  protected List<Service> getAllServices(WorkflowImp workflow) {
    List<Service> results = new ArrayList<Service>();
    results.addAll(getAllServices(workflow.getServiceCollection()));
    return results;
  }
  
  /**
   * Get a list of all services including nested ones
   * @param services - list of services
   * @return
   */
  protected List<Service> getAllServices(List<Service> workflowServices) {
    List<Service> results = new ArrayList<Service>();
    for (Service service : workflowServices) {
      results.addAll(getAllServices(service));
    }
    return results;
  }
  
  /**
   * Get a list of all services including nested ones
   * @param wrappedServices - Array of services
   * @return
   */
  protected List<Service> getAllServices(Service[] wrappedServices) {
    List<Service> results = new ArrayList<Service>();
    for (Service service : wrappedServices) {
      results.addAll(getAllServices(service));
    }
    return results;
  }
  
  /**
   * Process a single standard service
   * @param service
   * @return
   */
  protected List<Service> getAllServices(Service service) {
    List<Service> results = new ArrayList<Service>();
    
    if (service instanceof ServiceWrapper) {
      results.addAll(getAllServices((ServiceWrapper)service));
    }
    else if (service instanceof ServiceCollection) {
      results.addAll(getAllServices((ServiceCollection)service));
    }
    else {
      results.add(service);
    }
    return results;
  }
  
  /**
   * Process a special service like SplitJoinService that has nested services
   * @param service
   * @return
   */
  protected List<Service> getAllServices(ServiceWrapper service) {
    List<Service> results = new ArrayList<Service>();
    results.add(service);
    Service[] wrappedServices = ((ServiceWrapper)service).wrappedServices();
    if (wrappedServices != null && wrappedServices.length > 0)
      results.addAll(getAllServices(wrappedServices));
    return results;
  }
  
  /**
   * Process a special service collection
   * @param serviceCollection
   * @return
   */
  protected List<Service> getAllServices(ServiceCollection serviceCollection) {
    List<Service> results = new ArrayList<Service>();
    results.add(serviceCollection);
    List<Service> services = ((ServiceCollection)serviceCollection).getServices();
    if (services != null && services.size() > 0)
      results.addAll(getAllServices(services));
    return results;
  }

}
