package com.adaptris.profiler.aspects;

import java.util.Map;

import com.adaptris.core.Adapter;
import com.adaptris.core.AdaptrisComponent;
import com.adaptris.core.AdaptrisMessageConsumerImp;
import com.adaptris.core.AdaptrisMessageProducer;
import com.adaptris.core.Channel;
import com.adaptris.core.CoreException;
import com.adaptris.core.DefaultSerializableMessageTranslator;
import com.adaptris.core.SerializableAdaptrisMessage;
import com.adaptris.core.SerializableMessageTranslator;
import com.adaptris.core.Service;
import com.adaptris.core.ServiceCollection;
import com.adaptris.core.WorkflowImp;
import com.adaptris.core.jms.JmsConnection;

public class InterlokComponent {
  
  private transient SerializableMessageTranslator translator = new DefaultSerializableMessageTranslator();

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

  public InterlokComponent() {

  }

  public InterlokComponent build(Object object, Map<String, WorkflowImp> serviceWorkflowMap, Adapter myAdapter, SerializableAdaptrisMessage serializedMsg) {
    setUniqueId(((AdaptrisComponent) object).getUniqueId());
    setClassName(((AdaptrisComponent) object).getClass().getName());

    if(object instanceof AdaptrisMessageProducer) {
      setComponentType(ComponentType.Producer);
      if(isJms()) {
        setVendorImp(((AdaptrisMessageProducer)object).retrieveConnection(JmsConnection.class).getVendorImplementation().getClass().getName());
      }
      try {
        setDestination(((AdaptrisMessageProducer) object).getDestination().getDestination(translator.translate(serializedMsg)));
      } catch (CoreException e) {
        e.printStackTrace();
      }
      WorkflowImp workflowImp = serviceWorkflowMap.get(getUniqueId());
      if(workflowImp != null) {
        setParent(new InterlokComponent().build(workflowImp, null, myAdapter, serializedMsg));
      }
    } else if(object instanceof AdaptrisMessageConsumerImp) {
      setComponentType(ComponentType.Consumer);
      setDestination(((AdaptrisMessageConsumerImp) object).getDestination().getDestination());
      if(isJms()) {
        setVendorImp(((AdaptrisMessageConsumerImp)object).retrieveConnection(JmsConnection.class).getVendorImplementation().getClass().getName());
      }
      WorkflowImp workflowImp = (WorkflowImp) ((AdaptrisMessageConsumerImp) object).retrieveAdaptrisMessageListener();
      if(workflowImp != null) {
        setParent(new InterlokComponent().build(workflowImp, null, myAdapter, serializedMsg));
      }
    } else if(object instanceof ServiceCollection) {
      setComponentType(ComponentType.ServiceList);
      WorkflowImp workflowImp = serviceWorkflowMap.get(getUniqueId());
      if(workflowImp != null) {
        setParent(new InterlokComponent().build(workflowImp, null, myAdapter, serializedMsg));
      }
    } else if(object instanceof Service) {
      setComponentType(ComponentType.Service);
      WorkflowImp workflowImp = serviceWorkflowMap.get(getUniqueId());
      if(workflowImp != null) {
        setParent(new InterlokComponent().build(workflowImp, null, myAdapter, serializedMsg));
      }
    } else if(object instanceof WorkflowImp) {
      setComponentType(ComponentType.Workflow);
      setParent(new InterlokComponent().build(((WorkflowImp) object).obtainChannel(), null, myAdapter, serializedMsg));
    } else if(object instanceof Channel) {
      setComponentType(ComponentType.Channel);
      setParent(new InterlokComponent().build(myAdapter, null, myAdapter, serializedMsg));
    } else if(object instanceof Adapter) {
      setComponentType(ComponentType.Adapter);
    }

    return this;
  }

  private boolean isJms() {
    return getClassName().toLowerCase().contains("jms");
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

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();

    buffer.append("Interlok Component: [");
    buffer.append("ID = " + getUniqueId() + ", ");
    buffer.append("ComponentType = " + getComponentType() + ", ");
    buffer.append("Destination = " + getDestination() + "]");

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
