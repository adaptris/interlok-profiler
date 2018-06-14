package com.adaptris.profiler.aspects;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.Adapter;
import com.adaptris.core.AdaptrisComponent;
import com.adaptris.core.AdaptrisMessageConsumerImp;
import com.adaptris.core.AdaptrisMessageProducer;
import com.adaptris.core.Channel;
import com.adaptris.core.ConfiguredDestination;
import com.adaptris.core.CoreException;
import com.adaptris.core.ProduceDestination;
import com.adaptris.core.Service;
import com.adaptris.core.ServiceCollection;
import com.adaptris.core.WorkflowImp;
import com.adaptris.core.jms.JmsConnection;

public class InterlokComponent {

  protected transient Logger log = LoggerFactory.getLogger(this.getClass());
  
  public enum ComponentType {
    Producer,
    
    Service,
    
    ServiceList,
    
    Workflow,
    
    Channel,
    
    Adapter,
    
    Consumer;
  }
  
  private String uniqueId;
  
  private InterlokComponent parent;
  
  private ComponentType componentType;
  
  private String destination;
  
  private String className;
  
  private String vendorImp;
  
  /**
   * 
   * @param o
   * @param serviceWorkflowMap
   * @param myAdapter
   * @return
   */
  public InterlokComponent build(Object o, Map<String, WorkflowImp> serviceWorkflowMap, Adapter myAdapter) {
    this.setUniqueId(((AdaptrisComponent) o).getUniqueId());
    this.setClassName(((AdaptrisComponent) o).getClass().getName());
    
    log.trace("Building interlok component: {} of type: {}",  this.getUniqueId(), this.getClassName());
    // Handle producer
    if(o instanceof AdaptrisMessageProducer) {
      this.setComponentType(ComponentType.Producer);
      if(this.getClassName().contains("Jms")) {
        this.setVendorImp(((AdaptrisMessageProducer)o).retrieveConnection(JmsConnection.class).getVendorImplementation().getClass().getName());
      }
      try {
        ProduceDestination producerDestination = ((AdaptrisMessageProducer) o).getDestination();
        if (producerDestination != null) {
          if (producerDestination instanceof ConfiguredDestination) {
            this.setDestination(((ConfiguredDestination)producerDestination).getDestination());
          }
          else {
            this.setDestination(producerDestination.getDestination(null));
          }
        }
      } catch (CoreException e) {
        e.printStackTrace();
      }
      WorkflowImp workflowImp = serviceWorkflowMap.get(this.getUniqueId());
      if(workflowImp != null) {
        this.setParent(new InterlokComponent().build(workflowImp, null, myAdapter));
      }
      else {
        log.error("Failed to get service workflow for producer: " + this.getUniqueId());
      }
    }
    // Handle consumer
    else if(o instanceof AdaptrisMessageConsumerImp) {
      this.setComponentType(ComponentType.Consumer);
      this.setDestination(((AdaptrisMessageConsumerImp) o).getDestination().getDestination());
      if(this.getClassName().contains("Jms")) {
        this.setVendorImp(((AdaptrisMessageConsumerImp)o).retrieveConnection(JmsConnection.class).getVendorImplementation().getClass().getName());
      }
      WorkflowImp workflowImp = (WorkflowImp) ((AdaptrisMessageConsumerImp) o).retrieveAdaptrisMessageListener();
      if(workflowImp != null) {
        this.setParent(new InterlokComponent().build(workflowImp, null, myAdapter));
      }
      else {
        log.error("Failed to get workflow for consumer: " + this.getUniqueId());
      }
    }
    // Handler service collection
    else if(o instanceof ServiceCollection) {
      this.setComponentType(ComponentType.ServiceList);
      WorkflowImp workflowImp = getWorkflow(serviceWorkflowMap);
      if(workflowImp != null) {
        this.setParent(new InterlokComponent().build(workflowImp, null, myAdapter));
      }
      else {
        log.error("Failed to get workflow for servicelist: " + this.getUniqueId());
      }
    }
    // Handle Service
    else if(o instanceof Service) {
      this.setComponentType(ComponentType.Service);
      WorkflowImp workflowImp = getWorkflow(serviceWorkflowMap);
      if (workflowImp != null) {
        this.setParent(new InterlokComponent().build(workflowImp, null, myAdapter));
      }
      else {
        log.error("Failed to get workflow for service: " + this.getUniqueId());
      }
    }
    // Handle workflow
    else if(o instanceof WorkflowImp) {
      this.setComponentType(ComponentType.Workflow);
      this.setParent(new InterlokComponent().build(((WorkflowImp) o).obtainChannel(), null, myAdapter));
    }
    // Handle Channel
    else if(o instanceof Channel) {
      this.setComponentType(ComponentType.Channel);
      this.setParent(new InterlokComponent().build(myAdapter, null, myAdapter));
    }
    // Handler adapter
    else if(o instanceof Adapter) {
      this.setComponentType(ComponentType.Adapter);
    }
    
    return this;
  }
  
  private WorkflowImp getWorkflow(Map<String, WorkflowImp> serviceWorkflowMap) {
    WorkflowImp workflowImp = serviceWorkflowMap.get(this.getUniqueId());
    if (workflowImp == null) {
      log.debug("returning dummy workflow for: {}", this.getUniqueId());
      workflowImp = serviceWorkflowMap.get(AdapterAspect.DUMMY_WORKFLOW);
    }
    return workflowImp; 
  }
  
  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public InterlokComponent getParent() {
    return parent;
  }

  public void setParent(InterlokComponent parent) {
    this.parent = parent;
  }

  public ComponentType getComponentType() {
    return componentType;
  }

  public void setComponentType(ComponentType componentType) {
    this.componentType = componentType;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }
  
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    
    buffer.append("Interlok Component: [");
    buffer.append("ID = " + this.getUniqueId() + ", ");
    buffer.append("ComponentType = " + this.getComponentType() + ", ");
    buffer.append("Destination = " + this.getDestination() + "]");
    
    return buffer.toString();
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getVendorImp() {
    return vendorImp;
  }

  public void setVendorImp(String vendorImp) {
    this.vendorImp = vendorImp;
  }

}
