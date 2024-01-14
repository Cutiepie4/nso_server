package com.kitakeyos.server;

import com.kitakeyos.object.TileMap;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Map implements Runnable {
	public int mapId;
	private HashMap<Integer, Zone> zones = new HashMap<>();
	public TileMap tilemap;
	public static boolean running = true;

	public Map(int id) {
		this.mapId = id;
		for (TileMap tile : Server.maps) {
			if (tile.id == this.mapId) {
				this.tilemap = tile;
				break;
			}
		}
		for (int i = 0; i < 30; i++) {
			this.zones.put(Integer.valueOf(i), new Zone((byte) i, this.tilemap));
		}
		(new Thread(this)).start();
	}

	public Collection<Zone> getZones() {
		return this.zones.values();
	}

	public Zone getZoneById(int id) {
		return this.zones.get(Integer.valueOf(id));
	}

	public void run() {
		while (running) {
			long l1 = System.currentTimeMillis();
			Collection<Zone> zones = getZones();
			for (Zone zone : zones) {
				zone.update();
			}
			long l2 = System.currentTimeMillis() - l1;
			if (l2 < 1000L) {
				try {
					Thread.sleep(1000L - l2);
				} catch (InterruptedException interruptedException) {
				}
			}
		}
	}

	public void joinZone(Character pl, byte zoneId) {
		try {
			Zone z = getZoneById(zoneId);
			if (z != null) {
				pl.map = this;
				pl.zone = z;
				z.put(pl.id, pl);
				Character[] characters = z.getCharacters();
				z.numberCharacter = (byte) characters.length;
				for (Character _char : characters) {
					if (_char != null && _char.user != null && !_char.equals(pl)) {
						_char.user.service.sendCharInfo(pl);
					}
				}
				if (pl.mount[4] != null) {
					pl.user.service.sendMount();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void outZone(Character pl) {
		Zone z = pl.zone;
		z.removeCharacter(pl.id);
		Character[] characters = z.getCharacters();
		z.numberCharacter = (byte) characters.length;
		for (Character _char : characters) {
			if (_char != null && !_char.equals(pl))
				try {
					_char.user.service.outZone(pl.id);
				} catch (IOException ex) {
					Logger.getLogger(MapManager.class.getName()).log(Level.SEVERE, (String) null, ex);
				}
		}
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * server\Map.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */