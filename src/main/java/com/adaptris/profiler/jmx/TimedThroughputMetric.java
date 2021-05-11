package com.adaptris.profiler.jmx;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import lombok.Getter;
import lombok.Setter;

public class TimedThroughputMetric implements TimedThroughputMetricMBean {

  @Getter
  @Setter
  private String uniqueId;
  
  @Getter
  @Setter
  private String workflowId;

  @Getter
  @Setter
  private long messageCount, failedMessageCount;
  
  private DescriptiveStatistics averageNanos = new DescriptiveStatistics(100);

  @Override
  public void addToMessageCount() {
    messageCount ++;
  }

  @Override
  public void addToFailedMessageCount() {
    failedMessageCount ++;
  }

  @Override
  public void addToAverageNanoseconds(long nanoseconds) {
    averageNanos.addValue(nanoseconds);
  }

  @Override
  public long getAverageNanoseconds() {
    return Double.valueOf(averageNanos.getMean()).longValue();
  }
}
