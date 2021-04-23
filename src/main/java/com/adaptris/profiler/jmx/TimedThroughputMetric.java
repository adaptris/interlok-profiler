package com.adaptris.profiler.jmx;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class TimedThroughputMetric implements TimedThroughputMetricMBean {

  private String uniqueId;
  
  private long messageCount, failedMessageCount;
  
  private DescriptiveStatistics averageNanos = new DescriptiveStatistics(100);

  @Override
  public String getUniqueId() {
    return uniqueId;
  }

  @Override
  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  @Override
  public long getMessageCount() {
    return messageCount;
  }

  @Override
  public void addToMessageCount() {
    messageCount ++;
  }

  @Override
  public long getFailedMessageCount() {
    return failedMessageCount;
  }

  @Override
  public void addToFailedMessageCount() {
    failedMessageCount ++;
  }

  @Override
  public long getAverageNanoseconds() {
    return Double.valueOf(averageNanos.getMean()).longValue();
  }

  @Override
  public void addToAverageNanoseconds(long nanoseconds) {
    averageNanos.addValue(nanoseconds);
  }

}
