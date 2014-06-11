package com.adaptris.profiler.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adaptris.profiler.MessageProcessStep;
import com.adaptris.profiler.ProcessFlowProcessor;
import com.adaptris.profiler.ProcessStep;

public class ClientConnectionHandler implements Runnable {
  
  protected transient Log log = LogFactory.getLog(this.getClass().getName());
  
  private Socket connection;
  private ProcessFlowProcessor processor;
  
  public ClientConnectionHandler(Socket connection, ProcessFlowProcessor processor) {
    this.connection = connection;
    this.processor = processor;
  }

  @Override
  public void run() {
    try {
      InputStream inputStream = connection.getInputStream();
      ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
      
      ProcessStep step = new MessageProcessStep();
      step = (ProcessStep) objectInputStream.readObject();
      
//      log.debug("Received Adapter event: " + step.toString());
      
      this.processor.registerEvent(step);
    } catch (IOException e) {
      log.error(e);
    } catch (ClassNotFoundException e) {
      log.error(e);
    } finally {
      try {
        this.connection.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
  }

}
