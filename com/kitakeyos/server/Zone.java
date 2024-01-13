/*     */ package com.kitakeyos.server;
/*     */ 
/*     */ import com.kitakeyos.io.Message;
/*     */ import com.kitakeyos.object.BuNhin;
/*     */ import com.kitakeyos.object.ItemMap;
/*     */ import com.kitakeyos.object.TileMap;
/*     */ import com.kitakeyos.template.MonsterTemplate;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Zone
/*     */ {
/*  21 */   private HashMap<Integer, Character> mChar = new HashMap<>();
/*  22 */   private HashMap<Short, ItemMap> mItemMap = new HashMap<>();
/*  23 */   private HashMap<Integer, Monster> mMob = new HashMap<>();
/*  24 */   private HashMap<String, BuNhin> mBuNhin = new HashMap<>();
/*  25 */   private ArrayList<ItemMap> listRemoveItem = new ArrayList<>();
/*  26 */   private ArrayList<Integer> listRemoveCharacter = new ArrayList<>();
/*  27 */   public short numberDropItem = 0;
/*     */   public byte zoneId;
/*  29 */   public byte numberCharacter = 0;
/*  30 */   public byte numberGroup = 0;
/*     */   public TileMap tilemap;
/*  32 */   public ArrayList<Monster> waitingListRecoverys = new ArrayList<>();
/*  33 */   public ArrayList<Monster> listRecoverys = new ArrayList<>();
/*     */   
/*     */   public Zone(byte id, TileMap tilemap) {
/*  36 */     this.zoneId = id;
/*  37 */     this.tilemap = tilemap;
/*  38 */     createMonster();
/*     */   }
/*     */   
/*     */   public ItemMap getItemMapById(int id) {
/*  42 */     return this.mItemMap.get(Short.valueOf((short)id));
/*     */   }
/*     */   
/*     */   public void put(int id, ItemMap item) {
/*  46 */     this.mItemMap.put(Short.valueOf((short)id), item);
/*     */   }
/*     */   
/*     */   public Monster getMonsterById(int id) {
/*  50 */     return this.mMob.get(Integer.valueOf(id));
/*     */   }
/*     */   
/*     */   public void put(int id, Monster monster) {
/*  54 */     this.mMob.put(Integer.valueOf(id), monster);
/*     */   }
/*     */   
/*     */   public Character findCharInMap(int id) {
/*  58 */     return this.mChar.get(Integer.valueOf(id));
/*     */   }
/*     */   
/*     */   public void put(int id, Character _char) {
/*  62 */     this.mChar.put(Integer.valueOf(id), _char);
/*     */   }
/*     */   
/*     */   public BuNhin getBuNhinByName(String name) {
/*  66 */     return this.mBuNhin.get(name);
/*     */   }
/*     */   
/*     */   public void put(String name, BuNhin buNhin) {
/*  70 */     this.mBuNhin.put(name, buNhin);
/*     */   }
/*     */   
/*     */   public Character[] getCharacters() {
/*  74 */     return (Character[])this.mChar.values().toArray((Object[])new Character[this.mChar.size()]);
/*     */   }
/*     */   
/*     */   public Monster[] getMonsters() {
/*  78 */     return (Monster[])this.mMob.values().toArray((Object[])new Monster[this.mMob.size()]);
/*     */   }
/*     */   
/*     */   public ItemMap[] getItemMaps() {
/*  82 */     return (ItemMap[])this.mItemMap.values().toArray((Object[])new ItemMap[this.mItemMap.size()]);
/*     */   }
/*     */   
/*     */   public BuNhin[] getBuNhins() {
/*  86 */     return (BuNhin[])this.mBuNhin.values().toArray((Object[])new BuNhin[this.mBuNhin.size()]);
/*     */   }
/*     */   
/*     */   public void sendMessage(Message ms) {
/*  90 */     for (Character pl : this.mChar.values()) {
/*  91 */       if (pl != null) {
/*  92 */         pl.sendMessage(ms);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void removeItem(int id) {
/*  98 */     this.mItemMap.remove(Short.valueOf((short)id));
/*     */   }
/*     */   
/*     */   public void removeCharacter(int id) {
/* 102 */     this.mChar.remove(Integer.valueOf(id));
/*     */   }
/*     */   
/*     */   public void removeBuNhin(String name) {
/* 106 */     this.mBuNhin.remove(name);
/*     */   }
/*     */   
/*     */   public void removeItem() throws IOException {
/* 110 */     for (ItemMap item : this.listRemoveItem) {
/* 111 */       this.mItemMap.remove(Short.valueOf(item.id));
/* 112 */       Message ms = new Message(-15);
/* 113 */       DataOutputStream ds = ms.writer();
/* 114 */       ds.writeShort(item.id);
/* 115 */       ds.flush();
/* 116 */       sendMessage(ms);
/*     */     } 
/* 118 */     this.listRemoveItem.clear();
/*     */   }
/*     */   
/*     */   public void addMob() throws IOException {
/* 122 */     Message mss = new Message(122);
/* 123 */     DataOutputStream ds = mss.writer();
/* 124 */     ds.writeByte(0);
/* 125 */     ds.writeByte(this.listRecoverys.size());
/* 126 */     for (Monster mob : this.listRecoverys) {
/* 127 */       ds.writeByte(mob.mobId);
/* 128 */       ds.writeBoolean(mob.isDisable);
/* 129 */       ds.writeBoolean(mob.isDontMove);
/* 130 */       ds.writeBoolean(mob.isFire);
/* 131 */       ds.writeBoolean(mob.isIce);
/* 132 */       ds.writeBoolean(mob.isWind);
/* 133 */       ds.writeByte(mob.templateId);
/* 134 */       ds.writeByte(mob.sys);
/* 135 */       ds.writeInt(mob.hp);
/* 136 */       ds.writeByte(mob.level);
/* 137 */       ds.writeInt(mob.maxhp);
/* 138 */       ds.writeShort(mob.x);
/* 139 */       ds.writeShort(mob.y);
/* 140 */       ds.writeByte(mob.status);
/* 141 */       ds.writeByte(mob.levelBoss);
/* 142 */       ds.writeBoolean(mob.isBoss);
/* 143 */       this.waitingListRecoverys.remove(mob);
/*     */     } 
/* 145 */     ds.flush();
/* 146 */     sendMessage(mss);
/* 147 */     this.listRecoverys.clear();
/*     */   }
/*     */   
/*     */   public void createMonster() {
/* 151 */     int id = 0;
/* 152 */     for (TileMap.MonsterCoordinate mob : this.tilemap.monsterCoordinates) {
/* 153 */       MonsterTemplate template = Server.mobs.get(mob.templateId);
/*     */ 
/*     */ 
/*     */       
/* 157 */       Monster monster = new Monster(id, mob.templateId, template.hp, template.level, mob.x, mob.y, template.isBoss(), this);
/* 158 */       this.mMob.put(Integer.valueOf(id++), monster);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void update() {
/*     */     try {
/* 164 */       if (this.numberDropItem < 0 || this.numberDropItem >= Short.MAX_VALUE) {
/* 165 */         this.numberDropItem = 0;
/*     */       }
/* 167 */       if (this.mMob.size() > 0) {
/* 168 */         for (Monster mob : this.mMob.values()) {
/* 169 */           mob.update();
/*     */         }
/*     */       }
/* 172 */       if (this.mChar.size() > 0) {
/* 173 */         for (Map.Entry<Integer, Character> entry : this.mChar.entrySet()) {
/* 174 */           if (entry.getValue() == null) {
/* 175 */             this.listRemoveCharacter.add(entry.getKey());
/*     */           }
/*     */         } 
/* 178 */         if (this.listRemoveCharacter.size() > 0) {
/* 179 */           for (Integer key : this.listRemoveCharacter) {
/* 180 */             this.mChar.remove(key);
/*     */           }
/* 182 */           this.listRemoveCharacter.clear();
/*     */         } 
/*     */       } 
/* 185 */       if (this.mItemMap.size() > 0) {
/* 186 */         for (ItemMap item : this.mItemMap.values()) {
/* 187 */           if (item != null) {
/* 188 */             item.timeCount--;
/* 189 */             if (item.timeCount <= 0) {
/* 190 */               this.listRemoveItem.add(item);
/*     */             }
/*     */           } 
/*     */         } 
/* 194 */         if (this.listRemoveItem.size() > 0) {
/* 195 */           removeItem();
/*     */         }
/*     */       } 
/* 198 */       if (this.waitingListRecoverys.size() > 0) {
/* 199 */         for (Monster mob : this.waitingListRecoverys) {
/* 200 */           if (mob != null) {
/* 201 */             mob.recoveryTimeCount--;
/* 202 */             if (mob.recoveryTimeCount <= 0) {
/* 203 */               mob.recovery();
/* 204 */               this.listRecoverys.add(mob);
/*     */             } 
/*     */           } 
/*     */         } 
/* 208 */         if (this.listRecoverys.size() > 0) {
/* 209 */           addMob();
/*     */         }
/*     */       } 
/* 212 */       if (this.mChar.size() > 0) {
/* 213 */         for (Character _char : this.mChar.values()) {
/* 214 */           _char.update();
/*     */         }
/*     */       }
/* 217 */     } catch (IOException ex) {
/* 218 */       Logger.getLogger(Zone.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\server\Zone.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */