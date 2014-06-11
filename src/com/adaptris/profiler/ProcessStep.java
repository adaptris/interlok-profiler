package com.adaptris.profiler;

import com.adaptris.core.SerializableAdaptrisMessage;

public interface ProcessStep {

  public String getStepName();
  
  public String getStepInstanceId();
  
  public String getMessageId();
  
  public StepType getStepType();
  
  public long getOrder();
  
  public long getTimeTakenMs();
  
  public void setTimeTakenMs(long time);
  
  public long getTimeStarted();

  public SerializableAdaptrisMessage getMessage();
  
}
