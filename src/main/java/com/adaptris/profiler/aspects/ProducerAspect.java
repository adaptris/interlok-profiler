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

package com.adaptris.profiler.aspects;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.profiler.MessageProcessStep;
import com.adaptris.profiler.ProcessStep;
import com.adaptris.profiler.StepType;

@Aspect
public class ProducerAspect extends BaseAspect {

  private static Map<String, ProcessStep> waitingForCompletion = new HashMap<>();

  @Before("call(void produce(com.adaptris.core.AdaptrisMessage, com.adaptris.core.ProduceDestination)) && within(com.adaptris..*)")
  public synchronized void beforeProducer(JoinPoint jp) {
    try {
      AdaptrisMessage message = (AdaptrisMessage) jp.getArgs()[0];
      MessageProcessStep step = createStep(StepType.PRODUCER, jp.getTarget(), message.getUniqueId());
      super.recordEventStartTime(step);
      
      waitingForCompletion.put(generateStepKey(jp), step);
      log("Before Produce", jp);
    }
    catch (Exception e) {
      log.error("", e);
    }
  }

  @After("call(void produce(com.adaptris.core.AdaptrisMessage, com.adaptris.core.ProduceDestination)) && within(com.adaptris..*)")
  public synchronized void afterProducer(JoinPoint jp) {
    String key = generateStepKey(jp);
    ProcessStep step = waitingForCompletion.get(key);
    if (step != null) {
      super.recordEventTimeTaken(step);
      
      waitingForCompletion.remove(key);
      sendEvent(step);
      log("After Produce", jp);
    }
  }
}
