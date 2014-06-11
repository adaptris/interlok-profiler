package com.adaptris.profiler.server;

import com.adaptris.profiler.ProfilerSettings;

public class ServerSocketProperties extends ProfilerSettings {

  private static final String SERVER_PORT_KEY = "SERVER_PORT";
  
  public int getServerPort() {
    return Integer.parseInt(super.getProperty(SERVER_PORT_KEY));
  }
}
