package com.adaptris.profiler.client;

import java.util.List;

import com.adaptris.profiler.InterlokProfilerPlugin;
import com.adaptris.profiler.ProfilerSettings;

public abstract class PluginFactory {

  private static PluginFactory instance;
  
  protected PluginFactory() {
    
  }
  
  public static PluginFactory getInstance() throws Exception {
    if(instance == null) {
      String receiverClass = ProfilerSettings.getProperty("PLUGIN_FACTORY_CLASS");
      instance = (PluginFactory) Class.forName(receiverClass).newInstance();
    }
    
    return instance;
  }
  
  public abstract InterlokProfilerPlugin getPlugin();
  
  public abstract List<EventReceiver> getReceivers();
  
}
