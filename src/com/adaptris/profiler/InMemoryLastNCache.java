package com.adaptris.profiler;

import java.util.ArrayList;
import java.util.List;

import com.adaptris.core.SerializableAdaptrisMessage;

public class InMemoryLastNCache implements MessageCache {

  private static final int MAXIMUM_CACHED = 500;
  
  private List<SerializableAdaptrisMessage> messages;
  
  public InMemoryLastNCache() {
    messages = new ArrayList<>();
  }
  
  @Override
  public void addMessage(SerializableAdaptrisMessage message) {
    if(this.messages.size() == MAXIMUM_CACHED)
      this.messages.remove(0);
    messages.add(message);
  }

  @Override
  public int getMessageCount() {
    return messages.size();
  }

  @Override
  public SerializableAdaptrisMessage getMessage(int index) {
    return messages.get(index);
  }

  @Override
  public List<SerializableAdaptrisMessage> getAllMessages() {
    return messages;
  }

}
