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
import com.adaptris.core.AdaptrisMessageConsumer;
import com.adaptris.core.DefaultSerializableMessageTranslator;
import com.adaptris.core.WorkflowImp;
import com.adaptris.profiler.MessageProcessStep;
import com.adaptris.profiler.ProcessStep;
import com.adaptris.profiler.ReflectionHelper;
import com.adaptris.profiler.StepType;
import com.adaptris.profiler.client.ClientFactory;
import com.adaptris.profiler.client.EventReceiver;
import com.adaptris.profiler.client.PluginFactory;

@Aspect
public class WorkflowAspect {

  protected transient Log log = LogFactory.getLog(this.getClass().getName());
  
  private static Map<String, ProcessStep> waitingForCompletion = new HashMap<String, ProcessStep>();
  
  @Before("call(void workflowStart(com.adaptris.core.AdaptrisMessage))  && within(com.adaptris.core..*)")
  public synchronized void beforeService(JoinPoint jp) {
    try {
      DefaultSerializableMessageTranslator translator = new DefaultSerializableMessageTranslator();
      
      String workflowClass = jp.getTarget().getClass().getSimpleName();
      AdaptrisMessage message = ((AdaptrisMessage) jp.getArgs()[0]);
      String uniqueId = ReflectionHelper.getUniqueId(jp.getTarget());
      String messageId = message.getUniqueId();
      
      MessageProcessStep step = new MessageProcessStep();
      step.setMessageId(messageId);
      step.setStepInstanceId(uniqueId);
      step.setStepName(workflowClass);
      step.setStepType(StepType.WORKFLOW);
      step.setOrder(ClientFactory.getInstance().getStepIncrementor().generate(messageId));
      step.setTimeTakenMs(new Date().getTime());
      step.setTimeStarted(new Date().getTime());
      message.addMetadata("ENTRY_TIMESTAMP", new Date().toGMTString());
      step.setMessage(translator.translate(message));
      
      String key = messageId + workflowClass + uniqueId;
      waitingForCompletion.put(key, step);
      
      AdaptrisMessageConsumer consumer = ((WorkflowImp) jp.getTarget()).getConsumer();
      MessageProcessStep consumerStep = new MessageProcessStep();
      consumerStep.setMessageId(messageId);
      consumerStep.setStepInstanceId(consumer.getUniqueId());
      consumerStep.setStepName(consumer.getClass().getSimpleName());
      consumerStep.setStepType(StepType.CONSUMER);
      consumerStep.setOrder(ClientFactory.getInstance().getStepIncrementor().generate(messageId));
      consumerStep.setMessage(translator.translate(message));
      consumerStep.setTimeTakenMs(0l);
            
      this.sendEvent(consumerStep);
      
      log.debug("BEFORE WORKFLOW (" + workflowClass + "(" + uniqueId + "): " + messageId);
    } catch (Exception e) {
      log.error(e);
    } 
  }
  
  @After("call(void workflowEnd(com.adaptris.core.AdaptrisMessage, com.adaptris.core.AdaptrisMessage))  && within(com.adaptris.core..*)")
  public synchronized void afterService(JoinPoint jp) {
    try {
      String workflowClass = jp.getTarget().getClass().getSimpleName();
      String uniqueId = ReflectionHelper.getUniqueId(jp.getTarget());
      String messageId = ((AdaptrisMessage) jp.getArgs()[0]).getUniqueId();
    
      String key = messageId + workflowClass + uniqueId;
      ProcessStep step = waitingForCompletion.get(key);
            
      long startTime = step.getTimeTakenMs();
      step.setTimeTakenMs(new Date().getTime() - startTime);
      
      waitingForCompletion.remove(key);
      this.sendEvent(step);
      
      log.debug("AFTER WORKFLOW (" + workflowClass + "(" + uniqueId + "): " + messageId);
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
