package com.adaptris.profiler;

import com.adaptris.core.SerializableAdaptrisMessage;

public interface ProcessStep {

  public String getStepName();
  
  /**
   * Get the id of the step instance.
   * 
   * @return the instance id; generally the unique id of the component.
   */
  public String getStepInstanceId();
  
  public String getMessageId();
  
  /**
   * Get the step type.
   * 
   * @return the step type
   */
  public StepType getStepType();
  
  public long getOrder();
  
  /**
   * Get the time taken to process the step.
   * 
   * @return the time taken; the difference between {@link System#currentTimeMillis()} and {@link #getTimeStarted()}
   */
  public long getTimeTakenMs();
  
  public void setTimeTakenMs(long time);
  
  /**
   * Get when the process step was started.
   * 
   * @return when the process step was started; represented by {@link System#currentTimeMillis()}
   */
  public long getTimeStarted();

  public SerializableAdaptrisMessage getMessage();
  
}
