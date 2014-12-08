package com.adaptris.profiler;

import java.io.InputStream;
import java.util.Properties;

public class ProfilerSettings {
  
  private static final String PROPERTIES_RESOURCE = "adp-profiler.properties";
  private static final Properties PROPERTIES;
  
  static {
    PROPERTIES = new Properties();
    InputStream in = ProfilerSettings.class.getClassLoader().getResourceAsStream(PROPERTIES_RESOURCE);

    if (in == null)
      throw new RuntimeException("cannot locate resource [" + PROPERTIES_RESOURCE + "] on classpath");

    try {
      PROPERTIES.load(in);
      validate();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void validate() throws Exception {
    // nothing yet
  }

  public static String getProperty(String propertyKey) {
    return PROPERTIES.getProperty(propertyKey);
  }
  
}
