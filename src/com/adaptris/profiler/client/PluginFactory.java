package com.adaptris.profiler.client;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.profiler.ProfilerSettings;

/**
 * Factory for managing plugin instances.
 * 
 * @author amcgrath
 * 
 */
public abstract class PluginFactory {

  private static PluginFactory instance;

  private static final PluginFactory noOp = new NoOpFactory();
  private static Logger log = LoggerFactory.getLogger(PluginFactory.class);

  protected PluginFactory() {
  }

  public static PluginFactory getInstance() {
    if (instance == null) {
      String receiverClass = ProfilerSettings.getProperty("com.adaptris.profiler.plugin.factory");
      try {
        instance = (PluginFactory) Class.forName(receiverClass).newInstance();
      }
      catch (Exception e) {
        log.warn("Could not instantiate [{}] as PluginFactory, default to NoOp plugin", receiverClass);
        instance = noOp;
      }
    }
    return instance;
  }

  /**
   * Get the plugin.
   * 
   * @return the client plugin instance that should be used by the aspects.
   */
  public abstract ClientPlugin getPlugin();

  private static class NoOpFactory extends PluginFactory {

    @Override
    public ClientPlugin getPlugin() {
      return new NoOpPlugin();
    }
  }

  private static class NoOpPlugin implements ClientPlugin {

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public List<EventReceiver> getReceivers() {
      return new ArrayList<EventReceiver>();
    }
  }
}
