/*    */ package com.kitakeyos.db;
/*    */ 
/*    */ import com.kitakeyos.util.Logger;
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Connect
/*    */ {
/*    */   public static Connection conn;
/*    */   public static Statement stat;
/* 18 */   private static Logger logger = new Logger(Connect.class);
/*    */   
/*    */   public static synchronized void create() {
/*    */     try {
/* 22 */       Class.forName("com.mysql.jdbc.Driver");
/* 23 */     } catch (ClassNotFoundException e) {
/* 24 */       logger.log("driver mysql not found!");
/* 25 */       System.exit(0);
/*    */     } 
/* 27 */     String url = "jdbc:mysql://localhost:3306/ninja";
/* 28 */     logger.log("MySQL connect: " + url);
/*    */     try {
/* 30 */       conn = DriverManager.getConnection(url, "root", "12345678");
/* 31 */       stat = conn.createStatement();
/* 32 */       logger.log("successful connection");
/* 33 */     } catch (SQLException e) {
/* 34 */       logger.debug("create", e.toString());
/* 35 */       System.exit(0);
/*    */     } 
/*    */   }
/*    */   
/*    */   public static synchronized boolean close() {
/* 40 */     logger.log("Close connection to database");
/*    */     try {
/* 42 */       if (stat != null) {
/* 43 */         stat.close();
/*    */       }
/* 45 */       if (conn != null) {
/* 46 */         conn.close();
/*    */       }
/* 48 */       return true;
/* 49 */     } catch (SQLException e) {
/* 50 */       logger.debug("close", e.toString());
/* 51 */       return false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\db\Connect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */