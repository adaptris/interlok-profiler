package com.adaptris.profiler.client;

import com.adaptris.profiler.ProcessStep;

public interface EventReceiver {
  
  void onEvent(ProcessStep processStep);

}
