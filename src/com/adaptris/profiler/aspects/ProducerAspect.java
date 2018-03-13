package com.adaptris.profiler.aspects;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.SerializableAdaptrisMessage;
import com.adaptris.profiler.MessageProcessStep;
import com.adaptris.profiler.ProcessStep;
import com.adaptris.profiler.StepType;

@Aspect
public class ProducerAspect extends BaseAspect {

  private static Map<String, ProcessStep> waitingForCompletion = new HashMap<String, ProcessStep>();

  @Before("call(void produce(com.adaptris.core.AdaptrisMessage, com.adaptris.core.ProduceDestination)) && within(com.adaptris..*)")
  public synchronized void beforeService(JoinPoint jp) {
    try {
      AdaptrisMessage message = (AdaptrisMessage) jp.getArgs()[0];
      SerializableAdaptrisMessage serializedMsg = serialize(message);
      MessageProcessStep step = createStep(StepType.PRODUCER, jp.getTarget(), serializedMsg);
      step.setTimeStarted(System.nanoTime());
      waitingForCompletion.put(generateStepKey(jp), step);
      log("Before Produce", jp);
    }
    catch (Exception e) {
      log.error("", e);
    }
  }

  @After("call(void produce(com.adaptris.core.AdaptrisMessage, com.adaptris.core.ProduceDestination)) && within(com.adaptris..*)")
  public synchronized void afterService(JoinPoint jp) {
    String key = generateStepKey(jp);
    ProcessStep step = waitingForCompletion.get(key);
    if (step != null) {
      long difference = System.nanoTime() - step.getTimeStarted();
      step.setTimeTakenMs(difference);
      waitingForCompletion.remove(key);
      sendEvent(step);
      log("After Produce", jp);
    }
  }
}
