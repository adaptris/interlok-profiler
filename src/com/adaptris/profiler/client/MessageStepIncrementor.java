package com.adaptris.profiler.client;

import java.util.ArrayList;

public class MessageStepIncrementor implements StepIncrementor {

  private static final int MAX_ARRAY_SIZE = 1000;
  
  private static ArrayList<String> messageList = new ArrayList<String>();
  private static ArrayList<Integer> messageStepCount = new ArrayList<Integer>();
    
  @Override
  public synchronized long generate(String messageId) {
    int index = messageList.indexOf(messageId);
    if(index >= 0) {
      messageStepCount.set(index, messageStepCount.get(index) + 1);
      
      return messageStepCount.get(index);
    } else {
      if(messageList.size() >= MAX_ARRAY_SIZE) {
        messageList.remove(0);
        messageStepCount.remove(0);
      }
      messageList.add(messageId);
      messageStepCount.add(1);
      
      return 1;
    }
  }

}
