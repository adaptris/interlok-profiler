package com.adaptris.profiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.adaptris.core.SerializableAdaptrisMessage;

public class ProcessFlowMap {
  
  private static ArrayList<ProcessStep> workflows = new ArrayList<>();
  
  private static Map<String, ProcessFlowLinkedProcess> flowMap = new HashMap<String, ProcessFlowLinkedProcess>();
  
  public void processMessage(UnprocessedMessageFlow flow, List<ProcessStep> steps) {
    try {
      Locker.getInstance().getLock().lock();
      for(int counter = 1; counter <= steps.size(); counter ++) {
        ProcessStep previousStep = this.getProcessStepByOrder(steps, counter - 1);
        ProcessStep processStep = this.getProcessStepByOrder(steps, counter);
        ProcessStep nextStep = this.getProcessStepByOrder(steps, counter + 1);
        
        try {
        if(processStep.getStepType().equals(StepType.WORKFLOW)) {
          if(!workflows.contains(processStep))
            workflows.add(processStep);
        }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        this.checkFlowMap(previousStep, processStep, nextStep);
        this.processStats(processStep);
        this.persistMessage(processStep);
      }
    } finally {
      Locker.getInstance().getLock().unlock();
    }
  }

  private void persistMessage(ProcessStep processStep) {
    ProcessFlowLinkedProcess linkedProcess = flowMap.get(processStep.getStepInstanceId());

    SerializableAdaptrisMessage serializableAdaptrisMessage = processStep.getMessage();
    serializableAdaptrisMessage.addMetadata("TIME_TAKEN_MS", Long.toString(processStep.getTimeTakenMs()));
    linkedProcess.getMessageCache().addMessage(serializableAdaptrisMessage);
  }

  private void processStats(ProcessStep processStep) {
    ProcessFlowLinkedProcess linkedProcess = flowMap.get(processStep.getStepInstanceId());
    int numMessages = linkedProcess.getNumMessages() + 1;
    long averageProcessTime = linkedProcess.getAverageProcessingTime();
    long differenceInTiming = processStep.getTimeTakenMs() - averageProcessTime;
    
    long amountToChange = differenceInTiming / numMessages;
    
    linkedProcess.setAverageProcessingTime(averageProcessTime + amountToChange);
    linkedProcess.setNumMessages(numMessages);
  }

  private void checkFlowMap(ProcessStep previousStep, ProcessStep currentStep, ProcessStep nextStep) {
    // add all steps into the flow if they do not exist
    if((previousStep != null) && (!flowMap.containsKey(previousStep.getStepInstanceId()))) {
      ProcessFlowLinkedProcess processFlowLinkedProcess = new ProcessFlowLinkedProcess();
      processFlowLinkedProcess.setProcessStep(previousStep);
      flowMap.put(previousStep.getStepInstanceId(), processFlowLinkedProcess);
    }
    if(!flowMap.containsKey(currentStep.getStepInstanceId())) {
      ProcessFlowLinkedProcess processFlowLinkedProcess = new ProcessFlowLinkedProcess();
      processFlowLinkedProcess.setProcessStep(currentStep);
      flowMap.put(currentStep.getStepInstanceId(), processFlowLinkedProcess);
    }
    if((nextStep != null) && (!flowMap.containsKey(nextStep.getStepInstanceId()))) {
      ProcessFlowLinkedProcess processFlowLinkedProcess = new ProcessFlowLinkedProcess();
      processFlowLinkedProcess.setProcessStep(nextStep);
      flowMap.put(nextStep.getStepInstanceId(), processFlowLinkedProcess);
    }
    // link the steps together.
    ProcessFlowLinkedProcess processFlowLinkedProcess = null;
    if(previousStep != null) {
      processFlowLinkedProcess = flowMap.get(previousStep.getStepInstanceId());
      processFlowLinkedProcess.setNextStepUniqueId(currentStep.getStepInstanceId());
    }
    
    processFlowLinkedProcess = flowMap.get(currentStep.getStepInstanceId());
    if(previousStep != null)
      processFlowLinkedProcess.setPreviousStepUniqueId(previousStep.getStepInstanceId());
    if(nextStep != null)
      processFlowLinkedProcess.setNextStepUniqueId(nextStep.getStepInstanceId());
    
    if(nextStep != null) {
      processFlowLinkedProcess = flowMap.get(nextStep.getStepInstanceId());
      processFlowLinkedProcess.setPreviousStepUniqueId(currentStep.getStepInstanceId());
    }
  }

  private ProcessStep getProcessStepByOrder(List<ProcessStep> steps, int order) {
    ProcessStep returnValue = null;
    for(ProcessStep step : steps) {
      if(step.getOrder() == order) {
        returnValue = step;
        break;
      }
    }
    return returnValue;
  }

  public static Map<String, ProcessFlowLinkedProcess> getFlowMap() {
    return flowMap;
  }
  
  public static ArrayList<ProcessStep> getWorkflows() {
    return workflows;
  }
}
