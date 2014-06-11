package com.adaptris.profiler.client;

import com.adaptris.profiler.ObjectSerializer;
import com.adaptris.profiler.ProcessFlowMap;
import com.adaptris.profiler.StandardJavaSerializer;

public class ClientFactory {
  
  private static ClientFactory instance;
  private StepIncrementor stepIncrementor;
  private ClientSender clientSender;
  private ProcessFlowMap processFlowMap;
  
  private ClientFactory() {
    
  }
  
  public static ClientFactory getInstance() {
    if(instance == null) {
      instance = new ClientFactory();
    }
      return instance;
  }
  
  /**
   * FACTORY METHODS
   */
  
  public StepIncrementor getStepIncrementor() {
    if(stepIncrementor == null)
      stepIncrementor = new MessageStepIncrementor();

    return stepIncrementor;
  }
  
  public ClientSender getSender() {
    if(clientSender == null)
      clientSender = new SocketClientSender();

    return clientSender;
  }
  
  public ProcessFlowMap getProcessFlowMap() {
    if(processFlowMap == null)
      processFlowMap = new ProcessFlowMap();
    
    return processFlowMap;
  }

  public ObjectSerializer getSerializer() {
    return new StandardJavaSerializer();
  }
  
  public StepTimer getStepTimer() {
    return new MessageStepTimer();
  }
}
