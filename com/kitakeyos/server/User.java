package com.kitakeyos.server;

import com.kitakeyos.data.ItemData;
import com.kitakeyos.db.Connect;
import com.kitakeyos.io.Message;
import com.kitakeyos.io.Session;
import com.kitakeyos.object.Effect;
import com.kitakeyos.object.Friend;
import com.kitakeyos.option.ItemOption;
import com.kitakeyos.util.Logger;
import com.kitakeyos.util.NinjaUtil;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class User {
	private Logger logger = new Logger(getClass());
	public Session client;
	public Service service;
	public HashMap<String, Character> characters;
	public int id;
	public String username;
	private byte lock;
	public int luong;
	public Character selectedCharacter;

	public User(Session client) {
		this.client = client;
	}

	public static synchronized User login(Session cl, String username, String password, String random)
			throws IOException {
		User us = new User(cl);
		us.service = new Service(cl, us);
		try {
			if (username.equals("-1") && password.equals("12345")) {
				us.service.startOKDlg("Vào vừa thôi!");
				return null;
			}
			Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
			Matcher m1 = p.matcher(username);
			Matcher m2 = p.matcher(password);
			if (!m1.find() || !m2.find()) {
				us.service.startOKDlg("Tên đăng nhập không được chứa ký tự đặc biệt!");
				return null;
			}
			PreparedStatement stmt = Connect.conn
					.prepareStatement("SELECT * FROM `user` WHERE `username` = ? AND `password` = ? LIMIT 1");
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.first()) {
				if (us.lock == 1) {
					us.service.startOKDlg("Tài khoản đã bị khoá! Vui lòng liên hệ admin để biết thêm chi tiết");
					return null;
				}
				us.id = resultSet.getInt("id");
				User u = Server.getUser(us.id);
				if (u != null) {
					u.service.startOKDlg("Có người đăng nhập vào tài khoản của bạn.");
					Thread.sleep(1000L);
					u.client.closeMessage();
					us.service.startOKDlg("Tài khoản đã có người đăng nhập.");
					return null;
				}
				us.username = resultSet.getString("username");
				us.lock = resultSet.getByte("lock");
				us.luong = resultSet.getInt("luong");
				resultSet.close();
				stmt = Connect.conn.prepareStatement("UPDATE `user` SET `online` = ? WHERE `id` = ?");
				stmt.setInt(1, 1);
				stmt.setInt(2, us.id);
				stmt.execute();
				us.initCharacterList();
				return us;
			}
			resultSet.close();
			us.service.startOKDlg(Language.getString("LOGIN_FAIL", new Object[0]));
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException ex) {
//			Logger.getLogger(User.class.getName()).log(Level.SEVERE, (String) null, ex);
		}
		return null;
	}

	public void initCharacterList() throws SQLException {
		this.characters = new HashMap<>();
		PreparedStatement stmt = Connect.conn.prepareStatement("SELECT * FROM `player` WHERE `user_id` = ? LIMIT 3;");
		stmt.setInt(1, this.id);
		ResultSet resultSet = stmt.executeQuery();
		while (resultSet.next()) {
			Character _char = new Character();
			_char.id = resultSet.getInt("id");
			_char.name = resultSet.getString("name");
			_char.gender = resultSet.getByte("gender");
			_char.classId = resultSet.getByte("class");
			switch (_char.classId) {
			case 1:
				_char.school = "Ninja kiếm";
				break;

			case 2:
				_char.school = "Ninja tiêu";
				break;

			case 3:
				_char.school = "Ninja kunai";
				break;

			case 4:
				_char.school = "Ninja cung";
				break;

			case 5:
				_char.school = "Ninja đao";
				break;

			case 6:
				_char.school = "Ninja quạt";
				break;

			default:
				_char.school = "Chưa vào trường";
				break;
			}
			_char.original_head = _char.head = resultSet.getShort("head");
			_char.weapon = -1;
			_char.body = -1;
			_char.leg = -1;
			JSONObject json = (JSONObject) JSONValue.parse(resultSet.getString("data"));
			_char.exp = ((Long) json.get("exp")).longValue();
			long num = _char.exp;
			for (int i = 0; i < Server.exps.length; i++) {
				if (num < Server.exps[i]) {
					_char.level = i;
					_char.expR = num;
					break;
				}
				num -= Server.exps[i];
			}
			_char.expDown = ((Long) json.get("expDown")).intValue();
			_char.hieuChien = ((Long) json.get("hieuChien")).byteValue();
			_char.countFinishDay = ((Long) json.get("countFinishDay")).byteValue();
			_char.countLoosBoss = ((Long) json.get("countLoosBoss")).byteValue();
			_char.countPB = ((Long) json.get("countPB")).byteValue();
			_char.limitKyNangSo = ((Long) json.get("limitKyNangSo")).byteValue();
			_char.limitTiemNangSo = ((Long) json.get("limitTiemNangSo")).byteValue();
			if (json.get("tayTiemNang") != null) {
				_char.tayTiemNang = ((Long) json.get("tayTiemNang")).shortValue();
			} else {
				_char.tayTiemNang = 0;
			}
			if (json.get("tayKyNang") != null) {
				_char.tayKyNang = ((Long) json.get("tayKyNang")).shortValue();
			} else {
				_char.tayKyNang = 0;
			}
			if (json.get("numberUseExpanedBag") != null) {
				_char.numberUseExpanedBag = ((Long) json.get("numberUseExpanedBag")).byteValue();
			} else {
				_char.numberUseExpanedBag = 0;
			}
			_char.equiped = new Character.Equiped[16];
			JSONArray jso = (JSONArray) JSONValue.parse(resultSet.getString("equiped"));
			if (jso != null) {
				int size = jso.size();
				for (int j = 0; j < size; j++) {
					JSONObject obj = (JSONObject) jso.get(j);
					int id = ((Long) obj.get("id")).intValue();
					Character.Equiped equiped = new Character.Equiped(id);
					if (equiped.entry.isTypeWeapon()) {
						_char.weapon = equiped.entry.part;
					}
					if (equiped.entry.type == 2) {
						_char.body = equiped.entry.part;
					}
					if (equiped.entry.type == 6) {
						_char.leg = equiped.entry.part;
					} else if (equiped.entry.type == 11) {
						_char.head = equiped.entry.part;
					}
					equiped.upgrade = ((Long) obj.get("upgrade")).byteValue();
					equiped.sys = ((Long) obj.get("sys")).byteValue();
					equiped.expire = ((Long) obj.get("expire")).longValue();
					equiped.yen = ((Long) obj.get("yen")).intValue();
					JSONArray ability = (JSONArray) obj.get("options");
					int size2 = ability.size();
					equiped.options = new ArrayList<>();
					for (int c = 0; c < size2; c++) {
						JSONArray jAbility = (JSONArray) ability.get(c);
						int templateId = ((Long) jAbility.get(0)).intValue();
						int param = ((Long) jAbility.get(1)).intValue();
						equiped.options.add(new ItemOption(templateId, param));
					}
					_char.equiped[equiped.entry.type] = equiped;
				}
			}
			this.characters.put(_char.name, _char);
		}
		resultSet.close();
	}

	public synchronized void createCharacter(Message ms) throws IOException {
		try {
			if (this.characters.size() >= 3) {
				this.service.startOKDlg("Bạn chỉ được tạo tối đa 3 nhân vật.");
				return;
			}
			String name = ms.reader().readUTF();
			Pattern p = Pattern.compile("^[a-z0-9]+$");
			Matcher m1 = p.matcher(name);
			if (!m1.find()) {
				this.service.startOKDlg(Language.getString("CREATE_CHAR_FAIL_4", new Object[0]));
				return;
			}
			byte gender = ms.reader().readByte();
			byte head = ms.reader().readByte();
			byte[] h = null;
			if (gender == 0) {
				h = new byte[] { 11, 26, 27, 28 };
				gender = 0;
			} else {
				h = new byte[] { 2, 23, 24, 25 };
				gender = 1;
			}
			byte temp = h[0];
			for (byte b : h) {
				if (head == b) {
					temp = b;
					break;
				}
			}
			head = temp;
			if (name.length() < 6 || name.length() > 20) {
				this.service.startOKDlg(Language.getString("CREATE_CHAR_FAIL_1", new Object[0]));
				return;
			}
			ResultSet check = Connect.stat.executeQuery("SELECT * FROM `player` WHERE `user_id` = '" + this.id + "';");
			if (check.last() && check.getRow() >= 3) {
				this.service.startOKDlg(Language.getString("CREATE_CHAR_FAIL_5", new Object[0]));

				return;
			}
			check.close();
			check = Connect.stat.executeQuery("SELECT * FROM `player` WHERE `name` = '" + name + "';");
			if (check.last() && check.getRow() > 0) {
				this.service.startOKDlg(Language.getString("CREATE_CHAR_FAIL_2", new Object[0]));

				return;
			}
			check.close();
			PreparedStatement stmt = Connect.conn.prepareStatement(
					"INSERT INTO player(`user_id`, `name`, `gender`, `head`, `xu`, `yen`, `equiped`, `bag`, `box`, `mount`, `effect`, `friend`) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			stmt.setInt(1, this.id);
			stmt.setString(2, name);
			stmt.setByte(3, gender);
			stmt.setShort(4, (short) head);
			stmt.setInt(5, 550000000);
			stmt.setInt(6, 500000000);
			stmt.setString(7, "[]");
			stmt.setString(8, "[]");
			stmt.setString(9, "[]");
			stmt.setString(10, "[]");
			stmt.setString(11, "[]");
			stmt.setString(12, "[]");
			stmt.execute();
			initCharacterList();
			this.service.selectChar();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			this.service.startOKDlg(Language.getString("CREATE_CHAR_FAIL_3", new Object[0]));
		}
	}

	public static synchronized void newPlay(String rand, User us) {
		try {
			ResultSet result = Connect.stat
					.executeQuery("SELECT * FROM `user` WHERE `username` = '" + rand + "' LIMIT 1;");
			if (!result.first()) {
				Connect.stat.execute("INSERT INTO `user`(`username`, `password`, `online`, `luong`) VALUES ('" + rand
						+ "', 'kitakeyos', 0, 999999);");
			}
			result.close();
		} catch (SQLException ex) {
			us.logger.debug("newPlay", ex.toString());
		}
	}

	public synchronized void charInitSelected() {
		try {
			PreparedStatement stmt = Connect.conn.prepareStatement("SELECT * FROM `player` WHERE `id` = ? LIMIT 1;");
			stmt.setInt(1, this.selectedCharacter.id);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.first()) {
				this.selectedCharacter.xu = resultSet.getInt("xu");
				this.selectedCharacter.xuInBox = resultSet.getInt("xuInBox");
				this.selectedCharacter.yen = resultSet.getInt("yen");
				JSONArray ja = (JSONArray) JSONValue.parse(resultSet.getString("map"));
				this.selectedCharacter.mapId = ((Long) ja.get(0)).shortValue();
				this.selectedCharacter.x = ((Long) ja.get(1)).shortValue();
				this.selectedCharacter.y = ((Long) ja.get(2)).shortValue();
				this.selectedCharacter.saveCoordinate = resultSet.getShort("saveCoordinate");
				this.selectedCharacter.point = resultSet.getShort("point");
				this.selectedCharacter.spoint = resultSet.getShort("spoint");
				JSONArray jArr = (JSONArray) JSONValue.parse(resultSet.getString("potential"));
				int len = jArr.size();
				this.selectedCharacter.potential = new short[4];
				int i;
				for (i = 0; i < 4; i++) {
					if (jArr.get(i) != null) {
						this.selectedCharacter.potential[i] = ((Long) jArr.get(i)).shortValue();
					} else {
						this.selectedCharacter.potential[i] = 5;
					}
				}
				jArr = (JSONArray) JSONValue.parse(resultSet.getString("skill"));
				len = jArr.size();
				this.selectedCharacter.listSkill = new ArrayList<>();
				for (i = 0; i < len; i++) {
					JSONObject obj = (JSONObject) jArr.get(i);
					Character.MySkill skill = new Character.MySkill();
					skill.id = ((Long) obj.get("id")).intValue();
					skill.point = ((Long) obj.get("point")).intValue();
					this.selectedCharacter.listSkill.add(skill);
				}
				this.selectedCharacter.mount = new Character.Mount[5];
				JSONArray jso = (JSONArray) JSONValue.parse(resultSet.getString("mount"));
				if (jso != null) {
					int n = jso.size();
					for (int i1 = 0; i1 < n; i1++) {
						JSONObject obj = (JSONObject) jso.get(i1);
						int id = ((Long) obj.get("id")).intValue();
						Character.Mount mount = new Character.Mount(id);
						mount.entry = ItemData.getItemEntryById(mount.id);
						mount.level = ((Long) obj.get("level")).byteValue();
						mount.sys = ((Long) obj.get("sys")).byteValue();
						mount.expire = ((Long) obj.get("expire")).longValue();
						mount.yen = ((Long) obj.get("yen")).intValue();
						JSONArray ability = (JSONArray) obj.get("options");
						int size2 = ability.size();
						mount.options = new ArrayList<>();
						for (int c = 0; c < size2; c++) {
							JSONArray jAbility = (JSONArray) ability.get(c);
							int templateId = ((Long) jAbility.get(0)).intValue();
							int param = ((Long) jAbility.get(1)).intValue();
							mount.options.add(new ItemOption(templateId, param));
						}
						this.selectedCharacter.mount[mount.entry.type - 29] = mount;
					}
				}
				this.selectedCharacter.clanname = "";
				this.selectedCharacter.numberCellBag = resultSet.getByte("numberCellBag");
				this.selectedCharacter.numberCellBox = resultSet.getByte("numberCellBox");
				this.selectedCharacter.bag = new Character.Item[this.selectedCharacter.numberCellBag];
				jso = (JSONArray) JSONValue.parse(resultSet.getString("bag"));
				if (jso != null) {
					int n = jso.size();
					for (int i1 = 0; i1 < n; i1++) {
						JSONObject obj = (JSONObject) jso.get(i1);
						int id = ((Long) obj.get("id")).intValue();
						Character.Item item = new Character.Item(id);
						item.index = ((Long) obj.get("index")).intValue();
						item.isLock = ((Boolean) obj.get("isLock")).booleanValue();
						item.sys = ((Long) obj.get("sys")).byteValue();
						item.expire = ((Long) obj.get("expire")).longValue();
						if (item.expire == -1L || System.currentTimeMillis() <= item.expire) {

							item.yen = ((Long) obj.get("yen")).intValue();
							if (item.entry.isTypeBody() || item.entry.isTypeMount() || item.entry.isTypeNgocKham()) {
								item.upgrade = ((Long) obj.get("upgrade")).byteValue();
								JSONArray ability = (JSONArray) obj.get("options");
								int size2 = ability.size();
								item.options = new ArrayList<>();
								for (int c = 0; c < size2; c++) {
									JSONArray jAbility = (JSONArray) ability.get(c);
									int templateId = ((Long) jAbility.get(0)).intValue();
									int param = ((Long) jAbility.get(1)).intValue();
									item.options.add(new ItemOption(templateId, param));
								}
							} else {
								item.upgrade = 0;
							}
							if (item.entry.isUpToUp) {
								item.quantity = ((Long) obj.get("quantity")).shortValue();
							} else {
								item.quantity = 1;
							}
							this.selectedCharacter.bag[item.index] = item;
						}
					}
				}
				this.selectedCharacter.box = new Character.Item[this.selectedCharacter.numberCellBox];
				jso = (JSONArray) JSONValue.parse(resultSet.getString("box"));
				if (jso != null) {
					int n = jso.size();
					for (int i1 = 0; i1 < n; i1++) {
						JSONObject obj = (JSONObject) jso.get(i1);
						int id = ((Long) obj.get("id")).intValue();
						Character.Item item = new Character.Item(id);
						item.index = ((Long) obj.get("index")).intValue();
						item.isLock = ((Boolean) obj.get("isLock")).booleanValue();
						item.sys = ((Long) obj.get("sys")).byteValue();
						item.expire = ((Long) obj.get("expire")).longValue();
						item.yen = ((Long) obj.get("yen")).intValue();
						if (item.entry.isTypeBody() || item.entry.isTypeMount() || item.entry.isTypeNgocKham()) {
							item.upgrade = ((Long) obj.get("upgrade")).byteValue();
							JSONArray ability = (JSONArray) obj.get("options");
							int size2 = ability.size();
							item.options = new ArrayList<>();
							for (int c = 0; c < size2; c++) {
								JSONArray jAbility = (JSONArray) ability.get(c);
								int templateId = ((Long) jAbility.get(0)).intValue();
								int param = ((Long) jAbility.get(1)).intValue();
								item.options.add(new ItemOption(templateId, param));
							}
						} else {
							item.upgrade = 0;
						}
						if (item.entry.isUpToUp) {
							item.quantity = ((Long) obj.get("quantity")).shortValue();
						} else {
							item.quantity = 1;
						}
						this.selectedCharacter.box[item.index] = item;
					}
				}
				JSONArray j = (JSONArray) JSONValue.parse(resultSet.getString("onOSKill"));
				this.selectedCharacter.onOSkill = new byte[j.size()];
				int t;
				for (t = 0; t < this.selectedCharacter.onOSkill.length; t++) {
					this.selectedCharacter.onOSkill[t] = ((Long) j.get(t)).byteValue();
				}
				j = (JSONArray) JSONValue.parse(resultSet.getString("onCSKill"));
				this.selectedCharacter.onCSkill = new byte[j.size()];
				for (t = 0; t < this.selectedCharacter.onCSkill.length; t++) {
					this.selectedCharacter.onCSkill[t] = ((Long) j.get(t)).byteValue();
				}
				j = (JSONArray) JSONValue.parse(resultSet.getString("onKSKill"));
				this.selectedCharacter.onKSkill = new byte[j.size()];
				for (t = 0; t < this.selectedCharacter.onKSkill.length; t++) {
					this.selectedCharacter.onKSkill[t] = ((Long) j.get(t)).byteValue();
				}
				this.selectedCharacter.effects = new HashMap<>();
				JSONArray effects = (JSONArray) JSONValue.parse(resultSet.getString("effect"));
				int size = effects.size();
				for (int k = 0; k < size; k++) {
					JSONObject job = (JSONObject) effects.get(k);
					byte templateId = ((Long) job.get("id")).byteValue();
					int timeStart = ((Long) job.get("timeStart")).intValue();
					int timeLength = ((Long) job.get("timeLength")).intValue();
					short param = ((Long) job.get("param")).shortValue();
					Effect eff = new Effect(templateId, timeStart, timeLength, param);
					this.selectedCharacter.effects.put(Byte.valueOf(eff.template.type), eff);
				}
				JSONArray friend = (JSONArray) JSONValue.parse(resultSet.getString("friend"));
				size = friend.size();
				this.selectedCharacter.friends = new HashMap<>();
				for (int m = 0; m < size; m++) {
					JSONObject job = (JSONObject) friend.get(m);
					byte type = ((Long) job.get("type")).byteValue();
					String name = job.get("name").toString();
					this.selectedCharacter.friends.put(name, new Friend(name, type));
				}
				this.selectedCharacter.user = this;
			}
			resultSet.close();
		} catch (SQLException ex) {
			this.logger.debug("charInitSelected", ex.getMessage());
		}
	}

	public synchronized void selectToChar(Message ms) throws IOException {
		String username = ms.reader().readUTF();
		this.selectedCharacter = this.characters.get(username);
		this.characters.clear();
		if (this.selectedCharacter != null) {
			charInitSelected();
			this.service.setChar(this.selectedCharacter);
			this.selectedCharacter.setAbility();
			this.selectedCharacter.hp = this.selectedCharacter.maxHP;
			this.selectedCharacter.mp = this.selectedCharacter.maxMP;
			for (Effect eff : this.selectedCharacter.effects.values()) {
				this.service.addEffect(eff);
			}
			Character.characters_name.put(this.selectedCharacter.name, this.selectedCharacter);
			Character.characters_id.put(Integer.valueOf(this.selectedCharacter.id), this.selectedCharacter);
			Map map = MapManager.getMapById(this.selectedCharacter.mapId);
			Collection<Zone> zones = map.getZones();
			byte zoneId = 0;
			for (Zone zone : zones) {
				if (zone.numberCharacter > 15) {
					zoneId = (byte) (zoneId + 1);
				}
			}

			int size = zones.size();
			if (zoneId < 0 || zoneId > size) {
				zoneId = (byte) NinjaUtil.nextInt(size);
			}
			MapManager.joinZone(this.selectedCharacter, this.selectedCharacter.mapId, zoneId);
			this.selectedCharacter.hasJoin = true;
			this.service.sendInfo();
			this.service.sendBox();
			this.service.sendZone();
			this.service.sendItemMap();
			this.service.showAlert("Thông Báo",
					"- Chúc anh em trải nghiệm game vui vẻ. \n Share File Server Bởi LMK Army2 Private.\n\n- Số người đang hoạt động: "
							+ Character.characters_id.size());
		} else {
			this.service.startOKDlg("Không thể tìm thấy nhân vật!");
			this.client.closeMessage();
		}
	}

	public synchronized void close() {
		try {
			if (this.client.login) {
				if (this.selectedCharacter != null && this.selectedCharacter.hasJoin) {
					this.selectedCharacter.flushCache();
				}
				this.selectedCharacter = null;
				PreparedStatement stmt = Connect.conn
						.prepareStatement("UPDATE `user` SET `luong` = ?, `online` = ? WHERE `id` = ? LIMIT 1;");
				stmt.setInt(1, this.luong);
				stmt.setInt(2, 0);
				stmt.setInt(3, this.id);
				stmt.execute();
				this.service = null;
				this.characters = null;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public String toString() {
		return this.username;
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * server\User.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */