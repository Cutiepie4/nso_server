/*     */ package com.kitakeyos.server;
/*     */ 
/*     */ import com.kitakeyos.io.Message;
/*     */ import com.kitakeyos.object.ItemMap;
/*     */ import com.kitakeyos.util.NinjaUtil;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Monster
/*     */ {
/*     */   public int mobId;
/*     */   public boolean isDisable;
/*     */   public boolean isDontMove;
/*     */   public boolean isFire;
/*     */   public boolean isIce;
/*     */   public boolean isWind;
/*     */   public byte sys;
/*     */   public short templateId;
/*     */   public int hp;
/*     */   public int maxhp;
/*     */   public int originalHp;
/*     */   public short level;
/*     */   public short x;
/*     */   public short y;
/*     */   public byte status;
/*     */   public byte levelBoss;
/*     */   public boolean isBoss;
/*     */   public long lastTimeAttack;
/*  43 */   public long attackDelay = 2000L;
/*     */   
/*     */   public int recoveryTimeCount;
/*     */   
/*     */   public HashMap<Integer, Character> characters;
/*     */   
/*     */   private boolean isDead;
/*  50 */   public static final int[] LIST_ITEM_BOSS = new int[] { 253, 253, 9, 253, 252, 252, 12, 12, 12, 12, 11, 11, 11, 10, 10, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 5 }; public int dame; public int dame2; public Zone zone; public boolean isBusyAttackSokeOne;
/*     */   
/*     */   public Monster(int id, short templateId, int hp, short level, short x, short y, boolean isBoss, Zone zone) {
/*  53 */     this.mobId = id;
/*  54 */     this.templateId = templateId;
/*  55 */     this.originalHp = hp;
/*  56 */     this.level = level;
/*  57 */     this.x = x;
/*  58 */     this.y = y;
/*  59 */     this.isDisable = false;
/*  60 */     this.isDontMove = false;
/*  61 */     this.status = 5;
/*  62 */     this.isBoss = isBoss;
/*  63 */     this.levelBoss = 0;
/*  64 */     this.isFire = this.isIce = this.isWind = false;
/*  65 */     this.isDead = false;
/*  66 */     this.characters = new HashMap<>();
/*  67 */     this.zone = zone;
/*  68 */     setClass();
/*  69 */     setLevelBoss();
/*  70 */     setHP();
/*  71 */     setDamage();
/*     */   }
/*     */   
/*     */   public void setClass() {
/*  75 */     this.sys = (byte)NinjaUtil.nextInt(1, 3);
/*     */   }
/*     */   
/*     */   public void setLevelBoss() {
/*  79 */     if (NinjaUtil.nextInt(100) < 1 && this.level >= 10 && !this.isBoss) {
/*  80 */       this.levelBoss = 2;
/*  81 */     } else if (NinjaUtil.nextInt(50) < 1 && this.level >= 10 && !this.isBoss) {
/*  82 */       this.levelBoss = 1;
/*     */     } else {
/*  84 */       this.levelBoss = 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setDamage() {
/*  89 */     this.dame = 1000 / (this.level + 10);
/*  90 */     if (this.levelBoss == 1) {
/*  91 */       this.dame *= 2;
/*  92 */     } else if (this.levelBoss == 2) {
/*  93 */       this.dame *= 3;
/*     */     } 
/*  95 */     this.dame2 = this.dame - this.dame / 10;
/*     */   }
/*     */   
/*     */   public void setHP() {
/*  99 */     if (this.levelBoss == 1) {
/* 100 */       this.hp = this.maxhp = this.originalHp * 10;
/* 101 */     } else if (this.levelBoss == 2) {
/* 102 */       this.hp = this.maxhp = this.originalHp * 100;
/*     */     } else {
/* 104 */       this.hp = this.maxhp = this.originalHp;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void recovery() {
/* 109 */     this.isDead = false;
/* 110 */     setClass();
/* 111 */     setLevelBoss();
/* 112 */     setHP();
/* 113 */     setDamage();
/* 114 */     this.status = 5;
/*     */   }
/*     */   
/*     */   public void die() {
/* 118 */     this.hp = 0;
/* 119 */     this.status = 1;
/* 120 */     this.recoveryTimeCount = 5;
/* 121 */     this.isDead = true;
/* 122 */     this.characters.clear();
/*     */   }
/*     */   
/*     */   public void dropItem(Character owner) {
/*     */     try {
/* 127 */       this.zone.numberDropItem = (short)(this.zone.numberDropItem + 1); ItemMap itemMap = new ItemMap(this.zone.numberDropItem);
/* 128 */       itemMap.owner = owner;
/* 129 */       itemMap.x = (short)NinjaUtil.nextInt(this.x - 20, this.x + 20);
/* 130 */       itemMap.y = this.y;
/* 131 */       int rd = NinjaUtil.nextInt(2);
/*     */       
/* 133 */       short[] arId = { 12, 12, 434, 12, 12, 434, 434, 435, 434, 435, 434 };
/* 134 */       int itemId = arId[NinjaUtil.nextInt(arId.length)];
/* 135 */       if (this.isBoss) {
/* 136 */         itemId = LIST_ITEM_BOSS[NinjaUtil.nextInt(LIST_ITEM_BOSS.length)];
/* 137 */       } else if (rd == 0) {
/* 138 */         itemId = this.level / 10;
/* 139 */         itemId = (itemId > 4) ? 4 : itemId;
/*     */       } 
/* 141 */       Character.Item item = new Character.Item(itemId);
/* 142 */       item.isLock = true;
/* 143 */       if (item.id == 12) {
/* 144 */         item.quantity = NinjaUtil.nextInt(this.level * 100, this.level * 200);
/* 145 */         if (this.levelBoss == 1) {
/* 146 */           item.quantity *= 5;
/* 147 */           item.isLock = false;
/* 148 */         } else if (this.levelBoss == 2) {
/* 149 */           item.quantity *= 10;
/* 150 */           item.isLock = false;
/*     */         } 
/*     */       } else {
/* 153 */         item.quantity = 1;
/*     */       } 
/* 155 */       item.expire = -1L;
/* 156 */       itemMap.item = item;
/* 157 */       this.zone.put(itemMap.id, itemMap);
/* 158 */       Message m = new Message(6);
/* 159 */       DataOutputStream ds = m.writer();
/* 160 */       ds.writeShort(itemMap.id);
/* 161 */       ds.writeShort(itemMap.item.id);
/* 162 */       ds.writeShort(itemMap.x);
/* 163 */       ds.writeShort(itemMap.y);
/* 164 */       ds.flush();
/* 165 */       this.zone.sendMessage(m);
/* 166 */       m.cleanup();
/* 167 */     } catch (IOException ex) {
/* 168 */       Logger.getLogger(Monster.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void attack() {
/* 173 */     int i = 0;
/* 174 */     ArrayList<Character> list = new ArrayList<>();
/* 175 */     for (Character _char : this.characters.values()) {
/* 176 */       if (this.zone.findCharInMap(_char.id) != null) {
/* 177 */         int rangeX = NinjaUtil.getRange(this.x, _char.x);
/* 178 */         int rangeY = NinjaUtil.getRange(this.y, _char.y);
/* 179 */         if (rangeX > 200 || rangeY > 200) {
/*     */           continue;
/*     */         }
/* 182 */         list.add(_char);
/*     */       } 
/*     */     } 
/* 185 */     if (list.size() == 0) {
/*     */       return;
/*     */     }
/* 188 */     int rand = NinjaUtil.nextInt(list.size());
/* 189 */     Character pl = list.get(rand);
/* 190 */     if (pl != null && !pl.isDead) {
/*     */       try {
/* 192 */         int dame = NinjaUtil.nextInt(this.dame2, this.dame);
/* 193 */         dame -= pl.dameDown;
/* 194 */         int random = NinjaUtil.nextInt(1000);
/* 195 */         if (random < pl.miss) {
/* 196 */           dame = -1;
/*     */         }
/* 198 */         else if (dame <= 0) {
/* 199 */           dame = 1;
/*     */         } 
/*     */         
/* 202 */         pl.hp -= dame;
/* 203 */         Message ms = new Message(-3);
/* 204 */         DataOutputStream ds = ms.writer();
/* 205 */         ds.writeByte(this.mobId);
/* 206 */         ds.writeInt(dame);
/* 207 */         ds.writeInt(0);
/* 208 */         ds.writeShort(-1);
/* 209 */         ds.writeByte(0);
/* 210 */         ds.writeByte(0);
/* 211 */         ds.flush();
/* 212 */         pl.sendMessage(ms);
/*     */         
/* 214 */         ms = new Message(-2);
/* 215 */         ds = ms.writer();
/* 216 */         ds.writeByte(this.mobId);
/* 217 */         ds.writeInt(pl.id);
/* 218 */         ds.writeInt(pl.hp);
/* 219 */         ds.writeInt(pl.mp);
/* 220 */         ds.writeShort(-1);
/* 221 */         ds.writeByte(0);
/* 222 */         ds.writeByte(0);
/* 223 */         ds.flush();
/* 224 */         Character[] characters = this.zone.getCharacters();
/* 225 */         for (Character _char : characters) {
/* 226 */           if (!_char.equals(pl)) {
/* 227 */             _char.sendMessage(ms);
/*     */           }
/*     */         } 
/* 230 */         if (pl.hp <= 0) {
/* 231 */           pl.die();
/* 232 */           pl.waitToDie();
/*     */         } 
/* 234 */       } catch (IOException ex) {
/* 235 */         Logger.getLogger(Monster.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void update() {
/* 241 */     if (!this.isDead && this.templateId != 0 && this.characters.size() > 0) {
/* 242 */       long now = System.currentTimeMillis();
/* 243 */       if (now - this.lastTimeAttack > this.attackDelay) {
/* 244 */         this.lastTimeAttack = now;
/* 245 */         attack();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\server\Monster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */