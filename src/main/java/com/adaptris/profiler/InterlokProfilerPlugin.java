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

import com.adaptris.core.ComponentLifecycle;
import com.adaptris.profiler.aspects.AdapterAspect;

/**
 * Interface defining additional lifecycle steps for profiler implementations that require it.
 * <p>
 * These steps will be called during the corresponding {@link AdapterAspect} methods; and allow you to have additional behaviour
 * that happens before the corresponding {@link ComponentLifecycle} methods.
 * </p>
 *
 */
public interface InterlokProfilerPlugin {

  /**
   * Corresponds to {@link ComponentLifecycle#start()}.
   *
   */
  void start(Object object);

  /**
   * Corresponds to {@link ComponentLifecycle#stop()}.
   *
   */
  void stop(Object object);

  /**
   * Corresponds to {@link ComponentLifecycle#init()}.
   *
   */
  void init(Object object);

  /**
   * Corresponds to {@link ComponentLifecycle#close()}.
   *
   */
  void close(Object object);

}
