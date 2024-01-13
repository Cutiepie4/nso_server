/*    */ package com.kitakeyos.option;
/*    */ 
/*    */ import com.kitakeyos.data.SkillData;
/*    */ import com.kitakeyos.template.SkillOptionTemplate;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SkillOption
/*    */ {
/*    */   public int param;
/*    */   public SkillOptionTemplate optionTemplate;
/*    */   
/*    */   public SkillOption(int templateId, int param) {
/* 14 */     this.param = param;
/* 15 */     this.optionTemplate = (SkillOptionTemplate)SkillData.optionTemplates.get(Integer.valueOf(templateId));
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\option\SkillOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */