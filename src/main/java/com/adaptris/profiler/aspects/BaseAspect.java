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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.profiler.MessageProcessStep;
import com.adaptris.profiler.ProcessStep;
import com.adaptris.profiler.ReflectionHelper;
import com.adaptris.profiler.StepType;
import com.adaptris.profiler.jmx.EventReceiverToJMX;

abstract class BaseAspect {

  protected static final String WORKFLOW_ID_KEY = "AdaptrisWorkflowEntryID";

  private static final ExecutorService threadPool = Executors.newCachedThreadPool();
  private StepIncrementor sequenceGenerator = new MessageStepIncrementor();
  protected transient Logger log = LoggerFactory.getLogger(this.getClass());
  private static EventReceiverToJMX receiver = new EventReceiverToJMX();

  public BaseAspect() {

  }

  protected void sendEvent(final ProcessStep step) {
    threadPool.execute(() -> {
      Thread.currentThread().setName("Profiler-Event@" + hashCode());
      receiver.onEvent(step);
    });
  }

  protected long getNextSequenceNumber(String msgId) {
    return sequenceGenerator.generate(msgId);
  }

  protected MessageProcessStep createStep(StepType type, Object o, String messageId, String workflowId) {
    MessageProcessStep step = new MessageProcessStep();
    step.setMessageId(messageId);
    step.setStepType(type);
    step.setOrder(getNextSequenceNumber(messageId));
    step.setStepName(o.getClass().getSimpleName());
    step.setStepInstanceId(ReflectionHelper.getUniqueId(o));
    step.setWorkflowId(workflowId);
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
    processStep.setTimeStartedMs(System.currentTimeMillis());
    processStep.setTimeStartedNanos(System.nanoTime());
  }

  protected void recordEventTimeTaken(ProcessStep processStep) {
    long differenceMs = System.currentTimeMillis() - processStep.getTimeStartedMs();
    processStep.setTimeTakenMs(differenceMs);
    long differenceNanos = System.nanoTime() - processStep.getTimeStartedNanos();
    processStep.setTimeTakenNanos(differenceNanos);
  }

}
