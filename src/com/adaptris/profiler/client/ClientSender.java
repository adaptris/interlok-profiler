package com.adaptris.profiler.client;

import com.adaptris.profiler.ProcessStep;

public interface ClientSender {

  public void sendProcessStep(ProcessStep step) throws Exception;
  
}
