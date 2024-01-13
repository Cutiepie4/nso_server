/*    */ package com.kitakeyos.object;
/*    */ 
/*    */ public class Part
/*    */ {
/*    */   public Part(byte type) {
/*  6 */     this.type = type;
/*  7 */     if (type == 0) {
/*  8 */       this.pi = new PartImage[8];
/*    */     }
/* 10 */     if (type == 1) {
/* 11 */       this.pi = new PartImage[18];
/*    */     }
/* 13 */     if (type == 2) {
/* 14 */       this.pi = new PartImage[10];
/*    */     }
/* 16 */     if (type == 3)
/* 17 */       this.pi = new PartImage[2]; 
/*    */   }
/*    */   
/*    */   public byte type;
/*    */   public PartImage[] pi;
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\object\Part.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */