package com.kitakeyos.server;

import com.kitakeyos.io.Message;
import com.kitakeyos.object.ItemMap;
import com.kitakeyos.util.NinjaUtil;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Monster {
	public int mobId;
	public boolean isDisable;
	public boolean isDontMove;
	public boolean isFire;
	public boolean isIce;
	public boolean isWind;
	public byte sys;
	public short templateId;
	public int hp;
	public int maxhp;
	public int originalHp;
	public short level;
	public short x;
	public short y;
	public byte status;
	public byte levelBoss;
	public boolean isBoss;
	public long lastTimeAttack;
	public long attackDelay = 2000L;

	public int recoveryTimeCount;

	public HashMap<Integer, Character> characters;

	private boolean isDead;
	public static final int[] LIST_ITEM_BOSS = new int[] { 253, 253, 9, 253, 252, 252, 12, 12, 12, 12, 11, 11, 11, 10,
			10, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 5 };
	public int dame;
	public int dame2;
	public Zone zone;
	public boolean isBusyAttackSokeOne;

	public Monster(int id, short templateId, int hp, short level, short x, short y, boolean isBoss, Zone zone) {
		this.mobId = id;
		this.templateId = templateId;
		this.originalHp = hp;
		this.level = level;
		this.x = x;
		this.y = y;
		this.isDisable = false;
		this.isDontMove = false;
		this.status = 5;
		this.isBoss = isBoss;
		this.levelBoss = 0;
		this.isFire = this.isIce = this.isWind = false;
		this.isDead = false;
		this.characters = new HashMap<>();
		this.zone = zone;
		setClass();
		setLevelBoss();
		setHP();
		setDamage();
	}

	public void setClass() {
		this.sys = (byte) NinjaUtil.nextInt(1, 3);
	}

	public void setLevelBoss() {
		if (NinjaUtil.nextInt(100) < 1 && this.level >= 10 && !this.isBoss) {
			this.levelBoss = 2;
		} else if (NinjaUtil.nextInt(50) < 1 && this.level >= 10 && !this.isBoss) {
			this.levelBoss = 1;
		} else {
			this.levelBoss = 0;
		}
	}

	public void setDamage() {
		this.dame = 1000 / (this.level + 10);
		if (this.levelBoss == 1) {
			this.dame *= 2;
		} else if (this.levelBoss == 2) {
			this.dame *= 3;
		}
		this.dame2 = this.dame - this.dame / 10;
	}

	public void setHP() {
		if (this.levelBoss == 1) {
			this.hp = this.maxhp = this.originalHp * 10;
		} else if (this.levelBoss == 2) {
			this.hp = this.maxhp = this.originalHp * 100;
		} else {
			this.hp = this.maxhp = this.originalHp;
		}
	}

	public void recovery() {
		this.isDead = false;
		setClass();
		setLevelBoss();
		setHP();
		setDamage();
		this.status = 5;
	}

	public void die() {
		this.hp = 0;
		this.status = 1;
		this.recoveryTimeCount = 5;
		this.isDead = true;
		this.characters.clear();
	}

	public void dropItem(Character owner) {
		try {
			this.zone.numberDropItem = (short) (this.zone.numberDropItem + 1);
			ItemMap itemMap = new ItemMap(this.zone.numberDropItem);
			itemMap.owner = owner;
			itemMap.x = (short) NinjaUtil.nextInt(this.x - 20, this.x + 20);
			itemMap.y = this.y;
			int rd = NinjaUtil.nextInt(2);

			short[] arId = { 12, 12, 434, 12, 12, 434, 434, 435, 434, 435, 434 };
			int itemId = arId[NinjaUtil.nextInt(arId.length)];
			if (this.isBoss) {
				itemId = LIST_ITEM_BOSS[NinjaUtil.nextInt(LIST_ITEM_BOSS.length)];
			} else if (rd == 0) {
				itemId = this.level / 10;
				itemId = (itemId > 4) ? 4 : itemId;
			}
			Character.Item item = new Character.Item(itemId);
			item.isLock = true;
			if (item.id == 12) {
				item.quantity = NinjaUtil.nextInt(this.level * 100, this.level * 200);
				if (this.levelBoss == 1) {
					item.quantity *= 5;
					item.isLock = false;
				} else if (this.levelBoss == 2) {
					item.quantity *= 10;
					item.isLock = false;
				}
			} else {
				item.quantity = 1;
			}
			item.expire = -1L;
			itemMap.item = item;
			this.zone.put(itemMap.id, itemMap);
			Message m = new Message(6);
			DataOutputStream ds = m.writer();
			ds.writeShort(itemMap.id);
			ds.writeShort(itemMap.item.id);
			ds.writeShort(itemMap.x);
			ds.writeShort(itemMap.y);
			ds.flush();
			this.zone.sendMessage(m);
			m.cleanup();
		} catch (IOException ex) {
			Logger.getLogger(Monster.class.getName()).log(Level.SEVERE, (String) null, ex);
		}
	}

	private void attack() {
		int i = 0;
		ArrayList<Character> list = new ArrayList<>();
		for (Character _char : this.characters.values()) {
			if (this.zone.findCharInMap(_char.id) != null) {
				int rangeX = NinjaUtil.getRange(this.x, _char.x);
				int rangeY = NinjaUtil.getRange(this.y, _char.y);
				if (rangeX > 200 || rangeY > 200) {
					continue;
				}
				list.add(_char);
			}
		}
		if (list.size() == 0) {
			return;
		}
		int rand = NinjaUtil.nextInt(list.size());
		Character pl = list.get(rand);
		if (pl != null && !pl.isDead) {
			try {
				int dame = NinjaUtil.nextInt(this.dame2, this.dame);
				dame -= pl.dameDown;
				int random = NinjaUtil.nextInt(1000);
				if (random < pl.miss) {
					dame = -1;
				} else if (dame <= 0) {
					dame = 1;
				}

				pl.hp -= dame;
				Message ms = new Message(-3);
				DataOutputStream ds = ms.writer();
				ds.writeByte(this.mobId);
				ds.writeInt(dame);
				ds.writeInt(0);
				ds.writeShort(-1);
				ds.writeByte(0);
				ds.writeByte(0);
				ds.flush();
				pl.sendMessage(ms);

				ms = new Message(-2);
				ds = ms.writer();
				ds.writeByte(this.mobId);
				ds.writeInt(pl.id);
				ds.writeInt(pl.hp);
				ds.writeInt(pl.mp);
				ds.writeShort(-1);
				ds.writeByte(0);
				ds.writeByte(0);
				ds.flush();
				Character[] characters = this.zone.getCharacters();
				for (Character _char : characters) {
					if (!_char.equals(pl)) {
						_char.sendMessage(ms);
					}
				}
				if (pl.hp <= 0) {
					pl.die();
					pl.waitToDie();
				}
			} catch (IOException ex) {
				Logger.getLogger(Monster.class.getName()).log(Level.SEVERE, (String) null, ex);
			}
		}
	}

	public void update() {
		if (!this.isDead && this.templateId != 0 && this.characters.size() > 0) {
			long now = System.currentTimeMillis();
			if (now - this.lastTimeAttack > this.attackDelay) {
				this.lastTimeAttack = now;
				attack();
			}
		}
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * server\Monster.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */