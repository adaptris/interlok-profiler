/*
    Copyright 2015 Adaptris Ltd.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

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

  private static final ExecutorService threadPool = Executors.newCachedThreadPool();
  private StepIncrementor sequenceGenerator = new MessageStepIncrementor();
  protected transient Logger log = LoggerFactory.getLogger(this.getClass());

  protected static Map<String, WorkflowImp> serviceWorkflowMap = new HashMap<>();
  protected static Map<String, WorkflowImp> knownWorkflows = new HashMap<>();
  protected static Map<String, Service> serviceServiceCollectionMap = new HashMap<>();

  public BaseAspect() {
  }

  protected void sendEvent(final ProcessStep step) {
    for (final EventReceiver receiver : PluginFactory.getInstance().getPlugin().getReceivers()) {
      threadPool.execute(new Runnable() {
        @Override
        public void run() {
          Thread.currentThread().setName("Profiler-Event@" + hashCode());
          receiver.onEvent(step);
        }
      });
    }
  }

  protected long getNextSequenceNumber(String msgId) {
    return sequenceGenerator.generate(msgId);
  }

  protected MessageProcessStep createStep(StepType type, Object object, String messageId) {
    MessageProcessStep step = new MessageProcessStep();
    step.setMessageId(messageId);
    step.setStepType(type);
    step.setOrder(getNextSequenceNumber(messageId));
    step.setStepName(object.getClass().getSimpleName());
    step.setStepInstanceId(ReflectionHelper.getUniqueId(object));
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

  protected void recordEventStartTime(ProcessStep processStep) {
    processStep.setTimeStarted(System.currentTimeMillis());
    processStep.setTimeStartedNanos(System.nanoTime());
  }

  protected void recordEventTimeTaken(ProcessStep processStep) {
    long differenceMs = System.currentTimeMillis() - processStep.getTimeStarted();
    processStep.setTimeTakenMs(differenceMs);
    long differenceNanos = System.nanoTime() - processStep.getTimeStartedNanos();
    processStep.setTimeTakenNanos(differenceNanos);
  }

  protected List<Service> getNestedServices(List<Service> services) {
    List<Service> results = new ArrayList<>();
    if (isNotNull(services)) {
      for (Service service : services) {
        results.add(service);
        results.addAll(getNestedServices(service));
      }
    }
    return results;
  }

  protected List<Service> getNestedServices(Service[] services) {
    List<Service> results = new ArrayList<>();
    if (isNotNull(services)) {
      for (Service service : services) {
        results.add(service);
        results.addAll(getNestedServices(service));
      }
    }
    return results;
  }

  // TODO What about the ElseService, ThenService, While ....
  protected List<Service> getNestedServices(Service service) {
    List<Service> results = new ArrayList<>();
    if (service instanceof ServiceWrapper) {
      results.addAll(getNestedServices((ServiceWrapper) service));
    } else if (service instanceof ServiceCollection) {
      results.addAll(getNestedServices((ServiceCollection) service));
    }
    return results;
  }

  protected List<Service> getNestedServices(ServiceWrapper serviceWrapper) {
    return getNestedServices(serviceWrapper.wrappedServices());
  }

  protected List<Service> getNestedServices(ServiceCollection serviceCollection) {
    return getNestedServices(serviceCollection.getServices());
  }

  protected List<Service> getNestedServices(WorkflowImp workflow) {
    return getNestedServices(workflow.getServiceCollection());
  }

  private boolean isNotNull(Object obj) {
    return obj != null;
  }

}
