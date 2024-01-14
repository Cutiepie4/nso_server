package com.kitakeyos.io;

public interface ISession {
	boolean isConnected();

	void setHandler(IMessageHandler paramIMessageHandler);

	void sendMessage(Message paramMessage);

	void close();
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\io
 * \ISession.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */