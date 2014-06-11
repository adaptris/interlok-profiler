package com.adaptris.profiler.client;

public interface StepTimer {

  public void startTime(String messageId, String stepName, String stepId);
  
  public long getTime(String messageId, String stepName, String stepId);
}
