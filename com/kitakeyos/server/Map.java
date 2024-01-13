/*    */ package com.kitakeyos.server;
/*    */ 
/*    */ import com.kitakeyos.object.TileMap;
/*    */ import java.io.IOException;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
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
/*    */ public class Map
/*    */   implements Runnable
/*    */ {
/*    */   public int mapId;
/* 23 */   private HashMap<Integer, Zone> zones = new HashMap<>();
/*    */   public TileMap tilemap;
/*    */   public static boolean running = true;
/*    */   
/*    */   public Map(int id) {
/* 28 */     this.mapId = id;
/* 29 */     for (TileMap tile : Server.maps) {
/* 30 */       if (tile.id == this.mapId) {
/* 31 */         this.tilemap = tile;
/*    */         break;
/*    */       } 
/*    */     } 
/* 35 */     for (int i = 0; i < 30; i++) {
/* 36 */       this.zones.put(Integer.valueOf(i), new Zone((byte)i, this.tilemap));
/*    */     }
/* 38 */     (new Thread(this)).start();
/*    */   }
/*    */   
/*    */   public Collection<Zone> getZones() {
/* 42 */     return this.zones.values();
/*    */   }
/*    */   
/*    */   public Zone getZoneById(int id) {
/* 46 */     return this.zones.get(Integer.valueOf(id));
/*    */   }
/*    */ 
/*    */   
/*    */   public void run() {
/* 51 */     while (running) {
/* 52 */       long l1 = System.currentTimeMillis();
/* 53 */       Collection<Zone> zones = getZones();
/* 54 */       for (Zone zone : zones) {
/* 55 */         zone.update();
/*    */       }
/* 57 */       long l2 = System.currentTimeMillis() - l1;
/* 58 */       if (l2 < 1000L) {
/*    */         try {
/* 60 */           Thread.sleep(1000L - l2);
/* 61 */         } catch (InterruptedException interruptedException) {}
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   public void joinZone(Character pl, byte zoneId) {
/*    */     try {
/* 68 */       Zone z = getZoneById(zoneId);
/* 69 */       if (z != null) {
/* 70 */         pl.map = this;
/* 71 */         pl.zone = z;
/* 72 */         z.put(pl.id, pl);
/* 73 */         Character[] characters = z.getCharacters();
/* 74 */         z.numberCharacter = (byte)characters.length;
/* 75 */         for (Character _char : characters) {
/* 76 */           if (_char != null && _char.user != null && !_char.equals(pl)) {
/* 77 */             _char.user.service.sendCharInfo(pl);
/*    */           }
/*    */         } 
/* 80 */         if (pl.mount[4] != null) {
/* 81 */           pl.user.service.sendMount();
/*    */         }
/*    */       } 
/* 84 */     } catch (Exception e) {
/* 85 */       e.printStackTrace();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void outZone(Character pl) {
/* 90 */     Zone z = pl.zone;
/* 91 */     z.removeCharacter(pl.id);
/* 92 */     Character[] characters = z.getCharacters();
/* 93 */     z.numberCharacter = (byte)characters.length;
/* 94 */     for (Character _char : characters) {
/* 95 */       if (_char != null && !_char.equals(pl))
/*    */         try {
/* 97 */           _char.user.service.outZone(pl.id);
/* 98 */         } catch (IOException ex) {
/* 99 */           Logger.getLogger(MapManager.class.getName()).log(Level.SEVERE, (String)null, ex);
/*    */         }  
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\server\Map.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */