package com.adaptris.profiler.client;

import java.util.ArrayList;
import java.util.Date;

public class MessageStepTimer implements StepTimer {

  private static final int MAX_ARRAY_SIZE = 1000;
  
  private static ArrayList<String> keys = new ArrayList<String>();
  private static ArrayList<Long> timings = new ArrayList<Long>();
  
  @Override
  public synchronized long getTime(String messageId, String stepName, String stepId) {
    long now = new Date().getTime();
    int index = keys.indexOf(messageId);
    if(index >= 0) {
      long startTime = timings.get(index);
      keys.remove(index);
      timings.remove(index);
      return now - startTime;
    } else
      return -1;
  }

  @Override
  public synchronized void startTime(String messageId, String stepName, String stepId) {
    if(keys.size() >= MAX_ARRAY_SIZE) {
      keys.remove(0);
      timings.remove(0);
    }
    keys.add(messageId);
    timings.add(new Date().getTime());
  }

}
