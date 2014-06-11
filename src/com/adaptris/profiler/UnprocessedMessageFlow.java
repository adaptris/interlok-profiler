package com.adaptris.profiler;

import java.util.ArrayList;

public class UnprocessedMessageFlow {
  
  private String messageId;
  private ArrayList<ProcessStep> steps;
  
  public UnprocessedMessageFlow () {
    steps = new ArrayList<ProcessStep>();
  }

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public ArrayList<ProcessStep> getSteps() {
    return steps;
  }

  public void setSteps(ArrayList<ProcessStep> steps) {
    this.steps = steps;
  }

  public void addStep(ProcessStep step) {
    this.getSteps().add(step);
  }
}
