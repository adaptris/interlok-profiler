package com.adaptris.profiler.server;

public class ServerMain {

  public static void main(String[] args) {
    new ServerMain().start();
  }

  private void start() {
    ServerReciever server = new SocketServerReceiver();
    server.start();
  }
}
