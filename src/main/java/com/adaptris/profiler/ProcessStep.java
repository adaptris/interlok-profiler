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
  String getStepName();
  
  /**
   * Get the id of the step instance.
   * 
   * @return the instance id; generally the unique id of the component.
   */
  String getStepInstanceId();
  
  /**
   * Get the message ID associated with the step.
   * 
   * @return the message id.
   */
  String getMessageId();
  
  /**
   * Get the step type.
   * 
   * @return the step type
   */
  StepType getStepType();
  
  /**
   * Get the sequence number of this process step.
   * 
   * @return the sequence number; note that this sequence number may be reset under high load and is not guaranteed to preserve the
   *         order.
   */
  long getOrder();
  
  /**
   * Get the time taken to process the step.
   * 
   * @return the time taken; the difference between {@link System#currentTimeMillis()} and {@link #getTimeStarted()}
   */
  long getTimeTakenMs();
  
  void setTimeTakenMs(long time);
  
  /**
   * Get the time taken to process the step in nanoseconds.
   * 
   * @return the time taken; the difference between {@link System#nanoTime()} and {@link #getTimeStartedNanos()}
   */
  long getTimeTakenNanos();
  
  void setTimeTakenNanos(long time);
  
  /**
   * Get when the process step was started.
   * 
   * @return when the process step was started; represented by {@link System#currentTimeMillis()}
   */
  long getTimeStarted();
  
  void setTimeStarted(long time);
  
  /**
   * Get when the process step was started.
   * 
   * @return when the process step was started; represented by {@link System#nanoTime()}
   */
  long getTimeStartedNanos();
  
  void setTimeStartedNanos(long time);
  
}
