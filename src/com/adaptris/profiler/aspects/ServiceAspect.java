package com.adaptris.profiler.aspects;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.DefaultSerializableMessageTranslator;
import com.adaptris.profiler.MessageProcessStep;
import com.adaptris.profiler.ProcessStep;
import com.adaptris.profiler.ReflectionHelper;
import com.adaptris.profiler.StepType;
import com.adaptris.profiler.client.EventReceiver;
import com.adaptris.profiler.client.MessageStepIncrementor;
import com.adaptris.profiler.client.PluginFactory;

@Aspect
public class ServiceAspect {

  protected transient Logger log = LoggerFactory.getLogger(this.getClass());

  private static Map<String, ProcessStep> waitingForCompletion = new HashMap<String, ProcessStep>();

  @Before("call(void doService(com.adaptris.core.AdaptrisMessage)) && within(com.adaptris.core..*)")
  public synchronized void beforeService(JoinPoint jp) {
    try {
      DefaultSerializableMessageTranslator translator = new DefaultSerializableMessageTranslator();

      String serviceClass = jp.getTarget().getClass().getSimpleName();
      AdaptrisMessage message = (AdaptrisMessage) jp.getArgs()[0];
      String uniqueId = ReflectionHelper.getUniqueId(jp.getTarget());
      String messageId = message.getUniqueId();

      MessageProcessStep step = new MessageProcessStep();
      step.setMessageId(messageId);
      step.setStepInstanceId(uniqueId);
      step.setStepName(serviceClass);
      step.setStepType(StepType.SERVICE);
      step.setOrder(new MessageStepIncrementor().generate(messageId));
      step.setTimeStarted(System.nanoTime());
      step.setMessage(translator.translate(message));

      String key = messageId + serviceClass + uniqueId;
      waitingForCompletion.put(key, step);
      log.trace("Before Service ({}({}) : {}", serviceClass, uniqueId, messageId);
    } catch (Exception e) {
      log.error("", e);
    }
  }

  @After("call(void doService(com.adaptris.core.AdaptrisMessage)) && within(com.adaptris.core..*)")
  public synchronized void afterService(JoinPoint jp) {
    try {
      String serviceClass = jp.getTarget().getClass().getSimpleName();
      String uniqueId = ReflectionHelper.getUniqueId(jp.getTarget());
      String messageId = ((AdaptrisMessage) jp.getArgs()[0]).getUniqueId();

      String key = messageId + serviceClass + uniqueId;
      ProcessStep step = waitingForCompletion.get(key);

      long difference = System.nanoTime() - step.getTimeStarted();
      step.setTimeTakenMs(difference);

      waitingForCompletion.remove(key);
      this.sendEvent(step);

      log.trace("After Service ({}({}) : {}", serviceClass, uniqueId, messageId);
    } catch (Exception e) {
      log.error("", e);
    }
  }

  private void sendEvent(ProcessStep step) throws Exception {
    for(EventReceiver receiver : PluginFactory.getInstance().getReceivers()) {
      receiver.onEvent(step);
    }
  }

}
