package com.adaptris.profiler;

import java.util.List;

import com.adaptris.core.SerializableAdaptrisMessage;

public interface MessageCache {

  void addMessage(SerializableAdaptrisMessage message);
  
  int getMessageCount();
  
  SerializableAdaptrisMessage getMessage(int index);
    
  List<SerializableAdaptrisMessage> getAllMessages();
  
}
