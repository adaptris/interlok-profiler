package com.adaptris.profiler.client;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import com.adaptris.profiler.ObjectSerializer;
import com.adaptris.profiler.ProcessStep;

public class SocketClientSender implements ClientSender {

  private SocketSenderProperties properties = new SocketSenderProperties();
  private Socket clientSocket;
  
  @Override
  public void sendProcessStep(ProcessStep step) throws Exception {
    ObjectSerializer serializer = ClientFactory.getInstance().getSerializer();
    OutputStream serialized = serializer.serialize(step);
    
    clientSocket = new Socket(properties.getServerHost(), properties.getServerPort());
    
    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
    writer.print(serialized.toString());
    writer.flush();
    
    writer.close();
    serializer.close();
    clientSocket.close();
  }

}
