package com.kitakeyos.object;

import com.kitakeyos.server.Character;

public class ItemMap extends Thread {
	public short id;
	public Character.Item item;
	public Character owner;
	public short x;
	public short y;
	public int timeCount;

	public ItemMap(short id) {
		this.id = id;
		this.timeCount = 60;
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * object\ItemMap.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */