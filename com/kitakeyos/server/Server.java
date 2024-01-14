package com.kitakeyos.server;

import com.kitakeyos.data.ItemData;
import com.kitakeyos.data.SkillData;
import com.kitakeyos.data.StoreData;
import com.kitakeyos.db.Connect;
import com.kitakeyos.io.Message;
import com.kitakeyos.io.Session;
import com.kitakeyos.object.ArrowPaint;
import com.kitakeyos.object.Effect;
import com.kitakeyos.object.EffectCharPaint;
import com.kitakeyos.object.EffectInfoPaint;
import com.kitakeyos.object.Frame;
import com.kitakeyos.object.ImageInfo;
import com.kitakeyos.object.ItemEntry;
import com.kitakeyos.object.ItemStore;
import com.kitakeyos.object.Part;
import com.kitakeyos.object.PartImage;
import com.kitakeyos.object.Skill;
import com.kitakeyos.object.SkillInfoPaint;
import com.kitakeyos.object.SkillPaint;
import com.kitakeyos.object.TileMap;
import com.kitakeyos.object.Waypoint;
import com.kitakeyos.option.SkillOption;
import com.kitakeyos.template.EffectTemplate;
import com.kitakeyos.template.ItemOptionTemplate;
import com.kitakeyos.template.MonsterTemplate;
import com.kitakeyos.template.NpcTemplate;
import com.kitakeyos.template.SkillOptionTemplate;
import com.kitakeyos.template.SkillTemplate;
import com.kitakeyos.util.Logger;
import com.kitakeyos.util.NinjaUtil;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Server {
	public static ArrayList<Session> sessions;
	public static ServerSocket server;
	public static boolean start;
	public static int id;
	public static ArrayList<TileMap> maps;
	public static ArrayList<NpcTemplate> npcs;
	public static ArrayList<MonsterTemplate> mobs;
	public static ArrayList<SkillPaint> sks;
	public static ArrayList<Part> parts;
	public static ArrayList<EffectCharPaint> efs;
	public static ArrayList<ArrowPaint> arrs;
	public static ArrayList<int[]> smallImg;
	public static long[] exps;
	public static int[] crystals;
	public static int[] upClothes;
	public static int[] upAdorns;
	public static int[] upWeapons;
	public static int[] coinUpCrystals;
	public static int[] coinUpClothes;
	public static int[] coinUpAdorns;
	public static int[] coinUpWeapons;
	public static int[] goldUps;
	public static int[] maxPercents;
	public static byte[][] tasks;
	public static byte[][] mapTasks;
	public static LuckyDraw luckyDrawNormal;
	public static LuckyDraw luckyDrawVIP;
	public static JSONObject moto;
	public static Logger logger = new Logger(Server.class);

	public static void init() {
		start = false;
		Connect.create();
		maps = new ArrayList<>();
		npcs = new ArrayList<>();
		mobs = new ArrayList<>();
		moto = new JSONObject();
		sks = new ArrayList<>();
		parts = new ArrayList<>();
		efs = new ArrayList<>();
		arrs = new ArrayList<>();
		parts = new ArrayList<>();
		smallImg = (ArrayList) new ArrayList<>();
		try {
			int id = 0;
			ResultSet resultSet = Connect.stat.executeQuery("SELECT * FROM `map`;");
			while (resultSet.next()) {
				TileMap map = new TileMap();
				map.id = resultSet.getInt("id");
				map.name = resultSet.getString("name");
				map.tileId = resultSet.getByte("tileId");
				map.bgId = resultSet.getByte("bgId");
				map.type = resultSet.getByte("type");
				map.loadMapFromResource();
				map.npcs = new ArrayList();
				JSONArray jArr = (JSONArray) JSONValue.parse(resultSet.getString("npc"));
				int len = jArr.size();
				int j;
				for (j = 0; j < len; j++) {
					JSONObject obj = (JSONObject) jArr.get(j);
					NpcTemplate npc = new NpcTemplate();
					npc.status = ((Long) obj.get("status")).byteValue();
					npc.x = ((Long) obj.get("x")).shortValue();
					npc.y = ((Long) obj.get("y")).shortValue();
					npc.templateId = (short) (byte) ((Long) obj.get("templateId")).shortValue();
					map.npcs.add(npc);
				}
				map.monsterCoordinates = new ArrayList();
				jArr = (JSONArray) JSONValue.parse(resultSet.getString("monster"));
				len = jArr.size();
				for (j = 0; j < len; j++) {
					JSONObject obj = (JSONObject) jArr.get(j);
					short templateId = ((Long) obj.get("templateId")).shortValue();
					short x = ((Long) obj.get("x")).shortValue();
					short y = ((Long) obj.get("y")).shortValue();
					map.monsterCoordinates.add(new TileMap.MonsterCoordinate(templateId, x, y));
				}
				map.waypoints = new ArrayList();
				jArr = (JSONArray) JSONValue.parse(resultSet.getString("waypoint"));
				len = jArr.size();
				for (j = 0; j < len; j++) {
					JSONArray jArr2 = (JSONArray) jArr.get(j);
					Waypoint waypoint = new Waypoint();
					waypoint.mapId = ((Long) jArr2.get(0)).shortValue();
					waypoint.minX = ((Long) jArr2.get(1)).shortValue();
					waypoint.minY = ((Long) jArr2.get(2)).shortValue();
					waypoint.maxX = ((Long) jArr2.get(3)).shortValue();
					waypoint.maxY = ((Long) jArr2.get(4)).shortValue();
					map.waypoints.add(waypoint);
				}
				maps.add(map);
			}
			resultSet.close();
			logger.log("Map: " + maps.size());
			resultSet = Connect.stat.executeQuery("SELECT * FROM `npc`;");
			while (resultSet.next()) {
				NpcTemplate npc = new NpcTemplate();
				npc.id = resultSet.getInt("id");
				npc.name = resultSet.getString("name");
				npc.headId = resultSet.getShort("headId");
				npc.bodyId = resultSet.getShort("bodyId");
				npc.legId = resultSet.getShort("legId");
				JSONArray jArr = (JSONArray) JSONValue.parse(resultSet.getString("menu"));
				int size = jArr.size();
				npc.menu = new String[size][];
				for (int j = 0; j < size; j++) {
					JSONArray jArr2 = (JSONArray) JSONValue.parse(jArr.get(j).toString());
					int size2 = jArr2.size();
					npc.menu[j] = new String[size2];
					for (int a = 0; a < size2; a++) {
						npc.menu[j][a] = jArr2.get(a).toString();
					}
				}
				npcs.add(npc);
			}
			resultSet.close();
			logger.log("Npc: " + npcs.size());
			resultSet = Connect.stat.executeQuery("SELECT * FROM `monster`;");
			while (resultSet.next()) {
				MonsterTemplate mob = new MonsterTemplate();
				mob.id = resultSet.getInt("id");
				mob.name = resultSet.getString("name");
				mob.type = resultSet.getByte("type");
				mob.hp = resultSet.getInt("hp");
				mob.level = resultSet.getShort("level");
				mob.rangeMove = resultSet.getByte("rangeMove");
				mob.speed = resultSet.getByte("speed");
				mob.numberImage = resultSet.getByte("numberImage");
				mob.typeFly = resultSet.getByte("typeFly");
				JSONArray moves = (JSONArray) JSONValue.parse(resultSet.getString("move"));
				int size = moves.size();
				mob.frameBossMove = new byte[size];
				for (int j = 0; j < size; j++) {
					mob.frameBossMove[j] = ((Long) moves.get(j)).byteValue();
				}
				JSONArray attacks = (JSONArray) JSONValue.parse(resultSet.getString("attack"));
				size = attacks.size();
				mob.frameBossAttack = new byte[size][];
				for (int k = 0; k < size; k++) {
					JSONArray jArr = (JSONArray) attacks.get(k);
					int size2 = jArr.size();
					mob.frameBossAttack[k] = new byte[size2];
					for (int i1 = 0; i1 < size2; i1++) {
						mob.frameBossAttack[k][i1] = ((Long) jArr.get(i1)).byteValue();
					}
				}
				JSONArray imgInfos = (JSONArray) JSONValue.parse(resultSet.getString("image"));
				size = imgInfos.size();
				mob.imgInfo = new ImageInfo[size];
				for (int m = 0; m < size; m++) {
					JSONObject job = (JSONObject) imgInfos.get(m);
					ImageInfo img = new ImageInfo();
					img.id = ((Long) job.get("id")).intValue();
					img.x0 = ((Long) job.get("x")).intValue();
					img.y0 = ((Long) job.get("y")).intValue();
					img.w = ((Long) job.get("width")).intValue();
					img.h = ((Long) job.get("height")).intValue();
					mob.imgInfo[m] = img;
				}
				JSONArray frameBosss = (JSONArray) JSONValue.parse(resultSet.getString("frame"));
				size = frameBosss.size();
				mob.frameBoss = new Frame[size];
				for (int n = 0; n < size; n++) {
					JSONObject job = (JSONObject) frameBosss.get(n);
					JSONArray dx = (JSONArray) job.get("dx");
					JSONArray dy = (JSONArray) job.get("dy");
					JSONArray img = (JSONArray) job.get("img");
					Frame frame = new Frame();
					frame.dx = new int[dx.size()];
					frame.dy = new int[dy.size()];
					frame.idImg = new int[img.size()];
					for (int i1 = 0; i1 < frame.dx.length; i1++) {
						frame.dx[i1] = ((Long) dx.get(i1)).intValue();
						frame.dy[i1] = ((Long) dy.get(i1)).intValue();
						frame.idImg[i1] = ((Long) img.get(i1)).intValue();
					}
					mob.frameBoss[n] = frame;
				}
				mobs.add(mob);
			}
			resultSet.close();
			logger.log("Mob: " + mobs.size());
			resultSet = Connect.stat.executeQuery("SELECT * FROM `task`;");
			resultSet.last();
			int num = resultSet.getRow();
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `task`;");
			tasks = new byte[num][];
			mapTasks = new byte[num][];
			int i = 0;
			while (resultSet.next()) {
				JSONArray jArr = (JSONArray) JSONValue.parse(resultSet.getString("tasks"));
				JSONArray jArr2 = (JSONArray) JSONValue.parse(resultSet.getString("tasks"));
				tasks[i] = new byte[jArr.size()];
				mapTasks[i] = new byte[jArr.size()];
				for (int a = 0; a < (tasks[i]).length; a++) {
					tasks[i][a] = ((Long) jArr.get(a)).byteValue();
					mapTasks[i][a] = ((Long) jArr2.get(a)).byteValue();
				}
				i++;
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `config`;");
			while (resultSet.next()) {
				String name = resultSet.getString("name");
				if (name.equals("moto")) {
					moto = (JSONObject) JSONValue.parse(resultSet.getString("value"));
					continue;
				}
				JSONArray value = (JSONArray) JSONValue.parse(resultSet.getString("value"));
				if (name.equals("exp")) {
					exps = new long[value.size()];
					for (i = 0; i < exps.length; i++) {
						exps[i] = ((Long) value.get(i)).longValue();
					}
				}
				if (name.equals("crystal")) {
					crystals = new int[value.size()];
					for (i = 0; i < crystals.length; i++) {
						crystals[i] = ((Long) value.get(i)).intValue();
					}
				}
				if (name.equals("upClothe")) {
					upClothes = new int[value.size()];
					for (i = 0; i < upClothes.length; i++) {
						upClothes[i] = ((Long) value.get(i)).intValue();
					}
				}
				if (name.equals("upAdorn")) {
					upAdorns = new int[value.size()];
					for (i = 0; i < upAdorns.length; i++) {
						upAdorns[i] = ((Long) value.get(i)).intValue();
					}
				}
				if (name.equals("upWeapon")) {
					upWeapons = new int[value.size()];
					for (i = 0; i < upWeapons.length; i++) {
						upWeapons[i] = ((Long) value.get(i)).intValue();
					}
				}
				if (name.equals("coinUpCrystal")) {
					coinUpCrystals = new int[value.size()];
					for (i = 0; i < coinUpCrystals.length; i++) {
						coinUpCrystals[i] = ((Long) value.get(i)).intValue();
					}
				}
				if (name.equals("coinUpClothe")) {
					coinUpClothes = new int[value.size()];
					for (i = 0; i < coinUpClothes.length; i++) {
						coinUpClothes[i] = ((Long) value.get(i)).intValue();
					}
				}
				if (name.equals("coinUpAdorn")) {
					coinUpAdorns = new int[value.size()];
					for (i = 0; i < coinUpAdorns.length; i++) {
						coinUpAdorns[i] = ((Long) value.get(i)).intValue();
					}
				}
				if (name.equals("coinUpWeapon")) {
					coinUpWeapons = new int[value.size()];
					for (i = 0; i < coinUpWeapons.length; i++) {
						coinUpWeapons[i] = ((Long) value.get(i)).intValue();
					}
				}
				if (name.equals("goldUp")) {
					goldUps = new int[value.size()];
					for (i = 0; i < goldUps.length; i++) {
						goldUps[i] = ((Long) value.get(i)).intValue();
					}
				}
				if (name.equals("maxPercent")) {
					maxPercents = new int[value.size()];
					for (i = 0; i < maxPercents.length; i++) {
						maxPercents[i] = ((Long) value.get(i)).intValue();
					}
				}
			}

			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `effect`;");
			Effect.effTemplates = new ArrayList();
			while (resultSet.next()) {
				EffectTemplate eff = new EffectTemplate();
				eff.id = resultSet.getByte("id");
				eff.name = resultSet.getString("name");
				eff.type = resultSet.getByte("type");
				eff.icon = resultSet.getShort("icon");
				Effect.effTemplates.add(eff);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `item`;");
			while (resultSet.next()) {
				ItemEntry item = new ItemEntry();
				item.id = resultSet.getInt("id");
				item.name = resultSet.getString("name");
				item.type = resultSet.getByte("type");
				item.gender = resultSet.getByte("gender");
				item.level = resultSet.getShort("level");
				item.part = resultSet.getShort("part");
				item.icon = resultSet.getShort("icon");
				item.description = resultSet.getString("description");
				item.isUpToUp = resultSet.getBoolean("isUpToUp");
				ItemData.put(item.id, item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `item_option`;");
			while (resultSet.next()) {
				ItemOptionTemplate item = new ItemOptionTemplate();
				item.id = resultSet.getInt("id");
				item.name = resultSet.getString("name");
				item.type = resultSet.getByte("type");
				ItemData.put(item.id, item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `skill_option`;");
			SkillData.optionTemplates = new HashMap<>();
			while (resultSet.next()) {
				SkillOptionTemplate template = new SkillOptionTemplate();
				template.id = resultSet.getInt("id");
				template.name = resultSet.getString("name");
				SkillData.optionTemplates.put(Integer.valueOf(template.id), template);
			}
			resultSet.close();
			SkillData.nClasss = new HashMap<>();
			for (int g = 0; g < 7; g++) {
				resultSet = Connect.stat.executeQuery("SELECT * FROM `skill` WHERE `class` = " + g + ";");
				SkillData.NClass nClass = new SkillData.NClass();
				nClass.classId = g;
				switch (nClass.classId) {
				case 1:
					nClass.name = "Ninja kiếm";
					break;

				case 2:
					nClass.name = "Ninja phi tiêu";
					break;

				case 3:
					nClass.name = "Ninja kunai";
					break;

				case 4:
					nClass.name = "Ninja cung";
					break;

				case 5:
					nClass.name = "Ninja đao";
					break;

				case 6:
					nClass.name = "Ninja quạt";
					break;

				default:
					nClass.name = "Chưa vào trường";
					break;
				}
				nClass.templates = new HashMap<>();
				while (resultSet.next()) {
					SkillTemplate template = new SkillTemplate();
					template.id = resultSet.getInt("id");
					template.name = resultSet.getString("name");
					template.maxPoint = resultSet.getByte("maxPoint");
					template.type = resultSet.getByte("type");
					template.iconId = resultSet.getShort("iconId");
					template.description = resultSet.getString("description");
					template.skills = new ArrayList();
					JSONArray skills = (JSONArray) JSONValue.parse(resultSet.getString("skills"));
					for (int a = 0; a < skills.size(); a++) {
						JSONObject obj = (JSONObject) skills.get(a);
						Skill skill = new Skill();
						skill.skillId = ((Long) obj.get("skillId")).intValue();
						skill.point = ((Long) obj.get("point")).byteValue();
						skill.coolDown = ((Long) obj.get("coolDown")).intValue();
						skill.level = ((Long) obj.get("level")).byteValue();
						skill.maxFight = ((Long) obj.get("maxFight")).byteValue();
						skill.manaUse = ((Long) obj.get("manaUse")).shortValue();
						skill.dx = ((Long) obj.get("dx")).shortValue();
						skill.dy = ((Long) obj.get("dy")).shortValue();
						JSONArray jArr = (JSONArray) obj.get("options");
						int len = jArr.size();
						skill.options = new SkillOption[len];
						for (int j = 0; j < len; j++) {
							JSONObject o = (JSONObject) jArr.get(j);
							int templateId = ((Long) o.get("id")).intValue();
							int param = ((Long) o.get("param")).intValue();
							skill.options[j] = new SkillOption(templateId, param);
						}
						template.skills.add(skill);
					}
					nClass.templates.put(Integer.valueOf(template.id), template);
				}
				resultSet.close();
				SkillData.nClasss.put(Integer.valueOf(g), nClass);
			}
			resultSet = Connect.stat.executeQuery("SELECT * FROM `weapon_store`;");
			int index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.sys = resultSet.getByte("sys");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.VU_KHI.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `hat_store` WHERE `gender` = 1;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.sys = resultSet.getByte("sys");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.NON_NAM.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `hat_store` WHERE `gender` = 0;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.sys = resultSet.getByte("sys");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.NON_NU.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `shirt_store` WHERE `gender` = 1;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.sys = resultSet.getByte("sys");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.AO_NAM.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `shirt_store` WHERE `gender` = 0;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.sys = resultSet.getByte("sys");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.AO_NU.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `glove_store` WHERE `gender` = 1;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.sys = resultSet.getByte("sys");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.GANG_NAM.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `glove_store` WHERE `gender` = 0;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.sys = resultSet.getByte("sys");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.GANG_NU.add(item);
			}
			resultSet.close();

			resultSet = Connect.stat.executeQuery("SELECT * FROM `pant_store` WHERE `gender` = 1;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.sys = resultSet.getByte("sys");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.QUAN_NAM.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `pant_store` WHERE `gender` = 0;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.sys = resultSet.getByte("sys");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.QUAN_NU.add(item);
			}
			resultSet.close();

			resultSet = Connect.stat.executeQuery("SELECT * FROM `shoe_store` WHERE `gender` = 1;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.sys = resultSet.getByte("sys");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.GIAY_NAM.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `shoe_store` WHERE `gender` = 0;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.sys = resultSet.getByte("sys");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.GIAY_NU.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `store`;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.sys = resultSet.getByte("sys");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.LINH_TINH.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `book_store`;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.expire = resultSet.getLong("expire");
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.SACH.add(item);
			}
			resultSet.close();

			resultSet = Connect.stat.executeQuery("SELECT * FROM `necklace_store`;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.sys = resultSet.getByte("sys");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.DAY_CHUYEN.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `pearl_store`;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.sys = resultSet.getByte("sys");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.NGOC_BOI.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `ring_store`;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.sys = resultSet.getByte("sys");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.NHAN.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `spell_store`;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.sys = resultSet.getByte("sys");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.BUA.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `fashion_store`;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.sys = resultSet.getByte("sys");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.THOI_TRANG.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `clan_store`;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.sys = resultSet.getByte("sys");
				item.expire = resultSet.getLong("expire");
				JSONArray option_max = (JSONArray) JSONValue.parse(resultSet.getString("options"));
				item.option_max = new int[option_max.size()][2];
				for (int a = 0; a < item.option_max.length; a++) {
					JSONArray jArr2 = (JSONArray) option_max.get(a);
					item.option_max[a][0] = ((Long) jArr2.get(0)).intValue();
					item.option_max[a][1] = ((Long) jArr2.get(1)).intValue();
				}
				item.option_min = NinjaUtil.getOptionShop(item.option_max);
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.GIA_TOC.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `grocery_store` WHERE `isLock` = 0;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.isLock = false;
				item.expire = resultSet.getLong("expire");
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.THUC_AN.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `grocery_store` WHERE `isLock` = 1;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.isLock = true;
				item.expire = resultSet.getLong("expire");
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.THUC_AN_KHOA.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `stack_store` WHERE `isLock` = 0;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.isLock = true;
				item.expire = resultSet.getLong("expire");
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.DUOC_PHAM.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `stack_store` WHERE `isLock` = 1;");
			index = 0;
			while (resultSet.next()) {
				ItemStore item = new ItemStore();
				item.id = resultSet.getInt("id");
				item.templateId = resultSet.getShort("templateId");
				item.xu = resultSet.getInt("xu");
				item.luong = resultSet.getInt("luong");
				item.yen = resultSet.getInt("yen");
				item.isLock = true;
				item.expire = resultSet.getLong("expire");
				item.index = index++;
				item.entry = ItemData.getItemEntryById(item.templateId);
				StoreData.DUOC_PHAM_KHOA.add(item);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `nj_skill`;");
			while (resultSet.next()) {
				SkillPaint p = new SkillPaint();
				p.id = resultSet.getShort("skillId");
				p.effId = resultSet.getShort("effId");
				p.numEff = resultSet.getByte("numEff");
				JSONArray jA = (JSONArray) JSONValue.parse(resultSet.getString("skillStand"));
				p.skillStand = new SkillInfoPaint[jA.size()];
				int k;
				for (k = 0; k < p.skillStand.length; k++) {
					JSONObject o = (JSONObject) jA.get(k);
					p.skillStand[k] = new SkillInfoPaint();
					(p.skillStand[k]).status = ((Long) o.get("status")).byteValue();
					(p.skillStand[k]).effS0Id = ((Long) o.get("effS0Id")).shortValue();
					(p.skillStand[k]).e0dx = ((Long) o.get("e0dx")).shortValue();
					(p.skillStand[k]).e0dy = ((Long) o.get("e0dy")).shortValue();
					(p.skillStand[k]).effS1Id = ((Long) o.get("effS1Id")).shortValue();
					(p.skillStand[k]).e1dx = ((Long) o.get("e1dx")).shortValue();
					(p.skillStand[k]).e1dy = ((Long) o.get("e1dy")).shortValue();
					(p.skillStand[k]).effS2Id = ((Long) o.get("effS2Id")).shortValue();
					(p.skillStand[k]).e2dx = ((Long) o.get("e2dx")).shortValue();
					(p.skillStand[k]).e2dy = ((Long) o.get("e2dy")).shortValue();
					(p.skillStand[k]).arrowId = ((Long) o.get("arrowId")).shortValue();
					(p.skillStand[k]).adx = ((Long) o.get("adx")).shortValue();
					(p.skillStand[k]).ady = ((Long) o.get("ady")).shortValue();
				}
				jA = (JSONArray) JSONValue.parse(resultSet.getString("skillFly"));
				p.skillfly = new SkillInfoPaint[jA.size()];
				for (k = 0; k < p.skillfly.length; k++) {
					JSONObject o = (JSONObject) jA.get(k);
					p.skillfly[k] = new SkillInfoPaint();
					(p.skillfly[k]).status = ((Long) o.get("status")).byteValue();
					(p.skillfly[k]).effS0Id = ((Long) o.get("effS0Id")).shortValue();
					(p.skillfly[k]).e0dx = ((Long) o.get("e0dx")).shortValue();
					(p.skillfly[k]).e0dy = ((Long) o.get("e0dy")).shortValue();
					(p.skillfly[k]).effS1Id = ((Long) o.get("effS1Id")).shortValue();
					(p.skillfly[k]).e1dx = ((Long) o.get("e1dx")).shortValue();
					(p.skillfly[k]).e1dy = ((Long) o.get("e1dy")).shortValue();
					(p.skillfly[k]).effS2Id = ((Long) o.get("effS2Id")).shortValue();
					(p.skillfly[k]).e2dx = ((Long) o.get("e2dx")).shortValue();
					(p.skillfly[k]).e2dy = ((Long) o.get("e2dy")).shortValue();
					(p.skillfly[k]).arrowId = ((Long) o.get("arrowId")).shortValue();
					(p.skillfly[k]).adx = ((Long) o.get("adx")).shortValue();
					(p.skillfly[k]).ady = ((Long) o.get("ady")).shortValue();
				}
				sks.add(p);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `nj_part`;");
			while (resultSet.next()) {
				byte type = resultSet.getByte("type");
				JSONArray jA = (JSONArray) JSONValue.parse(resultSet.getString("part"));
				Part part = new Part(type);
				for (int k = 0; k < part.pi.length; k++) {
					JSONObject o = (JSONObject) jA.get(k);
					part.pi[k] = new PartImage();
					(part.pi[k]).id = ((Long) o.get("id")).shortValue();
					(part.pi[k]).dx = ((Long) o.get("dx")).byteValue();
					(part.pi[k]).dy = ((Long) o.get("dy")).byteValue();
				}
				parts.add(part);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `nj_image`;");
			while (resultSet.next()) {
				int[] smallImage = new int[5];
				JSONArray jA = (JSONArray) JSONValue.parse(resultSet.getString("smallImage"));
				smallImage[0] = ((Long) jA.get(0)).intValue();
				smallImage[1] = ((Long) jA.get(1)).intValue();
				smallImage[2] = ((Long) jA.get(2)).intValue();
				smallImage[3] = ((Long) jA.get(3)).intValue();
				smallImage[4] = ((Long) jA.get(4)).intValue();
				smallImg.add(smallImage);
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `nj_arrow`;");
			while (resultSet.next()) {
				ArrowPaint p = new ArrowPaint();
				p.id = resultSet.getShort("id");
				JSONArray jA = (JSONArray) JSONValue.parse(resultSet.getString("imgId"));
				p.imgId[0] = ((Long) jA.get(0)).shortValue();
				p.imgId[1] = ((Long) jA.get(1)).shortValue();
				p.imgId[2] = ((Long) jA.get(2)).shortValue();
			}
			resultSet.close();
			resultSet = Connect.stat.executeQuery("SELECT * FROM `nj_effect`;");
			while (resultSet.next()) {
				EffectCharPaint effectCharInfo = new EffectCharPaint();
				effectCharInfo.idEf = resultSet.getShort("id");
				JSONArray jA = (JSONArray) JSONValue.parse(resultSet.getString("info"));
				effectCharInfo.arrEfInfo = new EffectInfoPaint[jA.size()];
				for (int k = 0; k < effectCharInfo.arrEfInfo.length; k++) {
					JSONObject o = (JSONObject) jA.get(k);
					effectCharInfo.arrEfInfo[k] = new EffectInfoPaint();
					(effectCharInfo.arrEfInfo[k]).idImg = ((Long) o.get("imgId")).shortValue();
					(effectCharInfo.arrEfInfo[k]).dx = ((Long) o.get("dx")).byteValue();
					(effectCharInfo.arrEfInfo[k]).dy = ((Long) o.get("dy")).byteValue();
				}
			}
			resultSet.close();
			setCache((byte) 0);
			setCache((byte) 1);
			setCache((byte) 2);
			setCache((byte) 3);
			setCache((byte) 4);
			MapManager.init();
		} catch (SQLException ex) {
			logger.debug("init", ex.toString());
		}
	}

	public static User getUser(int userId) {
		for (Session client : sessions) {
			if (client.user != null && client.user.id == userId) {
				return client.user;
			}
		}
		return null;
	}

	public static synchronized void sendToServer(Message ms) {
		for (Session cl : sessions) {
			if (cl != null && cl.user != null && cl.user.selectedCharacter != null)
				cl.sendMessage(ms);
		}
	}

	private static void setCache(byte id) {
		try {
			ByteArrayOutputStream bas;
			DataOutputStream dos;
			byte[] ab;
			ByteArrayOutputStream arrow;
			DataOutputStream ds;
			ByteArrayOutputStream effect;
			ByteArrayOutputStream image;
			ByteArrayOutputStream parts;
			ByteArrayOutputStream skills;
			int i;
			byte[] abb;
			JSONArray jArr;
			int num;
			int j;
			Collection<ItemOptionTemplate> options;
			Collection<ItemEntry> entrys;
			Collection<SkillData.NClass> nClasss;
			switch (id) {
			case 0:
				bas = new ByteArrayOutputStream();
				dos = new DataOutputStream(bas);
				dos.writeByte(10);
				dos.writeByte(maps.size());
				for (TileMap map : maps) {
					dos.writeUTF(map.name);
				}
				dos.writeByte(npcs.size());
				for (NpcTemplate npc : npcs) {
					dos.writeUTF(npc.name);
					dos.writeShort(npc.headId);
					dos.writeShort(npc.bodyId);
					dos.writeShort(npc.legId);
					String[][] menu = npc.menu;
					dos.writeByte(menu.length);
					for (String[] m : menu) {
						dos.writeByte(m.length);
						for (String s : m) {
							dos.writeUTF(s);
						}
					}
				}
				dos.writeByte(mobs.size());
				for (MonsterTemplate mob : mobs) {
					dos.writeByte(mob.type);
					dos.writeUTF(mob.name);
					dos.writeInt(mob.hp);
					dos.writeByte(mob.rangeMove);
					dos.writeByte(mob.speed);
				}
				ab = bas.toByteArray();
				NinjaUtil.saveFile("cache/map", ab);
				dos.close();
				bas.close();
				break;

			case 1:
				bas = new ByteArrayOutputStream();
				dos = new DataOutputStream(bas);
				dos.writeByte(55);
				arrow = new ByteArrayOutputStream();
				ds = new DataOutputStream(arrow);
				ds.writeShort(arrs.size());
				for (ArrowPaint arr : arrs) {
					ds.writeShort(arr.id);
					ds.writeShort(arr.imgId[0]);
					ds.writeShort(arr.imgId[1]);
					ds.writeShort(arr.imgId[2]);
				}
				ds.flush();
				dos.writeInt((arrow.toByteArray()).length);
				dos.write(arrow.toByteArray());
				ds.close();
				arrow.close();

				effect = new ByteArrayOutputStream();
				ds = new DataOutputStream(effect);
				ds.writeShort(efs.size());
				for (EffectCharPaint eff : efs) {
					ds.writeShort(eff.idEf);
					ds.writeByte(eff.arrEfInfo.length);
					for (EffectInfoPaint eff2 : eff.arrEfInfo) {
						ds.writeShort(eff2.idImg);
						ds.writeByte(eff2.dx);
						ds.writeByte(eff2.dy);
					}
				}
				ds.flush();
				dos.writeInt((effect.toByteArray()).length);
				dos.write(effect.toByteArray());
				ds.close();
				effect.close();

				image = new ByteArrayOutputStream();
				ds = new DataOutputStream(image);
				ds.writeShort(smallImg.size());
				for (int[] img : smallImg) {
					ds.writeByte(img[0]);
					ds.writeShort(img[1]);
					ds.writeShort(img[2]);
					ds.writeShort(img[3]);
					ds.writeShort(img[4]);
				}
				ds.flush();
				dos.writeInt((image.toByteArray()).length);
				dos.write(image.toByteArray());
				ds.close();
				image.close();

				parts = new ByteArrayOutputStream();
				ds = new DataOutputStream(parts);
				ds.writeShort(Server.parts.size());
				for (Part p : Server.parts) {
					ds.writeByte(p.type);
					for (PartImage pi : p.pi) {
						ds.writeShort(pi.id);
						ds.writeByte(pi.dx);
						ds.writeByte(pi.dy);
					}
				}
				ds.flush();
				dos.writeInt((parts.toByteArray()).length);
				dos.write(parts.toByteArray());
				ds.close();
				parts.close();

				skills = new ByteArrayOutputStream();
				ds = new DataOutputStream(skills);
				ds.writeShort(sks.size());
				for (SkillPaint p : sks) {
					ds.writeShort(p.id);
					ds.writeShort(p.effId);
					ds.writeByte(p.numEff);
					ds.writeByte(p.skillStand.length);
					for (SkillInfoPaint skillStand : p.skillStand) {
						ds.writeByte(skillStand.status);
						ds.writeShort(skillStand.effS0Id);
						ds.writeShort(skillStand.e0dx);
						ds.writeShort(skillStand.e0dy);
						ds.writeShort(skillStand.effS1Id);
						ds.writeShort(skillStand.e1dx);
						ds.writeShort(skillStand.e1dy);
						ds.writeShort(skillStand.effS2Id);
						ds.writeShort(skillStand.e2dx);
						ds.writeShort(skillStand.e2dy);
						ds.writeShort(skillStand.arrowId);
						ds.writeShort(skillStand.adx);
						ds.writeShort(skillStand.ady);
					}
					ds.writeByte(p.skillfly.length);
					for (SkillInfoPaint skillfly : p.skillfly) {
						ds.writeByte(skillfly.status);
						ds.writeShort(skillfly.effS0Id);
						ds.writeShort(skillfly.e0dx);
						ds.writeShort(skillfly.e0dy);
						ds.writeShort(skillfly.effS1Id);
						ds.writeShort(skillfly.e1dx);
						ds.writeShort(skillfly.e1dy);
						ds.writeShort(skillfly.effS2Id);
						ds.writeShort(skillfly.e2dx);
						ds.writeShort(skillfly.e2dy);
						ds.writeShort(skillfly.arrowId);
						ds.writeShort(skillfly.adx);
						ds.writeShort(skillfly.ady);
					}
				}
				ds.flush();
				dos.writeInt((skills.toByteArray()).length);
				dos.write(skills.toByteArray());
				ds.close();
				skills.close();

				dos.writeByte(tasks.length);
				for (i = 0; i < tasks.length; i++) {
					dos.writeByte((tasks[i]).length);
					for (int a = 0; a < (tasks[i]).length; a++) {
						dos.writeByte(tasks[i][a]);
						dos.writeByte(mapTasks[i][a]);
					}
				}
				dos.writeByte(exps.length);
				for (long exp : exps) {
					dos.writeLong(exp);
				}
				dos.writeByte(crystals.length);
				for (int k : crystals) {
					dos.writeInt(k);
				}
				dos.writeByte(upClothes.length);
				for (int k : upClothes) {
					dos.writeInt(k);
				}
				dos.writeByte(upAdorns.length);
				for (int k : upAdorns) {
					dos.writeInt(k);
				}
				dos.writeByte(upWeapons.length);
				for (int k : upWeapons) {
					dos.writeInt(k);
				}
				dos.writeByte(coinUpCrystals.length);
				for (int k : coinUpCrystals) {
					dos.writeInt(k);
				}
				dos.writeByte(coinUpClothes.length);
				for (int k : coinUpClothes) {
					dos.writeInt(k);
				}
				dos.writeByte(coinUpAdorns.length);
				for (int k : coinUpAdorns) {
					dos.writeInt(k);
				}
				dos.writeByte(coinUpWeapons.length);
				for (int k : coinUpWeapons) {
					dos.writeInt(k);
				}
				dos.writeByte(goldUps.length);
				for (int k : goldUps) {
					dos.writeInt(k);
				}
				dos.writeByte(maxPercents.length);
				for (int k : maxPercents) {
					dos.writeInt(k);
				}
				dos.writeByte(Effect.effTemplates.size());
				for (EffectTemplate eff : Effect.effTemplates) {
					dos.writeByte(eff.id);
					dos.writeByte(eff.type);
					dos.writeUTF(eff.name);
					dos.writeShort(eff.icon);
				}
				abb = bas.toByteArray();
				NinjaUtil.saveFile("cache/data", abb);
				dos.close();
				bas.close();
				break;

			case 2:
				bas = new ByteArrayOutputStream();
				dos = new DataOutputStream(bas);
				dos.writeByte(55);
				dos.writeByte(10);
				dos.writeByte(10);
				dos.writeByte(76);
				jArr = (JSONArray) moto.get("head_jump");
				num = jArr.size();
				dos.writeByte(num);
				for (j = 0; j < num; j++) {
					JSONObject part = (JSONObject) jArr.get(j);
					JSONArray item = (JSONArray) part.get("item");
					int numItem = item.size() * 3 + 2;
					dos.writeByte(numItem);
					dos.writeShort(((Long) part.get("id")).shortValue());
					dos.writeShort(((Long) part.get("idSmall")).shortValue());
					for (int a = 0; j < numItem - 2; j += 3) {
						JSONObject obj = (JSONObject) item.get(a);
						dos.writeShort(((Long) obj.get("id")).shortValue());
						dos.writeShort(((Long) obj.get("dx")).shortValue());
						dos.writeShort(((Long) obj.get("dy")).shortValue());
					}
				}
				jArr = (JSONArray) moto.get("head_normal");
				num = jArr.size();
				for (j = 0; j < num; j++) {
					JSONObject part = (JSONObject) jArr.get(j);
					JSONArray item = (JSONArray) part.get("item");
					int numItem = item.size() * 3 + 2;
					dos.writeByte(numItem);
					dos.writeShort(((Long) part.get("id")).shortValue());
					dos.writeShort(((Long) part.get("idSmall")).shortValue());
					for (int a = 0; j < numItem - 2; j += 3) {
						JSONObject obj = (JSONObject) item.get(a);
						dos.writeShort(((Long) obj.get("id")).shortValue());
						dos.writeShort(((Long) obj.get("dx")).shortValue());
						dos.writeShort(((Long) obj.get("dy")).shortValue());
					}
				}
				jArr = (JSONArray) moto.get("head_boc_dau");
				num = jArr.size();
				for (j = 0; j < num; j++) {
					JSONObject part = (JSONObject) jArr.get(j);
					JSONArray item = (JSONArray) part.get("item");
					int numItem = item.size() * 3 + 2;
					dos.writeByte(numItem);
					dos.writeShort(((Long) part.get("id")).shortValue());
					dos.writeShort(((Long) part.get("idSmall")).shortValue());
					for (int a = 0; j < numItem - 2; j += 3) {
						JSONObject obj = (JSONObject) item.get(a);
						dos.writeShort(((Long) obj.get("id")).shortValue());
						dos.writeShort(((Long) obj.get("dx")).shortValue());
						dos.writeShort(((Long) obj.get("dy")).shortValue());
					}
				}
				jArr = (JSONArray) moto.get("body_jump");
				num = jArr.size() * 2;
				dos.writeByte(num);
				for (j = 0; j < num; j += 2) {
					JSONObject obj = (JSONObject) jArr.get(j / 2);
					dos.writeShort(((Long) obj.get("id")).shortValue());
					dos.writeShort(((Long) obj.get("idSmall")).shortValue());
				}

				jArr = (JSONArray) moto.get("body_jump");
				num = jArr.size();
				dos.writeByte(num);
				for (j = 0; j < num; j++) {
					JSONObject part = (JSONObject) jArr.get(j);
					JSONArray item = (JSONArray) part.get("item");
					int numItem = item.size() * 3 + 2;
					dos.writeByte(numItem);
					dos.writeShort(((Long) part.get("id")).shortValue());
					dos.writeShort(((Long) part.get("idSmall")).shortValue());
					for (int a = 0; j < numItem - 2; j += 3) {
						JSONObject obj = (JSONObject) item.get(a);
						dos.writeShort(((Long) obj.get("id")).shortValue());
						dos.writeShort(((Long) obj.get("dx")).shortValue());
						dos.writeShort(((Long) obj.get("dy")).shortValue());
					}
				}
				jArr = (JSONArray) moto.get("body_normal");
				for (j = 0; j < num; j++) {
					JSONObject part = (JSONObject) jArr.get(j);
					JSONArray item = (JSONArray) part.get("item");
					int numItem = item.size() * 3 + 2;
					dos.writeByte(numItem);
					dos.writeShort(((Long) part.get("id")).shortValue());
					dos.writeShort(((Long) part.get("idSmall")).shortValue());
					for (int a = 0; j < numItem - 2; j += 3) {
						JSONObject obj = (JSONObject) item.get(a);
						dos.writeShort(((Long) obj.get("id")).shortValue());
						dos.writeShort(((Long) obj.get("dx")).shortValue());
						dos.writeShort(((Long) obj.get("dy")).shortValue());
					}
				}
				jArr = (JSONArray) moto.get("body_dau");
				for (j = 0; j < num; j++) {
					JSONObject part = (JSONObject) jArr.get(j);
					JSONArray item = (JSONArray) part.get("item");
					int numItem = item.size() * 3 + 2;
					dos.writeByte(numItem);
					dos.writeShort(((Long) part.get("id")).shortValue());
					dos.writeShort(((Long) part.get("idSmall")).shortValue());
					for (int a = 0; j < numItem - 2; j += 3) {
						JSONObject obj = (JSONObject) item.get(a);
						dos.writeShort(((Long) obj.get("id")).shortValue());
						dos.writeShort(((Long) obj.get("dx")).shortValue());
						dos.writeShort(((Long) obj.get("dy")).shortValue());
					}
				}
				ab = bas.toByteArray();
				NinjaUtil.saveFile("cache/version", ab);
				dos.close();
				bas.close();
				break;

			case 3:
				bas = new ByteArrayOutputStream();
				dos = new DataOutputStream(bas);
				dos.writeByte(76);
				options = ItemData.getOptions();
				dos.writeByte(options.size());
				for (ItemOptionTemplate item : options) {
					dos.writeUTF(item.name);
					dos.writeByte(item.type);
				}
				entrys = ItemData.getEntrys();
				dos.writeShort(entrys.size());
				for (ItemEntry item : entrys) {
					dos.writeByte(item.type);
					dos.writeByte(item.gender);
					dos.writeUTF(item.name);
					dos.writeUTF(item.description);
					dos.writeByte(item.level);
					dos.writeShort(item.icon);
					dos.writeShort(item.part);
					dos.writeBoolean(item.isUpToUp);
				}
				ab = bas.toByteArray();
				NinjaUtil.saveFile("cache/item", ab);
				dos.close();
				bas.close();
				break;

			case 4:
				bas = new ByteArrayOutputStream();
				dos = new DataOutputStream(bas);
				dos.writeByte(10);
				dos.writeByte(SkillData.optionTemplates.size());
				for (SkillOptionTemplate optionTemplate : SkillData.optionTemplates.values()) {
					dos.writeUTF(optionTemplate.name);
				}
				dos.writeByte(SkillData.nClasss.size());
				nClasss = SkillData.nClasss.values();
				for (SkillData.NClass nClass : nClasss) {
					dos.writeUTF(nClass.name);
					dos.writeByte(nClass.templates.size());
					Collection<SkillTemplate> templates = nClass.templates.values();
					for (SkillTemplate template : templates) {
						dos.writeByte(template.id);
						dos.writeUTF(template.name);
						dos.writeByte(template.maxPoint);
						dos.writeByte(template.type);
						dos.writeShort(template.iconId);
						dos.writeUTF(template.description);
						dos.writeByte(template.skills.size());
						for (Skill skill : template.skills) {
							dos.writeShort(skill.skillId);
							dos.writeByte(skill.point);
							dos.writeByte(skill.level);
							dos.writeShort(skill.manaUse);
							dos.writeInt(skill.coolDown);
							dos.writeShort(skill.dx);
							dos.writeShort(skill.dy);
							dos.writeByte(skill.maxFight);
							dos.writeByte(skill.options.length);
							for (SkillOption option : skill.options) {
								dos.writeShort(option.param);
								dos.writeByte(option.optionTemplate.id);
							}
						}
					}
				}
				ab = bas.toByteArray();
				NinjaUtil.saveFile("cache/skill", ab);
				dos.close();
				bas.close();
				break;
			}
		} catch (IOException ex) {
			logger.debug("setCache", ex.toString());
		}
	}

	public static void start() {
		logger.log("Start socket post=14444");
		try {
			luckyDrawNormal = new LuckyDraw("Vòng xoay thường!", (byte) 0);
			luckyDrawVIP = new LuckyDraw("Vòng xoay vip", (byte) 1);
			(new Thread(luckyDrawNormal)).start();
			(new Thread(luckyDrawVIP)).start();
			sessions = new ArrayList<>();
			server = new ServerSocket(14444);
			id = 0;
			start = true;
			logger.log("Start server Success!");
			while (start) {
				try {
					Socket client = server.accept();
					Session cl = new Session(client, ++id);
					sessions.add(cl);
				} catch (IOException e) {
					logger.debug("start", e.toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void stop() {
		if (start) {
			close();
			LuckyDraw.running = false;
			Map.running = false;
			start = false;
			System.gc();
		}
	}

	public static void close() {
		try {
			server.close();
			server = null;
			while (sessions.size() > 0) {
				Session c = sessions.get(0);
				c.close();
			}
			sessions = null;
			Connect.close();
			System.gc();
			logger.log("End socket");
		} catch (IOException e) {
			logger.debug("close", e.toString());
		}
	}

	public static void removeClient(Session cl) {
		synchronized (sessions) {
			sessions.remove(cl);
			logger.log("Disconnect client: " + cl);
		}
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * server\Server.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */