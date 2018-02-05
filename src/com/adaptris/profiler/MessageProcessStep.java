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

import java.io.Serializable;

import com.adaptris.core.SerializableAdaptrisMessage;

public class MessageProcessStep implements ProcessStep, Serializable {

  private static final long serialVersionUID = 201310141247L;

  private String messageId;
  private String stepName;
  private String stepInstanceId;
  private StepType stepType;
  private long order;
  private long timeTaken;
  private SerializableAdaptrisMessage message;
  private long timeStarted;
    
  public void setTimeTakenMs(long time) {
    this.timeTaken = time;
  }
  
  public long getTimeTakenMs() {
    return timeTaken;
  }
  
  @Override
  public String getStepName() {
    return stepName;
  }

  @Override
  public String getStepInstanceId() {
    return stepInstanceId;
  }

  @Override
  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public void setStepName(String stepName) {
    this.stepName = stepName;
  }

  public void setStepInstanceId(String stepInstanceId) {
    this.stepInstanceId = stepInstanceId;
  }

  public StepType getStepType() {
    return stepType;
  }

  public void setStepType(StepType stepType) {
    this.stepType = stepType;
  }

  public long getOrder() {
    return order;
  }

  public void setOrder(long order) {
    this.order = order;
  }
  
  public SerializableAdaptrisMessage getMessage() {
    return message;
  }

  public void setMessage(SerializableAdaptrisMessage message) {
    this.message = message;
  }

  public boolean equals(Object object) {
    if(object instanceof MessageProcessStep) {
      MessageProcessStep other  = (MessageProcessStep) object;
      return (this.getStepInstanceId().equals(other.getStepInstanceId()));
    } else
      return false;
  }
  
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("MessageId - " + this.getMessageId() + "\n");
    buffer.append("Event class - " + this.getStepName() + "\n");
    buffer.append("Class instance - " + this.getStepInstanceId() + "\n");
    buffer.append("Message processed in (ms): " + this.getTimeTakenMs());
    
    return buffer.toString();
  }

  @Override
  public long getTimeStarted() {
    return timeStarted;
  }
  
  public void setTimeStarted(long timeStarted) {
    this.timeStarted = timeStarted;
  }
}
