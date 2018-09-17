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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

class MessageStepIncrementor implements StepIncrementor {

  private static final int MAX_ARRAY_SIZE = 1024;
  private static transient Map<String, AtomicLong> messages = new FixedSizeMap<String, AtomicLong>();

  @Override
  public long generate(String messageId) {
    long result;
    AtomicLong current = messages.get(messageId);
    if (current != null) {
      result = current.incrementAndGet();
    }
    else {
      current = new AtomicLong();
      messages.put(messageId, current);
      result = current.incrementAndGet();
    }
    return result;
  }

  private static class FixedSizeMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 2011031601L;

    // Default to access order, so that when removeEldest is triggered we act like a LRU cache.
    public FixedSizeMap() {
      super(16, 0.75f, true);
    }

    @Override
    protected boolean removeEldestEntry(@SuppressWarnings("rawtypes") Map.Entry eldest) {
      return size() > MAX_ARRAY_SIZE;
    }
  }

}
