/*      */ package com.kitakeyos.server;
/*      */ 
/*      */ import com.kitakeyos.data.ItemData;
/*      */ import com.kitakeyos.data.SkillData;
/*      */ import com.kitakeyos.data.StoreData;
/*      */ import com.kitakeyos.db.Connect;
/*      */ import com.kitakeyos.io.Message;
/*      */ import com.kitakeyos.io.Session;
/*      */ import com.kitakeyos.object.ArrowPaint;
/*      */ import com.kitakeyos.object.Effect;
/*      */ import com.kitakeyos.object.EffectCharPaint;
/*      */ import com.kitakeyos.object.EffectInfoPaint;
/*      */ import com.kitakeyos.object.Frame;
/*      */ import com.kitakeyos.object.ImageInfo;
/*      */ import com.kitakeyos.object.ItemEntry;
/*      */ import com.kitakeyos.object.ItemStore;
/*      */ import com.kitakeyos.object.Part;
/*      */ import com.kitakeyos.object.PartImage;
/*      */ import com.kitakeyos.object.Skill;
/*      */ import com.kitakeyos.object.SkillInfoPaint;
/*      */ import com.kitakeyos.object.SkillPaint;
/*      */ import com.kitakeyos.object.TileMap;
/*      */ import com.kitakeyos.object.Waypoint;
/*      */ import com.kitakeyos.option.SkillOption;
/*      */ import com.kitakeyos.template.EffectTemplate;
/*      */ import com.kitakeyos.template.ItemOptionTemplate;
/*      */ import com.kitakeyos.template.MonsterTemplate;
/*      */ import com.kitakeyos.template.NpcTemplate;
/*      */ import com.kitakeyos.template.SkillOptionTemplate;
/*      */ import com.kitakeyos.template.SkillTemplate;
/*      */ import com.kitakeyos.util.Logger;
/*      */ import com.kitakeyos.util.NinjaUtil;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.net.ServerSocket;
/*      */ import java.net.Socket;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import org.json.simple.JSONArray;
/*      */ import org.json.simple.JSONObject;
/*      */ import org.json.simple.JSONValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Server
/*      */ {
/*      */   public static ArrayList<Session> sessions;
/*      */   public static ServerSocket server;
/*      */   public static boolean start;
/*      */   public static int id;
/*      */   public static ArrayList<TileMap> maps;
/*      */   public static ArrayList<NpcTemplate> npcs;
/*      */   public static ArrayList<MonsterTemplate> mobs;
/*      */   public static ArrayList<SkillPaint> sks;
/*      */   public static ArrayList<Part> parts;
/*      */   public static ArrayList<EffectCharPaint> efs;
/*      */   public static ArrayList<ArrowPaint> arrs;
/*      */   public static ArrayList<int[]> smallImg;
/*      */   public static long[] exps;
/*      */   public static int[] crystals;
/*      */   public static int[] upClothes;
/*      */   public static int[] upAdorns;
/*      */   public static int[] upWeapons;
/*      */   public static int[] coinUpCrystals;
/*      */   public static int[] coinUpClothes;
/*      */   public static int[] coinUpAdorns;
/*      */   public static int[] coinUpWeapons;
/*      */   public static int[] goldUps;
/*      */   public static int[] maxPercents;
/*      */   public static byte[][] tasks;
/*      */   public static byte[][] mapTasks;
/*      */   public static LuckyDraw luckyDrawNormal;
/*      */   public static LuckyDraw luckyDrawVIP;
/*      */   public static JSONObject moto;
/*   83 */   public static Logger logger = new Logger(Server.class);
/*      */   
/*      */   public static void init() {
/*   86 */     start = false;
/*   87 */     Connect.create();
/*   88 */     maps = new ArrayList<>();
/*   89 */     npcs = new ArrayList<>();
/*   90 */     mobs = new ArrayList<>();
/*   91 */     moto = new JSONObject();
/*   92 */     sks = new ArrayList<>();
/*   93 */     parts = new ArrayList<>();
/*   94 */     efs = new ArrayList<>();
/*   95 */     arrs = new ArrayList<>();
/*   96 */     parts = new ArrayList<>();
/*   97 */     smallImg = (ArrayList)new ArrayList<>();
/*      */     try {
/*   99 */       int id = 0;
/*  100 */       ResultSet resultSet = Connect.stat.executeQuery("SELECT * FROM `map`;");
/*  101 */       while (resultSet.next()) {
/*  102 */         TileMap map = new TileMap();
/*  103 */         map.id = resultSet.getInt("id");
/*  104 */         map.name = resultSet.getString("name");
/*  105 */         map.tileId = resultSet.getByte("tileId");
/*  106 */         map.bgId = resultSet.getByte("bgId");
/*  107 */         map.type = resultSet.getByte("type");
/*  108 */         map.loadMapFromResource();
/*  109 */         map.npcs = new ArrayList();
/*  110 */         JSONArray jArr = (JSONArray)JSONValue.parse(resultSet.getString("npc"));
/*  111 */         int len = jArr.size(); int j;
/*  112 */         for (j = 0; j < len; j++) {
/*  113 */           JSONObject obj = (JSONObject)jArr.get(j);
/*  114 */           NpcTemplate npc = new NpcTemplate();
/*  115 */           npc.status = ((Long)obj.get("status")).byteValue();
/*  116 */           npc.x = ((Long)obj.get("x")).shortValue();
/*  117 */           npc.y = ((Long)obj.get("y")).shortValue();
/*  118 */           npc.templateId = (short)(byte)((Long)obj.get("templateId")).shortValue();
/*  119 */           map.npcs.add(npc);
/*      */         } 
/*  121 */         map.monsterCoordinates = new ArrayList();
/*  122 */         jArr = (JSONArray)JSONValue.parse(resultSet.getString("monster"));
/*  123 */         len = jArr.size();
/*  124 */         for (j = 0; j < len; j++) {
/*  125 */           JSONObject obj = (JSONObject)jArr.get(j);
/*  126 */           short templateId = ((Long)obj.get("templateId")).shortValue();
/*  127 */           short x = ((Long)obj.get("x")).shortValue();
/*  128 */           short y = ((Long)obj.get("y")).shortValue();
/*  129 */           map.monsterCoordinates.add(new TileMap.MonsterCoordinate(templateId, x, y));
/*      */         } 
/*  131 */         map.waypoints = new ArrayList();
/*  132 */         jArr = (JSONArray)JSONValue.parse(resultSet.getString("waypoint"));
/*  133 */         len = jArr.size();
/*  134 */         for (j = 0; j < len; j++) {
/*  135 */           JSONArray jArr2 = (JSONArray)jArr.get(j);
/*  136 */           Waypoint waypoint = new Waypoint();
/*  137 */           waypoint.mapId = ((Long)jArr2.get(0)).shortValue();
/*  138 */           waypoint.minX = ((Long)jArr2.get(1)).shortValue();
/*  139 */           waypoint.minY = ((Long)jArr2.get(2)).shortValue();
/*  140 */           waypoint.maxX = ((Long)jArr2.get(3)).shortValue();
/*  141 */           waypoint.maxY = ((Long)jArr2.get(4)).shortValue();
/*  142 */           map.waypoints.add(waypoint);
/*      */         } 
/*  144 */         maps.add(map);
/*      */       } 
/*  146 */       resultSet.close();
/*  147 */       logger.log("Map: " + maps.size());
/*  148 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `npc`;");
/*  149 */       while (resultSet.next()) {
/*  150 */         NpcTemplate npc = new NpcTemplate();
/*  151 */         npc.id = resultSet.getInt("id");
/*  152 */         npc.name = resultSet.getString("name");
/*  153 */         npc.headId = resultSet.getShort("headId");
/*  154 */         npc.bodyId = resultSet.getShort("bodyId");
/*  155 */         npc.legId = resultSet.getShort("legId");
/*  156 */         JSONArray jArr = (JSONArray)JSONValue.parse(resultSet.getString("menu"));
/*  157 */         int size = jArr.size();
/*  158 */         npc.menu = new String[size][];
/*  159 */         for (int j = 0; j < size; j++) {
/*  160 */           JSONArray jArr2 = (JSONArray)JSONValue.parse(jArr.get(j).toString());
/*  161 */           int size2 = jArr2.size();
/*  162 */           npc.menu[j] = new String[size2];
/*  163 */           for (int a = 0; a < size2; a++) {
/*  164 */             npc.menu[j][a] = jArr2.get(a).toString();
/*      */           }
/*      */         } 
/*  167 */         npcs.add(npc);
/*      */       } 
/*  169 */       resultSet.close();
/*  170 */       logger.log("Npc: " + npcs.size());
/*  171 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `monster`;");
/*  172 */       while (resultSet.next()) {
/*  173 */         MonsterTemplate mob = new MonsterTemplate();
/*  174 */         mob.id = resultSet.getInt("id");
/*  175 */         mob.name = resultSet.getString("name");
/*  176 */         mob.type = resultSet.getByte("type");
/*  177 */         mob.hp = resultSet.getInt("hp");
/*  178 */         mob.level = resultSet.getShort("level");
/*  179 */         mob.rangeMove = resultSet.getByte("rangeMove");
/*  180 */         mob.speed = resultSet.getByte("speed");
/*  181 */         mob.numberImage = resultSet.getByte("numberImage");
/*  182 */         mob.typeFly = resultSet.getByte("typeFly");
/*  183 */         JSONArray moves = (JSONArray)JSONValue.parse(resultSet.getString("move"));
/*  184 */         int size = moves.size();
/*  185 */         mob.frameBossMove = new byte[size];
/*  186 */         for (int j = 0; j < size; j++) {
/*  187 */           mob.frameBossMove[j] = ((Long)moves.get(j)).byteValue();
/*      */         }
/*  189 */         JSONArray attacks = (JSONArray)JSONValue.parse(resultSet.getString("attack"));
/*  190 */         size = attacks.size();
/*  191 */         mob.frameBossAttack = new byte[size][];
/*  192 */         for (int k = 0; k < size; k++) {
/*  193 */           JSONArray jArr = (JSONArray)attacks.get(k);
/*  194 */           int size2 = jArr.size();
/*  195 */           mob.frameBossAttack[k] = new byte[size2];
/*  196 */           for (int i1 = 0; i1 < size2; i1++) {
/*  197 */             mob.frameBossAttack[k][i1] = ((Long)jArr.get(i1)).byteValue();
/*      */           }
/*      */         } 
/*  200 */         JSONArray imgInfos = (JSONArray)JSONValue.parse(resultSet.getString("image"));
/*  201 */         size = imgInfos.size();
/*  202 */         mob.imgInfo = new ImageInfo[size];
/*  203 */         for (int m = 0; m < size; m++) {
/*  204 */           JSONObject job = (JSONObject)imgInfos.get(m);
/*  205 */           ImageInfo img = new ImageInfo();
/*  206 */           img.id = ((Long)job.get("id")).intValue();
/*  207 */           img.x0 = ((Long)job.get("x")).intValue();
/*  208 */           img.y0 = ((Long)job.get("y")).intValue();
/*  209 */           img.w = ((Long)job.get("width")).intValue();
/*  210 */           img.h = ((Long)job.get("height")).intValue();
/*  211 */           mob.imgInfo[m] = img;
/*      */         } 
/*  213 */         JSONArray frameBosss = (JSONArray)JSONValue.parse(resultSet.getString("frame"));
/*  214 */         size = frameBosss.size();
/*  215 */         mob.frameBoss = new Frame[size];
/*  216 */         for (int n = 0; n < size; n++) {
/*  217 */           JSONObject job = (JSONObject)frameBosss.get(n);
/*  218 */           JSONArray dx = (JSONArray)job.get("dx");
/*  219 */           JSONArray dy = (JSONArray)job.get("dy");
/*  220 */           JSONArray img = (JSONArray)job.get("img");
/*  221 */           Frame frame = new Frame();
/*  222 */           frame.dx = new int[dx.size()];
/*  223 */           frame.dy = new int[dy.size()];
/*  224 */           frame.idImg = new int[img.size()];
/*  225 */           for (int i1 = 0; i1 < frame.dx.length; i1++) {
/*  226 */             frame.dx[i1] = ((Long)dx.get(i1)).intValue();
/*  227 */             frame.dy[i1] = ((Long)dy.get(i1)).intValue();
/*  228 */             frame.idImg[i1] = ((Long)img.get(i1)).intValue();
/*      */           } 
/*  230 */           mob.frameBoss[n] = frame;
/*      */         } 
/*  232 */         mobs.add(mob);
/*      */       } 
/*  234 */       resultSet.close();
/*  235 */       logger.log("Mob: " + mobs.size());
/*  236 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `task`;");
/*  237 */       resultSet.last();
/*  238 */       int num = resultSet.getRow();
/*  239 */       resultSet.close();
/*  240 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `task`;");
/*  241 */       tasks = new byte[num][];
/*  242 */       mapTasks = new byte[num][];
/*  243 */       int i = 0;
/*  244 */       while (resultSet.next()) {
/*  245 */         JSONArray jArr = (JSONArray)JSONValue.parse(resultSet.getString("tasks"));
/*  246 */         JSONArray jArr2 = (JSONArray)JSONValue.parse(resultSet.getString("tasks"));
/*  247 */         tasks[i] = new byte[jArr.size()];
/*  248 */         mapTasks[i] = new byte[jArr.size()];
/*  249 */         for (int a = 0; a < (tasks[i]).length; a++) {
/*  250 */           tasks[i][a] = ((Long)jArr.get(a)).byteValue();
/*  251 */           mapTasks[i][a] = ((Long)jArr2.get(a)).byteValue();
/*      */         } 
/*  253 */         i++;
/*      */       } 
/*  255 */       resultSet.close();
/*  256 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `config`;");
/*  257 */       while (resultSet.next()) {
/*  258 */         String name = resultSet.getString("name");
/*  259 */         if (name.equals("moto")) {
/*  260 */           moto = (JSONObject)JSONValue.parse(resultSet.getString("value")); continue;
/*      */         } 
/*  262 */         JSONArray value = (JSONArray)JSONValue.parse(resultSet.getString("value"));
/*  263 */         if (name.equals("exp")) {
/*  264 */           exps = new long[value.size()];
/*  265 */           for (i = 0; i < exps.length; i++) {
/*  266 */             exps[i] = ((Long)value.get(i)).longValue();
/*      */           }
/*      */         } 
/*  269 */         if (name.equals("crystal")) {
/*  270 */           crystals = new int[value.size()];
/*  271 */           for (i = 0; i < crystals.length; i++) {
/*  272 */             crystals[i] = ((Long)value.get(i)).intValue();
/*      */           }
/*      */         } 
/*  275 */         if (name.equals("upClothe")) {
/*  276 */           upClothes = new int[value.size()];
/*  277 */           for (i = 0; i < upClothes.length; i++) {
/*  278 */             upClothes[i] = ((Long)value.get(i)).intValue();
/*      */           }
/*      */         } 
/*  281 */         if (name.equals("upAdorn")) {
/*  282 */           upAdorns = new int[value.size()];
/*  283 */           for (i = 0; i < upAdorns.length; i++) {
/*  284 */             upAdorns[i] = ((Long)value.get(i)).intValue();
/*      */           }
/*      */         } 
/*  287 */         if (name.equals("upWeapon")) {
/*  288 */           upWeapons = new int[value.size()];
/*  289 */           for (i = 0; i < upWeapons.length; i++) {
/*  290 */             upWeapons[i] = ((Long)value.get(i)).intValue();
/*      */           }
/*      */         } 
/*  293 */         if (name.equals("coinUpCrystal")) {
/*  294 */           coinUpCrystals = new int[value.size()];
/*  295 */           for (i = 0; i < coinUpCrystals.length; i++) {
/*  296 */             coinUpCrystals[i] = ((Long)value.get(i)).intValue();
/*      */           }
/*      */         } 
/*  299 */         if (name.equals("coinUpClothe")) {
/*  300 */           coinUpClothes = new int[value.size()];
/*  301 */           for (i = 0; i < coinUpClothes.length; i++) {
/*  302 */             coinUpClothes[i] = ((Long)value.get(i)).intValue();
/*      */           }
/*      */         } 
/*  305 */         if (name.equals("coinUpAdorn")) {
/*  306 */           coinUpAdorns = new int[value.size()];
/*  307 */           for (i = 0; i < coinUpAdorns.length; i++) {
/*  308 */             coinUpAdorns[i] = ((Long)value.get(i)).intValue();
/*      */           }
/*      */         } 
/*  311 */         if (name.equals("coinUpWeapon")) {
/*  312 */           coinUpWeapons = new int[value.size()];
/*  313 */           for (i = 0; i < coinUpWeapons.length; i++) {
/*  314 */             coinUpWeapons[i] = ((Long)value.get(i)).intValue();
/*      */           }
/*      */         } 
/*  317 */         if (name.equals("goldUp")) {
/*  318 */           goldUps = new int[value.size()];
/*  319 */           for (i = 0; i < goldUps.length; i++) {
/*  320 */             goldUps[i] = ((Long)value.get(i)).intValue();
/*      */           }
/*      */         } 
/*  323 */         if (name.equals("maxPercent")) {
/*  324 */           maxPercents = new int[value.size()];
/*  325 */           for (i = 0; i < maxPercents.length; i++) {
/*  326 */             maxPercents[i] = ((Long)value.get(i)).intValue();
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/*  331 */       resultSet.close();
/*  332 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `effect`;");
/*  333 */       Effect.effTemplates = new ArrayList();
/*  334 */       while (resultSet.next()) {
/*  335 */         EffectTemplate eff = new EffectTemplate();
/*  336 */         eff.id = resultSet.getByte("id");
/*  337 */         eff.name = resultSet.getString("name");
/*  338 */         eff.type = resultSet.getByte("type");
/*  339 */         eff.icon = resultSet.getShort("icon");
/*  340 */         Effect.effTemplates.add(eff);
/*      */       } 
/*  342 */       resultSet.close();
/*  343 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `item`;");
/*  344 */       while (resultSet.next()) {
/*  345 */         ItemEntry item = new ItemEntry();
/*  346 */         item.id = resultSet.getInt("id");
/*  347 */         item.name = resultSet.getString("name");
/*  348 */         item.type = resultSet.getByte("type");
/*  349 */         item.gender = resultSet.getByte("gender");
/*  350 */         item.level = resultSet.getShort("level");
/*  351 */         item.part = resultSet.getShort("part");
/*  352 */         item.icon = resultSet.getShort("icon");
/*  353 */         item.description = resultSet.getString("description");
/*  354 */         item.isUpToUp = resultSet.getBoolean("isUpToUp");
/*  355 */         ItemData.put(item.id, item);
/*      */       } 
/*  357 */       resultSet.close();
/*  358 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `item_option`;");
/*  359 */       while (resultSet.next()) {
/*  360 */         ItemOptionTemplate item = new ItemOptionTemplate();
/*  361 */         item.id = resultSet.getInt("id");
/*  362 */         item.name = resultSet.getString("name");
/*  363 */         item.type = resultSet.getByte("type");
/*  364 */         ItemData.put(item.id, item);
/*      */       } 
/*  366 */       resultSet.close();
/*  367 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `skill_option`;");
/*  368 */       SkillData.optionTemplates = new HashMap<>();
/*  369 */       while (resultSet.next()) {
/*  370 */         SkillOptionTemplate template = new SkillOptionTemplate();
/*  371 */         template.id = resultSet.getInt("id");
/*  372 */         template.name = resultSet.getString("name");
/*  373 */         SkillData.optionTemplates.put(Integer.valueOf(template.id), template);
/*      */       } 
/*  375 */       resultSet.close();
/*  376 */       SkillData.nClasss = new HashMap<>();
/*  377 */       for (int g = 0; g < 7; g++) {
/*  378 */         resultSet = Connect.stat.executeQuery("SELECT * FROM `skill` WHERE `class` = " + g + ";");
/*  379 */         SkillData.NClass nClass = new SkillData.NClass();
/*  380 */         nClass.classId = g;
/*  381 */         switch (nClass.classId) {
/*      */           case 1:
/*  383 */             nClass.name = "Ninja kiếm";
/*      */             break;
/*      */           
/*      */           case 2:
/*  387 */             nClass.name = "Ninja phi tiêu";
/*      */             break;
/*      */           
/*      */           case 3:
/*  391 */             nClass.name = "Ninja kunai";
/*      */             break;
/*      */           
/*      */           case 4:
/*  395 */             nClass.name = "Ninja cung";
/*      */             break;
/*      */           
/*      */           case 5:
/*  399 */             nClass.name = "Ninja đao";
/*      */             break;
/*      */           
/*      */           case 6:
/*  403 */             nClass.name = "Ninja quạt";
/*      */             break;
/*      */           
/*      */           default:
/*  407 */             nClass.name = "Chưa vào trường";
/*      */             break;
/*      */         } 
/*  410 */         nClass.templates = new HashMap<>();
/*  411 */         while (resultSet.next()) {
/*  412 */           SkillTemplate template = new SkillTemplate();
/*  413 */           template.id = resultSet.getInt("id");
/*  414 */           template.name = resultSet.getString("name");
/*  415 */           template.maxPoint = resultSet.getByte("maxPoint");
/*  416 */           template.type = resultSet.getByte("type");
/*  417 */           template.iconId = resultSet.getShort("iconId");
/*  418 */           template.description = resultSet.getString("description");
/*  419 */           template.skills = new ArrayList();
/*  420 */           JSONArray skills = (JSONArray)JSONValue.parse(resultSet.getString("skills"));
/*  421 */           for (int a = 0; a < skills.size(); a++) {
/*  422 */             JSONObject obj = (JSONObject)skills.get(a);
/*  423 */             Skill skill = new Skill();
/*  424 */             skill.skillId = ((Long)obj.get("skillId")).intValue();
/*  425 */             skill.point = ((Long)obj.get("point")).byteValue();
/*  426 */             skill.coolDown = ((Long)obj.get("coolDown")).intValue();
/*  427 */             skill.level = ((Long)obj.get("level")).byteValue();
/*  428 */             skill.maxFight = ((Long)obj.get("maxFight")).byteValue();
/*  429 */             skill.manaUse = ((Long)obj.get("manaUse")).shortValue();
/*  430 */             skill.dx = ((Long)obj.get("dx")).shortValue();
/*  431 */             skill.dy = ((Long)obj.get("dy")).shortValue();
/*  432 */             JSONArray jArr = (JSONArray)obj.get("options");
/*  433 */             int len = jArr.size();
/*  434 */             skill.options = new SkillOption[len];
/*  435 */             for (int j = 0; j < len; j++) {
/*  436 */               JSONObject o = (JSONObject)jArr.get(j);
/*  437 */               int templateId = ((Long)o.get("id")).intValue();
/*  438 */               int param = ((Long)o.get("param")).intValue();
/*  439 */               skill.options[j] = new SkillOption(templateId, param);
/*      */             } 
/*  441 */             template.skills.add(skill);
/*      */           } 
/*  443 */           nClass.templates.put(Integer.valueOf(template.id), template);
/*      */         } 
/*  445 */         resultSet.close();
/*  446 */         SkillData.nClasss.put(Integer.valueOf(g), nClass);
/*      */       } 
/*  448 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `weapon_store`;");
/*  449 */       int index = 0;
/*  450 */       while (resultSet.next()) {
/*  451 */         ItemStore item = new ItemStore();
/*  452 */         item.id = resultSet.getInt("id");
/*  453 */         item.templateId = resultSet.getShort("templateId");
/*  454 */         item.sys = resultSet.getByte("sys");
/*  455 */         item.xu = resultSet.getInt("xu");
/*  456 */         item.luong = resultSet.getInt("luong");
/*  457 */         item.yen = resultSet.getInt("yen");
/*  458 */         item.expire = resultSet.getLong("expire");
/*  459 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  460 */         item.option_max = new int[option_max.size()][2];
/*  461 */         for (int a = 0; a < item.option_max.length; a++) {
/*  462 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  463 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  464 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  466 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  467 */         item.index = index++;
/*  468 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  469 */         StoreData.VU_KHI.add(item);
/*      */       } 
/*  471 */       resultSet.close();
/*  472 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `hat_store` WHERE `gender` = 1;");
/*  473 */       index = 0;
/*  474 */       while (resultSet.next()) {
/*  475 */         ItemStore item = new ItemStore();
/*  476 */         item.id = resultSet.getInt("id");
/*  477 */         item.templateId = resultSet.getShort("templateId");
/*  478 */         item.sys = resultSet.getByte("sys");
/*  479 */         item.xu = resultSet.getInt("xu");
/*  480 */         item.luong = resultSet.getInt("luong");
/*  481 */         item.yen = resultSet.getInt("yen");
/*  482 */         item.expire = resultSet.getLong("expire");
/*  483 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  484 */         item.option_max = new int[option_max.size()][2];
/*  485 */         for (int a = 0; a < item.option_max.length; a++) {
/*  486 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  487 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  488 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  490 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  491 */         item.index = index++;
/*  492 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  493 */         StoreData.NON_NAM.add(item);
/*      */       } 
/*  495 */       resultSet.close();
/*  496 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `hat_store` WHERE `gender` = 0;");
/*  497 */       index = 0;
/*  498 */       while (resultSet.next()) {
/*  499 */         ItemStore item = new ItemStore();
/*  500 */         item.id = resultSet.getInt("id");
/*  501 */         item.templateId = resultSet.getShort("templateId");
/*  502 */         item.sys = resultSet.getByte("sys");
/*  503 */         item.xu = resultSet.getInt("xu");
/*  504 */         item.luong = resultSet.getInt("luong");
/*  505 */         item.yen = resultSet.getInt("yen");
/*  506 */         item.expire = resultSet.getLong("expire");
/*  507 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  508 */         item.option_max = new int[option_max.size()][2];
/*  509 */         for (int a = 0; a < item.option_max.length; a++) {
/*  510 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  511 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  512 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  514 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  515 */         item.index = index++;
/*  516 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  517 */         StoreData.NON_NU.add(item);
/*      */       } 
/*  519 */       resultSet.close();
/*  520 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `shirt_store` WHERE `gender` = 1;");
/*  521 */       index = 0;
/*  522 */       while (resultSet.next()) {
/*  523 */         ItemStore item = new ItemStore();
/*  524 */         item.id = resultSet.getInt("id");
/*  525 */         item.templateId = resultSet.getShort("templateId");
/*  526 */         item.sys = resultSet.getByte("sys");
/*  527 */         item.xu = resultSet.getInt("xu");
/*  528 */         item.luong = resultSet.getInt("luong");
/*  529 */         item.yen = resultSet.getInt("yen");
/*  530 */         item.expire = resultSet.getLong("expire");
/*  531 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  532 */         item.option_max = new int[option_max.size()][2];
/*  533 */         for (int a = 0; a < item.option_max.length; a++) {
/*  534 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  535 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  536 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  538 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  539 */         item.index = index++;
/*  540 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  541 */         StoreData.AO_NAM.add(item);
/*      */       } 
/*  543 */       resultSet.close();
/*  544 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `shirt_store` WHERE `gender` = 0;");
/*  545 */       index = 0;
/*  546 */       while (resultSet.next()) {
/*  547 */         ItemStore item = new ItemStore();
/*  548 */         item.id = resultSet.getInt("id");
/*  549 */         item.templateId = resultSet.getShort("templateId");
/*  550 */         item.sys = resultSet.getByte("sys");
/*  551 */         item.xu = resultSet.getInt("xu");
/*  552 */         item.luong = resultSet.getInt("luong");
/*  553 */         item.yen = resultSet.getInt("yen");
/*  554 */         item.expire = resultSet.getLong("expire");
/*  555 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  556 */         item.option_max = new int[option_max.size()][2];
/*  557 */         for (int a = 0; a < item.option_max.length; a++) {
/*  558 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  559 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  560 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  562 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  563 */         item.index = index++;
/*  564 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  565 */         StoreData.AO_NU.add(item);
/*      */       } 
/*  567 */       resultSet.close();
/*  568 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `glove_store` WHERE `gender` = 1;");
/*  569 */       index = 0;
/*  570 */       while (resultSet.next()) {
/*  571 */         ItemStore item = new ItemStore();
/*  572 */         item.id = resultSet.getInt("id");
/*  573 */         item.templateId = resultSet.getShort("templateId");
/*  574 */         item.sys = resultSet.getByte("sys");
/*  575 */         item.xu = resultSet.getInt("xu");
/*  576 */         item.luong = resultSet.getInt("luong");
/*  577 */         item.yen = resultSet.getInt("yen");
/*  578 */         item.expire = resultSet.getLong("expire");
/*  579 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  580 */         item.option_max = new int[option_max.size()][2];
/*  581 */         for (int a = 0; a < item.option_max.length; a++) {
/*  582 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  583 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  584 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  586 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  587 */         item.index = index++;
/*  588 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  589 */         StoreData.GANG_NAM.add(item);
/*      */       } 
/*  591 */       resultSet.close();
/*  592 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `glove_store` WHERE `gender` = 0;");
/*  593 */       index = 0;
/*  594 */       while (resultSet.next()) {
/*  595 */         ItemStore item = new ItemStore();
/*  596 */         item.id = resultSet.getInt("id");
/*  597 */         item.templateId = resultSet.getShort("templateId");
/*  598 */         item.sys = resultSet.getByte("sys");
/*  599 */         item.xu = resultSet.getInt("xu");
/*  600 */         item.luong = resultSet.getInt("luong");
/*  601 */         item.yen = resultSet.getInt("yen");
/*  602 */         item.expire = resultSet.getLong("expire");
/*  603 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  604 */         item.option_max = new int[option_max.size()][2];
/*  605 */         for (int a = 0; a < item.option_max.length; a++) {
/*  606 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  607 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  608 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  610 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  611 */         item.index = index++;
/*  612 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  613 */         StoreData.GANG_NU.add(item);
/*      */       } 
/*  615 */       resultSet.close();
/*      */       
/*  617 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `pant_store` WHERE `gender` = 1;");
/*  618 */       index = 0;
/*  619 */       while (resultSet.next()) {
/*  620 */         ItemStore item = new ItemStore();
/*  621 */         item.id = resultSet.getInt("id");
/*  622 */         item.templateId = resultSet.getShort("templateId");
/*  623 */         item.sys = resultSet.getByte("sys");
/*  624 */         item.xu = resultSet.getInt("xu");
/*  625 */         item.luong = resultSet.getInt("luong");
/*  626 */         item.yen = resultSet.getInt("yen");
/*  627 */         item.expire = resultSet.getLong("expire");
/*  628 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  629 */         item.option_max = new int[option_max.size()][2];
/*  630 */         for (int a = 0; a < item.option_max.length; a++) {
/*  631 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  632 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  633 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  635 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  636 */         item.index = index++;
/*  637 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  638 */         StoreData.QUAN_NAM.add(item);
/*      */       } 
/*  640 */       resultSet.close();
/*  641 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `pant_store` WHERE `gender` = 0;");
/*  642 */       index = 0;
/*  643 */       while (resultSet.next()) {
/*  644 */         ItemStore item = new ItemStore();
/*  645 */         item.id = resultSet.getInt("id");
/*  646 */         item.templateId = resultSet.getShort("templateId");
/*  647 */         item.sys = resultSet.getByte("sys");
/*  648 */         item.xu = resultSet.getInt("xu");
/*  649 */         item.luong = resultSet.getInt("luong");
/*  650 */         item.yen = resultSet.getInt("yen");
/*  651 */         item.expire = resultSet.getLong("expire");
/*  652 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  653 */         item.option_max = new int[option_max.size()][2];
/*  654 */         for (int a = 0; a < item.option_max.length; a++) {
/*  655 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  656 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  657 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  659 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  660 */         item.index = index++;
/*  661 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  662 */         StoreData.QUAN_NU.add(item);
/*      */       } 
/*  664 */       resultSet.close();
/*      */       
/*  666 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `shoe_store` WHERE `gender` = 1;");
/*  667 */       index = 0;
/*  668 */       while (resultSet.next()) {
/*  669 */         ItemStore item = new ItemStore();
/*  670 */         item.id = resultSet.getInt("id");
/*  671 */         item.templateId = resultSet.getShort("templateId");
/*  672 */         item.sys = resultSet.getByte("sys");
/*  673 */         item.xu = resultSet.getInt("xu");
/*  674 */         item.luong = resultSet.getInt("luong");
/*  675 */         item.yen = resultSet.getInt("yen");
/*  676 */         item.expire = resultSet.getLong("expire");
/*  677 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  678 */         item.option_max = new int[option_max.size()][2];
/*  679 */         for (int a = 0; a < item.option_max.length; a++) {
/*  680 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  681 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  682 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  684 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  685 */         item.index = index++;
/*  686 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  687 */         StoreData.GIAY_NAM.add(item);
/*      */       } 
/*  689 */       resultSet.close();
/*  690 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `shoe_store` WHERE `gender` = 0;");
/*  691 */       index = 0;
/*  692 */       while (resultSet.next()) {
/*  693 */         ItemStore item = new ItemStore();
/*  694 */         item.id = resultSet.getInt("id");
/*  695 */         item.templateId = resultSet.getShort("templateId");
/*  696 */         item.sys = resultSet.getByte("sys");
/*  697 */         item.xu = resultSet.getInt("xu");
/*  698 */         item.luong = resultSet.getInt("luong");
/*  699 */         item.yen = resultSet.getInt("yen");
/*  700 */         item.expire = resultSet.getLong("expire");
/*  701 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  702 */         item.option_max = new int[option_max.size()][2];
/*  703 */         for (int a = 0; a < item.option_max.length; a++) {
/*  704 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  705 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  706 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  708 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  709 */         item.index = index++;
/*  710 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  711 */         StoreData.GIAY_NU.add(item);
/*      */       } 
/*  713 */       resultSet.close();
/*  714 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `store`;");
/*  715 */       index = 0;
/*  716 */       while (resultSet.next()) {
/*  717 */         ItemStore item = new ItemStore();
/*  718 */         item.id = resultSet.getInt("id");
/*  719 */         item.templateId = resultSet.getShort("templateId");
/*  720 */         item.xu = resultSet.getInt("xu");
/*  721 */         item.luong = resultSet.getInt("luong");
/*  722 */         item.yen = resultSet.getInt("yen");
/*  723 */         item.sys = resultSet.getByte("sys");
/*  724 */         item.expire = resultSet.getLong("expire");
/*  725 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  726 */         item.option_max = new int[option_max.size()][2];
/*  727 */         for (int a = 0; a < item.option_max.length; a++) {
/*  728 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  729 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  730 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  732 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  733 */         item.index = index++;
/*  734 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  735 */         StoreData.LINH_TINH.add(item);
/*      */       } 
/*  737 */       resultSet.close();
/*  738 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `book_store`;");
/*  739 */       index = 0;
/*  740 */       while (resultSet.next()) {
/*  741 */         ItemStore item = new ItemStore();
/*  742 */         item.id = resultSet.getInt("id");
/*  743 */         item.templateId = resultSet.getShort("templateId");
/*  744 */         item.xu = resultSet.getInt("xu");
/*  745 */         item.luong = resultSet.getInt("luong");
/*  746 */         item.yen = resultSet.getInt("yen");
/*  747 */         item.expire = resultSet.getLong("expire");
/*  748 */         item.index = index++;
/*  749 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  750 */         StoreData.SACH.add(item);
/*      */       } 
/*  752 */       resultSet.close();
/*      */       
/*  754 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `necklace_store`;");
/*  755 */       index = 0;
/*  756 */       while (resultSet.next()) {
/*  757 */         ItemStore item = new ItemStore();
/*  758 */         item.id = resultSet.getInt("id");
/*  759 */         item.templateId = resultSet.getShort("templateId");
/*  760 */         item.xu = resultSet.getInt("xu");
/*  761 */         item.luong = resultSet.getInt("luong");
/*  762 */         item.yen = resultSet.getInt("yen");
/*  763 */         item.sys = resultSet.getByte("sys");
/*  764 */         item.expire = resultSet.getLong("expire");
/*  765 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  766 */         item.option_max = new int[option_max.size()][2];
/*  767 */         for (int a = 0; a < item.option_max.length; a++) {
/*  768 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  769 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  770 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  772 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  773 */         item.index = index++;
/*  774 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  775 */         StoreData.DAY_CHUYEN.add(item);
/*      */       } 
/*  777 */       resultSet.close();
/*  778 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `pearl_store`;");
/*  779 */       index = 0;
/*  780 */       while (resultSet.next()) {
/*  781 */         ItemStore item = new ItemStore();
/*  782 */         item.id = resultSet.getInt("id");
/*  783 */         item.templateId = resultSet.getShort("templateId");
/*  784 */         item.xu = resultSet.getInt("xu");
/*  785 */         item.luong = resultSet.getInt("luong");
/*  786 */         item.yen = resultSet.getInt("yen");
/*  787 */         item.sys = resultSet.getByte("sys");
/*  788 */         item.expire = resultSet.getLong("expire");
/*  789 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  790 */         item.option_max = new int[option_max.size()][2];
/*  791 */         for (int a = 0; a < item.option_max.length; a++) {
/*  792 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  793 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  794 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  796 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  797 */         item.index = index++;
/*  798 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  799 */         StoreData.NGOC_BOI.add(item);
/*      */       } 
/*  801 */       resultSet.close();
/*  802 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `ring_store`;");
/*  803 */       index = 0;
/*  804 */       while (resultSet.next()) {
/*  805 */         ItemStore item = new ItemStore();
/*  806 */         item.id = resultSet.getInt("id");
/*  807 */         item.templateId = resultSet.getShort("templateId");
/*  808 */         item.xu = resultSet.getInt("xu");
/*  809 */         item.luong = resultSet.getInt("luong");
/*  810 */         item.yen = resultSet.getInt("yen");
/*  811 */         item.sys = resultSet.getByte("sys");
/*  812 */         item.expire = resultSet.getLong("expire");
/*  813 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  814 */         item.option_max = new int[option_max.size()][2];
/*  815 */         for (int a = 0; a < item.option_max.length; a++) {
/*  816 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  817 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  818 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  820 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  821 */         item.index = index++;
/*  822 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  823 */         StoreData.NHAN.add(item);
/*      */       } 
/*  825 */       resultSet.close();
/*  826 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `spell_store`;");
/*  827 */       index = 0;
/*  828 */       while (resultSet.next()) {
/*  829 */         ItemStore item = new ItemStore();
/*  830 */         item.id = resultSet.getInt("id");
/*  831 */         item.templateId = resultSet.getShort("templateId");
/*  832 */         item.xu = resultSet.getInt("xu");
/*  833 */         item.luong = resultSet.getInt("luong");
/*  834 */         item.yen = resultSet.getInt("yen");
/*  835 */         item.sys = resultSet.getByte("sys");
/*  836 */         item.expire = resultSet.getLong("expire");
/*  837 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  838 */         item.option_max = new int[option_max.size()][2];
/*  839 */         for (int a = 0; a < item.option_max.length; a++) {
/*  840 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  841 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  842 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  844 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  845 */         item.index = index++;
/*  846 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  847 */         StoreData.BUA.add(item);
/*      */       } 
/*  849 */       resultSet.close();
/*  850 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `fashion_store`;");
/*  851 */       index = 0;
/*  852 */       while (resultSet.next()) {
/*  853 */         ItemStore item = new ItemStore();
/*  854 */         item.id = resultSet.getInt("id");
/*  855 */         item.templateId = resultSet.getShort("templateId");
/*  856 */         item.xu = resultSet.getInt("xu");
/*  857 */         item.luong = resultSet.getInt("luong");
/*  858 */         item.yen = resultSet.getInt("yen");
/*  859 */         item.sys = resultSet.getByte("sys");
/*  860 */         item.expire = resultSet.getLong("expire");
/*  861 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  862 */         item.option_max = new int[option_max.size()][2];
/*  863 */         for (int a = 0; a < item.option_max.length; a++) {
/*  864 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  865 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  866 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  868 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  869 */         item.index = index++;
/*  870 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  871 */         StoreData.THOI_TRANG.add(item);
/*      */       } 
/*  873 */       resultSet.close();
/*  874 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `clan_store`;");
/*  875 */       index = 0;
/*  876 */       while (resultSet.next()) {
/*  877 */         ItemStore item = new ItemStore();
/*  878 */         item.id = resultSet.getInt("id");
/*  879 */         item.templateId = resultSet.getShort("templateId");
/*  880 */         item.xu = resultSet.getInt("xu");
/*  881 */         item.luong = resultSet.getInt("luong");
/*  882 */         item.yen = resultSet.getInt("yen");
/*  883 */         item.sys = resultSet.getByte("sys");
/*  884 */         item.expire = resultSet.getLong("expire");
/*  885 */         JSONArray option_max = (JSONArray)JSONValue.parse(resultSet.getString("options"));
/*  886 */         item.option_max = new int[option_max.size()][2];
/*  887 */         for (int a = 0; a < item.option_max.length; a++) {
/*  888 */           JSONArray jArr2 = (JSONArray)option_max.get(a);
/*  889 */           item.option_max[a][0] = ((Long)jArr2.get(0)).intValue();
/*  890 */           item.option_max[a][1] = ((Long)jArr2.get(1)).intValue();
/*      */         } 
/*  892 */         item.option_min = NinjaUtil.getOptionShop(item.option_max);
/*  893 */         item.index = index++;
/*  894 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  895 */         StoreData.GIA_TOC.add(item);
/*      */       } 
/*  897 */       resultSet.close();
/*  898 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `grocery_store` WHERE `isLock` = 0;");
/*  899 */       index = 0;
/*  900 */       while (resultSet.next()) {
/*  901 */         ItemStore item = new ItemStore();
/*  902 */         item.id = resultSet.getInt("id");
/*  903 */         item.templateId = resultSet.getShort("templateId");
/*  904 */         item.xu = resultSet.getInt("xu");
/*  905 */         item.luong = resultSet.getInt("luong");
/*  906 */         item.yen = resultSet.getInt("yen");
/*  907 */         item.isLock = false;
/*  908 */         item.expire = resultSet.getLong("expire");
/*  909 */         item.index = index++;
/*  910 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  911 */         StoreData.THUC_AN.add(item);
/*      */       } 
/*  913 */       resultSet.close();
/*  914 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `grocery_store` WHERE `isLock` = 1;");
/*  915 */       index = 0;
/*  916 */       while (resultSet.next()) {
/*  917 */         ItemStore item = new ItemStore();
/*  918 */         item.id = resultSet.getInt("id");
/*  919 */         item.templateId = resultSet.getShort("templateId");
/*  920 */         item.xu = resultSet.getInt("xu");
/*  921 */         item.luong = resultSet.getInt("luong");
/*  922 */         item.yen = resultSet.getInt("yen");
/*  923 */         item.isLock = true;
/*  924 */         item.expire = resultSet.getLong("expire");
/*  925 */         item.index = index++;
/*  926 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  927 */         StoreData.THUC_AN_KHOA.add(item);
/*      */       } 
/*  929 */       resultSet.close();
/*  930 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `stack_store` WHERE `isLock` = 0;");
/*  931 */       index = 0;
/*  932 */       while (resultSet.next()) {
/*  933 */         ItemStore item = new ItemStore();
/*  934 */         item.id = resultSet.getInt("id");
/*  935 */         item.templateId = resultSet.getShort("templateId");
/*  936 */         item.xu = resultSet.getInt("xu");
/*  937 */         item.luong = resultSet.getInt("luong");
/*  938 */         item.yen = resultSet.getInt("yen");
/*  939 */         item.isLock = true;
/*  940 */         item.expire = resultSet.getLong("expire");
/*  941 */         item.index = index++;
/*  942 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  943 */         StoreData.DUOC_PHAM.add(item);
/*      */       } 
/*  945 */       resultSet.close();
/*  946 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `stack_store` WHERE `isLock` = 1;");
/*  947 */       index = 0;
/*  948 */       while (resultSet.next()) {
/*  949 */         ItemStore item = new ItemStore();
/*  950 */         item.id = resultSet.getInt("id");
/*  951 */         item.templateId = resultSet.getShort("templateId");
/*  952 */         item.xu = resultSet.getInt("xu");
/*  953 */         item.luong = resultSet.getInt("luong");
/*  954 */         item.yen = resultSet.getInt("yen");
/*  955 */         item.isLock = true;
/*  956 */         item.expire = resultSet.getLong("expire");
/*  957 */         item.index = index++;
/*  958 */         item.entry = ItemData.getItemEntryById(item.templateId);
/*  959 */         StoreData.DUOC_PHAM_KHOA.add(item);
/*      */       } 
/*  961 */       resultSet.close();
/*  962 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `nj_skill`;");
/*  963 */       while (resultSet.next()) {
/*  964 */         SkillPaint p = new SkillPaint();
/*  965 */         p.id = resultSet.getShort("skillId");
/*  966 */         p.effId = resultSet.getShort("effId");
/*  967 */         p.numEff = resultSet.getByte("numEff");
/*  968 */         JSONArray jA = (JSONArray)JSONValue.parse(resultSet.getString("skillStand"));
/*  969 */         p.skillStand = new SkillInfoPaint[jA.size()]; int k;
/*  970 */         for (k = 0; k < p.skillStand.length; k++) {
/*  971 */           JSONObject o = (JSONObject)jA.get(k);
/*  972 */           p.skillStand[k] = new SkillInfoPaint();
/*  973 */           (p.skillStand[k]).status = ((Long)o.get("status")).byteValue();
/*  974 */           (p.skillStand[k]).effS0Id = ((Long)o.get("effS0Id")).shortValue();
/*  975 */           (p.skillStand[k]).e0dx = ((Long)o.get("e0dx")).shortValue();
/*  976 */           (p.skillStand[k]).e0dy = ((Long)o.get("e0dy")).shortValue();
/*  977 */           (p.skillStand[k]).effS1Id = ((Long)o.get("effS1Id")).shortValue();
/*  978 */           (p.skillStand[k]).e1dx = ((Long)o.get("e1dx")).shortValue();
/*  979 */           (p.skillStand[k]).e1dy = ((Long)o.get("e1dy")).shortValue();
/*  980 */           (p.skillStand[k]).effS2Id = ((Long)o.get("effS2Id")).shortValue();
/*  981 */           (p.skillStand[k]).e2dx = ((Long)o.get("e2dx")).shortValue();
/*  982 */           (p.skillStand[k]).e2dy = ((Long)o.get("e2dy")).shortValue();
/*  983 */           (p.skillStand[k]).arrowId = ((Long)o.get("arrowId")).shortValue();
/*  984 */           (p.skillStand[k]).adx = ((Long)o.get("adx")).shortValue();
/*  985 */           (p.skillStand[k]).ady = ((Long)o.get("ady")).shortValue();
/*      */         } 
/*  987 */         jA = (JSONArray)JSONValue.parse(resultSet.getString("skillFly"));
/*  988 */         p.skillfly = new SkillInfoPaint[jA.size()];
/*  989 */         for (k = 0; k < p.skillfly.length; k++) {
/*  990 */           JSONObject o = (JSONObject)jA.get(k);
/*  991 */           p.skillfly[k] = new SkillInfoPaint();
/*  992 */           (p.skillfly[k]).status = ((Long)o.get("status")).byteValue();
/*  993 */           (p.skillfly[k]).effS0Id = ((Long)o.get("effS0Id")).shortValue();
/*  994 */           (p.skillfly[k]).e0dx = ((Long)o.get("e0dx")).shortValue();
/*  995 */           (p.skillfly[k]).e0dy = ((Long)o.get("e0dy")).shortValue();
/*  996 */           (p.skillfly[k]).effS1Id = ((Long)o.get("effS1Id")).shortValue();
/*  997 */           (p.skillfly[k]).e1dx = ((Long)o.get("e1dx")).shortValue();
/*  998 */           (p.skillfly[k]).e1dy = ((Long)o.get("e1dy")).shortValue();
/*  999 */           (p.skillfly[k]).effS2Id = ((Long)o.get("effS2Id")).shortValue();
/* 1000 */           (p.skillfly[k]).e2dx = ((Long)o.get("e2dx")).shortValue();
/* 1001 */           (p.skillfly[k]).e2dy = ((Long)o.get("e2dy")).shortValue();
/* 1002 */           (p.skillfly[k]).arrowId = ((Long)o.get("arrowId")).shortValue();
/* 1003 */           (p.skillfly[k]).adx = ((Long)o.get("adx")).shortValue();
/* 1004 */           (p.skillfly[k]).ady = ((Long)o.get("ady")).shortValue();
/*      */         } 
/* 1006 */         sks.add(p);
/*      */       } 
/* 1008 */       resultSet.close();
/* 1009 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `nj_part`;");
/* 1010 */       while (resultSet.next()) {
/* 1011 */         byte type = resultSet.getByte("type");
/* 1012 */         JSONArray jA = (JSONArray)JSONValue.parse(resultSet.getString("part"));
/* 1013 */         Part part = new Part(type);
/* 1014 */         for (int k = 0; k < part.pi.length; k++) {
/* 1015 */           JSONObject o = (JSONObject)jA.get(k);
/* 1016 */           part.pi[k] = new PartImage();
/* 1017 */           (part.pi[k]).id = ((Long)o.get("id")).shortValue();
/* 1018 */           (part.pi[k]).dx = ((Long)o.get("dx")).byteValue();
/* 1019 */           (part.pi[k]).dy = ((Long)o.get("dy")).byteValue();
/*      */         } 
/* 1021 */         parts.add(part);
/*      */       } 
/* 1023 */       resultSet.close();
/* 1024 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `nj_image`;");
/* 1025 */       while (resultSet.next()) {
/* 1026 */         int[] smallImage = new int[5];
/* 1027 */         JSONArray jA = (JSONArray)JSONValue.parse(resultSet.getString("smallImage"));
/* 1028 */         smallImage[0] = ((Long)jA.get(0)).intValue();
/* 1029 */         smallImage[1] = ((Long)jA.get(1)).intValue();
/* 1030 */         smallImage[2] = ((Long)jA.get(2)).intValue();
/* 1031 */         smallImage[3] = ((Long)jA.get(3)).intValue();
/* 1032 */         smallImage[4] = ((Long)jA.get(4)).intValue();
/* 1033 */         smallImg.add(smallImage);
/*      */       } 
/* 1035 */       resultSet.close();
/* 1036 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `nj_arrow`;");
/* 1037 */       while (resultSet.next()) {
/* 1038 */         ArrowPaint p = new ArrowPaint();
/* 1039 */         p.id = resultSet.getShort("id");
/* 1040 */         JSONArray jA = (JSONArray)JSONValue.parse(resultSet.getString("imgId"));
/* 1041 */         p.imgId[0] = ((Long)jA.get(0)).shortValue();
/* 1042 */         p.imgId[1] = ((Long)jA.get(1)).shortValue();
/* 1043 */         p.imgId[2] = ((Long)jA.get(2)).shortValue();
/*      */       } 
/* 1045 */       resultSet.close();
/* 1046 */       resultSet = Connect.stat.executeQuery("SELECT * FROM `nj_effect`;");
/* 1047 */       while (resultSet.next()) {
/* 1048 */         EffectCharPaint effectCharInfo = new EffectCharPaint();
/* 1049 */         effectCharInfo.idEf = resultSet.getShort("id");
/* 1050 */         JSONArray jA = (JSONArray)JSONValue.parse(resultSet.getString("info"));
/* 1051 */         effectCharInfo.arrEfInfo = new EffectInfoPaint[jA.size()];
/* 1052 */         for (int k = 0; k < effectCharInfo.arrEfInfo.length; k++) {
/* 1053 */           JSONObject o = (JSONObject)jA.get(k);
/* 1054 */           effectCharInfo.arrEfInfo[k] = new EffectInfoPaint();
/* 1055 */           (effectCharInfo.arrEfInfo[k]).idImg = ((Long)o.get("imgId")).shortValue();
/* 1056 */           (effectCharInfo.arrEfInfo[k]).dx = ((Long)o.get("dx")).byteValue();
/* 1057 */           (effectCharInfo.arrEfInfo[k]).dy = ((Long)o.get("dy")).byteValue();
/*      */         } 
/*      */       } 
/* 1060 */       resultSet.close();
/* 1061 */       setCache((byte)0);
/* 1062 */       setCache((byte)1);
/* 1063 */       setCache((byte)2);
/* 1064 */       setCache((byte)3);
/* 1065 */       setCache((byte)4);
/* 1066 */       MapManager.init();
/* 1067 */     } catch (SQLException ex) {
/* 1068 */       logger.debug("init", ex.toString());
/*      */     } 
/*      */   }
/*      */   
/*      */   public static User getUser(int userId) {
/* 1073 */     for (Session client : sessions) {
/* 1074 */       if (client.user != null && client.user.id == userId) {
/* 1075 */         return client.user;
/*      */       }
/*      */     } 
/* 1078 */     return null;
/*      */   }
/*      */   
/*      */   public static synchronized void sendToServer(Message ms) {
/* 1082 */     for (Session cl : sessions) {
/* 1083 */       if (cl != null && cl.user != null && cl.user.selectedCharacter != null)
/* 1084 */         cl.sendMessage(ms); 
/*      */     }  } private static void setCache(byte id) { try {
/*      */       ByteArrayOutputStream bas; DataOutputStream dos; byte[] ab; ByteArrayOutputStream arrow; DataOutputStream ds; ByteArrayOutputStream effect; ByteArrayOutputStream image; ByteArrayOutputStream parts; ByteArrayOutputStream skills; int i; byte[] abb; JSONArray jArr; int num;
/*      */       int j;
/*      */       Collection<ItemOptionTemplate> options;
/*      */       Collection<ItemEntry> entrys;
/*      */       Collection<SkillData.NClass> nClasss;
/* 1091 */       switch (id) {
/*      */         case 0:
/* 1093 */           bas = new ByteArrayOutputStream();
/* 1094 */           dos = new DataOutputStream(bas);
/* 1095 */           dos.writeByte(10);
/* 1096 */           dos.writeByte(maps.size());
/* 1097 */           for (TileMap map : maps) {
/* 1098 */             dos.writeUTF(map.name);
/*      */           }
/* 1100 */           dos.writeByte(npcs.size());
/* 1101 */           for (NpcTemplate npc : npcs) {
/* 1102 */             dos.writeUTF(npc.name);
/* 1103 */             dos.writeShort(npc.headId);
/* 1104 */             dos.writeShort(npc.bodyId);
/* 1105 */             dos.writeShort(npc.legId);
/* 1106 */             String[][] menu = npc.menu;
/* 1107 */             dos.writeByte(menu.length);
/* 1108 */             for (String[] m : menu) {
/* 1109 */               dos.writeByte(m.length);
/* 1110 */               for (String s : m) {
/* 1111 */                 dos.writeUTF(s);
/*      */               }
/*      */             } 
/*      */           } 
/* 1115 */           dos.writeByte(mobs.size());
/* 1116 */           for (MonsterTemplate mob : mobs) {
/* 1117 */             dos.writeByte(mob.type);
/* 1118 */             dos.writeUTF(mob.name);
/* 1119 */             dos.writeInt(mob.hp);
/* 1120 */             dos.writeByte(mob.rangeMove);
/* 1121 */             dos.writeByte(mob.speed);
/*      */           } 
/* 1123 */           ab = bas.toByteArray();
/* 1124 */           NinjaUtil.saveFile("cache/map", ab);
/* 1125 */           dos.close();
/* 1126 */           bas.close();
/*      */           break;
/*      */         
/*      */         case 1:
/* 1130 */           bas = new ByteArrayOutputStream();
/* 1131 */           dos = new DataOutputStream(bas);
/* 1132 */           dos.writeByte(55);
/* 1133 */           arrow = new ByteArrayOutputStream();
/* 1134 */           ds = new DataOutputStream(arrow);
/* 1135 */           ds.writeShort(arrs.size());
/* 1136 */           for (ArrowPaint arr : arrs) {
/* 1137 */             ds.writeShort(arr.id);
/* 1138 */             ds.writeShort(arr.imgId[0]);
/* 1139 */             ds.writeShort(arr.imgId[1]);
/* 1140 */             ds.writeShort(arr.imgId[2]);
/*      */           } 
/* 1142 */           ds.flush();
/* 1143 */           dos.writeInt((arrow.toByteArray()).length);
/* 1144 */           dos.write(arrow.toByteArray());
/* 1145 */           ds.close();
/* 1146 */           arrow.close();
/*      */           
/* 1148 */           effect = new ByteArrayOutputStream();
/* 1149 */           ds = new DataOutputStream(effect);
/* 1150 */           ds.writeShort(efs.size());
/* 1151 */           for (EffectCharPaint eff : efs) {
/* 1152 */             ds.writeShort(eff.idEf);
/* 1153 */             ds.writeByte(eff.arrEfInfo.length);
/* 1154 */             for (EffectInfoPaint eff2 : eff.arrEfInfo) {
/* 1155 */               ds.writeShort(eff2.idImg);
/* 1156 */               ds.writeByte(eff2.dx);
/* 1157 */               ds.writeByte(eff2.dy);
/*      */             } 
/*      */           } 
/* 1160 */           ds.flush();
/* 1161 */           dos.writeInt((effect.toByteArray()).length);
/* 1162 */           dos.write(effect.toByteArray());
/* 1163 */           ds.close();
/* 1164 */           effect.close();
/*      */           
/* 1166 */           image = new ByteArrayOutputStream();
/* 1167 */           ds = new DataOutputStream(image);
/* 1168 */           ds.writeShort(smallImg.size());
/* 1169 */           for (int[] img : smallImg) {
/* 1170 */             ds.writeByte(img[0]);
/* 1171 */             ds.writeShort(img[1]);
/* 1172 */             ds.writeShort(img[2]);
/* 1173 */             ds.writeShort(img[3]);
/* 1174 */             ds.writeShort(img[4]);
/*      */           } 
/* 1176 */           ds.flush();
/* 1177 */           dos.writeInt((image.toByteArray()).length);
/* 1178 */           dos.write(image.toByteArray());
/* 1179 */           ds.close();
/* 1180 */           image.close();
/*      */           
/* 1182 */           parts = new ByteArrayOutputStream();
/* 1183 */           ds = new DataOutputStream(parts);
/* 1184 */           ds.writeShort(Server.parts.size());
/* 1185 */           for (Part p : Server.parts) {
/* 1186 */             ds.writeByte(p.type);
/* 1187 */             for (PartImage pi : p.pi) {
/* 1188 */               ds.writeShort(pi.id);
/* 1189 */               ds.writeByte(pi.dx);
/* 1190 */               ds.writeByte(pi.dy);
/*      */             } 
/*      */           } 
/* 1193 */           ds.flush();
/* 1194 */           dos.writeInt((parts.toByteArray()).length);
/* 1195 */           dos.write(parts.toByteArray());
/* 1196 */           ds.close();
/* 1197 */           parts.close();
/*      */           
/* 1199 */           skills = new ByteArrayOutputStream();
/* 1200 */           ds = new DataOutputStream(skills);
/* 1201 */           ds.writeShort(sks.size());
/* 1202 */           for (SkillPaint p : sks) {
/* 1203 */             ds.writeShort(p.id);
/* 1204 */             ds.writeShort(p.effId);
/* 1205 */             ds.writeByte(p.numEff);
/* 1206 */             ds.writeByte(p.skillStand.length);
/* 1207 */             for (SkillInfoPaint skillStand : p.skillStand) {
/* 1208 */               ds.writeByte(skillStand.status);
/* 1209 */               ds.writeShort(skillStand.effS0Id);
/* 1210 */               ds.writeShort(skillStand.e0dx);
/* 1211 */               ds.writeShort(skillStand.e0dy);
/* 1212 */               ds.writeShort(skillStand.effS1Id);
/* 1213 */               ds.writeShort(skillStand.e1dx);
/* 1214 */               ds.writeShort(skillStand.e1dy);
/* 1215 */               ds.writeShort(skillStand.effS2Id);
/* 1216 */               ds.writeShort(skillStand.e2dx);
/* 1217 */               ds.writeShort(skillStand.e2dy);
/* 1218 */               ds.writeShort(skillStand.arrowId);
/* 1219 */               ds.writeShort(skillStand.adx);
/* 1220 */               ds.writeShort(skillStand.ady);
/*      */             } 
/* 1222 */             ds.writeByte(p.skillfly.length);
/* 1223 */             for (SkillInfoPaint skillfly : p.skillfly) {
/* 1224 */               ds.writeByte(skillfly.status);
/* 1225 */               ds.writeShort(skillfly.effS0Id);
/* 1226 */               ds.writeShort(skillfly.e0dx);
/* 1227 */               ds.writeShort(skillfly.e0dy);
/* 1228 */               ds.writeShort(skillfly.effS1Id);
/* 1229 */               ds.writeShort(skillfly.e1dx);
/* 1230 */               ds.writeShort(skillfly.e1dy);
/* 1231 */               ds.writeShort(skillfly.effS2Id);
/* 1232 */               ds.writeShort(skillfly.e2dx);
/* 1233 */               ds.writeShort(skillfly.e2dy);
/* 1234 */               ds.writeShort(skillfly.arrowId);
/* 1235 */               ds.writeShort(skillfly.adx);
/* 1236 */               ds.writeShort(skillfly.ady);
/*      */             } 
/*      */           } 
/* 1239 */           ds.flush();
/* 1240 */           dos.writeInt((skills.toByteArray()).length);
/* 1241 */           dos.write(skills.toByteArray());
/* 1242 */           ds.close();
/* 1243 */           skills.close();
/*      */           
/* 1245 */           dos.writeByte(tasks.length);
/* 1246 */           for (i = 0; i < tasks.length; i++) {
/* 1247 */             dos.writeByte((tasks[i]).length);
/* 1248 */             for (int a = 0; a < (tasks[i]).length; a++) {
/* 1249 */               dos.writeByte(tasks[i][a]);
/* 1250 */               dos.writeByte(mapTasks[i][a]);
/*      */             } 
/*      */           } 
/* 1253 */           dos.writeByte(exps.length);
/* 1254 */           for (long exp : exps) {
/* 1255 */             dos.writeLong(exp);
/*      */           }
/* 1257 */           dos.writeByte(crystals.length);
/* 1258 */           for (int k : crystals) {
/* 1259 */             dos.writeInt(k);
/*      */           }
/* 1261 */           dos.writeByte(upClothes.length);
/* 1262 */           for (int k : upClothes) {
/* 1263 */             dos.writeInt(k);
/*      */           }
/* 1265 */           dos.writeByte(upAdorns.length);
/* 1266 */           for (int k : upAdorns) {
/* 1267 */             dos.writeInt(k);
/*      */           }
/* 1269 */           dos.writeByte(upWeapons.length);
/* 1270 */           for (int k : upWeapons) {
/* 1271 */             dos.writeInt(k);
/*      */           }
/* 1273 */           dos.writeByte(coinUpCrystals.length);
/* 1274 */           for (int k : coinUpCrystals) {
/* 1275 */             dos.writeInt(k);
/*      */           }
/* 1277 */           dos.writeByte(coinUpClothes.length);
/* 1278 */           for (int k : coinUpClothes) {
/* 1279 */             dos.writeInt(k);
/*      */           }
/* 1281 */           dos.writeByte(coinUpAdorns.length);
/* 1282 */           for (int k : coinUpAdorns) {
/* 1283 */             dos.writeInt(k);
/*      */           }
/* 1285 */           dos.writeByte(coinUpWeapons.length);
/* 1286 */           for (int k : coinUpWeapons) {
/* 1287 */             dos.writeInt(k);
/*      */           }
/* 1289 */           dos.writeByte(goldUps.length);
/* 1290 */           for (int k : goldUps) {
/* 1291 */             dos.writeInt(k);
/*      */           }
/* 1293 */           dos.writeByte(maxPercents.length);
/* 1294 */           for (int k : maxPercents) {
/* 1295 */             dos.writeInt(k);
/*      */           }
/* 1297 */           dos.writeByte(Effect.effTemplates.size());
/* 1298 */           for (EffectTemplate eff : Effect.effTemplates) {
/* 1299 */             dos.writeByte(eff.id);
/* 1300 */             dos.writeByte(eff.type);
/* 1301 */             dos.writeUTF(eff.name);
/* 1302 */             dos.writeShort(eff.icon);
/*      */           } 
/* 1304 */           abb = bas.toByteArray();
/* 1305 */           NinjaUtil.saveFile("cache/data", abb);
/* 1306 */           dos.close();
/* 1307 */           bas.close();
/*      */           break;
/*      */         
/*      */         case 2:
/* 1311 */           bas = new ByteArrayOutputStream();
/* 1312 */           dos = new DataOutputStream(bas);
/* 1313 */           dos.writeByte(55);
/* 1314 */           dos.writeByte(10);
/* 1315 */           dos.writeByte(10);
/* 1316 */           dos.writeByte(76);
/* 1317 */           jArr = (JSONArray)moto.get("head_jump");
/* 1318 */           num = jArr.size();
/* 1319 */           dos.writeByte(num);
/* 1320 */           for (j = 0; j < num; j++) {
/* 1321 */             JSONObject part = (JSONObject)jArr.get(j);
/* 1322 */             JSONArray item = (JSONArray)part.get("item");
/* 1323 */             int numItem = item.size() * 3 + 2;
/* 1324 */             dos.writeByte(numItem);
/* 1325 */             dos.writeShort(((Long)part.get("id")).shortValue());
/* 1326 */             dos.writeShort(((Long)part.get("idSmall")).shortValue());
/* 1327 */             for (int a = 0; j < numItem - 2; j += 3) {
/* 1328 */               JSONObject obj = (JSONObject)item.get(a);
/* 1329 */               dos.writeShort(((Long)obj.get("id")).shortValue());
/* 1330 */               dos.writeShort(((Long)obj.get("dx")).shortValue());
/* 1331 */               dos.writeShort(((Long)obj.get("dy")).shortValue());
/*      */             } 
/*      */           } 
/* 1334 */           jArr = (JSONArray)moto.get("head_normal");
/* 1335 */           num = jArr.size();
/* 1336 */           for (j = 0; j < num; j++) {
/* 1337 */             JSONObject part = (JSONObject)jArr.get(j);
/* 1338 */             JSONArray item = (JSONArray)part.get("item");
/* 1339 */             int numItem = item.size() * 3 + 2;
/* 1340 */             dos.writeByte(numItem);
/* 1341 */             dos.writeShort(((Long)part.get("id")).shortValue());
/* 1342 */             dos.writeShort(((Long)part.get("idSmall")).shortValue());
/* 1343 */             for (int a = 0; j < numItem - 2; j += 3) {
/* 1344 */               JSONObject obj = (JSONObject)item.get(a);
/* 1345 */               dos.writeShort(((Long)obj.get("id")).shortValue());
/* 1346 */               dos.writeShort(((Long)obj.get("dx")).shortValue());
/* 1347 */               dos.writeShort(((Long)obj.get("dy")).shortValue());
/*      */             } 
/*      */           } 
/* 1350 */           jArr = (JSONArray)moto.get("head_boc_dau");
/* 1351 */           num = jArr.size();
/* 1352 */           for (j = 0; j < num; j++) {
/* 1353 */             JSONObject part = (JSONObject)jArr.get(j);
/* 1354 */             JSONArray item = (JSONArray)part.get("item");
/* 1355 */             int numItem = item.size() * 3 + 2;
/* 1356 */             dos.writeByte(numItem);
/* 1357 */             dos.writeShort(((Long)part.get("id")).shortValue());
/* 1358 */             dos.writeShort(((Long)part.get("idSmall")).shortValue());
/* 1359 */             for (int a = 0; j < numItem - 2; j += 3) {
/* 1360 */               JSONObject obj = (JSONObject)item.get(a);
/* 1361 */               dos.writeShort(((Long)obj.get("id")).shortValue());
/* 1362 */               dos.writeShort(((Long)obj.get("dx")).shortValue());
/* 1363 */               dos.writeShort(((Long)obj.get("dy")).shortValue());
/*      */             } 
/*      */           } 
/* 1366 */           jArr = (JSONArray)moto.get("body_jump");
/* 1367 */           num = jArr.size() * 2;
/* 1368 */           dos.writeByte(num);
/* 1369 */           for (j = 0; j < num; j += 2) {
/* 1370 */             JSONObject obj = (JSONObject)jArr.get(j / 2);
/* 1371 */             dos.writeShort(((Long)obj.get("id")).shortValue());
/* 1372 */             dos.writeShort(((Long)obj.get("idSmall")).shortValue());
/*      */           } 
/*      */           
/* 1375 */           jArr = (JSONArray)moto.get("body_jump");
/* 1376 */           num = jArr.size();
/* 1377 */           dos.writeByte(num);
/* 1378 */           for (j = 0; j < num; j++) {
/* 1379 */             JSONObject part = (JSONObject)jArr.get(j);
/* 1380 */             JSONArray item = (JSONArray)part.get("item");
/* 1381 */             int numItem = item.size() * 3 + 2;
/* 1382 */             dos.writeByte(numItem);
/* 1383 */             dos.writeShort(((Long)part.get("id")).shortValue());
/* 1384 */             dos.writeShort(((Long)part.get("idSmall")).shortValue());
/* 1385 */             for (int a = 0; j < numItem - 2; j += 3) {
/* 1386 */               JSONObject obj = (JSONObject)item.get(a);
/* 1387 */               dos.writeShort(((Long)obj.get("id")).shortValue());
/* 1388 */               dos.writeShort(((Long)obj.get("dx")).shortValue());
/* 1389 */               dos.writeShort(((Long)obj.get("dy")).shortValue());
/*      */             } 
/*      */           } 
/* 1392 */           jArr = (JSONArray)moto.get("body_normal");
/* 1393 */           for (j = 0; j < num; j++) {
/* 1394 */             JSONObject part = (JSONObject)jArr.get(j);
/* 1395 */             JSONArray item = (JSONArray)part.get("item");
/* 1396 */             int numItem = item.size() * 3 + 2;
/* 1397 */             dos.writeByte(numItem);
/* 1398 */             dos.writeShort(((Long)part.get("id")).shortValue());
/* 1399 */             dos.writeShort(((Long)part.get("idSmall")).shortValue());
/* 1400 */             for (int a = 0; j < numItem - 2; j += 3) {
/* 1401 */               JSONObject obj = (JSONObject)item.get(a);
/* 1402 */               dos.writeShort(((Long)obj.get("id")).shortValue());
/* 1403 */               dos.writeShort(((Long)obj.get("dx")).shortValue());
/* 1404 */               dos.writeShort(((Long)obj.get("dy")).shortValue());
/*      */             } 
/*      */           } 
/* 1407 */           jArr = (JSONArray)moto.get("body_dau");
/* 1408 */           for (j = 0; j < num; j++) {
/* 1409 */             JSONObject part = (JSONObject)jArr.get(j);
/* 1410 */             JSONArray item = (JSONArray)part.get("item");
/* 1411 */             int numItem = item.size() * 3 + 2;
/* 1412 */             dos.writeByte(numItem);
/* 1413 */             dos.writeShort(((Long)part.get("id")).shortValue());
/* 1414 */             dos.writeShort(((Long)part.get("idSmall")).shortValue());
/* 1415 */             for (int a = 0; j < numItem - 2; j += 3) {
/* 1416 */               JSONObject obj = (JSONObject)item.get(a);
/* 1417 */               dos.writeShort(((Long)obj.get("id")).shortValue());
/* 1418 */               dos.writeShort(((Long)obj.get("dx")).shortValue());
/* 1419 */               dos.writeShort(((Long)obj.get("dy")).shortValue());
/*      */             } 
/*      */           } 
/* 1422 */           ab = bas.toByteArray();
/* 1423 */           NinjaUtil.saveFile("cache/version", ab);
/* 1424 */           dos.close();
/* 1425 */           bas.close();
/*      */           break;
/*      */         
/*      */         case 3:
/* 1429 */           bas = new ByteArrayOutputStream();
/* 1430 */           dos = new DataOutputStream(bas);
/* 1431 */           dos.writeByte(76);
/* 1432 */           options = ItemData.getOptions();
/* 1433 */           dos.writeByte(options.size());
/* 1434 */           for (ItemOptionTemplate item : options) {
/* 1435 */             dos.writeUTF(item.name);
/* 1436 */             dos.writeByte(item.type);
/*      */           } 
/* 1438 */           entrys = ItemData.getEntrys();
/* 1439 */           dos.writeShort(entrys.size());
/* 1440 */           for (ItemEntry item : entrys) {
/* 1441 */             dos.writeByte(item.type);
/* 1442 */             dos.writeByte(item.gender);
/* 1443 */             dos.writeUTF(item.name);
/* 1444 */             dos.writeUTF(item.description);
/* 1445 */             dos.writeByte(item.level);
/* 1446 */             dos.writeShort(item.icon);
/* 1447 */             dos.writeShort(item.part);
/* 1448 */             dos.writeBoolean(item.isUpToUp);
/*      */           } 
/* 1450 */           ab = bas.toByteArray();
/* 1451 */           NinjaUtil.saveFile("cache/item", ab);
/* 1452 */           dos.close();
/* 1453 */           bas.close();
/*      */           break;
/*      */         
/*      */         case 4:
/* 1457 */           bas = new ByteArrayOutputStream();
/* 1458 */           dos = new DataOutputStream(bas);
/* 1459 */           dos.writeByte(10);
/* 1460 */           dos.writeByte(SkillData.optionTemplates.size());
/* 1461 */           for (SkillOptionTemplate optionTemplate : SkillData.optionTemplates.values()) {
/* 1462 */             dos.writeUTF(optionTemplate.name);
/*      */           }
/* 1464 */           dos.writeByte(SkillData.nClasss.size());
/* 1465 */           nClasss = SkillData.nClasss.values();
/* 1466 */           for (SkillData.NClass nClass : nClasss) {
/* 1467 */             dos.writeUTF(nClass.name);
/* 1468 */             dos.writeByte(nClass.templates.size());
/* 1469 */             Collection<SkillTemplate> templates = nClass.templates.values();
/* 1470 */             for (SkillTemplate template : templates) {
/* 1471 */               dos.writeByte(template.id);
/* 1472 */               dos.writeUTF(template.name);
/* 1473 */               dos.writeByte(template.maxPoint);
/* 1474 */               dos.writeByte(template.type);
/* 1475 */               dos.writeShort(template.iconId);
/* 1476 */               dos.writeUTF(template.description);
/* 1477 */               dos.writeByte(template.skills.size());
/* 1478 */               for (Skill skill : template.skills) {
/* 1479 */                 dos.writeShort(skill.skillId);
/* 1480 */                 dos.writeByte(skill.point);
/* 1481 */                 dos.writeByte(skill.level);
/* 1482 */                 dos.writeShort(skill.manaUse);
/* 1483 */                 dos.writeInt(skill.coolDown);
/* 1484 */                 dos.writeShort(skill.dx);
/* 1485 */                 dos.writeShort(skill.dy);
/* 1486 */                 dos.writeByte(skill.maxFight);
/* 1487 */                 dos.writeByte(skill.options.length);
/* 1488 */                 for (SkillOption option : skill.options) {
/* 1489 */                   dos.writeShort(option.param);
/* 1490 */                   dos.writeByte(option.optionTemplate.id);
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */           } 
/* 1495 */           ab = bas.toByteArray();
/* 1496 */           NinjaUtil.saveFile("cache/skill", ab);
/* 1497 */           dos.close();
/* 1498 */           bas.close();
/*      */           break;
/*      */       } 
/* 1501 */     } catch (IOException ex) {
/* 1502 */       logger.debug("setCache", ex.toString());
/*      */     }  }
/*      */ 
/*      */   
/*      */   public static void start() {
/* 1507 */     logger.log("Start socket post=14444");
/*      */     try {
/* 1509 */       luckyDrawNormal = new LuckyDraw("Vòng xoay thường!", (byte)0);
/* 1510 */       luckyDrawVIP = new LuckyDraw("Vòng xoay vip", (byte)1);
/* 1511 */       (new Thread(luckyDrawNormal)).start();
/* 1512 */       (new Thread(luckyDrawVIP)).start();
/* 1513 */       sessions = new ArrayList<>();
/* 1514 */       server = new ServerSocket(14444);
/* 1515 */       id = 0;
/* 1516 */       start = true;
/* 1517 */       logger.log("Start server Success!");
/* 1518 */       while (start) {
/*      */         try {
/* 1520 */           Socket client = server.accept();
/* 1521 */           Session cl = new Session(client, ++id);
/* 1522 */           sessions.add(cl);
/* 1523 */         } catch (IOException e) {
/* 1524 */           logger.debug("start", e.toString());
/*      */         } 
/*      */       } 
/* 1527 */     } catch (IOException e) {
/* 1528 */       e.printStackTrace();
/*      */     } 
/*      */   }
/*      */   
/*      */   public static void stop() {
/* 1533 */     if (start) {
/* 1534 */       close();
/* 1535 */       LuckyDraw.running = false;
/* 1536 */       Map.running = false;
/* 1537 */       start = false;
/* 1538 */       System.gc();
/*      */     } 
/*      */   }
/*      */   
/*      */   public static void close() {
/*      */     try {
/* 1544 */       server.close();
/* 1545 */       server = null;
/* 1546 */       while (sessions.size() > 0) {
/* 1547 */         Session c = sessions.get(0);
/* 1548 */         c.close();
/*      */       } 
/* 1550 */       sessions = null;
/* 1551 */       Connect.close();
/* 1552 */       System.gc();
/* 1553 */       logger.log("End socket");
/* 1554 */     } catch (IOException e) {
/* 1555 */       logger.debug("close", e.toString());
/*      */     } 
/*      */   }
/*      */   
/*      */   public static void removeClient(Session cl) {
/* 1560 */     synchronized (sessions) {
/* 1561 */       sessions.remove(cl);
/* 1562 */       logger.log("Disconnect client: " + cl);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\server\Server.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */