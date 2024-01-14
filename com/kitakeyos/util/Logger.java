package com.kitakeyos.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Logger {
	public static final int NONE = 0;
	public static final int ERROR = 1;
	public static final int INFO = 2;
	public static final int DEBUG = 3;
	public static final String[] PRIORITY_STRS = new String[] { "NONE", "ERROR", "INFO ", "DEBUG" };

	public static final int DEFAULT_CONSOLE_PRIORITY = 2;

	public static final int DEFAULT_FILE_PRIORITY = 3;

	public static final boolean DEFAULT_SHOW_PRIORITY = false;

	private static final String KEY_LOG_CONSOLE_PRIORITY = "log.priority.console";

	private static final String KEY_LOG_FILE_PRIORITY = "log.priority.file";

	private static final String KEY_LOG_SHOW_PRIORITY = "log.show.priority";

	private static final String LOG_DIRECTORY = "log";

	private static final String DONT_LOG = "-1";

	protected String className;

	protected int consolePriority;

	protected int filePriority;

	protected boolean showPriority;

	private static final String DATE_FORMAT_DIR = "yyyy_MM";

	private static final String DATE_FORMAT_FILE = "dd_MMM_yyyy";

	private static final String DATE_FORMAT_LOG = "yyyy/MM/dd hh:mm:ss:SSS";

	private SimpleDateFormat dateFormatDir = null;
	private SimpleDateFormat dateFormatFile = null;
	private SimpleDateFormat dateFormatLog = null;

	public Logger(Class loggedClass) {
		String fullClassName = loggedClass.getName();
		this.className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);

		this.dateFormatDir = new SimpleDateFormat("yyyy_MM");
		this.dateFormatFile = new SimpleDateFormat("dd_MMM_yyyy");
		this.dateFormatLog = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss:SSS");

		initilise();
	}

	public void error(String method, String message) {
		log(1, method, message);
	}

	public void info(String method, String message) {
		log(2, method, message);
	}

	public void debug(String method, String message) {
		log(3, method, message);
	}

	public void log(String message) {
		log(2, "-1", message);
	}

	public void stacktrace(Exception e) {
		error("Exception", e.getMessage());
	}

	private void initilise() {
		this.consolePriority = 3;
		this.filePriority = 0;
		this.showPriority = false;
	}

	private void log(int logPriority, String method, String message) {
		Calendar calender = Calendar.getInstance();
		Date date = calender.getTime();

		String timeStamp = this.dateFormatLog.format(date);
		String classMethodStr = "";
		String priorityStr = "";

		if (!method.equals("-1")) {
			classMethodStr = this.className + "." + method + "(): ";
		}

		if (this.showPriority && logPriority > 0 && logPriority < 4 && !method.equals("-1")) {
			priorityStr = PRIORITY_STRS[logPriority] + " ";
		}

		String logMessage = "[" + timeStamp + "] " + priorityStr + classMethodStr + message;

		if (logPriority <= this.consolePriority) {
			writeToConsole(logMessage);
		}
		if (logPriority <= this.filePriority) {
			writeToFile(logMessage, date);
		}
	}

	private void writeToConsole(String logMessage) {
		System.out.println(logMessage);
	}

	private void writeToFile(String logMessage, Date date) {
		createDirectory("log");

		char PS = File.separatorChar;
		String logDirectory = this.dateFormatDir.format(date);
		String logFilename = "log_" + this.dateFormatFile.format(date) + ".txt";

		String fullFileName = "log" + PS + logDirectory + PS + logFilename;
		File logFile = new File(fullFileName);

		createDirectory("log" + PS + logDirectory);

		try {
			BufferedWriter fileout = null;

			if (logFile.exists()) {
				fileout = new BufferedWriter(new FileWriter(fullFileName, true));
				fileout.newLine();
			} else {

				fileout = new BufferedWriter(new FileWriter(fullFileName));
			}

			fileout.write(logMessage);
			fileout.close();
		} catch (IOException iOException) {
		}
	}

	public static void createDirectory(String directory) {
		File logDirectory = new File(directory);
		if (!logDirectory.isDirectory()) {
			logDirectory.mkdir();
		}
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyo\\
 * util\Logger.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */