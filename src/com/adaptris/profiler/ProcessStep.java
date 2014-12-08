package com.adaptris.profiler;

import com.adaptris.core.SerializableAdaptrisMessage;

public interface ProcessStep {

  /**
   * Get the name of the step which is generally the classname of the component.
   * 
   * @return the step name.
   */
  public String getStepName();
  
  /**
   * Get the id of the step instance.
   * 
   * @return the instance id; generally the unique id of the component.
   */
  public String getStepInstanceId();
  
  /**
   * Get the message ID associated with the step.
   * 
   * @return the message id.
   */
  public String getMessageId();
  
  /**
   * Get the step type.
   * 
   * @return the step type
   */
  public StepType getStepType();
  
  /**
   * Get the sequence number of this process step.
   * 
   * @return the sequence number; note that this sequence number may be reset under high load and is not guaranteed to preserve the
   *         order.
   */
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

  /**
   * Get a copy of the message associated with the step.
   * 
   * @return the message; note that when processing arbitrarily large messages via {@link com.adaptris.core.lms.FileBackedMessage};
   *         then this may contain no data.
   */
  public SerializableAdaptrisMessage getMessage();
  
}
