/*    */ package com.kitakeyos.template;
/*    */ 
/*    */ import com.kitakeyos.object.Frame;
/*    */ import com.kitakeyos.object.ImageInfo;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MonsterTemplate
/*    */ {
/*    */   public int id;
/*    */   public String name;
/*    */   public byte type;
/*    */   public int hp;
/*    */   public short level;
/*    */   public byte rangeMove;
/*    */   public byte speed;
/*    */   public ImageInfo[] imgInfo;
/*    */   public Frame[] frameBoss;
/*    */   public byte[] frameBossMove;
/*    */   public byte[][] frameBossAttack;
/*    */   public byte numberImage;
/*    */   public byte typeFly;
/*    */   
/*    */   public boolean isBoss() {
/* 32 */     if ((this.id >= 114 && this.id <= 116) || (this.id >= 138 && this.id <= 141) || this.id == 144 || (this.id >= 160 && this.id <= 167) || (this.id >= 198 && this.id <= 204) || (this.id >= 209 && this.id <= 218) || (this.id >= 220 && this.id <= 234))
/*    */     {
/* 34 */       return true;
/*    */     }
/* 36 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\template\MonsterTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */