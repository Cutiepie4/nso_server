/*    */ package com.kitakeyos.server;
/*    */ 
/*    */ import com.kitakeyos.util.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NinjaSchool
/*    */ {
/* 11 */   private static Logger logger = new Logger(NinjaSchool.class);
/*    */   
/*    */   public static void main(String[] args) {
/* 14 */     logger.log("Start server!");
/* 15 */     Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
/*    */           {
/*    */             public void run() {
/* 18 */               NinjaSchool.logger.log("Shutdown Server!");
/* 19 */               Server.stop();
/*    */             }
/*    */           }));
/* 22 */     Server.init();
/* 23 */     Server.start();
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\server\NinjaSchool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */