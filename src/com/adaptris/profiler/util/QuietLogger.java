package com.adaptris.profiler.util;

import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessage.Kind;
import org.aspectj.bridge.IMessageHandler;

public class QuietLogger implements IMessageHandler {

  private final boolean stderrLog = Boolean.getBoolean("adp.profiler.debug");

  @Override
  public boolean handleMessage(IMessage message) throws AbortException {
    if (message.getKind().compareTo(IMessage.FAIL) >= 0) {
      throw new AbortException(message);
    }
    if (stderrLog) {
      return IMessageHandler.SYSTEM_ERR.handleMessage(message);
    }
    return true;
  }

  @Override
  public boolean isIgnoring(Kind kind) {
    return false;
  }

  @Override
  public void dontIgnore(Kind kind) {
  }

  @Override
  public void ignore(Kind kind) {
  }

}
