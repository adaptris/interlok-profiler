package com.adaptris.profiler.aspects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.SerializableAdaptrisMessage;
import com.adaptris.core.Service;
import com.adaptris.core.ServiceCollection;
import com.adaptris.core.ServiceWrapper;
import com.adaptris.core.Workflow;
import com.adaptris.core.WorkflowImp;
import com.adaptris.profiler.MessageProcessStep;
import com.adaptris.profiler.ProcessStep;
import com.adaptris.profiler.StepType;

@Aspect
public class WorkflowAspect extends BaseAspect {

  private static Map<String, ProcessStep> waitingForCompletion = new HashMap<String, ProcessStep>();

  @Before("call(void workflowStart(com.adaptris.core.AdaptrisMessage)) && within(com.adaptris..*)")
  public synchronized void beforeService(JoinPoint jp) {
    try {
      if (jp.getTarget() instanceof Workflow) {
        WorkflowImp workflow = knownWorkflows.get(((WorkflowImp) jp.getTarget()).getUniqueId());
        if (workflow == null) {
          List<Service> workflowServices = ((WorkflowImp) jp.getTarget()).getServiceCollection().getServices();
          List<Service> allNestedWorkflowServices = getAllServices(workflowServices);
          for (Service service : allNestedWorkflowServices) {
            serviceWorkflowMap.put(service.getUniqueId(), ((WorkflowImp) jp.getTarget()));
          }
          serviceWorkflowMap.put(((WorkflowImp) jp.getTarget()).getProducer().getUniqueId(),((WorkflowImp) jp.getTarget()));
          knownWorkflows.put(((WorkflowImp) jp.getTarget()).getUniqueId(), ((WorkflowImp) jp.getTarget()));
        }

        AdaptrisMessage message = (AdaptrisMessage) jp.getArgs()[0];
        Date now = new Date();
        message.addMetadata("AdaptrisWorkflowEntryTimestamp",
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
        SerializableAdaptrisMessage serializedMsg = serialize(message);

        sendEvent(createStep(StepType.CONSUMER, ((WorkflowImp) jp.getTarget()).getConsumer(), serializedMsg));
        MessageProcessStep workflowStep = createStep(StepType.WORKFLOW, jp.getTarget(), serializedMsg);
        workflowStep.setTimeStarted(now.getTime());
        waitingForCompletion.put(generateStepKey(jp), workflowStep);
        log("Before Workflow", jp);
      }
    } catch (Exception e) {
      log.error("", e);
    }
  }

  @After("call(void workflowEnd(com.adaptris.core.AdaptrisMessage, com.adaptris.core.AdaptrisMessage)) && within(com.adaptris..*)")
  public synchronized void afterService(JoinPoint jp) {
    if (jp.getTarget() instanceof Workflow) {
      String key = generateStepKey(jp);
      ProcessStep step = waitingForCompletion.get(key);
      // Step will only be null, if we've had an error in the beforeService
      // (serializing the message).
      if (step != null) {
        long difference = System.currentTimeMillis() - step.getTimeStarted();
        step.setTimeTakenMs(difference);
        waitingForCompletion.remove(key);
        this.sendEvent(step);
        log("After Workflow", jp);
      }
    }
  }

  /**
   * Get a list of all services including nested ones
   * @param services - list of services
   * @return
   */
  private List<Service> getAllServices(List<Service> workflowServices) {
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
  private List<Service> getAllServices(Service[] wrappedServices) {
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
  private List<Service> getAllServices(Service service) {
    List<Service> results = new ArrayList<Service>();
    if (service instanceof ServiceWrapper) {
      results.addAll(getAllServices((ServiceWrapper)service));
    }
    else if (service instanceof ServiceCollection) {
      results.addAll(getAllServices((ServiceCollection)service));
    }
    results.add(service);
    return results;
  }
  
  /**
   * Process a special service like SplitJoinService that has nested services
   * @param service
   * @return
   */
  private List<Service> getAllServices(ServiceWrapper service) {
    List<Service> results = new ArrayList<Service>();
    Service[] wrappedServices = ((ServiceWrapper)service).wrappedServices();
    if (wrappedServices != null && wrappedServices.length > 0)
      return getAllServices(wrappedServices);
    return results;
  }
  
  /**
   * Process a special service collection
   * @param serviceCollection
   * @return
   */
  private List<Service> getAllServices(ServiceCollection serviceCollection) {
    List<Service> results = new ArrayList<Service>();
    List<Service> services = ((ServiceCollection)serviceCollection).getServices();
    if (services != null && services.size() > 0)
      return getAllServices(services);
    return results;
  }
}
