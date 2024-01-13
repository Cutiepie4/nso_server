/*    */ package com.kitakeyos.data;
/*    */ 
/*    */ import com.kitakeyos.object.ItemEntry;
/*    */ import com.kitakeyos.template.ItemOptionTemplate;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public class ItemData
/*    */ {
/* 10 */   private static HashMap<Integer, ItemEntry> entrys = new HashMap<>();
/* 11 */   private static HashMap<Integer, ItemOptionTemplate> options = new HashMap<>();
/*    */   
/*    */   public static ItemEntry getItemEntryById(int id) {
/* 14 */     return entrys.get(Integer.valueOf(id));
/*    */   }
/*    */   
/*    */   public static void put(int id, ItemEntry entry) {
/* 18 */     entrys.put(Integer.valueOf(id), entry);
/*    */   }
/*    */   
/*    */   public static Collection<ItemEntry> getEntrys() {
/* 22 */     return entrys.values();
/*    */   }
/*    */   
/*    */   public static ItemOptionTemplate getItemOptionById(int id) {
/* 26 */     return options.get(Integer.valueOf(id));
/*    */   }
/*    */   
/*    */   public static void put(int id, ItemOptionTemplate option) {
/* 30 */     options.put(Integer.valueOf(id), option);
/*    */   }
/*    */   
/*    */   public static Collection<ItemOptionTemplate> getOptions() {
/* 34 */     return options.values();
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\data\ItemData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */