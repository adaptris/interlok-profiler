package com.adaptris.profiler.aspects;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.DefaultSerializableMessageTranslator;
import com.adaptris.profiler.MessageProcessStep;
import com.adaptris.profiler.ProcessStep;
import com.adaptris.profiler.ReflectionHelper;
import com.adaptris.profiler.StepType;
import com.adaptris.profiler.client.ClientFactory;
import com.adaptris.profiler.client.EventReceiver;
import com.adaptris.profiler.client.PluginFactory;

@Aspect
public class ProducerAspect {

  private static Map<String, ProcessStep> waitingForCompletion = new HashMap<String, ProcessStep>();

  protected transient Log log = LogFactory.getLog(this.getClass().getName());

  @Before("call(void produce(com.adaptris.core.AdaptrisMessage, com.adaptris.core.ProduceDestination))  && within(com.adaptris.core..*)")
  public synchronized void beforeService(JoinPoint jp) {
    try {
      DefaultSerializableMessageTranslator translator = new DefaultSerializableMessageTranslator();
      
      String producerClass = jp.getTarget().getClass().getSimpleName();
      AdaptrisMessage message = ((AdaptrisMessage) jp.getArgs()[0]);
      String uniqueId = ReflectionHelper.getUniqueId(jp.getTarget());
      String messageId = message.getUniqueId();
    
      MessageProcessStep step = new MessageProcessStep();
      step.setMessageId(messageId);
      step.setStepInstanceId(uniqueId);
      step.setStepName(producerClass);
      step.setStepType(StepType.PRODUCER);
      step.setOrder(ClientFactory.getInstance().getStepIncrementor().generate(messageId));
      step.setTimeTakenMs(new Date().getTime());
      step.setMessage(translator.translate(message));
      
      String key = messageId + producerClass + uniqueId;
      waitingForCompletion.put(key, step);
      
      log.debug("BEFORE PRODUCE (" + producerClass + "(" + uniqueId + "): " + messageId);
    } catch (Exception e) {
      log.error(e);
    } 
  }
  
  @After("call(void produce(com.adaptris.core.AdaptrisMessage, com.adaptris.core.ProduceDestination))  && within(com.adaptris.core..*)")
  public synchronized void afterService(JoinPoint jp) {
    try {
      String producerClass = jp.getTarget().getClass().getSimpleName();
      String uniqueId = ReflectionHelper.getUniqueId(jp.getTarget());
      String messageId = ((AdaptrisMessage) jp.getArgs()[0]).getUniqueId();
    
      String key = messageId + producerClass + uniqueId;
      ProcessStep step = waitingForCompletion.get(key);
      
      long startTime = step.getTimeTakenMs();
      step.setTimeTakenMs(new Date().getTime() - startTime);
      
      waitingForCompletion.remove(key);
      this.sendEvent(step);
      
      log.debug("AFTER PRODUCE (" + producerClass + "(" + uniqueId + "): " + messageId);
    } catch (Exception e) {
      log.error(e);
    } 
  }
  
  private void sendEvent(ProcessStep step) throws Exception {
    for(EventReceiver receiver : PluginFactory.getInstance().getReceivers()) {
      receiver.onEvent(step);
    }
  }
}
