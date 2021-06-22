package com.adaptris.profiler.jmx;

public interface TimedThroughputMetricMBean {
  
  public String getUniqueId();
  
  public void setWorkflowId(String workflowId);
  
  public String getWorkflowId();
  
  public void setUniqueId(String uniqueId);

  public long getMessageCount();
  
  public void addToMessageCount();
  
  public long getFailedMessageCount();
  
  public void addToFailedMessageCount();
  
  public long getAverageNanoseconds();
  
  public void addToAverageNanoseconds(long nanoseconds);
  
}
