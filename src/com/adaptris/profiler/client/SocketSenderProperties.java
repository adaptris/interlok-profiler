package com.adaptris.profiler.client;

import com.adaptris.profiler.ProfilerSettings;

public class SocketSenderProperties extends ProfilerSettings {
  
  private static final String SERVER_HOST_KEY = "SERVER_HOST";
  private static final String SERVER_PORT_KEY = "SERVER_PORT";
  
  public String getServerHost() {
    return super.getProperty(SERVER_HOST_KEY);
  }
  
  public int getServerPort() {
    return Integer.parseInt(super.getProperty(SERVER_PORT_KEY));
  }

}
