package com.adaptris.profiler;

import java.io.Serializable;

import com.adaptris.core.SerializableAdaptrisMessage;
import com.adaptris.profiler.aspects.InterlokComponent;

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
  private InterlokComponent interlokComponent;
    
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

  public InterlokComponent getInterlokComponent() {
    return interlokComponent;
  }

  public void setInterlokComponent(InterlokComponent interlokComponent) {
    this.interlokComponent = interlokComponent;
  }

}
