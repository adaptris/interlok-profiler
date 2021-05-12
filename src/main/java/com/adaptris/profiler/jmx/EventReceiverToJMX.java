package com.adaptris.profiler.jmx;

import static com.adaptris.core.runtime.AdapterComponentMBean.JMX_DOMAIN_NAME;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.util.JmxHelper;
import com.adaptris.profiler.ProcessStep;
import com.adaptris.profiler.client.EventReceiver;

public class EventReceiverToJMX implements EventReceiver {
  
  protected transient Logger log = LoggerFactory.getLogger(this.getClass());
  
  private static final String JMX_OBJECT_NAME = JMX_DOMAIN_NAME + ":type=Profiler,componentType=$1,id=$2";

  private Map<String, TimedThroughputMetricMBean> beanCache;
  
  public EventReceiverToJMX() {
    beanCache = Collections.synchronizedMap(new HashMap<String, TimedThroughputMetricMBean>());
  }
  
  @Override
  public void onEvent(ProcessStep processStep) {
    try {
      TimedThroughputMetricMBean mBean = getOrCreateMBean(processStep);
      
      if(processStep.isFailed())
        mBean.addToFailedMessageCount();
      else
        mBean.addToMessageCount();
      
      mBean.addToAverageNanoseconds(processStep.getTimeTakenNanos());
    } catch (Exception ex) {
      log.warn("Failed to record metric for component {}", processStep.getStepInstanceId(), ex);
    }
  }

  private TimedThroughputMetricMBean getOrCreateMBean(ProcessStep processStep) throws Exception {
    TimedThroughputMetricMBean metricMbean = null;
    metricMbean = beanCache.get(processStep.getWorkflowId() + ":" + processStep.getStepInstanceId());
    
    if(metricMbean == null) {
      metricMbean = new TimedThroughputMetric();
      metricMbean.setUniqueId(processStep.getStepInstanceId());
      metricMbean.setWorkflowId(processStep.getWorkflowId());
      
      beanCache.put(processStep.getWorkflowId() + ":" + processStep.getStepInstanceId(), metricMbean);
      
      JmxHelper.register(
          new ObjectName(JMX_OBJECT_NAME.replace("$1", processStep.getStepType().name().toLowerCase()).replace("$2", processStep.getStepInstanceId())), 
          metricMbean);
    }
    
    return metricMbean;
  }

}
