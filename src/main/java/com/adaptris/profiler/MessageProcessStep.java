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

import lombok.Getter;
import lombok.Setter;

public class MessageProcessStep implements ProcessStep, Serializable {

  private static final long serialVersionUID = 201310141247L;

  @Getter
  @Setter
  private String messageId;
  @Getter
  @Setter
  private String stepName;
  @Getter
  @Setter
  private String stepInstanceId;
  @Getter
  @Setter
  private String workflowId;
  @Getter
  @Setter
  private StepType stepType;
  @Getter
  @Setter
  private long order;
  @Getter
  @Setter
  private long timeTakenMs;
  @Getter
  @Setter
  private long timeStartedMs;
  @Getter
  @Setter
  private long timeTakenNanos;
  @Getter
  @Setter
  private long timeStartedNanos;
  @Getter
  @Setter
  private boolean failed;

  @Override
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof MessageProcessStep) {
      MessageProcessStep other = (MessageProcessStep) object;
      return getStepInstanceId().equals(other.getStepInstanceId());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return getStepInstanceId().hashCode();
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("MessageId - " + getMessageId() + "\n");
    buffer.append("Event class - " + getStepName() + "\n");
    buffer.append("Class instance - " + getStepInstanceId() + "\n");
    buffer.append("Message processed in (ms): " + getTimeTakenMs());

    return buffer.toString();
  }

}
