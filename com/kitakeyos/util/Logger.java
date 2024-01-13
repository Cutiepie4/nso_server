/*     */ package com.kitakeyos.util;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Logger
/*     */ {
/*     */   public static final int NONE = 0;
/*     */   public static final int ERROR = 1;
/*     */   public static final int INFO = 2;
/*     */   public static final int DEBUG = 3;
/*  41 */   public static final String[] PRIORITY_STRS = new String[] { "NONE", "ERROR", "INFO ", "DEBUG" };
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_CONSOLE_PRIORITY = 2;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_FILE_PRIORITY = 3;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final boolean DEFAULT_SHOW_PRIORITY = false;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String KEY_LOG_CONSOLE_PRIORITY = "log.priority.console";
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String KEY_LOG_FILE_PRIORITY = "log.priority.file";
/*     */ 
/*     */   
/*     */   private static final String KEY_LOG_SHOW_PRIORITY = "log.show.priority";
/*     */ 
/*     */   
/*     */   private static final String LOG_DIRECTORY = "log";
/*     */ 
/*     */   
/*     */   private static final String DONT_LOG = "-1";
/*     */ 
/*     */   
/*     */   protected String className;
/*     */ 
/*     */   
/*     */   protected int consolePriority;
/*     */ 
/*     */   
/*     */   protected int filePriority;
/*     */ 
/*     */   
/*     */   protected boolean showPriority;
/*     */ 
/*     */   
/*     */   private static final String DATE_FORMAT_DIR = "yyyy_MM";
/*     */ 
/*     */   
/*     */   private static final String DATE_FORMAT_FILE = "dd_MMM_yyyy";
/*     */ 
/*     */   
/*     */   private static final String DATE_FORMAT_LOG = "yyyy/MM/dd hh:mm:ss:SSS";
/*     */ 
/*     */   
/*  94 */   private SimpleDateFormat dateFormatDir = null;
/*  95 */   private SimpleDateFormat dateFormatFile = null;
/*  96 */   private SimpleDateFormat dateFormatLog = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Logger(Class loggedClass) {
/* 105 */     String fullClassName = loggedClass.getName();
/* 106 */     this.className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
/*     */ 
/*     */     
/* 109 */     this.dateFormatDir = new SimpleDateFormat("yyyy_MM");
/* 110 */     this.dateFormatFile = new SimpleDateFormat("dd_MMM_yyyy");
/* 111 */     this.dateFormatLog = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss:SSS");
/*     */ 
/*     */     
/* 114 */     initilise();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void error(String method, String message) {
/* 124 */     log(1, method, message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void info(String method, String message) {
/* 135 */     log(2, method, message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void debug(String method, String message) {
/* 145 */     log(3, method, message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(String message) {
/* 154 */     log(2, "-1", message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stacktrace(Exception e) {
/* 163 */     error("Exception", e.getMessage());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void initilise() {
/* 172 */     this.consolePriority = 3;
/* 173 */     this.filePriority = 0;
/* 174 */     this.showPriority = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void log(int logPriority, String method, String message) {
/* 198 */     Calendar calender = Calendar.getInstance();
/* 199 */     Date date = calender.getTime();
/*     */ 
/*     */     
/* 202 */     String timeStamp = this.dateFormatLog.format(date);
/* 203 */     String classMethodStr = "";
/* 204 */     String priorityStr = "";
/*     */     
/* 206 */     if (!method.equals("-1")) {
/* 207 */       classMethodStr = this.className + "." + method + "(): ";
/*     */     }
/*     */ 
/*     */     
/* 211 */     if (this.showPriority && logPriority > 0 && logPriority < 4 && !method.equals("-1")) {
/* 212 */       priorityStr = PRIORITY_STRS[logPriority] + " ";
/*     */     }
/*     */     
/* 215 */     String logMessage = "[" + timeStamp + "] " + priorityStr + classMethodStr + message;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 223 */     if (logPriority <= this.consolePriority) {
/* 224 */       writeToConsole(logMessage);
/*     */     }
/* 226 */     if (logPriority <= this.filePriority) {
/* 227 */       writeToFile(logMessage, date);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeToConsole(String logMessage) {
/* 237 */     System.out.println(logMessage);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeToFile(String logMessage, Date date) {
/* 248 */     createDirectory("log");
/*     */ 
/*     */     
/* 251 */     char PS = File.separatorChar;
/* 252 */     String logDirectory = this.dateFormatDir.format(date);
/* 253 */     String logFilename = "log_" + this.dateFormatFile.format(date) + ".txt";
/*     */ 
/*     */     
/* 256 */     String fullFileName = "log" + PS + logDirectory + PS + logFilename;
/* 257 */     File logFile = new File(fullFileName);
/*     */ 
/*     */     
/* 260 */     createDirectory("log" + PS + logDirectory);
/*     */     
/*     */     try {
/* 263 */       BufferedWriter fileout = null;
/*     */       
/* 265 */       if (logFile.exists()) {
/* 266 */         fileout = new BufferedWriter(new FileWriter(fullFileName, true));
/* 267 */         fileout.newLine();
/*     */       } else {
/*     */         
/* 270 */         fileout = new BufferedWriter(new FileWriter(fullFileName));
/*     */       } 
/*     */ 
/*     */       
/* 274 */       fileout.write(logMessage);
/* 275 */       fileout.close();
/* 276 */     } catch (IOException iOException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void createDirectory(String directory) {
/* 282 */     File logDirectory = new File(directory);
/* 283 */     if (!logDirectory.isDirectory())
/*     */     {
/* 285 */       logDirectory.mkdir();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyo\\util\Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */