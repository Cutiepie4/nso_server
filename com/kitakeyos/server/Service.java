/*      */ package com.kitakeyos.server;
/*      */ 
/*      */ import com.kitakeyos.data.SkillData;
/*      */ import com.kitakeyos.io.Message;
/*      */ import com.kitakeyos.io.Session;
/*      */ import com.kitakeyos.object.BuNhin;
/*      */ import com.kitakeyos.object.Effect;
/*      */ import com.kitakeyos.object.Frame;
/*      */ import com.kitakeyos.object.Friend;
/*      */ import com.kitakeyos.object.ImageInfo;
/*      */ import com.kitakeyos.object.ItemMap;
/*      */ import com.kitakeyos.object.ItemStore;
/*      */ import com.kitakeyos.object.TileMap;
/*      */ import com.kitakeyos.object.Waypoint;
/*      */ import com.kitakeyos.option.ItemOption;
/*      */ import com.kitakeyos.template.MonsterTemplate;
/*      */ import com.kitakeyos.template.NpcTemplate;
/*      */ import com.kitakeyos.util.NinjaUtil;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Service
/*      */ {
/*      */   private Session client;
/*      */   private Character character;
/*      */   private User user;
/*   31 */   private static byte[][][] item = new byte[4][4000][];
/*   32 */   private static byte[][] cache = new byte[5][];
/*      */   
/*      */   public Service(Session client, User user) {
/*   35 */     this.client = client;
/*   36 */     this.user = user;
/*      */   }
/*      */   
/*      */   public void setChar(Character pl) {
/*   40 */     this.character = pl;
/*      */   }
/*      */   
/*      */   public void showAlert(String title, String text) throws IOException {
/*   44 */     if (title.equals("typemoi")) {
/*      */       return;
/*      */     }
/*   47 */     Message ms = new Message(53);
/*   48 */     DataOutputStream ds = ms.writer();
/*   49 */     ds.writeUTF(title);
/*   50 */     ds.writeUTF(text);
/*   51 */     ds.flush();
/*   52 */     this.client.sendMessage(ms);
/*      */   }
/*      */   
/*      */   public void selectChar() throws IOException {
/*   56 */     Message ms = messageNotMap((byte)-126);
/*   57 */     DataOutputStream ds = ms.writer();
/*   58 */     ds.writeByte(this.user.characters.size());
/*   59 */     for (Character _char : this.user.characters.values()) {
/*   60 */       ds.writeByte(_char.gender);
/*   61 */       ds.writeUTF(_char.name);
/*   62 */       ds.writeUTF(_char.school);
/*   63 */       ds.writeByte(_char.level);
/*   64 */       ds.writeShort(_char.head);
/*   65 */       ds.writeShort(_char.weapon);
/*   66 */       ds.writeShort(_char.body);
/*   67 */       ds.writeShort(_char.leg);
/*      */     } 
/*   69 */     ds.flush();
/*   70 */     this.client.sendMessage(ms);
/*   71 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void sendItemMap() throws IOException {
/*   75 */     Message ms = new Message(117);
/*   76 */     DataOutputStream ds = ms.writer();
/*   77 */     ds.writeByte(0);
/*   78 */     ds.writeByte(0);
/*   79 */     ds.writeByte(0);
/*   80 */     ds.writeByte(0);
/*   81 */     ds.flush();
/*   82 */     this.client.sendMessage(ms);
/*   83 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void addYen(int add) throws IOException {
/*   87 */     Message ms = new Message(-8);
/*   88 */     DataOutputStream ds = ms.writer();
/*   89 */     ds.writeInt(add);
/*   90 */     ds.flush();
/*   91 */     this.client.sendMessage(ms);
/*   92 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void exchangeYenForXu(int xu) throws IOException {
/*   96 */     Message ms = new Message(-7);
/*   97 */     DataOutputStream ds = ms.writer();
/*   98 */     ds.writeInt(xu);
/*   99 */     ds.flush();
/*  100 */     this.client.sendMessage(ms);
/*  101 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void addXu(int xu) throws IOException {
/*  105 */     Message ms = new Message(95);
/*  106 */     DataOutputStream ds = ms.writer();
/*  107 */     ds.writeInt(xu);
/*  108 */     ds.flush();
/*  109 */     this.client.sendMessage(ms);
/*  110 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void update() throws IOException {
/*  114 */     Message ms = new Message(13);
/*  115 */     DataOutputStream ds = ms.writer();
/*  116 */     ds.writeInt(this.character.xu);
/*  117 */     ds.writeInt(this.character.yen);
/*  118 */     ds.writeInt(this.user.luong);
/*  119 */     ds.flush();
/*  120 */     this.client.sendMessage(ms);
/*  121 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void convertUpgrade(Character.Item... item) throws IOException {
/*  125 */     Message ms = messageNotMap((byte)-88);
/*  126 */     DataOutputStream ds = ms.writer();
/*  127 */     ds.writeByte((item[0]).index);
/*  128 */     ds.writeByte((item[0]).upgrade);
/*  129 */     ds.writeByte((item[1]).index);
/*  130 */     ds.writeByte((item[1]).upgrade);
/*  131 */     ds.flush();
/*  132 */     this.client.sendMessage(ms);
/*  133 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void addEffect(Effect effect) throws IOException {
/*  137 */     Message ms = messageSubCommand((byte)-101);
/*  138 */     DataOutputStream ds = ms.writer();
/*  139 */     ds.writeByte(effect.template.id);
/*  140 */     ds.writeInt(effect.timeStart);
/*  141 */     ds.writeInt(effect.timeLength * 1000);
/*  142 */     ds.writeShort(effect.param);
/*  143 */     if (effect.template.type == 14 || effect.template.type == 2 || effect.template.type == 3) {
/*  144 */       ds.writeShort(this.character.x);
/*  145 */       ds.writeShort(this.character.y);
/*      */     } 
/*  147 */     ds.flush();
/*  148 */     this.client.sendMessage(ms);
/*  149 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void removeEffect(Effect effect) throws IOException {
/*  153 */     Message ms = messageSubCommand((byte)-99);
/*  154 */     DataOutputStream ds = ms.writer();
/*  155 */     ds.writeByte(effect.template.id);
/*  156 */     if (effect.template.type == 0 || effect.template.type == 12) {
/*  157 */       ds.writeInt(this.character.hp);
/*  158 */       ds.writeInt(this.character.mp);
/*  159 */     } else if (effect.template.type == 4 || effect.template.type == 13 || effect.template.type == 17) {
/*  160 */       ds.writeInt(this.character.hp);
/*  161 */     } else if (effect.template.type == 23) {
/*  162 */       ds.writeInt(this.character.hp);
/*  163 */       ds.writeInt(this.character.maxHP);
/*      */     } 
/*  165 */     ds.flush();
/*  166 */     this.client.sendMessage(ms);
/*  167 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void replaceEffect(Effect effect) throws IOException {
/*  171 */     Message ms = messageSubCommand((byte)-100);
/*  172 */     DataOutputStream ds = ms.writer();
/*  173 */     ds.writeByte(effect.template.id);
/*  174 */     ds.writeInt(effect.timeStart);
/*  175 */     ds.writeInt(effect.timeLength * 1000);
/*  176 */     ds.writeShort(effect.param);
/*  177 */     ds.flush();
/*  178 */     this.client.sendMessage(ms);
/*  179 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void updateHp() throws IOException {
/*  183 */     Message ms = messageSubCommand((byte)-122);
/*  184 */     DataOutputStream ds = ms.writer();
/*  185 */     ds.writeInt(this.character.hp);
/*  186 */     ds.flush();
/*  187 */     this.client.sendMessage(ms);
/*  188 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void updateMp() throws IOException {
/*  192 */     Message ms = messageSubCommand((byte)-121);
/*  193 */     DataOutputStream ds = ms.writer();
/*  194 */     ds.writeInt(this.character.mp);
/*  195 */     ds.flush();
/*  196 */     this.client.sendMessage(ms);
/*  197 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void sendCharInfoInMap(Character pl) throws IOException {
/*  201 */     Message ms = messageSubCommand((byte)-120);
/*  202 */     DataOutputStream ds = ms.writer();
/*  203 */     ds.writeInt(pl.id);
/*  204 */     ds.writeUTF(pl.clanname);
/*  205 */     if (pl.clanname != "") {
/*  206 */       ds.writeByte(pl.clan);
/*      */     }
/*  208 */     ds.writeBoolean(false);
/*  209 */     ds.writeByte(pl.hieuChien);
/*  210 */     ds.writeByte(pl.classId);
/*  211 */     ds.writeByte(pl.gender);
/*  212 */     ds.writeShort(pl.head);
/*  213 */     ds.writeUTF(pl.name);
/*  214 */     ds.writeInt(pl.hp);
/*  215 */     ds.writeInt(pl.maxHP);
/*  216 */     ds.writeByte(pl.level);
/*  217 */     ds.writeShort(pl.weapon);
/*  218 */     ds.writeShort(pl.body);
/*  219 */     ds.writeShort(pl.leg);
/*  220 */     ds.writeByte(-1);
/*  221 */     ds.writeShort(pl.x);
/*  222 */     ds.writeShort(pl.y);
/*  223 */     ds.writeShort(pl.eff5buffhp);
/*  224 */     ds.writeShort(pl.eff5buffmp);
/*  225 */     ds.writeByte(0);
/*  226 */     ds.writeBoolean(true);
/*  227 */     ds.writeBoolean(false);
/*  228 */     ds.writeShort(pl.head);
/*  229 */     ds.writeShort(pl.weapon);
/*  230 */     ds.writeShort(pl.body);
/*  231 */     ds.writeShort(pl.leg);
/*  232 */     ds.flush();
/*  233 */     this.client.sendMessage(ms);
/*  234 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void sendSkillShortcut(String key, byte[] data) throws IOException {
/*  238 */     Message ms = messageSubCommand((byte)-65);
/*  239 */     DataOutputStream ds = ms.writer();
/*  240 */     ds.writeUTF(key);
/*  241 */     ds.writeInt(data.length);
/*  242 */     ds.write(data);
/*  243 */     ds.flush();
/*  244 */     this.client.sendMessage(ms);
/*  245 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void boxCoinIn(int xu) throws IOException {
/*  249 */     Message ms = messageSubCommand((byte)-105);
/*  250 */     DataOutputStream ds = ms.writer();
/*  251 */     ds.writeInt(xu);
/*  252 */     ds.flush();
/*  253 */     this.client.sendMessage(ms);
/*  254 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void boxCoinOut(int xu) throws IOException {
/*  258 */     Message ms = messageSubCommand((byte)-104);
/*  259 */     DataOutputStream ds = ms.writer();
/*  260 */     ds.writeInt(xu);
/*  261 */     ds.flush();
/*  262 */     this.client.sendMessage(ms);
/*  263 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void learnSkill(byte itemIndex, short skillId) throws IOException {
/*  267 */     Message ms = messageSubCommand((byte)-102);
/*  268 */     DataOutputStream ds = ms.writer();
/*  269 */     ds.writeByte(itemIndex);
/*  270 */     ds.writeShort(skillId);
/*  271 */     ds.flush();
/*  272 */     this.client.sendMessage(ms);
/*  273 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void sendBox() throws IOException {
/*  277 */     Message ms = new Message(31);
/*  278 */     DataOutputStream ds = ms.writer();
/*  279 */     ds.writeInt(this.character.xuInBox);
/*  280 */     ds.writeByte(this.character.numberCellBox);
/*  281 */     for (Character.Item item : this.character.box) {
/*  282 */       if (item != null) {
/*  283 */         ds.writeShort(item.id);
/*  284 */         ds.writeBoolean(item.isLock);
/*  285 */         if (item.entry.isTypeBody() || item.entry.isTypeNgocKham()) {
/*  286 */           ds.writeByte(item.upgrade);
/*      */         }
/*  288 */         ds.writeBoolean((item.expire != -1L));
/*  289 */         ds.writeShort(item.quantity);
/*      */       } else {
/*  291 */         ds.writeShort(-1);
/*      */       } 
/*      */     } 
/*  294 */     ds.flush();
/*  295 */     this.client.sendMessage(ms);
/*  296 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void sendZone() throws IOException {
/*  300 */     Map map = this.character.map;
/*  301 */     TileMap tilemap = map.tilemap;
/*  302 */     Zone zone = this.character.zone;
/*  303 */     Message ms = new Message(-18);
/*  304 */     DataOutputStream ds = ms.writer();
/*  305 */     ds.writeByte(this.character.mapId);
/*  306 */     ds.writeByte(tilemap.tileId);
/*  307 */     ds.writeByte(tilemap.bgId);
/*  308 */     ds.writeByte(tilemap.type);
/*  309 */     ds.writeUTF(tilemap.name);
/*  310 */     ds.writeByte(zone.zoneId);
/*  311 */     ds.writeShort(this.character.x);
/*  312 */     ds.writeShort(this.character.y);
/*  313 */     int len = tilemap.waypoints.size();
/*  314 */     len = (len > 127) ? 127 : len;
/*  315 */     ds.writeByte(len);
/*  316 */     for (int i = 0; i < len; i++) {
/*  317 */       Waypoint waypoint = tilemap.waypoints.get(i);
/*  318 */       ds.writeShort(waypoint.minX);
/*  319 */       ds.writeShort(waypoint.minY);
/*  320 */       ds.writeShort(waypoint.maxX);
/*  321 */       ds.writeShort(waypoint.maxY);
/*      */     } 
/*  323 */     Monster[] monsters = zone.getMonsters();
/*  324 */     len = monsters.length;
/*  325 */     len = (len > 127) ? 127 : len;
/*  326 */     ds.writeByte(len);
/*  327 */     for (int j = 0; j < len; j++) {
/*  328 */       Monster mob = monsters[j];
/*  329 */       ds.writeBoolean(mob.isDisable);
/*  330 */       ds.writeBoolean(mob.isDontMove);
/*  331 */       ds.writeBoolean(mob.isFire);
/*  332 */       ds.writeBoolean(mob.isIce);
/*  333 */       ds.writeBoolean(mob.isWind);
/*  334 */       ds.writeByte(mob.templateId);
/*  335 */       ds.writeByte(mob.sys);
/*  336 */       ds.writeInt(mob.hp);
/*  337 */       ds.writeByte(mob.level);
/*  338 */       ds.writeInt(mob.maxhp);
/*  339 */       ds.writeShort(mob.x);
/*  340 */       ds.writeShort(mob.y);
/*  341 */       ds.writeByte(mob.status);
/*  342 */       ds.writeByte(mob.levelBoss);
/*  343 */       ds.writeBoolean(mob.isBoss);
/*      */     } 
/*  345 */     BuNhin[] buNhins = zone.getBuNhins();
/*  346 */     int num = buNhins.length;
/*  347 */     num = (num > 127) ? 127 : num;
/*  348 */     ds.writeByte(num); int k;
/*  349 */     for (k = 0; k < num; k++) {
/*  350 */       BuNhin buNhin = buNhins[k];
/*  351 */       ds.writeUTF(buNhin.name);
/*  352 */       ds.writeShort(buNhin.x);
/*  353 */       ds.writeShort(buNhin.y);
/*      */     } 
/*  355 */     num = tilemap.npcs.size();
/*  356 */     num = (num > 127) ? 127 : num;
/*  357 */     ds.writeByte(num);
/*  358 */     for (k = 0; k < num; k++) {
/*  359 */       NpcTemplate npc = tilemap.npcs.get(k);
/*  360 */       ds.writeByte(npc.status);
/*  361 */       ds.writeShort(npc.x);
/*  362 */       ds.writeShort(npc.y);
/*  363 */       ds.writeByte(npc.templateId);
/*      */     } 
/*  365 */     ItemMap[] items = zone.getItemMaps();
/*  366 */     num = items.length;
/*  367 */     num = (num > 127) ? 127 : num;
/*  368 */     ds.writeByte(num);
/*  369 */     for (int m = 0; m < num; m++) {
/*  370 */       ItemMap item = items[m];
/*  371 */       ds.writeShort(item.id);
/*  372 */       ds.writeShort(item.item.id);
/*  373 */       ds.writeShort(item.x);
/*  374 */       ds.writeShort(item.y);
/*      */     } 
/*  376 */     ds.writeUTF(tilemap.name);
/*  377 */     ds.writeByte(0);
/*  378 */     ds.flush();
/*  379 */     this.client.sendMessage(ms);
/*  380 */     ms.cleanup();
/*  381 */     sendCharInMap();
/*      */   }
/*      */   
/*      */   public void useMask() throws IOException {
/*  385 */     Message ms = messageSubCommand((byte)-64);
/*  386 */     DataOutputStream ds = ms.writer();
/*  387 */     ds.writeInt(this.character.id);
/*  388 */     ds.writeInt(this.character.hp);
/*  389 */     ds.writeInt(this.character.maxHP);
/*  390 */     ds.writeShort(this.character.eff5buffhp);
/*  391 */     ds.writeShort(this.character.eff5buffmp);
/*  392 */     ds.writeShort(this.character.head);
/*  393 */     ds.flush();
/*  394 */     this.character.sendToMap(ms);
/*      */   }
/*      */   
/*      */   public void usePant() throws IOException {
/*  398 */     Message ms = messageSubCommand((byte)-113);
/*  399 */     DataOutputStream ds = ms.writer();
/*  400 */     ds.writeInt(this.character.id);
/*  401 */     ds.writeInt(this.character.hp);
/*  402 */     ds.writeInt(this.character.maxHP);
/*  403 */     ds.writeShort(this.character.eff5buffhp);
/*  404 */     ds.writeShort(this.character.eff5buffmp);
/*  405 */     ds.writeShort(this.character.leg);
/*  406 */     ds.flush();
/*  407 */     this.character.sendToMap(ms);
/*      */   }
/*      */   
/*      */   public void useShirt() throws IOException {
/*  411 */     Message ms = messageSubCommand((byte)-116);
/*  412 */     DataOutputStream ds = ms.writer();
/*  413 */     ds.writeInt(this.character.id);
/*  414 */     ds.writeInt(this.character.hp);
/*  415 */     ds.writeInt(this.character.maxHP);
/*  416 */     ds.writeShort(this.character.eff5buffhp);
/*  417 */     ds.writeShort(this.character.eff5buffmp);
/*  418 */     ds.writeShort(this.character.body);
/*  419 */     ds.flush();
/*  420 */     this.character.sendToMap(ms);
/*      */   }
/*      */   
/*      */   public void useWeapon() throws IOException {
/*  424 */     Message ms = messageSubCommand((byte)-117);
/*  425 */     DataOutputStream ds = ms.writer();
/*  426 */     ds.writeInt(this.character.id);
/*  427 */     ds.writeInt(this.character.hp);
/*  428 */     ds.writeInt(this.character.maxHP);
/*  429 */     ds.writeShort(this.character.eff5buffhp);
/*  430 */     ds.writeShort(this.character.eff5buffmp);
/*  431 */     ds.writeShort(this.character.weapon);
/*  432 */     ds.flush();
/*  433 */     this.character.sendToMap(ms);
/*      */   }
/*      */   
/*      */   public void sendCharInMap() throws IOException {
/*  437 */     Character[] characters = this.character.zone.getCharacters();
/*  438 */     for (Character _char : characters) {
/*  439 */       if (!this.character.equals(_char)) {
/*  440 */         sendCharInfo(_char);
/*      */       }
/*  442 */       if (_char.mount[4] != null) {
/*  443 */         _char.user.service.sendMount();
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public void itemMountToBag(int index1, int index2) throws IOException {
/*  449 */     Message mss = new Message(108);
/*  450 */     DataOutputStream ds = mss.writer();
/*  451 */     ds.writeByte(this.character.speed);
/*  452 */     ds.writeInt(this.character.maxHP);
/*  453 */     ds.writeInt(this.character.maxMP);
/*  454 */     ds.writeShort(this.character.eff5buffhp);
/*  455 */     ds.writeShort(this.character.eff5buffmp);
/*  456 */     ds.writeByte(index1);
/*  457 */     ds.writeByte(index2);
/*  458 */     ds.flush();
/*  459 */     this.client.sendMessage(mss);
/*      */   }
/*      */   
/*      */   public void sendMount() throws IOException {
/*  463 */     Message ms = messageSubCommand((byte)-54);
/*  464 */     DataOutputStream ds = ms.writer();
/*  465 */     ds.writeInt(this.character.id);
/*  466 */     for (int i = 0; i < 5; i++) {
/*  467 */       if (this.character.mount[i] != null) {
/*  468 */         ds.writeShort((this.character.mount[i]).id);
/*  469 */         ds.writeByte((this.character.mount[i]).level);
/*  470 */         ds.writeLong((this.character.mount[i]).expire);
/*  471 */         ds.writeByte((this.character.mount[i]).sys);
/*  472 */         ds.writeByte((this.character.mount[i]).options.size());
/*  473 */         for (ItemOption option : (this.character.mount[i]).options) {
/*  474 */           ds.writeByte(option.optionTemplate.id);
/*  475 */           ds.writeInt(option.param);
/*      */         } 
/*      */       } else {
/*  478 */         ds.writeShort(-1);
/*      */       } 
/*      */     } 
/*  481 */     ds.flush();
/*  482 */     this.character.sendToMap(ms);
/*  483 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void openUIZone() throws IOException {
/*  487 */     Message ms = new Message(36);
/*  488 */     DataOutputStream ds = ms.writer();
/*  489 */     Map m = this.character.map;
/*  490 */     Collection<Zone> zones = m.getZones();
/*  491 */     ds.writeByte(zones.size());
/*  492 */     for (Zone z : zones) {
/*  493 */       ds.writeByte(z.numberCharacter);
/*  494 */       ds.writeByte(z.numberGroup);
/*      */     } 
/*  496 */     ds.flush();
/*  497 */     this.client.sendMessage(ms);
/*  498 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void sendInfo() throws IOException {
/*  502 */     Message ms = messageSubCommand((byte)-127);
/*  503 */     DataOutputStream ds = ms.writer();
/*  504 */     ds.writeInt(this.character.id);
/*  505 */     ds.writeUTF(this.character.clanname);
/*  506 */     if (this.character.clanname != "") {
/*  507 */       ds.writeByte(this.character.clan);
/*      */     }
/*  509 */     ds.writeByte(40);
/*  510 */     ds.writeByte(this.character.gender);
/*  511 */     ds.writeShort(this.character.head);
/*  512 */     ds.writeByte(this.character.speed);
/*  513 */     ds.writeUTF(this.character.name);
/*  514 */     ds.writeByte(this.character.hieuChien);
/*  515 */     ds.writeByte(this.character.typePk);
/*  516 */     ds.writeInt(this.character.maxHP);
/*  517 */     ds.writeInt(this.character.hp);
/*  518 */     ds.writeInt(this.character.maxMP);
/*  519 */     ds.writeInt(this.character.mp);
/*  520 */     ds.writeLong(this.character.exp);
/*  521 */     ds.writeLong(this.character.expDown);
/*  522 */     ds.writeShort(this.character.eff5buffhp);
/*  523 */     ds.writeShort(this.character.eff5buffmp);
/*  524 */     ds.writeByte(this.character.classId);
/*  525 */     ds.writeShort(this.character.point);
/*  526 */     ds.writeShort(this.character.potential[0]);
/*  527 */     ds.writeShort(this.character.potential[1]);
/*  528 */     ds.writeInt(this.character.potential[2]);
/*  529 */     ds.writeInt(this.character.potential[3]);
/*  530 */     ds.writeShort(this.character.spoint);
/*  531 */     ds.writeByte(this.character.listSkill.size());
/*  532 */     for (Character.MySkill my : this.character.listSkill) {
/*  533 */       ds.writeShort((SkillData.getSkill(this.character.classId, my.id, my.point)).skillId);
/*      */     }
/*  535 */     ds.writeInt(this.character.xu);
/*  536 */     ds.writeInt(this.character.yen);
/*  537 */     ds.writeInt(this.user.luong);
/*  538 */     ds.writeByte(this.character.numberCellBag); int i;
/*  539 */     for (i = 0; i < this.character.numberCellBag; i++) {
/*  540 */       Character.Item item = this.character.bag[i];
/*  541 */       if (item != null) {
/*  542 */         ds.writeShort(item.id);
/*  543 */         ds.writeBoolean(item.isLock);
/*  544 */         if (item.entry.isTypeBody() || item.entry.isTypeNgocKham() || item.entry.isTypeMount()) {
/*  545 */           ds.writeByte(item.upgrade);
/*      */         }
/*  547 */         ds.writeBoolean((item.expire != -1L));
/*  548 */         ds.writeShort(item.quantity);
/*      */       } else {
/*  550 */         ds.writeShort(-1);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  555 */     for (i = 0; i < 16; i++) {
/*  556 */       if (this.character.equiped[i] != null) {
/*  557 */         ds.writeShort((this.character.equiped[i]).id);
/*  558 */         ds.writeByte((this.character.equiped[i]).upgrade);
/*  559 */         ds.writeByte((this.character.equiped[i]).sys);
/*      */       } else {
/*  561 */         ds.writeShort(-1);
/*      */       } 
/*      */     } 
/*  564 */     ds.writeBoolean(true);
/*  565 */     ds.writeBoolean(false);
/*  566 */     ds.writeShort(this.character.head);
/*  567 */     ds.writeShort(this.character.weapon);
/*  568 */     ds.writeShort(this.character.body);
/*  569 */     ds.writeShort(this.character.leg);
/*  570 */     ds.flush();
/*  571 */     this.client.sendMessage(ms);
/*      */   }
/*      */   
/*      */   public void sendCharInfo(Character pl) throws IOException {
/*  575 */     Message ms = new Message(3);
/*  576 */     DataOutputStream ds = ms.writer();
/*  577 */     ds.writeInt(pl.id);
/*  578 */     ds.writeUTF(pl.clanname);
/*  579 */     if (pl.clanname != "") {
/*  580 */       ds.writeByte(pl.clan);
/*      */     }
/*  582 */     ds.writeBoolean(false);
/*  583 */     ds.writeByte(pl.hieuChien);
/*  584 */     ds.writeByte(pl.classId);
/*  585 */     ds.writeByte(pl.gender);
/*  586 */     ds.writeShort(pl.head);
/*  587 */     ds.writeUTF(pl.name);
/*  588 */     ds.writeInt(pl.hp);
/*  589 */     ds.writeInt(pl.maxHP);
/*  590 */     ds.writeByte(pl.level);
/*  591 */     ds.writeShort(pl.weapon);
/*  592 */     ds.writeShort(pl.body);
/*  593 */     ds.writeShort(pl.leg);
/*  594 */     ds.writeByte(-1);
/*  595 */     ds.writeShort(pl.x);
/*  596 */     ds.writeShort(pl.y);
/*  597 */     ds.writeShort(pl.eff5buffhp);
/*  598 */     ds.writeShort(pl.eff5buffmp);
/*  599 */     ds.writeByte(0);
/*  600 */     ds.writeBoolean(true);
/*  601 */     ds.writeBoolean(false);
/*  602 */     ds.writeShort(pl.head);
/*  603 */     ds.writeShort(pl.weapon);
/*  604 */     ds.writeShort(pl.body);
/*  605 */     ds.writeShort(pl.leg);
/*  606 */     ds.flush();
/*  607 */     this.client.sendMessage(ms);
/*  608 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void charUpdateInMap(Character pl) throws IOException {
/*  612 */     Message ms = messageSubCommand((byte)-119);
/*  613 */     DataOutputStream ds = ms.writer();
/*  614 */     ds.writeInt(pl.id);
/*  615 */     ds.writeInt(pl.hp);
/*  616 */     ds.writeInt(pl.maxHP);
/*  617 */     ds.flush();
/*  618 */     this.client.sendMessage(ms);
/*  619 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void changePk() throws IOException {
/*  623 */     Message ms = messageSubCommand((byte)-92);
/*  624 */     DataOutputStream ds = ms.writer();
/*  625 */     ds.writeInt(this.character.id);
/*  626 */     ds.writeByte(this.character.typePk);
/*  627 */     ds.flush();
/*  628 */     this.character.sendToMap(ms);
/*  629 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void openUI(byte typeUI) throws IOException {
/*  633 */     Message ms = new Message(30);
/*  634 */     DataOutputStream ds = ms.writer();
/*  635 */     ds.writeByte(typeUI);
/*  636 */     ds.flush();
/*  637 */     this.client.sendMessage(ms);
/*  638 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void inputDlg(String title, int type) throws IOException {
/*  642 */     Message ms = new Message(92);
/*  643 */     DataOutputStream ds = ms.writer();
/*  644 */     ds.writeUTF(title);
/*  645 */     ds.writeShort(type);
/*  646 */     ds.flush();
/*  647 */     this.client.sendMessage(ms);
/*  648 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void showInfoLuckyDraw(LuckyDraw lucky) throws IOException {
/*  652 */     Message ms = new Message(53);
/*  653 */     DataOutputStream ds = ms.writer();
/*  654 */     ds.writeUTF("typemoi");
/*  655 */     ds.writeUTF(lucky.name);
/*  656 */     ds.writeShort(lucky.timeCount);
/*  657 */     String[] percen = lucky.getPercen(this.client.user.selectedCharacter.id);
/*  658 */     ds.writeUTF(NinjaUtil.getCurrency(lucky.totalMoney) + "Xu");
/*  659 */     ds.writeShort(Short.parseShort(percen[0]));
/*  660 */     ds.writeUTF(percen[1]);
/*  661 */     ds.writeShort(lucky.mem.size());
/*  662 */     if (!lucky.nameWin.equals("")) {
/*  663 */       ds.writeUTF("Người vừa chiến thắng:" + NinjaUtil.getColor("tahoma_7b_blue") + lucky.nameWin + "\nSố xu thắng: " + NinjaUtil.getCurrency(lucky.xuWin) + "Xu \nSố xu tham gia: " + NinjaUtil.getCurrency(lucky.xuThamGia) + "Xu");
/*      */     } else {
/*  665 */       ds.writeUTF("Chưa có thông tin!");
/*      */     } 
/*  667 */     ds.writeByte(lucky.type);
/*  668 */     ds.writeUTF(NinjaUtil.getCurrency(lucky.getMoneyById(this.client.user.selectedCharacter.id)) + "");
/*  669 */     ds.flush();
/*  670 */     this.client.sendMessage(ms);
/*  671 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void endDlg(boolean isResetButton) throws IOException {
/*  675 */     Message ms = new Message(126);
/*  676 */     DataOutputStream ds = ms.writer();
/*  677 */     ds.writeByte(isResetButton ? 0 : 1);
/*  678 */     ds.flush();
/*  679 */     this.client.sendMessage(ms);
/*  680 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void requestMapTemplate(Message ms) throws IOException {
/*  684 */     int templateId = ms.reader().readUnsignedByte();
/*  685 */     TileMap tilemap = (MapManager.getMapById(templateId)).tilemap;
/*  686 */     Zone zone = this.character.zone;
/*  687 */     ms = messageNotMap((byte)-109);
/*  688 */     DataOutputStream ds = ms.writer();
/*  689 */     ds.writeByte(tilemap.tmw);
/*  690 */     ds.writeByte(tilemap.tmh);
/*  691 */     int size = tilemap.tmw * tilemap.tmh;
/*  692 */     for (int i = 0; i < size; i++) {
/*  693 */       ds.writeByte(tilemap.maps[i]);
/*      */     }
/*  695 */     this.client.sendMessage(ms);
/*  696 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void requestMobTemplate(Message ms) throws IOException {
/*  700 */     int templateId = ms.reader().readUnsignedByte();
/*  701 */     byte zoomLevel = this.client.zoomLevel;
/*  702 */     MonsterTemplate mob = Server.mobs.get(templateId);
/*  703 */     Message mss = messageNotMap((byte)-108);
/*  704 */     DataOutputStream ds = mss.writer();
/*  705 */     ds.writeShort(templateId);
/*  706 */     ds.writeByte(mob.typeFly);
/*  707 */     ds.writeByte(mob.numberImage);
/*  708 */     for (int i = 0; i < mob.numberImage; i++) {
/*  709 */       byte[] ab = NinjaUtil.getFile("Data/Img/Mob/" + zoomLevel + "/" + templateId + "_" + i + ".png");
/*  710 */       ds.writeInt(ab.length);
/*  711 */       ds.write(ab);
/*      */     } 
/*  713 */     if (mob.isBoss()) {
/*  714 */       ds.writeBoolean(true);
/*  715 */       ds.writeByte(mob.frameBossMove.length);
/*  716 */       for (byte move : mob.frameBossMove) {
/*  717 */         ds.writeByte(move);
/*      */       }
/*  719 */       ds.writeByte(mob.frameBossAttack.length);
/*  720 */       for (byte[] attack : mob.frameBossAttack) {
/*  721 */         ds.writeByte(attack.length);
/*  722 */         for (byte att : attack) {
/*  723 */           ds.writeByte(att);
/*      */         }
/*      */       } 
/*      */     } else {
/*  727 */       ds.writeBoolean(false);
/*      */     } 
/*  729 */     if (mob.isBoss()) {
/*  730 */       ds.writeInt(1);
/*  731 */       ds.writeByte(mob.imgInfo.length);
/*  732 */       for (ImageInfo image : mob.imgInfo) {
/*  733 */         ds.writeByte(image.id);
/*  734 */         ds.writeByte(image.x0 * zoomLevel);
/*  735 */         ds.writeByte(image.y0 * zoomLevel);
/*  736 */         ds.writeByte(image.w * zoomLevel);
/*  737 */         ds.writeByte(image.h * zoomLevel);
/*      */       } 
/*  739 */       ds.writeShort(mob.frameBoss.length);
/*  740 */       for (Frame frame : mob.frameBoss) {
/*  741 */         ds.writeByte(frame.idImg.length);
/*  742 */         for (int j = 0; j < frame.dx.length; j++) {
/*  743 */           ds.writeShort(frame.dx[j]);
/*  744 */           ds.writeShort(frame.dy[j]);
/*  745 */           ds.writeByte(frame.idImg[j]);
/*      */         } 
/*      */       } 
/*      */     } else {
/*  749 */       ds.writeInt(0);
/*      */     } 
/*  751 */     ds.writeShort(0);
/*  752 */     ds.flush();
/*  753 */     this.client.sendMessage(mss);
/*  754 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void requestIcon(Message ms) throws IOException {
/*  758 */     int icon = ms.reader().readInt();
/*  759 */     byte index = (byte)(this.client.zoomLevel - 1);
/*  760 */     if (item[index][icon] == null) {
/*  761 */       item[index][icon] = NinjaUtil.getFile("Data/Img/Item/" + this.client.zoomLevel + "/Small" + icon + ".png");
/*      */     }
/*  763 */     byte[] ab = item[index][icon];
/*  764 */     Message mss = messageNotMap((byte)-115);
/*  765 */     DataOutputStream ds = mss.writer();
/*  766 */     ds.writeInt(icon);
/*  767 */     ds.writeInt(ab.length);
/*  768 */     ds.write(ab);
/*  769 */     ds.flush();
/*  770 */     this.client.sendMessage(mss);
/*  771 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void removeFriend(String name) throws IOException {
/*  775 */     Message m = messageSubCommand((byte)-83);
/*  776 */     DataOutputStream ds = m.writer();
/*  777 */     ds.writeUTF(name);
/*  778 */     ds.flush();
/*  779 */     this.client.sendMessage(m);
/*      */   }
/*      */   
/*      */   public void requestFriend() throws IOException {
/*  783 */     Message m = messageSubCommand((byte)-85);
/*  784 */     DataOutputStream ds = m.writer();
/*  785 */     for (Friend friend : this.character.friends.values()) {
/*  786 */       ds.writeUTF(friend.name);
/*  787 */       if (friend.type == 1 && Character.getCharacterByName(friend.name) != null) {
/*  788 */         ds.writeByte(3); continue;
/*      */       } 
/*  790 */       ds.writeByte(friend.type);
/*      */     } 
/*      */     
/*  793 */     ds.flush();
/*  794 */     this.client.sendMessage(m);
/*      */   }
/*      */   
/*      */   public void requesItemCharacter(Character.Equiped equiped) throws IOException {
/*  798 */     Message mss = new Message(94);
/*  799 */     DataOutputStream ds = mss.writer();
/*  800 */     ds.writeByte(equiped.entry.type);
/*  801 */     ds.writeLong(equiped.expire);
/*  802 */     ds.writeInt(equiped.yen);
/*  803 */     ds.writeByte(equiped.sys);
/*  804 */     for (ItemOption ab : equiped.options) {
/*  805 */       ds.writeByte(ab.optionTemplate.id);
/*  806 */       ds.writeInt(ab.param);
/*      */     } 
/*  808 */     ds.flush();
/*  809 */     this.client.sendMessage(mss);
/*  810 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void itemBoxToBag(int index1, int index2) throws IOException {
/*  814 */     Message mss = new Message(16);
/*  815 */     DataOutputStream ds = mss.writer();
/*  816 */     ds.writeByte(index1);
/*  817 */     ds.writeByte(index2);
/*  818 */     ds.flush();
/*  819 */     this.client.sendMessage(mss);
/*      */   }
/*      */   
/*      */   public void attackMonster(int damage, boolean flag, Monster mob) throws IOException {
/*  823 */     Message mss = new Message(-1);
/*  824 */     DataOutputStream ds = mss.writer();
/*  825 */     ds.writeByte(mob.mobId);
/*  826 */     ds.writeInt(mob.hp);
/*  827 */     ds.writeInt(damage);
/*  828 */     ds.writeBoolean(flag);
/*  829 */     ds.writeByte(mob.levelBoss);
/*  830 */     ds.writeInt(mob.maxhp);
/*  831 */     ds.flush();
/*  832 */     this.character.sendToMap(mss);
/*      */   }
/*      */   
/*      */   public void attackCharacter(int damage, Character _char) throws IOException {
/*  836 */     Message mss = new Message(62);
/*  837 */     DataOutputStream ds = mss.writer();
/*  838 */     ds.writeInt(_char.id);
/*  839 */     ds.writeInt(_char.hp);
/*  840 */     ds.writeInt(damage);
/*  841 */     ds.flush();
/*  842 */     _char.sendToMap(mss);
/*      */   }
/*      */   
/*      */   public void attackMonsterMiss(int mobId, int hp) throws IOException {
/*  846 */     Message mss = new Message(51);
/*  847 */     DataOutputStream ds = mss.writer();
/*  848 */     ds.writeByte(mobId);
/*  849 */     ds.writeInt(hp);
/*  850 */     ds.flush();
/*  851 */     this.character.sendToMap(mss);
/*      */   }
/*      */   
/*      */   public void setSkillPaint_1(ArrayList<Monster> monster) throws IOException {
/*  855 */     Message mss = new Message(60);
/*  856 */     DataOutputStream ds = mss.writer();
/*  857 */     ds.writeInt(this.character.id);
/*  858 */     ds.writeByte(this.character.selectedSkill.skillTemplateId);
/*  859 */     if (monster != null) {
/*  860 */       for (Monster mob : monster) {
/*  861 */         ds.writeByte(mob.mobId);
/*      */       }
/*      */     }
/*  864 */     ds.flush();
/*  865 */     Character[] characters = this.character.zone.getCharacters();
/*  866 */     for (Character pl : characters) {
/*  867 */       if (!pl.equals(this.character)) {
/*  868 */         pl.sendMessage(mss);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setSkillPaint_2(ArrayList<Character> _chars) throws IOException {
/*  874 */     Message mss = new Message(61);
/*  875 */     DataOutputStream ds = mss.writer();
/*  876 */     ds.writeInt(this.character.id);
/*  877 */     ds.writeByte(this.character.selectedSkill.skillTemplateId);
/*  878 */     if (_chars != null) {
/*  879 */       for (Character pl : _chars) {
/*  880 */         ds.writeByte(pl.id);
/*      */       }
/*      */     }
/*  883 */     ds.flush();
/*  884 */     Character[] characters = this.character.zone.getCharacters();
/*  885 */     for (Character _char : characters) {
/*  886 */       if (!_char.equals(this.character)) {
/*  887 */         _char.sendMessage(mss);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public void itemBagToBox(int index1, int index2) throws IOException {
/*  893 */     Message mss = new Message(17);
/*  894 */     DataOutputStream ds = mss.writer();
/*  895 */     ds.writeByte(index1);
/*  896 */     ds.writeByte(index2);
/*  897 */     ds.flush();
/*  898 */     this.client.sendMessage(mss);
/*  899 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void useItem(int index) throws IOException {
/*  903 */     Message mss = new Message(11);
/*  904 */     DataOutputStream ds = mss.writer();
/*  905 */     ds.writeByte(index);
/*  906 */     ds.writeByte(this.character.speed);
/*  907 */     ds.writeInt(this.character.maxHP);
/*  908 */     ds.writeInt(this.character.maxMP);
/*  909 */     ds.writeShort(this.character.eff5buffhp);
/*  910 */     ds.writeShort(this.character.eff5buffmp);
/*  911 */     ds.flush();
/*  912 */     this.client.sendMessage(mss);
/*  913 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void itemBodyToBag(int equipType, int index) throws IOException {
/*  917 */     Message mss = new Message(15);
/*  918 */     DataOutputStream ds = mss.writer();
/*  919 */     ds.writeByte(this.character.speed);
/*  920 */     ds.writeInt(this.character.maxHP);
/*  921 */     ds.writeInt(this.character.maxMP);
/*  922 */     ds.writeShort(this.character.eff5buffhp);
/*  923 */     ds.writeShort(this.character.eff5buffmp);
/*  924 */     ds.writeByte(equipType);
/*  925 */     ds.writeByte(index);
/*  926 */     ds.writeShort(this.character.head);
/*  927 */     ds.flush();
/*  928 */     this.client.sendMessage(mss);
/*      */   }
/*      */   
/*      */   public void itemInfo(Character.Item item, byte typeUI, byte indexUI) throws IOException {
/*  932 */     Message mss = new Message(42);
/*  933 */     DataOutputStream ds = mss.writer();
/*  934 */     ds.writeByte(typeUI);
/*  935 */     ds.writeByte(indexUI);
/*  936 */     ds.writeLong(item.expire);
/*  937 */     ds.writeInt(item.yen);
/*  938 */     if (item.entry.isTypeBody() || item.entry.isTypeMount() || item.entry.isTypeNgocKham()) {
/*  939 */       ds.writeByte(item.sys);
/*  940 */       for (ItemOption ability : item.options) {
/*  941 */         ds.writeByte(ability.optionTemplate.id);
/*  942 */         ds.writeInt(ability.param);
/*      */       } 
/*  944 */     } else if (item.id == 233 || item.id == 234 || item.id == 235) {
/*  945 */       byte[] ab = NinjaUtil.getFile("Data/Img/Item/" + this.user.client.zoomLevel + "/Small" + item.entry.icon + ".png");
/*  946 */       ds.writeInt(ab.length);
/*  947 */       ds.write(ab);
/*      */     } 
/*  949 */     ds.flush();
/*  950 */     this.client.sendMessage(mss);
/*  951 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void itemStoreInfo(ItemStore item, byte typeUI, byte indexUI) throws IOException {
/*  955 */     Message mss = new Message(42);
/*  956 */     DataOutputStream ds = mss.writer();
/*  957 */     ds.writeByte(typeUI);
/*  958 */     ds.writeByte(indexUI);
/*  959 */     ds.writeLong(item.expire);
/*  960 */     ds.writeInt(item.xu);
/*  961 */     ds.writeInt(item.yen);
/*  962 */     ds.writeInt(item.luong);
/*  963 */     if (item.entry.isTypeBody() || item.entry.isTypeMount() || item.entry.isTypeNgocKham()) {
/*  964 */       ds.writeByte(item.sys);
/*  965 */       for (int[] ability : item.option_max) {
/*  966 */         ds.writeByte(ability[0]);
/*  967 */         ds.writeInt(ability[1]);
/*      */       } 
/*      */     } 
/*  970 */     ds.flush();
/*  971 */     this.client.sendMessage(mss);
/*  972 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void equipedInfo(Character.Equiped equiped, byte typeUI, byte indexUI) throws IOException {
/*  976 */     Message mss = new Message(42);
/*  977 */     DataOutputStream ds = mss.writer();
/*  978 */     ds.writeByte(typeUI);
/*  979 */     ds.writeByte(indexUI);
/*  980 */     ds.writeLong(equiped.expire);
/*  981 */     ds.writeInt(equiped.yen);
/*  982 */     ds.writeByte(equiped.sys);
/*  983 */     for (ItemOption ability : equiped.options) {
/*  984 */       ds.writeByte(ability.optionTemplate.id);
/*  985 */       ds.writeInt(ability.param);
/*      */     } 
/*  987 */     ds.flush();
/*  988 */     this.client.sendMessage(mss);
/*  989 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void updatePotential() throws IOException {
/*  993 */     Message ms = messageSubCommand((byte)-109);
/*  994 */     DataOutputStream ds = ms.writer();
/*  995 */     ds.writeByte(this.character.speed);
/*  996 */     ds.writeInt(this.character.maxHP);
/*  997 */     ds.writeInt(this.character.maxMP);
/*  998 */     ds.writeShort(this.character.point);
/*  999 */     ds.writeShort(this.character.potential[0]);
/* 1000 */     ds.writeShort(this.character.potential[1]);
/* 1001 */     ds.writeInt(this.character.potential[2]);
/* 1002 */     ds.writeInt(this.character.potential[3]);
/* 1003 */     ds.flush();
/* 1004 */     this.client.sendMessage(ms);
/* 1005 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   protected void levelUp() throws IOException {
/* 1009 */     Message ms = messageSubCommand((byte)-124);
/* 1010 */     DataOutputStream ds = ms.writer();
/* 1011 */     ds.writeByte(this.character.speed);
/* 1012 */     ds.writeInt(this.character.maxHP);
/* 1013 */     ds.writeInt(this.character.maxMP);
/* 1014 */     ds.writeLong(this.character.exp);
/* 1015 */     ds.writeShort(this.character.spoint);
/* 1016 */     ds.writeShort(this.character.point);
/* 1017 */     ds.writeShort(this.character.potential[0]);
/* 1018 */     ds.writeShort(this.character.potential[1]);
/* 1019 */     ds.writeInt(this.character.potential[2]);
/* 1020 */     ds.writeInt(this.character.potential[3]);
/* 1021 */     ds.flush();
/* 1022 */     this.character.sendMessage(ms);
/* 1023 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void expandBag(Character.Item item) throws IOException {
/* 1027 */     Message ms = messageSubCommand((byte)-91);
/* 1028 */     DataOutputStream ds = ms.writer();
/* 1029 */     ds.writeByte(this.character.numberCellBag);
/* 1030 */     ds.writeByte(item.index);
/* 1031 */     ds.flush();
/* 1032 */     this.client.sendMessage(ms);
/* 1033 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void updateSkill() throws IOException {
/* 1037 */     Message ms = messageSubCommand((byte)-125);
/* 1038 */     DataOutputStream ds = ms.writer();
/* 1039 */     ds.writeByte(this.character.speed);
/* 1040 */     ds.writeInt(this.character.maxHP);
/* 1041 */     ds.writeInt(this.character.maxMP);
/* 1042 */     ds.writeShort(this.character.spoint);
/* 1043 */     ds.writeByte(this.character.listSkill.size());
/* 1044 */     for (Character.MySkill my : this.character.listSkill) {
/* 1045 */       ds.writeShort((SkillData.getSkill(this.character.classId, my.id, my.point)).skillId);
/*      */     }
/* 1047 */     ds.flush();
/* 1048 */     this.client.sendMessage(ms);
/* 1049 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void bagSort() throws IOException {
/* 1053 */     this.client.sendMessage(messageSubCommand((byte)-107));
/*      */   }
/*      */   
/*      */   public void boxSort() throws IOException {
/* 1057 */     this.client.sendMessage(messageSubCommand((byte)-106));
/*      */   }
/*      */   
/*      */   public void outZone(int id) throws IOException {
/* 1061 */     Message ms = new Message(2);
/* 1062 */     DataOutputStream ds = ms.writer();
/* 1063 */     ds.writeInt(id);
/* 1064 */     ds.flush();
/* 1065 */     this.client.sendMessage(ms);
/* 1066 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void throwItem(byte index, short itemMapId, short itemId, short x, short y) throws IOException {
/* 1070 */     Message ms = new Message(-12);
/* 1071 */     DataOutputStream ds = ms.writer();
/* 1072 */     ds.writeByte(index);
/* 1073 */     ds.writeShort(itemMapId);
/* 1074 */     ds.writeShort(x);
/* 1075 */     ds.writeShort(y);
/* 1076 */     ds.flush();
/* 1077 */     this.client.sendMessage(ms);
/* 1078 */     ms.cleanup();
/*      */     
/* 1080 */     ms = new Message(-6);
/* 1081 */     ds = ms.writer();
/* 1082 */     ds.writeInt(this.character.id);
/* 1083 */     ds.writeShort(itemMapId);
/* 1084 */     ds.writeShort(itemId);
/* 1085 */     ds.writeShort(x);
/* 1086 */     ds.writeShort(y);
/* 1087 */     ds.flush();
/* 1088 */     Character[] characters = this.character.zone.getCharacters();
/* 1089 */     for (Character _char : characters) {
/* 1090 */       if (!_char.equals(this.character))
/*      */       {
/*      */         
/* 1093 */         _char.sendMessage(ms); } 
/*      */     } 
/* 1095 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void sendVersion() throws IOException {
/* 1099 */     if (cache[0] == null) {
/* 1100 */       cache[0] = NinjaUtil.getFile("cache/version");
/*      */     }
/* 1102 */     Message ms = messageNotMap((byte)-123);
/* 1103 */     DataOutputStream ds = ms.writer();
/* 1104 */     ds.write(cache[0]);
/* 1105 */     ds.flush();
/* 1106 */     this.client.sendMessage(ms);
/* 1107 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void sendMap() throws IOException {
/* 1111 */     if (cache[1] == null) {
/* 1112 */       cache[1] = NinjaUtil.getFile("cache/map");
/*      */     }
/* 1114 */     Message ms = messageNotMap((byte)-121);
/* 1115 */     DataOutputStream ds = ms.writer();
/* 1116 */     ds.write(cache[1]);
/* 1117 */     ds.flush();
/* 1118 */     this.client.sendMessage(ms);
/* 1119 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void sendData() throws IOException {
/* 1123 */     if (cache[2] == null) {
/* 1124 */       cache[2] = NinjaUtil.getFile("cache/data");
/*      */     }
/* 1126 */     Message ms = messageNotMap((byte)-122);
/* 1127 */     DataOutputStream ds = ms.writer();
/* 1128 */     ds.write(cache[2]);
/* 1129 */     ds.flush();
/* 1130 */     this.client.sendMessage(ms);
/* 1131 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void sendSkill() throws IOException {
/* 1135 */     if (cache[3] == null) {
/* 1136 */       cache[3] = NinjaUtil.getFile("cache/skill");
/*      */     }
/* 1138 */     Message ms = messageNotMap((byte)-120);
/* 1139 */     DataOutputStream ds = ms.writer();
/* 1140 */     ds.write(cache[3]);
/* 1141 */     ds.flush();
/* 1142 */     this.client.sendMessage(ms);
/* 1143 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void sendItem() throws IOException {
/* 1147 */     if (cache[4] == null) {
/* 1148 */       cache[4] = NinjaUtil.getFile("cache/item");
/*      */     }
/* 1150 */     Message ms = messageNotMap((byte)-119);
/* 1151 */     DataOutputStream ds = ms.writer();
/* 1152 */     ds.write(cache[4]);
/* 1153 */     ds.flush();
/* 1154 */     this.client.sendMessage(ms);
/* 1155 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void startOKDlg(String text) throws IOException {
/* 1159 */     Message ms = new Message(-26);
/* 1160 */     DataOutputStream ds = ms.writer();
/* 1161 */     ds.writeUTF(text);
/* 1162 */     ds.flush();
/* 1163 */     this.client.sendMessage(ms);
/* 1164 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void addInfo(String text) throws IOException {
/* 1168 */     Message ms = new Message(-25);
/* 1169 */     DataOutputStream ds = ms.writer();
/* 1170 */     ds.writeUTF(text);
/* 1171 */     ds.flush();
/* 1172 */     this.client.sendMessage(ms);
/* 1173 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void updateItem(Character.Item item) throws IOException {
/* 1177 */     Message ms = new Message(7);
/* 1178 */     DataOutputStream ds = ms.writer();
/* 1179 */     ds.writeByte(item.index);
/* 1180 */     ds.writeShort(item.quantity);
/* 1181 */     ds.flush();
/* 1182 */     this.client.sendMessage(ms);
/* 1183 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void removeItem(int index, int quantity) throws IOException {
/* 1187 */     Message ms = new Message(18);
/* 1188 */     DataOutputStream ds = ms.writer();
/* 1189 */     ds.writeByte(index);
/* 1190 */     ds.writeShort(quantity);
/* 1191 */     ds.flush();
/* 1192 */     this.client.sendMessage(ms);
/* 1193 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void viewInfo(Character pl) throws IOException {
/* 1197 */     Message ms = new Message(93);
/* 1198 */     DataOutputStream ds = ms.writer();
/* 1199 */     ds.writeInt(pl.id);
/* 1200 */     ds.writeUTF(pl.name);
/* 1201 */     ds.writeShort(pl.head);
/* 1202 */     ds.writeByte(pl.gender);
/* 1203 */     ds.writeByte(pl.classId);
/* 1204 */     ds.writeByte(pl.hieuChien);
/* 1205 */     ds.writeInt(pl.hp);
/* 1206 */     ds.writeInt(pl.maxHP);
/* 1207 */     ds.writeInt(pl.mp);
/* 1208 */     ds.writeInt(pl.maxMP);
/* 1209 */     ds.writeByte(pl.speed);
/* 1210 */     ds.writeShort(99);
/* 1211 */     ds.writeShort(98);
/* 1212 */     ds.writeShort(97);
/* 1213 */     ds.writeInt(pl.dame);
/* 1214 */     ds.writeInt(pl.dameDown);
/* 1215 */     ds.writeShort(pl.exactly);
/* 1216 */     ds.writeShort(pl.miss);
/* 1217 */     ds.writeShort(765);
/* 1218 */     ds.writeShort(654);
/* 1219 */     ds.writeShort(543);
/* 1220 */     ds.writeShort(432);
/* 1221 */     ds.writeByte(pl.level);
/* 1222 */     ds.writeShort(321);
/* 1223 */     ds.writeUTF(pl.clanname);
/* 1224 */     if (!pl.clanname.equals("")) {
/* 1225 */       ds.writeByte(pl.clan);
/*      */     }
/* 1227 */     ds.writeShort(321);
/* 1228 */     ds.writeShort(123);
/* 1229 */     ds.writeShort(234);
/* 1230 */     ds.writeShort(345);
/* 1231 */     ds.writeShort(456);
/* 1232 */     ds.writeShort(567);
/* 1233 */     ds.writeShort(678);
/* 1234 */     ds.writeShort(789);
/* 1235 */     ds.writeShort(135);
/* 1236 */     ds.writeShort(246);
/* 1237 */     ds.writeShort(357);
/* 1238 */     ds.writeByte(pl.countFinishDay);
/* 1239 */     ds.writeByte(pl.countLoosBoss);
/* 1240 */     ds.writeByte(pl.countPB);
/* 1241 */     ds.writeByte(pl.limitTiemNangSo);
/* 1242 */     ds.writeByte(pl.limitKyNangSo);
/* 1243 */     for (int i = 0; i < 16; i++) {
/* 1244 */       if (pl.equiped[i] != null) {
/* 1245 */         ds.writeShort((pl.equiped[i]).id);
/* 1246 */         ds.writeByte((pl.equiped[i]).upgrade);
/* 1247 */         ds.writeByte((pl.equiped[i]).sys);
/*      */       } 
/*      */     } 
/* 1250 */     ds.flush();
/* 1251 */     this.user.client.sendMessage(ms);
/* 1252 */     ms.cleanup();
/* 1253 */     if (pl.user != this.user) {
/* 1254 */       pl.user.service.addInfoMe(this.character.name + " đang xem thông tin của bạn!");
/*      */     }
/*      */   }
/*      */   
/*      */   public void deleteItemBody(Character.Equiped equiped) throws IOException {
/* 1259 */     Message ms = new Message(-80);
/* 1260 */     DataOutputStream ds = ms.writer();
/* 1261 */     ds.writeByte(equiped.entry.type);
/* 1262 */     ds.flush();
/* 1263 */     this.client.sendMessage(ms);
/* 1264 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void addItem(Character.Item item) throws IOException {
/* 1268 */     Message ms = new Message(8);
/* 1269 */     DataOutputStream ds = ms.writer();
/* 1270 */     ds.writeByte(item.index);
/* 1271 */     ds.writeShort(item.id);
/* 1272 */     ds.writeBoolean(item.isLock);
/* 1273 */     if (item.entry.isTypeBody() || item.entry.isTypeNgocKham()) {
/* 1274 */       ds.writeByte(item.upgrade);
/*      */     }
/* 1276 */     ds.writeBoolean((item.expire != -1L));
/* 1277 */     ds.writeShort(item.quantity);
/* 1278 */     ds.flush();
/* 1279 */     this.client.sendMessage(ms);
/* 1280 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void tradeAccept() {
/* 1284 */     this.client.sendMessage(new Message(46));
/*      */   }
/*      */   
/*      */   public void startWaitDlg() {
/* 1288 */     this.client.sendMessage(new Message(-16));
/*      */   }
/*      */   
/*      */   public void addInfoMe(String text) throws IOException {
/* 1292 */     Message ms = new Message(-24);
/* 1293 */     DataOutputStream ds = ms.writer();
/* 1294 */     ds.writeUTF(text);
/* 1295 */     ds.flush();
/* 1296 */     this.client.sendMessage(ms);
/* 1297 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   private Message messageNotLogin(byte command) throws IOException {
/* 1301 */     Message ms = new Message(-29);
/* 1302 */     ms.writer().writeByte(command);
/* 1303 */     return ms;
/*      */   }
/*      */   
/*      */   private Message messageNotMap(byte command) throws IOException {
/* 1307 */     Message ms = new Message(-28);
/* 1308 */     ms.writer().writeByte(command);
/* 1309 */     return ms;
/*      */   }
/*      */   
/*      */   private Message messageSubCommand(byte command) throws IOException {
/* 1313 */     Message ms = new Message(-30);
/* 1314 */     ms.writer().writeByte(command);
/* 1315 */     return ms;
/*      */   }
/*      */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\server\Service.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */