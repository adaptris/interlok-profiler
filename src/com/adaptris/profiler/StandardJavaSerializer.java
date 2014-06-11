package com.adaptris.profiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class StandardJavaSerializer implements ObjectSerializer {

  private ByteArrayOutputStream byteArrayOutputStream;
  private ObjectOutputStream objectOutputStream;
  
  @Override
  public OutputStream serialize(Object object) throws IOException {
    byteArrayOutputStream = new ByteArrayOutputStream();
    objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    
    objectOutputStream.writeObject(object);
    
    return byteArrayOutputStream;
  }
  
  public void close() throws IOException {
    if(objectOutputStream != null)
      objectOutputStream.close();
    if(byteArrayOutputStream != null)
      byteArrayOutputStream.close();
  }

}
