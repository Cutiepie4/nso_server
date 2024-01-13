/*    */ package com.kitakeyos.io;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class Message
/*    */ {
/*    */   private byte command;
/*    */   private ByteArrayOutputStream os;
/*    */   private DataOutputStream dos;
/*    */   private ByteArrayInputStream is;
/*    */   public DataInputStream dis;
/*    */   
/*    */   public Message(int command) {
/* 18 */     this((byte)command);
/*    */   }
/*    */   
/*    */   public Message(byte command) {
/* 22 */     this.command = command;
/* 23 */     this.os = new ByteArrayOutputStream();
/* 24 */     this.dos = new DataOutputStream(this.os);
/*    */   }
/*    */   
/*    */   public Message(byte command, byte[] data) {
/* 28 */     this.command = command;
/* 29 */     this.is = new ByteArrayInputStream(data);
/* 30 */     this.dis = new DataInputStream(this.is);
/*    */   }
/*    */   
/*    */   public byte getCommand() {
/* 34 */     return this.command;
/*    */   }
/*    */   
/*    */   public void setCommand(int cmd) {
/* 38 */     setCommand((byte)cmd);
/*    */   }
/*    */   
/*    */   public void setCommand(byte cmd) {
/* 42 */     this.command = cmd;
/*    */   }
/*    */   
/*    */   public byte[] getData() {
/* 46 */     return this.os.toByteArray();
/*    */   }
/*    */   
/*    */   public DataInputStream reader() {
/* 50 */     return this.dis;
/*    */   }
/*    */ 
/*    */   
/*    */   public DataOutputStream writer() {
/* 55 */     return this.dos;
/*    */   }
/*    */   
/*    */   public void cleanup() {
/*    */     try {
/* 60 */       if (this.dis != null)
/* 61 */         this.dis.close(); 
/* 62 */       if (this.dos != null)
/* 63 */         this.dos.close(); 
/* 64 */     } catch (IOException iOException) {}
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\io\Message.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */