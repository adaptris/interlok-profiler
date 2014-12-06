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
public class ProducerAspect {

  private static Map<String, ProcessStep> waitingForCompletion = new HashMap<String, ProcessStep>();

  protected transient Logger log = LoggerFactory.getLogger(this.getClass());

  @Before("call(void produce(com.adaptris.core.AdaptrisMessage, com.adaptris.core.ProduceDestination)) && within(com.adaptris.core..*)")
  public synchronized void beforeService(JoinPoint jp) {
    try {
      DefaultSerializableMessageTranslator translator = new DefaultSerializableMessageTranslator();

      String producerClass = jp.getTarget().getClass().getSimpleName();
      AdaptrisMessage message = (AdaptrisMessage) jp.getArgs()[0];
      String uniqueId = ReflectionHelper.getUniqueId(jp.getTarget());
      String messageId = message.getUniqueId();

      MessageProcessStep step = new MessageProcessStep();
      step.setMessageId(messageId);
      step.setStepInstanceId(uniqueId);
      step.setStepName(producerClass);
      step.setStepType(StepType.PRODUCER);
      step.setOrder(new MessageStepIncrementor().generate(messageId));
      step.setTimeStarted(System.currentTimeMillis());
      step.setMessage(translator.translate(message));

      String key = messageId + producerClass + uniqueId;
      waitingForCompletion.put(key, step);
      log.trace("Before Produce ({}({}) : {}", producerClass, uniqueId, messageId);

    } catch (Exception e) {
      log.error("", e);
    }
  }

  @After("call(void produce(com.adaptris.core.AdaptrisMessage, com.adaptris.core.ProduceDestination)) && within(com.adaptris.core..*)")
  public synchronized void afterService(JoinPoint jp) {
    try {
      String producerClass = jp.getTarget().getClass().getSimpleName();
      String uniqueId = ReflectionHelper.getUniqueId(jp.getTarget());
      String messageId = ((AdaptrisMessage) jp.getArgs()[0]).getUniqueId();

      String key = messageId + producerClass + uniqueId;
      ProcessStep step = waitingForCompletion.get(key);
      long difference = System.currentTimeMillis() - step.getTimeStarted();
      step.setTimeTakenMs(difference);

      waitingForCompletion.remove(key);
      this.sendEvent(step);
      log.trace("After Produce ({}({}) : {}", producerClass, uniqueId, messageId);
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
