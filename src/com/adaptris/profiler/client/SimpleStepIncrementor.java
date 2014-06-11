package com.adaptris.profiler.client;

public class SimpleStepIncrementor implements StepIncrementor {

  private static long counter;
  
  @Override
  public synchronized long generate(String messageId) {
    counter ++;
    return counter;
  }

}
