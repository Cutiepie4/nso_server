package com.kitakeyos.server;

import com.kitakeyos.data.SkillData;
import com.kitakeyos.io.Message;
import com.kitakeyos.io.Session;
import com.kitakeyos.object.BuNhin;
import com.kitakeyos.object.Effect;
import com.kitakeyos.object.Frame;
import com.kitakeyos.object.Friend;
import com.kitakeyos.object.ImageInfo;
import com.kitakeyos.object.ItemMap;
import com.kitakeyos.object.ItemStore;
import com.kitakeyos.object.TileMap;
import com.kitakeyos.object.Waypoint;
import com.kitakeyos.option.ItemOption;
import com.kitakeyos.template.MonsterTemplate;
import com.kitakeyos.template.NpcTemplate;
import com.kitakeyos.util.NinjaUtil;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class Service {
	private Session client;
	private Character character;
	private User user;
	private static byte[][][] item = new byte[4][4000][];
	private static byte[][] cache = new byte[5][];

	public Service(Session client, User user) {
		this.client = client;
		this.user = user;
	}

	public void setChar(Character pl) {
		this.character = pl;
	}

	public void showAlert(String title, String text) throws IOException {
		if (title.equals("typemoi")) {
			return;
		}
		Message ms = new Message(53);
		DataOutputStream ds = ms.writer();
		ds.writeUTF(title);
		ds.writeUTF(text);
		ds.flush();
		this.client.sendMessage(ms);
	}

	public void selectChar() throws IOException {
		Message ms = messageNotMap((byte) -126);
		DataOutputStream ds = ms.writer();
		ds.writeByte(this.user.characters.size());
		for (Character _char : this.user.characters.values()) {
			ds.writeByte(_char.gender);
			ds.writeUTF(_char.name);
			ds.writeUTF(_char.school);
			ds.writeByte(_char.level);
			ds.writeShort(_char.head);
			ds.writeShort(_char.weapon);
			ds.writeShort(_char.body);
			ds.writeShort(_char.leg);
		}
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void sendItemMap() throws IOException {
		Message ms = new Message(117);
		DataOutputStream ds = ms.writer();
		ds.writeByte(0);
		ds.writeByte(0);
		ds.writeByte(0);
		ds.writeByte(0);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void addYen(int add) throws IOException {
		Message ms = new Message(-8);
		DataOutputStream ds = ms.writer();
		ds.writeInt(add);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void exchangeYenForXu(int xu) throws IOException {
		Message ms = new Message(-7);
		DataOutputStream ds = ms.writer();
		ds.writeInt(xu);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void addXu(int xu) throws IOException {
		Message ms = new Message(95);
		DataOutputStream ds = ms.writer();
		ds.writeInt(xu);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void update() throws IOException {
		Message ms = new Message(13);
		DataOutputStream ds = ms.writer();
		ds.writeInt(this.character.xu);
		ds.writeInt(this.character.yen);
		ds.writeInt(this.user.luong);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void convertUpgrade(Character.Item... item) throws IOException {
		Message ms = messageNotMap((byte) -88);
		DataOutputStream ds = ms.writer();
		ds.writeByte((item[0]).index);
		ds.writeByte((item[0]).upgrade);
		ds.writeByte((item[1]).index);
		ds.writeByte((item[1]).upgrade);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void addEffect(Effect effect) throws IOException {
		Message ms = messageSubCommand((byte) -101);
		DataOutputStream ds = ms.writer();
		ds.writeByte(effect.template.id);
		ds.writeInt(effect.timeStart);
		ds.writeInt(effect.timeLength * 1000);
		ds.writeShort(effect.param);
		if (effect.template.type == 14 || effect.template.type == 2 || effect.template.type == 3) {
			ds.writeShort(this.character.x);
			ds.writeShort(this.character.y);
		}
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void removeEffect(Effect effect) throws IOException {
		Message ms = messageSubCommand((byte) -99);
		DataOutputStream ds = ms.writer();
		ds.writeByte(effect.template.id);
		if (effect.template.type == 0 || effect.template.type == 12) {
			ds.writeInt(this.character.hp);
			ds.writeInt(this.character.mp);
		} else if (effect.template.type == 4 || effect.template.type == 13 || effect.template.type == 17) {
			ds.writeInt(this.character.hp);
		} else if (effect.template.type == 23) {
			ds.writeInt(this.character.hp);
			ds.writeInt(this.character.maxHP);
		}
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void replaceEffect(Effect effect) throws IOException {
		Message ms = messageSubCommand((byte) -100);
		DataOutputStream ds = ms.writer();
		ds.writeByte(effect.template.id);
		ds.writeInt(effect.timeStart);
		ds.writeInt(effect.timeLength * 1000);
		ds.writeShort(effect.param);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void updateHp() throws IOException {
		Message ms = messageSubCommand((byte) -122);
		DataOutputStream ds = ms.writer();
		ds.writeInt(this.character.hp);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void updateMp() throws IOException {
		Message ms = messageSubCommand((byte) -121);
		DataOutputStream ds = ms.writer();
		ds.writeInt(this.character.mp);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void sendCharInfoInMap(Character pl) throws IOException {
		Message ms = messageSubCommand((byte) -120);
		DataOutputStream ds = ms.writer();
		ds.writeInt(pl.id);
		ds.writeUTF(pl.clanname);
		if (pl.clanname != "") {
			ds.writeByte(pl.clan);
		}
		ds.writeBoolean(false);
		ds.writeByte(pl.hieuChien);
		ds.writeByte(pl.classId);
		ds.writeByte(pl.gender);
		ds.writeShort(pl.head);
		ds.writeUTF(pl.name);
		ds.writeInt(pl.hp);
		ds.writeInt(pl.maxHP);
		ds.writeByte(pl.level);
		ds.writeShort(pl.weapon);
		ds.writeShort(pl.body);
		ds.writeShort(pl.leg);
		ds.writeByte(-1);
		ds.writeShort(pl.x);
		ds.writeShort(pl.y);
		ds.writeShort(pl.eff5buffhp);
		ds.writeShort(pl.eff5buffmp);
		ds.writeByte(0);
		ds.writeBoolean(true);
		ds.writeBoolean(false);
		ds.writeShort(pl.head);
		ds.writeShort(pl.weapon);
		ds.writeShort(pl.body);
		ds.writeShort(pl.leg);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void sendSkillShortcut(String key, byte[] data) throws IOException {
		Message ms = messageSubCommand((byte) -65);
		DataOutputStream ds = ms.writer();
		ds.writeUTF(key);
		ds.writeInt(data.length);
		ds.write(data);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void boxCoinIn(int xu) throws IOException {
		Message ms = messageSubCommand((byte) -105);
		DataOutputStream ds = ms.writer();
		ds.writeInt(xu);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void boxCoinOut(int xu) throws IOException {
		Message ms = messageSubCommand((byte) -104);
		DataOutputStream ds = ms.writer();
		ds.writeInt(xu);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void learnSkill(byte itemIndex, short skillId) throws IOException {
		Message ms = messageSubCommand((byte) -102);
		DataOutputStream ds = ms.writer();
		ds.writeByte(itemIndex);
		ds.writeShort(skillId);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void sendBox() throws IOException {
		Message ms = new Message(31);
		DataOutputStream ds = ms.writer();
		ds.writeInt(this.character.xuInBox);
		ds.writeByte(this.character.numberCellBox);
		for (Character.Item item : this.character.box) {
			if (item != null) {
				ds.writeShort(item.id);
				ds.writeBoolean(item.isLock);
				if (item.entry.isTypeBody() || item.entry.isTypeNgocKham()) {
					ds.writeByte(item.upgrade);
				}
				ds.writeBoolean((item.expire != -1L));
				ds.writeShort(item.quantity);
			} else {
				ds.writeShort(-1);
			}
		}
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void sendZone() throws IOException {
		Map map = this.character.map;
		TileMap tilemap = map.tilemap;
		Zone zone = this.character.zone;
		Message ms = new Message(-18);
		DataOutputStream ds = ms.writer();
		ds.writeByte(this.character.mapId);
		ds.writeByte(tilemap.tileId);
		ds.writeByte(tilemap.bgId);
		ds.writeByte(tilemap.type);
		ds.writeUTF(tilemap.name);
		ds.writeByte(zone.zoneId);
		ds.writeShort(this.character.x);
		ds.writeShort(this.character.y);
		int len = tilemap.waypoints.size();
		len = (len > 127) ? 127 : len;
		ds.writeByte(len);
		for (int i = 0; i < len; i++) {
			Waypoint waypoint = tilemap.waypoints.get(i);
			ds.writeShort(waypoint.minX);
			ds.writeShort(waypoint.minY);
			ds.writeShort(waypoint.maxX);
			ds.writeShort(waypoint.maxY);
		}
		Monster[] monsters = zone.getMonsters();
		len = monsters.length;
		len = (len > 127) ? 127 : len;
		ds.writeByte(len);
		for (int j = 0; j < len; j++) {
			Monster mob = monsters[j];
			ds.writeBoolean(mob.isDisable);
			ds.writeBoolean(mob.isDontMove);
			ds.writeBoolean(mob.isFire);
			ds.writeBoolean(mob.isIce);
			ds.writeBoolean(mob.isWind);
			ds.writeByte(mob.templateId);
			ds.writeByte(mob.sys);
			ds.writeInt(mob.hp);
			ds.writeByte(mob.level);
			ds.writeInt(mob.maxhp);
			ds.writeShort(mob.x);
			ds.writeShort(mob.y);
			ds.writeByte(mob.status);
			ds.writeByte(mob.levelBoss);
			ds.writeBoolean(mob.isBoss);
		}
		BuNhin[] buNhins = zone.getBuNhins();
		int num = buNhins.length;
		num = (num > 127) ? 127 : num;
		ds.writeByte(num);
		int k;
		for (k = 0; k < num; k++) {
			BuNhin buNhin = buNhins[k];
			ds.writeUTF(buNhin.name);
			ds.writeShort(buNhin.x);
			ds.writeShort(buNhin.y);
		}
		num = tilemap.npcs.size();
		num = (num > 127) ? 127 : num;
		ds.writeByte(num);
		for (k = 0; k < num; k++) {
			NpcTemplate npc = tilemap.npcs.get(k);
			ds.writeByte(npc.status);
			ds.writeShort(npc.x);
			ds.writeShort(npc.y);
			ds.writeByte(npc.templateId);
		}
		ItemMap[] items = zone.getItemMaps();
		num = items.length;
		num = (num > 127) ? 127 : num;
		ds.writeByte(num);
		for (int m = 0; m < num; m++) {
			ItemMap item = items[m];
			ds.writeShort(item.id);
			ds.writeShort(item.item.id);
			ds.writeShort(item.x);
			ds.writeShort(item.y);
		}
		ds.writeUTF(tilemap.name);
		ds.writeByte(0);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
		sendCharInMap();
	}

	public void useMask() throws IOException {
		Message ms = messageSubCommand((byte) -64);
		DataOutputStream ds = ms.writer();
		ds.writeInt(this.character.id);
		ds.writeInt(this.character.hp);
		ds.writeInt(this.character.maxHP);
		ds.writeShort(this.character.eff5buffhp);
		ds.writeShort(this.character.eff5buffmp);
		ds.writeShort(this.character.head);
		ds.flush();
		this.character.sendToMap(ms);
	}

	public void usePant() throws IOException {
		Message ms = messageSubCommand((byte) -113);
		DataOutputStream ds = ms.writer();
		ds.writeInt(this.character.id);
		ds.writeInt(this.character.hp);
		ds.writeInt(this.character.maxHP);
		ds.writeShort(this.character.eff5buffhp);
		ds.writeShort(this.character.eff5buffmp);
		ds.writeShort(this.character.leg);
		ds.flush();
		this.character.sendToMap(ms);
	}

	public void useShirt() throws IOException {
		Message ms = messageSubCommand((byte) -116);
		DataOutputStream ds = ms.writer();
		ds.writeInt(this.character.id);
		ds.writeInt(this.character.hp);
		ds.writeInt(this.character.maxHP);
		ds.writeShort(this.character.eff5buffhp);
		ds.writeShort(this.character.eff5buffmp);
		ds.writeShort(this.character.body);
		ds.flush();
		this.character.sendToMap(ms);
	}

	public void useWeapon() throws IOException {
		Message ms = messageSubCommand((byte) -117);
		DataOutputStream ds = ms.writer();
		ds.writeInt(this.character.id);
		ds.writeInt(this.character.hp);
		ds.writeInt(this.character.maxHP);
		ds.writeShort(this.character.eff5buffhp);
		ds.writeShort(this.character.eff5buffmp);
		ds.writeShort(this.character.weapon);
		ds.flush();
		this.character.sendToMap(ms);
	}

	public void sendCharInMap() throws IOException {
		Character[] characters = this.character.zone.getCharacters();
		for (Character _char : characters) {
			if (!this.character.equals(_char)) {
				sendCharInfo(_char);
			}
			if (_char.mount[4] != null) {
				_char.user.service.sendMount();
			}
		}
	}

	public void itemMountToBag(int index1, int index2) throws IOException {
		Message mss = new Message(108);
		DataOutputStream ds = mss.writer();
		ds.writeByte(this.character.speed);
		ds.writeInt(this.character.maxHP);
		ds.writeInt(this.character.maxMP);
		ds.writeShort(this.character.eff5buffhp);
		ds.writeShort(this.character.eff5buffmp);
		ds.writeByte(index1);
		ds.writeByte(index2);
		ds.flush();
		this.client.sendMessage(mss);
	}

	public void sendMount() throws IOException {
		Message ms = messageSubCommand((byte) -54);
		DataOutputStream ds = ms.writer();
		ds.writeInt(this.character.id);
		for (int i = 0; i < 5; i++) {
			if (this.character.mount[i] != null) {
				ds.writeShort((this.character.mount[i]).id);
				ds.writeByte((this.character.mount[i]).level);
				ds.writeLong((this.character.mount[i]).expire);
				ds.writeByte((this.character.mount[i]).sys);
				ds.writeByte((this.character.mount[i]).options.size());
				for (ItemOption option : (this.character.mount[i]).options) {
					ds.writeByte(option.optionTemplate.id);
					ds.writeInt(option.param);
				}
			} else {
				ds.writeShort(-1);
			}
		}
		ds.flush();
		this.character.sendToMap(ms);
		ms.cleanup();
	}

	public void openUIZone() throws IOException {
		Message ms = new Message(36);
		DataOutputStream ds = ms.writer();
		Map m = this.character.map;
		Collection<Zone> zones = m.getZones();
		ds.writeByte(zones.size());
		for (Zone z : zones) {
			ds.writeByte(z.numberCharacter);
			ds.writeByte(z.numberGroup);
		}
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void sendInfo() throws IOException {
		Message ms = messageSubCommand((byte) -127);
		DataOutputStream ds = ms.writer();
		ds.writeInt(this.character.id);
		ds.writeUTF(this.character.clanname);
		if (this.character.clanname != "") {
			ds.writeByte(this.character.clan);
		}
		ds.writeByte(40);
		ds.writeByte(this.character.gender);
		ds.writeShort(this.character.head);
		ds.writeByte(this.character.speed);
		ds.writeUTF(this.character.name);
		ds.writeByte(this.character.hieuChien);
		ds.writeByte(this.character.typePk);
		ds.writeInt(this.character.maxHP);
		ds.writeInt(this.character.hp);
		ds.writeInt(this.character.maxMP);
		ds.writeInt(this.character.mp);
		ds.writeLong(this.character.exp);
		ds.writeLong(this.character.expDown);
		ds.writeShort(this.character.eff5buffhp);
		ds.writeShort(this.character.eff5buffmp);
		ds.writeByte(this.character.classId);
		ds.writeShort(this.character.point);
		ds.writeShort(this.character.potential[0]);
		ds.writeShort(this.character.potential[1]);
		ds.writeInt(this.character.potential[2]);
		ds.writeInt(this.character.potential[3]);
		ds.writeShort(this.character.spoint);
		ds.writeByte(this.character.listSkill.size());
		for (Character.MySkill my : this.character.listSkill) {
			ds.writeShort((SkillData.getSkill(this.character.classId, my.id, my.point)).skillId);
		}
		ds.writeInt(this.character.xu);
		ds.writeInt(this.character.yen);
		ds.writeInt(this.user.luong);
		ds.writeByte(this.character.numberCellBag);
		int i;
		for (i = 0; i < this.character.numberCellBag; i++) {
			Character.Item item = this.character.bag[i];
			if (item != null) {
				ds.writeShort(item.id);
				ds.writeBoolean(item.isLock);
				if (item.entry.isTypeBody() || item.entry.isTypeNgocKham() || item.entry.isTypeMount()) {
					ds.writeByte(item.upgrade);
				}
				ds.writeBoolean((item.expire != -1L));
				ds.writeShort(item.quantity);
			} else {
				ds.writeShort(-1);
			}
		}

		for (i = 0; i < 16; i++) {
			if (this.character.equiped[i] != null) {
				ds.writeShort((this.character.equiped[i]).id);
				ds.writeByte((this.character.equiped[i]).upgrade);
				ds.writeByte((this.character.equiped[i]).sys);
			} else {
				ds.writeShort(-1);
			}
		}
		ds.writeBoolean(true);
		ds.writeBoolean(false);
		ds.writeShort(this.character.head);
		ds.writeShort(this.character.weapon);
		ds.writeShort(this.character.body);
		ds.writeShort(this.character.leg);
		ds.flush();
		this.client.sendMessage(ms);
	}

	public void sendCharInfo(Character pl) throws IOException {
		Message ms = new Message(3);
		DataOutputStream ds = ms.writer();
		ds.writeInt(pl.id);
		ds.writeUTF(pl.clanname);
		if (pl.clanname != "") {
			ds.writeByte(pl.clan);
		}
		ds.writeBoolean(false);
		ds.writeByte(pl.hieuChien);
		ds.writeByte(pl.classId);
		ds.writeByte(pl.gender);
		ds.writeShort(pl.head);
		ds.writeUTF(pl.name);
		ds.writeInt(pl.hp);
		ds.writeInt(pl.maxHP);
		ds.writeByte(pl.level);
		ds.writeShort(pl.weapon);
		ds.writeShort(pl.body);
		ds.writeShort(pl.leg);
		ds.writeByte(-1);
		ds.writeShort(pl.x);
		ds.writeShort(pl.y);
		ds.writeShort(pl.eff5buffhp);
		ds.writeShort(pl.eff5buffmp);
		ds.writeByte(0);
		ds.writeBoolean(true);
		ds.writeBoolean(false);
		ds.writeShort(pl.head);
		ds.writeShort(pl.weapon);
		ds.writeShort(pl.body);
		ds.writeShort(pl.leg);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void charUpdateInMap(Character pl) throws IOException {
		Message ms = messageSubCommand((byte) -119);
		DataOutputStream ds = ms.writer();
		ds.writeInt(pl.id);
		ds.writeInt(pl.hp);
		ds.writeInt(pl.maxHP);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void changePk() throws IOException {
		Message ms = messageSubCommand((byte) -92);
		DataOutputStream ds = ms.writer();
		ds.writeInt(this.character.id);
		ds.writeByte(this.character.typePk);
		ds.flush();
		this.character.sendToMap(ms);
		ms.cleanup();
	}

	public void openUI(byte typeUI) throws IOException {
		Message ms = new Message(30);
		DataOutputStream ds = ms.writer();
		ds.writeByte(typeUI);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void inputDlg(String title, int type) throws IOException {
		Message ms = new Message(92);
		DataOutputStream ds = ms.writer();
		ds.writeUTF(title);
		ds.writeShort(type);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void showInfoLuckyDraw(LuckyDraw lucky) throws IOException {
		Message ms = new Message(53);
		DataOutputStream ds = ms.writer();
		ds.writeUTF("typemoi");
		ds.writeUTF(lucky.name);
		ds.writeShort(lucky.timeCount);
		String[] percen = lucky.getPercen(this.client.user.selectedCharacter.id);
		ds.writeUTF(NinjaUtil.getCurrency(lucky.totalMoney) + "Xu");
		ds.writeShort(Short.parseShort(percen[0]));
		ds.writeUTF(percen[1]);
		ds.writeShort(lucky.mem.size());
		if (!lucky.nameWin.equals("")) {
			ds.writeUTF("Người vừa chiến thắng:" + NinjaUtil.getColor("tahoma_7b_blue") + lucky.nameWin
					+ "\nSố xu thắng: " + NinjaUtil.getCurrency(lucky.xuWin) + "Xu \nSố xu tham gia: "
					+ NinjaUtil.getCurrency(lucky.xuThamGia) + "Xu");
		} else {
			ds.writeUTF("Chưa có thông tin!");
		}
		ds.writeByte(lucky.type);
		ds.writeUTF(NinjaUtil.getCurrency(lucky.getMoneyById(this.client.user.selectedCharacter.id)) + "");
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void endDlg(boolean isResetButton) throws IOException {
		Message ms = new Message(126);
		DataOutputStream ds = ms.writer();
		ds.writeByte(isResetButton ? 0 : 1);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void requestMapTemplate(Message ms) throws IOException {
		int templateId = ms.reader().readUnsignedByte();
		TileMap tilemap = (MapManager.getMapById(templateId)).tilemap;
		Zone zone = this.character.zone;
		ms = messageNotMap((byte) -109);
		DataOutputStream ds = ms.writer();
		ds.writeByte(tilemap.tmw);
		ds.writeByte(tilemap.tmh);
		int size = tilemap.tmw * tilemap.tmh;
		for (int i = 0; i < size; i++) {
			ds.writeByte(tilemap.maps[i]);
		}
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void requestMobTemplate(Message ms) throws IOException {
		int templateId = ms.reader().readUnsignedByte();
		byte zoomLevel = this.client.zoomLevel;
		MonsterTemplate mob = Server.mobs.get(templateId);
		Message mss = messageNotMap((byte) -108);
		DataOutputStream ds = mss.writer();
		ds.writeShort(templateId);
		ds.writeByte(mob.typeFly);
		ds.writeByte(mob.numberImage);
		for (int i = 0; i < mob.numberImage; i++) {
			byte[] ab = NinjaUtil.getFile("Data/Img/Mob/" + zoomLevel + "/" + templateId + "_" + i + ".png");
			ds.writeInt(ab.length);
			ds.write(ab);
		}
		if (mob.isBoss()) {
			ds.writeBoolean(true);
			ds.writeByte(mob.frameBossMove.length);
			for (byte move : mob.frameBossMove) {
				ds.writeByte(move);
			}
			ds.writeByte(mob.frameBossAttack.length);
			for (byte[] attack : mob.frameBossAttack) {
				ds.writeByte(attack.length);
				for (byte att : attack) {
					ds.writeByte(att);
				}
			}
		} else {
			ds.writeBoolean(false);
		}
		if (mob.isBoss()) {
			ds.writeInt(1);
			ds.writeByte(mob.imgInfo.length);
			for (ImageInfo image : mob.imgInfo) {
				ds.writeByte(image.id);
				ds.writeByte(image.x0 * zoomLevel);
				ds.writeByte(image.y0 * zoomLevel);
				ds.writeByte(image.w * zoomLevel);
				ds.writeByte(image.h * zoomLevel);
			}
			ds.writeShort(mob.frameBoss.length);
			for (Frame frame : mob.frameBoss) {
				ds.writeByte(frame.idImg.length);
				for (int j = 0; j < frame.dx.length; j++) {
					ds.writeShort(frame.dx[j]);
					ds.writeShort(frame.dy[j]);
					ds.writeByte(frame.idImg[j]);
				}
			}
		} else {
			ds.writeInt(0);
		}
		ds.writeShort(0);
		ds.flush();
		this.client.sendMessage(mss);
		ms.cleanup();
	}

	public void requestIcon(Message ms) throws IOException {
		int icon = ms.reader().readInt();
		byte index = (byte) (this.client.zoomLevel - 1);
		if (item[index][icon] == null) {
			item[index][icon] = NinjaUtil.getFile("Data/Img/Item/" + this.client.zoomLevel + "/Small" + icon + ".png");
		}
		byte[] ab = item[index][icon];
		Message mss = messageNotMap((byte) -115);
		DataOutputStream ds = mss.writer();
		ds.writeInt(icon);
		ds.writeInt(ab.length);
		ds.write(ab);
		ds.flush();
		this.client.sendMessage(mss);
		ms.cleanup();
	}

	public void removeFriend(String name) throws IOException {
		Message m = messageSubCommand((byte) -83);
		DataOutputStream ds = m.writer();
		ds.writeUTF(name);
		ds.flush();
		this.client.sendMessage(m);
	}

	public void requestFriend() throws IOException {
		Message m = messageSubCommand((byte) -85);
		DataOutputStream ds = m.writer();
		for (Friend friend : this.character.friends.values()) {
			ds.writeUTF(friend.name);
			if (friend.type == 1 && Character.getCharacterByName(friend.name) != null) {
				ds.writeByte(3);
				continue;
			}
			ds.writeByte(friend.type);
		}

		ds.flush();
		this.client.sendMessage(m);
	}

	public void requesItemCharacter(Character.Equiped equiped) throws IOException {
		Message mss = new Message(94);
		DataOutputStream ds = mss.writer();
		ds.writeByte(equiped.entry.type);
		ds.writeLong(equiped.expire);
		ds.writeInt(equiped.yen);
		ds.writeByte(equiped.sys);
		for (ItemOption ab : equiped.options) {
			ds.writeByte(ab.optionTemplate.id);
			ds.writeInt(ab.param);
		}
		ds.flush();
		this.client.sendMessage(mss);
		mss.cleanup();
	}

	public void itemBoxToBag(int index1, int index2) throws IOException {
		Message mss = new Message(16);
		DataOutputStream ds = mss.writer();
		ds.writeByte(index1);
		ds.writeByte(index2);
		ds.flush();
		this.client.sendMessage(mss);
	}

	public void attackMonster(int damage, boolean flag, Monster mob) throws IOException {
		Message mss = new Message(-1);
		DataOutputStream ds = mss.writer();
		ds.writeByte(mob.mobId);
		ds.writeInt(mob.hp);
		ds.writeInt(damage);
		ds.writeBoolean(flag);
		ds.writeByte(mob.levelBoss);
		ds.writeInt(mob.maxhp);
		ds.flush();
		this.character.sendToMap(mss);
	}

	public void attackCharacter(int damage, Character _char) throws IOException {
		Message mss = new Message(62);
		DataOutputStream ds = mss.writer();
		ds.writeInt(_char.id);
		ds.writeInt(_char.hp);
		ds.writeInt(damage);
		ds.flush();
		_char.sendToMap(mss);
	}

	public void attackMonsterMiss(int mobId, int hp) throws IOException {
		Message mss = new Message(51);
		DataOutputStream ds = mss.writer();
		ds.writeByte(mobId);
		ds.writeInt(hp);
		ds.flush();
		this.character.sendToMap(mss);
	}

	public void setSkillPaint_1(ArrayList<Monster> monster) throws IOException {
		Message mss = new Message(60);
		DataOutputStream ds = mss.writer();
		ds.writeInt(this.character.id);
		ds.writeByte(this.character.selectedSkill.skillTemplateId);
		if (monster != null) {
			for (Monster mob : monster) {
				ds.writeByte(mob.mobId);
			}
		}
		ds.flush();
		Character[] characters = this.character.zone.getCharacters();
		for (Character pl : characters) {
			if (!pl.equals(this.character)) {
				pl.sendMessage(mss);
			}
		}
	}

	public void setSkillPaint_2(ArrayList<Character> _chars) throws IOException {
		Message mss = new Message(61);
		DataOutputStream ds = mss.writer();
		ds.writeInt(this.character.id);
		ds.writeByte(this.character.selectedSkill.skillTemplateId);
		if (_chars != null) {
			for (Character pl : _chars) {
				ds.writeByte(pl.id);
			}
		}
		ds.flush();
		Character[] characters = this.character.zone.getCharacters();
		for (Character _char : characters) {
			if (!_char.equals(this.character)) {
				_char.sendMessage(mss);
			}
		}
	}

	public void itemBagToBox(int index1, int index2) throws IOException {
		Message mss = new Message(17);
		DataOutputStream ds = mss.writer();
		ds.writeByte(index1);
		ds.writeByte(index2);
		ds.flush();
		this.client.sendMessage(mss);
		mss.cleanup();
	}

	public void useItem(int index) throws IOException {
		Message mss = new Message(11);
		DataOutputStream ds = mss.writer();
		ds.writeByte(index);
		ds.writeByte(this.character.speed);
		ds.writeInt(this.character.maxHP);
		ds.writeInt(this.character.maxMP);
		ds.writeShort(this.character.eff5buffhp);
		ds.writeShort(this.character.eff5buffmp);
		ds.flush();
		this.client.sendMessage(mss);
		mss.cleanup();
	}

	public void itemBodyToBag(int equipType, int index) throws IOException {
		Message mss = new Message(15);
		DataOutputStream ds = mss.writer();
		ds.writeByte(this.character.speed);
		ds.writeInt(this.character.maxHP);
		ds.writeInt(this.character.maxMP);
		ds.writeShort(this.character.eff5buffhp);
		ds.writeShort(this.character.eff5buffmp);
		ds.writeByte(equipType);
		ds.writeByte(index);
		ds.writeShort(this.character.head);
		ds.flush();
		this.client.sendMessage(mss);
	}

	public void itemInfo(Character.Item item, byte typeUI, byte indexUI) throws IOException {
		Message mss = new Message(42);
		DataOutputStream ds = mss.writer();
		ds.writeByte(typeUI);
		ds.writeByte(indexUI);
		ds.writeLong(item.expire);
		ds.writeInt(item.yen);
		if (item.entry.isTypeBody() || item.entry.isTypeMount() || item.entry.isTypeNgocKham()) {
			ds.writeByte(item.sys);
			for (ItemOption ability : item.options) {
				ds.writeByte(ability.optionTemplate.id);
				ds.writeInt(ability.param);
			}
		} else if (item.id == 233 || item.id == 234 || item.id == 235) {
			byte[] ab = NinjaUtil
					.getFile("Data/Img/Item/" + this.user.client.zoomLevel + "/Small" + item.entry.icon + ".png");
			ds.writeInt(ab.length);
			ds.write(ab);
		}
		ds.flush();
		this.client.sendMessage(mss);
		mss.cleanup();
	}

	public void itemStoreInfo(ItemStore item, byte typeUI, byte indexUI) throws IOException {
		Message mss = new Message(42);
		DataOutputStream ds = mss.writer();
		ds.writeByte(typeUI);
		ds.writeByte(indexUI);
		ds.writeLong(item.expire);
		ds.writeInt(item.xu);
		ds.writeInt(item.yen);
		ds.writeInt(item.luong);
		if (item.entry.isTypeBody() || item.entry.isTypeMount() || item.entry.isTypeNgocKham()) {
			ds.writeByte(item.sys);
			for (int[] ability : item.option_max) {
				ds.writeByte(ability[0]);
				ds.writeInt(ability[1]);
			}
		}
		ds.flush();
		this.client.sendMessage(mss);
		mss.cleanup();
	}

	public void equipedInfo(Character.Equiped equiped, byte typeUI, byte indexUI) throws IOException {
		Message mss = new Message(42);
		DataOutputStream ds = mss.writer();
		ds.writeByte(typeUI);
		ds.writeByte(indexUI);
		ds.writeLong(equiped.expire);
		ds.writeInt(equiped.yen);
		ds.writeByte(equiped.sys);
		for (ItemOption ability : equiped.options) {
			ds.writeByte(ability.optionTemplate.id);
			ds.writeInt(ability.param);
		}
		ds.flush();
		this.client.sendMessage(mss);
		mss.cleanup();
	}

	public void updatePotential() throws IOException {
		Message ms = messageSubCommand((byte) -109);
		DataOutputStream ds = ms.writer();
		ds.writeByte(this.character.speed);
		ds.writeInt(this.character.maxHP);
		ds.writeInt(this.character.maxMP);
		ds.writeShort(this.character.point);
		ds.writeShort(this.character.potential[0]);
		ds.writeShort(this.character.potential[1]);
		ds.writeInt(this.character.potential[2]);
		ds.writeInt(this.character.potential[3]);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	protected void levelUp() throws IOException {
		Message ms = messageSubCommand((byte) -124);
		DataOutputStream ds = ms.writer();
		ds.writeByte(this.character.speed);
		ds.writeInt(this.character.maxHP);
		ds.writeInt(this.character.maxMP);
		ds.writeLong(this.character.exp);
		ds.writeShort(this.character.spoint);
		ds.writeShort(this.character.point);
		ds.writeShort(this.character.potential[0]);
		ds.writeShort(this.character.potential[1]);
		ds.writeInt(this.character.potential[2]);
		ds.writeInt(this.character.potential[3]);
		ds.flush();
		this.character.sendMessage(ms);
		ms.cleanup();
	}

	public void expandBag(Character.Item item) throws IOException {
		Message ms = messageSubCommand((byte) -91);
		DataOutputStream ds = ms.writer();
		ds.writeByte(this.character.numberCellBag);
		ds.writeByte(item.index);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void updateSkill() throws IOException {
		Message ms = messageSubCommand((byte) -125);
		DataOutputStream ds = ms.writer();
		ds.writeByte(this.character.speed);
		ds.writeInt(this.character.maxHP);
		ds.writeInt(this.character.maxMP);
		ds.writeShort(this.character.spoint);
		ds.writeByte(this.character.listSkill.size());
		for (Character.MySkill my : this.character.listSkill) {
			ds.writeShort((SkillData.getSkill(this.character.classId, my.id, my.point)).skillId);
		}
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void bagSort() throws IOException {
		this.client.sendMessage(messageSubCommand((byte) -107));
	}

	public void boxSort() throws IOException {
		this.client.sendMessage(messageSubCommand((byte) -106));
	}

	public void outZone(int id) throws IOException {
		Message ms = new Message(2);
		DataOutputStream ds = ms.writer();
		ds.writeInt(id);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void throwItem(byte index, short itemMapId, short itemId, short x, short y) throws IOException {
		Message ms = new Message(-12);
		DataOutputStream ds = ms.writer();
		ds.writeByte(index);
		ds.writeShort(itemMapId);
		ds.writeShort(x);
		ds.writeShort(y);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();

		ms = new Message(-6);
		ds = ms.writer();
		ds.writeInt(this.character.id);
		ds.writeShort(itemMapId);
		ds.writeShort(itemId);
		ds.writeShort(x);
		ds.writeShort(y);
		ds.flush();
		Character[] characters = this.character.zone.getCharacters();
		for (Character _char : characters) {
			if (!_char.equals(this.character)) {

				_char.sendMessage(ms);
			}
		}
		ms.cleanup();
	}

	public void sendVersion() throws IOException {
		if (cache[0] == null) {
			cache[0] = NinjaUtil.getFile("cache/version");
		}
		Message ms = messageNotMap((byte) -123);
		DataOutputStream ds = ms.writer();
		ds.write(cache[0]);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void sendMap() throws IOException {
		if (cache[1] == null) {
			cache[1] = NinjaUtil.getFile("cache/map");
		}
		Message ms = messageNotMap((byte) -121);
		DataOutputStream ds = ms.writer();
		ds.write(cache[1]);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void sendData() throws IOException {
		if (cache[2] == null) {
			cache[2] = NinjaUtil.getFile("cache/data");
		}
		Message ms = messageNotMap((byte) -122);
		DataOutputStream ds = ms.writer();
		ds.write(cache[2]);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void sendSkill() throws IOException {
		if (cache[3] == null) {
			cache[3] = NinjaUtil.getFile("cache/skill");
		}
		Message ms = messageNotMap((byte) -120);
		DataOutputStream ds = ms.writer();
		ds.write(cache[3]);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void sendItem() throws IOException {
		if (cache[4] == null) {
			cache[4] = NinjaUtil.getFile("cache/item");
		}
		Message ms = messageNotMap((byte) -119);
		DataOutputStream ds = ms.writer();
		ds.write(cache[4]);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void startOKDlg(String text) throws IOException {
		Message ms = new Message(-26);
		DataOutputStream ds = ms.writer();
		ds.writeUTF(text);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void addInfo(String text) throws IOException {
		Message ms = new Message(-25);
		DataOutputStream ds = ms.writer();
		ds.writeUTF(text);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void updateItem(Character.Item item) throws IOException {
		Message ms = new Message(7);
		DataOutputStream ds = ms.writer();
		ds.writeByte(item.index);
		ds.writeShort(item.quantity);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void removeItem(int index, int quantity) throws IOException {
		Message ms = new Message(18);
		DataOutputStream ds = ms.writer();
		ds.writeByte(index);
		ds.writeShort(quantity);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void viewInfo(Character pl) throws IOException {
		Message ms = new Message(93);
		DataOutputStream ds = ms.writer();
		ds.writeInt(pl.id);
		ds.writeUTF(pl.name);
		ds.writeShort(pl.head);
		ds.writeByte(pl.gender);
		ds.writeByte(pl.classId);
		ds.writeByte(pl.hieuChien);
		ds.writeInt(pl.hp);
		ds.writeInt(pl.maxHP);
		ds.writeInt(pl.mp);
		ds.writeInt(pl.maxMP);
		ds.writeByte(pl.speed);
		ds.writeShort(99);
		ds.writeShort(98);
		ds.writeShort(97);
		ds.writeInt(pl.dame);
		ds.writeInt(pl.dameDown);
		ds.writeShort(pl.exactly);
		ds.writeShort(pl.miss);
		ds.writeShort(765);
		ds.writeShort(654);
		ds.writeShort(543);
		ds.writeShort(432);
		ds.writeByte(pl.level);
		ds.writeShort(321);
		ds.writeUTF(pl.clanname);
		if (!pl.clanname.equals("")) {
			ds.writeByte(pl.clan);
		}
		ds.writeShort(321);
		ds.writeShort(123);
		ds.writeShort(234);
		ds.writeShort(345);
		ds.writeShort(456);
		ds.writeShort(567);
		ds.writeShort(678);
		ds.writeShort(789);
		ds.writeShort(135);
		ds.writeShort(246);
		ds.writeShort(357);
		ds.writeByte(pl.countFinishDay);
		ds.writeByte(pl.countLoosBoss);
		ds.writeByte(pl.countPB);
		ds.writeByte(pl.limitTiemNangSo);
		ds.writeByte(pl.limitKyNangSo);
		for (int i = 0; i < 16; i++) {
			if (pl.equiped[i] != null) {
				ds.writeShort((pl.equiped[i]).id);
				ds.writeByte((pl.equiped[i]).upgrade);
				ds.writeByte((pl.equiped[i]).sys);
			}
		}
		ds.flush();
		this.user.client.sendMessage(ms);
		ms.cleanup();
		if (pl.user != this.user) {
			pl.user.service.addInfoMe(this.character.name + " đang xem thông tin của bạn!");
		}
	}

	public void deleteItemBody(Character.Equiped equiped) throws IOException {
		Message ms = new Message(-80);
		DataOutputStream ds = ms.writer();
		ds.writeByte(equiped.entry.type);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void addItem(Character.Item item) throws IOException {
		Message ms = new Message(8);
		DataOutputStream ds = ms.writer();
		ds.writeByte(item.index);
		ds.writeShort(item.id);
		ds.writeBoolean(item.isLock);
		if (item.entry.isTypeBody() || item.entry.isTypeNgocKham()) {
			ds.writeByte(item.upgrade);
		}
		ds.writeBoolean((item.expire != -1L));
		ds.writeShort(item.quantity);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	public void tradeAccept() {
		this.client.sendMessage(new Message(46));
	}

	public void startWaitDlg() {
		this.client.sendMessage(new Message(-16));
	}

	public void addInfoMe(String text) throws IOException {
		Message ms = new Message(-24);
		DataOutputStream ds = ms.writer();
		ds.writeUTF(text);
		ds.flush();
		this.client.sendMessage(ms);
		ms.cleanup();
	}

	private Message messageNotLogin(byte command) throws IOException {
		Message ms = new Message(-29);
		ms.writer().writeByte(command);
		return ms;
	}

	private Message messageNotMap(byte command) throws IOException {
		Message ms = new Message(-28);
		ms.writer().writeByte(command);
		return ms;
	}

	private Message messageSubCommand(byte command) throws IOException {
		Message ms = new Message(-30);
		ms.writer().writeByte(command);
		return ms;
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * server\Service.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */