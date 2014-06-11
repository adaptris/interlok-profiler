package com.adaptris.profiler;

import java.io.OutputStream;

public interface ObjectSerializer {

  public OutputStream serialize(Object object) throws Exception;
  
  public void close() throws Exception;
  
}
