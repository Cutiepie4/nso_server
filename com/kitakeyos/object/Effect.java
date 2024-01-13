/*    */ package com.kitakeyos.object;
/*    */ 
/*    */ import com.kitakeyos.template.EffectTemplate;
/*    */ import java.util.ArrayList;
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
/*    */ public class Effect
/*    */ {
/*    */   public static ArrayList<EffectTemplate> effTemplates;
/* 18 */   public final byte EFF_ME = 0;
/* 19 */   public final byte EFF_FRIEND = 1;
/*    */   public int timeStart;
/*    */   public int timeLength;
/*    */   public short param;
/*    */   public EffectTemplate template;
/*    */   
/*    */   public Effect(byte templateId, int timeStart, int timeLength, short param) {
/* 26 */     this.template = effTemplates.get(templateId);
/* 27 */     this.timeStart = timeStart;
/* 28 */     this.timeLength = timeLength;
/* 29 */     this.param = param;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\object\Effect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */