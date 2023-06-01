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

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreConstants;

@Aspect
public class LoggingContextAspect {

  public static final String MESSAGE_ID_CONTEXT = "messageId";
  public static final String PARENT_ID_CONTEXT = "parentMessageId";

  @Before("call(* com.adaptris.core.Service+.doService(com.adaptris.core.AdaptrisMessage)) "
      + "|| call(* com.adaptris.core.AdaptrisMessageListener+.onAdaptrisMessage(com.adaptris.core.AdaptrisMessage)) "
      + "|| call(* com.adaptris.core.AdaptrisMessageListener+.onAdaptrisMessage(com.adaptris.core.AdaptrisMessage, java.util.function.Consumer)) "
      + "|| call(* com.adaptris.core.AdaptrisMessageListener+.onAdaptrisMessage(com.adaptris.core.AdaptrisMessage, java.util.function.Consumer, java.util.function.Consumer))")
  public synchronized void beforeService(JoinPoint jp) {
    AdaptrisMessage msg = (AdaptrisMessage) jp.getArgs()[0];
    String msgId = msg.getUniqueId();
    String parentId = msg.getMetadataValue(CoreConstants.PARENT_UNIQUE_ID_KEY);
    MDC.put(MESSAGE_ID_CONTEXT, msgId);
    if (StringUtils.isNotEmpty(parentId)) {
      MDC.put(PARENT_ID_CONTEXT, parentId);
    }
  }

  // Before PollerImp#processMessages() we wipe out the diagnostic context
  // Which means the context is cleared for internal threads in the context of AdaptrisPollingConsumer
  @Before("call(* com.adaptris.core.PollerImp+.processMessages())")
  public synchronized void beforeProcessMessages(JoinPoint jp) {
    MDC.remove(MESSAGE_ID_CONTEXT);
    MDC.remove(PARENT_ID_CONTEXT);
  }

}
