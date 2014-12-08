package com.adaptris.profiler.aspects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageConsumer;
import com.adaptris.core.DefaultSerializableMessageTranslator;
import com.adaptris.core.WorkflowImp;
import com.adaptris.profiler.MessageProcessStep;
import com.adaptris.profiler.ProcessStep;
import com.adaptris.profiler.ReflectionHelper;
import com.adaptris.profiler.StepType;
import com.adaptris.profiler.client.EventReceiver;
import com.adaptris.profiler.client.MessageStepIncrementor;
import com.adaptris.profiler.client.PluginFactory;

@Aspect
public class WorkflowAspect {

  protected transient Logger log = LoggerFactory.getLogger(this.getClass());

  private static Map<String, ProcessStep> waitingForCompletion = new HashMap<String, ProcessStep>();

  @Before("call(void workflowStart(com.adaptris.core.AdaptrisMessage)) && within(com.adaptris.core..*)")
  public synchronized void beforeService(JoinPoint jp) {
    try {
      DefaultSerializableMessageTranslator translator = new DefaultSerializableMessageTranslator();

      String workflowClass = jp.getTarget().getClass().getSimpleName();
      AdaptrisMessage message = (AdaptrisMessage) jp.getArgs()[0];
      String uniqueId = ReflectionHelper.getUniqueId(jp.getTarget());
      String messageId = message.getUniqueId();

      MessageProcessStep step = new MessageProcessStep();
      step.setMessageId(messageId);
      step.setStepInstanceId(uniqueId);

      step.setStepName(workflowClass);
      step.setStepType(StepType.WORKFLOW);
      step.setOrder(new MessageStepIncrementor().generate(messageId));
      Date now = new Date();
      step.setTimeStarted(System.currentTimeMillis());
      message.addMetadata("ENTRY_TIMESTAMP", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
      step.setMessage(translator.translate(message));

      String key = messageId + workflowClass + uniqueId;
      waitingForCompletion.put(key, step);

      AdaptrisMessageConsumer consumer = ((WorkflowImp) jp.getTarget()).getConsumer();
      MessageProcessStep consumerStep = new MessageProcessStep();
      consumerStep.setMessageId(messageId);
      consumerStep.setStepInstanceId(consumer.getUniqueId());
      consumerStep.setStepName(consumer.getClass().getSimpleName());
      consumerStep.setStepType(StepType.CONSUMER);
      consumerStep.setOrder(new MessageStepIncrementor().generate(messageId));
      consumerStep.setMessage(translator.translate(message));
      consumerStep.setTimeStarted(System.currentTimeMillis());

      this.sendEvent(consumerStep);
      log.trace("Before Workflow ({}({})) : {}", workflowClass, uniqueId, messageId);
    } catch (Exception e) {
      log.error("", e);
    }
  }

  @After("call(void workflowEnd(com.adaptris.core.AdaptrisMessage, com.adaptris.core.AdaptrisMessage)) && within(com.adaptris.core..*)")
  public synchronized void afterService(JoinPoint jp) {
    try {
      String workflowClass = jp.getTarget().getClass().getSimpleName();
      String uniqueId = ReflectionHelper.getUniqueId(jp.getTarget());
      String messageId = ((AdaptrisMessage) jp.getArgs()[0]).getUniqueId();

      String key = messageId + workflowClass + uniqueId;
      ProcessStep step = waitingForCompletion.get(key);
      long difference = System.currentTimeMillis() - step.getTimeStarted();
      step.setTimeTakenMs(difference);

      waitingForCompletion.remove(key);
      this.sendEvent(step);

      log.trace("After Workflow ({}({})) : {}", workflowClass, uniqueId, messageId);
    } catch (Exception e) {
      log.error("", e);
    }
  }

  private void sendEvent(ProcessStep step) throws Exception {
    for (EventReceiver receiver : PluginFactory.getInstance().getPlugin().getReceivers()) {
      receiver.onEvent(step);
    }
  }
}
