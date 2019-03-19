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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.adaptris.profiler.client.PluginFactory;

@Aspect
public class AdapterAspect extends BaseAspect {


  @Before("execution(* com.adaptris.core.Adapter.init())")
  public synchronized void beforeAdapterInit(JoinPoint jp) throws Exception {
    log.trace("Profiler : Before Adapter Init");
    PluginFactory.getInstance().getPlugin().init(jp.getTarget());
  }
  
  @Before("execution(* com.adaptris.core.Adapter.start())")
  public synchronized void beforeAdapterStart(JoinPoint jp) throws Exception {
    log.trace("Profiler : Before Adapter Start");
    PluginFactory.getInstance().getPlugin().start(jp.getTarget());
  }
  
  @Before("execution(* com.adaptris.core.Adapter.stop())")
  public synchronized void beforeAdapterStop(JoinPoint jp) throws Exception {
    log.trace("Profiler : Before Adapter Stop");
    PluginFactory.getInstance().getPlugin().stop(jp.getTarget());
  }

  @Before("execution(* com.adaptris.core.Adapter.close())")
  public synchronized void beforeAdapterClose(JoinPoint jp) throws Exception {
    log.trace("Profiler : Before Adapter Close");
    PluginFactory.getInstance().getPlugin().close(jp.getTarget());
  }
}
