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
public class ServiceAspect extends BaseAspect {

  private static Map<String, ProcessStep> waitingForCompletion = new HashMap<String, ProcessStep>();

  @Before("call(void doService(com.adaptris.core.AdaptrisMessage)) && within(com.adaptris..*)")
  public synchronized void beforeService(JoinPoint jp) {
    try {
      AdaptrisMessage message = (AdaptrisMessage) jp.getArgs()[0];
      SerializableAdaptrisMessage serializedMsg = serialize(message);

      MessageProcessStep step = createStep(StepType.SERVICE, jp.getTarget(), serializedMsg);
      step.setTimeStarted(System.currentTimeMillis());
      waitingForCompletion.put(generateStepKey(jp), step);
      log("Before Service", jp);
    }
    catch (Exception e) {
      log.error("", e);
    }
  }

  @After("call(void doService(com.adaptris.core.AdaptrisMessage)) && within(com.adaptris..*)")
  public synchronized void afterService(JoinPoint jp) {
    String key = generateStepKey(jp);
    ProcessStep step = waitingForCompletion.get(key);
    // Step will only be null, if we've had an error in the beforeService (serializing the message).
    if (step != null) {
      long difference = System.currentTimeMillis() - step.getTimeStarted();
      step.setTimeTakenMs(difference);
      waitingForCompletion.remove(key);
      this.sendEvent(step);
      log("After Service", jp);
    }
  }

}
