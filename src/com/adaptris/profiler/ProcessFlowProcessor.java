package com.adaptris.profiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adaptris.profiler.client.ClientFactory;

public class ProcessFlowProcessor {

  protected transient Log log = LogFactory.getLog(this.getClass().getName());
  
  private static Map<String, UnprocessedMessageFlow> unprocessed = new HashMap<String, UnprocessedMessageFlow>();
  
  /**
   * Each ProcessStep will contain an;
   *    MessageId
   *    Class name of the step
   *    Steps unique id
   * 
   * Also each ProcessStep will have a counter, that will be related to the messageId.
   * Every message will have a step 1, 2...
   * 
   * @param step
   */
  public synchronized void registerEvent(ProcessStep step) {
    log.info(step);
    try {
      UnprocessedMessageFlow unprocessedMessageFlow = null;
      if(unprocessed.containsKey(step.getMessageId())) {
        unprocessedMessageFlow = unprocessed.get(step.getMessageId());
        unprocessedMessageFlow.addStep(step);
      } else {
        unprocessedMessageFlow = new UnprocessedMessageFlow();
        unprocessedMessageFlow.setMessageId(step.getMessageId());
        unprocessedMessageFlow.addStep(step);
        unprocessed.put(step.getMessageId(), unprocessedMessageFlow);
      }
      
      // if we are just ending a workflow, lets process the entire messages flow
      if(step.getStepType().equals(StepType.WORKFLOW)) {
        this.processMessageFlow(unprocessedMessageFlow);
        unprocessed.remove(step.getMessageId());
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
      
  }

  private void processMessageFlow(UnprocessedMessageFlow unprocessedMessageFlow) {
    ClientFactory.getInstance().getProcessFlowMap().processMessage(unprocessedMessageFlow, unprocessedMessageFlow.getSteps());
    
    logWorkflowStats(ProcessFlowMap.getFlowMap());
  }

  private void logWorkflowStats(Map<String, ProcessFlowLinkedProcess> flowMap) {
    Set<String> keySet = flowMap.keySet();
    for(String key : keySet) {
      ProcessFlowLinkedProcess processFlowLinkedProcess = flowMap.get(key);
      if(processFlowLinkedProcess.getPreviousStepUniqueId() == null) {  // means this is the workflow step (Bad huh!)
        this.logWorkflowStats(flowMap, processFlowLinkedProcess);
      }
    }
  }

  private void logWorkflowStats(Map<String, ProcessFlowLinkedProcess> flowMap, ProcessFlowLinkedProcess processFlowLinkedProcess) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("(Workflow) " + processFlowLinkedProcess.getProcessStep().getStepInstanceId());
    buffer.append(" avg ms = " + processFlowLinkedProcess.getAverageProcessingTime());
    buffer.append(" (" + processFlowLinkedProcess.getNumMessages() + " msgs)");
    buffer.append("\n");
    ProcessFlowLinkedProcess currentProcessing = processFlowLinkedProcess;
    
    int serviceCount = 1;
    while(currentProcessing.getNextStepUniqueId() != null) {
      currentProcessing = flowMap.get(currentProcessing.getNextStepUniqueId());
      
      if(serviceCount == 1) // then its the services container
        buffer.append("    ");
      else {
        if(currentProcessing.getProcessStep().getStepType().equals(StepType.SERVICE))
          buffer.append("        ");
        else if(currentProcessing.getProcessStep().getStepType().equals(StepType.PRODUCER))
          buffer.append("    ");
      }
      
      buffer.append(currentProcessing.getProcessStep().getStepInstanceId() + " avg ms " + currentProcessing.getAverageProcessingTime());
      buffer.append("\n");
      
      serviceCount ++;
    }
    log.debug(buffer.toString());
  }
}
