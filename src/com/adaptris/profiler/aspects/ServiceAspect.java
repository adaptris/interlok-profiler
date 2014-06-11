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
public class ServiceAspect {
  
  protected transient Log log = LogFactory.getLog(this.getClass().getName());
  
  private static Map<String, ProcessStep> waitingForCompletion = new HashMap<String, ProcessStep>();
  
  @Before("call(void doService(com.adaptris.core.AdaptrisMessage))  && within(com.adaptris.core..*)")
  public synchronized void beforeService(JoinPoint jp) {
    try {
      DefaultSerializableMessageTranslator translator = new DefaultSerializableMessageTranslator();
      
      String serviceClass = jp.getTarget().getClass().getSimpleName();
      AdaptrisMessage message = ((AdaptrisMessage) jp.getArgs()[0]);
      String uniqueId = ReflectionHelper.getUniqueId(jp.getTarget());
      String messageId = message.getUniqueId();
    
      MessageProcessStep step = new MessageProcessStep();
      step.setMessageId(messageId);
      step.setStepInstanceId(uniqueId);
      step.setStepName(serviceClass);
      step.setStepType(StepType.SERVICE);
      step.setOrder(ClientFactory.getInstance().getStepIncrementor().generate(messageId));
      step.setTimeTakenMs(new Date().getTime());
      step.setMessage(translator.translate(message));
      
      String key = messageId + serviceClass + uniqueId;
      waitingForCompletion.put(key, step);
      
      log.debug("BEFORE SERVICE (" + serviceClass + "(" + uniqueId + "): " + messageId);
    } catch (Exception e) {
      log.error(e);
    } 
  }
  
  @After("call(void doService(com.adaptris.core.AdaptrisMessage))  && within(com.adaptris.core..*)")
  public synchronized void afterService(JoinPoint jp) {
    try {
      String serviceClass = jp.getTarget().getClass().getSimpleName();
      String uniqueId = ReflectionHelper.getUniqueId(jp.getTarget());
      String messageId = ((AdaptrisMessage) jp.getArgs()[0]).getUniqueId();
    
      String key = messageId + serviceClass + uniqueId;
      ProcessStep step = waitingForCompletion.get(key);
      
      long startTime = step.getTimeTakenMs();
      step.setTimeTakenMs(new Date().getTime() - startTime);
      
      waitingForCompletion.remove(key);
      this.sendEvent(step);
      
      log.debug("AFTER SERVICE (" + serviceClass + "(" + uniqueId + "): " + messageId);
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
