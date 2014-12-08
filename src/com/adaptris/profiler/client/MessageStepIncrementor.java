package com.adaptris.profiler.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MessageStepIncrementor implements StepIncrementor {

  private static final int MAX_ARRAY_SIZE = 1024;
  private static transient Map<String, AtomicLong> messages = new FixedSizeMap<String, AtomicLong>();

  @Override
  public synchronized long generate(String messageId) {
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
    protected boolean removeEldestEntry(Map.Entry eldest) {
      return size() > MAX_ARRAY_SIZE;
    }
  }

}
