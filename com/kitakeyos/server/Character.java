/*      */ package com.kitakeyos.server;
/*      */ import com.kitakeyos.data.StoreData;
/*      */ import com.kitakeyos.io.Message;
/*      */ import com.kitakeyos.object.Effect;
/*      */ import com.kitakeyos.object.ItemMap;
/*      */ import com.kitakeyos.object.ItemStore;
/*      */ import com.kitakeyos.option.ItemOption;
/*      */ import com.kitakeyos.template.NpcTemplate;
/*      */ import com.kitakeyos.util.NinjaUtil;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Vector;
/*      */ import org.json.simple.JSONArray;
/*      */ import org.json.simple.JSONObject;
/*      */ 
/*      */ public class Character {
/*      */   protected int id;
/*      */   protected User user;
/*      */   protected String name;
/*      */   protected byte gender;
/*      */   protected String school;
/*      */   protected byte classId;
/*      */   protected int level;
/*      */   protected short head;
/*      */   protected short original_head;
/*      */   protected short weapon;
/*      */   protected short body;
/*      */   protected short leg;
/*      */   protected int xu;
/*      */   protected int xuInBox;
/*      */   protected int yen;
/*      */   protected int hp;
/*      */   protected int maxHP;
/*      */   protected int mp;
/*      */   protected int maxMP;
/*   38 */   private Logger logger = new Logger(getClass());
/*      */   
/*      */   protected int dame;
/*      */   
/*      */   protected int dame2;
/*      */   
/*      */   protected int dameDown;
/*      */   
/*      */   protected int exactly;
/*      */   
/*      */   protected int miss;
/*      */   
/*      */   protected int resFire;
/*      */   
/*      */   protected int resIce;
/*      */   protected int resWind;
/*      */   protected long exp;
/*      */   protected long expDown;
/*      */   protected long expR;
/*      */   protected byte hieuChien;
/*      */   protected byte typePk;
/*      */   protected String clanname;
/*      */   protected short clan;
/*      */   protected short[] potential;
/*      */   protected ArrayList<MySkill> listSkill;
/*      */   protected short point;
/*      */   protected short spoint;
/*   65 */   protected byte speed = 8; protected byte numberCellBag; protected byte numberCellBox; protected Map map; protected Zone zone;
/*      */   protected short mapId;
/*      */   protected short x;
/*      */   protected short y;
/*      */   protected short eff5buffhp;
/*      */   protected short eff5buffmp;
/*   71 */   protected byte captcha = 0;
/*      */   protected byte[] onKSkill;
/*      */   protected byte[] onOSkill;
/*      */   protected byte[] onCSkill;
/*   75 */   protected ArrayList<Integer> menu = new ArrayList<>(); protected Item[] bag; protected Item[] box; protected Equiped[] equiped; protected Trade trade; protected Trade.Trader trader; protected byte countPB;
/*      */   protected byte countFinishDay;
/*      */   protected byte countLoosBoss;
/*      */   protected byte limitTiemNangSo;
/*      */   protected byte limitKyNangSo;
/*      */   protected short tayTiemNang;
/*      */   protected short tayKyNang;
/*   82 */   protected int[] options = new int[127];
/*   83 */   public static HashMap<String, Character> characters_name = new HashMap<>();
/*   84 */   public static HashMap<Integer, Character> characters_id = new HashMap<>();
/*      */   protected short saveCoordinate;
/*   86 */   private static final short[] ITEM_IN_SELECT_CARD = new short[] { 434, 434, 435, 435, 8, 9, 10, 11, 12, 30, 242, 249, 250, 252, 253, 254, 255, 257, 283, 311, 312, 313, 314, 315, 316, 343, 344, 345, 346, 375, 376, 377, 378, 379, 380, 403, 404, 405, 406, 407, 408, 409, 410, 419, 523, 547, 552, 553, 554, 555, 556, 557, 558, 559, 560, 561, 562, 563 };
/*   87 */   private static final byte[] EXPIRE_DATE_OF_TIME = new byte[] { 3, 7, 15, 30 };
/*   88 */   private static final int[] YEN_IN_SELECT_CARD = new int[] { 10000, 20000, 30000, 50000, 100000, 200000, 500000, 1000000, 5000000 };
/*      */   public Character enemy;
/*      */   public Mount[] mount;
/*   91 */   public SelectedSkill selectedSkill = new SelectedSkill();
/*      */   public HashMap<Byte, Effect> effects;
/*      */   public boolean isDead;
/*   94 */   public ArrayList<Byte> removeEffect = new ArrayList<>();
/*      */   public int multiExp;
/*      */   public HashMap<String, Friend> friends;
/*      */   public byte numberUseExpanedBag;
/*   98 */   public long lastTimeRecovery = 0L;
/*      */   public boolean hasJoin = false;
/*      */   
/*      */   public void update() {
/*      */     try {
/*  103 */       long now = System.currentTimeMillis();
/*  104 */       int hp = 0;
/*  105 */       int mp = 0;
/*  106 */       this.multiExp = 1;
/*  107 */       for (Map.Entry<Byte, Effect> entry : this.effects.entrySet()) {
/*  108 */         int i; Effect eff = entry.getValue();
/*  109 */         if (eff == null || eff.timeStart >= eff.timeLength) {
/*  110 */           this.removeEffect.add(entry.getKey());
/*      */           continue;
/*      */         } 
/*  113 */         switch (eff.template.type) {
/*      */           case 0:
/*  115 */             i = eff.param * 2;
/*  116 */             hp += i;
/*  117 */             mp += i;
/*      */             break;
/*      */           case 18:
/*  120 */             this.multiExp = eff.param;
/*      */             break;
/*      */           case 17:
/*  123 */             hp += eff.param * 2;
/*      */             break;
/*      */         } 
/*      */ 
/*      */         
/*  128 */         eff.timeStart++;
/*      */       } 
/*  130 */       if (now - this.lastTimeRecovery >= 5000L) {
/*  131 */         this.lastTimeRecovery = now;
/*  132 */         hp += this.options[30] + this.options[120];
/*  133 */         mp += this.options[27] + this.options[119];
/*      */       } 
/*  135 */       if (!this.isDead) {
/*  136 */         this.hp += hp;
/*  137 */         this.mp += hp;
/*  138 */         if (this.hp > this.maxHP) {
/*  139 */           this.hp = this.maxHP;
/*      */         }
/*  141 */         if (this.mp > this.maxMP) {
/*  142 */           this.mp = this.maxMP;
/*      */         }
/*      */       } 
/*  145 */       for (Iterator<Byte> iterator = this.removeEffect.iterator(); iterator.hasNext(); ) { byte b = ((Byte)iterator.next()).byteValue();
/*  146 */         this.user.service.removeEffect(this.effects.get(Byte.valueOf(b)));
/*  147 */         this.effects.remove(Byte.valueOf(b)); }
/*      */       
/*  149 */       this.removeEffect.clear();
/*  150 */     } catch (IOException ex) {
/*  151 */       this.logger.error("update", ex.getMessage());
/*      */     } 
/*      */   }
/*      */   
/*      */   public static class SelectedSkill
/*      */   {
/*      */     public short skillTemplateId;
/*      */     public int[] options;
/*      */     public int manaUse;
/*      */     public byte maxFight;
/*      */     public int coolDown;
/*      */     public Character.MySkill skill;
/*      */     public boolean haveLearned;
/*      */     public int dx;
/*      */     public int dy;
/*      */   }
/*      */   
/*      */   public static class Item
/*      */     implements Cloneable {
/*      */     public int id;
/*      */     public int index;
/*      */     public int quantity;
/*      */     public long expire;
/*      */     public byte upgrade;
/*      */     public byte sys;
/*      */     public boolean isLock;
/*      */     public int yen;
/*      */     public ArrayList<ItemOption> options;
/*      */     public ItemEntry entry;
/*      */     
/*      */     public Item(int id) {
/*  182 */       this.id = id;
/*  183 */       this.entry = ItemData.getItemEntryById(id);
/*      */     }
/*      */     
/*      */     public Item clone() {
/*      */       try {
/*  188 */         Item item = (Item)super.clone();
/*  189 */         if (this.options != null) {
/*  190 */           item.options = (ArrayList<ItemOption>)this.options.clone();
/*      */         }
/*  192 */         return item;
/*  193 */       } catch (CloneNotSupportedException cloneNotSupportedException) {
/*      */         
/*  195 */         return null;
/*      */       } 
/*      */     }
/*      */     public void next(int next) {
/*  199 */       if (next == 0) {
/*      */         return;
/*      */       }
/*  202 */       this.isLock = true;
/*  203 */       this.upgrade = (byte)(this.upgrade + next);
/*  204 */       if (this.options != null)
/*  205 */         for (int i = 0; i < this.options.size(); i++) {
/*  206 */           ItemOption itemOption = this.options.get(i);
/*  207 */           if (itemOption.optionTemplate.id == 6 || itemOption.optionTemplate.id == 7) {
/*  208 */             itemOption.param += (short)(15 * next);
/*  209 */           } else if (itemOption.optionTemplate.id == 8 || itemOption.optionTemplate.id == 9 || itemOption.optionTemplate.id == 19) {
/*  210 */             itemOption.param += (short)(10 * next);
/*  211 */           } else if (itemOption.optionTemplate.id == 10 || itemOption.optionTemplate.id == 11 || itemOption.optionTemplate.id == 12 || itemOption.optionTemplate.id == 13 || itemOption.optionTemplate.id == 14 || itemOption.optionTemplate.id == 15 || itemOption.optionTemplate.id == 17 || itemOption.optionTemplate.id == 18 || itemOption.optionTemplate.id == 20) {
/*  212 */             itemOption.param += (short)(5 * next);
/*  213 */           } else if (itemOption.optionTemplate.id == 21 || itemOption.optionTemplate.id == 22 || itemOption.optionTemplate.id == 23 || itemOption.optionTemplate.id == 24 || itemOption.optionTemplate.id == 25 || itemOption.optionTemplate.id == 26) {
/*  214 */             itemOption.param += (short)(150 * next);
/*  215 */           } else if (itemOption.optionTemplate.id == 16) {
/*  216 */             itemOption.param += (short)(3 * next);
/*      */           } 
/*      */         }  
/*      */     } }
/*      */   public static class Mount { public int id; public byte level; public byte sys; public long expire;
/*      */     public int yen;
/*      */     public ArrayList<ItemOption> options;
/*      */     public ItemEntry entry;
/*      */     
/*      */     public Mount(int id) {
/*  226 */       this.id = id;
/*  227 */       this.entry = ItemData.getItemEntryById(id);
/*      */     } }
/*      */ 
/*      */   
/*      */   public static class Equiped
/*      */   {
/*      */     public int id;
/*      */     public byte upgrade;
/*      */     public byte sys;
/*      */     public long expire;
/*      */     public int yen;
/*      */     public ArrayList<ItemOption> options;
/*      */     public ItemEntry entry;
/*      */     
/*      */     public Equiped(int id) {
/*  242 */       this.id = id;
/*  243 */       this.entry = ItemData.getItemEntryById(id);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class MySkill
/*      */   {
/*      */     public int id;
/*      */ 
/*      */     
/*      */     public int point;
/*      */ 
/*      */     
/*      */     public long lastTimeUseSkill;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void upSkill(Message ms) throws IOException {
/*  263 */     short skillId = ms.reader().readShort();
/*  264 */     byte point = ms.reader().readByte();
/*  265 */     if (point > this.spoint || point < 0) {
/*      */       return;
/*      */     }
/*  268 */     int size = this.listSkill.size();
/*  269 */     for (int i = 0; i < size; i++) {
/*  270 */       if (((MySkill)this.listSkill.get(i)).id == skillId) {
/*  271 */         SkillTemplate tem = SkillData.getTemplate(this.classId, skillId);
/*  272 */         if (tem.maxPoint - ((MySkill)this.listSkill.get(i)).point >= point) {
/*  273 */           for (Skill skill : tem.skills) {
/*  274 */             if (skill.point == ((MySkill)this.listSkill.get(i)).point + point && 
/*  275 */               skill.level > this.level) {
/*  276 */               startOKDlg("Trình độ không đủ yêu cầu!");
/*      */               
/*      */               return;
/*      */             } 
/*      */           } 
/*  281 */           ((MySkill)this.listSkill.get(i)).point += point;
/*  282 */           this.spoint = (short)(this.spoint - 1);
/*  283 */           setAbility();
/*  284 */           this.hp = this.maxHP;
/*  285 */           this.mp = this.maxMP;
/*  286 */           this.user.service.updateSkill(); break;
/*      */         } 
/*  288 */         startOKDlg("Điểm nhập không hợp lệ!");
/*      */         break;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void upPotential(Message ms) throws IOException {
/*  296 */     byte index = ms.reader().readByte();
/*  297 */     short point = ms.reader().readShort();
/*  298 */     if (index < 0 || index > 4) {
/*  299 */       startOKDlg("Không hợp lệ!");
/*      */       return;
/*      */     } 
/*  302 */     if (point > this.point) {
/*  303 */       startOKDlg("Bạn không đủ điểm!");
/*      */       return;
/*      */     } 
/*  306 */     this.potential[index] = (short)(this.potential[index] + point);
/*  307 */     setAbility();
/*  308 */     this.hp = this.maxHP;
/*  309 */     this.mp = this.maxMP;
/*  310 */     this.point = (short)(this.point - point);
/*  311 */     this.user.service.updatePotential();
/*      */   }
/*      */   
/*      */   public void removeItem(int index, int quantity, boolean isUpdate) {
/*      */     try {
/*  316 */       if (this.bag[index] != null) {
/*  317 */         this.logger.log("removeItem index= " + index + " quantity=" + quantity);
/*  318 */         (this.bag[index]).quantity -= quantity;
/*  319 */         if (isUpdate) {
/*  320 */           this.user.service.removeItem(index, quantity);
/*      */         }
/*  322 */         if ((this.bag[index]).quantity <= 0) {
/*  323 */           this.bag[index] = null;
/*      */         }
/*      */       } 
/*  326 */     } catch (IOException ex) {
/*  327 */       this.logger.error("removeItem", ex.getMessage());
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean checkItemExist(Item item) {
/*  332 */     for (Item bag : this.bag) {
/*  333 */       if (bag != null && bag.id == item.id) {
/*  334 */         return true;
/*      */       }
/*      */     } 
/*  337 */     return false;
/*      */   }
/*      */   
/*      */   public int getIndexItemByIdInBag(int id) {
/*  341 */     for (Item bag : this.bag) {
/*  342 */       if (bag != null && bag.id == id) {
/*  343 */         return bag.index;
/*      */       }
/*      */     } 
/*  346 */     return -1;
/*      */   }
/*      */   
/*      */   public int getIndexItemByIdInBox(int id) {
/*  350 */     for (Item box : this.box) {
/*  351 */       if (box != null && box.id == id) {
/*  352 */         return box.index;
/*      */       }
/*      */     } 
/*  355 */     return -1;
/*      */   }
/*      */   
/*      */   public int getNumberItem(int id) {
/*  359 */     int number = 0;
/*  360 */     for (Item bag : this.bag) {
/*  361 */       if (bag != null && bag.id == id) {
/*  362 */         number += bag.quantity;
/*      */       }
/*      */     } 
/*  365 */     return number;
/*      */   }
/*      */   
/*      */   public void useItemChangeMap(Message ms) throws IOException {
/*  369 */     byte indexUI = ms.reader().readByte();
/*  370 */     byte indexMenu = ms.reader().readByte();
/*  371 */     if (this.bag[indexUI] != null && ((this.bag[indexUI]).id == 35 || (this.bag[indexUI]).id == 37)) {
/*  372 */       this.user.service.startWaitDlg();
/*  373 */       (new short[10])[0] = 1; (new short[10])[1] = 27; (new short[10])[2] = 72; (new short[10])[3] = 10; (new short[10])[4] = 17; (new short[10])[5] = 22; (new short[10])[6] = 32; (new short[10])[7] = 38; (new short[10])[8] = 43; (new short[10])[9] = 48; short map = (new short[10])[indexMenu];
/*  374 */       short[] xy = NinjaUtil.getXY(map);
/*  375 */       this.x = xy[0];
/*  376 */       this.y = xy[1];
/*  377 */       changeMap(map);
/*  378 */       if ((this.bag[indexUI]).id == 35 || ((this.bag[indexUI]).id == 37 && (this.bag[indexUI]).expire != -1L && (this.bag[indexUI]).expire < (new Date()).getTime())) {
/*  379 */         removeItem(indexUI, 1, true);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public void useEquipment(Item item) throws IOException {
/*  385 */     if (item.entry.isTypeWeapon() && (((
/*  386 */       this.classId == 0 || this.classId == 1) && !item.entry.isKiem()) || (this.classId == 2 && !item.entry.isTieu()) || (this.classId == 3 && 
/*  387 */       !item.entry.isKunai()) || (this.classId == 4 && !item.entry.isCung()) || (this.classId == 5 && 
/*  388 */       !item.entry.isDao()) || (this.classId == 6 && !item.entry.isQuat()))) {
/*  389 */       startOKDlg("Vũ khí không thích hợp!");
/*      */       
/*      */       return;
/*      */     } 
/*  393 */     byte indexUI = (byte)item.index;
/*  394 */     byte index = item.entry.type;
/*  395 */     if (item.entry.isTypeWeapon()) {
/*  396 */       this.weapon = item.entry.part;
/*  397 */       this.user.service.useWeapon();
/*  398 */     } else if (item.entry.type == 2) {
/*  399 */       this.body = item.entry.part;
/*  400 */       this.user.service.useShirt();
/*  401 */     } else if (item.entry.type == 6) {
/*  402 */       this.leg = item.entry.part;
/*  403 */       this.user.service.usePant();
/*  404 */     } else if (item.entry.type == 11) {
/*  405 */       this.head = item.entry.part;
/*  406 */       this.user.service.useMask();
/*      */     } 
/*  408 */     if (this.equiped[index] != null) {
/*  409 */       Item it = new Item((this.equiped[index]).id);
/*  410 */       it.upgrade = (this.equiped[index]).upgrade;
/*  411 */       it.sys = (this.equiped[index]).sys;
/*  412 */       it.expire = (this.equiped[index]).expire;
/*  413 */       it.yen = (this.equiped[index]).yen;
/*  414 */       it.options = (this.equiped[index]).options;
/*  415 */       it.index = indexUI;
/*  416 */       it.isLock = true;
/*  417 */       this.equiped[index] = new Equiped(item.id);
/*  418 */       (this.equiped[index]).id = item.id;
/*  419 */       (this.equiped[index]).upgrade = item.upgrade;
/*  420 */       (this.equiped[index]).sys = item.sys;
/*  421 */       (this.equiped[index]).expire = item.expire;
/*  422 */       (this.equiped[index]).yen = item.yen;
/*  423 */       (this.equiped[index]).options = item.options;
/*  424 */       this.bag[indexUI] = it;
/*      */     } 
/*  426 */     if (this.equiped[index] == null) {
/*  427 */       this.equiped[index] = new Equiped(item.id);
/*  428 */       (this.equiped[index]).id = item.id;
/*  429 */       (this.equiped[index]).upgrade = item.upgrade;
/*  430 */       (this.equiped[index]).sys = item.sys;
/*  431 */       (this.equiped[index]).expire = item.expire;
/*  432 */       (this.equiped[index]).yen = item.yen;
/*  433 */       (this.equiped[index]).options = item.options;
/*  434 */       this.bag[indexUI] = null;
/*      */     } 
/*  436 */     setAbility();
/*  437 */     this.user.service.useItem(indexUI);
/*      */   }
/*      */   
/*      */   public void useItem(Message ms) throws IOException {
/*  441 */     byte indexUI = ms.reader().readByte();
/*  442 */     if (indexUI >= 0 && indexUI <= this.numberCellBag) {
/*  443 */       boolean isMount = true;
/*  444 */       Item item = this.bag[indexUI];
/*  445 */       if (this.level < item.entry.level) {
/*  446 */         startOKDlg("Trình độ không đạt yêu cầu!");
/*      */         return;
/*      */       } 
/*  449 */       if ((item.entry.gender == 0 || item.entry.gender == 1) && item.entry.gender != this.gender) {
/*  450 */         startOKDlg("Giới tính không phù hợp.");
/*      */         return;
/*      */       } 
/*  453 */       if (item != null && item.quantity >= 1) {
/*      */         
/*  455 */         if (item.entry.isTypeBody()) {
/*  456 */           useEquipment(item); return;
/*      */         } 
/*  458 */         if (item.id == 34 || item.id == 36)
/*  459 */         { this.user.service.startWaitDlg();
/*  460 */           short[] xy = NinjaUtil.getXY(this.saveCoordinate);
/*  461 */           this.x = xy[0];
/*  462 */           this.y = xy[1];
/*  463 */           if (item.id == 34) {
/*  464 */             removeItem(item.index, item.quantity, true);
/*      */           }
/*  466 */           changeMap(this.saveCoordinate); }
/*  467 */         else if (item.entry.isTypeMount())
/*  468 */         { isMount = true;
/*  469 */           byte index = (byte)(item.entry.type - 29);
/*  470 */           if (this.mount[index] == null) {
/*  471 */             this.mount[index] = new Mount(item.id);
/*  472 */             (this.mount[index]).id = item.id;
/*  473 */             (this.mount[index]).level = item.upgrade;
/*  474 */             (this.mount[index]).expire = item.expire;
/*  475 */             (this.mount[index]).yen = item.yen;
/*  476 */             (this.mount[index]).options = item.options;
/*  477 */             (this.mount[index]).sys = item.sys;
/*  478 */             removeItem(item.index, item.quantity, true);
/*      */           } else {
/*  480 */             Item temp = item.clone();
/*  481 */             this.bag[indexUI] = new Item((this.mount[index]).id);
/*  482 */             (this.bag[indexUI]).quantity = 1;
/*  483 */             (this.bag[indexUI]).index = indexUI;
/*  484 */             (this.bag[indexUI]).isLock = true;
/*  485 */             (this.bag[indexUI]).sys = (this.mount[index]).sys;
/*  486 */             (this.bag[indexUI]).upgrade = (this.mount[index]).level;
/*  487 */             (this.bag[indexUI]).expire = (this.mount[index]).expire;
/*  488 */             (this.bag[indexUI]).options = (this.mount[index]).options;
/*  489 */             (this.bag[indexUI]).yen = (this.mount[index]).yen;
/*  490 */             this.mount[index] = new Mount(temp.id);
/*  491 */             (this.mount[index]).id = temp.id;
/*  492 */             (this.mount[index]).level = temp.upgrade;
/*  493 */             (this.mount[index]).sys = temp.sys;
/*  494 */             (this.mount[index]).expire = temp.expire;
/*  495 */             (this.mount[index]).yen = temp.yen;
/*  496 */             (this.mount[index]).options = temp.options;
/*      */           }  }
/*  498 */         else if (item.entry.type == 27)
/*  499 */         { if (item.id == 248) {
/*  500 */             int time = 18000;
/*  501 */             short param = 2;
/*  502 */             byte template = 22;
/*  503 */             Effect effect = new Effect(template, 0, time, param);
/*  504 */             byte type = effect.template.type;
/*  505 */             Effect temp = this.effects.get(Byte.valueOf(type));
/*  506 */             if (temp != null) {
/*  507 */               temp.timeLength += time;
/*  508 */               this.user.service.replaceEffect(temp);
/*      */             } else {
/*  510 */               this.effects.put(Byte.valueOf(type), effect);
/*  511 */               this.user.service.addEffect(effect);
/*      */             } 
/*  513 */             removeItem(item.index, item.quantity, true);
/*  514 */           } else if (item.id == 240) {
/*  515 */             this.tayTiemNang = (short)(this.tayTiemNang + 1);
/*  516 */             removeItem(item.index, item.quantity, true);
/*  517 */           } else if (item.id == 241) {
/*  518 */             this.tayKyNang = (short)(this.tayKyNang + 1);
/*  519 */             removeItem(item.index, item.quantity, true);
/*  520 */           } else if (item.id == 252) {
/*  521 */             if (this.limitKyNangSo < 3) {
/*  522 */               this.limitKyNangSo = (byte)(this.limitKyNangSo + 1);
/*  523 */               this.spoint = (short)(this.spoint + 1);
/*  524 */               this.user.service.updateSkill();
/*  525 */               removeItem(item.index, item.quantity, true);
/*  526 */               this.user.service.addInfoMe("Bạn nhận được 1 điểm kỹ năng.");
/*      */             } else {
/*  528 */               startOKDlg("Bạn chỉ được học 3 lần.");
/*      */             } 
/*  530 */           } else if (item.id == 253) {
/*  531 */             if (this.limitTiemNangSo < 3) {
/*  532 */               this.limitTiemNangSo = (byte)(this.limitTiemNangSo + 1);
/*  533 */               this.point = (short)(this.point + 10);
/*  534 */               this.user.service.updatePotential();
/*  535 */               removeItem(item.index, item.quantity, true);
/*  536 */               this.user.service.addInfoMe("Bạn nhận được 10 điểm tiềm năng.");
/*      */             } else {
/*  538 */               startOKDlg("Bạn chỉ được học 3 lần.");
/*      */             } 
/*  540 */           } else if (item.id == 215 || item.id == 229 || item.id == 283) {
/*  541 */             expandBag(item);
/*      */           } else {
/*  543 */             learnSkill(item);
/*      */           }  }
/*  545 */         else { if (item.entry.type == 16) {
/*  546 */             if (this.hp == this.maxHP) {
/*  547 */               this.user.service.addInfoMe("HP đã đầy.");
/*      */               return;
/*      */             } 
/*  550 */             int time = 3;
/*  551 */             short param = 0;
/*  552 */             if (item.id == 13) {
/*  553 */               param = 25;
/*  554 */             } else if (item.id == 14) {
/*  555 */               param = 90;
/*  556 */             } else if (item.id == 15) {
/*  557 */               param = 230;
/*  558 */             } else if (item.id == 16) {
/*  559 */               param = 400;
/*  560 */             } else if (item.id == 17) {
/*  561 */               param = 650;
/*  562 */             } else if (item.id == 565) {
/*  563 */               param = 1500;
/*      */             } 
/*  565 */             byte template = 21;
/*  566 */             Effect effect = new Effect(template, 0, time, param);
/*  567 */             byte type = effect.template.type;
/*  568 */             Effect temp = this.effects.get(Byte.valueOf(type));
/*  569 */             if (temp != null) {
/*  570 */               this.user.service.replaceEffect(effect);
/*      */             } else {
/*  572 */               this.user.service.addEffect(effect);
/*      */             } 
/*  574 */             this.effects.put(Byte.valueOf(type), effect);
/*  575 */             removeItem(item.index, 1, true); return;
/*      */           } 
/*  577 */           if (item.entry.type == 17) {
/*  578 */             if (this.mp == this.maxMP) {
/*  579 */               this.user.service.addInfoMe("MP đã đầy.");
/*      */               return;
/*      */             } 
/*  582 */             int mp = 0;
/*  583 */             switch (item.id) {
/*      */               
/*      */               case 18:
/*  586 */                 mp = 150;
/*      */                 break;
/*      */               
/*      */               case 19:
/*  590 */                 mp = 500;
/*      */                 break;
/*      */               
/*      */               case 20:
/*  594 */                 mp = 1000;
/*      */                 break;
/*      */               
/*      */               case 21:
/*  598 */                 mp = 2000;
/*      */                 break;
/*      */               
/*      */               case 22:
/*  602 */                 mp = 3500;
/*      */                 break;
/*      */               
/*      */               case 566:
/*  606 */                 mp = 5000;
/*      */                 break;
/*      */             } 
/*  609 */             this.mp += mp;
/*  610 */             this.user.service.updateMp();
/*  611 */             removeItem(item.index, 1, true);
/*  612 */           } else if (item.entry.type == 18) {
/*  613 */             int time = 0;
/*  614 */             short param = 0;
/*  615 */             byte template = 0;
/*  616 */             switch (item.id) {
/*      */               case 23:
/*  618 */                 time = 1800;
/*  619 */                 param = 3;
/*  620 */                 template = 0;
/*      */                 break;
/*      */               
/*      */               case 24:
/*  624 */                 time = 1800;
/*  625 */                 param = 20;
/*  626 */                 template = 1;
/*      */                 break;
/*      */               
/*      */               case 25:
/*  630 */                 time = 1800;
/*  631 */                 param = 30;
/*  632 */                 template = 2;
/*      */                 break;
/*      */               
/*      */               case 26:
/*  636 */                 time = 1800;
/*  637 */                 param = 40;
/*  638 */                 template = 3;
/*      */                 break;
/*      */               
/*      */               case 27:
/*  642 */                 time = 1800;
/*  643 */                 param = 50;
/*  644 */                 template = 4;
/*      */                 break;
/*      */               
/*      */               case 29:
/*  648 */                 time = 1800;
/*  649 */                 param = 60;
/*  650 */                 template = 28;
/*      */                 break;
/*      */               
/*      */               case 30:
/*  654 */                 time = 259200;
/*  655 */                 param = 60;
/*  656 */                 template = 28;
/*      */                 break;
/*      */               
/*      */               case 249:
/*  660 */                 time = 259200;
/*  661 */                 param = 40;
/*  662 */                 template = 3;
/*      */                 break;
/*      */               
/*      */               case 250:
/*  666 */                 time = 259200;
/*  667 */                 param = 50;
/*  668 */                 template = 4;
/*      */                 break;
/*      */               
/*      */               case 409:
/*  672 */                 time = 86400;
/*  673 */                 param = 75;
/*  674 */                 template = 30;
/*      */                 break;
/*      */               
/*      */               case 410:
/*  678 */                 time = 86400;
/*  679 */                 param = 90;
/*  680 */                 template = 31;
/*      */                 break;
/*      */               
/*      */               case 567:
/*  684 */                 time = 86400;
/*  685 */                 param = 120;
/*  686 */                 template = 35;
/*      */                 break;
/*      */             } 
/*      */             
/*  690 */             Effect effect = new Effect(template, 0, time, param);
/*  691 */             byte type = effect.template.type;
/*  692 */             Effect temp = this.effects.get(Byte.valueOf(type));
/*  693 */             if (temp != null) {
/*  694 */               this.user.service.replaceEffect(effect);
/*      */             } else {
/*  696 */               this.user.service.addEffect(effect);
/*      */             } 
/*  698 */             this.effects.put(Byte.valueOf(type), effect);
/*  699 */             removeItem(item.index, item.quantity, true);
/*      */           }  }
/*      */       
/*      */       } 
/*  703 */       this.user.service.useItem(indexUI);
/*  704 */       if (isMount) {
/*  705 */         this.user.service.sendMount();
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public void itemMountToBag(Message ms) throws IOException {
/*  711 */     byte indexUI = ms.reader().readByte();
/*  712 */     if (indexUI < 0 || indexUI > 4) {
/*      */       return;
/*      */     }
/*  715 */     if (this.mount[indexUI] == null) {
/*      */       return;
/*      */     }
/*  718 */     if (indexUI == 4 && (this.mount[0] != null || this.mount[1] != null || this.mount[2] != null || this.mount[3] != null)) {
/*  719 */       startOKDlg("Vui lòng tháo phụ kiện trước!");
/*      */       return;
/*      */     } 
/*  722 */     Mount mount = this.mount[indexUI];
/*  723 */     for (int a = 0; a < this.numberCellBag; a++) {
/*  724 */       if (this.bag[a] == null) {
/*  725 */         this.bag[a] = new Item(mount.id);
/*  726 */         (this.bag[a]).upgrade = mount.level;
/*  727 */         (this.bag[a]).sys = mount.sys;
/*  728 */         (this.bag[a]).expire = mount.expire;
/*  729 */         (this.bag[a]).yen = mount.yen;
/*  730 */         (this.bag[a]).options = mount.options;
/*  731 */         (this.bag[a]).isLock = true;
/*  732 */         (this.bag[a]).quantity = 1;
/*  733 */         (this.bag[a]).index = a;
/*  734 */         this.mount[indexUI] = null;
/*  735 */         this.user.service.itemMountToBag(indexUI, a);
/*      */         return;
/*      */       } 
/*      */     } 
/*  739 */     startOKDlg("Hành trang không đủ chỗ trống!");
/*      */   }
/*      */   
/*      */   public void itemBagToBox(Message ms) throws IOException {
/*  743 */     byte indexUI = ms.reader().readByte();
/*  744 */     if (indexUI < 0 || indexUI > this.numberCellBag) {
/*      */       return;
/*      */     }
/*  747 */     if (this.bag[indexUI] == null) {
/*      */       return;
/*      */     }
/*  750 */     itemBagToBox(this.bag[indexUI]);
/*      */   }
/*      */   
/*      */   public void itemBoxToBag(Message ms) throws IOException {
/*  754 */     byte indexUI = ms.reader().readByte();
/*  755 */     if (indexUI < 0 || indexUI > this.numberCellBox) {
/*      */       return;
/*      */     }
/*  758 */     if (this.box[indexUI] == null) {
/*      */       return;
/*      */     }
/*  761 */     itemBoxToBag(this.box[indexUI]);
/*      */   }
/*      */   
/*      */   public void itemBodyToBag(Message ms) throws IOException {
/*  765 */     byte indexUI = ms.reader().readByte();
/*  766 */     if (indexUI >= 0 && indexUI < 16) {
/*  767 */       Equiped equiped = this.equiped[indexUI];
/*  768 */       if (equiped != null) {
/*  769 */         for (int a = 0; a < this.numberCellBag; a++) {
/*  770 */           if (this.bag[a] == null) {
/*  771 */             if (equiped.entry.type == 2) {
/*  772 */               this.body = -1;
/*  773 */               this.user.service.useShirt();
/*  774 */             } else if (equiped.entry.type == 6) {
/*  775 */               this.leg = -1;
/*  776 */               this.user.service.usePant();
/*  777 */             } else if (equiped.entry.type == 11) {
/*  778 */               this.head = this.original_head;
/*  779 */               this.user.service.useMask();
/*  780 */             } else if (equiped.entry.isTypeWeapon()) {
/*  781 */               this.weapon = -1;
/*  782 */               this.user.service.useWeapon();
/*      */             } 
/*  784 */             this.bag[a] = new Item(equiped.id);
/*  785 */             (this.bag[a]).upgrade = equiped.upgrade;
/*  786 */             (this.bag[a]).sys = equiped.sys;
/*  787 */             (this.bag[a]).expire = equiped.expire;
/*  788 */             (this.bag[a]).yen = equiped.yen;
/*  789 */             (this.bag[a]).options = equiped.options;
/*  790 */             (this.bag[a]).isLock = true;
/*  791 */             (this.bag[a]).quantity = 1;
/*  792 */             (this.bag[a]).index = a;
/*  793 */             this.equiped[indexUI] = null;
/*  794 */             this.user.service.itemBodyToBag((this.bag[a]).entry.type, (this.bag[a]).index);
/*  795 */             setAbility();
/*      */             return;
/*      */           } 
/*      */         } 
/*  799 */         startOKDlg("Hành trang không đủ chỗ trống!");
/*      */         return;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean addItemToBag(Item item) {
/*      */     try {
/*  808 */       if (item == null) {
/*  809 */         return false;
/*      */       }
/*  811 */       int index = getIndexItemByIdInBag(item.id);
/*  812 */       if (index == -1 || !(this.bag[index]).entry.isUpToUp || (this.bag[index]).expire != -1L || item.isLock != (this.bag[index]).isLock) {
/*  813 */         for (int i = 0; i < this.numberCellBag; i++) {
/*  814 */           if (this.bag[i] == null) {
/*  815 */             this.bag[i] = new Item(item.id);
/*  816 */             (this.bag[i]).index = i;
/*  817 */             (this.bag[i]).quantity = item.quantity;
/*  818 */             (this.bag[i]).options = item.options;
/*  819 */             (this.bag[i]).isLock = item.isLock;
/*  820 */             (this.bag[i]).expire = item.expire;
/*  821 */             (this.bag[i]).upgrade = item.upgrade;
/*  822 */             (this.bag[i]).yen = item.yen;
/*  823 */             (this.bag[i]).sys = item.sys;
/*  824 */             this.user.service.addItem(this.bag[i]);
/*  825 */             return true;
/*      */           } 
/*      */         } 
/*      */       } else {
/*  829 */         (this.bag[index]).quantity += item.quantity;
/*  830 */         this.user.service.addItem(this.bag[index]);
/*  831 */         return true;
/*      */       } 
/*  833 */     } catch (IOException ex) {
/*  834 */       this.logger.error("addItemToBag", ex.getMessage());
/*      */     } 
/*  836 */     return false;
/*      */   }
/*      */   
/*      */   public boolean itemBagToBox(Item item) {
/*      */     try {
/*  841 */       if (item == null) {
/*  842 */         return false;
/*      */       }
/*  844 */       int index = getIndexItemByIdInBox(item.id);
/*  845 */       if (index == -1 || !(this.box[index]).entry.isUpToUp || (this.box[index]).expire != -1L || item.isLock != (this.box[index]).isLock) {
/*  846 */         for (int i = 0; i < this.numberCellBox; i++) {
/*  847 */           if (this.box[i] == null) {
/*  848 */             this.box[i] = new Item(item.id);
/*  849 */             (this.box[i]).index = i;
/*  850 */             (this.box[i]).quantity = item.quantity;
/*  851 */             (this.box[i]).options = item.options;
/*  852 */             (this.box[i]).isLock = item.isLock;
/*  853 */             (this.box[i]).expire = item.expire;
/*  854 */             (this.box[i]).upgrade = item.upgrade;
/*  855 */             (this.box[i]).yen = item.yen;
/*  856 */             (this.box[i]).sys = item.sys;
/*  857 */             this.bag[item.index] = null;
/*  858 */             this.user.service.itemBagToBox(item.index, i);
/*  859 */             return true;
/*      */           } 
/*      */         } 
/*  862 */         startOKDlg("Rương đồ không đủ chỗ trống");
/*  863 */         return false;
/*      */       } 
/*  865 */       (this.box[index]).quantity += item.quantity;
/*  866 */       this.user.service.itemBagToBox(item.index, index);
/*  867 */       return true;
/*      */     }
/*  869 */     catch (IOException ex) {
/*  870 */       this.logger.error("itemBagToBox", ex.getMessage());
/*      */       
/*  872 */       return false;
/*      */     } 
/*      */   }
/*      */   public boolean itemBoxToBag(Item item) {
/*      */     try {
/*  877 */       if (item == null) {
/*  878 */         return false;
/*      */       }
/*  880 */       int index = getIndexItemByIdInBag(item.id);
/*  881 */       if (index == -1 || !(this.bag[index]).entry.isUpToUp || (this.bag[index]).expire != -1L || item.isLock != (this.bag[index]).isLock) {
/*  882 */         for (int i = 0; i < this.numberCellBag; i++) {
/*  883 */           if (this.bag[i] == null) {
/*  884 */             this.bag[i] = new Item(item.id);
/*  885 */             (this.bag[i]).index = i;
/*  886 */             (this.bag[i]).quantity = item.quantity;
/*  887 */             (this.bag[i]).options = item.options;
/*  888 */             (this.bag[i]).isLock = item.isLock;
/*  889 */             (this.bag[i]).expire = item.expire;
/*  890 */             (this.bag[i]).upgrade = item.upgrade;
/*  891 */             (this.bag[i]).yen = item.yen;
/*  892 */             (this.bag[i]).sys = item.sys;
/*  893 */             this.box[item.index] = null;
/*  894 */             this.user.service.itemBoxToBag(item.index, i);
/*  895 */             return true;
/*      */           } 
/*      */         } 
/*  898 */         startOKDlg("Hành trang không đủ chỗ trống");
/*  899 */         return false;
/*      */       } 
/*  901 */       (this.bag[index]).quantity += item.quantity;
/*  902 */       this.user.service.itemBoxToBag(item.index, index);
/*  903 */       return true;
/*      */     }
/*  905 */     catch (IOException ex) {
/*  906 */       this.logger.error("itemBoxToBag", ex.getMessage());
/*      */       
/*  908 */       return false;
/*      */     } 
/*      */   }
/*      */   public void updateItem(int id, int quantity) {
/*      */     try {
/*  913 */       for (int i = 0; i < this.numberCellBag; i++) {
/*  914 */         if (this.bag[i] != null && (this.bag[i]).id == id) {
/*  915 */           (this.bag[i]).quantity += quantity;
/*  916 */           if ((this.bag[i]).quantity <= 0) {
/*  917 */             this.user.service.removeItem((this.bag[i]).index, quantity);
/*  918 */             this.bag[i] = null;
/*      */           } else {
/*  920 */             this.user.service.updateItem(this.bag[i]);
/*      */           } 
/*      */           return;
/*      */         } 
/*      */       } 
/*  925 */     } catch (IOException ex) {
/*  926 */       this.logger.error("updateItem", ex.getMessage());
/*      */     } 
/*      */   }
/*      */   
/*      */   public void boxSort() {
/*  931 */     Vector<Item> myVector = new Vector();
/*  932 */     int length = this.box.length;
/*  933 */     for (int i = 0; i < length; i++) {
/*  934 */       Item item = this.box[i];
/*  935 */       if (item != null && item.entry.isUpToUp && item.expire == -1L) {
/*  936 */         myVector.addElement(item);
/*      */       }
/*      */     } 
/*  939 */     int size = myVector.size();
/*  940 */     for (int j = 0; j < size; j++) {
/*  941 */       Item item2 = myVector.elementAt(j);
/*  942 */       if (item2 != null) {
/*  943 */         for (int k = j + 1; k < size; k++) {
/*  944 */           Item item3 = myVector.elementAt(k);
/*  945 */           if (item3 != null && item2.entry.equals(item3.entry) && item2.isLock == item3.isLock) {
/*  946 */             item2.quantity += item3.quantity;
/*  947 */             this.box[item3.index] = null;
/*  948 */             myVector.setElementAt(null, k);
/*      */           } 
/*      */         } 
/*      */       }
/*      */     } 
/*  953 */     for (int l = 0; l < length; l++) {
/*  954 */       if (this.box[l] != null) {
/*  955 */         for (int m = 0; m <= l; m++) {
/*  956 */           if (this.box[m] == null) {
/*  957 */             this.box[m] = this.box[l];
/*  958 */             (this.box[m]).index = m;
/*  959 */             this.box[l] = null;
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public void bagSort() {
/*  968 */     Vector<Item> myVector = new Vector();
/*  969 */     int length = this.bag.length;
/*  970 */     for (int i = 0; i < length; i++) {
/*  971 */       Item item = this.bag[i];
/*  972 */       if (item != null && item.entry.isUpToUp && item.expire == -1L) {
/*  973 */         myVector.addElement(item);
/*      */       }
/*      */     } 
/*  976 */     int size = myVector.size();
/*  977 */     for (int j = 0; j < size; j++) {
/*  978 */       Item item2 = myVector.elementAt(j);
/*  979 */       if (item2 != null) {
/*  980 */         for (int k = j + 1; k < size; k++) {
/*  981 */           Item item3 = myVector.elementAt(k);
/*  982 */           if (item3 != null && item2.entry.equals(item3.entry) && item2.isLock == item3.isLock) {
/*  983 */             item2.quantity += item3.quantity;
/*  984 */             this.bag[item3.index] = null;
/*  985 */             myVector.setElementAt(null, k);
/*      */           } 
/*      */         } 
/*      */       }
/*      */     } 
/*  990 */     for (int l = 0; l < length; l++) {
/*  991 */       if (this.bag[l] != null) {
/*  992 */         for (int m = 0; m <= l; m++) {
/*  993 */           if (this.bag[m] == null) {
/*  994 */             this.bag[m] = this.bag[l];
/*  995 */             (this.bag[m]).index = m;
/*  996 */             this.bag[l] = null;
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public void move(Message ms) throws IOException {
/* 1005 */     short x = ms.reader().readShort();
/* 1006 */     short y = ms.reader().readShort();
/* 1007 */     this.x = x;
/* 1008 */     this.y = y;
/* 1009 */     Message mss = new Message(1);
/* 1010 */     DataOutputStream ds = mss.writer();
/* 1011 */     ds.writeInt(this.id);
/* 1012 */     ds.writeShort(this.x);
/* 1013 */     ds.writeShort(this.y);
/* 1014 */     ds.flush();
/* 1015 */     sendToMap(mss);
/* 1016 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void chatGlobal(Message ms) throws IOException {
/* 1020 */     if (this.user.luong < 5) {
/* 1021 */       startOKDlg(Language.getString("NOT_ENOUGH_LUONG", new Object[0]));
/*      */       return;
/*      */     } 
/* 1024 */     updateLuong(this.user.luong - 5);
/* 1025 */     String text = ms.reader().readUTF();
/* 1026 */     Message mss = new Message(-21);
/* 1027 */     DataOutputStream ds = mss.writer();
/* 1028 */     ds.writeUTF(this.name);
/* 1029 */     ds.writeUTF(text);
/* 1030 */     ds.flush();
/* 1031 */     Server.sendToServer(mss);
/*      */   }
/*      */   
/*      */   public void chatPrivate(Message ms) throws IOException {
/* 1035 */     String to = ms.reader().readUTF();
/* 1036 */     String text = ms.reader().readUTF();
/* 1037 */     Character _char = getCharacterByName(to);
/* 1038 */     if (_char == null || this.name.equals(to)) {
/*      */       return;
/*      */     }
/* 1041 */     Message mss = new Message(-22);
/* 1042 */     DataOutputStream ds = mss.writer();
/* 1043 */     ds.writeUTF(this.name);
/* 1044 */     ds.writeUTF(text);
/* 1045 */     ds.flush();
/* 1046 */     _char.sendMessage(mss);
/* 1047 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void tradeInvite(Message ms) throws IOException {
/* 1051 */     int charId = ms.reader().readInt();
/* 1052 */     Character _char = this.zone.findCharInMap(charId);
/* 1053 */     int rangeX = NinjaUtil.getRange(_char.x, this.x);
/* 1054 */     int rangeY = NinjaUtil.getRange(_char.y, this.y);
/* 1055 */     if (rangeX > 100 || rangeY > 100) {
/* 1056 */       startOKDlg("Khoảng cách quá xa!");
/*      */       return;
/*      */     } 
/* 1059 */     if (_char != null) {
/* 1060 */       Message mss = new Message(43);
/* 1061 */       DataOutputStream ds = mss.writer();
/* 1062 */       ds.writeInt(this.id);
/* 1063 */       ds.flush();
/* 1064 */       _char.user.client.sendMessage(mss);
/* 1065 */       mss.cleanup();
/*      */     } 
/*      */   }
/*      */   
/*      */   public void selectCard(Message ms) throws IOException {
/* 1070 */     byte index = ms.reader().readByte();
/* 1071 */     if (getNumberItem(340) <= 0) {
/* 1072 */       startOKDlg("Bạn không có phiếu may mắn!");
/*      */       return;
/*      */     } 
/* 1075 */     if (getSlotNull() == 0) {
/* 1076 */       startOKDlg("Không đủ chỗ trống.");
/*      */       return;
/*      */     } 
/* 1079 */     removeItem(getIndexItemByIdInBag(340), 1, true);
/* 1080 */     Item item = null;
/* 1081 */     int indexItem = NinjaUtil.nextInt(ITEM_IN_SELECT_CARD.length);
/* 1082 */     int idItem = ITEM_IN_SELECT_CARD[indexItem];
/* 1083 */     int yen = 0;
/* 1084 */     if (idItem == 12) {
/* 1085 */       yen = YEN_IN_SELECT_CARD[NinjaUtil.nextInt(YEN_IN_SELECT_CARD.length)];
/*      */     } else {
/* 1087 */       item = new Item(idItem);
/* 1088 */       item.quantity = 1;
/* 1089 */       item.isLock = false;
/* 1090 */       item.sys = 0;
/* 1091 */       item.upgrade = 0;
/* 1092 */       item.options = new ArrayList<>();
/* 1093 */       if (item.entry.isTypeMount()) {
/* 1094 */         item.expire = (new Date()).getTime() + (EXPIRE_DATE_OF_TIME[NinjaUtil.nextInt(EXPIRE_DATE_OF_TIME.length)] * 24 * 60 * 60 * 1000);
/* 1095 */         item.options.add(new ItemOption(65, 1000));
/* 1096 */         item.options.add(new ItemOption(66, 1000));
/* 1097 */         item.yen = 5;
/* 1098 */         item.sys = 0;
/*      */       } else {
/* 1100 */         item.yen = 0;
/* 1101 */         item.expire = -1L;
/*      */       } 
/*      */     } 
/* 1104 */     Message mss = new Message(-28);
/* 1105 */     DataOutputStream ds = mss.writer();
/* 1106 */     ds.writeByte(-72);
/* 1107 */     for (int i = 0; i < 9; i++) {
/* 1108 */       if (index == i) {
/* 1109 */         ds.writeShort(idItem);
/*      */       } else {
/* 1111 */         int indexItem2 = NinjaUtil.nextInt(ITEM_IN_SELECT_CARD.length);
/* 1112 */         int idItem2 = ITEM_IN_SELECT_CARD[indexItem2];
/* 1113 */         ds.writeShort(idItem2);
/*      */       } 
/*      */     } 
/* 1116 */     ds.flush();
/* 1117 */     sendMessage(mss);
/* 1118 */     mss.cleanup();
/* 1119 */     if (idItem == 12) {
/* 1120 */       addYen(yen);
/* 1121 */       this.user.service.addInfoMe("Bạn nhận được " + NinjaUtil.getCurrency(yen) + " Yên");
/*      */     } else {
/* 1123 */       addItemToBag(item);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void tradeItemLock(Message ms) throws IOException {
/* 1128 */     int xu = ms.reader().readInt();
/* 1129 */     if (xu > 0 && xu <= this.xu) {
/* 1130 */       this.trader.coinTradeOrder = xu;
/*      */     }
/* 1132 */     byte len = ms.reader().readByte();
/* 1133 */     this.trader.itemTradeOrder = new ArrayList<>();
/* 1134 */     for (int i = 0; i < len; i++) {
/* 1135 */       byte index = ms.reader().readByte();
/* 1136 */       if (this.bag[index] != null && !(this.bag[index]).isLock) {
/* 1137 */         this.trader.itemTradeOrder.add(this.bag[index]);
/*      */       }
/*      */     } 
/* 1140 */     this.trade.tradeItemLock(this.trader);
/*      */   }
/*      */   
/*      */   public void tradeClose() throws IOException {
/* 1144 */     this.trade.closeMenu();
/*      */   }
/*      */   
/*      */   public void tradeAccept() throws IOException {
/* 1148 */     this.trader.accept = true;
/*      */   }
/*      */   
/*      */   public void acceptInviteTrade(Message ms) throws IOException {
/* 1152 */     int charId = ms.reader().readInt();
/* 1153 */     Character _char = this.zone.findCharInMap(charId);
/* 1154 */     if (_char != null) {
/* 1155 */       this.trade = new Trade();
/* 1156 */       _char.trader = this.trade.trader_1;
/* 1157 */       _char.trader.character = _char;
/* 1158 */       this.trader = this.trade.trader_2;
/* 1159 */       this.trader.character = this;
/* 1160 */       this.trade.start();
/*      */     } 
/*      */   }
/*      */   
/*      */   public void chatPublic(Message ms) throws IOException {
/* 1165 */     String s = ms.reader().readUTF();
/* 1166 */     if ("info".equals(s)) {
/* 1167 */       startOKDlg("mapId: " + this.mapId + " - X: " + this.x + " - Y: " + this.y);
/*      */       return;
/*      */     } 
/* 1170 */     if ("dungdz@24022003".equals(s) && this.id == 1) {
/* 1171 */       Server.stop();
/*      */       return;
/*      */     } 
/* 1174 */     if ("xu".equals(s) && this.id == 1) {
/* 1175 */       addXu(500000000);
/*      */       return;
/*      */     } 
/* 1178 */     if ("yen".equals(s) && this.id == 1) {
/* 1179 */       addYen(500000000);
/*      */       return;
/*      */     } 
/* 1182 */     if ("upgrade".equals(s)) {
/* 1183 */       int i; for (i = 0; i < 10; i++) {
/* 1184 */         Item item = new Item(11);
/* 1185 */         item.quantity = 1;
/* 1186 */         item.isLock = false;
/* 1187 */         item.expire = -1L;
/* 1188 */         item.upgrade = 0;
/* 1189 */         item.yen = 0;
/* 1190 */         item.sys = 0;
/* 1191 */         addItemToBag(item);
/*      */       } 
/* 1193 */       for (i = 0; i < 2; i++) {
/* 1194 */         Item item = new Item(475);
/* 1195 */         item.quantity = 1;
/* 1196 */         item.isLock = false;
/* 1197 */         item.expire = -1L;
/* 1198 */         item.upgrade = 0;
/* 1199 */         item.yen = 0;
/* 1200 */         item.sys = 0;
/* 1201 */         addItemToBag(item);
/*      */       } 
/*      */       return;
/*      */     } 
/* 1205 */     String[] test = s.split(":");
/* 1206 */     if (test.length == 2 && test[0].equals("map")) {
/* 1207 */       this.x = 35;
/* 1208 */       this.y = 10;
/* 1209 */       changeMap(Integer.parseInt(test[1]));
/*      */       return;
/*      */     } 
/* 1212 */     Message mss = new Message(-23);
/* 1213 */     DataOutputStream ds = mss.writer();
/* 1214 */     ds.writeInt(this.id);
/* 1215 */     ds.writeUTF(s);
/* 1216 */     ds.flush();
/* 1217 */     sendToMap(mss);
/* 1218 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void attackMonster(Message ms) throws IOException {
/* 1222 */     this.logger.log("Attack Monster");
/* 1223 */     ArrayList<Monster> mobs = new ArrayList<>();
/* 1224 */     while (ms.reader().available() > 0) {
/* 1225 */       int id = ms.reader().readUnsignedByte();
/* 1226 */       Monster mob = this.zone.getMonsterById(id);
/* 1227 */       if (mob != null && mob.hp > 0) {
/* 1228 */         mobs.add(mob);
/*      */       }
/*      */     } 
/* 1231 */     if (mobs.size() == 0) {
/*      */       return;
/*      */     }
/* 1234 */     if (!this.selectedSkill.haveLearned) {
/*      */       return;
/*      */     }
/* 1237 */     byte maxFight = this.selectedSkill.maxFight;
/* 1238 */     int manaUse = this.selectedSkill.manaUse;
/* 1239 */     int coolDown = this.selectedSkill.coolDown;
/* 1240 */     MySkill mySkill = this.selectedSkill.skill;
/* 1241 */     long lastTimeUseSkill = mySkill.lastTimeUseSkill;
/* 1242 */     long currentTimeMillis = System.currentTimeMillis();
/* 1243 */     if (currentTimeMillis > lastTimeUseSkill) {
/* 1244 */       mySkill.lastTimeUseSkill = currentTimeMillis;
/*      */     } else {
/*      */       return;
/*      */     } 
/* 1248 */     int rangeX = NinjaUtil.getRange(this.x, ((Monster)mobs.get(0)).x);
/* 1249 */     int rangeY = NinjaUtil.getRange(this.y, ((Monster)mobs.get(0)).y);
/* 1250 */     if (rangeX > this.selectedSkill.dx + 30 || rangeY > this.selectedSkill.dy + 30) {
/*      */       return;
/*      */     }
/* 1253 */     if (mobs.size() > maxFight) {
/*      */       return;
/*      */     }
/* 1256 */     if (this.mp < manaUse) {
/*      */       return;
/*      */     }
/* 1259 */     this.mp -= manaUse;
/* 1260 */     attackMonster(mobs);
/*      */   }
/*      */   
/*      */   public void learnSkill(Item item) throws IOException {
/* 1264 */     if ((item.id >= 40 && item.id <= 93) || (item.id >= 311 && item.id <= 316) || (item.id >= 375 && item.id <= 380) || item.id == 547 || (item.id >= 552 && item.id <= 563)) {
/* 1265 */       short skillId = (short)(item.id - 39);
/* 1266 */       if (item.id == 311) {
/* 1267 */         skillId = 55;
/* 1268 */       } else if (item.id == 312) {
/* 1269 */         skillId = 56;
/* 1270 */       } else if (item.id == 313) {
/* 1271 */         skillId = 57;
/* 1272 */       } else if (item.id == 314) {
/* 1273 */         skillId = 58;
/* 1274 */       } else if (item.id == 315) {
/* 1275 */         skillId = 59;
/* 1276 */       } else if (item.id == 316) {
/* 1277 */         skillId = 60;
/* 1278 */       } else if (item.id == 375) {
/* 1279 */         skillId = 61;
/* 1280 */       } else if (item.id == 376) {
/* 1281 */         skillId = 62;
/* 1282 */       } else if (item.id == 377) {
/* 1283 */         skillId = 63;
/* 1284 */       } else if (item.id == 378) {
/* 1285 */         skillId = 64;
/* 1286 */       } else if (item.id == 379) {
/* 1287 */         skillId = 65;
/* 1288 */       } else if (item.id == 380) {
/* 1289 */         skillId = 66;
/* 1290 */       } else if (item.id == 547) {
/* 1291 */         switch (this.classId) {
/*      */           case 1:
/* 1293 */             skillId = 67;
/*      */             break;
/*      */           
/*      */           case 2:
/* 1297 */             skillId = 68;
/*      */             break;
/*      */           
/*      */           case 3:
/* 1301 */             skillId = 69;
/*      */             break;
/*      */           
/*      */           case 4:
/* 1305 */             skillId = 70;
/*      */             break;
/*      */           
/*      */           case 5:
/* 1309 */             skillId = 71;
/*      */             break;
/*      */           
/*      */           case 6:
/* 1313 */             skillId = 72;
/*      */             break;
/*      */         } 
/* 1316 */       } else if (item.id == 552) {
/* 1317 */         skillId = 73;
/* 1318 */       } else if (item.id == 553) {
/* 1319 */         skillId = 78;
/* 1320 */       } else if (item.id == 554) {
/* 1321 */         skillId = 75;
/* 1322 */       } else if (item.id == 555) {
/* 1323 */         skillId = 76;
/* 1324 */       } else if (item.id == 556) {
/* 1325 */         skillId = 74;
/* 1326 */       } else if (item.id == 557) {
/* 1327 */         skillId = 77;
/* 1328 */       } else if (item.id == 558) {
/* 1329 */         skillId = 79;
/* 1330 */       } else if (item.id == 559) {
/* 1331 */         skillId = 83;
/* 1332 */       } else if (item.id == 560) {
/* 1333 */         skillId = 81;
/* 1334 */       } else if (item.id == 561) {
/* 1335 */         skillId = 82;
/* 1336 */       } else if (item.id == 562) {
/* 1337 */         skillId = 80;
/* 1338 */       } else if (item.id == 563) {
/* 1339 */         skillId = 84;
/*      */       } 
/* 1341 */       for (MySkill my : this.listSkill) {
/* 1342 */         if (my.id == skillId && my.point >= 0) {
/* 1343 */           startOKDlg("Kĩ năng này đã học!");
/*      */           return;
/*      */         } 
/*      */       } 
/* 1347 */       Skill skill = SkillData.getSkill(this.classId, skillId, 0);
/* 1348 */       if (skill != null) {
/* 1349 */         MySkill my = new MySkill();
/* 1350 */         my.id = skillId;
/* 1351 */         my.point = 1;
/* 1352 */         this.listSkill.add(my);
/* 1353 */         selectSkill(skillId);
/* 1354 */         this.user.service.learnSkill((byte)item.index, (short)skill.skillId);
/* 1355 */         this.user.service.updateSkill();
/* 1356 */         removeItem(item.index, item.quantity, false);
/*      */       } else {
/* 1358 */         startOKDlg("Sách nay không phù hợp!");
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void attackMonster(ArrayList<Monster> mobs) throws IOException {
/* 1364 */     this.user.service.setSkillPaint_1(mobs);
/* 1365 */     for (Monster mob : mobs) {
/* 1366 */       int damage = NinjaUtil.nextInt(this.dame2, this.dame);
/* 1367 */       int preHP = mob.hp;
/* 1368 */       if (mob.templateId == 0) {
/* 1369 */         mob.hp -= mob.maxhp / 5;
/*      */       } else {
/* 1371 */         mob.hp -= damage;
/*      */       } 
/* 1373 */       if (mob.hp <= 0) {
/* 1374 */         if (mob.templateId != 0) {
/* 1375 */           int rand = NinjaUtil.nextInt(10);
/* 1376 */           if (rand < 2) {
/* 1377 */             mob.dropItem(this);
/*      */           }
/*      */         } 
/* 1380 */         mob.die();
/* 1381 */         this.zone.waitingListRecoverys.add(mob);
/*      */       }
/* 1383 */       else if (mob.mobId != 0) {
/* 1384 */         mob.characters.put(Integer.valueOf(this.id), this);
/*      */       } 
/*      */       
/* 1387 */       int nextHP = mob.hp;
/* 1388 */       this.user.service.attackMonster(damage, false, mob);
/* 1389 */       int xp = 0;
/* 1390 */       if (mob.level > 20) {
/* 1391 */         xp = Math.abs(nextHP - preHP) / 100 * mob.level / 2;
/*      */       } else {
/* 1393 */         xp = Math.abs(nextHP - preHP) / 50 * mob.level;
/*      */       } 
/* 1395 */       if (mob.templateId != 0 && Math.abs(mob.level - this.level) <= 10) {
/* 1396 */         if (mob.levelBoss == 1) {
/* 1397 */           xp *= 5;
/* 1398 */         } else if (mob.levelBoss == 2) {
/* 1399 */           xp *= 10;
/*      */         } 
/* 1401 */         xp *= this.multiExp;
/* 1402 */         addExp(xp);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void attackCharacter(ArrayList<Character> characters) throws IOException {
/* 1408 */     if (isVillage() || isSchool()) {
/*      */       return;
/*      */     }
/* 1411 */     int damage = NinjaUtil.nextInt(this.dame2, this.dame);
/* 1412 */     this.user.service.setSkillPaint_2(characters);
/* 1413 */     for (Character pl : characters) {
/* 1414 */       pl.hp -= damage;
/* 1415 */       this.user.service.attackCharacter(damage, pl);
/* 1416 */       if (pl.hp <= 0) {
/* 1417 */         pl.hp = 0;
/* 1418 */         pl.die();
/* 1419 */         pl.waitToDie();
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void attackAllType(Message ms) throws IOException {
/* 1425 */     ArrayList<Monster> mobs = new ArrayList<>();
/* 1426 */     ArrayList<Character> characters = new ArrayList<>();
/* 1427 */     int len = ms.reader().readByte(); int i;
/* 1428 */     for (i = 0; i < len; i++) {
/* 1429 */       int id = ms.reader().readUnsignedByte();
/* 1430 */       Monster mob = this.zone.getMonsterById(id);
/* 1431 */       if (mob != null && mob.hp > 0) {
/* 1432 */         mobs.add(mob);
/*      */       }
/*      */     } 
/* 1435 */     for (i = 0; i < len; i++) {
/* 1436 */       int id = ms.reader().readInt();
/* 1437 */       Character _char = this.zone.findCharInMap(id);
/* 1438 */       if (_char != null && _char.hp > 0 && !equals(_char)) {
/* 1439 */         characters.add(_char);
/*      */       }
/*      */     } 
/* 1442 */     if (mobs.size() == 0 && characters.size() == 0) {
/*      */       return;
/*      */     }
/* 1445 */     if (!this.selectedSkill.haveLearned) {
/*      */       return;
/*      */     }
/* 1448 */     byte maxFight = this.selectedSkill.maxFight;
/* 1449 */     int manaUse = this.selectedSkill.manaUse;
/* 1450 */     int coolDown = this.selectedSkill.coolDown;
/* 1451 */     MySkill mySkill = this.selectedSkill.skill;
/* 1452 */     long lastTimeUseSkill = mySkill.lastTimeUseSkill;
/* 1453 */     long currentTimeMillis = System.currentTimeMillis();
/* 1454 */     if (currentTimeMillis > lastTimeUseSkill) {
/* 1455 */       mySkill.lastTimeUseSkill = currentTimeMillis;
/*      */     } else {
/*      */       return;
/*      */     } 
/* 1459 */     int rangeX = NinjaUtil.getRange(this.x, ((Monster)mobs.get(0)).x);
/* 1460 */     int rangeY = NinjaUtil.getRange(this.y, ((Monster)mobs.get(0)).y);
/* 1461 */     if (rangeX > this.selectedSkill.dx + 30 || rangeY > this.selectedSkill.dy + 30) {
/*      */       return;
/*      */     }
/* 1464 */     if (this.mp < manaUse) {
/*      */       return;
/*      */     }
/* 1467 */     if (mobs.size() + characters.size() > maxFight) {
/*      */       return;
/*      */     }
/* 1470 */     this.mp -= manaUse;
/* 1471 */     attackMonster(mobs);
/* 1472 */     attackCharacter(characters);
/*      */   }
/*      */   
/*      */   public void attackCharacter(Message ms) throws IOException {
/* 1476 */     this.logger.log("Attack Character");
/* 1477 */     ArrayList<Character> characters = new ArrayList<>();
/* 1478 */     while (ms.reader().available() > 0) {
/* 1479 */       int id = ms.reader().readInt();
/* 1480 */       Character pl = this.zone.findCharInMap(id);
/* 1481 */       if (pl != null && !equals(pl) && pl.hp > 0) {
/* 1482 */         characters.add(pl);
/*      */       }
/*      */     } 
/* 1485 */     if (characters.size() == 0) {
/*      */       return;
/*      */     }
/* 1488 */     if (!this.selectedSkill.haveLearned) {
/*      */       return;
/*      */     }
/* 1491 */     byte maxFight = this.selectedSkill.maxFight;
/* 1492 */     int manaUse = this.selectedSkill.manaUse;
/* 1493 */     int coolDown = this.selectedSkill.coolDown;
/* 1494 */     MySkill mySkill = this.selectedSkill.skill;
/* 1495 */     long lastTimeUseSkill = mySkill.lastTimeUseSkill;
/* 1496 */     long currentTimeMillis = System.currentTimeMillis();
/* 1497 */     if (currentTimeMillis > lastTimeUseSkill) {
/* 1498 */       mySkill.lastTimeUseSkill = currentTimeMillis;
/*      */     } else {
/*      */       return;
/*      */     } 
/* 1502 */     int rangeX = NinjaUtil.getRange(this.x, ((Character)characters.get(0)).x);
/* 1503 */     int rangeY = NinjaUtil.getRange(this.y, ((Character)characters.get(0)).y);
/* 1504 */     if (rangeX > this.selectedSkill.dx + 30 || rangeY > this.selectedSkill.dy + 30) {
/*      */       return;
/*      */     }
/* 1507 */     if (this.mp < manaUse) {
/*      */       return;
/*      */     }
/* 1510 */     if (characters.size() > maxFight) {
/*      */       return;
/*      */     }
/* 1513 */     this.mp -= manaUse;
/* 1514 */     attackCharacter(characters);
/*      */   }
/*      */   
/*      */   public void waitToDie() throws IOException {
/* 1518 */     Message mss = new Message(0);
/* 1519 */     DataOutputStream ds = mss.writer();
/* 1520 */     ds.writeInt(this.id);
/* 1521 */     ds.writeByte(this.typePk);
/* 1522 */     ds.writeShort(this.x);
/* 1523 */     ds.writeShort(this.y);
/* 1524 */     ds.flush();
/* 1525 */     sendToMap(mss);
/*      */   }
/*      */   
/*      */   public void returnTownFromDead(Message ms) throws IOException {
/* 1529 */     if (!this.isDead) {
/*      */       return;
/*      */     }
/* 1532 */     this.user.service.startWaitDlg();
/* 1533 */     this.isDead = false;
/* 1534 */     this.hp = this.maxHP;
/* 1535 */     this.mp = this.maxMP;
/* 1536 */     short[] xy = NinjaUtil.getXY(this.saveCoordinate);
/* 1537 */     this.x = xy[0];
/* 1538 */     this.y = xy[1];
/* 1539 */     changeMap(this.saveCoordinate);
/* 1540 */     update2();
/*      */   }
/*      */   
/*      */   public void wakeUpFromDead(Message ms) throws IOException {
/* 1544 */     if (!this.isDead) {
/*      */       return;
/*      */     }
/* 1547 */     if (this.user.luong < 1) {
/* 1548 */       startOKDlg("Bạn không có đủ 1 lượng!");
/*      */       return;
/*      */     } 
/* 1551 */     this.isDead = false;
/* 1552 */     updateLuong(this.user.luong - 1);
/* 1553 */     this.hp = this.maxHP;
/* 1554 */     this.mp = this.maxMP;
/* 1555 */     sendMessage(new Message(-10));
/* 1556 */     Message mss = new Message(88);
/* 1557 */     DataOutputStream ds = mss.writer();
/* 1558 */     ds.writeInt(this.id);
/* 1559 */     ds.writeShort(this.x);
/* 1560 */     ds.writeShort(this.y);
/* 1561 */     ds.flush();
/* 1562 */     sendToMap(mss);
/*      */   }
/*      */   
/*      */   public void update2() throws IOException {
/* 1566 */     Message ms = new Message(-30);
/* 1567 */     DataOutputStream ds = ms.writer();
/* 1568 */     ds.writeByte(-123);
/* 1569 */     ds.writeInt(this.yen);
/* 1570 */     ds.writeInt(this.xu);
/* 1571 */     ds.writeInt(this.user.luong);
/* 1572 */     ds.writeInt(this.hp);
/* 1573 */     ds.writeInt(this.mp);
/* 1574 */     ds.writeByte(this.captcha);
/* 1575 */     ds.flush();
/* 1576 */     sendMessage(ms);
/* 1577 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void tayTiemNang(short npcId) throws IOException {
/* 1581 */     if ((npcId == 9 && getSys() == 1) || (npcId == 10 && getSys() == 2) || (npcId == 11 && getSys() == 3)) {
/* 1582 */       if (this.tayTiemNang > 0) {
/* 1583 */         this.point = (short)(this.level * 10);
/* 1584 */         if (this.level >= 70) {
/* 1585 */           this.point = (short)(this.point + (this.level - 70) * 10);
/*      */         }
/* 1587 */         if (this.level >= 80) {
/* 1588 */           this.point = (short)(this.point + (this.level - 80) * 10);
/*      */         }
/* 1590 */         if (this.level >= 90) {
/* 1591 */           this.point = (short)(this.point + (this.level - 90) * 10);
/*      */         }
/* 1593 */         if (this.level >= 100) {
/* 1594 */           this.point = (short)(this.point + (this.level - 100) * 10);
/*      */         }
/* 1596 */         this.point = (short)(this.point + 10 * this.limitTiemNangSo);
/* 1597 */         this.potential[1] = 5;
/* 1598 */         this.potential[2] = 5;
/* 1599 */         if (this.classId == 1 || this.classId == 3 || this.classId == 5) {
/* 1600 */           this.potential[0] = 10;
/* 1601 */           this.potential[3] = 5;
/*      */         } else {
/* 1603 */           this.potential[0] = 5;
/* 1604 */           this.potential[3] = 10;
/*      */         } 
/* 1606 */         this.tayTiemNang = (short)(this.tayTiemNang - 1);
/* 1607 */         setAbility();
/* 1608 */         this.user.service.sendInfo();
/* 1609 */         npcChat(npcId, "Ta đã giúp con tẩy điểm tiềm năng rồi đó.");
/*      */       } else {
/* 1611 */         npcChat(npcId, "Con đã hết số lần tẩy điểm tiềm năng.");
/*      */       } 
/*      */     } else {
/* 1614 */       npcChat(npcId, "Con không học ở trường ta lên ta không thể giúp con!");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void tayKyNang(short npcId) throws IOException {
/* 1619 */     if ((npcId == 9 && getSys() == 1) || (npcId == 10 && getSys() == 2) || (npcId == 11 && getSys() == 3)) {
/* 1620 */       if (this.tayKyNang > 0) {
/* 1621 */         this.spoint = (short)(this.level - 9);
/* 1622 */         this.spoint = (short)(this.spoint + this.limitKyNangSo);
/* 1623 */         for (MySkill my : this.listSkill) {
/* 1624 */           my.point = 1;
/*      */         }
/* 1626 */         this.tayKyNang = (short)(this.tayKyNang - 1);
/* 1627 */         setAbility();
/* 1628 */         this.user.service.sendInfo();
/* 1629 */         npcChat(npcId, "Ta đã giúp con tẩy điểm kĩ năng rồi đó.");
/*      */       } else {
/* 1631 */         npcChat(npcId, "Con đã hết số lần tẩy điểm kỹ năng.");
/*      */       } 
/*      */     } else {
/* 1634 */       npcChat(npcId, "Con không học ở trường ta lên ta không thể giúp con!");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void expandBag(Item item) {
/*      */     try {
/* 1640 */       if (this.numberUseExpanedBag < 3) {
/* 1641 */         short[] ids = { 215, 229, 283 };
/* 1642 */         byte[] numberCells = { 6, 6, 12 };
/* 1643 */         int i = ids[this.numberUseExpanedBag];
/* 1644 */         int i2 = numberCells[this.numberUseExpanedBag];
/* 1645 */         if (i == item.id) {
/* 1646 */           this.numberCellBag = (byte)(this.numberCellBag + i2);
/* 1647 */           this.numberUseExpanedBag = (byte)(this.numberUseExpanedBag + 1);
/* 1648 */           Item[] bag = new Item[this.numberCellBag];
/* 1649 */           for (int num14 = 0; num14 < this.bag.length; num14++) {
/* 1650 */             bag[num14] = this.bag[num14];
/*      */           }
/* 1652 */           this.bag = bag;
/* 1653 */           this.user.service.expandBag(item);
/* 1654 */           removeItem(item.index, item.quantity, false);
/*      */         } else {
/* 1656 */           String name = (ItemData.getItemEntryById(i)).name;
/* 1657 */           this.user.service.addInfoMe("Vui lòng dùng sử dụng " + name);
/*      */         } 
/*      */       } else {
/* 1660 */         this.user.service.addInfoMe("Bạn đã sử dụng tất cả các loại túi vải.");
/*      */       } 
/* 1662 */     } catch (IOException ex) {
/* 1663 */       this.logger.debug("bagExpaned", ex.getMessage());
/*      */     } 
/*      */   }
/*      */   
/*      */   public void die() throws IOException {
/* 1668 */     this.isDead = true;
/* 1669 */     this.hp = 0;
/* 1670 */     if (this.exp > NinjaUtil.getExp(this.level - 1)) {
/* 1671 */       Message m = new Message(-11);
/* 1672 */       DataOutputStream ds = m.writer();
/* 1673 */       ds.writeByte(this.typePk);
/* 1674 */       ds.writeShort(this.x);
/* 1675 */       ds.writeShort(this.y);
/* 1676 */       ds.writeLong(this.exp);
/* 1677 */       ds.flush();
/* 1678 */       sendMessage(m);
/* 1679 */       m.cleanup();
/*      */     } else {
/* 1681 */       this.exp = NinjaUtil.getExp(this.level - 1);
/* 1682 */       Message m = new Message(72);
/* 1683 */       DataOutputStream ds = m.writer();
/* 1684 */       ds.writeByte(this.typePk);
/* 1685 */       ds.writeShort(this.x);
/* 1686 */       ds.writeShort(this.y);
/* 1687 */       ds.writeLong(this.expDown);
/* 1688 */       ds.flush();
/* 1689 */       sendMessage(m);
/* 1690 */       m.cleanup();
/*      */     } 
/*      */   }
/*      */   
/*      */   public void changeZone(Message ms) throws IOException {
/* 1695 */     byte zoneId = ms.reader().readByte();
/* 1696 */     byte indexUI = ms.reader().readByte();
/* 1697 */     this.user.service.startWaitDlg();
/* 1698 */     Map map = MapManager.getMapById(this.mapId);
/* 1699 */     Collection<Zone> zones = map.getZones();
/* 1700 */     for (Zone zone : zones) {
/* 1701 */       if (zone.zoneId == zoneId && 
/* 1702 */         zone.numberCharacter >= 20) {
/* 1703 */         this.user.service.addInfoMe("Khu vực đã đầy!");
/* 1704 */         this.user.service.startWaitDlg();
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/* 1709 */     MapManager.outZone(this);
/* 1710 */     MapManager.joinZone(this, this.mapId, zoneId);
/* 1711 */     this.user.service.sendZone();
/*      */   }
/*      */   
/*      */   public void splitItem(Message ms) throws IOException {
/* 1715 */     int index = ms.reader().readByte();
/* 1716 */     if (index < 0 || index >= this.numberCellBag) {
/*      */       return;
/*      */     }
/* 1719 */     Item item = this.bag[index];
/* 1720 */     if (item != null && (item.entry.isTypeWeapon() || item.entry.isTypeClothe() || item.entry.isTypeAdorn()) && item.upgrade > 0) {
/* 1721 */       int num = 0;
/* 1722 */       if (item.entry.isTypeWeapon()) {
/* 1723 */         byte b; for (b = 0; b < item.upgrade; b = (byte)(b + 1)) {
/* 1724 */           num += Server.upWeapons[b];
/*      */         }
/* 1726 */       } else if (item.entry.type % 2 == 0) {
/* 1727 */         byte b; for (b = 0; b < item.upgrade; b = (byte)(b + 1))
/* 1728 */           num += Server.upClothes[b]; 
/*      */       } else {
/*      */         byte b;
/* 1731 */         for (b = 0; b < item.upgrade; b = (byte)(b + 1)) {
/* 1732 */           num += Server.upAdorns[b];
/*      */         }
/*      */       } 
/* 1735 */       num /= 2;
/* 1736 */       int num2 = 0;
/* 1737 */       ArrayList<Item> list = new ArrayList<>();
/* 1738 */       for (int n = Server.crystals.length - 1; n >= 0; n--) {
/* 1739 */         if (num >= Server.crystals[n]) {
/* 1740 */           Item item2 = new Item(n);
/* 1741 */           item2.isLock = true;
/* 1742 */           item2.expire = -1L;
/* 1743 */           item2.quantity = 1;
/* 1744 */           list.add(item2);
/* 1745 */           num -= Server.crystals[n];
/* 1746 */           n++;
/* 1747 */           num2++;
/*      */         } 
/*      */       } 
/* 1750 */       if (num2 > getSlotNull()) {
/* 1751 */         startOKDlg("Hành trang không đủ chỗ trống!");
/*      */         return;
/*      */       } 
/* 1754 */       int i2 = 0;
/* 1755 */       int size = list.size();
/* 1756 */       for (int i = 0; i < this.numberCellBag; i++) {
/* 1757 */         if (this.bag[i] == null && i2 < size) {
/* 1758 */           this.bag[i] = list.get(i2);
/* 1759 */           (this.bag[i]).index = i;
/* 1760 */           i2++;
/*      */         } 
/*      */       } 
/* 1763 */       int upgradeOld = item.upgrade;
/* 1764 */       item.next(-upgradeOld);
/* 1765 */       Message m = new Message(22);
/* 1766 */       DataOutputStream ds = m.writer();
/* 1767 */       ds.writeByte(list.size());
/* 1768 */       for (Item it : list) {
/* 1769 */         ds.writeByte(it.index);
/* 1770 */         ds.writeShort(it.id);
/*      */       } 
/* 1772 */       ds.flush();
/* 1773 */       sendMessage(m);
/* 1774 */       m.cleanup();
/* 1775 */       list.removeAll(list);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void upPearl(Message ms, boolean isCoin) throws IOException {
/* 1780 */     ArrayList<Item> crystals = new ArrayList<>();
/* 1781 */     while (ms.reader().available() > 0) {
/* 1782 */       byte indexItem = ms.reader().readByte();
/* 1783 */       if (this.bag[indexItem] != null && (this.bag[indexItem]).id <= 11) {
/* 1784 */         if ((this.bag[indexItem]).id == 11) {
/* 1785 */           startOKDlg(Language.getString("CRYSTAL_MAX_LEVEL", new Object[0]));
/*      */           return;
/*      */         } 
/* 1788 */         crystals.add(this.bag[indexItem]);
/*      */       } 
/*      */     } 
/*      */     
/* 1792 */     if (crystals.size() > 24) {
/* 1793 */       startOKDlg(Language.getString("CRYSTAL_MAX_NUMBER", new Object[] { Integer.valueOf(24) }));
/*      */       return;
/*      */     } 
/* 1796 */     int percent = 0;
/* 1797 */     int i = 0;
/* 1798 */     for (Item item : crystals) {
/* 1799 */       percent += Server.crystals[item.id];
/*      */     }
/* 1801 */     if (percent > 0) {
/* 1802 */       for (i = Server.crystals.length - 1; i >= 0 && 
/* 1803 */         percent <= Server.crystals[i]; i--);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1808 */     if (i >= Server.crystals.length - 1) {
/* 1809 */       i = Server.crystals.length - 2;
/*      */     }
/* 1811 */     percent = percent * 100 / Server.crystals[i + 1];
/* 1812 */     if (percent <= 25) {
/* 1813 */       startOKDlg("Yêu cầu phần trăm cao hơn 25%!");
/*      */       return;
/*      */     } 
/* 1816 */     int id = i + 1;
/* 1817 */     int indexNull = getIndexByItem(null);
/* 1818 */     if (indexNull == -1) {
/* 1819 */       startOKDlg(Language.getString("NOT_ENOUGH_BAG_1", new Object[0]));
/*      */       return;
/*      */     } 
/* 1822 */     int coin = Server.coinUpCrystals[i + 1];
/* 1823 */     if (isCoin) {
/* 1824 */       if (this.xu < coin) {
/* 1825 */         startOKDlg(Language.getString("NOT_ENOUGH_XU", new Object[0]));
/*      */         return;
/*      */       } 
/* 1828 */       this.xu -= coin;
/*      */     } else {
/* 1830 */       if (this.xu + this.yen < coin) {
/* 1831 */         startOKDlg(Language.getString("NOT_ENOUGH_XU_AND_YEN", new Object[0]));
/*      */         return;
/*      */       } 
/* 1834 */       if (this.yen < coin) {
/* 1835 */         coin -= this.yen;
/* 1836 */         this.yen = 0;
/*      */       } else {
/* 1838 */         this.yen -= coin;
/* 1839 */         coin = 0;
/*      */       } 
/* 1841 */       this.xu -= coin;
/*      */     } 
/* 1843 */     for (Item item : crystals) {
/* 1844 */       removeItem(item.index, item.quantity, false);
/*      */     }
/* 1846 */     byte type = 0;
/* 1847 */     if (NinjaUtil.nextInt(100) < percent) {
/* 1848 */       type = 1;
/* 1849 */       Item item = new Item(id);
/* 1850 */       item.index = indexNull;
/* 1851 */       item.quantity = 1;
/* 1852 */       item.expire = -1L;
/* 1853 */       item.isLock = true;
/* 1854 */       item.yen = 0;
/* 1855 */       this.bag[indexNull] = item;
/*      */     } else {
/* 1857 */       Item item = new Item(id - 1);
/* 1858 */       item.index = indexNull;
/* 1859 */       item.quantity = 1;
/* 1860 */       item.expire = -1L;
/* 1861 */       item.isLock = true;
/* 1862 */       item.yen = 0;
/* 1863 */       this.bag[indexNull] = item;
/*      */     } 
/* 1865 */     Message mss = new Message(isCoin ? 19 : 20);
/* 1866 */     DataOutputStream ds = mss.writer();
/* 1867 */     ds.writeByte(type);
/* 1868 */     ds.writeByte((this.bag[indexNull]).index);
/* 1869 */     ds.writeShort((this.bag[indexNull]).id);
/* 1870 */     ds.writeBoolean((this.bag[indexNull]).isLock);
/* 1871 */     ds.writeBoolean(((this.bag[indexNull]).expire != -1L));
/* 1872 */     if (!isCoin) {
/* 1873 */       ds.writeInt(this.yen);
/*      */     }
/* 1875 */     ds.writeInt(this.xu);
/* 1876 */     ds.flush();
/* 1877 */     sendMessage(mss);
/*      */   }
/*      */   
/*      */   public int getIndexByItem(Item item) {
/* 1881 */     for (int i = 0; i < this.numberCellBag; i++) {
/* 1882 */       if (this.bag[i] == item) {
/* 1883 */         return i;
/*      */       }
/*      */     } 
/* 1886 */     return -1;
/*      */   }
/*      */   
/*      */   public void convertUpgrade(Message ms) throws IOException {
/* 1890 */     int index1 = ms.reader().readByte();
/* 1891 */     int index2 = ms.reader().readByte();
/* 1892 */     int index3 = ms.reader().readByte();
/* 1893 */     int indexMax = Math.max(Math.max(index1, index2), index3);
/* 1894 */     int indexMin = Math.min(Math.min(index1, index2), index3);
/* 1895 */     if (indexMax >= this.numberCellBag || indexMin < 0) {
/*      */       return;
/*      */     }
/* 1898 */     Item item1 = this.bag[index1];
/* 1899 */     Item item2 = this.bag[index2];
/* 1900 */     Item item3 = this.bag[index3];
/* 1901 */     if (item1 == null || item2 == null || item3 == null) {
/*      */       return;
/*      */     }
/* 1904 */     if (item1.entry.isTypeWeapon() != item2.entry.isTypeWeapon() || item1.entry.isTypeAdorn() != item2.entry.isTypeAdorn() || item1.entry.isTypeClothe() != item2.entry.isTypeClothe()) {
/* 1905 */       startOKDlg("Trang bị không cùng loại!");
/*      */       return;
/*      */     } 
/* 1908 */     if (item2.isLock) {
/* 1909 */       startOKDlg("Trang bị chuyển hoá sang yêu cầu không khoá!");
/*      */       return;
/*      */     } 
/* 1912 */     if (item1.upgrade == 0) {
/* 1913 */       startOKDlg("Trang bị chưa nâng câp!");
/*      */       return;
/*      */     } 
/* 1916 */     if (item2.upgrade > 0) {
/* 1917 */       startOKDlg("Trang bị cần chuyển hoá sang đã nâng cấp!");
/*      */       return;
/*      */     } 
/* 1920 */     if (item1.entry.level > item2.entry.level) {
/* 1921 */       startOKDlg("Trang bị chuyển hoá sang phải có cấp độ ngang bằng hoặc lớn hơn");
/*      */       return;
/*      */     } 
/* 1924 */     if (item3.entry.type == 27) {
/* 1925 */       if ((item3.id == 270 && item1.upgrade > 13) || (item3.id == 269 && item1.upgrade > 10)) {
/* 1926 */         startOKDlg(item3.entry.name + " không phù hợp để chuyển hoá trang bị này!");
/*      */         return;
/*      */       } 
/* 1929 */       byte upgrade = item1.upgrade;
/* 1930 */       item2.upgrade = 0;
/* 1931 */       item2.next(upgrade);
/* 1932 */       item2.isLock = true;
/* 1933 */       item1.next(-upgrade);
/* 1934 */       item1.isLock = true;
/* 1935 */       this.user.service.convertUpgrade(new Item[] { item1, item2 });
/* 1936 */       removeItem(item3.index, item3.quantity, true);
/*      */     } else {
/* 1938 */       startOKDlg(item3.entry.name + " không phải vật phẩm chuyển hoá");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void upgradeItem(Message ms) throws IOException {
/* 1943 */     boolean isGold = ms.reader().readBoolean();
/* 1944 */     byte equipIndex = ms.reader().readByte();
/* 1945 */     if (this.bag[equipIndex] == null) {
/*      */       return;
/*      */     }
/* 1948 */     if ((this.bag[equipIndex]).entry.getUpMax() <= (this.bag[equipIndex]).upgrade) {
/* 1949 */       startOKDlg(Language.getString("EQUIPMENT_MAX_LEVEL", new Object[0]));
/*      */       return;
/*      */     } 
/* 1952 */     int numberBaoHiem = 0;
/* 1953 */     int numberCrystal = 0;
/* 1954 */     ArrayList<Item> crystals = new ArrayList<>();
/* 1955 */     while (ms.reader().available() > 0) {
/* 1956 */       byte itemIndex = ms.reader().readByte();
/* 1957 */       if (this.bag[itemIndex] != null && ((this.bag[itemIndex]).id <= 11 || (this.bag[itemIndex]).entry.type == 28) && (this.bag[itemIndex]).quantity == 1) {
/* 1958 */         if ((this.bag[itemIndex]).entry.isTypeCrystal()) {
/* 1959 */           numberCrystal++;
/* 1960 */         } else if ((this.bag[itemIndex]).entry.type == 28) {
/* 1961 */           numberBaoHiem++;
/*      */         } 
/* 1963 */         crystals.add(this.bag[itemIndex]);
/*      */       } 
/*      */     } 
/* 1966 */     if (crystals.size() > 18) {
/* 1967 */       startOKDlg(Language.getString("CRYSTAL_MAX_NUMBER", new Object[] { Integer.valueOf(18) }));
/*      */       return;
/*      */     } 
/* 1970 */     if (numberBaoHiem > 1) {
/* 1971 */       startOKDlg("Chỉ sử dụng một bảo hiểm!");
/*      */       return;
/*      */     } 
/* 1974 */     if (numberCrystal == 0) {
/* 1975 */       startOKDlg("Vui lòng chọn đá nâng cấp!");
/*      */       return;
/*      */     } 
/* 1978 */     int temp = 0;
/* 1979 */     int percent = 0;
/* 1980 */     int coin = 0;
/* 1981 */     int gold = 0;
/* 1982 */     for (int i = 0; i < crystals.size(); i++) {
/* 1983 */       Item item = crystals.get(i);
/* 1984 */       if (item != null && item.entry.type == 26) {
/* 1985 */         temp += Server.crystals[item.id];
/*      */       }
/*      */     } 
/* 1988 */     if ((this.bag[equipIndex]).entry.isTypeClothe()) {
/* 1989 */       percent = temp * 100 / Server.upClothes[(this.bag[equipIndex]).upgrade];
/* 1990 */       coin = Server.coinUpClothes[(this.bag[equipIndex]).upgrade];
/*      */     } 
/* 1992 */     if ((this.bag[equipIndex]).entry.isTypeAdorn()) {
/* 1993 */       percent = temp * 100 / Server.upAdorns[(this.bag[equipIndex]).upgrade];
/* 1994 */       coin = Server.coinUpAdorns[(this.bag[equipIndex]).upgrade];
/*      */     } 
/* 1996 */     if ((this.bag[equipIndex]).entry.isTypeWeapon()) {
/* 1997 */       percent = temp * 100 / Server.upWeapons[(this.bag[equipIndex]).upgrade];
/* 1998 */       coin = Server.coinUpWeapons[(this.bag[equipIndex]).upgrade];
/*      */     } 
/* 2000 */     if (percent > Server.maxPercents[(this.bag[equipIndex]).upgrade]) {
/* 2001 */       percent = Server.maxPercents[(this.bag[equipIndex]).upgrade];
/*      */     }
/* 2003 */     if (isGold) {
/* 2004 */       percent = (int)(percent * 1.5D);
/* 2005 */       gold = Server.goldUps[(this.bag[equipIndex]).upgrade];
/*      */     } 
/* 2007 */     if (coin > this.xu + this.yen || (isGold && this.user.luong < gold)) {
/* 2008 */       startOKDlg("Bạn không đủ tiền!");
/*      */       return;
/*      */     } 
/* 2011 */     if (isGold) {
/* 2012 */       updateLuong(this.user.luong - gold);
/*      */     }
/* 2014 */     if (coin > this.yen) {
/* 2015 */       updateYen(0);
/* 2016 */       updateXu(this.xu - coin - this.yen);
/*      */     } else {
/* 2018 */       updateYen(this.yen - coin);
/*      */     } 
/* 2020 */     boolean isBaoHiem = false;
/* 2021 */     for (int j = 0; j < crystals.size(); j++) {
/* 2022 */       Item item = crystals.get(j);
/* 2023 */       if (item != null && (item.entry.type == 26 || item.entry.type == 28)) {
/* 2024 */         if (!isBaoHiem) {
/* 2025 */           if (item.id == 242 && (this.bag[equipIndex]).upgrade < 8) {
/* 2026 */             isBaoHiem = true;
/* 2027 */           } else if (item.id == 284 && (this.bag[equipIndex]).upgrade < 12) {
/* 2028 */             isBaoHiem = true;
/* 2029 */           } else if (item.id == 285 && (this.bag[equipIndex]).upgrade < 14) {
/* 2030 */             isBaoHiem = true;
/* 2031 */           } else if (item.id == 475 && (this.bag[equipIndex]).upgrade < 16) {
/* 2032 */             isBaoHiem = true;
/*      */           } 
/*      */         }
/* 2035 */         removeItem(item.index, item.quantity, false);
/*      */       } 
/*      */     } 
/* 2038 */     byte type = 1;
/* 2039 */     int rand = NinjaUtil.nextInt(100);
/* 2040 */     int up1 = (this.bag[equipIndex]).upgrade;
/* 2041 */     int up2 = (this.bag[equipIndex]).upgrade;
/* 2042 */     if (rand < percent) {
/* 2043 */       type = 1;
/* 2044 */       up2++;
/*      */     } else {
/* 2046 */       type = 0;
/* 2047 */       if (!isBaoHiem && (this.bag[equipIndex]).upgrade > 4) {
/* 2048 */         if ((this.bag[equipIndex]).upgrade >= 14) {
/* 2049 */           up2 = 14;
/* 2050 */         } else if ((this.bag[equipIndex]).upgrade >= 12) {
/* 2051 */           up2 = 12;
/*      */         } else {
/* 2053 */           up2 = (byte)((this.bag[equipIndex]).upgrade / 4 * 4);
/*      */         } 
/*      */       }
/*      */     } 
/* 2057 */     (this.bag[equipIndex]).isLock = true;
/* 2058 */     this.bag[equipIndex].next(up2 - up1);
/* 2059 */     this.logger.log("upgradeItem percent: " + percent + " isBaoHiem: " + isBaoHiem + " coin: " + coin + " gold: " + gold);
/* 2060 */     Message mss = new Message(21);
/* 2061 */     DataOutputStream ds = mss.writer();
/* 2062 */     ds.writeByte(type);
/* 2063 */     ds.writeInt(this.user.luong);
/* 2064 */     ds.writeInt(this.xu);
/* 2065 */     ds.writeInt(this.yen);
/* 2066 */     ds.writeByte((this.bag[equipIndex]).upgrade);
/* 2067 */     ds.flush();
/* 2068 */     sendMessage(mss);
/*      */   }
/*      */   
/*      */   public void luckyDrawRefresh(Message ms) throws IOException {
/* 2072 */     int size = this.menu.size();
/* 2073 */     if (size != 2) {
/*      */       return;
/*      */     }
/* 2076 */     if (((Integer)this.menu.get(0)).intValue() == 2 && (
/* 2077 */       (Integer)this.menu.get(1)).intValue() == 0) {
/* 2078 */       this.user.service.showInfoLuckyDraw(Server.luckyDrawVIP);
/*      */     }
/*      */     
/* 2081 */     if (((Integer)this.menu.get(0)).intValue() == 3 && (
/* 2082 */       (Integer)this.menu.get(1)).intValue() == 0) {
/* 2083 */       this.user.service.showInfoLuckyDraw(Server.luckyDrawNormal);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void input(Message ms) throws IOException {
/* 2089 */     short menuId = ms.reader().readShort();
/* 2090 */     String content = ms.reader().readUTF();
/* 2091 */     ms.reader().reset();
/* 2092 */     switch (menuId) {
/*      */       case 100:
/* 2094 */         betMessage(ms);
/*      */         break;
/*      */       case 101:
/* 2097 */         luckyDrawRefresh(ms);
/*      */         break;
/*      */       case 2003:
/* 2100 */         bet(content, 0);
/*      */         break;
/*      */       case 2402:
/* 2103 */         bet(content, 1);
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void bet(String money, int type) throws IOException {
/* 2111 */     if (money == null || money.equals("")) {
/*      */       return;
/*      */     }
/* 2114 */     Pattern p = Pattern.compile("^[0-9]+$");
/* 2115 */     Matcher m1 = p.matcher(money);
/* 2116 */     if (!m1.find()) {
/* 2117 */       this.user.service.addInfoMe("Số xu không hợp lệ!");
/*      */       return;
/*      */     } 
/* 2120 */     LuckyDraw lucky = null;
/* 2121 */     switch (type) {
/*      */       case 0:
/* 2123 */         lucky = Server.luckyDrawNormal;
/*      */         break;
/*      */       
/*      */       case 1:
/* 2127 */         lucky = Server.luckyDrawVIP;
/*      */         break;
/*      */     } 
/* 2130 */     lucky.join(this, Integer.parseInt(money));
/* 2131 */     this.user.service.showInfoLuckyDraw(lucky);
/*      */   }
/*      */   
/*      */   public void kickOption(Equiped equiped, int maxKick) {
/* 2135 */     int num = 0;
/* 2136 */     if (equiped != null && equiped.options != null) {
/* 2137 */       for (int i = 0; i < equiped.options.size(); i++) {
/* 2138 */         ItemOption itemOption = equiped.options.get(i);
/* 2139 */         itemOption.active = 0;
/* 2140 */         if (itemOption.optionTemplate.type == 2) {
/* 2141 */           if (num < maxKick) {
/* 2142 */             itemOption.active = 1;
/* 2143 */             num++;
/*      */           } 
/* 2145 */         } else if (itemOption.optionTemplate.type == 3 && equiped.upgrade >= 4) {
/* 2146 */           itemOption.active = 1;
/* 2147 */         } else if (itemOption.optionTemplate.type == 4 && equiped.upgrade >= 8) {
/* 2148 */           itemOption.active = 1;
/* 2149 */         } else if (itemOption.optionTemplate.type == 5 && equiped.upgrade >= 12) {
/* 2150 */           itemOption.active = 1;
/* 2151 */         } else if (itemOption.optionTemplate.type == 6 && equiped.upgrade >= 14) {
/* 2152 */           itemOption.active = 1;
/* 2153 */         } else if (itemOption.optionTemplate.type == 7 && equiped.upgrade >= 16) {
/* 2154 */           itemOption.active = 1;
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   public void updateKickOption() {
/* 2161 */     int num = 2;
/* 2162 */     int num2 = 2;
/* 2163 */     int num3 = 2;
/* 2164 */     if (this.equiped[0] == null) {
/* 2165 */       num--;
/*      */     }
/* 2167 */     if (this.equiped[6] == null) {
/* 2168 */       num--;
/*      */     }
/* 2170 */     if (this.equiped[5] == null) {
/* 2171 */       num--;
/*      */     }
/* 2173 */     kickOption(this.equiped[0], num);
/* 2174 */     kickOption(this.equiped[6], num);
/* 2175 */     kickOption(this.equiped[5], num);
/* 2176 */     if (this.equiped[2] == null) {
/* 2177 */       num2--;
/*      */     }
/* 2179 */     if (this.equiped[8] == null) {
/* 2180 */       num2--;
/*      */     }
/* 2182 */     if (this.equiped[7] == null) {
/* 2183 */       num2--;
/*      */     }
/* 2185 */     kickOption(this.equiped[2], num2);
/* 2186 */     kickOption(this.equiped[8], num2);
/* 2187 */     kickOption(this.equiped[7], num2);
/* 2188 */     if (this.equiped[4] == null) {
/* 2189 */       num3--;
/*      */     }
/* 2191 */     if (this.equiped[3] == null) {
/* 2192 */       num3--;
/*      */     }
/* 2194 */     if (this.equiped[9] == null) {
/* 2195 */       num3--;
/*      */     }
/* 2197 */     if (this.equiped[1] != null) {
/* 2198 */       if ((this.equiped[1]).sys == getSys()) {
/* 2199 */         if ((this.equiped[1]).options != null) {
/* 2200 */           for (int i = 0; i < (this.equiped[1]).options.size(); i++) {
/* 2201 */             ItemOption itemOption = (this.equiped[1]).options.get(i);
/* 2202 */             if (itemOption.optionTemplate.type == 2) {
/* 2203 */               itemOption.active = 1;
/*      */             }
/*      */           } 
/*      */         }
/* 2207 */       } else if ((this.equiped[1]).options != null) {
/* 2208 */         for (int j = 0; j < (this.equiped[1]).options.size(); j++) {
/* 2209 */           ItemOption itemOption2 = (this.equiped[1]).options.get(j);
/* 2210 */           if (itemOption2.optionTemplate.type == 2) {
/* 2211 */             itemOption2.active = 0;
/*      */           }
/*      */         } 
/*      */       } 
/*      */     }
/* 2216 */     kickOption(this.equiped[4], num3);
/* 2217 */     kickOption(this.equiped[3], num3);
/* 2218 */     kickOption(this.equiped[9], num3);
/*      */   }
/*      */   
/*      */   public void setAbility() {
/* 2222 */     this.options = new int[127];
/* 2223 */     updateKickOption();
/* 2224 */     for (Equiped item : this.equiped) {
/* 2225 */       if (item != null) {
/* 2226 */         for (ItemOption itemOption2 : item.options) {
/* 2227 */           if (itemOption2.optionTemplate.type >= 2 && itemOption2.optionTemplate.type <= 7) {
/* 2228 */             if (itemOption2.active == 1)
/* 2229 */               this.options[itemOption2.optionTemplate.id] = this.options[itemOption2.optionTemplate.id] + itemOption2.param; 
/*      */             continue;
/*      */           } 
/* 2232 */           this.options[itemOption2.optionTemplate.id] = this.options[itemOption2.optionTemplate.id] + itemOption2.param;
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/* 2237 */     this.maxHP = this.potential[2] * 10;
/* 2238 */     this.maxMP = this.potential[3] * 10;
/* 2239 */     int basicAttack = 0;
/* 2240 */     switch (this.classId) {
/*      */       case 0:
/*      */       case 1:
/*      */       case 3:
/*      */       case 5:
/* 2245 */         basicAttack = this.potential[0] * 3;
/* 2246 */         this.miss = this.exactly = this.potential[1] * 2;
/*      */         break;
/*      */       
/*      */       case 2:
/*      */       case 4:
/*      */       case 6:
/* 2252 */         basicAttack = this.potential[3] * 2;
/* 2253 */         this.miss = this.exactly = this.potential[1] * 3;
/*      */         break;
/*      */     } 
/* 2256 */     if (this.selectedSkill != null && this.selectedSkill.options != null) {
/* 2257 */       basicAttack += basicAttack * this.selectedSkill.options[11] / 100;
/* 2258 */       basicAttack += this.options[0] * this.selectedSkill.options[0] / 100;
/* 2259 */       basicAttack += this.options[1] * this.selectedSkill.options[1] / 100;
/*      */     } 
/* 2261 */     basicAttack += this.options[38];
/* 2262 */     basicAttack += this.options[0] + this.options[1];
/* 2263 */     basicAttack += basicAttack * this.options[8] / 100 + basicAttack * this.options[9] / 100;
/* 2264 */     this.dame = basicAttack;
/* 2265 */     this.dame2 = this.dame - this.dame / 10;
/* 2266 */     this.maxHP += this.options[6] + this.options[32] + this.options[77] + this.options[82] + this.options[125];
/* 2267 */     this.maxHP += this.maxHP * this.options[31] / 100;
/* 2268 */     this.maxHP += this.maxHP * this.options[61] / 100;
/* 2269 */     this.maxMP += this.options[7] + this.options[19] + this.options[29] + this.options[83] + this.options[117];
/* 2270 */     this.maxMP += this.maxMP * this.options[28] / 100;
/* 2271 */     this.maxMP += this.maxMP * this.options[60] / 100;
/* 2272 */     if (this.maxHP == 0) {
/* 2273 */       this.maxHP = 50;
/*      */     }
/* 2275 */     if (this.maxMP == 0) {
/* 2276 */       this.maxMP = 50;
/*      */     }
/* 2278 */     this.dameDown = this.options[47] + this.options[80] + this.options[124];
/* 2279 */     this.miss = this.options[5] + this.options[17] + this.options[62] + this.options[68] + this.options[78] + this.options[84] + this.options[115];
/*      */   }
/*      */   
/*      */   public void inputNumberSplit(Message ms) throws IOException {
/* 2283 */     byte indexItem = ms.reader().readByte();
/* 2284 */     int numSplit = ms.reader().readInt();
/* 2285 */     if (this.bag[indexItem] != null && (this.bag[indexItem]).entry.isUpToUp) {
/* 2286 */       int quantity = (this.bag[indexItem]).quantity;
/* 2287 */       if (numSplit >= quantity) {
/*      */         return;
/*      */       }
/* 2290 */       Item item2 = this.bag[indexItem].clone();
/* 2291 */       for (int i = 0; i < this.numberCellBag; i++) {
/* 2292 */         if (this.bag[i] == null) {
/* 2293 */           this.bag[i] = item2;
/* 2294 */           (this.bag[i]).index = i;
/* 2295 */           (this.bag[i]).quantity = numSplit;
/* 2296 */           this.user.service.addItem(this.bag[i]);
/* 2297 */           (this.bag[indexItem]).quantity -= numSplit;
/* 2298 */           this.user.service.removeItem(indexItem, numSplit);
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void betMessage(Message ms) throws IOException {
/* 2306 */     short type = ms.reader().readShort();
/* 2307 */     String money = ms.reader().readUTF();
/* 2308 */     byte typeLuck = ms.reader().readByte();
/* 2309 */     if (money == null || money.equals("")) {
/*      */       return;
/*      */     }
/* 2312 */     Pattern p = Pattern.compile("^[0-9]+$");
/* 2313 */     Matcher m1 = p.matcher(money);
/* 2314 */     if (!m1.find()) {
/* 2315 */       startOKDlg("Số xu không hợp lệ!");
/*      */       return;
/*      */     } 
/* 2318 */     LuckyDraw lucky = null;
/* 2319 */     switch (typeLuck) {
/*      */       case 0:
/* 2321 */         lucky = Server.luckyDrawNormal;
/*      */         break;
/*      */       
/*      */       case 1:
/* 2325 */         lucky = Server.luckyDrawVIP;
/*      */         break;
/*      */     } 
/* 2328 */     lucky.join(this, Integer.parseInt(money));
/* 2329 */     this.user.service.showInfoLuckyDraw(lucky);
/*      */   }
/*      */   
/*      */   public void selectSkill(Message ms) throws IOException {
/* 2333 */     short skillTemplateId = ms.reader().readShort();
/* 2334 */     selectSkill(skillTemplateId);
/*      */   }
/*      */ 
/*      */   
/*      */   public void selectSkill(short skillTemplateId) {
/* 2339 */     this.selectedSkill.skillTemplateId = skillTemplateId;
/* 2340 */     this.selectedSkill.options = new int[72];
/* 2341 */     this.selectedSkill.haveLearned = false;
/* 2342 */     int point = 0;
/* 2343 */     for (MySkill my : this.listSkill) {
/* 2344 */       if (my.id == skillTemplateId) {
/* 2345 */         this.selectedSkill.skill = my;
/* 2346 */         point = my.point;
/* 2347 */         this.selectedSkill.haveLearned = true;
/*      */         break;
/*      */       } 
/*      */     } 
/* 2351 */     if (!this.selectedSkill.haveLearned) {
/*      */       return;
/*      */     }
/* 2354 */     SkillTemplate tem = SkillData.getTemplate(this.classId, skillTemplateId);
/* 2355 */     for (Skill skill : tem.skills) {
/* 2356 */       if (skill.point == point) {
/* 2357 */         this.selectedSkill.manaUse = skill.manaUse;
/* 2358 */         for (SkillOption op : skill.options) {
/* 2359 */           this.selectedSkill.options[op.optionTemplate.id] = this.selectedSkill.options[op.optionTemplate.id] + op.param;
/*      */         }
/* 2361 */         this.selectedSkill.dx = skill.dx;
/* 2362 */         this.selectedSkill.dy = skill.dy;
/* 2363 */         this.selectedSkill.maxFight = skill.maxFight;
/* 2364 */         this.selectedSkill.coolDown = skill.coolDown;
/*      */         break;
/*      */       } 
/*      */     } 
/* 2368 */     setAbility();
/*      */   }
/*      */   
/*      */   public void menuId(Message ms) throws IOException {
/* 2372 */     short npcId = ms.reader().readShort();
/* 2373 */     this.logger.log("npcId: " + npcId);
/* 2374 */     NpcTemplate npc = Server.npcs.get(npcId);
/* 2375 */     Message mss = new Message(39);
/* 2376 */     DataOutputStream ds = mss.writer();
/* 2377 */     ds.writeShort(npcId);
/* 2378 */     ds.writeUTF("");
/* 2379 */     ds.writeByte(npc.menu.length);
/* 2380 */     for (String[] c : npc.menu) {
/* 2381 */       ds.writeUTF(c[0]);
/*      */     }
/* 2383 */     ds.flush();
/* 2384 */     sendMessage(mss);
/* 2385 */     mss.cleanup();
/* 2386 */     this.menu.clear();
/*      */   }
/*      */   
/*      */   public void menu(Message ms) throws IOException {
/* 2390 */     byte npcId = ms.reader().readByte();
/* 2391 */     byte menuId = ms.reader().readByte();
/* 2392 */     this.logger.log("npcId: " + npcId + " menuId: " + menuId);
/* 2393 */     if (ms.reader().available() > 0) {
/* 2394 */       byte optionId = ms.reader().readByte();
/* 2395 */       this.logger.log("optionId: " + optionId);
/*      */     } 
/* 2397 */     this.menu.add(Integer.valueOf(menuId));
/* 2398 */     switch (npcId) {
/*      */       
/*      */       case 1:
/* 2401 */         npcFuroya(menuId);
/*      */         break;
/*      */       case 2:
/* 2404 */         npcAmeji(menuId);
/*      */         break;
/*      */       case 3:
/* 2407 */         npcKirito(menuId);
/*      */         break;
/*      */       case 4:
/* 2410 */         npcTabemono(menuId);
/*      */         break;
/*      */       case 5:
/* 2413 */         npcKamakara(menuId);
/*      */         break;
/*      */       case 7:
/* 2416 */         npcUmayaki_1(menuId);
/*      */         break;
/*      */       case 8:
/* 2419 */         npcUmayaki_2(menuId);
/*      */         break;
/*      */       case 6:
/* 2422 */         npcKenshinto(menuId);
/*      */         break;
/*      */       case 9:
/* 2425 */         npcToyotomi(menuId);
/*      */         break;
/*      */       case 10:
/* 2428 */         npcOokamesama(menuId);
/*      */         break;
/*      */       case 11:
/* 2431 */         npcKazeto(menuId);
/*      */         break;
/*      */       case 12:
/* 2434 */         npcTajima(menuId);
/*      */         break;
/*      */       case 0:
/* 2437 */         npcKanata(menuId);
/*      */         break;
/*      */       case 30:
/* 2440 */         npcRakkii(menuId);
/*      */         break;
/*      */       case 26:
/* 2443 */         npcGoosho(menuId);
/*      */         break;
/*      */       case 33:
/* 2446 */         npcTiennu(menuId);
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void npcTiennu(byte menuId) throws IOException {
/* 2454 */     int size = this.menu.size();
/* 2455 */     if (size == 1) {
/* 2456 */       if (((Integer)this.menu.get(0)).intValue() == 0) {
/* 2457 */         if (getNumberItem(434) <= 0) {
/* 2458 */           startOKDlg("Bạn không có diều giấy!");
/*      */           return;
/*      */         } 
/* 2461 */         removeItem(getIndexItemByIdInBag(434), 1, true);
/* 2462 */         addExp(150000000L);
/* 2463 */         if (NinjaUtil.nextInt(7) == 0) {
/* 2464 */           addExp(300000000L);
/*      */ 
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/* 2471 */       else if (((Integer)this.menu.get(0)).intValue() == 1) {
/* 2472 */         if (getNumberItem(435) <= 0) {
/* 2473 */           startOKDlg("Bạn không có diều vải!");
/*      */           return;
/*      */         } 
/* 2476 */         removeItem(getIndexItemByIdInBag(435), 1, true);
/* 2477 */         addExp(250000000L);
/* 2478 */         if (NinjaUtil.nextInt(7) == 0) {
/* 2479 */           addExp(500000000L);
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2509 */     NpcTemplate npc = Server.npcs.get(12);
/* 2510 */     Message mss = new Message(39);
/* 2511 */     DataOutputStream ds = mss.writer();
/* 2512 */     ds.writeShort(12);
/* 2513 */     ds.writeUTF("Cho kiếm này.");
/* 2514 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 2515 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 2516 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 2518 */     ds.flush();
/* 2519 */     sendMessage(mss);
/* 2520 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcKamakara(byte menuId) throws IOException {
/* 2524 */     int size = this.menu.size();
/* 2525 */     if (size == 1)
/* 2526 */       if (((Integer)this.menu.get(0)).intValue() == 0)
/* 2527 */       { this.user.service.openUI((byte)4); }
/* 2528 */       else { if (((Integer)this.menu.get(0)).intValue() == 1) {
/* 2529 */           this.saveCoordinate = this.mapId;
/* 2530 */           npcChat((short)5, "Lưu toạ độ thành công! Mày chết sẽ quay về nơi đây."); return;
/*      */         } 
/* 2532 */         if (((Integer)this.menu.get(0)).intValue() == 3) {
/* 2533 */           switch (NinjaUtil.nextInt(3)) {
/*      */             case 0:
/* 2535 */               npcChat((short)5, "Hãy yên tâm giao đồ cho ta.");
/*      */               break;
/*      */             
/*      */             case 1:
/* 2539 */               npcChat((short)5, "Ta giữ đồ chưa hề để thất lạc bao giờ.");
/*      */               break;
/*      */             
/*      */             case 2:
/* 2543 */               npcChat((short)5, "Trên người ngươi toàn đồ có giá trị, sao không cất bớt ở đây?");
/*      */               break;
/*      */           } 
/*      */           return;
/*      */         }  }
/*      */        
/* 2549 */     NpcTemplate npc = Server.npcs.get(5);
/* 2550 */     Message mss = new Message(39);
/* 2551 */     DataOutputStream ds = mss.writer();
/* 2552 */     ds.writeShort(5);
/* 2553 */     ds.writeUTF("");
/* 2554 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 2555 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 2556 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 2558 */     ds.flush();
/* 2559 */     sendMessage(mss);
/* 2560 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void admission(byte sys, short npc) throws IOException {
/* 2564 */     if (this.classId == 0) {
/* 2565 */       if (this.level >= 10) {
/* 2566 */         if (this.equiped[1] != null) {
/* 2567 */           startOKDlg("Vui lòng tháo vũ khí trước khi nhập học tránh đồ sát thầy!");
/*      */           return;
/*      */         } 
/* 2570 */         this.classId = sys;
/* 2571 */         this.spoint = (short)(this.level - 9);
/* 2572 */         this.point = (short)(this.level * 10);
/* 2573 */         if (this.level >= 70) {
/* 2574 */           this.point = (short)(this.point + (this.level - 70) * 10);
/*      */         }
/* 2576 */         if (this.level >= 80) {
/* 2577 */           this.point = (short)(this.point + (this.level - 80) * 10);
/*      */         }
/* 2579 */         if (this.level >= 90) {
/* 2580 */           this.point = (short)(this.point + (this.level - 90) * 10);
/*      */         }
/* 2582 */         if (this.level >= 100) {
/* 2583 */           this.point = (short)(this.point + (this.level - 100) * 10);
/*      */         }
/* 2585 */         this.potential[1] = 5;
/* 2586 */         this.potential[2] = 5;
/* 2587 */         if (this.classId == 1 || this.classId == 3 || this.classId == 5) {
/* 2588 */           this.potential[0] = 10;
/* 2589 */           this.potential[3] = 5;
/*      */         } else {
/* 2591 */           this.potential[0] = 5;
/* 2592 */           this.potential[3] = 10;
/*      */         } 
/* 2594 */         this.listSkill.clear();
/* 2595 */         npcChat(npc, "Mày chọn ta là đúng rồi đấy!");
/* 2596 */         Item item = null;
/* 2597 */         Item item2 = null;
/* 2598 */         switch (sys) {
/*      */           case 1:
/* 2600 */             item = new Item(40);
/* 2601 */             item.quantity = 1;
/* 2602 */             item.isLock = true;
/* 2603 */             item.expire = -1L;
/* 2604 */             item2 = new Item(94);
/* 2605 */             item2.quantity = 1;
/* 2606 */             item2.isLock = true;
/* 2607 */             item2.expire = -1L;
/* 2608 */             item2.upgrade = 0;
/* 2609 */             item2.sys = 1;
/* 2610 */             item2.options = new ArrayList<>();
/* 2611 */             item2.options.add(new ItemOption(0, 100));
/* 2612 */             item2.options.add(new ItemOption(1, 100));
/* 2613 */             item2.options.add(new ItemOption(8, 10));
/* 2614 */             item2.options.add(new ItemOption(10, 5));
/* 2615 */             item2.options.add(new ItemOption(21, 100));
/* 2616 */             item2.options.add(new ItemOption(19, 10));
/* 2617 */             item2.options.add(new ItemOption(30, 5));
/* 2618 */             item2.yen = 300;
/*      */             break;
/*      */           
/*      */           case 3:
/* 2622 */             item = new Item(58);
/* 2623 */             item.quantity = 1;
/* 2624 */             item.isLock = true;
/* 2625 */             item.expire = -1L;
/* 2626 */             item2 = new Item(99);
/* 2627 */             item2.quantity = 1;
/* 2628 */             item2.isLock = true;
/* 2629 */             item2.expire = -1L;
/* 2630 */             item2.upgrade = 0;
/* 2631 */             item2.sys = 1;
/* 2632 */             item2.options = new ArrayList<>();
/* 2633 */             item2.options.add(new ItemOption(0, 100));
/* 2634 */             item2.options.add(new ItemOption(1, 100));
/* 2635 */             item2.options.add(new ItemOption(8, 10));
/* 2636 */             item2.options.add(new ItemOption(10, 5));
/* 2637 */             item2.options.add(new ItemOption(21, 100));
/* 2638 */             item2.options.add(new ItemOption(19, 10));
/* 2639 */             item2.options.add(new ItemOption(30, 5));
/* 2640 */             item2.yen = 300;
/*      */             break;
/*      */           
/*      */           case 5:
/* 2644 */             item = new Item(76);
/* 2645 */             item.quantity = 1;
/* 2646 */             item.isLock = true;
/* 2647 */             item.expire = -1L;
/* 2648 */             item2 = new Item(104);
/* 2649 */             item2.quantity = 1;
/* 2650 */             item2.isLock = true;
/* 2651 */             item2.expire = -1L;
/* 2652 */             item2.upgrade = 0;
/* 2653 */             item2.sys = 1;
/* 2654 */             item2.options = new ArrayList<>();
/* 2655 */             item2.options.add(new ItemOption(0, 100));
/* 2656 */             item2.options.add(new ItemOption(1, 100));
/* 2657 */             item2.options.add(new ItemOption(8, 10));
/* 2658 */             item2.options.add(new ItemOption(10, 5));
/* 2659 */             item2.options.add(new ItemOption(21, 100));
/* 2660 */             item2.options.add(new ItemOption(19, 10));
/* 2661 */             item2.options.add(new ItemOption(30, 5));
/* 2662 */             item2.yen = 300;
/*      */             break;
/*      */           
/*      */           case 2:
/* 2666 */             item = new Item(49);
/* 2667 */             item.quantity = 1;
/* 2668 */             item.isLock = true;
/* 2669 */             item.expire = -1L;
/* 2670 */             item2 = new Item(114);
/* 2671 */             item2.quantity = 1;
/* 2672 */             item2.isLock = true;
/* 2673 */             item2.expire = -1L;
/* 2674 */             item2.upgrade = 0;
/* 2675 */             item2.sys = 1;
/* 2676 */             item2.options = new ArrayList<>();
/* 2677 */             item2.options.add(new ItemOption(0, 100));
/* 2678 */             item2.options.add(new ItemOption(1, 100));
/* 2679 */             item2.options.add(new ItemOption(9, 10));
/* 2680 */             item2.options.add(new ItemOption(10, 5));
/* 2681 */             item2.options.add(new ItemOption(22, 100));
/* 2682 */             item2.options.add(new ItemOption(19, 10));
/* 2683 */             item2.options.add(new ItemOption(30, 5));
/* 2684 */             item2.yen = 300;
/*      */             break;
/*      */           
/*      */           case 4:
/* 2688 */             item = new Item(67);
/* 2689 */             item.quantity = 1;
/* 2690 */             item.isLock = true;
/* 2691 */             item.expire = -1L;
/* 2692 */             item2 = new Item(109);
/* 2693 */             item2.quantity = 1;
/* 2694 */             item2.isLock = true;
/* 2695 */             item2.expire = -1L;
/* 2696 */             item2.upgrade = 0;
/* 2697 */             item2.sys = 1;
/* 2698 */             item2.options = new ArrayList<>();
/* 2699 */             item2.options.add(new ItemOption(0, 100));
/* 2700 */             item2.options.add(new ItemOption(1, 100));
/* 2701 */             item2.options.add(new ItemOption(9, 10));
/* 2702 */             item2.options.add(new ItemOption(10, 5));
/* 2703 */             item2.options.add(new ItemOption(22, 100));
/* 2704 */             item2.options.add(new ItemOption(19, 10));
/* 2705 */             item2.options.add(new ItemOption(30, 5));
/* 2706 */             item2.yen = 300;
/*      */             break;
/*      */           
/*      */           case 6:
/* 2710 */             item = new Item(85);
/* 2711 */             item.quantity = 1;
/* 2712 */             item.isLock = true;
/* 2713 */             item.expire = -1L;
/* 2714 */             item2 = new Item(119);
/* 2715 */             item2.quantity = 1;
/* 2716 */             item2.isLock = true;
/* 2717 */             item2.expire = -1L;
/* 2718 */             item2.upgrade = 0;
/* 2719 */             item2.sys = 1;
/* 2720 */             item2.options = new ArrayList<>();
/* 2721 */             item2.options.add(new ItemOption(0, 100));
/* 2722 */             item2.options.add(new ItemOption(1, 100));
/* 2723 */             item2.options.add(new ItemOption(9, 10));
/* 2724 */             item2.options.add(new ItemOption(10, 5));
/* 2725 */             item2.options.add(new ItemOption(22, 100));
/* 2726 */             item2.options.add(new ItemOption(19, 10));
/* 2727 */             item2.options.add(new ItemOption(30, 5));
/* 2728 */             item2.yen = 300;
/*      */             break;
/*      */         } 
/* 2731 */         this.user.service.sendInfo();
/* 2732 */         addItemToBag(item);
/* 2733 */         addItemToBag(item2);
/*      */       } else {
/* 2735 */         npcChat(npc, "Mày còn kém lắm chém nhau lên cấp 10 thì ta cho học!");
/*      */       } 
/*      */     } else {
/* 2738 */       npcChat(npc, "Mày đòi học lắm nhiều phái thế!");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void npcKazeto(byte menuId) throws IOException {
/* 2743 */     int size = this.menu.size();
/* 2744 */     if (size == 2) {
/* 2745 */       if (((Integer)this.menu.get(0)).intValue() == 1) {
/* 2746 */         if (((Integer)this.menu.get(1)).intValue() == 0) {
/* 2747 */           admission((byte)5, (short)11);
/*      */         } else {
/* 2749 */           admission((byte)6, (short)11);
/*      */         }  return;
/*      */       } 
/* 2752 */       if (((Integer)this.menu.get(0)).intValue() == 2) {
/* 2753 */         if (((Integer)this.menu.get(1)).intValue() == 0) {
/* 2754 */           tayTiemNang((short)11);
/*      */         } else {
/* 2756 */           tayKyNang((short)11);
/*      */         } 
/*      */         return;
/*      */       } 
/*      */     } 
/* 2761 */     NpcTemplate npc = Server.npcs.get(11);
/* 2762 */     Message mss = new Message(39);
/* 2763 */     DataOutputStream ds = mss.writer();
/* 2764 */     ds.writeShort(11);
/* 2765 */     ds.writeUTF("Ta là bố của thần gió, hay theo ta.");
/* 2766 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 2767 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 2768 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 2770 */     ds.flush();
/* 2771 */     sendMessage(mss);
/* 2772 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcOokamesama(byte menuId) throws IOException {
/* 2776 */     int size = this.menu.size();
/* 2777 */     if (size == 2) {
/* 2778 */       if (((Integer)this.menu.get(0)).intValue() == 1) {
/* 2779 */         if (((Integer)this.menu.get(1)).intValue() == 0) {
/* 2780 */           admission((byte)3, (short)10);
/*      */         } else {
/* 2782 */           admission((byte)4, (short)10);
/*      */         }  return;
/*      */       } 
/* 2785 */       if (((Integer)this.menu.get(0)).intValue() == 2) {
/* 2786 */         if (((Integer)this.menu.get(1)).intValue() == 0) {
/* 2787 */           tayTiemNang((short)10);
/*      */         } else {
/* 2789 */           tayKyNang((short)10);
/*      */         } 
/*      */         return;
/*      */       } 
/*      */     } 
/* 2794 */     NpcTemplate npc = Server.npcs.get(10);
/* 2795 */     Message mss = new Message(39);
/* 2796 */     DataOutputStream ds = mss.writer();
/* 2797 */     ds.writeShort(10);
/* 2798 */     ds.writeUTF("Ta ngươi chơi hệ thủy, hay theo ta.");
/* 2799 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 2800 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 2801 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 2803 */     ds.flush();
/* 2804 */     sendMessage(mss);
/* 2805 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcToyotomi(byte menuId) throws IOException {
/* 2809 */     int size = this.menu.size();
/* 2810 */     if (size == 2) {
/* 2811 */       if (((Integer)this.menu.get(0)).intValue() == 1) {
/* 2812 */         if (((Integer)this.menu.get(1)).intValue() == 0) {
/* 2813 */           admission((byte)1, (short)9);
/*      */         } else {
/* 2815 */           admission((byte)2, (short)9);
/*      */         }  return;
/*      */       } 
/* 2818 */       if (((Integer)this.menu.get(0)).intValue() == 2) {
/* 2819 */         if (((Integer)this.menu.get(1)).intValue() == 0) {
/* 2820 */           tayTiemNang((short)9);
/*      */         } else {
/* 2822 */           tayKyNang((short)9);
/*      */         } 
/*      */         return;
/*      */       } 
/*      */     } 
/* 2827 */     NpcTemplate npc = Server.npcs.get(9);
/* 2828 */     Message mss = new Message(39);
/* 2829 */     DataOutputStream ds = mss.writer();
/* 2830 */     ds.writeShort(9);
/* 2831 */     ds.writeUTF("Theo ta không ta đốt nhà mi.");
/* 2832 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 2833 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 2834 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 2836 */     ds.flush();
/* 2837 */     sendMessage(mss);
/* 2838 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcKenshinto(byte menuId) throws IOException {
/* 2842 */     int size = this.menu.size();
/* 2843 */     if (size == 2) {
/* 2844 */       if (((Integer)this.menu.get(0)).intValue() == 0) {
/* 2845 */         if (((Integer)this.menu.get(1)).intValue() == 0) {
/* 2846 */           this.user.service.openUI((byte)10); return;
/*      */         } 
/* 2848 */         if (((Integer)this.menu.get(1)).intValue() == 1) {
/* 2849 */           this.user.service.openUI((byte)31); return;
/*      */         } 
/* 2851 */         if (((Integer)this.menu.get(1)).intValue() == 2) {
/* 2852 */           npcChat((short)6, "Bỏ Trang bị và Đá vào trong khung để nâng cấp, Khi nâng cấp cẩn thận thì phải có lượng.");
/*      */           return;
/*      */         } 
/* 2855 */       } else if (((Integer)this.menu.get(0)).intValue() == 1) {
/* 2856 */         if (((Integer)this.menu.get(1)).intValue() == 0) {
/* 2857 */           this.user.service.openUI((byte)12); return;
/*      */         } 
/* 2859 */         if (((Integer)this.menu.get(1)).intValue() == 1) {
/* 2860 */           this.user.service.openUI((byte)11);
/*      */           return;
/*      */         } 
/*      */       } 
/* 2864 */     } else if (size == 1) {
/* 2865 */       if (((Integer)this.menu.get(0)).intValue() == 2) {
/* 2866 */         this.user.service.openUI((byte)13); return;
/*      */       } 
/* 2868 */       if (((Integer)this.menu.get(0)).intValue() == 3) {
/* 2869 */         this.user.service.openUI((byte)33); return;
/*      */       } 
/* 2871 */       if (((Integer)this.menu.get(0)).intValue() == 4) {
/* 2872 */         this.user.service.openUI((byte)46); return;
/*      */       } 
/* 2874 */       if (((Integer)this.menu.get(0)).intValue() == 5) {
/* 2875 */         this.user.service.openUI((byte)47); return;
/*      */       } 
/* 2877 */       if (((Integer)this.menu.get(0)).intValue() == 6) {
/* 2878 */         this.user.service.openUI((byte)49); return;
/*      */       } 
/* 2880 */       if (((Integer)this.menu.get(0)).intValue() == 7) {
/* 2881 */         this.user.service.openUI((byte)50); return;
/*      */       } 
/* 2883 */       if (((Integer)this.menu.get(0)).intValue() == 8) {
/* 2884 */         npcChat((short)6, "Chỉ có đứa ngu mới tin tao.");
/*      */         return;
/*      */       } 
/*      */     } 
/* 2888 */     NpcTemplate npc = Server.npcs.get(6);
/* 2889 */     Message mss = new Message(39);
/* 2890 */     DataOutputStream ds = mss.writer();
/* 2891 */     ds.writeShort(6);
/* 2892 */     ds.writeUTF("Đập đồ đi mấy cưng!!!");
/* 2893 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 2894 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 2895 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 2897 */     ds.flush();
/* 2898 */     sendMessage(mss);
/* 2899 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcFuroya(byte menuId) throws IOException {
/* 2903 */     int size = this.menu.size();
/* 2904 */     if (size == 2 && (
/* 2905 */       (Integer)this.menu.get(0)).intValue() == 0) {
/* 2906 */       if (((Integer)this.menu.get(1)).intValue() == 0) {
/* 2907 */         if (this.gender == 1) {
/* 2908 */           this.user.service.openUI((byte)20);
/*      */         } else {
/* 2910 */           this.user.service.openUI((byte)21);
/*      */         }  return;
/*      */       } 
/* 2913 */       if (((Integer)this.menu.get(1)).intValue() == 1) {
/* 2914 */         if (this.gender == 1) {
/* 2915 */           this.user.service.openUI((byte)22);
/*      */         } else {
/* 2917 */           this.user.service.openUI((byte)23);
/*      */         }  return;
/*      */       } 
/* 2920 */       if (((Integer)this.menu.get(1)).intValue() == 2) {
/* 2921 */         if (this.gender == 1) {
/* 2922 */           this.user.service.openUI((byte)24);
/*      */         } else {
/* 2924 */           this.user.service.openUI((byte)25);
/*      */         }  return;
/*      */       } 
/* 2927 */       if (((Integer)this.menu.get(1)).intValue() == 3) {
/* 2928 */         if (this.gender == 1) {
/* 2929 */           this.user.service.openUI((byte)26);
/*      */         } else {
/* 2931 */           this.user.service.openUI((byte)27);
/*      */         }  return;
/*      */       } 
/* 2934 */       if (((Integer)this.menu.get(1)).intValue() == 4) {
/* 2935 */         if (this.gender == 1) {
/* 2936 */           this.user.service.openUI((byte)28);
/*      */         } else {
/* 2938 */           this.user.service.openUI((byte)29);
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/* 2944 */     NpcTemplate npc = Server.npcs.get(1);
/* 2945 */     Message mss = new Message(39);
/* 2946 */     DataOutputStream ds = mss.writer();
/* 2947 */     ds.writeShort(1);
/* 2948 */     ds.writeUTF("Tao bán trang bị đây!");
/* 2949 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 2950 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 2951 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 2953 */     ds.flush();
/* 2954 */     sendMessage(mss);
/* 2955 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcAmeji(byte menuId) throws IOException {
/* 2959 */     int size = this.menu.size();
/* 2960 */     if (size == 2 && (
/* 2961 */       (Integer)this.menu.get(0)).intValue() == 0) {
/* 2962 */       if (((Integer)this.menu.get(1)).intValue() == 0) {
/* 2963 */         this.user.service.openUI((byte)16);
/*      */         return;
/*      */       } 
/* 2966 */       if (((Integer)this.menu.get(1)).intValue() == 1) {
/* 2967 */         this.user.service.openUI((byte)17);
/*      */         return;
/*      */       } 
/* 2970 */       if (((Integer)this.menu.get(1)).intValue() == 2) {
/* 2971 */         this.user.service.openUI((byte)18);
/*      */         
/*      */         return;
/*      */       } 
/* 2975 */       if (((Integer)this.menu.get(1)).intValue() == 3) {
/* 2976 */         this.user.service.openUI((byte)19);
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/* 2981 */     NpcTemplate npc = Server.npcs.get(2);
/* 2982 */     Message mss = new Message(39);
/* 2983 */     DataOutputStream ds = mss.writer();
/* 2984 */     ds.writeShort(2);
/* 2985 */     ds.writeUTF("Tao bán trang bị đây!");
/* 2986 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 2987 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 2988 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 2990 */     ds.flush();
/* 2991 */     sendMessage(mss);
/* 2992 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcTabemono(byte menuId) throws IOException {
/* 2996 */     int size = this.menu.size();
/* 2997 */     if (size == 1) {
/* 2998 */       if (((Integer)this.menu.get(0)).intValue() == 0) {
/* 2999 */         this.user.service.openUI((byte)9); return;
/*      */       } 
/* 3001 */       if (((Integer)this.menu.get(0)).intValue() == 1) {
/* 3002 */         this.user.service.openUI((byte)8); return;
/*      */       } 
/* 3004 */       if (((Integer)this.menu.get(0)).intValue() == 2) {
/* 3005 */         npcChat((short)4, "Ta ở đây cung cấp lương thực, nhưng không phải miễn phí.");
/*      */         return;
/*      */       } 
/* 3008 */       npcChat((short)4, "Thiên địa bảng còn lâu mới có. Mua đồ ăn thì mua, không mua thì lượn đi.");
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 3013 */     NpcTemplate npc = Server.npcs.get(4);
/* 3014 */     Message mss = new Message(39);
/* 3015 */     DataOutputStream ds = mss.writer();
/* 3016 */     ds.writeShort(4);
/* 3017 */     ds.writeUTF("Đồ ngon đêy mua đê.");
/* 3018 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 3019 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 3020 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 3022 */     ds.flush();
/* 3023 */     sendMessage(mss);
/* 3024 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcKirito(byte menuId) throws IOException {
/* 3028 */     int size = this.menu.size();
/* 3029 */     if (size == 1) {
/* 3030 */       if (((Integer)this.menu.get(0)).intValue() == 0) {
/* 3031 */         this.user.service.openUI((byte)7); return;
/*      */       } 
/* 3033 */       if (((Integer)this.menu.get(0)).intValue() == 1) {
/* 3034 */         this.user.service.openUI((byte)6); return;
/*      */       } 
/* 3036 */       if (((Integer)this.menu.get(0)).intValue() == 2) {
/* 3037 */         npcChat((short)3, "Ta ở đây cung cấp vật phẩm y tê giá rẻ chất lượng thấp.");
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/* 3042 */     NpcTemplate npc = Server.npcs.get(3);
/* 3043 */     Message mss = new Message(39);
/* 3044 */     DataOutputStream ds = mss.writer();
/* 3045 */     ds.writeShort(3);
/* 3046 */     ds.writeUTF("Đồ ngon đêy mua đê.");
/* 3047 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 3048 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 3049 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 3051 */     ds.flush();
/* 3052 */     sendMessage(mss);
/* 3053 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcOkanechan(byte menuId) throws IOException {
/* 3057 */     int size = this.menu.size();
/* 3058 */     if (size == 1 && (
/* 3059 */       (Integer)this.menu.get(0)).intValue() == 0) {
/* 3060 */       switch (NinjaUtil.nextInt(3)) {
/*      */         case 0:
/* 3062 */           npcChat((short)5, "Hãy yên tâm giao đồ cho ta.");
/*      */           break;
/*      */         
/*      */         case 1:
/* 3066 */           npcChat((short)5, "Ta giữ đồ chưa hề để thất lạc bao giờ.");
/*      */           break;
/*      */         
/*      */         case 2:
/* 3070 */           npcChat((short)5, "Trên người ngươi toàn đồ có giá trị, sao không cất bớt ở đây?");
/*      */           break;
/*      */       } 
/*      */       
/*      */       return;
/*      */     } 
/* 3076 */     NpcTemplate npc = Server.npcs.get(24);
/* 3077 */     Message mss = new Message(39);
/* 3078 */     DataOutputStream ds = mss.writer();
/* 3079 */     ds.writeShort(24);
/* 3080 */     ds.writeUTF("Tao giữ đồ!");
/* 3081 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 3082 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 3083 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 3085 */     ds.flush();
/* 3086 */     sendMessage(mss);
/* 3087 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcUmayaki_1(byte menuId) throws IOException {
/* 3091 */     int size = this.menu.size();
/* 3092 */     if (size == 1) {
/* 3093 */       int index = ((Integer)this.menu.get(0)).intValue();
/* 3094 */       if (index >= 1 && index <= 7) {
/* 3095 */         (new short[8])[0] = -1; (new short[8])[1] = 10; (new short[8])[2] = 17; (new short[8])[3] = 22; (new short[8])[4] = 32; (new short[8])[5] = 38; (new short[8])[6] = 43; (new short[8])[7] = 48; short map = (new short[8])[index];
/* 3096 */         short[] xy = NinjaUtil.getXY(map);
/* 3097 */         this.x = xy[0];
/* 3098 */         this.y = xy[1];
/* 3099 */         changeMap(map); return;
/*      */       } 
/* 3101 */       if (index == 0) {
/* 3102 */         npcChat((short)7, "Tao kéo xe qua các làng!");
/*      */         return;
/*      */       } 
/*      */     } 
/* 3106 */     NpcTemplate npc = Server.npcs.get(7);
/* 3107 */     Message mss = new Message(39);
/* 3108 */     DataOutputStream ds = mss.writer();
/* 3109 */     ds.writeShort(7);
/* 3110 */     ds.writeUTF("Tao buôn hàng cấm đây!");
/* 3111 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 3112 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 3113 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 3115 */     ds.flush();
/* 3116 */     sendMessage(mss);
/* 3117 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcTajima(byte menuId) throws IOException {
/* 3121 */     int size = this.menu.size();
/* 3122 */     if (size == 1) {
/* 3123 */       int index = ((Integer)this.menu.get(0)).intValue();
/* 3124 */       if (index == 0) {
/* 3125 */         if (this.level == 3) {
/* 3126 */           Item item = new Item(194);
/* 3127 */           item.sys = 0;
/* 3128 */           item.isLock = true;
/* 3129 */           item.expire = -1L;
/* 3130 */           item.quantity = 1;
/* 3131 */           item.yen = 5;
/* 3132 */           item.options = new ArrayList<>();
/* 3133 */           item.options.add(new ItemOption(0, 10));
/* 3134 */           item.options.add(new ItemOption(8, 1));
/* 3135 */           addItemToBag(item);
/* 3136 */           this.potential[0] = 15;
/* 3137 */           this.potential[1] = 10;
/* 3138 */           this.potential[2] = 10;
/* 3139 */           this.potential[3] = 10;
/* 3140 */           MySkill my = new MySkill();
/* 3141 */           my.id = 0;
/* 3142 */           my.point = 0;
/* 3143 */           this.listSkill.add(my);
/* 3144 */           addExp(100000L);
/* 3145 */           this.user.service.sendInfo();
/*      */         } 
/*      */         return;
/*      */       } 
/*      */     } 
/* 3150 */     NpcTemplate npc = Server.npcs.get(12);
/* 3151 */     Message mss = new Message(39);
/* 3152 */     DataOutputStream ds = mss.writer();
/* 3153 */     ds.writeShort(12);
/* 3154 */     ds.writeUTF("Cho kiếm này.");
/* 3155 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 3156 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 3157 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 3159 */     ds.flush();
/* 3160 */     sendMessage(mss);
/* 3161 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcUmayaki_2(byte menuId) throws IOException {
/* 3165 */     int size = this.menu.size();
/* 3166 */     if (size == 1) {
/* 3167 */       int index = ((Integer)this.menu.get(0)).intValue();
/* 3168 */       if (index >= 0 && index <= 2) {
/* 3169 */         (new short[3])[0] = 1; (new short[3])[1] = 27; (new short[3])[2] = 72; short map = (new short[3])[index];
/* 3170 */         short[] xy = NinjaUtil.getXY(map);
/* 3171 */         this.x = xy[0];
/* 3172 */         this.y = xy[1];
/* 3173 */         changeMap(map); return;
/*      */       } 
/* 3175 */       if (index == 3) {
/* 3176 */         npcChat((short)8, "Tao kéo xe qua các trường!");
/*      */         return;
/*      */       } 
/*      */     } 
/* 3180 */     NpcTemplate npc = Server.npcs.get(8);
/* 3181 */     Message mss = new Message(39);
/* 3182 */     DataOutputStream ds = mss.writer();
/* 3183 */     ds.writeShort(8);
/* 3184 */     ds.writeUTF("Tao buôn hàng cấm đây!");
/* 3185 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 3186 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 3187 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 3189 */     ds.flush();
/* 3190 */     sendMessage(mss);
/* 3191 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcKanata(byte menuId) throws IOException {
/* 3195 */     int size = this.menu.size();
/* 3196 */     if (size == 1 && (
/* 3197 */       (Integer)this.menu.get(0)).intValue() == 0) {
/* 3198 */       this.user.service.openUI((byte)2);
/*      */       
/*      */       return;
/*      */     } 
/* 3202 */     NpcTemplate npc = Server.npcs.get(0);
/* 3203 */     Message mss = new Message(39);
/* 3204 */     DataOutputStream ds = mss.writer();
/* 3205 */     ds.writeShort(0);
/* 3206 */     ds.writeUTF("Tao buôn hàng cấm đây!");
/* 3207 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 3208 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 3209 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 3211 */     ds.flush();
/* 3212 */     sendMessage(mss);
/* 3213 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcGoosho(byte menuId) throws IOException {
/* 3217 */     int size = this.menu.size();
/* 3218 */     if (size == 1) {
/* 3219 */       if (((Integer)this.menu.get(0)).intValue() == 0) {
/* 3220 */         this.user.service.openUI((byte)14); return;
/*      */       } 
/* 3222 */       if (((Integer)this.menu.get(0)).intValue() == 1) {
/* 3223 */         this.user.service.openUI((byte)15); return;
/*      */       } 
/* 3225 */       if (((Integer)this.menu.get(0)).intValue() == 2) {
/* 3226 */         this.user.service.openUI((byte)32); return;
/*      */       } 
/* 3228 */       if (((Integer)this.menu.get(0)).intValue() == 3) {
/* 3229 */         this.user.service.openUI((byte)34);
/*      */         return;
/*      */       } 
/*      */     } 
/* 3233 */     NpcTemplate npc = Server.npcs.get(26);
/* 3234 */     Message mss = new Message(39);
/* 3235 */     DataOutputStream ds = mss.writer();
/* 3236 */     ds.writeShort(26);
/* 3237 */     ds.writeUTF("Hết hàng rồi nha bro!");
/* 3238 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 3239 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 3240 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 3242 */     ds.flush();
/* 3243 */     sendMessage(mss);
/* 3244 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void npcRakkii(byte menuId) throws IOException {
/* 3248 */     int size = this.menu.size();
/* 3249 */     if (size == 1) {
/* 3250 */       if (((Integer)this.menu.get(0)).intValue() == 0) {
/* 3251 */         this.user.service.openUI((byte)38); return;
/*      */       } 
/* 3253 */       if (((Integer)this.menu.get(0)).intValue() == 1) {
/* 3254 */         this.user.service.inputDlg("Mã quà tặng", 1234567);
/*      */         return;
/*      */       } 
/*      */     } 
/* 3258 */     if (size == 2) {
/* 3259 */       String law = "- Giá trị nhập xu thấp nhất của mỗi người là %s\n- Giá trị nhập xu cao nhất của mỗi người là %s\n- Mỗi 2 phút bắt đầu vòng quay một lần.\n- Khi có người bắt đầu nhập xu thì mới bắt đầu đêm ngược thời gian.\n- Còn 10 giây cuối sẽ bắt đầu khoá không cho gửi xu.\n- Người chiến thắng sẽ nhận tổng tất cả số tiền tất cả người chơi khác đặt cược sau khi trừ thuế.\n- Người chơi nhiều hơn 10 người thuế sẽ là 10.";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3266 */       if (((Integer)this.menu.get(0)).intValue() == 2) {
/* 3267 */         if (((Integer)this.menu.get(1)).intValue() == 0) {
/* 3268 */           this.user.service.showInfoLuckyDraw(Server.luckyDrawVIP); return;
/*      */         } 
/* 3270 */         if (((Integer)this.menu.get(1)).intValue() == 1) {
/* 3271 */           this.user.service.inputDlg(Server.luckyDrawVIP.name, 2402); return;
/*      */         } 
/* 3273 */         if (((Integer)this.menu.get(1)).intValue() == 2) {
/* 3274 */           this.user.service.showAlert(Server.luckyDrawVIP.name, String.format(law, new Object[] { NinjaUtil.getCurrency(Server.luckyDrawVIP.xuMin), NinjaUtil.getCurrency(Server.luckyDrawVIP.xuMax) }));
/*      */           return;
/*      */         } 
/*      */       } 
/* 3278 */       if (((Integer)this.menu.get(0)).intValue() == 3) {
/* 3279 */         if (((Integer)this.menu.get(1)).intValue() == 0) {
/* 3280 */           this.user.service.showInfoLuckyDraw(Server.luckyDrawNormal); return;
/*      */         } 
/* 3282 */         if (((Integer)this.menu.get(1)).intValue() == 1) {
/* 3283 */           this.user.service.inputDlg(Server.luckyDrawNormal.name, 2003); return;
/*      */         } 
/* 3285 */         if (((Integer)this.menu.get(1)).intValue() == 2) {
/* 3286 */           this.user.service.showAlert(Server.luckyDrawNormal.name, String.format(law, new Object[] { NinjaUtil.getCurrency(Server.luckyDrawNormal.xuMin), NinjaUtil.getCurrency(Server.luckyDrawNormal.xuMax) }));
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 3291 */     NpcTemplate npc = Server.npcs.get(30);
/* 3292 */     Message mss = new Message(39);
/* 3293 */     DataOutputStream ds = mss.writer();
/* 3294 */     ds.writeShort(30);
/* 3295 */     ds.writeUTF("Muốn giải trí không bro!");
/* 3296 */     ds.writeByte((npc.menu[menuId]).length - 1);
/* 3297 */     for (int i = 1; i < (npc.menu[menuId]).length; i++) {
/* 3298 */       ds.writeUTF(npc.menu[menuId][i]);
/*      */     }
/* 3300 */     ds.flush();
/* 3301 */     sendMessage(mss);
/* 3302 */     mss.cleanup();
/*      */   }
/*      */   
/*      */   public void pickItem(Message ms) throws IOException {
/* 3306 */     short itemMapId = ms.reader().readShort();
/* 3307 */     ItemMap item = this.zone.getItemMapById(itemMapId);
/* 3308 */     if (item != null) {
/* 3309 */       int rangeX = NinjaUtil.getRange(this.x, item.x);
/* 3310 */       int rangeY = NinjaUtil.getRange(this.y, item.y);
/* 3311 */       if (rangeX < 30 && rangeY < 30) {
/* 3312 */         if (item.owner == null || equals(item.owner) || item.timeCount < 30) {
/* 3313 */           if (item.item.entry.type != 19) {
/* 3314 */             int num = getSlotNull();
/* 3315 */             if (item.item.entry.isUpToUp) {
/* 3316 */               int index = getIndexItemByIdInBag(item.item.id);
/* 3317 */               if (index == -1 && num == 0) {
/* 3318 */                 startOKDlg("Hành trang không dủ chỗ trống!");
/*      */                 
/*      */                 return;
/*      */               } 
/* 3322 */             } else if (num == 0) {
/* 3323 */               startOKDlg("Hành trang không dủ chỗ trống!");
/*      */               
/*      */               return;
/*      */             } 
/* 3327 */             addItemToBag(item.item);
/*      */           } else {
/* 3329 */             addYen(item.item.quantity);
/*      */           } 
/* 3331 */           ms = new Message(-14);
/* 3332 */           DataOutputStream ds = ms.writer();
/* 3333 */           ds.writeShort(item.id);
/* 3334 */           ds.writeShort(item.item.quantity);
/* 3335 */           ds.flush();
/* 3336 */           sendMessage(ms);
/*      */           
/* 3338 */           ms = new Message(-13);
/* 3339 */           ds = ms.writer();
/* 3340 */           ds.writeShort(item.id);
/* 3341 */           ds.writeInt(this.id);
/* 3342 */           ds.flush();
/* 3343 */           for (Character pl : characters_name.values()) {
/* 3344 */             if (pl != null && !equals(pl)) {
/* 3345 */               pl.sendMessage(ms);
/*      */             }
/*      */           } 
/* 3348 */           this.zone.removeItem(item.id);
/*      */         } else {
/* 3350 */           this.user.service.addInfoMe("Vật phẩm của người khác");
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public void throwItem(Message ms) throws IOException {
/* 3357 */     byte indexUI = ms.reader().readByte();
/* 3358 */     if (indexUI < 0 || indexUI >= this.numberCellBag || this.bag[indexUI] == null || (this.bag[indexUI]).isLock) {
/*      */       return;
/*      */     }
/* 3361 */     Item item = this.bag[indexUI].clone();
/* 3362 */     this.zone.numberDropItem = (short)(this.zone.numberDropItem + 1); ItemMap itemMap = new ItemMap(this.zone.numberDropItem);
/* 3363 */     itemMap.x = (short)NinjaUtil.nextInt(this.x - 30, this.x + 30);
/* 3364 */     itemMap.y = this.y;
/* 3365 */     itemMap.item = item;
/* 3366 */     itemMap.owner = null;
/* 3367 */     this.zone.put(itemMap.id, itemMap);
/* 3368 */     this.bag[indexUI] = null;
/* 3369 */     this.user.service.throwItem(indexUI, itemMap.id, (short)itemMap.item.id, itemMap.x, itemMap.y);
/*      */   }
/*      */ 
/*      */   
/*      */   public void changeMap() throws IOException {
/* 3374 */     this.user.service.startWaitDlg();
/* 3375 */     int mapId = this.mapId;
/* 3376 */     Map map = MapManager.getMapById(mapId);
/* 3377 */     for (Waypoint way : map.tilemap.waypoints) {
/* 3378 */       if (this.x >= way.minX - 40 && this.x <= way.maxX + 40 && this.y >= way.minY - 40 && this.y <= way.maxY + 40) {
/* 3379 */         switch (way.mapId) {
/*      */           case 1:
/* 3381 */             if (mapId == 2) {
/* 3382 */               this.x = 35;
/* 3383 */               this.y = 384; break;
/* 3384 */             }  if (mapId == 3) {
/* 3385 */               this.x = 1885;
/* 3386 */               this.y = 360;
/*      */             } 
/*      */             break;
/*      */           case 2:
/* 3390 */             if (mapId == 6) {
/* 3391 */               this.x = 35;
/* 3392 */               this.y = 216; break;
/* 3393 */             }  if (mapId == 1) {
/* 3394 */               this.x = 1405;
/* 3395 */               this.y = 216;
/*      */             } 
/*      */             break;
/*      */           case 3:
/* 3399 */             if (mapId == 1) {
/* 3400 */               this.x = 35;
/* 3401 */               this.y = 288; break;
/* 3402 */             }  if (mapId == 4) {
/* 3403 */               this.x = 1405;
/* 3404 */               this.y = 264;
/*      */             } 
/*      */             break;
/*      */           case 4:
/* 3408 */             if (mapId == 3) {
/* 3409 */               this.x = 35;
/* 3410 */               this.y = 216; break;
/* 3411 */             }  if (mapId == 5) {
/* 3412 */               this.x = 2845;
/* 3413 */               this.y = 216;
/*      */             } 
/*      */             break;
/*      */           case 5:
/* 3417 */             if (mapId == 4) {
/* 3418 */               this.x = 35;
/* 3419 */               this.y = 144; break;
/* 3420 */             }  if (mapId == 7) {
/* 3421 */               this.x = 1741;
/* 3422 */               this.y = 288;
/*      */             } 
/*      */             break;
/*      */           case 6:
/* 3426 */             if (mapId == 7) {
/* 3427 */               this.x = 445;
/* 3428 */               this.y = 1704; break;
/* 3429 */             }  if (mapId == 21) {
/* 3430 */               this.x = 37;
/* 3431 */               this.y = 120; break;
/* 3432 */             }  if (mapId == 2) {
/* 3433 */               this.x = 445;
/* 3434 */               this.y = 120; break;
/* 3435 */             }  if (mapId == 20) {
/* 3436 */               this.x = 263;
/* 3437 */               this.y = 1872;
/*      */             } 
/*      */             break;
/*      */           case 7:
/* 3441 */             if (mapId == 5) {
/* 3442 */               this.x = 659;
/* 3443 */               this.y = 72; break;
/* 3444 */             }  if (mapId == 6) {
/* 3445 */               this.x = 35;
/* 3446 */               this.y = 192; break;
/* 3447 */             }  if (mapId == 8) {
/* 3448 */               this.x = 5965;
/* 3449 */               this.y = 288;
/*      */             } 
/*      */             break;
/*      */           case 8:
/* 3453 */             if (mapId == 9) {
/* 3454 */               this.x = 1885;
/* 3455 */               this.y = 264; break;
/* 3456 */             }  if (mapId == 7) {
/* 3457 */               this.x = 35;
/* 3458 */               this.y = 168;
/*      */             } 
/*      */             break;
/*      */           case 9:
/* 3462 */             if (mapId == 8) {
/* 3463 */               this.x = 35;
/* 3464 */               this.y = 288; break;
/* 3465 */             }  if (mapId == 10) {
/* 3466 */               this.x = 1885;
/* 3467 */               this.y = 264;
/*      */             } 
/*      */             break;
/*      */           case 10:
/* 3471 */             if (mapId == 9) {
/* 3472 */               this.x = 35;
/* 3473 */               this.y = 264; break;
/* 3474 */             }  if (mapId == 11) {
/* 3475 */               this.x = 1612;
/* 3476 */               this.y = 264;
/*      */             } 
/*      */             break;
/*      */           case 11:
/* 3480 */             if (mapId == 10) {
/* 3481 */               this.x = 892;
/* 3482 */               this.y = 72; break;
/* 3483 */             }  if (mapId == 12) {
/* 3484 */               this.x = 925;
/* 3485 */               this.y = 720;
/*      */             } 
/*      */             break;
/*      */           case 12:
/* 3489 */             if (mapId == 11) {
/* 3490 */               this.x = 35;
/* 3491 */               this.y = 264; break;
/* 3492 */             }  if (mapId == 57) {
/* 3493 */               this.x = 2125;
/* 3494 */               this.y = 288;
/*      */             } 
/*      */             break;
/*      */           case 13:
/* 3498 */             if (mapId == 57) {
/* 3499 */               this.x = 35;
/* 3500 */               this.y = 192; break;
/* 3501 */             }  if (mapId == 14) {
/* 3502 */               this.x = 925;
/* 3503 */               this.y = 456;
/*      */             } 
/*      */             break;
/*      */           case 14:
/* 3507 */             if (mapId == 13) {
/* 3508 */               this.x = 35;
/* 3509 */               this.y = 216; break;
/* 3510 */             }  if (mapId == 15) {
/* 3511 */               this.x = 1885;
/* 3512 */               this.y = 264;
/*      */             } 
/*      */             break;
/*      */           case 15:
/* 3516 */             if (mapId == 14) {
/* 3517 */               this.x = 35;
/* 3518 */               this.y = 168; break;
/* 3519 */             }  if (mapId == 16) {
/* 3520 */               this.x = 1405;
/* 3521 */               this.y = 144;
/*      */             } 
/*      */             break;
/*      */           case 16:
/* 3525 */             if (mapId == 15) {
/* 3526 */               this.x = 35;
/* 3527 */               this.y = 288; break;
/* 3528 */             }  if (mapId == 17) {
/* 3529 */               this.x = 925;
/* 3530 */               this.y = 168;
/*      */             } 
/*      */             break;
/*      */           case 17:
/* 3534 */             if (mapId == 16) {
/* 3535 */               this.x = 35;
/* 3536 */               this.y = 264; break;
/* 3537 */             }  if (mapId == 18) {
/* 3538 */               this.x = 1645;
/* 3539 */               this.y = 144;
/*      */             } 
/*      */             break;
/*      */           case 18:
/* 3543 */             if (mapId == 17) {
/* 3544 */               this.x = 35;
/* 3545 */               this.y = 432; break;
/* 3546 */             }  if (mapId == 19) {
/* 3547 */               this.x = 1765;
/* 3548 */               this.y = 360;
/*      */             } 
/*      */             break;
/*      */           case 19:
/* 3552 */             if (mapId == 18) {
/* 3553 */               this.x = 35;
/* 3554 */               this.y = 360; break;
/* 3555 */             }  if (mapId == 58) {
/* 3556 */               this.x = 1645;
/* 3557 */               this.y = 360;
/*      */             } 
/*      */             break;
/*      */           case 20:
/* 3561 */             if (mapId == 6) {
/* 3562 */               this.x = 194;
/* 3563 */               this.y = 48;
/*      */             } 
/*      */             break;
/*      */           case 21:
/* 3567 */             if (mapId == 22) {
/* 3568 */               this.x = 422;
/* 3569 */               this.y = 480; break;
/* 3570 */             }  if (mapId == 6) {
/* 3571 */               this.x = 1645;
/* 3572 */               this.y = 360;
/*      */             } 
/*      */             break;
/*      */           case 22:
/* 3576 */             if (mapId == 23) {
/* 3577 */               this.x = 50;
/* 3578 */               this.y = 168; break;
/* 3579 */             }  if (mapId == 21) {
/* 3580 */               this.x = 2805;
/* 3581 */               this.y = 72;
/*      */             } 
/*      */             break;
/*      */           case 23:
/* 3585 */             if (mapId == 22) {
/* 3586 */               this.x = 685;
/* 3587 */               this.y = 1848; break;
/* 3588 */             }  if (mapId == 25) {
/* 3589 */               this.x = 685;
/* 3590 */               this.y = 120; break;
/* 3591 */             }  if (mapId == 69) {
/* 3592 */               this.x = 88;
/* 3593 */               this.y = 1848;
/*      */             } 
/*      */             break;
/*      */           case 24:
/* 3597 */             if (mapId == 59) {
/* 3598 */               this.x = 35;
/* 3599 */               this.y = 432; break;
/* 3600 */             }  if (mapId == 36) {
/* 3601 */               this.x = 1885;
/* 3602 */               this.y = 312;
/*      */             } 
/*      */             break;
/*      */           case 25:
/* 3606 */             if (mapId == 23) {
/* 3607 */               this.x = 35;
/* 3608 */               this.y = 216; break;
/* 3609 */             }  if (mapId == 26) {
/* 3610 */               this.x = 2365;
/* 3611 */               this.y = 264;
/*      */             } 
/*      */             break;
/*      */           case 26:
/* 3615 */             if (mapId == 25) {
/* 3616 */               this.x = 35;
/* 3617 */               this.y = 240; break;
/* 3618 */             }  if (mapId == 27) {
/* 3619 */               this.x = 3565;
/* 3620 */               this.y = 240;
/*      */             } 
/*      */             break;
/*      */           case 27:
/* 3624 */             if (mapId == 26) {
/* 3625 */               this.x = 35;
/* 3626 */               this.y = 408; break;
/* 3627 */             }  if (mapId == 28) {
/* 3628 */               this.x = 2845;
/* 3629 */               this.y = 384;
/*      */             } 
/*      */             break;
/*      */           case 28:
/* 3633 */             if (mapId == 27) {
/* 3634 */               this.x = 35;
/* 3635 */               this.y = 288; break;
/* 3636 */             }  if (mapId == 60) {
/* 3637 */               this.x = 1165;
/* 3638 */               this.y = 72;
/*      */             } 
/*      */             break;
/*      */           case 29:
/* 3642 */             if (mapId == 60) {
/* 3643 */               this.x = 35;
/* 3644 */               this.y = 912; break;
/* 3645 */             }  if (mapId == 30) {
/* 3646 */               this.x = 1501;
/* 3647 */               this.y = 888;
/*      */             } 
/*      */             break;
/*      */           case 30:
/* 3651 */             if (mapId == 29) {
/* 3652 */               this.x = 35;
/* 3653 */               this.y = 240; break;
/* 3654 */             }  if (mapId == 31) {
/* 3655 */               this.x = 2845;
/* 3656 */               this.y = 288;
/*      */             } 
/*      */             break;
/*      */           case 31:
/* 3660 */             if (mapId == 30) {
/* 3661 */               this.x = 35;
/* 3662 */               this.y = 264; break;
/* 3663 */             }  if (mapId == 32) {
/* 3664 */               this.x = 2365;
/* 3665 */               this.y = 264;
/*      */             } 
/*      */             break;
/*      */           case 32:
/* 3669 */             if (mapId == 31) {
/* 3670 */               this.x = 35;
/* 3671 */               this.y = 384; break;
/* 3672 */             }  if (mapId == 61) {
/* 3673 */               this.x = 2749;
/* 3674 */               this.y = 432;
/*      */             } 
/*      */             break;
/*      */           case 33:
/* 3678 */             if (mapId == 61) {
/* 3679 */               this.x = 35;
/* 3680 */               this.y = 216; break;
/* 3681 */             }  if (mapId == 34) {
/* 3682 */               this.x = 3325;
/* 3683 */               this.y = 192;
/*      */             } 
/*      */             break;
/*      */           case 34:
/* 3687 */             if (mapId == 33) {
/* 3688 */               this.x = 35;
/* 3689 */               this.y = 168; break;
/* 3690 */             }  if (mapId == 35) {
/* 3691 */               this.x = 2365;
/* 3692 */               this.y = 192;
/*      */             } 
/*      */             break;
/*      */           case 35:
/* 3696 */             if (mapId == 34) {
/* 3697 */               this.x = 35;
/* 3698 */               this.y = 672; break;
/* 3699 */             }  if (mapId == 66) {
/* 3700 */               this.x = 1861;
/* 3701 */               this.y = 72;
/*      */             } 
/*      */             break;
/*      */           case 36:
/* 3705 */             if (mapId == 24) {
/* 3706 */               this.x = 35;
/* 3707 */               this.y = 368; break;
/* 3708 */             }  if (mapId == 37) {
/* 3709 */               this.x = 2365;
/* 3710 */               this.y = 264;
/*      */             } 
/*      */             break;
/*      */           case 37:
/* 3714 */             if (mapId == 36) {
/* 3715 */               this.x = 35;
/* 3716 */               this.y = 648;
/*      */             } 
/*      */             break;
/*      */           case 38:
/* 3720 */             if (mapId == 67) {
/* 3721 */               this.x = 35;
/* 3722 */               this.y = 288; break;
/* 3723 */             }  if (mapId == 68) {
/* 3724 */               this.x = 1885;
/* 3725 */               this.y = 288;
/*      */             } 
/*      */             break;
/*      */           case 39:
/* 3729 */             if (mapId == 72) {
/* 3730 */               this.x = 1771;
/* 3731 */               this.y = 72; break;
/* 3732 */             }  if (mapId == 46) {
/* 3733 */               this.x = 85;
/* 3734 */               this.y = 312; break;
/* 3735 */             }  if (mapId == 40) {
/* 3736 */               this.x = 3205;
/* 3737 */               this.y = 288;
/*      */             } 
/*      */             break;
/*      */           case 40:
/* 3741 */             if (mapId == 39) {
/* 3742 */               this.x = 35;
/* 3743 */               this.y = 264; break;
/* 3744 */             }  if (mapId == 41) {
/* 3745 */               this.x = 2973;
/* 3746 */               this.y = 336; break;
/* 3747 */             }  if (mapId == 65) {
/* 3748 */               this.x = 3027;
/* 3749 */               this.y = 120;
/*      */             } 
/*      */             break;
/*      */           case 41:
/* 3753 */             if (mapId == 40) {
/* 3754 */               this.x = 519;
/* 3755 */               this.y = 72; break;
/* 3756 */             }  if (mapId == 42) {
/* 3757 */               this.x = 35;
/* 3758 */               this.y = 360; break;
/* 3759 */             }  if (mapId == 43) {
/* 3760 */               this.x = 2005;
/* 3761 */               this.y = 528;
/*      */             } 
/*      */             break;
/*      */           case 42:
/* 3765 */             if (mapId == 41) {
/* 3766 */               this.x = 925;
/* 3767 */               this.y = 912; break;
/* 3768 */             }  if (mapId == 62) {
/* 3769 */               this.x = 42;
/* 3770 */               this.y = 120;
/*      */             } 
/*      */             break;
/*      */           case 43:
/* 3774 */             if (mapId == 41) {
/* 3775 */               this.x = 35;
/* 3776 */               this.y = 456; break;
/* 3777 */             }  if (mapId == 44) {
/* 3778 */               this.x = 2629;
/* 3779 */               this.y = 240;
/*      */             } 
/*      */             break;
/*      */           case 44:
/* 3783 */             if (mapId == 43) {
/* 3784 */               this.x = 35;
/* 3785 */               this.y = 672; break;
/* 3786 */             }  if (mapId == 45) {
/* 3787 */               this.x = 1573;
/* 3788 */               this.y = 480;
/*      */             } 
/*      */             break;
/*      */           case 45:
/* 3792 */             if (mapId == 44) {
/* 3793 */               this.x = 59;
/* 3794 */               this.y = 96; break;
/* 3795 */             }  if (mapId == 53) {
/* 3796 */               this.x = 1189;
/* 3797 */               this.y = 816;
/*      */             } 
/*      */             break;
/*      */           case 46:
/* 3801 */             if (mapId == 39) {
/* 3802 */               this.x = 72;
/* 3803 */               this.y = 72; break;
/* 3804 */             }  if (mapId == 47) {
/* 3805 */               this.x = 1429;
/* 3806 */               this.y = 264; break;
/* 3807 */             }  if (mapId == 63) {
/* 3808 */               this.x = 1429;
/* 3809 */               this.y = 672;
/*      */             } 
/*      */             break;
/*      */           case 47:
/* 3813 */             if (mapId == 46) {
/* 3814 */               this.x = 35;
/* 3815 */               this.y = 240; break;
/* 3816 */             }  if (mapId == 48) {
/* 3817 */               this.x = 2365;
/* 3818 */               this.y = 384;
/*      */             } 
/*      */             break;
/*      */           case 48:
/* 3822 */             if (mapId == 47) {
/* 3823 */               this.x = 35;
/* 3824 */               this.y = 432; break;
/* 3825 */             }  if (mapId == 50) {
/* 3826 */               this.x = 2869;
/* 3827 */               this.y = 336;
/*      */             } 
/*      */             break;
/*      */           case 49:
/* 3831 */             if (mapId == 50) {
/* 3832 */               this.x = 35;
/* 3833 */               this.y = 336; break;
/* 3834 */             }  if (mapId == 51) {
/* 3835 */               this.x = 2365;
/* 3836 */               this.y = 456;
/*      */             } 
/*      */             break;
/*      */           case 50:
/* 3840 */             if (mapId == 48) {
/* 3841 */               this.x = 35;
/* 3842 */               this.y = 480; break;
/* 3843 */             }  if (mapId == 49) {
/* 3844 */               this.x = 2221;
/* 3845 */               this.y = 432;
/*      */             } 
/*      */             break;
/*      */           case 51:
/* 3849 */             if (mapId == 49) {
/* 3850 */               this.x = 35;
/* 3851 */               this.y = 240; break;
/* 3852 */             }  if (mapId == 52) {
/* 3853 */               this.x = 1645;
/* 3854 */               this.y = 288;
/*      */             } 
/*      */             break;
/*      */           case 52:
/* 3858 */             if (mapId == 51) {
/* 3859 */               this.x = 35;
/* 3860 */               this.y = 384; break;
/* 3861 */             }  if (mapId == 64) {
/* 3862 */               this.x = 2869;
/* 3863 */               this.y = 288;
/*      */             } 
/*      */             break;
/*      */           case 53:
/* 3867 */             if (mapId == 45) {
/* 3868 */               this.x = 35;
/* 3869 */               this.y = 144; break;
/* 3870 */             }  if (mapId == 54) {
/* 3871 */               this.x = 1165;
/* 3872 */               this.y = 1032;
/*      */             } 
/*      */             break;
/*      */           case 54:
/* 3876 */             if (mapId == 53) {
/* 3877 */               this.x = 2347;
/* 3878 */               this.y = 72; break;
/* 3879 */             }  if (mapId == 55) {
/* 3880 */               this.x = 35;
/* 3881 */               this.y = 264;
/*      */             } 
/*      */             break;
/*      */           case 55:
/* 3885 */             if (mapId == 54) {
/* 3886 */               this.x = 445;
/* 3887 */               this.y = 1386;
/*      */             } 
/*      */             break;
/*      */           case 57:
/* 3891 */             if (mapId == 12) {
/* 3892 */               this.x = 35;
/* 3893 */               this.y = 192; break;
/* 3894 */             }  if (mapId == 13) {
/* 3895 */               this.x = 565;
/* 3896 */               this.y = 264;
/*      */             } 
/*      */             break;
/*      */           case 58:
/* 3900 */             if (mapId == 19) {
/* 3901 */               this.x = 35;
/* 3902 */               this.y = 192;
/*      */             } 
/*      */             break;
/*      */           case 59:
/* 3906 */             if (mapId == 68) {
/* 3907 */               this.x = 35;
/* 3908 */               this.y = 168; break;
/* 3909 */             }  if (mapId == 24) {
/* 3910 */               this.x = 1407;
/* 3911 */               this.y = 168;
/*      */             } 
/*      */             break;
/*      */           case 60:
/* 3915 */             if (mapId == 28) {
/* 3916 */               this.x = 35;
/* 3917 */               this.y = 120; break;
/* 3918 */             }  if (mapId == 29) {
/* 3919 */               this.x = 445;
/* 3920 */               this.y = 264;
/*      */             } 
/*      */             break;
/*      */           case 61:
/* 3924 */             if (mapId == 32) {
/* 3925 */               this.x = 35;
/* 3926 */               this.y = 192; break;
/* 3927 */             }  if (mapId == 33) {
/* 3928 */               this.x = 1165;
/* 3929 */               this.y = 240;
/*      */             } 
/*      */             break;
/*      */           case 62:
/* 3933 */             if (mapId == 42) {
/* 3934 */               this.x = 1045;
/* 3935 */               this.y = 288;
/*      */             } 
/*      */             break;
/*      */           case 63:
/* 3939 */             if (mapId == 46) {
/* 3940 */               this.x = 35;
/* 3941 */               this.y = 48;
/*      */             } 
/*      */             break;
/*      */           case 64:
/* 3945 */             if (mapId == 52) {
/* 3946 */               this.x = 35;
/* 3947 */               this.y = 816;
/*      */             } 
/*      */             break;
/*      */           case 65:
/* 3951 */             if (mapId == 40) {
/* 3952 */               this.x = 35;
/* 3953 */               this.y = 312;
/*      */             } 
/*      */             break;
/*      */           case 66:
/* 3957 */             if (mapId == 35) {
/* 3958 */               this.x = 829;
/* 3959 */               this.y = 480; break;
/* 3960 */             }  if (mapId == 67) {
/* 3961 */               this.x = 1525;
/* 3962 */               this.y = 168;
/*      */             } 
/*      */             break;
/*      */           case 67:
/* 3966 */             if (mapId == 66) {
/* 3967 */               this.x = 35;
/* 3968 */               this.y = 144; break;
/* 3969 */             }  if (mapId == 38) {
/* 3970 */               this.x = 685;
/* 3971 */               this.y = 816;
/*      */             } 
/*      */             break;
/*      */           case 68:
/* 3975 */             if (mapId == 38) {
/* 3976 */               this.x = 35;
/* 3977 */               this.y = 672; break;
/* 3978 */             }  if (mapId == 59) {
/* 3979 */               this.x = 1285;
/* 3980 */               this.y = 408;
/*      */             } 
/*      */             break;
/*      */           case 69:
/* 3984 */             if (mapId == 23) {
/* 3985 */               this.x = 35;
/* 3986 */               this.y = 48; break;
/* 3987 */             }  if (mapId == 70) {
/* 3988 */               this.x = 1405;
/* 3989 */               this.y = 216;
/*      */             } 
/*      */             break;
/*      */           case 70:
/* 3993 */             if (mapId == 69) {
/* 3994 */               this.x = 35;
/* 3995 */               this.y = 528; break;
/* 3996 */             }  if (mapId == 71) {
/* 3997 */               this.x = 1645;
/* 3998 */               this.y = 192;
/*      */             } 
/*      */             break;
/*      */           case 71:
/* 4002 */             if (mapId == 70) {
/* 4003 */               this.x = 35;
/* 4004 */               this.y = 432; break;
/* 4005 */             }  if (mapId == 72) {
/* 4006 */               this.x = 1645;
/* 4007 */               this.y = 432;
/*      */             } 
/*      */             break;
/*      */           case 72:
/* 4011 */             if (mapId == 71) {
/* 4012 */               this.x = 35;
/* 4013 */               this.y = 432; break;
/* 4014 */             }  if (mapId == 39) {
/* 4015 */               this.x = 1809;
/* 4016 */               this.y = 672;
/*      */             } 
/*      */             break;
/*      */         } 
/* 4020 */         changeMap(way.mapId);
/*      */         return;
/*      */       } 
/*      */     } 
/* 4024 */     changeMap(this.mapId);
/*      */   }
/*      */   
/*      */   public void changePk(Message ms) throws IOException {
/* 4028 */     byte type = ms.reader().readByte();
/* 4029 */     if (type < 0 || type > 3) {
/*      */       return;
/*      */     }
/* 4032 */     this.typePk = type;
/* 4033 */     this.user.service.changePk();
/*      */   }
/*      */   
/*      */   public void changeMap(int id) throws IOException {
/* 4037 */     this.mapId = (short)id;
/* 4038 */     Map mape = MapManager.getMapById(id);
/* 4039 */     Collection<Zone> zones = mape.getZones();
/* 4040 */     byte zoneId = 0;
/* 4041 */     for (Zone zone : zones) {
/* 4042 */       if (zone.numberCharacter > 15) {
/* 4043 */         zoneId = (byte)(zoneId + 1);
/*      */       }
/*      */     } 
/* 4046 */     int size = zones.size();
/* 4047 */     if (zoneId < 0 || zoneId > size) {
/* 4048 */       zoneId = (byte)NinjaUtil.nextInt(size);
/*      */     }
/* 4050 */     MapManager.outZone(this);
/* 4051 */     MapManager.joinZone(this, id, zoneId);
/* 4052 */     this.user.service.sendZone();
/*      */   }
/*      */   
/*      */   public void boxCoinIn(Message ms) throws IOException {
/* 4056 */     int xu = ms.reader().readInt();
/* 4057 */     if (xu > this.xu) {
/* 4058 */       startOKDlg("Số xu ở hành trang không đủ!");
/*      */       return;
/*      */     } 
/* 4061 */     this.xu -= xu;
/* 4062 */     this.xuInBox += xu;
/* 4063 */     this.user.service.boxCoinIn(xu);
/*      */   }
/*      */   
/*      */   public void boxCoinOut(Message ms) throws IOException {
/* 4067 */     int xu = ms.reader().readInt();
/* 4068 */     if (xu > this.xuInBox) {
/* 4069 */       startOKDlg("Số xu ở rương không đủ!");
/*      */       return;
/*      */     } 
/* 4072 */     this.xuInBox -= xu;
/* 4073 */     this.xu += xu;
/* 4074 */     this.user.service.boxCoinOut(xu);
/*      */   }
/*      */   
/*      */   public void requestCharacterInfo(Message ms) throws IOException {
/* 4078 */     int num = ms.reader().readByte();
/* 4079 */     this.logger.log("requestCharacterInfo num: " + num);
/* 4080 */     for (int i = 0; i < num; i++) {
/* 4081 */       this.logger.log("requestCharacterInfo charId: " + ms.reader().readInt());
/*      */     }
/*      */   }
/*      */   
/*      */   public void requestItemCharacter(Message ms) throws IOException {
/* 4086 */     int charId = ms.reader().readInt();
/* 4087 */     byte indexUI = ms.reader().readByte();
/* 4088 */     Character _char = getCharacterById(charId);
/* 4089 */     if (_char != null && 
/* 4090 */       _char.equiped[indexUI] != null) {
/* 4091 */       Equiped equiped = _char.equiped[indexUI];
/* 4092 */       this.user.service.requesItemCharacter(equiped);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Character getCharacterById(int charId) {
/* 4098 */     return characters_id.get(Integer.valueOf(charId));
/*      */   }
/*      */   
/*      */   public static Character getCharacterByName(String name) {
/* 4102 */     return characters_name.get(name);
/*      */   }
/*      */   
/*      */   public void requestItem(Message ms) throws IOException {
/* 4106 */     byte typeUI = ms.reader().readByte();
/* 4107 */     switch (typeUI) {
/*      */       
/*      */       case 4:
/* 4110 */         this.user.service.sendBox();
/*      */         return;
/*      */     } 
/*      */     
/* 4114 */     store(typeUI);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void requestEnemies() throws IOException {}
/*      */ 
/*      */   
/*      */   public void store(byte type) throws IOException {
/* 4123 */     this.logger.log("Store: " + String.valueOf(type));
/* 4124 */     ArrayList<ItemStore> store = null;
/* 4125 */     switch (type) {
/*      */       case 2:
/* 4127 */         store = StoreData.VU_KHI;
/*      */         break;
/*      */       
/*      */       case 6:
/* 4131 */         store = StoreData.DUOC_PHAM;
/*      */         break;
/*      */       
/*      */       case 7:
/* 4135 */         store = StoreData.DUOC_PHAM_KHOA;
/*      */         break;
/*      */       
/*      */       case 8:
/* 4139 */         store = StoreData.THUC_AN;
/*      */         break;
/*      */       
/*      */       case 9:
/* 4143 */         store = StoreData.THUC_AN_KHOA;
/*      */         break;
/*      */       
/*      */       case 14:
/* 4147 */         store = StoreData.LINH_TINH;
/*      */         break;
/*      */       
/*      */       case 15:
/* 4151 */         store = StoreData.SACH;
/*      */         break;
/*      */       
/*      */       case 32:
/* 4155 */         store = StoreData.THOI_TRANG;
/*      */         break;
/*      */       
/*      */       case 34:
/* 4159 */         store = StoreData.GIA_TOC;
/*      */         break;
/*      */       
/*      */       case 16:
/* 4163 */         store = StoreData.DAY_CHUYEN;
/*      */         break;
/*      */       
/*      */       case 17:
/* 4167 */         store = StoreData.NHAN;
/*      */         break;
/*      */       
/*      */       case 18:
/* 4171 */         store = StoreData.NGOC_BOI;
/*      */         break;
/*      */       
/*      */       case 19:
/* 4175 */         store = StoreData.BUA;
/*      */         break;
/*      */       
/*      */       case 20:
/* 4179 */         store = StoreData.NON_NAM;
/*      */         break;
/*      */       
/*      */       case 21:
/* 4183 */         store = StoreData.NON_NU;
/*      */         break;
/*      */       
/*      */       case 22:
/* 4187 */         store = StoreData.AO_NAM;
/*      */         break;
/*      */       
/*      */       case 23:
/* 4191 */         store = StoreData.AO_NU;
/*      */         break;
/*      */       
/*      */       case 24:
/* 4195 */         store = StoreData.GANG_NAM;
/*      */         break;
/*      */       
/*      */       case 25:
/* 4199 */         store = StoreData.GANG_NU;
/*      */         break;
/*      */       
/*      */       case 26:
/* 4203 */         store = StoreData.QUAN_NAM;
/*      */         break;
/*      */       
/*      */       case 27:
/* 4207 */         store = StoreData.QUAN_NU;
/*      */         break;
/*      */       
/*      */       case 28:
/* 4211 */         store = StoreData.GIAY_NAM;
/*      */         break;
/*      */       
/*      */       case 29:
/* 4215 */         store = StoreData.GIAY_NU;
/*      */         break;
/*      */     } 
/* 4218 */     if (store != null) {
/* 4219 */       Message ms = new Message(33);
/* 4220 */       DataOutputStream ds = ms.writer();
/* 4221 */       ds.writeByte(type);
/* 4222 */       int num = store.size();
/* 4223 */       ds.writeByte(num);
/* 4224 */       for (ItemStore item : store) {
/* 4225 */         ds.writeByte(item.index);
/* 4226 */         ds.writeShort(item.templateId);
/*      */       } 
/* 4228 */       ds.flush();
/* 4229 */       sendMessage(ms);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void requestItemInfo(Message ms) throws IOException {
/* 4234 */     byte typeUI = ms.reader().readByte();
/* 4235 */     byte indexUI = ms.reader().readByte();
/* 4236 */     this.logger.log("requestItemInfo type: " + typeUI + " - indexUI: " + indexUI);
/* 4237 */     if (typeUI == 2 || (typeUI >= 14 && typeUI <= 29) || typeUI == 32 || typeUI == 34 || typeUI == 8 || typeUI == 9 || typeUI == 7 || typeUI == 8) {
/* 4238 */       ItemStore item = new ItemStore();
/* 4239 */       switch (typeUI) {
/*      */         case 2:
/* 4241 */           item = StoreData.VU_KHI.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 6:
/* 4245 */           item = StoreData.DUOC_PHAM.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 7:
/* 4249 */           item = StoreData.DUOC_PHAM_KHOA.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 8:
/* 4253 */           item = StoreData.THUC_AN.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 9:
/* 4257 */           item = StoreData.THUC_AN_KHOA.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 14:
/* 4261 */           item = StoreData.LINH_TINH.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 15:
/* 4265 */           item = StoreData.SACH.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 32:
/* 4269 */           item = StoreData.THOI_TRANG.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 34:
/* 4273 */           item = StoreData.GIA_TOC.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 16:
/* 4277 */           item = StoreData.DAY_CHUYEN.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 17:
/* 4281 */           item = StoreData.NHAN.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 18:
/* 4285 */           item = StoreData.NGOC_BOI.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 19:
/* 4289 */           item = StoreData.BUA.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 20:
/* 4293 */           item = StoreData.NON_NAM.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 21:
/* 4297 */           item = StoreData.NON_NU.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 22:
/* 4301 */           item = StoreData.NON_NAM.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 23:
/* 4305 */           item = StoreData.NON_NU.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 24:
/* 4309 */           item = StoreData.GANG_NAM.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 25:
/* 4313 */           item = StoreData.GANG_NU.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 26:
/* 4317 */           item = StoreData.QUAN_NAM.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 27:
/* 4321 */           item = StoreData.QUAN_NU.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 28:
/* 4325 */           item = StoreData.GIAY_NAM.get(indexUI);
/*      */           break;
/*      */         
/*      */         case 29:
/* 4329 */           item = StoreData.GIAY_NU.get(indexUI);
/*      */           break;
/*      */       } 
/* 4332 */       this.user.service.itemStoreInfo(item, typeUI, indexUI);
/* 4333 */     } else if (typeUI == 3) {
/* 4334 */       if (this.bag[indexUI] != null) {
/* 4335 */         this.user.service.itemInfo(this.bag[indexUI], typeUI, indexUI);
/*      */       }
/* 4337 */     } else if (typeUI == 4) {
/* 4338 */       if (this.box[indexUI] != null) {
/* 4339 */         this.user.service.itemInfo(this.box[indexUI], typeUI, indexUI);
/*      */       }
/* 4341 */     } else if (typeUI == 5) {
/* 4342 */       if (this.equiped[indexUI] != null) {
/* 4343 */         this.user.service.equipedInfo(this.equiped[indexUI], typeUI, indexUI);
/*      */       }
/* 4345 */     } else if (typeUI == 30 && 
/* 4346 */       this.trade != null) {
/* 4347 */       this.trade.viewItemInfo(this, typeUI, indexUI);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public int getSlotNull() {
/* 4353 */     int number = 0;
/* 4354 */     for (int i = 0; i < this.numberCellBag; i++) {
/* 4355 */       if (this.bag[i] == null) {
/* 4356 */         number++;
/*      */       }
/*      */     } 
/* 4359 */     return number;
/*      */   }
/*      */   
/*      */   public void sellItem(Message mss) throws IOException {
/* 4363 */     if (!isVillage() && !isSchool()) {
/* 4364 */       startOKDlg("Vui lòng về trường hoặc làng để bán vật phẩm.");
/*      */       return;
/*      */     } 
/* 4367 */     byte indexUI = mss.reader().readByte();
/* 4368 */     int quantity = 1;
/* 4369 */     if (mss.reader().available() > 0) {
/* 4370 */       quantity = mss.reader().readShort();
/*      */     }
/* 4372 */     if (this.bag[indexUI] != null && (this.bag[indexUI]).upgrade == 0) {
/* 4373 */       addYen(quantity * (this.bag[indexUI]).yen);
/* 4374 */       removeItem(indexUI, quantity, true);
/* 4375 */       startOKDlg("Bán vật phẩm thành công!");
/*      */     } else {
/* 4377 */       startOKDlg("Có lỗi xảy ra!");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void buyItem(Message mss) throws IOException {
/* 4382 */     if (!isVillage() && !isSchool()) {
/* 4383 */       startOKDlg("Vui lòng về trường hoặc làng để mua vật phẩm.");
/*      */       return;
/*      */     } 
/* 4386 */     byte typeUI = mss.reader().readByte();
/* 4387 */     byte indexUI = mss.reader().readByte();
/* 4388 */     int quantity = 1;
/* 4389 */     if (mss.reader().available() > 0) {
/* 4390 */       quantity = mss.reader().readShort();
/*      */     }
/* 4392 */     if (quantity < 1) {
/* 4393 */       startOKDlg("Số lượng không hợp lệ!");
/*      */       
/*      */       return;
/*      */     } 
/* 4397 */     ArrayList<ItemStore> store = null;
/* 4398 */     switch (typeUI) {
/*      */       case 2:
/* 4400 */         store = StoreData.VU_KHI;
/*      */         break;
/*      */       
/*      */       case 6:
/* 4404 */         store = StoreData.DUOC_PHAM;
/*      */         break;
/*      */       
/*      */       case 7:
/* 4408 */         store = StoreData.DUOC_PHAM_KHOA;
/*      */         break;
/*      */       
/*      */       case 8:
/* 4412 */         store = StoreData.THUC_AN;
/*      */         break;
/*      */       
/*      */       case 9:
/* 4416 */         store = StoreData.THUC_AN_KHOA;
/*      */         break;
/*      */       
/*      */       case 14:
/* 4420 */         store = StoreData.LINH_TINH;
/*      */         break;
/*      */       
/*      */       case 15:
/* 4424 */         store = StoreData.SACH;
/*      */         break;
/*      */       
/*      */       case 32:
/* 4428 */         store = StoreData.THOI_TRANG;
/*      */         break;
/*      */       
/*      */       case 34:
/* 4432 */         store = StoreData.GIA_TOC;
/*      */         break;
/*      */       
/*      */       case 16:
/* 4436 */         store = StoreData.DAY_CHUYEN;
/*      */         break;
/*      */       case 17:
/* 4439 */         store = StoreData.NHAN;
/*      */         break;
/*      */       
/*      */       case 18:
/* 4443 */         store = StoreData.NGOC_BOI;
/*      */         break;
/*      */       
/*      */       case 19:
/* 4447 */         store = StoreData.BUA;
/*      */         break;
/*      */       
/*      */       case 20:
/* 4451 */         store = StoreData.NON_NAM;
/*      */         break;
/*      */       
/*      */       case 21:
/* 4455 */         store = StoreData.NON_NU;
/*      */         break;
/*      */       
/*      */       case 22:
/* 4459 */         store = StoreData.AO_NAM;
/*      */         break;
/*      */       
/*      */       case 23:
/* 4463 */         store = StoreData.AO_NU;
/*      */         break;
/*      */       
/*      */       case 24:
/* 4467 */         store = StoreData.GANG_NAM;
/*      */         break;
/*      */       
/*      */       case 25:
/* 4471 */         store = StoreData.GANG_NU;
/*      */         break;
/*      */       
/*      */       case 26:
/* 4475 */         store = StoreData.QUAN_NAM;
/*      */         break;
/*      */       
/*      */       case 27:
/* 4479 */         store = StoreData.QUAN_NU;
/*      */         break;
/*      */       
/*      */       case 28:
/* 4483 */         store = StoreData.GIAY_NAM;
/*      */         break;
/*      */       
/*      */       case 29:
/* 4487 */         store = StoreData.GIAY_NU;
/*      */         break;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4495 */     if (store != null) {
/* 4496 */       ItemStore item = store.get(indexUI);
/* 4497 */       int slotNull = getSlotNull();
/* 4498 */       if ((item.entry.isUpToUp && slotNull == 0) || (!item.entry.isUpToUp && slotNull < quantity)) {
/* 4499 */         startOKDlg("Hành trang đã đầy!");
/*      */         return;
/*      */       } 
/* 4502 */       if (item == null) {
/* 4503 */         startOKDlg("Có lỗi xảy ra!");
/*      */         return;
/*      */       } 
/* 4506 */       int giaXu = item.xu * quantity;
/* 4507 */       int giaYen = item.yen * quantity;
/* 4508 */       int giaLuong = item.luong * quantity;
/* 4509 */       if (giaXu > this.xu || giaLuong > this.user.luong || giaYen > this.yen) {
/* 4510 */         startOKDlg("Không đủ tiền!");
/*      */         return;
/*      */       } 
/* 4513 */       updateXu(this.xu - giaXu);
/* 4514 */       updateLuong(this.user.luong - giaLuong);
/* 4515 */       updateYen(this.yen - giaYen);
/* 4516 */       if (item.entry.isUpToUp) {
/* 4517 */         Item add = new Item(item.templateId);
/* 4518 */         if (item.expire != -1L) {
/* 4519 */           add.expire = (new Date()).getTime() + item.expire;
/*      */         } else {
/* 4521 */           add.expire = -1L;
/*      */         } 
/* 4523 */         add.isLock = item.isLock;
/* 4524 */         add.quantity = quantity;
/* 4525 */         add.upgrade = 0;
/* 4526 */         if (item.entry.isTypeBody() || item.entry.isTypeNgocKham() || item.entry.isTypeMount()) {
/* 4527 */           add.quantity = 1;
/* 4528 */           int num = item.option_max.length;
/* 4529 */           add.options = new ArrayList<>();
/* 4530 */           for (int a = 0; a < num; a++) {
/* 4531 */             int templateId = item.option_max[a][0];
/* 4532 */             int param = NinjaUtil.nextInt(item.option_min[a][1], item.option_max[a][1]);
/* 4533 */             add.options.add(new ItemOption(templateId, param));
/*      */           } 
/*      */         } 
/* 4536 */         addItemToBag(add);
/*      */       } else {
/* 4538 */         for (int i = 0; i < quantity; i++) {
/* 4539 */           Item add = new Item(item.templateId);
/* 4540 */           if (item.expire != -1L) {
/* 4541 */             add.expire = (new Date()).getTime() + item.expire;
/*      */           } else {
/* 4543 */             add.expire = -1L;
/*      */           } 
/* 4545 */           add.isLock = item.isLock;
/* 4546 */           add.quantity = 1;
/* 4547 */           add.upgrade = 0;
/* 4548 */           add.sys = item.sys;
/* 4549 */           add.yen = item.entry.level / 10 * 100;
/* 4550 */           if (item.entry.isTypeClothe()) {
/* 4551 */             add.yen *= 3;
/* 4552 */           } else if (item.entry.isTypeAdorn()) {
/* 4553 */             add.yen *= 4;
/* 4554 */           } else if (item.entry.isTypeWeapon()) {
/* 4555 */             add.yen *= 5;
/* 4556 */           } else if (item.entry.isTypeMount() || item.entry.isTypeNgocKham()) {
/* 4557 */             add.yen = 5;
/*      */           } else {
/* 4559 */             add.yen = 0;
/*      */           } 
/* 4561 */           if (item.entry.isTypeBody() || item.entry.isTypeNgocKham() || item.entry.isTypeMount()) {
/* 4562 */             int num = item.option_max.length;
/* 4563 */             add.options = new ArrayList<>();
/* 4564 */             for (int a = 0; a < num; a++) {
/* 4565 */               int templateId = item.option_max[a][0];
/* 4566 */               int param = NinjaUtil.nextInt(item.option_min[a][1], item.option_max[a][1]);
/* 4567 */               add.options.add(new ItemOption(templateId, param));
/*      */             } 
/*      */           } 
/* 4570 */           addItemToBag(add);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void viewInfo(Message mss) throws IOException {
/* 4577 */     String name = mss.reader().readUTF();
/* 4578 */     Character _char = getCharacterByName(name);
/* 4579 */     if (_char != null) {
/* 4580 */       this.user.service.viewInfo(_char);
/*      */     } else {
/* 4582 */       this.user.service.addInfoMe("Người này hiện tại không online.");
/*      */     } 
/*      */   }
/*      */   
/*      */   public int getSys() {
/* 4587 */     if (this.classId == 1 || this.classId == 2) {
/* 4588 */       return 1;
/*      */     }
/* 4590 */     if (this.classId == 3 || this.classId == 4) {
/* 4591 */       return 2;
/*      */     }
/* 4593 */     if (this.classId == 5 || this.classId == 6) {
/* 4594 */       return 3;
/*      */     }
/* 4596 */     return 0;
/*      */   }
/*      */   
/*      */   private boolean isVillage() {
/* 4600 */     short[] map = { 10, 17, 22, 32, 38, 43, 48 };
/* 4601 */     for (short m : map) {
/* 4602 */       if (this.mapId == m) {
/* 4603 */         return true;
/*      */       }
/*      */     } 
/* 4606 */     return false;
/*      */   }
/*      */   
/*      */   private boolean isSchool() {
/* 4610 */     short[] map = { 1, 27, 72 };
/* 4611 */     for (short m : map) {
/* 4612 */       if (this.mapId == m) {
/* 4613 */         return true;
/*      */       }
/*      */     } 
/* 4616 */     return false;
/*      */   }
/*      */   
/*      */   public void addCuuSat(Message ms) throws IOException {
/* 4620 */     if (isVillage() || isSchool()) {
/* 4621 */       startOKDlg("Không được cừu sát ở trong trường hoặc làng.");
/*      */       return;
/*      */     } 
/* 4624 */     int charId = ms.reader().readInt();
/*      */   }
/*      */   
/*      */   public void addXu(int xu) throws IOException {
/* 4628 */     this.xu += xu;
/* 4629 */     this.user.service.addXu(xu);
/*      */   }
/*      */   
/*      */   public void addYen(int yen) throws IOException {
/* 4633 */     this.yen += yen;
/* 4634 */     this.user.service.addYen(yen);
/*      */   }
/*      */   
/*      */   public void addExp(long exp) throws IOException {
/* 4638 */     if (this.expDown > 0L) {
/* 4639 */       this.expDown -= exp;
/* 4640 */       Message ms = new Message(71);
/* 4641 */       DataOutputStream ds = ms.writer();
/* 4642 */       ds.writeLong(exp);
/* 4643 */       ds.flush();
/* 4644 */       sendMessage(ms);
/* 4645 */       ms.cleanup();
/*      */     } else {
/* 4647 */       this.exp += exp;
/* 4648 */       int preLevel = this.level;
/* 4649 */       this.level = NinjaUtil.getLevel(this.exp);
/* 4650 */       int nextLevel = this.level;
/* 4651 */       int num = nextLevel - preLevel;
/* 4652 */       if (num > 0) {
/* 4653 */         if (this.classId == 0) {
/* 4654 */           this.potential[0] = (short)(this.potential[0] + 5 * num);
/* 4655 */           this.potential[1] = (short)(this.potential[1] + 2 * num);
/* 4656 */           this.potential[2] = (short)(this.potential[2] + 2 * num);
/* 4657 */           this.potential[3] = (short)(this.potential[3] + 2 * num);
/*      */         } else {
/* 4659 */           for (int i = preLevel; i < nextLevel; i++) {
/* 4660 */             if (i >= 10 && i <= 69) {
/* 4661 */               this.point = (short)(this.point + 10);
/* 4662 */             } else if (i >= 70 && i <= 79) {
/* 4663 */               this.point = (short)(this.point + 20);
/* 4664 */             } else if (i >= 80 && i <= 89) {
/* 4665 */               this.point = (short)(this.point + 30);
/* 4666 */             } else if (i >= 90 && i <= 99) {
/* 4667 */               this.point = (short)(this.point + 40);
/*      */             } else {
/* 4669 */               this.point = (short)(this.point + 50);
/*      */             } 
/*      */           } 
/* 4672 */           this.spoint = (short)(this.spoint + num);
/*      */         } 
/*      */       }
/* 4675 */       setAbility();
/* 4676 */       Message ms = new Message(5);
/* 4677 */       DataOutputStream ds = ms.writer();
/* 4678 */       ds.writeLong(exp);
/* 4679 */       ds.flush();
/* 4680 */       sendMessage(ms);
/* 4681 */       ms.cleanup();
/* 4682 */       if (preLevel != nextLevel) {
/* 4683 */         this.user.service.levelUp();
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public void removeFriend(Message ms) throws IOException {
/* 4689 */     String name = ms.reader().readUTF();
/* 4690 */     if (this.friends.get(name) != null) {
/* 4691 */       this.friends.remove(name);
/* 4692 */       this.user.service.removeFriend(name);
/*      */     } else {
/* 4694 */       this.user.service.addInfoMe("Người này không tồn tại.");
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void addFriend(Message ms) throws IOException {
/* 4700 */     String name = ms.reader().readUTF();
/* 4701 */     Character _char = getCharacterByName(name);
/* 4702 */     if (_char == null) {
/* 4703 */       this.user.service.addInfoMe("Người này không online hoặc không tồn tại!");
/*      */       return;
/*      */     } 
/* 4706 */     Friend friend = this.friends.get(name);
/* 4707 */     if (friend != null) {
/* 4708 */       this.user.service.addInfoMe(name + " đã có trong danh sách bạn bè.");
/*      */       return;
/*      */     } 
/* 4711 */     Friend me = _char.friends.get(this.name);
/* 4712 */     if (me != null) {
/* 4713 */       me.type = 1;
/* 4714 */       this.friends.put(_char.name, new Friend(_char.name, (byte)1));
/* 4715 */       this.user.service.addInfoMe("Bạn và " + name + " đã trở thành hảo hữu.");
/* 4716 */       _char.user.service.addInfoMe("Bạn và " + this.name + " đã trở thành hảo hữu.");
/*      */       return;
/*      */     } 
/* 4719 */     this.friends.put(_char.name, new Friend(_char.name, (byte)0));
/*      */     
/* 4721 */     Message m = new Message(59);
/* 4722 */     DataOutputStream ds = m.writer();
/* 4723 */     ds.writeUTF(this.name);
/* 4724 */     ds.flush();
/* 4725 */     _char.sendMessage(m);
/* 4726 */     this.user.service.addInfoMe("Đã thêm " + name + " vào danh sách bạn bè.");
/*      */   }
/*      */   
/*      */   public void updateXu(int xu) throws IOException {
/* 4730 */     this.logger.log("update xu: " + xu);
/* 4731 */     this.xu = xu;
/* 4732 */     this.user.service.update();
/*      */   }
/*      */   
/*      */   public void updateLuong(int luong) throws IOException {
/* 4736 */     this.logger.log("update luong: " + luong);
/* 4737 */     this.user.luong = luong;
/* 4738 */     this.user.service.update();
/*      */   }
/*      */   
/*      */   public void updateYen(int yen) throws IOException {
/* 4742 */     this.logger.log("update yen: " + yen);
/* 4743 */     this.yen = yen;
/* 4744 */     this.user.service.update();
/*      */   }
/*      */   
/*      */   public int getXu() {
/* 4748 */     return this.xu;
/*      */   }
/*      */   
/*      */   public int getYen() {
/* 4752 */     return this.yen;
/*      */   }
/*      */   
/*      */   public void npcChat(short npcId, String text) throws IOException {
/* 4756 */     Message ms = new Message(38);
/* 4757 */     ms.writer().writeShort(npcId);
/* 4758 */     ms.writer().writeUTF(text);
/* 4759 */     ms.writer().flush();
/* 4760 */     sendMessage(ms);
/* 4761 */     ms.cleanup();
/*      */   }
/*      */   
/*      */   public void saveRms(Message mss) throws IOException {
/* 4765 */     String key = mss.reader().readUTF();
/* 4766 */     int len = mss.reader().readInt();
/* 4767 */     byte[] ab = new byte[len];
/* 4768 */     if (ab == null) {
/*      */       return;
/*      */     }
/* 4771 */     mss.reader().read(ab);
/* 4772 */     byte type = mss.reader().readByte();
/* 4773 */     switch (key) {
/*      */       case "KSkill":
/* 4775 */         if (ab.length == 3) {
/* 4776 */           this.onKSkill = ab;
/*      */         }
/*      */         break;
/*      */       
/*      */       case "OSkill":
/* 4781 */         if (ab.length == 5) {
/* 4782 */           this.onOSkill = ab;
/*      */         }
/*      */         break;
/*      */       
/*      */       case "CSkill":
/* 4787 */         this.onCSkill = ab;
/*      */         break;
/*      */     } 
/*      */   }
/*      */   
/*      */   public void loadSkillShortcut(Message mss) throws IOException {
/* 4793 */     String key = mss.reader().readUTF();
/* 4794 */     byte[] data = new byte[0];
/* 4795 */     switch (key) {
/*      */       case "KSkill":
/* 4797 */         data = this.onKSkill;
/*      */         break;
/*      */       
/*      */       case "OSkill":
/* 4801 */         data = this.onOSkill;
/*      */         break;
/*      */       
/*      */       case "CSkill":
/* 4805 */         data = this.onCSkill;
/*      */         break;
/*      */     } 
/* 4808 */     this.user.service.sendSkillShortcut(key, data);
/*      */   }
/*      */   
/*      */   public synchronized void flushCache() {
/*      */     try {
/* 4813 */       MapManager.outZone(this);
/* 4814 */       characters_name.remove(this.name);
/* 4815 */       characters_id.remove(Integer.valueOf(this.id));
/* 4816 */       if (this.trade != null) {
/* 4817 */         this.trade.closeMenu();
/* 4818 */         this.trade = null;
/*      */       } 
/* 4820 */       JSONArray jArr = new JSONArray();
/* 4821 */       if (this.hp > 0) {
/* 4822 */         jArr.add(Short.valueOf(this.mapId));
/* 4823 */         jArr.add(Short.valueOf(this.x));
/* 4824 */         jArr.add(Short.valueOf(this.y));
/*      */       } else {
/* 4826 */         short[] xy = NinjaUtil.getXY(this.saveCoordinate);
/* 4827 */         jArr.add(Short.valueOf(this.saveCoordinate));
/* 4828 */         jArr.add(Short.valueOf(xy[0]));
/* 4829 */         jArr.add(Short.valueOf(xy[1]));
/*      */       } 
/* 4831 */       JSONArray items = new JSONArray();
/* 4832 */       for (int i = 0; i < this.numberCellBag; i++) {
/* 4833 */         if (this.bag[i] != null) {
/* 4834 */           JSONObject item = new JSONObject();
/* 4835 */           item.put("index", Integer.valueOf((this.bag[i]).index));
/* 4836 */           item.put("id", Integer.valueOf((this.bag[i]).id));
/* 4837 */           item.put("expire", Long.valueOf((this.bag[i]).expire));
/* 4838 */           item.put("sys", Byte.valueOf((this.bag[i]).sys));
/* 4839 */           item.put("isLock", Boolean.valueOf((this.bag[i]).isLock));
/* 4840 */           item.put("yen", Integer.valueOf((this.bag[i]).yen));
/* 4841 */           if ((this.bag[i]).entry.isTypeBody() || (this.bag[i]).entry.isTypeMount() || (this.bag[i]).entry.isTypeNgocKham()) {
/* 4842 */             item.put("upgrade", Byte.valueOf((this.bag[i]).upgrade));
/* 4843 */             JSONArray abilitys = new JSONArray();
/* 4844 */             if ((this.bag[i]).options != null) {
/* 4845 */               for (ItemOption option : (this.bag[i]).options) {
/* 4846 */                 JSONArray ability = new JSONArray();
/* 4847 */                 ability.add(Integer.valueOf(option.optionTemplate.id));
/* 4848 */                 ability.add(Integer.valueOf(option.param));
/* 4849 */                 abilitys.add(ability);
/*      */               } 
/*      */             }
/* 4852 */             item.put("options", abilitys);
/*      */           } 
/* 4854 */           if ((this.bag[i]).entry.isUpToUp) {
/* 4855 */             item.put("quantity", Integer.valueOf((this.bag[i]).quantity));
/*      */           }
/* 4857 */           items.add(item);
/*      */         } 
/*      */       } 
/* 4860 */       JSONArray boxs = new JSONArray();
/* 4861 */       for (int j = 0; j < this.numberCellBox; j++) {
/* 4862 */         if (this.box[j] != null) {
/* 4863 */           JSONObject box = new JSONObject();
/* 4864 */           box.put("index", Integer.valueOf((this.box[j]).index));
/* 4865 */           box.put("id", Integer.valueOf((this.box[j]).id));
/* 4866 */           box.put("expire", Long.valueOf((this.box[j]).expire));
/* 4867 */           box.put("sys", Byte.valueOf((this.box[j]).sys));
/* 4868 */           box.put("isLock", Boolean.valueOf((this.box[j]).isLock));
/* 4869 */           box.put("yen", Integer.valueOf((this.box[j]).yen));
/* 4870 */           if ((this.box[j]).entry.isTypeBody() || (this.box[j]).entry.isTypeMount() || (this.box[j]).entry.isTypeNgocKham()) {
/* 4871 */             box.put("upgrade", Byte.valueOf((this.box[j]).upgrade));
/* 4872 */             JSONArray options = new JSONArray();
/* 4873 */             for (ItemOption option : (this.box[j]).options) {
/* 4874 */               JSONArray ab = new JSONArray();
/* 4875 */               ab.add(Integer.valueOf(option.optionTemplate.id));
/* 4876 */               ab.add(Integer.valueOf(option.param));
/* 4877 */               options.add(ab);
/*      */             } 
/* 4879 */             box.put("options", options);
/*      */           } 
/* 4881 */           if ((this.box[j]).entry.isUpToUp) {
/* 4882 */             box.put("quantity", Integer.valueOf((this.box[j]).quantity));
/*      */           }
/* 4884 */           boxs.add(box);
/*      */         } 
/*      */       } 
/*      */       
/* 4888 */       JSONArray equiped = new JSONArray();
/* 4889 */       for (int k = 0; k < 16; k++) {
/* 4890 */         if (this.equiped[k] != null) {
/* 4891 */           JSONObject equip = new JSONObject();
/* 4892 */           equip.put("id", Integer.valueOf((this.equiped[k]).id));
/* 4893 */           equip.put("expire", Long.valueOf((this.equiped[k]).expire));
/* 4894 */           equip.put("sys", Byte.valueOf((this.equiped[k]).sys));
/* 4895 */           equip.put("yen", Integer.valueOf((this.equiped[k]).yen));
/* 4896 */           equip.put("upgrade", Byte.valueOf((this.equiped[k]).upgrade));
/* 4897 */           JSONArray options = new JSONArray();
/* 4898 */           for (ItemOption option : (this.equiped[k]).options) {
/* 4899 */             JSONArray ability = new JSONArray();
/* 4900 */             ability.add(Integer.valueOf(option.optionTemplate.id));
/* 4901 */             ability.add(Integer.valueOf(option.param));
/* 4902 */             options.add(ability);
/*      */           } 
/* 4904 */           equip.put("options", options);
/* 4905 */           equiped.add(equip);
/*      */         } 
/*      */       } 
/* 4908 */       JSONArray mounts = new JSONArray();
/* 4909 */       for (int m = 0; m < 5; m++) {
/* 4910 */         if (this.mount[m] != null) {
/* 4911 */           JSONObject mount = new JSONObject();
/* 4912 */           mount.put("id", Integer.valueOf((this.mount[m]).id));
/* 4913 */           mount.put("expire", Long.valueOf((this.mount[m]).expire));
/* 4914 */           mount.put("sys", Byte.valueOf((this.mount[m]).sys));
/* 4915 */           mount.put("yen", Integer.valueOf((this.mount[m]).yen));
/* 4916 */           mount.put("level", Byte.valueOf((this.mount[m]).level));
/* 4917 */           JSONArray options = new JSONArray();
/* 4918 */           for (ItemOption option : (this.mount[m]).options) {
/* 4919 */             JSONArray ability = new JSONArray();
/* 4920 */             ability.add(Integer.valueOf(option.optionTemplate.id));
/* 4921 */             ability.add(Integer.valueOf(option.param));
/* 4922 */             options.add(ability);
/*      */           } 
/* 4924 */           mount.put("options", options);
/* 4925 */           mounts.add(mount);
/*      */         } 
/*      */       } 
/* 4928 */       JSONArray skill = new JSONArray();
/* 4929 */       if (this.listSkill != null && this.listSkill.size() > 0) {
/* 4930 */         for (MySkill s : this.listSkill) {
/* 4931 */           JSONObject obj = new JSONObject();
/* 4932 */           obj.put("id", Integer.valueOf(s.id));
/* 4933 */           obj.put("point", Integer.valueOf(s.point));
/* 4934 */           skill.add(obj);
/*      */         } 
/*      */       }
/* 4937 */       JSONObject data = new JSONObject();
/* 4938 */       data.put("exp", Long.valueOf(this.exp));
/* 4939 */       data.put("expDown", Long.valueOf(this.expDown));
/* 4940 */       data.put("countPB", Byte.valueOf(this.countPB));
/* 4941 */       data.put("hieuChien", Byte.valueOf(this.hieuChien));
/* 4942 */       data.put("countFinishDay", Byte.valueOf(this.countFinishDay));
/* 4943 */       data.put("countLoosBoss", Byte.valueOf(this.countLoosBoss));
/* 4944 */       data.put("limitKyNangSo", Byte.valueOf(this.limitKyNangSo));
/* 4945 */       data.put("limitTiemNangSo", Byte.valueOf(this.limitTiemNangSo));
/* 4946 */       data.put("tayTiemNang", Short.valueOf(this.tayTiemNang));
/* 4947 */       data.put("tayKyNang", Short.valueOf(this.tayKyNang));
/* 4948 */       data.put("numberUseExpanedBag", Byte.valueOf(this.numberUseExpanedBag));
/* 4949 */       String potential = Arrays.toString(this.potential).replace(" ", "");
/* 4950 */       if (this.onOSkill == null) {
/* 4951 */         this.onOSkill = new byte[] { -1, -1, -1, -1, -1 };
/*      */       }
/* 4953 */       if (this.onKSkill == null) {
/* 4954 */         this.onKSkill = new byte[] { -1, -1, -1 };
/*      */       }
/* 4956 */       if (this.onCSkill == null) {
/* 4957 */         this.onCSkill = new byte[0];
/*      */       }
/* 4959 */       String onOSkill = Arrays.toString(this.onOSkill).replace(" ", "");
/* 4960 */       String onCSkill = Arrays.toString(this.onCSkill).replace(" ", "");
/* 4961 */       String onKSkill = Arrays.toString(this.onKSkill).replace(" ", "");
/*      */       
/* 4963 */       PreparedStatement stmt = Connect.conn.prepareStatement("UPDATE `player` SET `xu` = ?, `xuInBox` = ?, `yen` = ?, `point` = ?, `spoint` = ?, `saveCoordinate` = ?, `numberCellBag` = ?, `numberCellBox` = ? WHERE `id` = ?");
/* 4964 */       stmt.setInt(1, this.xu);
/* 4965 */       stmt.setInt(2, this.xuInBox);
/* 4966 */       stmt.setInt(3, this.yen);
/* 4967 */       stmt.setInt(4, this.point);
/* 4968 */       stmt.setInt(5, this.spoint);
/* 4969 */       stmt.setInt(6, this.saveCoordinate);
/* 4970 */       stmt.setInt(7, this.numberCellBag);
/* 4971 */       stmt.setInt(8, this.numberCellBox);
/* 4972 */       stmt.setInt(9, this.id);
/* 4973 */       stmt.execute();
/* 4974 */       stmt = Connect.conn.prepareStatement("UPDATE `player` SET `onCSkill` = ?, `onKSkill` = ?, `onOSKill` = ?, `data` = ?, `skill` = ?, `potential` = ?, `map` = ?, `equiped` = ?, `bag` = ?, `box` = ?, `mount` = ? WHERE `id` = ?");
/* 4975 */       stmt.setString(1, onCSkill);
/* 4976 */       stmt.setString(2, onKSkill);
/* 4977 */       stmt.setString(3, onOSkill);
/* 4978 */       stmt.setString(4, data.toJSONString());
/* 4979 */       stmt.setString(5, skill.toJSONString());
/* 4980 */       stmt.setString(6, potential);
/* 4981 */       stmt.setString(7, jArr.toJSONString());
/* 4982 */       stmt.setString(8, equiped.toJSONString());
/* 4983 */       stmt.setString(9, items.toJSONString());
/* 4984 */       stmt.setString(10, boxs.toJSONString());
/* 4985 */       stmt.setString(11, mounts.toJSONString());
/* 4986 */       stmt.setInt(12, this.id);
/* 4987 */       stmt.execute();
/* 4988 */       JSONArray effects = new JSONArray();
/* 4989 */       if (this.effects != null && this.effects.size() > 0) {
/* 4990 */         for (Effect eff : this.effects.values()) {
/* 4991 */           JSONObject job = new JSONObject();
/* 4992 */           job.put("id", Byte.valueOf(eff.template.id));
/* 4993 */           job.put("timeStart", Integer.valueOf(eff.timeStart));
/* 4994 */           job.put("timeLength", Integer.valueOf(eff.timeLength));
/* 4995 */           job.put("param", Short.valueOf(eff.param));
/* 4996 */           effects.add(job);
/*      */         } 
/*      */       }
/* 4999 */       JSONArray friends = new JSONArray();
/* 5000 */       if (this.friends != null && this.friends.size() > 0) {
/* 5001 */         for (Friend friend : this.friends.values()) {
/* 5002 */           JSONObject job = new JSONObject();
/* 5003 */           job.put("name", friend.name);
/* 5004 */           job.put("type", Byte.valueOf(friend.type));
/* 5005 */           friends.add(job);
/*      */         } 
/*      */       }
/* 5008 */       stmt = Connect.conn.prepareStatement("UPDATE `player` SET `class` = ?, `effect` = ?, `friend` = ? WHERE `id` = ?");
/* 5009 */       stmt.setByte(1, this.classId);
/* 5010 */       stmt.setString(2, effects.toJSONString());
/* 5011 */       stmt.setString(3, friends.toJSONString());
/* 5012 */       stmt.setInt(4, this.id);
/* 5013 */       stmt.execute();
/* 5014 */       this.bag = null;
/* 5015 */       this.box = null;
/* 5016 */       this.equiped = null;
/* 5017 */     } catch (Exception ex) {
/* 5018 */       this.logger.debug("flushCache", ex.getMessage());
/*      */     } 
/*      */   }
/*      */   
/*      */   public synchronized void sendToMap(Message ms) throws IOException {
/* 5023 */     Character[] characters = this.zone.getCharacters();
/* 5024 */     for (Character pl : characters) {
/* 5025 */       if (pl != null) {
/* 5026 */         pl.sendMessage(ms);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object obj) {
/* 5033 */     if (obj != null) {
/* 5034 */       Character _char = (Character)obj;
/* 5035 */       if (_char != null && _char.id == this.id) {
/* 5036 */         return true;
/*      */       }
/*      */     } 
/* 5039 */     return false;
/*      */   }
/*      */   
/*      */   public void startOKDlg(String text) throws IOException {
/* 5043 */     this.user.service.startOKDlg(text);
/*      */   }
/*      */   
/*      */   public void sendMessage(Message ms) {
/* 5047 */     this.user.client.sendMessage(ms);
/*      */   }
/*      */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\server\Character.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */