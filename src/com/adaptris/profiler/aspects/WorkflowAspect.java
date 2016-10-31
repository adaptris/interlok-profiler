package com.adaptris.profiler.aspects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.SerializableAdaptrisMessage;
import com.adaptris.core.Workflow;
import com.adaptris.core.WorkflowImp;
import com.adaptris.profiler.MessageProcessStep;
import com.adaptris.profiler.ProcessStep;
import com.adaptris.profiler.StepType;

@Aspect
public class WorkflowAspect extends BaseAspect {

  private static Map<String, ProcessStep> waitingForCompletion = new HashMap<String, ProcessStep>();

  @Before("call(void workflowStart(com.adaptris.core.AdaptrisMessage)) && within(com.adaptris..*)")
  public synchronized void beforeService(JoinPoint jp) {
    try {
      if(jp.getTarget() instanceof Workflow) {
        AdaptrisMessage message = (AdaptrisMessage) jp.getArgs()[0];
        Date now = new Date();
        message.addMetadata("AdaptrisWorkflowEntryTimestamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
        SerializableAdaptrisMessage serializedMsg = serialize(message);
        
        sendEvent(createStep(StepType.CONSUMER, ((WorkflowImp) jp.getTarget()).getConsumer(), serializedMsg));
        MessageProcessStep workflowStep = createStep(StepType.WORKFLOW, jp.getTarget(), serializedMsg);
        workflowStep.setTimeStarted(now.getTime());
        waitingForCompletion.put(generateStepKey(jp), workflowStep);
        log("Before Workflow", jp);
      }
    }
    catch (Exception e) {
      log.error("", e);
    }
  }

  @After("call(void workflowEnd(com.adaptris.core.AdaptrisMessage, com.adaptris.core.AdaptrisMessage)) && within(com.adaptris..*)")
  public synchronized void afterService(JoinPoint jp) {
    if(jp.getTarget() instanceof Workflow) {
      String key = generateStepKey(jp);
      ProcessStep step = waitingForCompletion.get(key);
      // Step will only be null, if we've had an error in the beforeService (serializing the message).
      if (step != null) {
        long difference = System.currentTimeMillis() - step.getTimeStarted();
        step.setTimeTakenMs(difference);
        waitingForCompletion.remove(key);
        this.sendEvent(step);
        log("After Workflow", jp);
      }
    }
  }

}
