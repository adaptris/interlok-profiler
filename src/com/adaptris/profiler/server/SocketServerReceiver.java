package com.adaptris.profiler.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adaptris.profiler.ProcessFlowProcessor;

public class SocketServerReceiver implements ServerReciever, Runnable {

  protected transient Log log = LogFactory.getLog(this.getClass().getName());
  
  private ServerSocket serverSocket;
  private ServerSocketProperties properties = new ServerSocketProperties();
  private ProcessFlowProcessor processor;

  private boolean isRunning;
  
  @Override
  public void start() {
    processor = new ProcessFlowProcessor();
    try {
      log.debug("Listening on port " + properties.getServerPort());
      serverSocket = new ServerSocket(properties.getServerPort());
      
      isRunning = true;
      Thread processingThread = new Thread(this);
      processingThread.start();
      
    } catch (IOException e) {
      log.error(e);
    }
  }

  @Override
  public void stop() {
    // a bit rubbish, this doesn't actually stop anything.
    isRunning = false;
  }

  @Override
  public void run() {
    while(isRunning) {
      try {
        Socket accepted = serverSocket.accept();
        new Thread(new ClientConnectionHandler(accepted, processor)).start();
      } catch (IOException e) {
        log.error(e);
      }
    }
  }

}
