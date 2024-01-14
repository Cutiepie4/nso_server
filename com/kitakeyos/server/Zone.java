package com.kitakeyos.server;

import com.kitakeyos.io.Message;
import com.kitakeyos.object.BuNhin;
import com.kitakeyos.object.ItemMap;
import com.kitakeyos.object.TileMap;
import com.kitakeyos.template.MonsterTemplate;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Zone {
	private HashMap<Integer, Character> mChar = new HashMap<>();
	private HashMap<Short, ItemMap> mItemMap = new HashMap<>();
	private HashMap<Integer, Monster> mMob = new HashMap<>();
	private HashMap<String, BuNhin> mBuNhin = new HashMap<>();
	private ArrayList<ItemMap> listRemoveItem = new ArrayList<>();
	private ArrayList<Integer> listRemoveCharacter = new ArrayList<>();
	public short numberDropItem = 0;
	public byte zoneId;
	public byte numberCharacter = 0;
	public byte numberGroup = 0;
	public TileMap tilemap;
	public ArrayList<Monster> waitingListRecoverys = new ArrayList<>();
	public ArrayList<Monster> listRecoverys = new ArrayList<>();

	public Zone(byte id, TileMap tilemap) {
		this.zoneId = id;
		this.tilemap = tilemap;
		createMonster();
	}

	public ItemMap getItemMapById(int id) {
		return this.mItemMap.get(Short.valueOf((short) id));
	}

	public void put(int id, ItemMap item) {
		this.mItemMap.put(Short.valueOf((short) id), item);
	}

	public Monster getMonsterById(int id) {
		return this.mMob.get(Integer.valueOf(id));
	}

	public void put(int id, Monster monster) {
		this.mMob.put(Integer.valueOf(id), monster);
	}

	public Character findCharInMap(int id) {
		return this.mChar.get(Integer.valueOf(id));
	}

	public void put(int id, Character _char) {
		this.mChar.put(Integer.valueOf(id), _char);
	}

	public BuNhin getBuNhinByName(String name) {
		return this.mBuNhin.get(name);
	}

	public void put(String name, BuNhin buNhin) {
		this.mBuNhin.put(name, buNhin);
	}

	public Character[] getCharacters() {
		return (Character[]) this.mChar.values().toArray((Object[]) new Character[this.mChar.size()]);
	}

	public Monster[] getMonsters() {
		return (Monster[]) this.mMob.values().toArray((Object[]) new Monster[this.mMob.size()]);
	}

	public ItemMap[] getItemMaps() {
		return (ItemMap[]) this.mItemMap.values().toArray((Object[]) new ItemMap[this.mItemMap.size()]);
	}

	public BuNhin[] getBuNhins() {
		return (BuNhin[]) this.mBuNhin.values().toArray((Object[]) new BuNhin[this.mBuNhin.size()]);
	}

	public void sendMessage(Message ms) {
		for (Character pl : this.mChar.values()) {
			if (pl != null) {
				pl.sendMessage(ms);
			}
		}
	}

	public void removeItem(int id) {
		this.mItemMap.remove(Short.valueOf((short) id));
	}

	public void removeCharacter(int id) {
		this.mChar.remove(Integer.valueOf(id));
	}

	public void removeBuNhin(String name) {
		this.mBuNhin.remove(name);
	}

	public void removeItem() throws IOException {
		for (ItemMap item : this.listRemoveItem) {
			this.mItemMap.remove(Short.valueOf(item.id));
			Message ms = new Message(-15);
			DataOutputStream ds = ms.writer();
			ds.writeShort(item.id);
			ds.flush();
			sendMessage(ms);
		}
		this.listRemoveItem.clear();
	}

	public void addMob() throws IOException {
		Message mss = new Message(122);
		DataOutputStream ds = mss.writer();
		ds.writeByte(0);
		ds.writeByte(this.listRecoverys.size());
		for (Monster mob : this.listRecoverys) {
			ds.writeByte(mob.mobId);
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
			this.waitingListRecoverys.remove(mob);
		}
		ds.flush();
		sendMessage(mss);
		this.listRecoverys.clear();
	}

	public void createMonster() {
		int id = 0;
		for (TileMap.MonsterCoordinate mob : this.tilemap.monsterCoordinates) {
			MonsterTemplate template = Server.mobs.get(mob.templateId);

			Monster monster = new Monster(id, mob.templateId, template.hp, template.level, mob.x, mob.y,
					template.isBoss(), this);
			this.mMob.put(Integer.valueOf(id++), monster);
		}
	}

	public void update() {
		try {
			if (this.numberDropItem < 0 || this.numberDropItem >= Short.MAX_VALUE) {
				this.numberDropItem = 0;
			}
			if (this.mMob.size() > 0) {
				for (Monster mob : this.mMob.values()) {
					mob.update();
				}
			}
			if (this.mChar.size() > 0) {
				for (Map.Entry<Integer, Character> entry : this.mChar.entrySet()) {
					if (entry.getValue() == null) {
						this.listRemoveCharacter.add(entry.getKey());
					}
				}
				if (this.listRemoveCharacter.size() > 0) {
					for (Integer key : this.listRemoveCharacter) {
						this.mChar.remove(key);
					}
					this.listRemoveCharacter.clear();
				}
			}
			if (this.mItemMap.size() > 0) {
				for (ItemMap item : this.mItemMap.values()) {
					if (item != null) {
						item.timeCount--;
						if (item.timeCount <= 0) {
							this.listRemoveItem.add(item);
						}
					}
				}
				if (this.listRemoveItem.size() > 0) {
					removeItem();
				}
			}
			if (this.waitingListRecoverys.size() > 0) {
				for (Monster mob : this.waitingListRecoverys) {
					if (mob != null) {
						mob.recoveryTimeCount--;
						if (mob.recoveryTimeCount <= 0) {
							mob.recovery();
							this.listRecoverys.add(mob);
						}
					}
				}
				if (this.listRecoverys.size() > 0) {
					addMob();
				}
			}
			if (this.mChar.size() > 0) {
				for (Character _char : this.mChar.values()) {
					_char.update();
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(Zone.class.getName()).log(Level.SEVERE, (String) null, ex);
		}
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * server\Zone.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */