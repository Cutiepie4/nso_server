package com.kitakeyos.server;

import com.kitakeyos.object.TileMap;
import java.util.HashMap;

public class MapManager {
	private static HashMap<Integer, Map> maps = new HashMap<>();

	public static Map getMapById(int id) {
		return maps.get(Integer.valueOf(id));
	}

	public static void init() {
		for (TileMap tile : Server.maps) {
			if (tile.maps != null) {
				maps.put(Integer.valueOf(tile.id), new Map(tile.id));
			}
		}
	}

	public static void joinZone(Character pl, int mapId, byte zoneId) {
		Map m = getMapById(mapId);
		if (m != null) {
			m.joinZone(pl, zoneId);
		}
	}

	public static void outZone(Character pl) {
		if (pl != null && pl.map != null) {
			Map m = getMapById(pl.mapId);
			if (m != null)
				m.outZone(pl);
		}
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * server\MapManager.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.3
 */