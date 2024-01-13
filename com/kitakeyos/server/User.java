/*     */ package com.kitakeyos.server;
/*     */ 
/*     */ import com.kitakeyos.data.ItemData;
/*     */ import com.kitakeyos.db.Connect;
/*     */ import com.kitakeyos.io.Message;
/*     */ import com.kitakeyos.io.Session;
/*     */ import com.kitakeyos.object.Effect;
/*     */ import com.kitakeyos.object.Friend;
/*     */ import com.kitakeyos.option.ItemOption;
/*     */ import com.kitakeyos.util.Logger;
/*     */ import com.kitakeyos.util.NinjaUtil;
/*     */ import java.io.IOException;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ import org.json.simple.JSONValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class User
/*     */ {
/*  33 */   private Logger logger = new Logger(getClass());
/*     */   public Session client;
/*     */   public Service service;
/*     */   public HashMap<String, Character> characters;
/*     */   public int id;
/*     */   public String username;
/*     */   private byte lock;
/*     */   public int luong;
/*     */   public Character selectedCharacter;
/*     */   
/*     */   public User(Session client) {
/*  44 */     this.client = client;
/*     */   }
/*     */   
/*     */   public static synchronized User login(Session cl, String username, String password, String random) throws IOException {
/*  48 */     User us = new User(cl);
/*  49 */     us.service = new Service(cl, us);
/*     */     try {
/*  51 */       if (username.equals("-1") && password.equals("12345")) {
/*  52 */         us.service.startOKDlg("Vào vừa thôi!");
/*  53 */         return null;
/*     */       } 
/*  55 */       Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
/*  56 */       Matcher m1 = p.matcher(username);
/*  57 */       Matcher m2 = p.matcher(password);
/*  58 */       if (!m1.find() || !m2.find()) {
/*  59 */         us.service.startOKDlg("Tên đăng nhập không được chứa ký tự đặc biệt!");
/*  60 */         return null;
/*     */       } 
/*  62 */       PreparedStatement stmt = Connect.conn.prepareStatement("SELECT * FROM `user` WHERE `username` = ? AND `password` = ? LIMIT 1");
/*  63 */       stmt.setString(1, username);
/*  64 */       stmt.setString(2, password);
/*  65 */       ResultSet resultSet = stmt.executeQuery();
/*  66 */       if (resultSet.first()) {
/*  67 */         if (us.lock == 1) {
/*  68 */           us.service.startOKDlg("Tài khoản đã bị khoá! Vui lòng liên hệ admin để biết thêm chi tiết");
/*  69 */           return null;
/*     */         } 
/*  71 */         us.id = resultSet.getInt("id");
/*  72 */         User u = Server.getUser(us.id);
/*  73 */         if (u != null) {
/*  74 */           u.service.startOKDlg("Có người đăng nhập vào tài khoản của bạn.");
/*  75 */           Thread.sleep(1000L);
/*  76 */           u.client.closeMessage();
/*  77 */           us.service.startOKDlg("Tài khoản đã có người đăng nhập.");
/*  78 */           return null;
/*     */         } 
/*  80 */         us.username = resultSet.getString("username");
/*  81 */         us.lock = resultSet.getByte("lock");
/*  82 */         us.luong = resultSet.getInt("luong");
/*  83 */         resultSet.close();
/*  84 */         stmt = Connect.conn.prepareStatement("UPDATE `user` SET `online` = ? WHERE `id` = ?");
/*  85 */         stmt.setInt(1, 1);
/*  86 */         stmt.setInt(2, us.id);
/*  87 */         stmt.execute();
/*  88 */         us.initCharacterList();
/*  89 */         return us;
/*     */       } 
/*  91 */       resultSet.close();
/*  92 */       us.service.startOKDlg(Language.getString("LOGIN_FAIL", new Object[0]));
/*  93 */       return null;
/*     */     }
/*  95 */     catch (SQLException e) {
/*  96 */       e.printStackTrace();
/*  97 */     } catch (InterruptedException ex) {
/*  98 */       Logger.getLogger(User.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 100 */     return null;
/*     */   }
/*     */   
/*     */   public void initCharacterList() throws SQLException {
/* 104 */     this.characters = new HashMap<>();
/* 105 */     PreparedStatement stmt = Connect.conn.prepareStatement("SELECT * FROM `player` WHERE `user_id` = ? LIMIT 3;");
/* 106 */     stmt.setInt(1, this.id);
/* 107 */     ResultSet resultSet = stmt.executeQuery();
/* 108 */     while (resultSet.next()) {
/* 109 */       Character _char = new Character();
/* 110 */       _char.id = resultSet.getInt("id");
/* 111 */       _char.name = resultSet.getString("name");
/* 112 */       _char.gender = resultSet.getByte("gender");
/* 113 */       _char.classId = resultSet.getByte("class");
/* 114 */       switch (_char.classId) {
/*     */         case 1:
/* 116 */           _char.school = "Ninja kiếm";
/*     */           break;
/*     */         
/*     */         case 2:
/* 120 */           _char.school = "Ninja tiêu";
/*     */           break;
/*     */         
/*     */         case 3:
/* 124 */           _char.school = "Ninja kunai";
/*     */           break;
/*     */         
/*     */         case 4:
/* 128 */           _char.school = "Ninja cung";
/*     */           break;
/*     */         
/*     */         case 5:
/* 132 */           _char.school = "Ninja đao";
/*     */           break;
/*     */         
/*     */         case 6:
/* 136 */           _char.school = "Ninja quạt";
/*     */           break;
/*     */         
/*     */         default:
/* 140 */           _char.school = "Chưa vào trường";
/*     */           break;
/*     */       } 
/* 143 */       _char.original_head = _char.head = resultSet.getShort("head");
/* 144 */       _char.weapon = -1;
/* 145 */       _char.body = -1;
/* 146 */       _char.leg = -1;
/* 147 */       JSONObject json = (JSONObject)JSONValue.parse(resultSet.getString("data"));
/* 148 */       _char.exp = ((Long)json.get("exp")).longValue();
/* 149 */       long num = _char.exp;
/* 150 */       for (int i = 0; i < Server.exps.length; i++) {
/* 151 */         if (num < Server.exps[i]) {
/* 152 */           _char.level = i;
/* 153 */           _char.expR = num;
/*     */           break;
/*     */         } 
/* 156 */         num -= Server.exps[i];
/*     */       } 
/* 158 */       _char.expDown = ((Long)json.get("expDown")).intValue();
/* 159 */       _char.hieuChien = ((Long)json.get("hieuChien")).byteValue();
/* 160 */       _char.countFinishDay = ((Long)json.get("countFinishDay")).byteValue();
/* 161 */       _char.countLoosBoss = ((Long)json.get("countLoosBoss")).byteValue();
/* 162 */       _char.countPB = ((Long)json.get("countPB")).byteValue();
/* 163 */       _char.limitKyNangSo = ((Long)json.get("limitKyNangSo")).byteValue();
/* 164 */       _char.limitTiemNangSo = ((Long)json.get("limitTiemNangSo")).byteValue();
/* 165 */       if (json.get("tayTiemNang") != null) {
/* 166 */         _char.tayTiemNang = ((Long)json.get("tayTiemNang")).shortValue();
/*     */       } else {
/* 168 */         _char.tayTiemNang = 0;
/*     */       } 
/* 170 */       if (json.get("tayKyNang") != null) {
/* 171 */         _char.tayKyNang = ((Long)json.get("tayKyNang")).shortValue();
/*     */       } else {
/* 173 */         _char.tayKyNang = 0;
/*     */       } 
/* 175 */       if (json.get("numberUseExpanedBag") != null) {
/* 176 */         _char.numberUseExpanedBag = ((Long)json.get("numberUseExpanedBag")).byteValue();
/*     */       } else {
/* 178 */         _char.numberUseExpanedBag = 0;
/*     */       } 
/* 180 */       _char.equiped = new Character.Equiped[16];
/* 181 */       JSONArray jso = (JSONArray)JSONValue.parse(resultSet.getString("equiped"));
/* 182 */       if (jso != null) {
/* 183 */         int size = jso.size();
/* 184 */         for (int j = 0; j < size; j++) {
/* 185 */           JSONObject obj = (JSONObject)jso.get(j);
/* 186 */           int id = ((Long)obj.get("id")).intValue();
/* 187 */           Character.Equiped equiped = new Character.Equiped(id);
/* 188 */           if (equiped.entry.isTypeWeapon()) {
/* 189 */             _char.weapon = equiped.entry.part;
/*     */           }
/* 191 */           if (equiped.entry.type == 2) {
/* 192 */             _char.body = equiped.entry.part;
/*     */           }
/* 194 */           if (equiped.entry.type == 6) {
/* 195 */             _char.leg = equiped.entry.part;
/* 196 */           } else if (equiped.entry.type == 11) {
/* 197 */             _char.head = equiped.entry.part;
/*     */           } 
/* 199 */           equiped.upgrade = ((Long)obj.get("upgrade")).byteValue();
/* 200 */           equiped.sys = ((Long)obj.get("sys")).byteValue();
/* 201 */           equiped.expire = ((Long)obj.get("expire")).longValue();
/* 202 */           equiped.yen = ((Long)obj.get("yen")).intValue();
/* 203 */           JSONArray ability = (JSONArray)obj.get("options");
/* 204 */           int size2 = ability.size();
/* 205 */           equiped.options = new ArrayList<>();
/* 206 */           for (int c = 0; c < size2; c++) {
/* 207 */             JSONArray jAbility = (JSONArray)ability.get(c);
/* 208 */             int templateId = ((Long)jAbility.get(0)).intValue();
/* 209 */             int param = ((Long)jAbility.get(1)).intValue();
/* 210 */             equiped.options.add(new ItemOption(templateId, param));
/*     */           } 
/* 212 */           _char.equiped[equiped.entry.type] = equiped;
/*     */         } 
/*     */       } 
/* 215 */       this.characters.put(_char.name, _char);
/*     */     } 
/* 217 */     resultSet.close();
/*     */   }
/*     */   
/*     */   public synchronized void createCharacter(Message ms) throws IOException {
/*     */     try {
/* 222 */       if (this.characters.size() >= 3) {
/* 223 */         this.service.startOKDlg("Bạn chỉ được tạo tối đa 3 nhân vật.");
/*     */         return;
/*     */       } 
/* 226 */       String name = ms.reader().readUTF();
/* 227 */       Pattern p = Pattern.compile("^[a-z0-9]+$");
/* 228 */       Matcher m1 = p.matcher(name);
/* 229 */       if (!m1.find()) {
/* 230 */         this.service.startOKDlg(Language.getString("CREATE_CHAR_FAIL_4", new Object[0]));
/*     */         return;
/*     */       } 
/* 233 */       byte gender = ms.reader().readByte();
/* 234 */       byte head = ms.reader().readByte();
/* 235 */       byte[] h = null;
/* 236 */       if (gender == 0) {
/* 237 */         h = new byte[] { 11, 26, 27, 28 };
/* 238 */         gender = 0;
/*     */       } else {
/* 240 */         h = new byte[] { 2, 23, 24, 25 };
/* 241 */         gender = 1;
/*     */       } 
/* 243 */       byte temp = h[0];
/* 244 */       for (byte b : h) {
/* 245 */         if (head == b) {
/* 246 */           temp = b;
/*     */           break;
/*     */         } 
/*     */       } 
/* 250 */       head = temp;
/* 251 */       if (name.length() < 6 || name.length() > 20) {
/* 252 */         this.service.startOKDlg(Language.getString("CREATE_CHAR_FAIL_1", new Object[0]));
/*     */         return;
/*     */       } 
/* 255 */       ResultSet check = Connect.stat.executeQuery("SELECT * FROM `player` WHERE `user_id` = '" + this.id + "';");
/* 256 */       if (check.last() && 
/* 257 */         check.getRow() >= 3) {
/* 258 */         this.service.startOKDlg(Language.getString("CREATE_CHAR_FAIL_5", new Object[0]));
/*     */         
/*     */         return;
/*     */       } 
/* 262 */       check.close();
/* 263 */       check = Connect.stat.executeQuery("SELECT * FROM `player` WHERE `name` = '" + name + "';");
/* 264 */       if (check.last() && 
/* 265 */         check.getRow() > 0) {
/* 266 */         this.service.startOKDlg(Language.getString("CREATE_CHAR_FAIL_2", new Object[0]));
/*     */         
/*     */         return;
/*     */       } 
/* 270 */       check.close();
/* 271 */       PreparedStatement stmt = Connect.conn.prepareStatement("INSERT INTO player(`user_id`, `name`, `gender`, `head`, `xu`, `yen`, `equiped`, `bag`, `box`, `mount`, `effect`, `friend`) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
/* 272 */       stmt.setInt(1, this.id);
/* 273 */       stmt.setString(2, name);
/* 274 */       stmt.setByte(3, gender);
/* 275 */       stmt.setShort(4, (short)head);
/* 276 */       stmt.setInt(5, 550000000);
/* 277 */       stmt.setInt(6, 500000000);
/* 278 */       stmt.setString(7, "[]");
/* 279 */       stmt.setString(8, "[]");
/* 280 */       stmt.setString(9, "[]");
/* 281 */       stmt.setString(10, "[]");
/* 282 */       stmt.setString(11, "[]");
/* 283 */       stmt.setString(12, "[]");
/* 284 */       stmt.execute();
/* 285 */       initCharacterList();
/* 286 */       this.service.selectChar();
/* 287 */     } catch (IOException|SQLException e) {
/* 288 */       e.printStackTrace();
/* 289 */       this.service.startOKDlg(Language.getString("CREATE_CHAR_FAIL_3", new Object[0]));
/*     */     } 
/*     */   }
/*     */   
/*     */   public static synchronized void newPlay(String rand, User us) {
/*     */     try {
/* 295 */       ResultSet result = Connect.stat.executeQuery("SELECT * FROM `user` WHERE `username` = '" + rand + "' LIMIT 1;");
/* 296 */       if (!result.first()) {
/* 297 */         Connect.stat.execute("INSERT INTO `user`(`username`, `password`, `online`, `luong`) VALUES ('" + rand + "', 'kitakeyos', 0, 999999);");
/*     */       }
/* 299 */       result.close();
/* 300 */     } catch (SQLException ex) {
/* 301 */       us.logger.debug("newPlay", ex.toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized void charInitSelected() {
/*     */     try {
/* 307 */       PreparedStatement stmt = Connect.conn.prepareStatement("SELECT * FROM `player` WHERE `id` = ? LIMIT 1;");
/* 308 */       stmt.setInt(1, this.selectedCharacter.id);
/* 309 */       ResultSet resultSet = stmt.executeQuery();
/* 310 */       if (resultSet.first()) {
/* 311 */         this.selectedCharacter.xu = resultSet.getInt("xu");
/* 312 */         this.selectedCharacter.xuInBox = resultSet.getInt("xuInBox");
/* 313 */         this.selectedCharacter.yen = resultSet.getInt("yen");
/* 314 */         JSONArray ja = (JSONArray)JSONValue.parse(resultSet.getString("map"));
/* 315 */         this.selectedCharacter.mapId = ((Long)ja.get(0)).shortValue();
/* 316 */         this.selectedCharacter.x = ((Long)ja.get(1)).shortValue();
/* 317 */         this.selectedCharacter.y = ((Long)ja.get(2)).shortValue();
/* 318 */         this.selectedCharacter.saveCoordinate = resultSet.getShort("saveCoordinate");
/* 319 */         this.selectedCharacter.point = resultSet.getShort("point");
/* 320 */         this.selectedCharacter.spoint = resultSet.getShort("spoint");
/* 321 */         JSONArray jArr = (JSONArray)JSONValue.parse(resultSet.getString("potential"));
/* 322 */         int len = jArr.size();
/* 323 */         this.selectedCharacter.potential = new short[4]; int i;
/* 324 */         for (i = 0; i < 4; i++) {
/* 325 */           if (jArr.get(i) != null) {
/* 326 */             this.selectedCharacter.potential[i] = ((Long)jArr.get(i)).shortValue();
/*     */           } else {
/* 328 */             this.selectedCharacter.potential[i] = 5;
/*     */           } 
/*     */         } 
/* 331 */         jArr = (JSONArray)JSONValue.parse(resultSet.getString("skill"));
/* 332 */         len = jArr.size();
/* 333 */         this.selectedCharacter.listSkill = new ArrayList<>();
/* 334 */         for (i = 0; i < len; i++) {
/* 335 */           JSONObject obj = (JSONObject)jArr.get(i);
/* 336 */           Character.MySkill skill = new Character.MySkill();
/* 337 */           skill.id = ((Long)obj.get("id")).intValue();
/* 338 */           skill.point = ((Long)obj.get("point")).intValue();
/* 339 */           this.selectedCharacter.listSkill.add(skill);
/*     */         } 
/* 341 */         this.selectedCharacter.mount = new Character.Mount[5];
/* 342 */         JSONArray jso = (JSONArray)JSONValue.parse(resultSet.getString("mount"));
/* 343 */         if (jso != null) {
/* 344 */           int n = jso.size();
/* 345 */           for (int i1 = 0; i1 < n; i1++) {
/* 346 */             JSONObject obj = (JSONObject)jso.get(i1);
/* 347 */             int id = ((Long)obj.get("id")).intValue();
/* 348 */             Character.Mount mount = new Character.Mount(id);
/* 349 */             mount.entry = ItemData.getItemEntryById(mount.id);
/* 350 */             mount.level = ((Long)obj.get("level")).byteValue();
/* 351 */             mount.sys = ((Long)obj.get("sys")).byteValue();
/* 352 */             mount.expire = ((Long)obj.get("expire")).longValue();
/* 353 */             mount.yen = ((Long)obj.get("yen")).intValue();
/* 354 */             JSONArray ability = (JSONArray)obj.get("options");
/* 355 */             int size2 = ability.size();
/* 356 */             mount.options = new ArrayList<>();
/* 357 */             for (int c = 0; c < size2; c++) {
/* 358 */               JSONArray jAbility = (JSONArray)ability.get(c);
/* 359 */               int templateId = ((Long)jAbility.get(0)).intValue();
/* 360 */               int param = ((Long)jAbility.get(1)).intValue();
/* 361 */               mount.options.add(new ItemOption(templateId, param));
/*     */             } 
/* 363 */             this.selectedCharacter.mount[mount.entry.type - 29] = mount;
/*     */           } 
/*     */         } 
/* 366 */         this.selectedCharacter.clanname = "";
/* 367 */         this.selectedCharacter.numberCellBag = resultSet.getByte("numberCellBag");
/* 368 */         this.selectedCharacter.numberCellBox = resultSet.getByte("numberCellBox");
/* 369 */         this.selectedCharacter.bag = new Character.Item[this.selectedCharacter.numberCellBag];
/* 370 */         jso = (JSONArray)JSONValue.parse(resultSet.getString("bag"));
/* 371 */         if (jso != null) {
/* 372 */           int n = jso.size();
/* 373 */           for (int i1 = 0; i1 < n; i1++) {
/* 374 */             JSONObject obj = (JSONObject)jso.get(i1);
/* 375 */             int id = ((Long)obj.get("id")).intValue();
/* 376 */             Character.Item item = new Character.Item(id);
/* 377 */             item.index = ((Long)obj.get("index")).intValue();
/* 378 */             item.isLock = ((Boolean)obj.get("isLock")).booleanValue();
/* 379 */             item.sys = ((Long)obj.get("sys")).byteValue();
/* 380 */             item.expire = ((Long)obj.get("expire")).longValue();
/* 381 */             if (item.expire == -1L || System.currentTimeMillis() <= item.expire) {
/*     */ 
/*     */               
/* 384 */               item.yen = ((Long)obj.get("yen")).intValue();
/* 385 */               if (item.entry.isTypeBody() || item.entry.isTypeMount() || item.entry.isTypeNgocKham()) {
/* 386 */                 item.upgrade = ((Long)obj.get("upgrade")).byteValue();
/* 387 */                 JSONArray ability = (JSONArray)obj.get("options");
/* 388 */                 int size2 = ability.size();
/* 389 */                 item.options = new ArrayList<>();
/* 390 */                 for (int c = 0; c < size2; c++) {
/* 391 */                   JSONArray jAbility = (JSONArray)ability.get(c);
/* 392 */                   int templateId = ((Long)jAbility.get(0)).intValue();
/* 393 */                   int param = ((Long)jAbility.get(1)).intValue();
/* 394 */                   item.options.add(new ItemOption(templateId, param));
/*     */                 } 
/*     */               } else {
/* 397 */                 item.upgrade = 0;
/*     */               } 
/* 399 */               if (item.entry.isUpToUp) {
/* 400 */                 item.quantity = ((Long)obj.get("quantity")).shortValue();
/*     */               } else {
/* 402 */                 item.quantity = 1;
/*     */               } 
/* 404 */               this.selectedCharacter.bag[item.index] = item;
/*     */             } 
/*     */           } 
/* 407 */         }  this.selectedCharacter.box = new Character.Item[this.selectedCharacter.numberCellBox];
/* 408 */         jso = (JSONArray)JSONValue.parse(resultSet.getString("box"));
/* 409 */         if (jso != null) {
/* 410 */           int n = jso.size();
/* 411 */           for (int i1 = 0; i1 < n; i1++) {
/* 412 */             JSONObject obj = (JSONObject)jso.get(i1);
/* 413 */             int id = ((Long)obj.get("id")).intValue();
/* 414 */             Character.Item item = new Character.Item(id);
/* 415 */             item.index = ((Long)obj.get("index")).intValue();
/* 416 */             item.isLock = ((Boolean)obj.get("isLock")).booleanValue();
/* 417 */             item.sys = ((Long)obj.get("sys")).byteValue();
/* 418 */             item.expire = ((Long)obj.get("expire")).longValue();
/* 419 */             item.yen = ((Long)obj.get("yen")).intValue();
/* 420 */             if (item.entry.isTypeBody() || item.entry.isTypeMount() || item.entry.isTypeNgocKham()) {
/* 421 */               item.upgrade = ((Long)obj.get("upgrade")).byteValue();
/* 422 */               JSONArray ability = (JSONArray)obj.get("options");
/* 423 */               int size2 = ability.size();
/* 424 */               item.options = new ArrayList<>();
/* 425 */               for (int c = 0; c < size2; c++) {
/* 426 */                 JSONArray jAbility = (JSONArray)ability.get(c);
/* 427 */                 int templateId = ((Long)jAbility.get(0)).intValue();
/* 428 */                 int param = ((Long)jAbility.get(1)).intValue();
/* 429 */                 item.options.add(new ItemOption(templateId, param));
/*     */               } 
/*     */             } else {
/* 432 */               item.upgrade = 0;
/*     */             } 
/* 434 */             if (item.entry.isUpToUp) {
/* 435 */               item.quantity = ((Long)obj.get("quantity")).shortValue();
/*     */             } else {
/* 437 */               item.quantity = 1;
/*     */             } 
/* 439 */             this.selectedCharacter.box[item.index] = item;
/*     */           } 
/*     */         } 
/* 442 */         JSONArray j = (JSONArray)JSONValue.parse(resultSet.getString("onOSKill"));
/* 443 */         this.selectedCharacter.onOSkill = new byte[j.size()]; int t;
/* 444 */         for (t = 0; t < this.selectedCharacter.onOSkill.length; t++) {
/* 445 */           this.selectedCharacter.onOSkill[t] = ((Long)j.get(t)).byteValue();
/*     */         }
/* 447 */         j = (JSONArray)JSONValue.parse(resultSet.getString("onCSKill"));
/* 448 */         this.selectedCharacter.onCSkill = new byte[j.size()];
/* 449 */         for (t = 0; t < this.selectedCharacter.onCSkill.length; t++) {
/* 450 */           this.selectedCharacter.onCSkill[t] = ((Long)j.get(t)).byteValue();
/*     */         }
/* 452 */         j = (JSONArray)JSONValue.parse(resultSet.getString("onKSKill"));
/* 453 */         this.selectedCharacter.onKSkill = new byte[j.size()];
/* 454 */         for (t = 0; t < this.selectedCharacter.onKSkill.length; t++) {
/* 455 */           this.selectedCharacter.onKSkill[t] = ((Long)j.get(t)).byteValue();
/*     */         }
/* 457 */         this.selectedCharacter.effects = new HashMap<>();
/* 458 */         JSONArray effects = (JSONArray)JSONValue.parse(resultSet.getString("effect"));
/* 459 */         int size = effects.size();
/* 460 */         for (int k = 0; k < size; k++) {
/* 461 */           JSONObject job = (JSONObject)effects.get(k);
/* 462 */           byte templateId = ((Long)job.get("id")).byteValue();
/* 463 */           int timeStart = ((Long)job.get("timeStart")).intValue();
/* 464 */           int timeLength = ((Long)job.get("timeLength")).intValue();
/* 465 */           short param = ((Long)job.get("param")).shortValue();
/* 466 */           Effect eff = new Effect(templateId, timeStart, timeLength, param);
/* 467 */           this.selectedCharacter.effects.put(Byte.valueOf(eff.template.type), eff);
/*     */         } 
/* 469 */         JSONArray friend = (JSONArray)JSONValue.parse(resultSet.getString("friend"));
/* 470 */         size = friend.size();
/* 471 */         this.selectedCharacter.friends = new HashMap<>();
/* 472 */         for (int m = 0; m < size; m++) {
/* 473 */           JSONObject job = (JSONObject)friend.get(m);
/* 474 */           byte type = ((Long)job.get("type")).byteValue();
/* 475 */           String name = job.get("name").toString();
/* 476 */           this.selectedCharacter.friends.put(name, new Friend(name, type));
/*     */         } 
/* 478 */         this.selectedCharacter.user = this;
/*     */       } 
/* 480 */       resultSet.close();
/* 481 */     } catch (SQLException ex) {
/* 482 */       this.logger.debug("charInitSelected", ex.getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized void selectToChar(Message ms) throws IOException {
/* 487 */     String username = ms.reader().readUTF();
/* 488 */     this.selectedCharacter = this.characters.get(username);
/* 489 */     this.characters.clear();
/* 490 */     if (this.selectedCharacter != null) {
/* 491 */       charInitSelected();
/* 492 */       this.service.setChar(this.selectedCharacter);
/* 493 */       this.selectedCharacter.setAbility();
/* 494 */       this.selectedCharacter.hp = this.selectedCharacter.maxHP;
/* 495 */       this.selectedCharacter.mp = this.selectedCharacter.maxMP;
/* 496 */       for (Effect eff : this.selectedCharacter.effects.values()) {
/* 497 */         this.service.addEffect(eff);
/*     */       }
/* 499 */       Character.characters_name.put(this.selectedCharacter.name, this.selectedCharacter);
/* 500 */       Character.characters_id.put(Integer.valueOf(this.selectedCharacter.id), this.selectedCharacter);
/* 501 */       Map map = MapManager.getMapById(this.selectedCharacter.mapId);
/* 502 */       Collection<Zone> zones = map.getZones();
/* 503 */       byte zoneId = 0;
/* 504 */       for (Zone zone : zones) {
/* 505 */         if (zone.numberCharacter > 15) {
/* 506 */           zoneId = (byte)(zoneId + 1);
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 511 */       int size = zones.size();
/* 512 */       if (zoneId < 0 || zoneId > size) {
/* 513 */         zoneId = (byte)NinjaUtil.nextInt(size);
/*     */       }
/* 515 */       MapManager.joinZone(this.selectedCharacter, this.selectedCharacter.mapId, zoneId);
/* 516 */       this.selectedCharacter.hasJoin = true;
/* 517 */       this.service.sendInfo();
/* 518 */       this.service.sendBox();
/* 519 */       this.service.sendZone();
/* 520 */       this.service.sendItemMap();
/* 521 */       this.service.showAlert("Thông Báo", "- Chúc anh em trải nghiệm game vui vẻ. \n Share File Server Bởi LMK Army2 Private.\n\n- Số người đang hoạt động: " + Character.characters_id.size());
/*     */     } else {
/* 523 */       this.service.startOKDlg("Không thể tìm thấy nhân vật!");
/* 524 */       this.client.closeMessage();
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized void close() {
/*     */     try {
/* 530 */       if (this.client.login) {
/* 531 */         if (this.selectedCharacter != null && this.selectedCharacter.hasJoin) {
/* 532 */           this.selectedCharacter.flushCache();
/*     */         }
/* 534 */         this.selectedCharacter = null;
/* 535 */         PreparedStatement stmt = Connect.conn.prepareStatement("UPDATE `user` SET `luong` = ?, `online` = ? WHERE `id` = ? LIMIT 1;");
/* 536 */         stmt.setInt(1, this.luong);
/* 537 */         stmt.setInt(2, 0);
/* 538 */         stmt.setInt(3, this.id);
/* 539 */         stmt.execute();
/* 540 */         this.service = null;
/* 541 */         this.characters = null;
/*     */       } 
/* 543 */     } catch (SQLException ex) {
/* 544 */       ex.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 550 */     return this.username;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\server\User.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */