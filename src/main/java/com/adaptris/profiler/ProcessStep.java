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

package com.adaptris.profiler;

public interface ProcessStep {

  /**
   * Get the name of the step which is generally the classname of the component.
   * 
   * @return the step name.
   */
  public String getStepName();
  
  /**
   * Get the unique-id of the parent workflow.
   * 
   * @return the workflow id.
   */
  public String getWorkflowId();
  
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
   * Get the time taken to process the step in nanoseconds.
   * 
   * @return the time taken; the difference between {@link System#nanoTime()} and {@link #getTimeStartedNanos()}
   */
  public long getTimeTakenNanos();
  
  public void setTimeTakenNanos(long time);
  
  /**
   * Get when the process step was started.
   * 
   * @return when the process step was started; represented by {@link System#currentTimeMillis()}
   */
  public long getTimeStartedMs();
  
  public void setTimeStartedMs(long time);
  
  /**
   * Get when the process step was started.
   * 
   * @return when the process step was started; represented by {@link System#nanoTime()}
   */
  public long getTimeStartedNanos();
  
  public void setTimeStartedNanos(long time);
  
  /**
   * Did this step produce an error?
   * 
   * @return true if the {@link AdaptrisMessage} is deemed to have failed this step.
   */
  public boolean isFailed();
  
  public void setFailed(boolean time);
  
  
}
