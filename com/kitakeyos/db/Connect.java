package com.kitakeyos.db;

import com.kitakeyos.server.Config;
import com.kitakeyos.util.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect {
	public static Connection conn;
	public static Statement stat;
	private static Logger logger = new Logger(Connect.class);

	public static synchronized void create() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			logger.log("driver mysql not found!");
			System.exit(0);
		}
		String url = "jdbc:mysql://localhost:3306/nso_db";
		logger.log("MySQL connect: " + url);
		try {
			conn = DriverManager.getConnection(url, Config.MYSQL_USER, Config.MYSQL_PASSWORD);
			stat = conn.createStatement();
			logger.log("successful connection");
		} catch (SQLException e) {
			logger.debug("create", e.toString());
			System.exit(0);
		}
	}

	public static synchronized boolean close() {
		logger.log("Close connection to database");
		try {
			if (stat != null) {
				stat.close();
			}
			if (conn != null) {
				conn.close();
			}
			return true;
		} catch (SQLException e) {
			logger.debug("close", e.toString());
			return false;
		}
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\db
 * \Connect.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */