package com.kitakeyos.io;

public interface IMessageHandler {
  void onMessage(Message paramMessage);
  
  void onConnectionFail();
  
  void onDisconnected();
  
  void onConnectOK();
}


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\io\IMessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */