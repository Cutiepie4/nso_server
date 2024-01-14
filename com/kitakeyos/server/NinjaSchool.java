package com.kitakeyos.server;

import com.kitakeyos.util.Logger;

public class NinjaSchool {
	private static final Logger logger = new Logger(NinjaSchool.class);
	public static void main(String[] args) {
		logger.log("Start server!");
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				logger.log("Shutdown Server!");
				Server.stop();
			}
		}));
		Server.init();
		Server.start();
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * server\NinjaSchool.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.3
 */