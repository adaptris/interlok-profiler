package com.adaptris.profiler;

public class ProcessFlowLinkedProcess {

  private String previousStepUniqueId;
  private String nextStepUniqueId;
  private int numMessages;
  private long averageProcessingTime;
  private ProcessStep processStep;
  private MessageCache messageCache;
  
  public ProcessFlowLinkedProcess(){
    messageCache = new InMemoryLastNCache();
  }
  
  public String getPreviousStepUniqueId() {
    return previousStepUniqueId;
  }
  public void setPreviousStepUniqueId(String previousStepUniqueId) {
    this.previousStepUniqueId = previousStepUniqueId;
  }
  public String getNextStepUniqueId() {
    return nextStepUniqueId;
  }
  public void setNextStepUniqueId(String nextStepUniqueId) {
    this.nextStepUniqueId = nextStepUniqueId;
  }
  public ProcessStep getProcessStep() {
    return processStep;
  }
  public void setProcessStep(ProcessStep processStep) {
    this.processStep = processStep;
  }

  public int getNumMessages() {
    return numMessages;
  }

  public void setNumMessages(int numMessages) {
    this.numMessages = numMessages;
  }

  public long getAverageProcessingTime() {
    return averageProcessingTime;
  }

  public void setAverageProcessingTime(long averageProcessingTime) {
    this.averageProcessingTime = averageProcessingTime;
  }

  public MessageCache getMessageCache() {
    return messageCache;
  }

  public void setMessageCache(MessageCache messageCache) {
    this.messageCache = messageCache;
  }
}
