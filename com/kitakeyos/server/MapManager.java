/*    */ package com.kitakeyos.server;
/*    */ 
/*    */ import com.kitakeyos.object.TileMap;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ 
/*    */ public class MapManager
/*    */ {
/*  9 */   private static HashMap<Integer, Map> maps = new HashMap<>();
/*    */   
/*    */   public static Map getMapById(int id) {
/* 12 */     return maps.get(Integer.valueOf(id));
/*    */   }
/*    */   
/*    */   public static void init() {
/* 16 */     for (TileMap tile : Server.maps) {
/* 17 */       if (tile.maps != null) {
/* 18 */         maps.put(Integer.valueOf(tile.id), new Map(tile.id));
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   public static void joinZone(Character pl, int mapId, byte zoneId) {
/* 24 */     Map m = getMapById(mapId);
/* 25 */     if (m != null) {
/* 26 */       m.joinZone(pl, zoneId);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void outZone(Character pl) {
/* 31 */     if (pl != null && pl.map != null) {
/* 32 */       Map m = getMapById(pl.mapId);
/* 33 */       if (m != null)
/* 34 */         m.outZone(pl); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\server\MapManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */