
package com.kitakeyos.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.kitakeyos.data.ItemData;
import com.kitakeyos.data.SkillData;
import com.kitakeyos.data.StoreData;
import com.kitakeyos.db.Connect;
import com.kitakeyos.io.Message;
import com.kitakeyos.object.Effect;
import com.kitakeyos.object.Friend;
import com.kitakeyos.object.ItemEntry;
import com.kitakeyos.object.ItemMap;
import com.kitakeyos.object.ItemStore;
import com.kitakeyos.object.Skill;
import com.kitakeyos.object.Waypoint;
import com.kitakeyos.option.ItemOption;
import com.kitakeyos.option.SkillOption;
import com.kitakeyos.template.NpcTemplate;
import com.kitakeyos.template.SkillTemplate;
import com.kitakeyos.util.Logger;
import com.kitakeyos.util.NinjaUtil;

public class Character {
	protected int id;
	protected User user;
	protected String name;
	protected byte gender;
	protected String school;
	protected byte classId;
	protected int level;
	protected short head;
	protected short original_head;
	protected short weapon;
	protected short body;
	protected short leg;
	protected int xu;
	protected int xuInBox;
	protected int yen;
	protected int hp;
	protected int maxHP;
	protected int mp;
	protected int maxMP;
	private Logger logger = new Logger(getClass());

	protected int dame;

	protected int dame2;

	protected int dameDown;

	protected int exactly;

	protected int miss;

	protected int resFire;

	protected int resIce;
	protected int resWind;
	protected long exp;
	protected long expDown;
	protected long expR;
	protected byte hieuChien;
	protected byte typePk;
	protected String clanname;
	protected short clan;
	protected short[] potential;
	protected ArrayList<MySkill> listSkill;
	protected short point;
	protected short spoint;
	protected byte speed = 8;
	protected byte numberCellBag;
	protected byte numberCellBox;
	protected Map map;
	protected Zone zone;
	protected short mapId;
	protected short x;
	protected short y;
	protected short eff5buffhp;
	protected short eff5buffmp;
	protected byte captcha = 0;
	protected byte[] onKSkill;
	protected byte[] onOSkill;
	protected byte[] onCSkill;
	protected ArrayList<Integer> menu = new ArrayList<>();
	protected Item[] bag;
	protected Item[] box;
	protected Equiped[] equiped;
	protected Trade trade;
	protected Trade.Trader trader;
	protected byte countPB;
	protected byte countFinishDay;
	protected byte countLoosBoss;
	protected byte limitTiemNangSo;
	protected byte limitKyNangSo;
	protected short tayTiemNang;
	protected short tayKyNang;
	protected int[] options = new int[127];
	public static HashMap<String, Character> characters_name = new HashMap<>();
	public static HashMap<Integer, Character> characters_id = new HashMap<>();
	protected short saveCoordinate;
	private static final short[] ITEM_IN_SELECT_CARD = new short[] { 434, 434, 435, 435, 8, 9, 10, 11, 12, 30, 242, 249,
			250, 252, 253, 254, 255, 257, 283, 311, 312, 313, 314, 315, 316, 343, 344, 345, 346, 375, 376, 377, 378,
			379, 380, 403, 404, 405, 406, 407, 408, 409, 410, 419, 523, 547, 552, 553, 554, 555, 556, 557, 558, 559,
			560, 561, 562, 563 };
	private static final byte[] EXPIRE_DATE_OF_TIME = new byte[] { 3, 7, 15, 30 };
	private static final int[] YEN_IN_SELECT_CARD = new int[] { 10000, 20000, 30000, 50000, 100000, 200000, 500000,
			1000000, 5000000 };
	public Character enemy;
	public Mount[] mount;
	public SelectedSkill selectedSkill = new SelectedSkill();
	public HashMap<Byte, Effect> effects;
	public boolean isDead;
	public ArrayList<Byte> removeEffect = new ArrayList<>();
	public int multiExp;
	public HashMap<String, Friend> friends;
	public byte numberUseExpanedBag;
	public long lastTimeRecovery = 0L;
	public boolean hasJoin = false;

	public void update() {
		try {
			long now = System.currentTimeMillis();
			int hp = 0;
			int mp = 0;
			this.multiExp = 1;
			for (Entry<Byte, Effect> entry : this.effects.entrySet()) {
				int i;
				Effect eff = entry.getValue();
				if (eff == null || eff.timeStart >= eff.timeLength) {
					this.removeEffect.add(entry.getKey());
					continue;
				}
				switch (eff.template.type) {
				case 0:
					i = eff.param * 2;
					hp += i;
					mp += i;
					break;
				case 18:
					this.multiExp = eff.param;
					break;
				case 17:
					hp += eff.param * 2;
					break;
				}

				eff.timeStart++;
			}
			if (now - this.lastTimeRecovery >= 5000L) {
				this.lastTimeRecovery = now;
				hp += this.options[30] + this.options[120];
				mp += this.options[27] + this.options[119];
			}
			if (!this.isDead) {
				this.hp += hp;
				this.mp += hp;
				if (this.hp > this.maxHP) {
					this.hp = this.maxHP;
				}
				if (this.mp > this.maxMP) {
					this.mp = this.maxMP;
				}
			}
			for (Iterator<Byte> iterator = this.removeEffect.iterator(); iterator.hasNext();) {
				byte b = ((Byte) iterator.next()).byteValue();
				this.user.service.removeEffect(this.effects.get(Byte.valueOf(b)));
				this.effects.remove(Byte.valueOf(b));
			}

			this.removeEffect.clear();
		} catch (IOException ex) {
			this.logger.error("update", ex.getMessage());
		}
	}

	public static class SelectedSkill {
		public short skillTemplateId;
		public int[] options;
		public int manaUse;
		public byte maxFight;
		public int coolDown;
		public Character.MySkill skill;
		public boolean haveLearned;
		public int dx;
		public int dy;
	}

	public static class Item implements Cloneable {
		public int id;
		public int index;
		public int quantity;
		public long expire;
		public byte upgrade;
		public byte sys;
		public boolean isLock;
		public int yen;
		public ArrayList<ItemOption> options;
		public ItemEntry entry;

		public Item(int id) {
			this.id = id;
			this.entry = ItemData.getItemEntryById(id);
		}

		public Item clone() {
			try {
				Item item = (Item) super.clone();
				if (this.options != null) {
					item.options = (ArrayList<ItemOption>) this.options.clone();
				}
				return item;
			} catch (CloneNotSupportedException cloneNotSupportedException) {

				return null;
			}
		}

		public void next(int next) {
			if (next == 0) {
				return;
			}
			this.isLock = true;
			this.upgrade = (byte) (this.upgrade + next);
			if (this.options != null)
				for (int i = 0; i < this.options.size(); i++) {
					ItemOption itemOption = this.options.get(i);
					if (itemOption.optionTemplate.id == 6 || itemOption.optionTemplate.id == 7) {
						itemOption.param += (short) (15 * next);
					} else if (itemOption.optionTemplate.id == 8 || itemOption.optionTemplate.id == 9
							|| itemOption.optionTemplate.id == 19) {
						itemOption.param += (short) (10 * next);
					} else if (itemOption.optionTemplate.id == 10 || itemOption.optionTemplate.id == 11
							|| itemOption.optionTemplate.id == 12 || itemOption.optionTemplate.id == 13
							|| itemOption.optionTemplate.id == 14 || itemOption.optionTemplate.id == 15
							|| itemOption.optionTemplate.id == 17 || itemOption.optionTemplate.id == 18
							|| itemOption.optionTemplate.id == 20) {
						itemOption.param += (short) (5 * next);
					} else if (itemOption.optionTemplate.id == 21 || itemOption.optionTemplate.id == 22
							|| itemOption.optionTemplate.id == 23 || itemOption.optionTemplate.id == 24
							|| itemOption.optionTemplate.id == 25 || itemOption.optionTemplate.id == 26) {
						itemOption.param += (short) (150 * next);
					} else if (itemOption.optionTemplate.id == 16) {
						itemOption.param += (short) (3 * next);
					}
				}
		}
	}

	public static class Mount {
		public int id;
		public byte level;
		public byte sys;
		public long expire;
		public int yen;
		public ArrayList<ItemOption> options;
		public ItemEntry entry;

		public Mount(int id) {
			this.id = id;
			this.entry = ItemData.getItemEntryById(id);
		}
	}

	public static class Equiped {
		public int id;
		public byte upgrade;
		public byte sys;
		public long expire;
		public int yen;
		public ArrayList<ItemOption> options;
		public ItemEntry entry;

		public Equiped(int id) {
			this.id = id;
			this.entry = ItemData.getItemEntryById(id);
		}
	}

	public static class MySkill {
		public int id;

		public int point;

		public long lastTimeUseSkill;
	}

	public void upSkill(Message ms) throws IOException {
		short skillId = ms.reader().readShort();
		byte point = ms.reader().readByte();
		if (point > this.spoint || point < 0) {
			return;
		}
		int size = this.listSkill.size();
		for (int i = 0; i < size; i++) {
			if (((MySkill) this.listSkill.get(i)).id == skillId) {
				SkillTemplate tem = SkillData.getTemplate(this.classId, skillId);
				if (tem.maxPoint - ((MySkill) this.listSkill.get(i)).point >= point) {
					for (Skill skill : tem.skills) {
						if (skill.point == ((MySkill) this.listSkill.get(i)).point + point
								&& skill.level > this.level) {
							startOKDlg("Trình độ không đủ yêu cầu!");

							return;
						}
					}
					((MySkill) this.listSkill.get(i)).point += point;
					this.spoint = (short) (this.spoint - 1);
					setAbility();
					this.hp = this.maxHP;
					this.mp = this.maxMP;
					this.user.service.updateSkill();
					break;
				}
				startOKDlg("Điểm nhập không hợp lệ!");
				break;
			}
		}
	}

	public void upPotential(Message ms) throws IOException {
		byte index = ms.reader().readByte();
		short point = ms.reader().readShort();
		if (index < 0 || index > 4) {
			startOKDlg("Không hợp lệ!");
			return;
		}
		if (point > this.point) {
			startOKDlg("Bạn không đủ điểm!");
			return;
		}
		this.potential[index] = (short) (this.potential[index] + point);
		setAbility();
		this.hp = this.maxHP;
		this.mp = this.maxMP;
		this.point = (short) (this.point - point);
		this.user.service.updatePotential();
	}

	public void removeItem(int index, int quantity, boolean isUpdate) {
		try {
			if (this.bag[index] != null) {
				this.logger.log("removeItem index= " + index + " quantity=" + quantity);
				(this.bag[index]).quantity -= quantity;
				if (isUpdate) {
					this.user.service.removeItem(index, quantity);
				}
				if ((this.bag[index]).quantity <= 0) {
					this.bag[index] = null;
				}
			}
		} catch (IOException ex) {
			this.logger.error("removeItem", ex.getMessage());
		}
	}

	public boolean checkItemExist(Item item) {
		for (Item bag : this.bag) {
			if (bag != null && bag.id == item.id) {
				return true;
			}
		}
		return false;
	}

	public int getIndexItemByIdInBag(int id) {
		for (Item bag : this.bag) {
			if (bag != null && bag.id == id) {
				return bag.index;
			}
		}
		return -1;
	}

	public int getIndexItemByIdInBox(int id) {
		for (Item box : this.box) {
			if (box != null && box.id == id) {
				return box.index;
			}
		}
		return -1;
	}

	public int getNumberItem(int id) {
		int number = 0;
		for (Item bag : this.bag) {
			if (bag != null && bag.id == id) {
				number += bag.quantity;
			}
		}
		return number;
	}

	public void useItemChangeMap(Message ms) throws IOException {
		byte indexUI = ms.reader().readByte();
		byte indexMenu = ms.reader().readByte();
		if (this.bag[indexUI] != null && ((this.bag[indexUI]).id == 35 || (this.bag[indexUI]).id == 37)) {
			this.user.service.startWaitDlg();
			(new short[10])[0] = 1;
			(new short[10])[1] = 27;
			(new short[10])[2] = 72;
			(new short[10])[3] = 10;
			(new short[10])[4] = 17;
			(new short[10])[5] = 22;
			(new short[10])[6] = 32;
			(new short[10])[7] = 38;
			(new short[10])[8] = 43;
			(new short[10])[9] = 48;
			short map = (new short[10])[indexMenu];
			short[] xy = NinjaUtil.getXY(map);
			this.x = xy[0];
			this.y = xy[1];
			changeMap(map);
			if ((this.bag[indexUI]).id == 35 || ((this.bag[indexUI]).id == 37 && (this.bag[indexUI]).expire != -1L
					&& (this.bag[indexUI]).expire < (new Date()).getTime())) {
				removeItem(indexUI, 1, true);
			}
		}
	}

	public void useEquipment(Item item) throws IOException {
		if (item.entry.isTypeWeapon() && (((this.classId == 0 || this.classId == 1) && !item.entry.isKiem())
				|| (this.classId == 2 && !item.entry.isTieu()) || (this.classId == 3 && !item.entry.isKunai())
				|| (this.classId == 4 && !item.entry.isCung()) || (this.classId == 5 && !item.entry.isDao())
				|| (this.classId == 6 && !item.entry.isQuat()))) {
			startOKDlg("Vũ khí không thích hợp!");

			return;
		}
		byte indexUI = (byte) item.index;
		byte index = item.entry.type;
		if (item.entry.isTypeWeapon()) {
			this.weapon = item.entry.part;
			this.user.service.useWeapon();
		} else if (item.entry.type == 2) {
			this.body = item.entry.part;
			this.user.service.useShirt();
		} else if (item.entry.type == 6) {
			this.leg = item.entry.part;
			this.user.service.usePant();
		} else if (item.entry.type == 11) {
			this.head = item.entry.part;
			this.user.service.useMask();
		}
		if (this.equiped[index] != null) {
			Item it = new Item((this.equiped[index]).id);
			it.upgrade = (this.equiped[index]).upgrade;
			it.sys = (this.equiped[index]).sys;
			it.expire = (this.equiped[index]).expire;
			it.yen = (this.equiped[index]).yen;
			it.options = (this.equiped[index]).options;
			it.index = indexUI;
			it.isLock = true;
			this.equiped[index] = new Equiped(item.id);
			(this.equiped[index]).id = item.id;
			(this.equiped[index]).upgrade = item.upgrade;
			(this.equiped[index]).sys = item.sys;
			(this.equiped[index]).expire = item.expire;
			(this.equiped[index]).yen = item.yen;
			(this.equiped[index]).options = item.options;
			this.bag[indexUI] = it;
		}
		if (this.equiped[index] == null) {
			this.equiped[index] = new Equiped(item.id);
			(this.equiped[index]).id = item.id;
			(this.equiped[index]).upgrade = item.upgrade;
			(this.equiped[index]).sys = item.sys;
			(this.equiped[index]).expire = item.expire;
			(this.equiped[index]).yen = item.yen;
			(this.equiped[index]).options = item.options;
			this.bag[indexUI] = null;
		}
		setAbility();
		this.user.service.useItem(indexUI);
	}

	public void useItem(Message ms) throws IOException {
		byte indexUI = ms.reader().readByte();
		if (indexUI >= 0 && indexUI <= this.numberCellBag) {
			boolean isMount = true;
			Item item = this.bag[indexUI];
			if (this.level < item.entry.level) {
				startOKDlg("Trình độ không đạt yêu cầu!");
				return;
			}
			if ((item.entry.gender == 0 || item.entry.gender == 1) && item.entry.gender != this.gender) {
				startOKDlg("Giới tính không phù hợp.");
				return;
			}
			if (item != null && item.quantity >= 1) {

				if (item.entry.isTypeBody()) {
					useEquipment(item);
					return;
				}
				if (item.id == 34 || item.id == 36) {
					this.user.service.startWaitDlg();
					short[] xy = NinjaUtil.getXY(this.saveCoordinate);
					this.x = xy[0];
					this.y = xy[1];
					if (item.id == 34) {
						removeItem(item.index, item.quantity, true);
					}
					changeMap(this.saveCoordinate);
				} else if (item.entry.isTypeMount()) {
					isMount = true;
					byte index = (byte) (item.entry.type - 29);
					if (this.mount[index] == null) {
						this.mount[index] = new Mount(item.id);
						(this.mount[index]).id = item.id;
						(this.mount[index]).level = item.upgrade;
						(this.mount[index]).expire = item.expire;
						(this.mount[index]).yen = item.yen;
						(this.mount[index]).options = item.options;
						(this.mount[index]).sys = item.sys;
						removeItem(item.index, item.quantity, true);
					} else {
						Item temp = item.clone();
						this.bag[indexUI] = new Item((this.mount[index]).id);
						(this.bag[indexUI]).quantity = 1;
						(this.bag[indexUI]).index = indexUI;
						(this.bag[indexUI]).isLock = true;
						(this.bag[indexUI]).sys = (this.mount[index]).sys;
						(this.bag[indexUI]).upgrade = (this.mount[index]).level;
						(this.bag[indexUI]).expire = (this.mount[index]).expire;
						(this.bag[indexUI]).options = (this.mount[index]).options;
						(this.bag[indexUI]).yen = (this.mount[index]).yen;
						this.mount[index] = new Mount(temp.id);
						(this.mount[index]).id = temp.id;
						(this.mount[index]).level = temp.upgrade;
						(this.mount[index]).sys = temp.sys;
						(this.mount[index]).expire = temp.expire;
						(this.mount[index]).yen = temp.yen;
						(this.mount[index]).options = temp.options;
					}
				} else if (item.entry.type == 27) {
					if (item.id == 248) {
						int time = 18000;
						short param = 2;
						byte template = 22;
						Effect effect = new Effect(template, 0, time, param);
						byte type = effect.template.type;
						Effect temp = this.effects.get(Byte.valueOf(type));
						if (temp != null) {
							temp.timeLength += time;
							this.user.service.replaceEffect(temp);
						} else {
							this.effects.put(Byte.valueOf(type), effect);
							this.user.service.addEffect(effect);
						}
						removeItem(item.index, item.quantity, true);
					} else if (item.id == 240) {
						this.tayTiemNang = (short) (this.tayTiemNang + 1);
						removeItem(item.index, item.quantity, true);
					} else if (item.id == 241) {
						this.tayKyNang = (short) (this.tayKyNang + 1);
						removeItem(item.index, item.quantity, true);
					} else if (item.id == 252) {
						if (this.limitKyNangSo < 3) {
							this.limitKyNangSo = (byte) (this.limitKyNangSo + 1);
							this.spoint = (short) (this.spoint + 1);
							this.user.service.updateSkill();
							removeItem(item.index, item.quantity, true);
							this.user.service.addInfoMe("Bạn nhận được 1 điểm kỹ năng.");
						} else {
							startOKDlg("Bạn chỉ được học 3 lần.");
						}
					} else if (item.id == 253) {
						if (this.limitTiemNangSo < 3) {
							this.limitTiemNangSo = (byte) (this.limitTiemNangSo + 1);
							this.point = (short) (this.point + 10);
							this.user.service.updatePotential();
							removeItem(item.index, item.quantity, true);
							this.user.service.addInfoMe("Bạn nhận được 10 điểm tiềm năng.");
						} else {
							startOKDlg("Bạn chỉ được học 3 lần.");
						}
					} else if (item.id == 215 || item.id == 229 || item.id == 283) {
						expandBag(item);
					} else {
						learnSkill(item);
					}
				} else {
					if (item.entry.type == 16) {
						if (this.hp == this.maxHP) {
							this.user.service.addInfoMe("HP đã đầy.");
							return;
						}
						int time = 3;
						short param = 0;
						if (item.id == 13) {
							param = 25;
						} else if (item.id == 14) {
							param = 90;
						} else if (item.id == 15) {
							param = 230;
						} else if (item.id == 16) {
							param = 400;
						} else if (item.id == 17) {
							param = 650;
						} else if (item.id == 565) {
							param = 1500;
						}
						byte template = 21;
						Effect effect = new Effect(template, 0, time, param);
						byte type = effect.template.type;
						Effect temp = this.effects.get(Byte.valueOf(type));
						if (temp != null) {
							this.user.service.replaceEffect(effect);
						} else {
							this.user.service.addEffect(effect);
						}
						this.effects.put(Byte.valueOf(type), effect);
						removeItem(item.index, 1, true);
						return;
					}
					if (item.entry.type == 17) {
						if (this.mp == this.maxMP) {
							this.user.service.addInfoMe("MP đã đầy.");
							return;
						}
						int mp = 0;
						switch (item.id) {

						case 18:
							mp = 150;
							break;

						case 19:
							mp = 500;
							break;

						case 20:
							mp = 1000;
							break;

						case 21:
							mp = 2000;
							break;

						case 22:
							mp = 3500;
							break;

						case 566:
							mp = 5000;
							break;
						}
						this.mp += mp;
						this.user.service.updateMp();
						removeItem(item.index, 1, true);
					} else if (item.entry.type == 18) {
						int time = 0;
						short param = 0;
						byte template = 0;
						switch (item.id) {
						case 23:
							time = 1800;
							param = 3;
							template = 0;
							break;

						case 24:
							time = 1800;
							param = 20;
							template = 1;
							break;

						case 25:
							time = 1800;
							param = 30;
							template = 2;
							break;

						case 26:
							time = 1800;
							param = 40;
							template = 3;
							break;

						case 27:
							time = 1800;
							param = 50;
							template = 4;
							break;

						case 29:
							time = 1800;
							param = 60;
							template = 28;
							break;

						case 30:
							time = 259200;
							param = 60;
							template = 28;
							break;

						case 249:
							time = 259200;
							param = 40;
							template = 3;
							break;

						case 250:
							time = 259200;
							param = 50;
							template = 4;
							break;

						case 409:
							time = 86400;
							param = 75;
							template = 30;
							break;

						case 410:
							time = 86400;
							param = 90;
							template = 31;
							break;

						case 567:
							time = 86400;
							param = 120;
							template = 35;
							break;
						}

						Effect effect = new Effect(template, 0, time, param);
						byte type = effect.template.type;
						Effect temp = this.effects.get(Byte.valueOf(type));
						if (temp != null) {
							this.user.service.replaceEffect(effect);
						} else {
							this.user.service.addEffect(effect);
						}
						this.effects.put(Byte.valueOf(type), effect);
						removeItem(item.index, item.quantity, true);
					}
				}

			}
			this.user.service.useItem(indexUI);
			if (isMount) {
				this.user.service.sendMount();
			}
		}
	}

	public void itemMountToBag(Message ms) throws IOException {
		byte indexUI = ms.reader().readByte();
		if (indexUI < 0 || indexUI > 4) {
			return;
		}
		if (this.mount[indexUI] == null) {
			return;
		}
		if (indexUI == 4
				&& (this.mount[0] != null || this.mount[1] != null || this.mount[2] != null || this.mount[3] != null)) {
			startOKDlg("Vui lòng tháo phụ kiện trước!");
			return;
		}
		Mount mount = this.mount[indexUI];
		for (int a = 0; a < this.numberCellBag; a++) {
			if (this.bag[a] == null) {
				this.bag[a] = new Item(mount.id);
				(this.bag[a]).upgrade = mount.level;
				(this.bag[a]).sys = mount.sys;
				(this.bag[a]).expire = mount.expire;
				(this.bag[a]).yen = mount.yen;
				(this.bag[a]).options = mount.options;
				(this.bag[a]).isLock = true;
				(this.bag[a]).quantity = 1;
				(this.bag[a]).index = a;
				this.mount[indexUI] = null;
				this.user.service.itemMountToBag(indexUI, a);
				return;
			}
		}
		startOKDlg("Hành trang không đủ chỗ trống!");
	}

	public void itemBagToBox(Message ms) throws IOException {
		byte indexUI = ms.reader().readByte();
		if (indexUI < 0 || indexUI > this.numberCellBag) {
			return;
		}
		if (this.bag[indexUI] == null) {
			return;
		}
		itemBagToBox(this.bag[indexUI]);
	}

	public void itemBoxToBag(Message ms) throws IOException {
		byte indexUI = ms.reader().readByte();
		if (indexUI < 0 || indexUI > this.numberCellBox) {
			return;
		}
		if (this.box[indexUI] == null) {
			return;
		}
		itemBoxToBag(this.box[indexUI]);
	}

	public void itemBodyToBag(Message ms) throws IOException {
		byte indexUI = ms.reader().readByte();
		if (indexUI >= 0 && indexUI < 16) {
			Equiped equiped = this.equiped[indexUI];
			if (equiped != null) {
				for (int a = 0; a < this.numberCellBag; a++) {
					if (this.bag[a] == null) {
						if (equiped.entry.type == 2) {
							this.body = -1;
							this.user.service.useShirt();
						} else if (equiped.entry.type == 6) {
							this.leg = -1;
							this.user.service.usePant();
						} else if (equiped.entry.type == 11) {
							this.head = this.original_head;
							this.user.service.useMask();
						} else if (equiped.entry.isTypeWeapon()) {
							this.weapon = -1;
							this.user.service.useWeapon();
						}
						this.bag[a] = new Item(equiped.id);
						(this.bag[a]).upgrade = equiped.upgrade;
						(this.bag[a]).sys = equiped.sys;
						(this.bag[a]).expire = equiped.expire;
						(this.bag[a]).yen = equiped.yen;
						(this.bag[a]).options = equiped.options;
						(this.bag[a]).isLock = true;
						(this.bag[a]).quantity = 1;
						(this.bag[a]).index = a;
						this.equiped[indexUI] = null;
						this.user.service.itemBodyToBag((this.bag[a]).entry.type, (this.bag[a]).index);
						setAbility();
						return;
					}
				}
				startOKDlg("Hành trang không đủ chỗ trống!");
				return;
			}
		}
	}

	public boolean addItemToBag(Item item) {
		try {
			if (item == null) {
				return false;
			}
			int index = getIndexItemByIdInBag(item.id);
			if (index == -1 || !(this.bag[index]).entry.isUpToUp || (this.bag[index]).expire != -1L
					|| item.isLock != (this.bag[index]).isLock) {
				for (int i = 0; i < this.numberCellBag; i++) {
					if (this.bag[i] == null) {
						this.bag[i] = new Item(item.id);
						(this.bag[i]).index = i;
						(this.bag[i]).quantity = item.quantity;
						(this.bag[i]).options = item.options;
						(this.bag[i]).isLock = item.isLock;
						(this.bag[i]).expire = item.expire;
						(this.bag[i]).upgrade = item.upgrade;
						(this.bag[i]).yen = item.yen;
						(this.bag[i]).sys = item.sys;
						this.user.service.addItem(this.bag[i]);
						return true;
					}
				}
			} else {
				(this.bag[index]).quantity += item.quantity;
				this.user.service.addItem(this.bag[index]);
				return true;
			}
		} catch (IOException ex) {
			this.logger.error("addItemToBag", ex.getMessage());
		}
		return false;
	}

	public boolean itemBagToBox(Item item) {
		try {
			if (item == null) {
				return false;
			}
			int index = getIndexItemByIdInBox(item.id);
			if (index == -1 || !(this.box[index]).entry.isUpToUp || (this.box[index]).expire != -1L
					|| item.isLock != (this.box[index]).isLock) {
				for (int i = 0; i < this.numberCellBox; i++) {
					if (this.box[i] == null) {
						this.box[i] = new Item(item.id);
						(this.box[i]).index = i;
						(this.box[i]).quantity = item.quantity;
						(this.box[i]).options = item.options;
						(this.box[i]).isLock = item.isLock;
						(this.box[i]).expire = item.expire;
						(this.box[i]).upgrade = item.upgrade;
						(this.box[i]).yen = item.yen;
						(this.box[i]).sys = item.sys;
						this.bag[item.index] = null;
						this.user.service.itemBagToBox(item.index, i);
						return true;
					}
				}
				startOKDlg("Rương đồ không đủ chỗ trống");
				return false;
			}
			(this.box[index]).quantity += item.quantity;
			this.user.service.itemBagToBox(item.index, index);
			return true;
		} catch (IOException ex) {
			this.logger.error("itemBagToBox", ex.getMessage());

			return false;
		}
	}

	public boolean itemBoxToBag(Item item) {
		try {
			if (item == null) {
				return false;
			}
			int index = getIndexItemByIdInBag(item.id);
			if (index == -1 || !(this.bag[index]).entry.isUpToUp || (this.bag[index]).expire != -1L
					|| item.isLock != (this.bag[index]).isLock) {
				for (int i = 0; i < this.numberCellBag; i++) {
					if (this.bag[i] == null) {
						this.bag[i] = new Item(item.id);
						(this.bag[i]).index = i;
						(this.bag[i]).quantity = item.quantity;
						(this.bag[i]).options = item.options;
						(this.bag[i]).isLock = item.isLock;
						(this.bag[i]).expire = item.expire;
						(this.bag[i]).upgrade = item.upgrade;
						(this.bag[i]).yen = item.yen;
						(this.bag[i]).sys = item.sys;
						this.box[item.index] = null;
						this.user.service.itemBoxToBag(item.index, i);
						return true;
					}
				}
				startOKDlg("Hành trang không đủ chỗ trống");
				return false;
			}
			(this.bag[index]).quantity += item.quantity;
			this.user.service.itemBoxToBag(item.index, index);
			return true;
		} catch (IOException ex) {
			this.logger.error("itemBoxToBag", ex.getMessage());

			return false;
		}
	}

	public void updateItem(int id, int quantity) {
		try {
			for (int i = 0; i < this.numberCellBag; i++) {
				if (this.bag[i] != null && (this.bag[i]).id == id) {
					(this.bag[i]).quantity += quantity;
					if ((this.bag[i]).quantity <= 0) {
						this.user.service.removeItem((this.bag[i]).index, quantity);
						this.bag[i] = null;
					} else {
						this.user.service.updateItem(this.bag[i]);
					}
					return;
				}
			}
		} catch (IOException ex) {
			this.logger.error("updateItem", ex.getMessage());
		}
	}

	public void boxSort() {
		Vector<Item> myVector = new Vector();
		int length = this.box.length;
		for (int i = 0; i < length; i++) {
			Item item = this.box[i];
			if (item != null && item.entry.isUpToUp && item.expire == -1L) {
				myVector.addElement(item);
			}
		}
		int size = myVector.size();
		for (int j = 0; j < size; j++) {
			Item item2 = myVector.elementAt(j);
			if (item2 != null) {
				for (int k = j + 1; k < size; k++) {
					Item item3 = myVector.elementAt(k);
					if (item3 != null && item2.entry.equals(item3.entry) && item2.isLock == item3.isLock) {
						item2.quantity += item3.quantity;
						this.box[item3.index] = null;
						myVector.setElementAt(null, k);
					}
				}
			}
		}
		for (int l = 0; l < length; l++) {
			if (this.box[l] != null) {
				for (int m = 0; m <= l; m++) {
					if (this.box[m] == null) {
						this.box[m] = this.box[l];
						(this.box[m]).index = m;
						this.box[l] = null;
						break;
					}
				}
			}
		}
	}

	public void bagSort() {
		Vector<Item> myVector = new Vector();
		int length = this.bag.length;
		for (int i = 0; i < length; i++) {
			Item item = this.bag[i];
			if (item != null && item.entry.isUpToUp && item.expire == -1L) {
				myVector.addElement(item);
			}
		}
		int size = myVector.size();
		for (int j = 0; j < size; j++) {
			Item item2 = myVector.elementAt(j);
			if (item2 != null) {
				for (int k = j + 1; k < size; k++) {
					Item item3 = myVector.elementAt(k);
					if (item3 != null && item2.entry.equals(item3.entry) && item2.isLock == item3.isLock) {
						item2.quantity += item3.quantity;
						this.bag[item3.index] = null;
						myVector.setElementAt(null, k);
					}
				}
			}
		}
		for (int l = 0; l < length; l++) {
			if (this.bag[l] != null) {
				for (int m = 0; m <= l; m++) {
					if (this.bag[m] == null) {
						this.bag[m] = this.bag[l];
						(this.bag[m]).index = m;
						this.bag[l] = null;
						break;
					}
				}
			}
		}
	}

	public void move(Message ms) throws IOException {
		short x = ms.reader().readShort();
		short y = ms.reader().readShort();
		this.x = x;
		this.y = y;
		Message mss = new Message(1);
		DataOutputStream ds = mss.writer();
		ds.writeInt(this.id);
		ds.writeShort(this.x);
		ds.writeShort(this.y);
		ds.flush();
		sendToMap(mss);
		mss.cleanup();
	}

	public void chatGlobal(Message ms) throws IOException {
		if (this.user.luong < 5) {
			startOKDlg(Language.getString("NOT_ENOUGH_LUONG", new Object[0]));
			return;
		}
		updateLuong(this.user.luong - 5);
		String text = ms.reader().readUTF();
		Message mss = new Message(-21);
		DataOutputStream ds = mss.writer();
		ds.writeUTF(this.name);
		ds.writeUTF(text);
		ds.flush();
		Server.sendToServer(mss);
	}

	public void chatPrivate(Message ms) throws IOException {
		String to = ms.reader().readUTF();
		String text = ms.reader().readUTF();
		Character _char = getCharacterByName(to);
		if (_char == null || this.name.equals(to)) {
			return;
		}
		Message mss = new Message(-22);
		DataOutputStream ds = mss.writer();
		ds.writeUTF(this.name);
		ds.writeUTF(text);
		ds.flush();
		_char.sendMessage(mss);
		mss.cleanup();
	}

	public void tradeInvite(Message ms) throws IOException {
		int charId = ms.reader().readInt();
		Character _char = this.zone.findCharInMap(charId);
		int rangeX = NinjaUtil.getRange(_char.x, this.x);
		int rangeY = NinjaUtil.getRange(_char.y, this.y);
		if (rangeX > 100 || rangeY > 100) {
			startOKDlg("Khoảng cách quá xa!");
			return;
		}
		if (_char != null) {
			Message mss = new Message(43);
			DataOutputStream ds = mss.writer();
			ds.writeInt(this.id);
			ds.flush();
			_char.user.client.sendMessage(mss);
			mss.cleanup();
		}
	}

	public void selectCard(Message ms) throws IOException {
		byte index = ms.reader().readByte();
		if (getNumberItem(340) <= 0) {
			startOKDlg("Bạn không có phiếu may mắn!");
			return;
		}
		if (getSlotNull() == 0) {
			startOKDlg("Không đủ chỗ trống.");
			return;
		}
		removeItem(getIndexItemByIdInBag(340), 1, true);
		Item item = null;
		int indexItem = NinjaUtil.nextInt(ITEM_IN_SELECT_CARD.length);
		int idItem = ITEM_IN_SELECT_CARD[indexItem];
		int yen = 0;
		if (idItem == 12) {
			yen = YEN_IN_SELECT_CARD[NinjaUtil.nextInt(YEN_IN_SELECT_CARD.length)];
		} else {
			item = new Item(idItem);
			item.quantity = 1;
			item.isLock = false;
			item.sys = 0;
			item.upgrade = 0;
			item.options = new ArrayList<>();
			if (item.entry.isTypeMount()) {
				item.expire = (new Date()).getTime()
						+ (EXPIRE_DATE_OF_TIME[NinjaUtil.nextInt(EXPIRE_DATE_OF_TIME.length)] * 24 * 60 * 60 * 1000);
				item.options.add(new ItemOption(65, 1000));
				item.options.add(new ItemOption(66, 1000));
				item.yen = 5;
				item.sys = 0;
			} else {
				item.yen = 0;
				item.expire = -1L;
			}
		}
		Message mss = new Message(-28);
		DataOutputStream ds = mss.writer();
		ds.writeByte(-72);
		for (int i = 0; i < 9; i++) {
			if (index == i) {
				ds.writeShort(idItem);
			} else {
				int indexItem2 = NinjaUtil.nextInt(ITEM_IN_SELECT_CARD.length);
				int idItem2 = ITEM_IN_SELECT_CARD[indexItem2];
				ds.writeShort(idItem2);
			}
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
		if (idItem == 12) {
			addYen(yen);
			this.user.service.addInfoMe("Bạn nhận được " + NinjaUtil.getCurrency(yen) + " Yên");
		} else {
			addItemToBag(item);
		}
	}

	public void tradeItemLock(Message ms) throws IOException {
		int xu = ms.reader().readInt();
		if (xu > 0 && xu <= this.xu) {
			this.trader.coinTradeOrder = xu;
		}
		byte len = ms.reader().readByte();
		this.trader.itemTradeOrder = new ArrayList<>();
		for (int i = 0; i < len; i++) {
			byte index = ms.reader().readByte();
			if (this.bag[index] != null && !(this.bag[index]).isLock) {
				this.trader.itemTradeOrder.add(this.bag[index]);
			}
		}
		this.trade.tradeItemLock(this.trader);
	}

	public void tradeClose() throws IOException {
		this.trade.closeMenu();
	}

	public void tradeAccept() throws IOException {
		this.trader.accept = true;
	}

	public void acceptInviteTrade(Message ms) throws IOException {
		int charId = ms.reader().readInt();
		Character _char = this.zone.findCharInMap(charId);
		if (_char != null) {
			this.trade = new Trade();
			_char.trader = this.trade.trader_1;
			_char.trader.character = _char;
			this.trader = this.trade.trader_2;
			this.trader.character = this;
			this.trade.start();
		}
	}

	public void chatPublic(Message ms) throws IOException {
		String s = ms.reader().readUTF();
		if ("info".equals(s)) {
			startOKDlg("mapId: " + this.mapId + " - X: " + this.x + " - Y: " + this.y);
			return;
		}
		if ("dungdz@24022003".equals(s) && this.id == 1) {
			Server.stop();
			return;
		}
		if ("xu".equals(s) && this.id == 1) {
			addXu(500000000);
			return;
		}
		if ("yen".equals(s) && this.id == 1) {
			addYen(500000000);
			return;
		}
		if ("upgrade".equals(s)) {
			int i;
			for (i = 0; i < 10; i++) {
				Item item = new Item(11);
				item.quantity = 1;
				item.isLock = false;
				item.expire = -1L;
				item.upgrade = 0;
				item.yen = 0;
				item.sys = 0;
				addItemToBag(item);
			}
			for (i = 0; i < 2; i++) {
				Item item = new Item(475);
				item.quantity = 1;
				item.isLock = false;
				item.expire = -1L;
				item.upgrade = 0;
				item.yen = 0;
				item.sys = 0;
				addItemToBag(item);
			}
			return;
		}
		String[] test = s.split(":");
		if (test.length == 2 && test[0].equals("map")) {
			this.x = 35;
			this.y = 10;
			changeMap(Integer.parseInt(test[1]));
			return;
		}
		Message mss = new Message(-23);
		DataOutputStream ds = mss.writer();
		ds.writeInt(this.id);
		ds.writeUTF(s);
		ds.flush();
		sendToMap(mss);
		mss.cleanup();
	}

	public void attackMonster(Message ms) throws IOException {
		this.logger.log("Attack Monster");
		ArrayList<Monster> mobs = new ArrayList<>();
		while (ms.reader().available() > 0) {
			int id = ms.reader().readUnsignedByte();
			Monster mob = this.zone.getMonsterById(id);
			if (mob != null && mob.hp > 0) {
				mobs.add(mob);
			}
		}
		if (mobs.size() == 0) {
			return;
		}
		if (!this.selectedSkill.haveLearned) {
			return;
		}
		byte maxFight = this.selectedSkill.maxFight;
		int manaUse = this.selectedSkill.manaUse;
		int coolDown = this.selectedSkill.coolDown;
		MySkill mySkill = this.selectedSkill.skill;
		long lastTimeUseSkill = mySkill.lastTimeUseSkill;
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis > lastTimeUseSkill) {
			mySkill.lastTimeUseSkill = currentTimeMillis;
		} else {
			return;
		}
		int rangeX = NinjaUtil.getRange(this.x, ((Monster) mobs.get(0)).x);
		int rangeY = NinjaUtil.getRange(this.y, ((Monster) mobs.get(0)).y);
		if (rangeX > this.selectedSkill.dx + 30 || rangeY > this.selectedSkill.dy + 30) {
			return;
		}
		if (mobs.size() > maxFight) {
			return;
		}
		if (this.mp < manaUse) {
			return;
		}
		this.mp -= manaUse;
		attackMonster(mobs);
	}

	public void learnSkill(Item item) throws IOException {
		if ((item.id >= 40 && item.id <= 93) || (item.id >= 311 && item.id <= 316) || (item.id >= 375 && item.id <= 380)
				|| item.id == 547 || (item.id >= 552 && item.id <= 563)) {
			short skillId = (short) (item.id - 39);
			if (item.id == 311) {
				skillId = 55;
			} else if (item.id == 312) {
				skillId = 56;
			} else if (item.id == 313) {
				skillId = 57;
			} else if (item.id == 314) {
				skillId = 58;
			} else if (item.id == 315) {
				skillId = 59;
			} else if (item.id == 316) {
				skillId = 60;
			} else if (item.id == 375) {
				skillId = 61;
			} else if (item.id == 376) {
				skillId = 62;
			} else if (item.id == 377) {
				skillId = 63;
			} else if (item.id == 378) {
				skillId = 64;
			} else if (item.id == 379) {
				skillId = 65;
			} else if (item.id == 380) {
				skillId = 66;
			} else if (item.id == 547) {
				switch (this.classId) {
				case 1:
					skillId = 67;
					break;

				case 2:
					skillId = 68;
					break;

				case 3:
					skillId = 69;
					break;

				case 4:
					skillId = 70;
					break;

				case 5:
					skillId = 71;
					break;

				case 6:
					skillId = 72;
					break;
				}
			} else if (item.id == 552) {
				skillId = 73;
			} else if (item.id == 553) {
				skillId = 78;
			} else if (item.id == 554) {
				skillId = 75;
			} else if (item.id == 555) {
				skillId = 76;
			} else if (item.id == 556) {
				skillId = 74;
			} else if (item.id == 557) {
				skillId = 77;
			} else if (item.id == 558) {
				skillId = 79;
			} else if (item.id == 559) {
				skillId = 83;
			} else if (item.id == 560) {
				skillId = 81;
			} else if (item.id == 561) {
				skillId = 82;
			} else if (item.id == 562) {
				skillId = 80;
			} else if (item.id == 563) {
				skillId = 84;
			}
			for (MySkill my : this.listSkill) {
				if (my.id == skillId && my.point >= 0) {
					startOKDlg("Kĩ năng này đã học!");
					return;
				}
			}
			Skill skill = SkillData.getSkill(this.classId, skillId, 0);
			if (skill != null) {
				MySkill my = new MySkill();
				my.id = skillId;
				my.point = 1;
				this.listSkill.add(my);
				selectSkill(skillId);
				this.user.service.learnSkill((byte) item.index, (short) skill.skillId);
				this.user.service.updateSkill();
				removeItem(item.index, item.quantity, false);
			} else {
				startOKDlg("Sách nay không phù hợp!");
			}
		}
	}

	public void attackMonster(ArrayList<Monster> mobs) throws IOException {
		this.user.service.setSkillPaint_1(mobs);
		for (Monster mob : mobs) {
			int damage = NinjaUtil.nextInt(this.dame2, this.dame);
			int preHP = mob.hp;
			if (mob.templateId == 0) {
				mob.hp -= mob.maxhp / 5;
			} else {
				mob.hp -= damage;
			}
			if (mob.hp <= 0) {
				if (mob.templateId != 0) {
					int rand = NinjaUtil.nextInt(10);
					if (rand < 2) {
						mob.dropItem(this);
					}
				}
				mob.die();
				this.zone.waitingListRecoverys.add(mob);
			} else if (mob.mobId != 0) {
				mob.characters.put(Integer.valueOf(this.id), this);
			}

			int nextHP = mob.hp;
			this.user.service.attackMonster(damage, false, mob);
			int xp = 0;
			if (mob.level > 20) {
				xp = Math.abs(nextHP - preHP) / 100 * mob.level / 2;
			} else {
				xp = Math.abs(nextHP - preHP) / 50 * mob.level;
			}
			if (mob.templateId != 0 && Math.abs(mob.level - this.level) <= 10) {
				if (mob.levelBoss == 1) {
					xp *= 5;
				} else if (mob.levelBoss == 2) {
					xp *= 10;
				}
				xp *= this.multiExp;
				addExp(xp);
			}
		}
	}

	public void attackCharacter(ArrayList<Character> characters) throws IOException {
		if (isVillage() || isSchool()) {
			return;
		}
		int damage = NinjaUtil.nextInt(this.dame2, this.dame);
		this.user.service.setSkillPaint_2(characters);
		for (Character pl : characters) {
			pl.hp -= damage;
			this.user.service.attackCharacter(damage, pl);
			if (pl.hp <= 0) {
				pl.hp = 0;
				pl.die();
				pl.waitToDie();
			}
		}
	}

	public void attackAllType(Message ms) throws IOException {
		ArrayList<Monster> mobs = new ArrayList<>();
		ArrayList<Character> characters = new ArrayList<>();
		int len = ms.reader().readByte();
		int i;
		for (i = 0; i < len; i++) {
			int id = ms.reader().readUnsignedByte();
			Monster mob = this.zone.getMonsterById(id);
			if (mob != null && mob.hp > 0) {
				mobs.add(mob);
			}
		}
		for (i = 0; i < len; i++) {
			int id = ms.reader().readInt();
			Character _char = this.zone.findCharInMap(id);
			if (_char != null && _char.hp > 0 && !equals(_char)) {
				characters.add(_char);
			}
		}
		if (mobs.size() == 0 && characters.size() == 0) {
			return;
		}
		if (!this.selectedSkill.haveLearned) {
			return;
		}
		byte maxFight = this.selectedSkill.maxFight;
		int manaUse = this.selectedSkill.manaUse;
		int coolDown = this.selectedSkill.coolDown;
		MySkill mySkill = this.selectedSkill.skill;
		long lastTimeUseSkill = mySkill.lastTimeUseSkill;
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis > lastTimeUseSkill) {
			mySkill.lastTimeUseSkill = currentTimeMillis;
		} else {
			return;
		}
		int rangeX = NinjaUtil.getRange(this.x, ((Monster) mobs.get(0)).x);
		int rangeY = NinjaUtil.getRange(this.y, ((Monster) mobs.get(0)).y);
		if (rangeX > this.selectedSkill.dx + 30 || rangeY > this.selectedSkill.dy + 30) {
			return;
		}
		if (this.mp < manaUse) {
			return;
		}
		if (mobs.size() + characters.size() > maxFight) {
			return;
		}
		this.mp -= manaUse;
		attackMonster(mobs);
		attackCharacter(characters);
	}

	public void attackCharacter(Message ms) throws IOException {
		this.logger.log("Attack Character");
		ArrayList<Character> characters = new ArrayList<>();
		while (ms.reader().available() > 0) {
			int id = ms.reader().readInt();
			Character pl = this.zone.findCharInMap(id);
			if (pl != null && !equals(pl) && pl.hp > 0) {
				characters.add(pl);
			}
		}
		if (characters.size() == 0) {
			return;
		}
		if (!this.selectedSkill.haveLearned) {
			return;
		}
		byte maxFight = this.selectedSkill.maxFight;
		int manaUse = this.selectedSkill.manaUse;
		int coolDown = this.selectedSkill.coolDown;
		MySkill mySkill = this.selectedSkill.skill;
		long lastTimeUseSkill = mySkill.lastTimeUseSkill;
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis > lastTimeUseSkill) {
			mySkill.lastTimeUseSkill = currentTimeMillis;
		} else {
			return;
		}
		int rangeX = NinjaUtil.getRange(this.x, ((Character) characters.get(0)).x);
		int rangeY = NinjaUtil.getRange(this.y, ((Character) characters.get(0)).y);
		if (rangeX > this.selectedSkill.dx + 30 || rangeY > this.selectedSkill.dy + 30) {
			return;
		}
		if (this.mp < manaUse) {
			return;
		}
		if (characters.size() > maxFight) {
			return;
		}
		this.mp -= manaUse;
		attackCharacter(characters);
	}

	public void waitToDie() throws IOException {
		Message mss = new Message(0);
		DataOutputStream ds = mss.writer();
		ds.writeInt(this.id);
		ds.writeByte(this.typePk);
		ds.writeShort(this.x);
		ds.writeShort(this.y);
		ds.flush();
		sendToMap(mss);
	}

	public void returnTownFromDead(Message ms) throws IOException {
		if (!this.isDead) {
			return;
		}
		this.user.service.startWaitDlg();
		this.isDead = false;
		this.hp = this.maxHP;
		this.mp = this.maxMP;
		short[] xy = NinjaUtil.getXY(this.saveCoordinate);
		this.x = xy[0];
		this.y = xy[1];
		changeMap(this.saveCoordinate);
		update2();
	}

	public void wakeUpFromDead(Message ms) throws IOException {
		if (!this.isDead) {
			return;
		}
		if (this.user.luong < 1) {
			startOKDlg("Bạn không có đủ 1 lượng!");
			return;
		}
		this.isDead = false;
		updateLuong(this.user.luong - 1);
		this.hp = this.maxHP;
		this.mp = this.maxMP;
		sendMessage(new Message(-10));
		Message mss = new Message(88);
		DataOutputStream ds = mss.writer();
		ds.writeInt(this.id);
		ds.writeShort(this.x);
		ds.writeShort(this.y);
		ds.flush();
		sendToMap(mss);
	}

	public void update2() throws IOException {
		Message ms = new Message(-30);
		DataOutputStream ds = ms.writer();
		ds.writeByte(-123);
		ds.writeInt(this.yen);
		ds.writeInt(this.xu);
		ds.writeInt(this.user.luong);
		ds.writeInt(this.hp);
		ds.writeInt(this.mp);
		ds.writeByte(this.captcha);
		ds.flush();
		sendMessage(ms);
		ms.cleanup();
	}

	public void tayTiemNang(short npcId) throws IOException {
		if ((npcId == 9 && getSys() == 1) || (npcId == 10 && getSys() == 2) || (npcId == 11 && getSys() == 3)) {
			if (this.tayTiemNang > 0) {
				this.point = (short) (this.level * 10);
				if (this.level >= 70) {
					this.point = (short) (this.point + (this.level - 70) * 10);
				}
				if (this.level >= 80) {
					this.point = (short) (this.point + (this.level - 80) * 10);
				}
				if (this.level >= 90) {
					this.point = (short) (this.point + (this.level - 90) * 10);
				}
				if (this.level >= 100) {
					this.point = (short) (this.point + (this.level - 100) * 10);
				}
				this.point = (short) (this.point + 10 * this.limitTiemNangSo);
				this.potential[1] = 5;
				this.potential[2] = 5;
				if (this.classId == 1 || this.classId == 3 || this.classId == 5) {
					this.potential[0] = 10;
					this.potential[3] = 5;
				} else {
					this.potential[0] = 5;
					this.potential[3] = 10;
				}
				this.tayTiemNang = (short) (this.tayTiemNang - 1);
				setAbility();
				this.user.service.sendInfo();
				npcChat(npcId, "Ta đã giúp con tẩy điểm tiềm năng rồi đó.");
			} else {
				npcChat(npcId, "Con đã hết số lần tẩy điểm tiềm năng.");
			}
		} else {
			npcChat(npcId, "Con không học ở trường ta lên ta không thể giúp con!");
		}
	}

	public void tayKyNang(short npcId) throws IOException {
		if ((npcId == 9 && getSys() == 1) || (npcId == 10 && getSys() == 2) || (npcId == 11 && getSys() == 3)) {
			if (this.tayKyNang > 0) {
				this.spoint = (short) (this.level - 9);
				this.spoint = (short) (this.spoint + this.limitKyNangSo);
				for (MySkill my : this.listSkill) {
					my.point = 1;
				}
				this.tayKyNang = (short) (this.tayKyNang - 1);
				setAbility();
				this.user.service.sendInfo();
				npcChat(npcId, "Ta đã giúp con tẩy điểm kĩ năng rồi đó.");
			} else {
				npcChat(npcId, "Con đã hết số lần tẩy điểm kỹ năng.");
			}
		} else {
			npcChat(npcId, "Con không học ở trường ta lên ta không thể giúp con!");
		}
	}

	public void expandBag(Item item) {
		try {
			if (this.numberUseExpanedBag < 3) {
				short[] ids = { 215, 229, 283 };
				byte[] numberCells = { 6, 6, 12 };
				int i = ids[this.numberUseExpanedBag];
				int i2 = numberCells[this.numberUseExpanedBag];
				if (i == item.id) {
					this.numberCellBag = (byte) (this.numberCellBag + i2);
					this.numberUseExpanedBag = (byte) (this.numberUseExpanedBag + 1);
					Item[] bag = new Item[this.numberCellBag];
					for (int num14 = 0; num14 < this.bag.length; num14++) {
						bag[num14] = this.bag[num14];
					}
					this.bag = bag;
					this.user.service.expandBag(item);
					removeItem(item.index, item.quantity, false);
				} else {
					String name = (ItemData.getItemEntryById(i)).name;
					this.user.service.addInfoMe("Vui lòng dùng sử dụng " + name);
				}
			} else {
				this.user.service.addInfoMe("Bạn đã sử dụng tất cả các loại túi vải.");
			}
		} catch (IOException ex) {
			this.logger.debug("bagExpaned", ex.getMessage());
		}
	}

	public void die() throws IOException {
		this.isDead = true;
		this.hp = 0;
		if (this.exp > NinjaUtil.getExp(this.level - 1)) {
			Message m = new Message(-11);
			DataOutputStream ds = m.writer();
			ds.writeByte(this.typePk);
			ds.writeShort(this.x);
			ds.writeShort(this.y);
			ds.writeLong(this.exp);
			ds.flush();
			sendMessage(m);
			m.cleanup();
		} else {
			this.exp = NinjaUtil.getExp(this.level - 1);
			Message m = new Message(72);
			DataOutputStream ds = m.writer();
			ds.writeByte(this.typePk);
			ds.writeShort(this.x);
			ds.writeShort(this.y);
			ds.writeLong(this.expDown);
			ds.flush();
			sendMessage(m);
			m.cleanup();
		}
	}

	public void changeZone(Message ms) throws IOException {
		byte zoneId = ms.reader().readByte();
		byte indexUI = ms.reader().readByte();
		this.user.service.startWaitDlg();
		Map map = MapManager.getMapById(this.mapId);
		Collection<Zone> zones = map.getZones();
		for (Zone zone : zones) {
			if (zone.zoneId == zoneId && zone.numberCharacter >= 20) {
				this.user.service.addInfoMe("Khu vực đã đầy!");
				this.user.service.startWaitDlg();

				return;
			}
		}
		MapManager.outZone(this);
		MapManager.joinZone(this, this.mapId, zoneId);
		this.user.service.sendZone();
	}

	public void splitItem(Message ms) throws IOException {
		int index = ms.reader().readByte();
		if (index < 0 || index >= this.numberCellBag) {
			return;
		}
		Item item = this.bag[index];
		if (item != null && (item.entry.isTypeWeapon() || item.entry.isTypeClothe() || item.entry.isTypeAdorn())
				&& item.upgrade > 0) {
			int num = 0;
			if (item.entry.isTypeWeapon()) {
				byte b;
				for (b = 0; b < item.upgrade; b = (byte) (b + 1)) {
					num += Server.upWeapons[b];
				}
			} else if (item.entry.type % 2 == 0) {
				byte b;
				for (b = 0; b < item.upgrade; b = (byte) (b + 1))
					num += Server.upClothes[b];
			} else {
				byte b;
				for (b = 0; b < item.upgrade; b = (byte) (b + 1)) {
					num += Server.upAdorns[b];
				}
			}
			num /= 2;
			int num2 = 0;
			ArrayList<Item> list = new ArrayList<>();
			for (int n = Server.crystals.length - 1; n >= 0; n--) {
				if (num >= Server.crystals[n]) {
					Item item2 = new Item(n);
					item2.isLock = true;
					item2.expire = -1L;
					item2.quantity = 1;
					list.add(item2);
					num -= Server.crystals[n];
					n++;
					num2++;
				}
			}
			if (num2 > getSlotNull()) {
				startOKDlg("Hành trang không đủ chỗ trống!");
				return;
			}
			int i2 = 0;
			int size = list.size();
			for (int i = 0; i < this.numberCellBag; i++) {
				if (this.bag[i] == null && i2 < size) {
					this.bag[i] = list.get(i2);
					(this.bag[i]).index = i;
					i2++;
				}
			}
			int upgradeOld = item.upgrade;
			item.next(-upgradeOld);
			Message m = new Message(22);
			DataOutputStream ds = m.writer();
			ds.writeByte(list.size());
			for (Item it : list) {
				ds.writeByte(it.index);
				ds.writeShort(it.id);
			}
			ds.flush();
			sendMessage(m);
			m.cleanup();
			list.removeAll(list);
		}
	}

	public void upPearl(Message ms, boolean isCoin) throws IOException {
		ArrayList<Item> crystals = new ArrayList<>();
		while (ms.reader().available() > 0) {
			byte indexItem = ms.reader().readByte();
			if (this.bag[indexItem] != null && (this.bag[indexItem]).id <= 11) {
				if ((this.bag[indexItem]).id == 11) {
					startOKDlg(Language.getString("CRYSTAL_MAX_LEVEL", new Object[0]));
					return;
				}
				crystals.add(this.bag[indexItem]);
			}
		}

		if (crystals.size() > 24) {
			startOKDlg(Language.getString("CRYSTAL_MAX_NUMBER", new Object[] { Integer.valueOf(24) }));
			return;
		}
		int percent = 0;
		int i = 0;
		for (Item item : crystals) {
			percent += Server.crystals[item.id];
		}
		if (percent > 0) {
			for (i = Server.crystals.length - 1; i >= 0 && percent <= Server.crystals[i]; i--)
				;
		}

		if (i >= Server.crystals.length - 1) {
			i = Server.crystals.length - 2;
		}
		percent = percent * 100 / Server.crystals[i + 1];
		if (percent <= 25) {
			startOKDlg("Yêu cầu phần trăm cao hơn 25%!");
			return;
		}
		int id = i + 1;
		int indexNull = getIndexByItem(null);
		if (indexNull == -1) {
			startOKDlg(Language.getString("NOT_ENOUGH_BAG_1", new Object[0]));
			return;
		}
		int coin = Server.coinUpCrystals[i + 1];
		if (isCoin) {
			if (this.xu < coin) {
				startOKDlg(Language.getString("NOT_ENOUGH_XU", new Object[0]));
				return;
			}
			this.xu -= coin;
		} else {
			if (this.xu + this.yen < coin) {
				startOKDlg(Language.getString("NOT_ENOUGH_XU_AND_YEN", new Object[0]));
				return;
			}
			if (this.yen < coin) {
				coin -= this.yen;
				this.yen = 0;
			} else {
				this.yen -= coin;
				coin = 0;
			}
			this.xu -= coin;
		}
		for (Item item : crystals) {
			removeItem(item.index, item.quantity, false);
		}
		byte type = 0;
		if (NinjaUtil.nextInt(100) < percent) {
			type = 1;
			Item item = new Item(id);
			item.index = indexNull;
			item.quantity = 1;
			item.expire = -1L;
			item.isLock = true;
			item.yen = 0;
			this.bag[indexNull] = item;
		} else {
			Item item = new Item(id - 1);
			item.index = indexNull;
			item.quantity = 1;
			item.expire = -1L;
			item.isLock = true;
			item.yen = 0;
			this.bag[indexNull] = item;
		}
		Message mss = new Message(isCoin ? 19 : 20);
		DataOutputStream ds = mss.writer();
		ds.writeByte(type);
		ds.writeByte((this.bag[indexNull]).index);
		ds.writeShort((this.bag[indexNull]).id);
		ds.writeBoolean((this.bag[indexNull]).isLock);
		ds.writeBoolean(((this.bag[indexNull]).expire != -1L));
		if (!isCoin) {
			ds.writeInt(this.yen);
		}
		ds.writeInt(this.xu);
		ds.flush();
		sendMessage(mss);
	}

	public int getIndexByItem(Item item) {
		for (int i = 0; i < this.numberCellBag; i++) {
			if (this.bag[i] == item) {
				return i;
			}
		}
		return -1;
	}

	public void convertUpgrade(Message ms) throws IOException {
		int index1 = ms.reader().readByte();
		int index2 = ms.reader().readByte();
		int index3 = ms.reader().readByte();
		int indexMax = Math.max(Math.max(index1, index2), index3);
		int indexMin = Math.min(Math.min(index1, index2), index3);
		if (indexMax >= this.numberCellBag || indexMin < 0) {
			return;
		}
		Item item1 = this.bag[index1];
		Item item2 = this.bag[index2];
		Item item3 = this.bag[index3];
		if (item1 == null || item2 == null || item3 == null) {
			return;
		}
		if (item1.entry.isTypeWeapon() != item2.entry.isTypeWeapon()
				|| item1.entry.isTypeAdorn() != item2.entry.isTypeAdorn()
				|| item1.entry.isTypeClothe() != item2.entry.isTypeClothe()) {
			startOKDlg("Trang bị không cùng loại!");
			return;
		}
		if (item2.isLock) {
			startOKDlg("Trang bị chuyển hoá sang yêu cầu không khoá!");
			return;
		}
		if (item1.upgrade == 0) {
			startOKDlg("Trang bị chưa nâng câp!");
			return;
		}
		if (item2.upgrade > 0) {
			startOKDlg("Trang bị cần chuyển hoá sang đã nâng cấp!");
			return;
		}
		if (item1.entry.level > item2.entry.level) {
			startOKDlg("Trang bị chuyển hoá sang phải có cấp độ ngang bằng hoặc lớn hơn");
			return;
		}
		if (item3.entry.type == 27) {
			if ((item3.id == 270 && item1.upgrade > 13) || (item3.id == 269 && item1.upgrade > 10)) {
				startOKDlg(item3.entry.name + " không phù hợp để chuyển hoá trang bị này!");
				return;
			}
			byte upgrade = item1.upgrade;
			item2.upgrade = 0;
			item2.next(upgrade);
			item2.isLock = true;
			item1.next(-upgrade);
			item1.isLock = true;
			this.user.service.convertUpgrade(new Item[] { item1, item2 });
			removeItem(item3.index, item3.quantity, true);
		} else {
			startOKDlg(item3.entry.name + " không phải vật phẩm chuyển hoá");
		}
	}

	public void upgradeItem(Message ms) throws IOException {
		boolean isGold = ms.reader().readBoolean();
		byte equipIndex = ms.reader().readByte();
		if (this.bag[equipIndex] == null) {
			return;
		}
		if ((this.bag[equipIndex]).entry.getUpMax() <= (this.bag[equipIndex]).upgrade) {
			startOKDlg(Language.getString("EQUIPMENT_MAX_LEVEL", new Object[0]));
			return;
		}
		int numberBaoHiem = 0;
		int numberCrystal = 0;
		ArrayList<Item> crystals = new ArrayList<>();
		while (ms.reader().available() > 0) {
			byte itemIndex = ms.reader().readByte();
			if (this.bag[itemIndex] != null
					&& ((this.bag[itemIndex]).id <= 11 || (this.bag[itemIndex]).entry.type == 28)
					&& (this.bag[itemIndex]).quantity == 1) {
				if ((this.bag[itemIndex]).entry.isTypeCrystal()) {
					numberCrystal++;
				} else if ((this.bag[itemIndex]).entry.type == 28) {
					numberBaoHiem++;
				}
				crystals.add(this.bag[itemIndex]);
			}
		}
		if (crystals.size() > 18) {
			startOKDlg(Language.getString("CRYSTAL_MAX_NUMBER", new Object[] { Integer.valueOf(18) }));
			return;
		}
		if (numberBaoHiem > 1) {
			startOKDlg("Chỉ sử dụng một bảo hiểm!");
			return;
		}
		if (numberCrystal == 0) {
			startOKDlg("Vui lòng chọn đá nâng cấp!");
			return;
		}
		int temp = 0;
		int percent = 0;
		int coin = 0;
		int gold = 0;
		for (int i = 0; i < crystals.size(); i++) {
			Item item = crystals.get(i);
			if (item != null && item.entry.type == 26) {
				temp += Server.crystals[item.id];
			}
		}
		if ((this.bag[equipIndex]).entry.isTypeClothe()) {
			percent = temp * 100 / Server.upClothes[(this.bag[equipIndex]).upgrade];
			coin = Server.coinUpClothes[(this.bag[equipIndex]).upgrade];
		}
		if ((this.bag[equipIndex]).entry.isTypeAdorn()) {
			percent = temp * 100 / Server.upAdorns[(this.bag[equipIndex]).upgrade];
			coin = Server.coinUpAdorns[(this.bag[equipIndex]).upgrade];
		}
		if ((this.bag[equipIndex]).entry.isTypeWeapon()) {
			percent = temp * 100 / Server.upWeapons[(this.bag[equipIndex]).upgrade];
			coin = Server.coinUpWeapons[(this.bag[equipIndex]).upgrade];
		}
		if (percent > Server.maxPercents[(this.bag[equipIndex]).upgrade]) {
			percent = Server.maxPercents[(this.bag[equipIndex]).upgrade];
		}
		if (isGold) {
			percent = (int) (percent * 1.5D);
			gold = Server.goldUps[(this.bag[equipIndex]).upgrade];
		}
		if (coin > this.xu + this.yen || (isGold && this.user.luong < gold)) {
			startOKDlg("Bạn không đủ tiền!");
			return;
		}
		if (isGold) {
			updateLuong(this.user.luong - gold);
		}
		if (coin > this.yen) {
			updateYen(0);
			updateXu(this.xu - coin - this.yen);
		} else {
			updateYen(this.yen - coin);
		}
		boolean isBaoHiem = false;
		for (int j = 0; j < crystals.size(); j++) {
			Item item = crystals.get(j);
			if (item != null && (item.entry.type == 26 || item.entry.type == 28)) {
				if (!isBaoHiem) {
					if (item.id == 242 && (this.bag[equipIndex]).upgrade < 8) {
						isBaoHiem = true;
					} else if (item.id == 284 && (this.bag[equipIndex]).upgrade < 12) {
						isBaoHiem = true;
					} else if (item.id == 285 && (this.bag[equipIndex]).upgrade < 14) {
						isBaoHiem = true;
					} else if (item.id == 475 && (this.bag[equipIndex]).upgrade < 16) {
						isBaoHiem = true;
					}
				}
				removeItem(item.index, item.quantity, false);
			}
		}
		byte type = 1;
		int rand = NinjaUtil.nextInt(100);
		int up1 = (this.bag[equipIndex]).upgrade;
		int up2 = (this.bag[equipIndex]).upgrade;
		if (rand < percent) {
			type = 1;
			up2++;
		} else {
			type = 0;
			if (!isBaoHiem && (this.bag[equipIndex]).upgrade > 4) {
				if ((this.bag[equipIndex]).upgrade >= 14) {
					up2 = 14;
				} else if ((this.bag[equipIndex]).upgrade >= 12) {
					up2 = 12;
				} else {
					up2 = (byte) ((this.bag[equipIndex]).upgrade / 4 * 4);
				}
			}
		}
		(this.bag[equipIndex]).isLock = true;
		this.bag[equipIndex].next(up2 - up1);
		this.logger.log(
				"upgradeItem percent: " + percent + " isBaoHiem: " + isBaoHiem + " coin: " + coin + " gold: " + gold);
		Message mss = new Message(21);
		DataOutputStream ds = mss.writer();
		ds.writeByte(type);
		ds.writeInt(this.user.luong);
		ds.writeInt(this.xu);
		ds.writeInt(this.yen);
		ds.writeByte((this.bag[equipIndex]).upgrade);
		ds.flush();
		sendMessage(mss);
	}

	public void luckyDrawRefresh(Message ms) throws IOException {
		int size = this.menu.size();
		if (size != 2) {
			return;
		}
		if (((Integer) this.menu.get(0)).intValue() == 2 && ((Integer) this.menu.get(1)).intValue() == 0) {
			this.user.service.showInfoLuckyDraw(Server.luckyDrawVIP);
		}

		if (((Integer) this.menu.get(0)).intValue() == 3 && ((Integer) this.menu.get(1)).intValue() == 0) {
			this.user.service.showInfoLuckyDraw(Server.luckyDrawNormal);
		}
	}

	public void input(Message ms) throws IOException {
		short menuId = ms.reader().readShort();
		String content = ms.reader().readUTF();
		ms.reader().reset();
		switch (menuId) {
		case 100:
			betMessage(ms);
			break;
		case 101:
			luckyDrawRefresh(ms);
			break;
		case 2003:
			bet(content, 0);
			break;
		case 2402:
			bet(content, 1);
			break;
		}
	}

	public void bet(String money, int type) throws IOException {
		if (money == null || money.equals("")) {
			return;
		}
		Pattern p = Pattern.compile("^[0-9]+$");
		Matcher m1 = p.matcher(money);
		if (!m1.find()) {
			this.user.service.addInfoMe("Số xu không hợp lệ!");
			return;
		}
		LuckyDraw lucky = null;
		switch (type) {
		case 0:
			lucky = Server.luckyDrawNormal;
			break;

		case 1:
			lucky = Server.luckyDrawVIP;
			break;
		}
		lucky.join(this, Integer.parseInt(money));
		this.user.service.showInfoLuckyDraw(lucky);
	}

	public void kickOption(Equiped equiped, int maxKick) {
		int num = 0;
		if (equiped != null && equiped.options != null) {
			for (int i = 0; i < equiped.options.size(); i++) {
				ItemOption itemOption = equiped.options.get(i);
				itemOption.active = 0;
				if (itemOption.optionTemplate.type == 2) {
					if (num < maxKick) {
						itemOption.active = 1;
						num++;
					}
				} else if (itemOption.optionTemplate.type == 3 && equiped.upgrade >= 4) {
					itemOption.active = 1;
				} else if (itemOption.optionTemplate.type == 4 && equiped.upgrade >= 8) {
					itemOption.active = 1;
				} else if (itemOption.optionTemplate.type == 5 && equiped.upgrade >= 12) {
					itemOption.active = 1;
				} else if (itemOption.optionTemplate.type == 6 && equiped.upgrade >= 14) {
					itemOption.active = 1;
				} else if (itemOption.optionTemplate.type == 7 && equiped.upgrade >= 16) {
					itemOption.active = 1;
				}
			}
		}
	}

	public void updateKickOption() {
		int num = 2;
		int num2 = 2;
		int num3 = 2;
		if (this.equiped[0] == null) {
			num--;
		}
		if (this.equiped[6] == null) {
			num--;
		}
		if (this.equiped[5] == null) {
			num--;
		}
		kickOption(this.equiped[0], num);
		kickOption(this.equiped[6], num);
		kickOption(this.equiped[5], num);
		if (this.equiped[2] == null) {
			num2--;
		}
		if (this.equiped[8] == null) {
			num2--;
		}
		if (this.equiped[7] == null) {
			num2--;
		}
		kickOption(this.equiped[2], num2);
		kickOption(this.equiped[8], num2);
		kickOption(this.equiped[7], num2);
		if (this.equiped[4] == null) {
			num3--;
		}
		if (this.equiped[3] == null) {
			num3--;
		}
		if (this.equiped[9] == null) {
			num3--;
		}
		if (this.equiped[1] != null) {
			if ((this.equiped[1]).sys == getSys()) {
				if ((this.equiped[1]).options != null) {
					for (int i = 0; i < (this.equiped[1]).options.size(); i++) {
						ItemOption itemOption = (this.equiped[1]).options.get(i);
						if (itemOption.optionTemplate.type == 2) {
							itemOption.active = 1;
						}
					}
				}
			} else if ((this.equiped[1]).options != null) {
				for (int j = 0; j < (this.equiped[1]).options.size(); j++) {
					ItemOption itemOption2 = (this.equiped[1]).options.get(j);
					if (itemOption2.optionTemplate.type == 2) {
						itemOption2.active = 0;
					}
				}
			}
		}
		kickOption(this.equiped[4], num3);
		kickOption(this.equiped[3], num3);
		kickOption(this.equiped[9], num3);
	}

	public void setAbility() {
		this.options = new int[127];
		updateKickOption();
		for (Equiped item : this.equiped) {
			if (item != null) {
				for (ItemOption itemOption2 : item.options) {
					if (itemOption2.optionTemplate.type >= 2 && itemOption2.optionTemplate.type <= 7) {
						if (itemOption2.active == 1)
							this.options[itemOption2.optionTemplate.id] = this.options[itemOption2.optionTemplate.id]
									+ itemOption2.param;
						continue;
					}
					this.options[itemOption2.optionTemplate.id] = this.options[itemOption2.optionTemplate.id]
							+ itemOption2.param;
				}
			}
		}

		this.maxHP = this.potential[2] * 10;
		this.maxMP = this.potential[3] * 10;
		int basicAttack = 0;
		switch (this.classId) {
		case 0:
		case 1:
		case 3:
		case 5:
			basicAttack = this.potential[0] * 3;
			this.miss = this.exactly = this.potential[1] * 2;
			break;

		case 2:
		case 4:
		case 6:
			basicAttack = this.potential[3] * 2;
			this.miss = this.exactly = this.potential[1] * 3;
			break;
		}
		if (this.selectedSkill != null && this.selectedSkill.options != null) {
			basicAttack += basicAttack * this.selectedSkill.options[11] / 100;
			basicAttack += this.options[0] * this.selectedSkill.options[0] / 100;
			basicAttack += this.options[1] * this.selectedSkill.options[1] / 100;
		}
		basicAttack += this.options[38];
		basicAttack += this.options[0] + this.options[1];
		basicAttack += basicAttack * this.options[8] / 100 + basicAttack * this.options[9] / 100;
		this.dame = basicAttack;
		this.dame2 = this.dame - this.dame / 10;
		this.maxHP += this.options[6] + this.options[32] + this.options[77] + this.options[82] + this.options[125];
		this.maxHP += this.maxHP * this.options[31] / 100;
		this.maxHP += this.maxHP * this.options[61] / 100;
		this.maxMP += this.options[7] + this.options[19] + this.options[29] + this.options[83] + this.options[117];
		this.maxMP += this.maxMP * this.options[28] / 100;
		this.maxMP += this.maxMP * this.options[60] / 100;
		if (this.maxHP == 0) {
			this.maxHP = 50;
		}
		if (this.maxMP == 0) {
			this.maxMP = 50;
		}
		this.dameDown = this.options[47] + this.options[80] + this.options[124];
		this.miss = this.options[5] + this.options[17] + this.options[62] + this.options[68] + this.options[78]
				+ this.options[84] + this.options[115];
	}

	public void inputNumberSplit(Message ms) throws IOException {
		byte indexItem = ms.reader().readByte();
		int numSplit = ms.reader().readInt();
		if (this.bag[indexItem] != null && (this.bag[indexItem]).entry.isUpToUp) {
			int quantity = (this.bag[indexItem]).quantity;
			if (numSplit >= quantity) {
				return;
			}
			Item item2 = this.bag[indexItem].clone();
			for (int i = 0; i < this.numberCellBag; i++) {
				if (this.bag[i] == null) {
					this.bag[i] = item2;
					(this.bag[i]).index = i;
					(this.bag[i]).quantity = numSplit;
					this.user.service.addItem(this.bag[i]);
					(this.bag[indexItem]).quantity -= numSplit;
					this.user.service.removeItem(indexItem, numSplit);
					return;
				}
			}
		}
	}

	public void betMessage(Message ms) throws IOException {
		short type = ms.reader().readShort();
		String money = ms.reader().readUTF();
		byte typeLuck = ms.reader().readByte();
		if (money == null || money.equals("")) {
			return;
		}
		Pattern p = Pattern.compile("^[0-9]+$");
		Matcher m1 = p.matcher(money);
		if (!m1.find()) {
			startOKDlg("Số xu không hợp lệ!");
			return;
		}
		LuckyDraw lucky = null;
		switch (typeLuck) {
		case 0:
			lucky = Server.luckyDrawNormal;
			break;

		case 1:
			lucky = Server.luckyDrawVIP;
			break;
		}
		lucky.join(this, Integer.parseInt(money));
		this.user.service.showInfoLuckyDraw(lucky);
	}

	public void selectSkill(Message ms) throws IOException {
		short skillTemplateId = ms.reader().readShort();
		selectSkill(skillTemplateId);
	}

	public void selectSkill(short skillTemplateId) {
		this.selectedSkill.skillTemplateId = skillTemplateId;
		this.selectedSkill.options = new int[72];
		this.selectedSkill.haveLearned = false;
		int point = 0;
		for (MySkill my : this.listSkill) {
			if (my.id == skillTemplateId) {
				this.selectedSkill.skill = my;
				point = my.point;
				this.selectedSkill.haveLearned = true;
				break;
			}
		}
		if (!this.selectedSkill.haveLearned) {
			return;
		}
		SkillTemplate tem = SkillData.getTemplate(this.classId, skillTemplateId);
		for (Skill skill : tem.skills) {
			if (skill.point == point) {
				this.selectedSkill.manaUse = skill.manaUse;
				for (SkillOption op : skill.options) {
					this.selectedSkill.options[op.optionTemplate.id] = this.selectedSkill.options[op.optionTemplate.id]
							+ op.param;
				}
				this.selectedSkill.dx = skill.dx;
				this.selectedSkill.dy = skill.dy;
				this.selectedSkill.maxFight = skill.maxFight;
				this.selectedSkill.coolDown = skill.coolDown;
				break;
			}
		}
		setAbility();
	}

	public void menuId(Message ms) throws IOException {
		short npcId = ms.reader().readShort();
		this.logger.log("npcId: " + npcId);
		NpcTemplate npc = Server.npcs.get(npcId);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(npcId);
		ds.writeUTF("");
		ds.writeByte(npc.menu.length);
		for (String[] c : npc.menu) {
			ds.writeUTF(c[0]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
		this.menu.clear();
	}

	public void menu(Message ms) throws IOException {
		byte npcId = ms.reader().readByte();
		byte menuId = ms.reader().readByte();
		this.logger.log("npcId: " + npcId + " menuId: " + menuId);
		if (ms.reader().available() > 0) {
			byte optionId = ms.reader().readByte();
			this.logger.log("optionId: " + optionId);
		}
		this.menu.add(Integer.valueOf(menuId));
		switch (npcId) {

		case 1:
			npcFuroya(menuId);
			break;
		case 2:
			npcAmeji(menuId);
			break;
		case 3:
			npcKirito(menuId);
			break;
		case 4:
			npcTabemono(menuId);
			break;
		case 5:
			npcKamakara(menuId);
			break;
		case 7:
			npcUmayaki_1(menuId);
			break;
		case 8:
			npcUmayaki_2(menuId);
			break;
		case 6:
			npcKenshinto(menuId);
			break;
		case 9:
			npcToyotomi(menuId);
			break;
		case 10:
			npcOokamesama(menuId);
			break;
		case 11:
			npcKazeto(menuId);
			break;
		case 12:
			npcTajima(menuId);
			break;
		case 0:
			npcKanata(menuId);
			break;
		case 30:
			npcRakkii(menuId);
			break;
		case 26:
			npcGoosho(menuId);
			break;
		case 33:
			npcTiennu(menuId);
			break;
		}
	}

	public void npcTiennu(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 1) {
			if (((Integer) this.menu.get(0)).intValue() == 0) {
				if (getNumberItem(434) <= 0) {
					startOKDlg("Bạn không có diều giấy!");
					return;
				}
				removeItem(getIndexItemByIdInBag(434), 1, true);
				addExp(150000000L);
				if (NinjaUtil.nextInt(7) == 0) {
					addExp(300000000L);

				}

			} else if (((Integer) this.menu.get(0)).intValue() == 1) {
				if (getNumberItem(435) <= 0) {
					startOKDlg("Bạn không có diều vải!");
					return;
				}
				removeItem(getIndexItemByIdInBag(435), 1, true);
				addExp(250000000L);
				if (NinjaUtil.nextInt(7) == 0) {
					addExp(500000000L);
				}
			}
		}

		NpcTemplate npc = Server.npcs.get(12);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(12);
		ds.writeUTF("Cho kiếm này.");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcKamakara(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 1)
			if (((Integer) this.menu.get(0)).intValue() == 0) {
				this.user.service.openUI((byte) 4);
			} else {
				if (((Integer) this.menu.get(0)).intValue() == 1) {
					this.saveCoordinate = this.mapId;
					npcChat((short) 5, "Lưu toạ độ thành công! Mày chết sẽ quay về nơi đây.");
					return;
				}
				if (((Integer) this.menu.get(0)).intValue() == 3) {
					switch (NinjaUtil.nextInt(3)) {
					case 0:
						npcChat((short) 5, "Hãy yên tâm giao đồ cho ta.");
						break;

					case 1:
						npcChat((short) 5, "Ta giữ đồ chưa hề để thất lạc bao giờ.");
						break;

					case 2:
						npcChat((short) 5, "Trên người ngươi toàn đồ có giá trị, sao không cất bớt ở đây?");
						break;
					}
					return;
				}
			}

		NpcTemplate npc = Server.npcs.get(5);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(5);
		ds.writeUTF("");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void admission(byte sys, short npc) throws IOException {
		if (this.classId == 0) {
			if (this.level >= 10) {
				if (this.equiped[1] != null) {
					startOKDlg("Vui lòng tháo vũ khí trước khi nhập học tránh đồ sát thầy!");
					return;
				}
				this.classId = sys;
				this.spoint = (short) (this.level - 9);
				this.point = (short) (this.level * 10);
				if (this.level >= 70) {
					this.point = (short) (this.point + (this.level - 70) * 10);
				}
				if (this.level >= 80) {
					this.point = (short) (this.point + (this.level - 80) * 10);
				}
				if (this.level >= 90) {
					this.point = (short) (this.point + (this.level - 90) * 10);
				}
				if (this.level >= 100) {
					this.point = (short) (this.point + (this.level - 100) * 10);
				}
				this.potential[1] = 5;
				this.potential[2] = 5;
				if (this.classId == 1 || this.classId == 3 || this.classId == 5) {
					this.potential[0] = 10;
					this.potential[3] = 5;
				} else {
					this.potential[0] = 5;
					this.potential[3] = 10;
				}
				this.listSkill.clear();
				npcChat(npc, "Mày chọn ta là đúng rồi đấy!");
				Item item = null;
				Item item2 = null;
				switch (sys) {
				case 1:
					item = new Item(40);
					item.quantity = 1;
					item.isLock = true;
					item.expire = -1L;
					item2 = new Item(94);
					item2.quantity = 1;
					item2.isLock = true;
					item2.expire = -1L;
					item2.upgrade = 0;
					item2.sys = 1;
					item2.options = new ArrayList<>();
					item2.options.add(new ItemOption(0, 100));
					item2.options.add(new ItemOption(1, 100));
					item2.options.add(new ItemOption(8, 10));
					item2.options.add(new ItemOption(10, 5));
					item2.options.add(new ItemOption(21, 100));
					item2.options.add(new ItemOption(19, 10));
					item2.options.add(new ItemOption(30, 5));
					item2.yen = 300;
					break;

				case 3:
					item = new Item(58);
					item.quantity = 1;
					item.isLock = true;
					item.expire = -1L;
					item2 = new Item(99);
					item2.quantity = 1;
					item2.isLock = true;
					item2.expire = -1L;
					item2.upgrade = 0;
					item2.sys = 1;
					item2.options = new ArrayList<>();
					item2.options.add(new ItemOption(0, 100));
					item2.options.add(new ItemOption(1, 100));
					item2.options.add(new ItemOption(8, 10));
					item2.options.add(new ItemOption(10, 5));
					item2.options.add(new ItemOption(21, 100));
					item2.options.add(new ItemOption(19, 10));
					item2.options.add(new ItemOption(30, 5));
					item2.yen = 300;
					break;

				case 5:
					item = new Item(76);
					item.quantity = 1;
					item.isLock = true;
					item.expire = -1L;
					item2 = new Item(104);
					item2.quantity = 1;
					item2.isLock = true;
					item2.expire = -1L;
					item2.upgrade = 0;
					item2.sys = 1;
					item2.options = new ArrayList<>();
					item2.options.add(new ItemOption(0, 100));
					item2.options.add(new ItemOption(1, 100));
					item2.options.add(new ItemOption(8, 10));
					item2.options.add(new ItemOption(10, 5));
					item2.options.add(new ItemOption(21, 100));
					item2.options.add(new ItemOption(19, 10));
					item2.options.add(new ItemOption(30, 5));
					item2.yen = 300;
					break;

				case 2:
					item = new Item(49);
					item.quantity = 1;
					item.isLock = true;
					item.expire = -1L;
					item2 = new Item(114);
					item2.quantity = 1;
					item2.isLock = true;
					item2.expire = -1L;
					item2.upgrade = 0;
					item2.sys = 1;
					item2.options = new ArrayList<>();
					item2.options.add(new ItemOption(0, 100));
					item2.options.add(new ItemOption(1, 100));
					item2.options.add(new ItemOption(9, 10));
					item2.options.add(new ItemOption(10, 5));
					item2.options.add(new ItemOption(22, 100));
					item2.options.add(new ItemOption(19, 10));
					item2.options.add(new ItemOption(30, 5));
					item2.yen = 300;
					break;

				case 4:
					item = new Item(67);
					item.quantity = 1;
					item.isLock = true;
					item.expire = -1L;
					item2 = new Item(109);
					item2.quantity = 1;
					item2.isLock = true;
					item2.expire = -1L;
					item2.upgrade = 0;
					item2.sys = 1;
					item2.options = new ArrayList<>();
					item2.options.add(new ItemOption(0, 100));
					item2.options.add(new ItemOption(1, 100));
					item2.options.add(new ItemOption(9, 10));
					item2.options.add(new ItemOption(10, 5));
					item2.options.add(new ItemOption(22, 100));
					item2.options.add(new ItemOption(19, 10));
					item2.options.add(new ItemOption(30, 5));
					item2.yen = 300;
					break;

				case 6:
					item = new Item(85);
					item.quantity = 1;
					item.isLock = true;
					item.expire = -1L;
					item2 = new Item(119);
					item2.quantity = 1;
					item2.isLock = true;
					item2.expire = -1L;
					item2.upgrade = 0;
					item2.sys = 1;
					item2.options = new ArrayList<>();
					item2.options.add(new ItemOption(0, 100));
					item2.options.add(new ItemOption(1, 100));
					item2.options.add(new ItemOption(9, 10));
					item2.options.add(new ItemOption(10, 5));
					item2.options.add(new ItemOption(22, 100));
					item2.options.add(new ItemOption(19, 10));
					item2.options.add(new ItemOption(30, 5));
					item2.yen = 300;
					break;
				}
				this.user.service.sendInfo();
				addItemToBag(item);
				addItemToBag(item2);
			} else {
				npcChat(npc, "Mày còn kém lắm chém nhau lên cấp 10 thì ta cho học!");
			}
		} else {
			npcChat(npc, "Mày đòi học lắm nhiều phái thế!");
		}
	}

	public void npcKazeto(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 2) {
			if (((Integer) this.menu.get(0)).intValue() == 1) {
				if (((Integer) this.menu.get(1)).intValue() == 0) {
					admission((byte) 5, (short) 11);
				} else {
					admission((byte) 6, (short) 11);
				}
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 2) {
				if (((Integer) this.menu.get(1)).intValue() == 0) {
					tayTiemNang((short) 11);
				} else {
					tayKyNang((short) 11);
				}
				return;
			}
		}
		NpcTemplate npc = Server.npcs.get(11);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(11);
		ds.writeUTF("Ta là bố của thần gió, hay theo ta.");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcOokamesama(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 2) {
			if (((Integer) this.menu.get(0)).intValue() == 1) {
				if (((Integer) this.menu.get(1)).intValue() == 0) {
					admission((byte) 3, (short) 10);
				} else {
					admission((byte) 4, (short) 10);
				}
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 2) {
				if (((Integer) this.menu.get(1)).intValue() == 0) {
					tayTiemNang((short) 10);
				} else {
					tayKyNang((short) 10);
				}
				return;
			}
		}
		NpcTemplate npc = Server.npcs.get(10);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(10);
		ds.writeUTF("Ta ngươi chơi hệ thủy, hay theo ta.");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcToyotomi(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 2) {
			if (((Integer) this.menu.get(0)).intValue() == 1) {
				if (((Integer) this.menu.get(1)).intValue() == 0) {
					admission((byte) 1, (short) 9);
				} else {
					admission((byte) 2, (short) 9);
				}
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 2) {
				if (((Integer) this.menu.get(1)).intValue() == 0) {
					tayTiemNang((short) 9);
				} else {
					tayKyNang((short) 9);
				}
				return;
			}
		}
		NpcTemplate npc = Server.npcs.get(9);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(9);
		ds.writeUTF("Theo ta không ta đốt nhà mi.");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcKenshinto(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 2) {
			if (((Integer) this.menu.get(0)).intValue() == 0) {
				if (((Integer) this.menu.get(1)).intValue() == 0) {
					this.user.service.openUI((byte) 10);
					return;
				}
				if (((Integer) this.menu.get(1)).intValue() == 1) {
					this.user.service.openUI((byte) 31);
					return;
				}
				if (((Integer) this.menu.get(1)).intValue() == 2) {
					npcChat((short) 6,
							"Bỏ Trang bị và Đá vào trong khung để nâng cấp, Khi nâng cấp cẩn thận thì phải có lượng.");
					return;
				}
			} else if (((Integer) this.menu.get(0)).intValue() == 1) {
				if (((Integer) this.menu.get(1)).intValue() == 0) {
					this.user.service.openUI((byte) 12);
					return;
				}
				if (((Integer) this.menu.get(1)).intValue() == 1) {
					this.user.service.openUI((byte) 11);
					return;
				}
			}
		} else if (size == 1) {
			if (((Integer) this.menu.get(0)).intValue() == 2) {
				this.user.service.openUI((byte) 13);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 3) {
				this.user.service.openUI((byte) 33);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 4) {
				this.user.service.openUI((byte) 46);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 5) {
				this.user.service.openUI((byte) 47);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 6) {
				this.user.service.openUI((byte) 49);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 7) {
				this.user.service.openUI((byte) 50);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 8) {
				npcChat((short) 6, "Chỉ có đứa ngu mới tin tao.");
				return;
			}
		}
		NpcTemplate npc = Server.npcs.get(6);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(6);
		ds.writeUTF("Đập đồ đi mấy cưng!!!");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcFuroya(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 2 && ((Integer) this.menu.get(0)).intValue() == 0) {
			if (((Integer) this.menu.get(1)).intValue() == 0) {
				if (this.gender == 1) {
					this.user.service.openUI((byte) 20);
				} else {
					this.user.service.openUI((byte) 21);
				}
				return;
			}
			if (((Integer) this.menu.get(1)).intValue() == 1) {
				if (this.gender == 1) {
					this.user.service.openUI((byte) 22);
				} else {
					this.user.service.openUI((byte) 23);
				}
				return;
			}
			if (((Integer) this.menu.get(1)).intValue() == 2) {
				if (this.gender == 1) {
					this.user.service.openUI((byte) 24);
				} else {
					this.user.service.openUI((byte) 25);
				}
				return;
			}
			if (((Integer) this.menu.get(1)).intValue() == 3) {
				if (this.gender == 1) {
					this.user.service.openUI((byte) 26);
				} else {
					this.user.service.openUI((byte) 27);
				}
				return;
			}
			if (((Integer) this.menu.get(1)).intValue() == 4) {
				if (this.gender == 1) {
					this.user.service.openUI((byte) 28);
				} else {
					this.user.service.openUI((byte) 29);
				}

				return;
			}
		}
		NpcTemplate npc = Server.npcs.get(1);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(1);
		ds.writeUTF("Tao bán trang bị đây!");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcAmeji(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 2 && ((Integer) this.menu.get(0)).intValue() == 0) {
			if (((Integer) this.menu.get(1)).intValue() == 0) {
				this.user.service.openUI((byte) 16);
				return;
			}
			if (((Integer) this.menu.get(1)).intValue() == 1) {
				this.user.service.openUI((byte) 17);
				return;
			}
			if (((Integer) this.menu.get(1)).intValue() == 2) {
				this.user.service.openUI((byte) 18);

				return;
			}
			if (((Integer) this.menu.get(1)).intValue() == 3) {
				this.user.service.openUI((byte) 19);

				return;
			}
		}
		NpcTemplate npc = Server.npcs.get(2);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(2);
		ds.writeUTF("Tao bán trang bị đây!");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcTabemono(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 1) {
			if (((Integer) this.menu.get(0)).intValue() == 0) {
				this.user.service.openUI((byte) 9);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 1) {
				this.user.service.openUI((byte) 8);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 2) {
				npcChat((short) 4, "Ta ở đây cung cấp lương thực, nhưng không phải miễn phí.");
				return;
			}
			npcChat((short) 4, "Thiên địa bảng còn lâu mới có. Mua đồ ăn thì mua, không mua thì lượn đi.");

			return;
		}

		NpcTemplate npc = Server.npcs.get(4);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(4);
		ds.writeUTF("Đồ ngon đêy mua đê.");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcKirito(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 1) {
			if (((Integer) this.menu.get(0)).intValue() == 0) {
				this.user.service.openUI((byte) 7);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 1) {
				this.user.service.openUI((byte) 6);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 2) {
				npcChat((short) 3, "Ta ở đây cung cấp vật phẩm y tê giá rẻ chất lượng thấp.");

				return;
			}
		}
		NpcTemplate npc = Server.npcs.get(3);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(3);
		ds.writeUTF("Đồ ngon đêy mua đê.");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcOkanechan(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 1 && ((Integer) this.menu.get(0)).intValue() == 0) {
			switch (NinjaUtil.nextInt(3)) {
			case 0:
				npcChat((short) 5, "Hãy yên tâm giao đồ cho ta.");
				break;

			case 1:
				npcChat((short) 5, "Ta giữ đồ chưa hề để thất lạc bao giờ.");
				break;

			case 2:
				npcChat((short) 5, "Trên người ngươi toàn đồ có giá trị, sao không cất bớt ở đây?");
				break;
			}

			return;
		}
		NpcTemplate npc = Server.npcs.get(24);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(24);
		ds.writeUTF("Tao giữ đồ!");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcUmayaki_1(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 1) {
			int index = ((Integer) this.menu.get(0)).intValue();
			if (index >= 1 && index <= 7) {
				(new short[8])[0] = -1;
				(new short[8])[1] = 10;
				(new short[8])[2] = 17;
				(new short[8])[3] = 22;
				(new short[8])[4] = 32;
				(new short[8])[5] = 38;
				(new short[8])[6] = 43;
				(new short[8])[7] = 48;
				short map = (new short[8])[index];
				short[] xy = NinjaUtil.getXY(map);
				this.x = xy[0];
				this.y = xy[1];
				changeMap(map);
				return;
			}
			if (index == 0) {
				npcChat((short) 7, "Tao kéo xe qua các làng!");
				return;
			}
		}
		NpcTemplate npc = Server.npcs.get(7);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(7);
		ds.writeUTF("Tao buôn hàng cấm đây!");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcTajima(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 1) {
			int index = ((Integer) this.menu.get(0)).intValue();
			if (index == 0) {
				if (this.level == 3) {
					Item item = new Item(194);
					item.sys = 0;
					item.isLock = true;
					item.expire = -1L;
					item.quantity = 1;
					item.yen = 5;
					item.options = new ArrayList<>();
					item.options.add(new ItemOption(0, 10));
					item.options.add(new ItemOption(8, 1));
					addItemToBag(item);
					this.potential[0] = 15;
					this.potential[1] = 10;
					this.potential[2] = 10;
					this.potential[3] = 10;
					MySkill my = new MySkill();
					my.id = 0;
					my.point = 0;
					this.listSkill.add(my);
					addExp(100000L);
					this.user.service.sendInfo();
				}
				return;
			}
		}
		NpcTemplate npc = Server.npcs.get(12);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(12);
		ds.writeUTF("Cho kiếm này.");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcUmayaki_2(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 1) {
			int index = ((Integer) this.menu.get(0)).intValue();
			if (index >= 0 && index <= 2) {
				(new short[3])[0] = 1;
				(new short[3])[1] = 27;
				(new short[3])[2] = 72;
				short map = (new short[3])[index];
				short[] xy = NinjaUtil.getXY(map);
				this.x = xy[0];
				this.y = xy[1];
				changeMap(map);
				return;
			}
			if (index == 3) {
				npcChat((short) 8, "Tao kéo xe qua các trường!");
				return;
			}
		}
		NpcTemplate npc = Server.npcs.get(8);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(8);
		ds.writeUTF("Tao buôn hàng cấm đây!");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcKanata(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 1 && ((Integer) this.menu.get(0)).intValue() == 0) {
			this.user.service.openUI((byte) 2);

			return;
		}
		NpcTemplate npc = Server.npcs.get(0);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(0);
		ds.writeUTF("Tao buôn hàng cấm đây!");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcGoosho(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 1) {
			if (((Integer) this.menu.get(0)).intValue() == 0) {
				this.user.service.openUI((byte) 14);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 1) {
				this.user.service.openUI((byte) 15);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 2) {
				this.user.service.openUI((byte) 32);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 3) {
				this.user.service.openUI((byte) 34);
				return;
			}
		}
		NpcTemplate npc = Server.npcs.get(26);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(26);
		ds.writeUTF("Hết hàng rồi nha bro!");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void npcRakkii(byte menuId) throws IOException {
		int size = this.menu.size();
		if (size == 1) {
			if (((Integer) this.menu.get(0)).intValue() == 0) {
				this.user.service.openUI((byte) 38);
				return;
			}
			if (((Integer) this.menu.get(0)).intValue() == 1) {
				this.user.service.inputDlg("Mã quà tặng", 1234567);
				return;
			}
		}
		if (size == 2) {
			String law = "- Giá trị nhập xu thấp nhất của mỗi người là %s\n- Giá trị nhập xu cao nhất của mỗi người là %s\n- Mỗi 2 phút bắt đầu vòng quay một lần.\n- Khi có người bắt đầu nhập xu thì mới bắt đầu đêm ngược thời gian.\n- Còn 10 giây cuối sẽ bắt đầu khoá không cho gửi xu.\n- Người chiến thắng sẽ nhận tổng tất cả số tiền tất cả người chơi khác đặt cược sau khi trừ thuế.\n- Người chơi nhiều hơn 10 người thuế sẽ là 10.";

			if (((Integer) this.menu.get(0)).intValue() == 2) {
				if (((Integer) this.menu.get(1)).intValue() == 0) {
					this.user.service.showInfoLuckyDraw(Server.luckyDrawVIP);
					return;
				}
				if (((Integer) this.menu.get(1)).intValue() == 1) {
					this.user.service.inputDlg(Server.luckyDrawVIP.name, 2402);
					return;
				}
				if (((Integer) this.menu.get(1)).intValue() == 2) {
					this.user.service.showAlert(Server.luckyDrawVIP.name,
							String.format(law, new Object[] { NinjaUtil.getCurrency(Server.luckyDrawVIP.xuMin),
									NinjaUtil.getCurrency(Server.luckyDrawVIP.xuMax) }));
					return;
				}
			}
			if (((Integer) this.menu.get(0)).intValue() == 3) {
				if (((Integer) this.menu.get(1)).intValue() == 0) {
					this.user.service.showInfoLuckyDraw(Server.luckyDrawNormal);
					return;
				}
				if (((Integer) this.menu.get(1)).intValue() == 1) {
					this.user.service.inputDlg(Server.luckyDrawNormal.name, 2003);
					return;
				}
				if (((Integer) this.menu.get(1)).intValue() == 2) {
					this.user.service.showAlert(Server.luckyDrawNormal.name,
							String.format(law, new Object[] { NinjaUtil.getCurrency(Server.luckyDrawNormal.xuMin),
									NinjaUtil.getCurrency(Server.luckyDrawNormal.xuMax) }));
					return;
				}
			}
		}
		NpcTemplate npc = Server.npcs.get(30);
		Message mss = new Message(39);
		DataOutputStream ds = mss.writer();
		ds.writeShort(30);
		ds.writeUTF("Muốn giải trí không bro!");
		ds.writeByte((npc.menu[menuId]).length - 1);
		for (int i = 1; i < (npc.menu[menuId]).length; i++) {
			ds.writeUTF(npc.menu[menuId][i]);
		}
		ds.flush();
		sendMessage(mss);
		mss.cleanup();
	}

	public void pickItem(Message ms) throws IOException {
		short itemMapId = ms.reader().readShort();
		ItemMap item = this.zone.getItemMapById(itemMapId);
		if (item != null) {
			int rangeX = NinjaUtil.getRange(this.x, item.x);
			int rangeY = NinjaUtil.getRange(this.y, item.y);
			if (rangeX < 30 && rangeY < 30) {
				if (item.owner == null || equals(item.owner) || item.timeCount < 30) {
					if (item.item.entry.type != 19) {
						int num = getSlotNull();
						if (item.item.entry.isUpToUp) {
							int index = getIndexItemByIdInBag(item.item.id);
							if (index == -1 && num == 0) {
								startOKDlg("Hành trang không dủ chỗ trống!");

								return;
							}
						} else if (num == 0) {
							startOKDlg("Hành trang không dủ chỗ trống!");

							return;
						}
						addItemToBag(item.item);
					} else {
						addYen(item.item.quantity);
					}
					ms = new Message(-14);
					DataOutputStream ds = ms.writer();
					ds.writeShort(item.id);
					ds.writeShort(item.item.quantity);
					ds.flush();
					sendMessage(ms);

					ms = new Message(-13);
					ds = ms.writer();
					ds.writeShort(item.id);
					ds.writeInt(this.id);
					ds.flush();
					for (Character pl : characters_name.values()) {
						if (pl != null && !equals(pl)) {
							pl.sendMessage(ms);
						}
					}
					this.zone.removeItem(item.id);
				} else {
					this.user.service.addInfoMe("Vật phẩm của người khác");
				}
			}
		}
	}

	public void throwItem(Message ms) throws IOException {
		byte indexUI = ms.reader().readByte();
		if (indexUI < 0 || indexUI >= this.numberCellBag || this.bag[indexUI] == null || (this.bag[indexUI]).isLock) {
			return;
		}
		Item item = this.bag[indexUI].clone();
		this.zone.numberDropItem = (short) (this.zone.numberDropItem + 1);
		ItemMap itemMap = new ItemMap(this.zone.numberDropItem);
		itemMap.x = (short) NinjaUtil.nextInt(this.x - 30, this.x + 30);
		itemMap.y = this.y;
		itemMap.item = item;
		itemMap.owner = null;
		this.zone.put(itemMap.id, itemMap);
		this.bag[indexUI] = null;
		this.user.service.throwItem(indexUI, itemMap.id, (short) itemMap.item.id, itemMap.x, itemMap.y);
	}

	public void changeMap() throws IOException {
		this.user.service.startWaitDlg();
		int mapId = this.mapId;
		Map map = MapManager.getMapById(mapId);
		for (Waypoint way : map.tilemap.waypoints) {
			if (this.x >= way.minX - 40 && this.x <= way.maxX + 40 && this.y >= way.minY - 40
					&& this.y <= way.maxY + 40) {
				switch (way.mapId) {
				case 1:
					if (mapId == 2) {
						this.x = 35;
						this.y = 384;
						break;
					}
					if (mapId == 3) {
						this.x = 1885;
						this.y = 360;
					}
					break;
				case 2:
					if (mapId == 6) {
						this.x = 35;
						this.y = 216;
						break;
					}
					if (mapId == 1) {
						this.x = 1405;
						this.y = 216;
					}
					break;
				case 3:
					if (mapId == 1) {
						this.x = 35;
						this.y = 288;
						break;
					}
					if (mapId == 4) {
						this.x = 1405;
						this.y = 264;
					}
					break;
				case 4:
					if (mapId == 3) {
						this.x = 35;
						this.y = 216;
						break;
					}
					if (mapId == 5) {
						this.x = 2845;
						this.y = 216;
					}
					break;
				case 5:
					if (mapId == 4) {
						this.x = 35;
						this.y = 144;
						break;
					}
					if (mapId == 7) {
						this.x = 1741;
						this.y = 288;
					}
					break;
				case 6:
					if (mapId == 7) {
						this.x = 445;
						this.y = 1704;
						break;
					}
					if (mapId == 21) {
						this.x = 37;
						this.y = 120;
						break;
					}
					if (mapId == 2) {
						this.x = 445;
						this.y = 120;
						break;
					}
					if (mapId == 20) {
						this.x = 263;
						this.y = 1872;
					}
					break;
				case 7:
					if (mapId == 5) {
						this.x = 659;
						this.y = 72;
						break;
					}
					if (mapId == 6) {
						this.x = 35;
						this.y = 192;
						break;
					}
					if (mapId == 8) {
						this.x = 5965;
						this.y = 288;
					}
					break;
				case 8:
					if (mapId == 9) {
						this.x = 1885;
						this.y = 264;
						break;
					}
					if (mapId == 7) {
						this.x = 35;
						this.y = 168;
					}
					break;
				case 9:
					if (mapId == 8) {
						this.x = 35;
						this.y = 288;
						break;
					}
					if (mapId == 10) {
						this.x = 1885;
						this.y = 264;
					}
					break;
				case 10:
					if (mapId == 9) {
						this.x = 35;
						this.y = 264;
						break;
					}
					if (mapId == 11) {
						this.x = 1612;
						this.y = 264;
					}
					break;
				case 11:
					if (mapId == 10) {
						this.x = 892;
						this.y = 72;
						break;
					}
					if (mapId == 12) {
						this.x = 925;
						this.y = 720;
					}
					break;
				case 12:
					if (mapId == 11) {
						this.x = 35;
						this.y = 264;
						break;
					}
					if (mapId == 57) {
						this.x = 2125;
						this.y = 288;
					}
					break;
				case 13:
					if (mapId == 57) {
						this.x = 35;
						this.y = 192;
						break;
					}
					if (mapId == 14) {
						this.x = 925;
						this.y = 456;
					}
					break;
				case 14:
					if (mapId == 13) {
						this.x = 35;
						this.y = 216;
						break;
					}
					if (mapId == 15) {
						this.x = 1885;
						this.y = 264;
					}
					break;
				case 15:
					if (mapId == 14) {
						this.x = 35;
						this.y = 168;
						break;
					}
					if (mapId == 16) {
						this.x = 1405;
						this.y = 144;
					}
					break;
				case 16:
					if (mapId == 15) {
						this.x = 35;
						this.y = 288;
						break;
					}
					if (mapId == 17) {
						this.x = 925;
						this.y = 168;
					}
					break;
				case 17:
					if (mapId == 16) {
						this.x = 35;
						this.y = 264;
						break;
					}
					if (mapId == 18) {
						this.x = 1645;
						this.y = 144;
					}
					break;
				case 18:
					if (mapId == 17) {
						this.x = 35;
						this.y = 432;
						break;
					}
					if (mapId == 19) {
						this.x = 1765;
						this.y = 360;
					}
					break;
				case 19:
					if (mapId == 18) {
						this.x = 35;
						this.y = 360;
						break;
					}
					if (mapId == 58) {
						this.x = 1645;
						this.y = 360;
					}
					break;
				case 20:
					if (mapId == 6) {
						this.x = 194;
						this.y = 48;
					}
					break;
				case 21:
					if (mapId == 22) {
						this.x = 422;
						this.y = 480;
						break;
					}
					if (mapId == 6) {
						this.x = 1645;
						this.y = 360;
					}
					break;
				case 22:
					if (mapId == 23) {
						this.x = 50;
						this.y = 168;
						break;
					}
					if (mapId == 21) {
						this.x = 2805;
						this.y = 72;
					}
					break;
				case 23:
					if (mapId == 22) {
						this.x = 685;
						this.y = 1848;
						break;
					}
					if (mapId == 25) {
						this.x = 685;
						this.y = 120;
						break;
					}
					if (mapId == 69) {
						this.x = 88;
						this.y = 1848;
					}
					break;
				case 24:
					if (mapId == 59) {
						this.x = 35;
						this.y = 432;
						break;
					}
					if (mapId == 36) {
						this.x = 1885;
						this.y = 312;
					}
					break;
				case 25:
					if (mapId == 23) {
						this.x = 35;
						this.y = 216;
						break;
					}
					if (mapId == 26) {
						this.x = 2365;
						this.y = 264;
					}
					break;
				case 26:
					if (mapId == 25) {
						this.x = 35;
						this.y = 240;
						break;
					}
					if (mapId == 27) {
						this.x = 3565;
						this.y = 240;
					}
					break;
				case 27:
					if (mapId == 26) {
						this.x = 35;
						this.y = 408;
						break;
					}
					if (mapId == 28) {
						this.x = 2845;
						this.y = 384;
					}
					break;
				case 28:
					if (mapId == 27) {
						this.x = 35;
						this.y = 288;
						break;
					}
					if (mapId == 60) {
						this.x = 1165;
						this.y = 72;
					}
					break;
				case 29:
					if (mapId == 60) {
						this.x = 35;
						this.y = 912;
						break;
					}
					if (mapId == 30) {
						this.x = 1501;
						this.y = 888;
					}
					break;
				case 30:
					if (mapId == 29) {
						this.x = 35;
						this.y = 240;
						break;
					}
					if (mapId == 31) {
						this.x = 2845;
						this.y = 288;
					}
					break;
				case 31:
					if (mapId == 30) {
						this.x = 35;
						this.y = 264;
						break;
					}
					if (mapId == 32) {
						this.x = 2365;
						this.y = 264;
					}
					break;
				case 32:
					if (mapId == 31) {
						this.x = 35;
						this.y = 384;
						break;
					}
					if (mapId == 61) {
						this.x = 2749;
						this.y = 432;
					}
					break;
				case 33:
					if (mapId == 61) {
						this.x = 35;
						this.y = 216;
						break;
					}
					if (mapId == 34) {
						this.x = 3325;
						this.y = 192;
					}
					break;
				case 34:
					if (mapId == 33) {
						this.x = 35;
						this.y = 168;
						break;
					}
					if (mapId == 35) {
						this.x = 2365;
						this.y = 192;
					}
					break;
				case 35:
					if (mapId == 34) {
						this.x = 35;
						this.y = 672;
						break;
					}
					if (mapId == 66) {
						this.x = 1861;
						this.y = 72;
					}
					break;
				case 36:
					if (mapId == 24) {
						this.x = 35;
						this.y = 368;
						break;
					}
					if (mapId == 37) {
						this.x = 2365;
						this.y = 264;
					}
					break;
				case 37:
					if (mapId == 36) {
						this.x = 35;
						this.y = 648;
					}
					break;
				case 38:
					if (mapId == 67) {
						this.x = 35;
						this.y = 288;
						break;
					}
					if (mapId == 68) {
						this.x = 1885;
						this.y = 288;
					}
					break;
				case 39:
					if (mapId == 72) {
						this.x = 1771;
						this.y = 72;
						break;
					}
					if (mapId == 46) {
						this.x = 85;
						this.y = 312;
						break;
					}
					if (mapId == 40) {
						this.x = 3205;
						this.y = 288;
					}
					break;
				case 40:
					if (mapId == 39) {
						this.x = 35;
						this.y = 264;
						break;
					}
					if (mapId == 41) {
						this.x = 2973;
						this.y = 336;
						break;
					}
					if (mapId == 65) {
						this.x = 3027;
						this.y = 120;
					}
					break;
				case 41:
					if (mapId == 40) {
						this.x = 519;
						this.y = 72;
						break;
					}
					if (mapId == 42) {
						this.x = 35;
						this.y = 360;
						break;
					}
					if (mapId == 43) {
						this.x = 2005;
						this.y = 528;
					}
					break;
				case 42:
					if (mapId == 41) {
						this.x = 925;
						this.y = 912;
						break;
					}
					if (mapId == 62) {
						this.x = 42;
						this.y = 120;
					}
					break;
				case 43:
					if (mapId == 41) {
						this.x = 35;
						this.y = 456;
						break;
					}
					if (mapId == 44) {
						this.x = 2629;
						this.y = 240;
					}
					break;
				case 44:
					if (mapId == 43) {
						this.x = 35;
						this.y = 672;
						break;
					}
					if (mapId == 45) {
						this.x = 1573;
						this.y = 480;
					}
					break;
				case 45:
					if (mapId == 44) {
						this.x = 59;
						this.y = 96;
						break;
					}
					if (mapId == 53) {
						this.x = 1189;
						this.y = 816;
					}
					break;
				case 46:
					if (mapId == 39) {
						this.x = 72;
						this.y = 72;
						break;
					}
					if (mapId == 47) {
						this.x = 1429;
						this.y = 264;
						break;
					}
					if (mapId == 63) {
						this.x = 1429;
						this.y = 672;
					}
					break;
				case 47:
					if (mapId == 46) {
						this.x = 35;
						this.y = 240;
						break;
					}
					if (mapId == 48) {
						this.x = 2365;
						this.y = 384;
					}
					break;
				case 48:
					if (mapId == 47) {
						this.x = 35;
						this.y = 432;
						break;
					}
					if (mapId == 50) {
						this.x = 2869;
						this.y = 336;
					}
					break;
				case 49:
					if (mapId == 50) {
						this.x = 35;
						this.y = 336;
						break;
					}
					if (mapId == 51) {
						this.x = 2365;
						this.y = 456;
					}
					break;
				case 50:
					if (mapId == 48) {
						this.x = 35;
						this.y = 480;
						break;
					}
					if (mapId == 49) {
						this.x = 2221;
						this.y = 432;
					}
					break;
				case 51:
					if (mapId == 49) {
						this.x = 35;
						this.y = 240;
						break;
					}
					if (mapId == 52) {
						this.x = 1645;
						this.y = 288;
					}
					break;
				case 52:
					if (mapId == 51) {
						this.x = 35;
						this.y = 384;
						break;
					}
					if (mapId == 64) {
						this.x = 2869;
						this.y = 288;
					}
					break;
				case 53:
					if (mapId == 45) {
						this.x = 35;
						this.y = 144;
						break;
					}
					if (mapId == 54) {
						this.x = 1165;
						this.y = 1032;
					}
					break;
				case 54:
					if (mapId == 53) {
						this.x = 2347;
						this.y = 72;
						break;
					}
					if (mapId == 55) {
						this.x = 35;
						this.y = 264;
					}
					break;
				case 55:
					if (mapId == 54) {
						this.x = 445;
						this.y = 1386;
					}
					break;
				case 57:
					if (mapId == 12) {
						this.x = 35;
						this.y = 192;
						break;
					}
					if (mapId == 13) {
						this.x = 565;
						this.y = 264;
					}
					break;
				case 58:
					if (mapId == 19) {
						this.x = 35;
						this.y = 192;
					}
					break;
				case 59:
					if (mapId == 68) {
						this.x = 35;
						this.y = 168;
						break;
					}
					if (mapId == 24) {
						this.x = 1407;
						this.y = 168;
					}
					break;
				case 60:
					if (mapId == 28) {
						this.x = 35;
						this.y = 120;
						break;
					}
					if (mapId == 29) {
						this.x = 445;
						this.y = 264;
					}
					break;
				case 61:
					if (mapId == 32) {
						this.x = 35;
						this.y = 192;
						break;
					}
					if (mapId == 33) {
						this.x = 1165;
						this.y = 240;
					}
					break;
				case 62:
					if (mapId == 42) {
						this.x = 1045;
						this.y = 288;
					}
					break;
				case 63:
					if (mapId == 46) {
						this.x = 35;
						this.y = 48;
					}
					break;
				case 64:
					if (mapId == 52) {
						this.x = 35;
						this.y = 816;
					}
					break;
				case 65:
					if (mapId == 40) {
						this.x = 35;
						this.y = 312;
					}
					break;
				case 66:
					if (mapId == 35) {
						this.x = 829;
						this.y = 480;
						break;
					}
					if (mapId == 67) {
						this.x = 1525;
						this.y = 168;
					}
					break;
				case 67:
					if (mapId == 66) {
						this.x = 35;
						this.y = 144;
						break;
					}
					if (mapId == 38) {
						this.x = 685;
						this.y = 816;
					}
					break;
				case 68:
					if (mapId == 38) {
						this.x = 35;
						this.y = 672;
						break;
					}
					if (mapId == 59) {
						this.x = 1285;
						this.y = 408;
					}
					break;
				case 69:
					if (mapId == 23) {
						this.x = 35;
						this.y = 48;
						break;
					}
					if (mapId == 70) {
						this.x = 1405;
						this.y = 216;
					}
					break;
				case 70:
					if (mapId == 69) {
						this.x = 35;
						this.y = 528;
						break;
					}
					if (mapId == 71) {
						this.x = 1645;
						this.y = 192;
					}
					break;
				case 71:
					if (mapId == 70) {
						this.x = 35;
						this.y = 432;
						break;
					}
					if (mapId == 72) {
						this.x = 1645;
						this.y = 432;
					}
					break;
				case 72:
					if (mapId == 71) {
						this.x = 35;
						this.y = 432;
						break;
					}
					if (mapId == 39) {
						this.x = 1809;
						this.y = 672;
					}
					break;
				}
				changeMap(way.mapId);
				return;
			}
		}
		changeMap(this.mapId);
	}

	public void changePk(Message ms) throws IOException {
		byte type = ms.reader().readByte();
		if (type < 0 || type > 3) {
			return;
		}
		this.typePk = type;
		this.user.service.changePk();
	}

	public void changeMap(int id) throws IOException {
		this.mapId = (short) id;
		Map mape = MapManager.getMapById(id);
		Collection<Zone> zones = mape.getZones();
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
		MapManager.outZone(this);
		MapManager.joinZone(this, id, zoneId);
		this.user.service.sendZone();
	}

	public void boxCoinIn(Message ms) throws IOException {
		int xu = ms.reader().readInt();
		if (xu > this.xu) {
			startOKDlg("Số xu ở hành trang không đủ!");
			return;
		}
		this.xu -= xu;
		this.xuInBox += xu;
		this.user.service.boxCoinIn(xu);
	}

	public void boxCoinOut(Message ms) throws IOException {
		int xu = ms.reader().readInt();
		if (xu > this.xuInBox) {
			startOKDlg("Số xu ở rương không đủ!");
			return;
		}
		this.xuInBox -= xu;
		this.xu += xu;
		this.user.service.boxCoinOut(xu);
	}

	public void requestCharacterInfo(Message ms) throws IOException {
		int num = ms.reader().readByte();
		this.logger.log("requestCharacterInfo num: " + num);
		for (int i = 0; i < num; i++) {
			this.logger.log("requestCharacterInfo charId: " + ms.reader().readInt());
		}
	}

	public void requestItemCharacter(Message ms) throws IOException {
		int charId = ms.reader().readInt();
		byte indexUI = ms.reader().readByte();
		Character _char = getCharacterById(charId);
		if (_char != null && _char.equiped[indexUI] != null) {
			Equiped equiped = _char.equiped[indexUI];
			this.user.service.requesItemCharacter(equiped);
		}
	}

	public static Character getCharacterById(int charId) {
		return characters_id.get(Integer.valueOf(charId));
	}

	public static Character getCharacterByName(String name) {
		return characters_name.get(name);
	}

	public void requestItem(Message ms) throws IOException {
		byte typeUI = ms.reader().readByte();
		switch (typeUI) {

		case 4:
			this.user.service.sendBox();
			return;
		}

		store(typeUI);
	}

	public void requestEnemies() throws IOException {
	}

	public void store(byte type) throws IOException {
		this.logger.log("Store: " + String.valueOf(type));
		ArrayList<ItemStore> store = null;
		switch (type) {
		case 2:
			store = StoreData.VU_KHI;
			break;

		case 6:
			store = StoreData.DUOC_PHAM;
			break;

		case 7:
			store = StoreData.DUOC_PHAM_KHOA;
			break;

		case 8:
			store = StoreData.THUC_AN;
			break;

		case 9:
			store = StoreData.THUC_AN_KHOA;
			break;

		case 14:
			store = StoreData.LINH_TINH;
			break;

		case 15:
			store = StoreData.SACH;
			break;

		case 32:
			store = StoreData.THOI_TRANG;
			break;

		case 34:
			store = StoreData.GIA_TOC;
			break;

		case 16:
			store = StoreData.DAY_CHUYEN;
			break;

		case 17:
			store = StoreData.NHAN;
			break;

		case 18:
			store = StoreData.NGOC_BOI;
			break;

		case 19:
			store = StoreData.BUA;
			break;

		case 20:
			store = StoreData.NON_NAM;
			break;

		case 21:
			store = StoreData.NON_NU;
			break;

		case 22:
			store = StoreData.AO_NAM;
			break;

		case 23:
			store = StoreData.AO_NU;
			break;

		case 24:
			store = StoreData.GANG_NAM;
			break;

		case 25:
			store = StoreData.GANG_NU;
			break;

		case 26:
			store = StoreData.QUAN_NAM;
			break;

		case 27:
			store = StoreData.QUAN_NU;
			break;

		case 28:
			store = StoreData.GIAY_NAM;
			break;

		case 29:
			store = StoreData.GIAY_NU;
			break;
		}
		if (store != null) {
			Message ms = new Message(33);
			DataOutputStream ds = ms.writer();
			ds.writeByte(type);
			int num = store.size();
			ds.writeByte(num);
			for (ItemStore item : store) {
				ds.writeByte(item.index);
				ds.writeShort(item.templateId);
			}
			ds.flush();
			sendMessage(ms);
		}
	}

	public void requestItemInfo(Message ms) throws IOException {
		byte typeUI = ms.reader().readByte();
		byte indexUI = ms.reader().readByte();
		this.logger.log("requestItemInfo type: " + typeUI + " - indexUI: " + indexUI);
		if (typeUI == 2 || (typeUI >= 14 && typeUI <= 29) || typeUI == 32 || typeUI == 34 || typeUI == 8 || typeUI == 9
				|| typeUI == 7 || typeUI == 8) {
			ItemStore item = new ItemStore();
			switch (typeUI) {
			case 2:
				item = StoreData.VU_KHI.get(indexUI);
				break;

			case 6:
				item = StoreData.DUOC_PHAM.get(indexUI);
				break;

			case 7:
				item = StoreData.DUOC_PHAM_KHOA.get(indexUI);
				break;

			case 8:
				item = StoreData.THUC_AN.get(indexUI);
				break;

			case 9:
				item = StoreData.THUC_AN_KHOA.get(indexUI);
				break;

			case 14:
				item = StoreData.LINH_TINH.get(indexUI);
				break;

			case 15:
				item = StoreData.SACH.get(indexUI);
				break;

			case 32:
				item = StoreData.THOI_TRANG.get(indexUI);
				break;

			case 34:
				item = StoreData.GIA_TOC.get(indexUI);
				break;

			case 16:
				item = StoreData.DAY_CHUYEN.get(indexUI);
				break;

			case 17:
				item = StoreData.NHAN.get(indexUI);
				break;

			case 18:
				item = StoreData.NGOC_BOI.get(indexUI);
				break;

			case 19:
				item = StoreData.BUA.get(indexUI);
				break;

			case 20:
				item = StoreData.NON_NAM.get(indexUI);
				break;

			case 21:
				item = StoreData.NON_NU.get(indexUI);
				break;

			case 22:
				item = StoreData.NON_NAM.get(indexUI);
				break;

			case 23:
				item = StoreData.NON_NU.get(indexUI);
				break;

			case 24:
				item = StoreData.GANG_NAM.get(indexUI);
				break;

			case 25:
				item = StoreData.GANG_NU.get(indexUI);
				break;

			case 26:
				item = StoreData.QUAN_NAM.get(indexUI);
				break;

			case 27:
				item = StoreData.QUAN_NU.get(indexUI);
				break;

			case 28:
				item = StoreData.GIAY_NAM.get(indexUI);
				break;

			case 29:
				item = StoreData.GIAY_NU.get(indexUI);
				break;
			}
			this.user.service.itemStoreInfo(item, typeUI, indexUI);
		} else if (typeUI == 3) {
			if (this.bag[indexUI] != null) {
				this.user.service.itemInfo(this.bag[indexUI], typeUI, indexUI);
			}
		} else if (typeUI == 4) {
			if (this.box[indexUI] != null) {
				this.user.service.itemInfo(this.box[indexUI], typeUI, indexUI);
			}
		} else if (typeUI == 5) {
			if (this.equiped[indexUI] != null) {
				this.user.service.equipedInfo(this.equiped[indexUI], typeUI, indexUI);
			}
		} else if (typeUI == 30 && this.trade != null) {
			this.trade.viewItemInfo(this, typeUI, indexUI);
		}
	}

	public int getSlotNull() {
		int number = 0;
		for (int i = 0; i < this.numberCellBag; i++) {
			if (this.bag[i] == null) {
				number++;
			}
		}
		return number;
	}

	public void sellItem(Message mss) throws IOException {
		if (!isVillage() && !isSchool()) {
			startOKDlg("Vui lòng về trường hoặc làng để bán vật phẩm.");
			return;
		}
		byte indexUI = mss.reader().readByte();
		int quantity = 1;
		if (mss.reader().available() > 0) {
			quantity = mss.reader().readShort();
		}
		if (this.bag[indexUI] != null && (this.bag[indexUI]).upgrade == 0) {
			addYen(quantity * (this.bag[indexUI]).yen);
			removeItem(indexUI, quantity, true);
			startOKDlg("Bán vật phẩm thành công!");
		} else {
			startOKDlg("Có lỗi xảy ra!");
		}
	}

	public void buyItem(Message mss) throws IOException {
		if (!isVillage() && !isSchool()) {
			startOKDlg("Vui lòng về trường hoặc làng để mua vật phẩm.");
			return;
		}
		byte typeUI = mss.reader().readByte();
		byte indexUI = mss.reader().readByte();
		int quantity = 1;
		if (mss.reader().available() > 0) {
			quantity = mss.reader().readShort();
		}
		if (quantity < 1) {
			startOKDlg("Số lượng không hợp lệ!");

			return;
		}
		ArrayList<ItemStore> store = null;
		switch (typeUI) {
		case 2:
			store = StoreData.VU_KHI;
			break;

		case 6:
			store = StoreData.DUOC_PHAM;
			break;

		case 7:
			store = StoreData.DUOC_PHAM_KHOA;
			break;

		case 8:
			store = StoreData.THUC_AN;
			break;

		case 9:
			store = StoreData.THUC_AN_KHOA;
			break;

		case 14:
			store = StoreData.LINH_TINH;
			break;

		case 15:
			store = StoreData.SACH;
			break;

		case 32:
			store = StoreData.THOI_TRANG;
			break;

		case 34:
			store = StoreData.GIA_TOC;
			break;

		case 16:
			store = StoreData.DAY_CHUYEN;
			break;
		case 17:
			store = StoreData.NHAN;
			break;

		case 18:
			store = StoreData.NGOC_BOI;
			break;

		case 19:
			store = StoreData.BUA;
			break;

		case 20:
			store = StoreData.NON_NAM;
			break;

		case 21:
			store = StoreData.NON_NU;
			break;

		case 22:
			store = StoreData.AO_NAM;
			break;

		case 23:
			store = StoreData.AO_NU;
			break;

		case 24:
			store = StoreData.GANG_NAM;
			break;

		case 25:
			store = StoreData.GANG_NU;
			break;

		case 26:
			store = StoreData.QUAN_NAM;
			break;

		case 27:
			store = StoreData.QUAN_NU;
			break;

		case 28:
			store = StoreData.GIAY_NAM;
			break;

		case 29:
			store = StoreData.GIAY_NU;
			break;
		}

		if (store != null) {
			ItemStore item = store.get(indexUI);
			int slotNull = getSlotNull();
			if ((item.entry.isUpToUp && slotNull == 0) || (!item.entry.isUpToUp && slotNull < quantity)) {
				startOKDlg("Hành trang đã đầy!");
				return;
			}
			if (item == null) {
				startOKDlg("Có lỗi xảy ra!");
				return;
			}
			int giaXu = item.xu * quantity;
			int giaYen = item.yen * quantity;
			int giaLuong = item.luong * quantity;
			if (giaXu > this.xu || giaLuong > this.user.luong || giaYen > this.yen) {
				startOKDlg("Không đủ tiền!");
				return;
			}
			updateXu(this.xu - giaXu);
			updateLuong(this.user.luong - giaLuong);
			updateYen(this.yen - giaYen);
			if (item.entry.isUpToUp) {
				Item add = new Item(item.templateId);
				if (item.expire != -1L) {
					add.expire = (new Date()).getTime() + item.expire;
				} else {
					add.expire = -1L;
				}
				add.isLock = item.isLock;
				add.quantity = quantity;
				add.upgrade = 0;
				if (item.entry.isTypeBody() || item.entry.isTypeNgocKham() || item.entry.isTypeMount()) {
					add.quantity = 1;
					int num = item.option_max.length;
					add.options = new ArrayList<>();
					for (int a = 0; a < num; a++) {
						int templateId = item.option_max[a][0];
						int param = NinjaUtil.nextInt(item.option_min[a][1], item.option_max[a][1]);
						add.options.add(new ItemOption(templateId, param));
					}
				}
				addItemToBag(add);
			} else {
				for (int i = 0; i < quantity; i++) {
					Item add = new Item(item.templateId);
					if (item.expire != -1L) {
						add.expire = (new Date()).getTime() + item.expire;
					} else {
						add.expire = -1L;
					}
					add.isLock = item.isLock;
					add.quantity = 1;
					add.upgrade = 0;
					add.sys = item.sys;
					add.yen = item.entry.level / 10 * 100;
					if (item.entry.isTypeClothe()) {
						add.yen *= 3;
					} else if (item.entry.isTypeAdorn()) {
						add.yen *= 4;
					} else if (item.entry.isTypeWeapon()) {
						add.yen *= 5;
					} else if (item.entry.isTypeMount() || item.entry.isTypeNgocKham()) {
						add.yen = 5;
					} else {
						add.yen = 0;
					}
					if (item.entry.isTypeBody() || item.entry.isTypeNgocKham() || item.entry.isTypeMount()) {
						int num = item.option_max.length;
						add.options = new ArrayList<>();
						for (int a = 0; a < num; a++) {
							int templateId = item.option_max[a][0];
							int param = NinjaUtil.nextInt(item.option_min[a][1], item.option_max[a][1]);
							add.options.add(new ItemOption(templateId, param));
						}
					}
					addItemToBag(add);
				}
			}
		}
	}

	public void viewInfo(Message mss) throws IOException {
		String name = mss.reader().readUTF();
		Character _char = getCharacterByName(name);
		if (_char != null) {
			this.user.service.viewInfo(_char);
		} else {
			this.user.service.addInfoMe("Người này hiện tại không online.");
		}
	}

	public int getSys() {
		if (this.classId == 1 || this.classId == 2) {
			return 1;
		}
		if (this.classId == 3 || this.classId == 4) {
			return 2;
		}
		if (this.classId == 5 || this.classId == 6) {
			return 3;
		}
		return 0;
	}

	private boolean isVillage() {
		short[] map = { 10, 17, 22, 32, 38, 43, 48 };
		for (short m : map) {
			if (this.mapId == m) {
				return true;
			}
		}
		return false;
	}

	private boolean isSchool() {
		short[] map = { 1, 27, 72 };
		for (short m : map) {
			if (this.mapId == m) {
				return true;
			}
		}
		return false;
	}

	public void addCuuSat(Message ms) throws IOException {
		if (isVillage() || isSchool()) {
			startOKDlg("Không được cừu sát ở trong trường hoặc làng.");
			return;
		}
		int charId = ms.reader().readInt();
	}

	public void addXu(int xu) throws IOException {
		this.xu += xu;
		this.user.service.addXu(xu);
	}

	public void addYen(int yen) throws IOException {
		this.yen += yen;
		this.user.service.addYen(yen);
	}

	public void addExp(long exp) throws IOException {
		if (this.expDown > 0L) {
			this.expDown -= exp;
			Message ms = new Message(71);
			DataOutputStream ds = ms.writer();
			ds.writeLong(exp);
			ds.flush();
			sendMessage(ms);
			ms.cleanup();
		} else {
			this.exp += exp;
			int preLevel = this.level;
			this.level = NinjaUtil.getLevel(this.exp);
			int nextLevel = this.level;
			int num = nextLevel - preLevel;
			if (num > 0) {
				if (this.classId == 0) {
					this.potential[0] = (short) (this.potential[0] + 5 * num);
					this.potential[1] = (short) (this.potential[1] + 2 * num);
					this.potential[2] = (short) (this.potential[2] + 2 * num);
					this.potential[3] = (short) (this.potential[3] + 2 * num);
				} else {
					for (int i = preLevel; i < nextLevel; i++) {
						if (i >= 10 && i <= 69) {
							this.point = (short) (this.point + 10);
						} else if (i >= 70 && i <= 79) {
							this.point = (short) (this.point + 20);
						} else if (i >= 80 && i <= 89) {
							this.point = (short) (this.point + 30);
						} else if (i >= 90 && i <= 99) {
							this.point = (short) (this.point + 40);
						} else {
							this.point = (short) (this.point + 50);
						}
					}
					this.spoint = (short) (this.spoint + num);
				}
			}
			setAbility();
			Message ms = new Message(5);
			DataOutputStream ds = ms.writer();
			ds.writeLong(exp);
			ds.flush();
			sendMessage(ms);
			ms.cleanup();
			if (preLevel != nextLevel) {
				this.user.service.levelUp();
			}
		}
	}

	public void removeFriend(Message ms) throws IOException {
		String name = ms.reader().readUTF();
		if (this.friends.get(name) != null) {
			this.friends.remove(name);
			this.user.service.removeFriend(name);
		} else {
			this.user.service.addInfoMe("Người này không tồn tại.");
		}
	}

	public void addFriend(Message ms) throws IOException {
		String name = ms.reader().readUTF();
		Character _char = getCharacterByName(name);
		if (_char == null) {
			this.user.service.addInfoMe("Người này không online hoặc không tồn tại!");
			return;
		}
		Friend friend = this.friends.get(name);
		if (friend != null) {
			this.user.service.addInfoMe(name + " đã có trong danh sách bạn bè.");
			return;
		}
		Friend me = _char.friends.get(this.name);
		if (me != null) {
			me.type = 1;
			this.friends.put(_char.name, new Friend(_char.name, (byte) 1));
			this.user.service.addInfoMe("Bạn và " + name + " đã trở thành hảo hữu.");
			_char.user.service.addInfoMe("Bạn và " + this.name + " đã trở thành hảo hữu.");
			return;
		}
		this.friends.put(_char.name, new Friend(_char.name, (byte) 0));

		Message m = new Message(59);
		DataOutputStream ds = m.writer();
		ds.writeUTF(this.name);
		ds.flush();
		_char.sendMessage(m);
		this.user.service.addInfoMe("Đã thêm " + name + " vào danh sách bạn bè.");
	}

	public void updateXu(int xu) throws IOException {
		this.logger.log("update xu: " + xu);
		this.xu = xu;
		this.user.service.update();
	}

	public void updateLuong(int luong) throws IOException {
		this.logger.log("update luong: " + luong);
		this.user.luong = luong;
		this.user.service.update();
	}

	public void updateYen(int yen) throws IOException {
		this.logger.log("update yen: " + yen);
		this.yen = yen;
		this.user.service.update();
	}

	public int getXu() {
		return this.xu;
	}

	public int getYen() {
		return this.yen;
	}

	public void npcChat(short npcId, String text) throws IOException {
		Message ms = new Message(38);
		ms.writer().writeShort(npcId);
		ms.writer().writeUTF(text);
		ms.writer().flush();
		sendMessage(ms);
		ms.cleanup();
	}

	public void saveRms(Message mss) throws IOException {
		String key = mss.reader().readUTF();
		int len = mss.reader().readInt();
		byte[] ab = new byte[len];
		if (ab == null) {
			return;
		}
		mss.reader().read(ab);
		byte type = mss.reader().readByte();
		switch (key) {
		case "KSkill":
			if (ab.length == 3) {
				this.onKSkill = ab;
			}
			break;

		case "OSkill":
			if (ab.length == 5) {
				this.onOSkill = ab;
			}
			break;

		case "CSkill":
			this.onCSkill = ab;
			break;
		}
	}

	public void loadSkillShortcut(Message mss) throws IOException {
		String key = mss.reader().readUTF();
		byte[] data = new byte[0];
		switch (key) {
		case "KSkill":
			data = this.onKSkill;
			break;

		case "OSkill":
			data = this.onOSkill;
			break;

		case "CSkill":
			data = this.onCSkill;
			break;
		}
		this.user.service.sendSkillShortcut(key, data);
	}

	public synchronized void flushCache() {
		try {
			MapManager.outZone(this);
			characters_name.remove(this.name);
			characters_id.remove(Integer.valueOf(this.id));
			if (this.trade != null) {
				this.trade.closeMenu();
				this.trade = null;
			}
			JSONArray jArr = new JSONArray();
			if (this.hp > 0) {
				jArr.add(Short.valueOf(this.mapId));
				jArr.add(Short.valueOf(this.x));
				jArr.add(Short.valueOf(this.y));
			} else {
				short[] xy = NinjaUtil.getXY(this.saveCoordinate);
				jArr.add(Short.valueOf(this.saveCoordinate));
				jArr.add(Short.valueOf(xy[0]));
				jArr.add(Short.valueOf(xy[1]));
			}
			JSONArray items = new JSONArray();
			for (int i = 0; i < this.numberCellBag; i++) {
				if (this.bag[i] != null) {
					JSONObject item = new JSONObject();
					item.put("index", Integer.valueOf((this.bag[i]).index));
					item.put("id", Integer.valueOf((this.bag[i]).id));
					item.put("expire", Long.valueOf((this.bag[i]).expire));
					item.put("sys", Byte.valueOf((this.bag[i]).sys));
					item.put("isLock", Boolean.valueOf((this.bag[i]).isLock));
					item.put("yen", Integer.valueOf((this.bag[i]).yen));
					if ((this.bag[i]).entry.isTypeBody() || (this.bag[i]).entry.isTypeMount()
							|| (this.bag[i]).entry.isTypeNgocKham()) {
						item.put("upgrade", Byte.valueOf((this.bag[i]).upgrade));
						JSONArray abilitys = new JSONArray();
						if ((this.bag[i]).options != null) {
							for (ItemOption option : (this.bag[i]).options) {
								JSONArray ability = new JSONArray();
								ability.add(Integer.valueOf(option.optionTemplate.id));
								ability.add(Integer.valueOf(option.param));
								abilitys.add(ability);
							}
						}
						item.put("options", abilitys);
					}
					if ((this.bag[i]).entry.isUpToUp) {
						item.put("quantity", Integer.valueOf((this.bag[i]).quantity));
					}
					items.add(item);
				}
			}
			JSONArray boxs = new JSONArray();
			for (int j = 0; j < this.numberCellBox; j++) {
				if (this.box[j] != null) {
					JSONObject box = new JSONObject();
					box.put("index", Integer.valueOf((this.box[j]).index));
					box.put("id", Integer.valueOf((this.box[j]).id));
					box.put("expire", Long.valueOf((this.box[j]).expire));
					box.put("sys", Byte.valueOf((this.box[j]).sys));
					box.put("isLock", Boolean.valueOf((this.box[j]).isLock));
					box.put("yen", Integer.valueOf((this.box[j]).yen));
					if ((this.box[j]).entry.isTypeBody() || (this.box[j]).entry.isTypeMount()
							|| (this.box[j]).entry.isTypeNgocKham()) {
						box.put("upgrade", Byte.valueOf((this.box[j]).upgrade));
						JSONArray options = new JSONArray();
						for (ItemOption option : (this.box[j]).options) {
							JSONArray ab = new JSONArray();
							ab.add(Integer.valueOf(option.optionTemplate.id));
							ab.add(Integer.valueOf(option.param));
							options.add(ab);
						}
						box.put("options", options);
					}
					if ((this.box[j]).entry.isUpToUp) {
						box.put("quantity", Integer.valueOf((this.box[j]).quantity));
					}
					boxs.add(box);
				}
			}

			JSONArray equiped = new JSONArray();
			for (int k = 0; k < 16; k++) {
				if (this.equiped[k] != null) {
					JSONObject equip = new JSONObject();
					equip.put("id", Integer.valueOf((this.equiped[k]).id));
					equip.put("expire", Long.valueOf((this.equiped[k]).expire));
					equip.put("sys", Byte.valueOf((this.equiped[k]).sys));
					equip.put("yen", Integer.valueOf((this.equiped[k]).yen));
					equip.put("upgrade", Byte.valueOf((this.equiped[k]).upgrade));
					JSONArray options = new JSONArray();
					for (ItemOption option : (this.equiped[k]).options) {
						JSONArray ability = new JSONArray();
						ability.add(Integer.valueOf(option.optionTemplate.id));
						ability.add(Integer.valueOf(option.param));
						options.add(ability);
					}
					equip.put("options", options);
					equiped.add(equip);
				}
			}
			JSONArray mounts = new JSONArray();
			for (int m = 0; m < 5; m++) {
				if (this.mount[m] != null) {
					JSONObject mount = new JSONObject();
					mount.put("id", Integer.valueOf((this.mount[m]).id));
					mount.put("expire", Long.valueOf((this.mount[m]).expire));
					mount.put("sys", Byte.valueOf((this.mount[m]).sys));
					mount.put("yen", Integer.valueOf((this.mount[m]).yen));
					mount.put("level", Byte.valueOf((this.mount[m]).level));
					JSONArray options = new JSONArray();
					for (ItemOption option : (this.mount[m]).options) {
						JSONArray ability = new JSONArray();
						ability.add(Integer.valueOf(option.optionTemplate.id));
						ability.add(Integer.valueOf(option.param));
						options.add(ability);
					}
					mount.put("options", options);
					mounts.add(mount);
				}
			}
			JSONArray skill = new JSONArray();
			if (this.listSkill != null && this.listSkill.size() > 0) {
				for (MySkill s : this.listSkill) {
					JSONObject obj = new JSONObject();
					obj.put("id", Integer.valueOf(s.id));
					obj.put("point", Integer.valueOf(s.point));
					skill.add(obj);
				}
			}
			JSONObject data = new JSONObject();
			data.put("exp", Long.valueOf(this.exp));
			data.put("expDown", Long.valueOf(this.expDown));
			data.put("countPB", Byte.valueOf(this.countPB));
			data.put("hieuChien", Byte.valueOf(this.hieuChien));
			data.put("countFinishDay", Byte.valueOf(this.countFinishDay));
			data.put("countLoosBoss", Byte.valueOf(this.countLoosBoss));
			data.put("limitKyNangSo", Byte.valueOf(this.limitKyNangSo));
			data.put("limitTiemNangSo", Byte.valueOf(this.limitTiemNangSo));
			data.put("tayTiemNang", Short.valueOf(this.tayTiemNang));
			data.put("tayKyNang", Short.valueOf(this.tayKyNang));
			data.put("numberUseExpanedBag", Byte.valueOf(this.numberUseExpanedBag));
			String potential = Arrays.toString(this.potential).replace(" ", "");
			if (this.onOSkill == null) {
				this.onOSkill = new byte[] { -1, -1, -1, -1, -1 };
			}
			if (this.onKSkill == null) {
				this.onKSkill = new byte[] { -1, -1, -1 };
			}
			if (this.onCSkill == null) {
				this.onCSkill = new byte[0];
			}
			String onOSkill = Arrays.toString(this.onOSkill).replace(" ", "");
			String onCSkill = Arrays.toString(this.onCSkill).replace(" ", "");
			String onKSkill = Arrays.toString(this.onKSkill).replace(" ", "");

			PreparedStatement stmt = Connect.conn.prepareStatement(
					"UPDATE `player` SET `xu` = ?, `xuInBox` = ?, `yen` = ?, `point` = ?, `spoint` = ?, `saveCoordinate` = ?, `numberCellBag` = ?, `numberCellBox` = ? WHERE `id` = ?");
			stmt.setInt(1, this.xu);
			stmt.setInt(2, this.xuInBox);
			stmt.setInt(3, this.yen);
			stmt.setInt(4, this.point);
			stmt.setInt(5, this.spoint);
			stmt.setInt(6, this.saveCoordinate);
			stmt.setInt(7, this.numberCellBag);
			stmt.setInt(8, this.numberCellBox);
			stmt.setInt(9, this.id);
			stmt.execute();
			stmt = Connect.conn.prepareStatement(
					"UPDATE `player` SET `onCSkill` = ?, `onKSkill` = ?, `onOSKill` = ?, `data` = ?, `skill` = ?, `potential` = ?, `map` = ?, `equiped` = ?, `bag` = ?, `box` = ?, `mount` = ? WHERE `id` = ?");
			stmt.setString(1, onCSkill);
			stmt.setString(2, onKSkill);
			stmt.setString(3, onOSkill);
			stmt.setString(4, data.toJSONString());
			stmt.setString(5, skill.toJSONString());
			stmt.setString(6, potential);
			stmt.setString(7, jArr.toJSONString());
			stmt.setString(8, equiped.toJSONString());
			stmt.setString(9, items.toJSONString());
			stmt.setString(10, boxs.toJSONString());
			stmt.setString(11, mounts.toJSONString());
			stmt.setInt(12, this.id);
			stmt.execute();
			JSONArray effects = new JSONArray();
			if (this.effects != null && this.effects.size() > 0) {
				for (Effect eff : this.effects.values()) {
					JSONObject job = new JSONObject();
					job.put("id", Byte.valueOf(eff.template.id));
					job.put("timeStart", Integer.valueOf(eff.timeStart));
					job.put("timeLength", Integer.valueOf(eff.timeLength));
					job.put("param", Short.valueOf(eff.param));
					effects.add(job);
				}
			}
			JSONArray friends = new JSONArray();
			if (this.friends != null && this.friends.size() > 0) {
				for (Friend friend : this.friends.values()) {
					JSONObject job = new JSONObject();
					job.put("name", friend.name);
					job.put("type", Byte.valueOf(friend.type));
					friends.add(job);
				}
			}
			stmt = Connect.conn
					.prepareStatement("UPDATE `player` SET `class` = ?, `effect` = ?, `friend` = ? WHERE `id` = ?");
			stmt.setByte(1, this.classId);
			stmt.setString(2, effects.toJSONString());
			stmt.setString(3, friends.toJSONString());
			stmt.setInt(4, this.id);
			stmt.execute();
			this.bag = null;
			this.box = null;
			this.equiped = null;
		} catch (Exception ex) {
			this.logger.debug("flushCache", ex.getMessage());
		}
	}

	public synchronized void sendToMap(Message ms) throws IOException {
		Character[] characters = this.zone.getCharacters();
		for (Character pl : characters) {
			if (pl != null) {
				pl.sendMessage(ms);
			}
		}
	}

	public boolean equals(Object obj) {
		if (obj != null) {
			Character _char = (Character) obj;
			if (_char != null && _char.id == this.id) {
				return true;
			}
		}
		return false;
	}

	public void startOKDlg(String text) throws IOException {
		this.user.service.startOKDlg(text);
	}

	public void sendMessage(Message ms) {
		this.user.client.sendMessage(ms);
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * server\Character.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */