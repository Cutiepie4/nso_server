/*    */ package com.kitakeyos.data;
/*    */ 
/*    */ import com.kitakeyos.object.Skill;
/*    */ import com.kitakeyos.template.SkillOptionTemplate;
/*    */ import com.kitakeyos.template.SkillTemplate;
/*    */ import java.util.HashMap;
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
/*    */ public class SkillData
/*    */ {
/*    */   public static HashMap<Integer, NClass> nClasss;
/*    */   public static HashMap<Integer, SkillOptionTemplate> optionTemplates;
/*    */   
/*    */   public static Skill getSkill(int classId, int templateId, int point) {
/* 23 */     NClass n = nClasss.get(Integer.valueOf(classId));
/* 24 */     if (n != null) {
/* 25 */       SkillTemplate tem = n.templates.get(Integer.valueOf(templateId));
/* 26 */       if (tem != null) {
/* 27 */         for (Skill skill : tem.skills) {
/* 28 */           if (skill.point == point) {
/* 29 */             return skill;
/*    */           }
/*    */         } 
/*    */       }
/*    */     } 
/* 34 */     return null;
/*    */   }
/*    */   
/*    */   public static SkillTemplate getTemplate(int classId, int templateId) {
/* 38 */     NClass n = nClasss.get(Integer.valueOf(classId));
/* 39 */     if (n != null) {
/* 40 */       SkillTemplate tem = n.templates.get(Integer.valueOf(templateId));
/* 41 */       if (tem != null) {
/* 42 */         return tem;
/*    */       }
/*    */     } 
/*    */     
/* 46 */     return null;
/*    */   }
/*    */   
/*    */   public static class NClass {
/*    */     public int classId;
/*    */     public String name;
/*    */     public HashMap<Integer, SkillTemplate> templates;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\data\SkillData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */