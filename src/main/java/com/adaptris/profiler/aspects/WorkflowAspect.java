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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreConstants;
import com.adaptris.core.Workflow;
import com.adaptris.core.WorkflowImp;
import com.adaptris.profiler.MessageProcessStep;
import com.adaptris.profiler.ProcessStep;
import com.adaptris.profiler.StepType;

@Aspect
public class WorkflowAspect extends BaseAspect {

  private static final String WORKFLOW_ENTRY_START_KEY = "AdaptrisWorkflowEntryTimestamp"; 
  
  private static Map<String, ProcessStep> waitingForCompletion = new HashMap<String, ProcessStep>();

  @Before("call(void workflowStart(com.adaptris.core.AdaptrisMessage)) && within(com.adaptris..*)")
  public synchronized void beforeService(JoinPoint jp) {
    try {
      if(jp.getTarget() instanceof Workflow) {
        Workflow wf = ((WorkflowImp) jp.getTarget());
        
        AdaptrisMessage message = (AdaptrisMessage) jp.getArgs()[0];
        Date now = new Date();
        message.addMetadata(WORKFLOW_ENTRY_START_KEY, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
        message.addMetadata(WORKFLOW_ID_KEY, wf.obtainWorkflowId());
        
        sendEvent(createStep(StepType.CONSUMER,wf.getConsumer(), message.getUniqueId(), wf.obtainWorkflowId()));
        MessageProcessStep workflowStep = createStep(StepType.WORKFLOW, wf, message.getUniqueId(), wf.obtainWorkflowId());
        super.recordEventStartTime(workflowStep);
        
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
      // Step will only be null, if we've had an error in the beforeService
      if (step != null) {
        super.recordEventTimeTaken(step);
        AdaptrisMessage message = (AdaptrisMessage) jp.getArgs()[1];
        if(message.getObjectHeaders().containsKey(CoreConstants.OBJ_METADATA_EXCEPTION))
          step.setFailed(true);
        
        waitingForCompletion.remove(key);
        this.sendEvent(step);
        log("After Workflow", jp);
      }
    }
  }

}
