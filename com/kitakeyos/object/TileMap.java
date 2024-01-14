package com.kitakeyos.object;

import com.kitakeyos.template.NpcTemplate;
import com.kitakeyos.util.Logger;
import com.kitakeyos.util.NinjaUtil;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class TileMap {
	public static int T_EMPTY = 0;
	public static int T_TOP = 2;
	public static int T_LEFT = 4;
	public static int T_RIGHT = 8;
	public static int T_TREE = 16;
	public static int T_WATERFALL = 32;
	public static int T_WATERFLOW = 64;
	public static int T_TOPFALL = 128;
	public static int T_OUTSIDE = 256;
	public static int T_DOWN1PIXEL = 512;
	public static int T_BRIDGE = 1024;
	public static int T_UNDERWATER = 2048;
	public static int T_SOLIDGROUND = 4096;
	public static int T_BOTTOM = 8192;
	public static int T_DIE = 16384;
	public static int T_HEBI = 32768;
	public static int T_BANG = 65536;
	public static int T_JUM8 = 131072;
	public static int T_NT0 = 262144;
	public static int T_NT1 = 524288;

	private Logger logger = new Logger(getClass());

	public int id;

	public String name;

	public byte type;

	public byte tileId;

	public byte bgId;
	public ArrayList<NpcTemplate> npcs;
	public ArrayList<Waypoint> waypoints;

	public int tileAt(int x, int y) {
		int result;
		try {
			result = this.maps[y * this.tmw + x];
		} catch (Exception ex) {
			result = 1000;
		}
		return result;
	}

	public ArrayList<MonsterCoordinate> monsterCoordinates;
	public short tmw;
	public short tmh;
	public char[] maps;
	public int[] types;
	public short pxh;
	public short pxw;

	public boolean tileTypeAt(int px, int py, int t) {
		boolean result;
		try {
			result = ((this.types[py / 24 * this.tmw + px / 24] & t) == t);
		} catch (Exception ex) {
			result = false;
		}
		return result;
	}

	public int tileTypeAt(int x, int y) {
		int result;
		try {
			result = this.types[y * this.tmw + x];
		} catch (Exception ex) {
			result = 1000;
			ex.printStackTrace();
		}
		return result;
	}

	public int tileTypeAtPixel(int px, int py) {
		int result;
		try {
			result = this.types[py / 24 * this.tmw + px / 24];
		} catch (Exception ex) {
			result = 1000;
		}
		return result;
	}

	public int tileXofPixel(int px) {
		return px / 24 * 24;
	}

	public int tileYofPixel(int py) {
		return py / 24 * 24;
	}

	public void loadMap() {
		this.types = new int[this.tmw * this.tmh];
		this.pxh = (short) (this.tmh * 24);
		this.pxw = (short) (this.tmw * 24);
		try {
			int length = this.tmh * this.tmw;
			for (int i = 0; i < length; i++) {

				if (this.tileId == 4) {
					if (this.maps[i] == '\001' || this.maps[i] == '\002' || this.maps[i] == '\003'
							|| this.maps[i] == '\004' || this.maps[i] == '\005' || this.maps[i] == '\006'
							|| this.maps[i] == '\t' || this.maps[i] == '\n' || this.maps[i] == 'O'
							|| this.maps[i] == 'P' || this.maps[i] == '\r' || this.maps[i] == '\016'
							|| this.maps[i] == '+' || this.maps[i] == ',' || this.maps[i] == '-'
							|| this.maps[i] == '2') {
						this.types[i] = this.types[i] | T_TOP;
					}
					if (this.maps[i] == '\t' || this.maps[i] == '\013') {
						this.types[i] = this.types[i] | T_LEFT;
					}
					if (this.maps[i] == '\n' || this.maps[i] == '\f') {
						this.types[i] = this.types[i] | T_RIGHT;
					}
					if (this.maps[i] == '\r' || this.maps[i] == '\016') {
						this.types[i] = this.types[i] | T_BRIDGE;
					}
					if (this.maps[i] == 'L' || this.maps[i] == 'M') {
						this.types[i] = this.types[i] | T_WATERFLOW;
					}
				} else if (this.tileId == 1) {
					if (this.maps[i] == '\001' || this.maps[i] == '\002' || this.maps[i] == '\003'
							|| this.maps[i] == '\004' || this.maps[i] == '\005' || this.maps[i] == '\006'
							|| this.maps[i] == '\007' || this.maps[i] == '$' || this.maps[i] == '%'
							|| this.maps[i] == '6' || this.maps[i] == '[' || this.maps[i] == '\\' || this.maps[i] == ']'
							|| this.maps[i] == '^' || this.maps[i] == 'I' || this.maps[i] == 'J' || this.maps[i] == 'a'
							|| this.maps[i] == 'b' || this.maps[i] == 't' || this.maps[i] == 'u' || this.maps[i] == 'v'
							|| this.maps[i] == 'x' || this.maps[i] == '=') {
						this.types[i] = this.types[i] | T_TOP;
					}
					if (this.maps[i] == '\002' || this.maps[i] == '\003' || this.maps[i] == '\004'
							|| this.maps[i] == '\005' || this.maps[i] == '\006' || this.maps[i] == '\024'
							|| this.maps[i] == '\025' || this.maps[i] == '\026' || this.maps[i] == '\027'
							|| this.maps[i] == '$' || this.maps[i] == '%' || this.maps[i] == '&' || this.maps[i] == '\''
							|| this.maps[i] == '=') {
						this.types[i] = this.types[i] | T_SOLIDGROUND;
					}
					if (this.maps[i] == '\b' || this.maps[i] == '\t' || this.maps[i] == '\n' || this.maps[i] == '\f'
							|| this.maps[i] == '\r' || this.maps[i] == '\016' || this.maps[i] == '\036') {
						this.types[i] = this.types[i] | T_TREE;
					}
					if (this.maps[i] == '\021') {
						this.types[i] = this.types[i] | T_WATERFALL;
					}
					if (this.maps[i] == '\022') {
						this.types[i] = this.types[i] | T_TOPFALL;
					}
					if (this.maps[i] == '%' || this.maps[i] == '&' || this.maps[i] == '=') {
						this.types[i] = this.types[i] | T_LEFT;
					}
					if (this.maps[i] == '$' || this.maps[i] == '\'' || this.maps[i] == '=') {
						this.types[i] = this.types[i] | T_RIGHT;
					}
					if (this.maps[i] == '\023') {
						this.types[i] = this.types[i] | T_WATERFLOW;
						if ((this.types[i - this.tmw] & T_SOLIDGROUND) == T_SOLIDGROUND) {
							this.types[i] = this.types[i] | T_SOLIDGROUND;
						}
					}
					if (this.maps[i] == '#') {
						this.types[i] = this.types[i] | T_UNDERWATER;
					}
					if (this.maps[i] == '\007') {
						this.types[i] = this.types[i] | T_BRIDGE;
					}
					if (this.maps[i] == ' ' || this.maps[i] == '!' || this.maps[i] == '"') {
						this.types[i] = this.types[i] | T_OUTSIDE;
					}
				} else if (this.tileId == 2) {
					if (this.maps[i] == '\001' || this.maps[i] == '\002' || this.maps[i] == '\003'
							|| this.maps[i] == '\004' || this.maps[i] == '\005' || this.maps[i] == '\006'
							|| this.maps[i] == '\007' || this.maps[i] == '$' || this.maps[i] == '%'
							|| this.maps[i] == '6' || this.maps[i] == '=' || this.maps[i] == 'I' || this.maps[i] == 'L'
							|| this.maps[i] == 'M' || this.maps[i] == 'N' || this.maps[i] == 'O' || this.maps[i] == 'R'
							|| this.maps[i] == 'S' || this.maps[i] == 'b' || this.maps[i] == 'c' || this.maps[i] == 'd'
							|| this.maps[i] == 'f' || this.maps[i] == 'g' || this.maps[i] == 'l' || this.maps[i] == 'm'
							|| this.maps[i] == 'n' || this.maps[i] == 'p' || this.maps[i] == 'q' || this.maps[i] == 't'
							|| this.maps[i] == 'u' || this.maps[i] == '}' || this.maps[i] == '~' || this.maps[i] == ''
							|| this.maps[i] == '' || this.maps[i] == '') {
						this.types[i] = this.types[i] | T_TOP;
					}
					if (this.maps[i] == '\001' || this.maps[i] == '\003' || this.maps[i] == '\004'
							|| this.maps[i] == '\005' || this.maps[i] == '\006' || this.maps[i] == '\024'
							|| this.maps[i] == '\025' || this.maps[i] == '\026' || this.maps[i] == '\027'
							|| this.maps[i] == '$' || this.maps[i] == '%' || this.maps[i] == '&' || this.maps[i] == '\''
							|| this.maps[i] == '7' || this.maps[i] == 'm' || this.maps[i] == 'o' || this.maps[i] == 'p'
							|| this.maps[i] == 'q' || this.maps[i] == 'r' || this.maps[i] == 's' || this.maps[i] == 't'
							|| this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == '') {
						this.types[i] = this.types[i] | T_SOLIDGROUND;
					}
					if (this.maps[i] == '\b' || this.maps[i] == '\t' || this.maps[i] == '\n' || this.maps[i] == '\f'
							|| this.maps[i] == '\r' || this.maps[i] == '\016' || this.maps[i] == '\036'
							|| this.maps[i] == '') {
						this.types[i] = this.types[i] | T_TREE;
					}
					if (this.maps[i] == '\021') {
						this.types[i] = this.types[i] | T_WATERFALL;
					}
					if (this.maps[i] == '\022') {
						this.types[i] = this.types[i] | T_TOPFALL;
					}
					if (this.maps[i] == '=' || this.maps[i] == '%' || this.maps[i] == '&' || this.maps[i] == ''
							|| this.maps[i] == '' || this.maps[i] == '') {
						this.types[i] = this.types[i] | T_LEFT;
					}
					if (this.maps[i] == '=' || this.maps[i] == '$' || this.maps[i] == '\'' || this.maps[i] == ''
							|| this.maps[i] == '' || this.maps[i] == '') {
						this.types[i] = this.types[i] | T_RIGHT;
					}
					if (this.maps[i] == '\023') {
						this.types[i] = this.types[i] | T_WATERFLOW;
						if ((this.types[i - this.tmw] & T_SOLIDGROUND) == T_SOLIDGROUND) {
							this.types[i] = this.types[i] | T_SOLIDGROUND;
						}
					}
					if (this.maps[i] == '') {
						this.types[i] = this.types[i] | T_WATERFLOW;
						if ((this.types[i - this.tmw] & T_SOLIDGROUND) == T_SOLIDGROUND) {
							this.types[i] = this.types[i] | T_SOLIDGROUND;
						}
					}
					if (this.maps[i] == '#') {
						this.types[i] = this.types[i] | T_UNDERWATER;
					}
					if (this.maps[i] == '\007') {
						this.types[i] = this.types[i] | T_BRIDGE;
					}
					if (this.maps[i] == ' ' || this.maps[i] == '!' || this.maps[i] == '"') {
						this.types[i] = this.types[i] | T_OUTSIDE;
					}
					if (this.maps[i] == '=' || this.maps[i] == '') {
						this.types[i] = this.types[i] | T_BOTTOM;
					}
				} else if (this.tileId == 3) {
					if (this.maps[i] == '\001' || this.maps[i] == '\002' || this.maps[i] == '\003'
							|| this.maps[i] == '\004' || this.maps[i] == '\005' || this.maps[i] == '\006'
							|| this.maps[i] == '\007' || this.maps[i] == '\013' || this.maps[i] == '\016'
							|| this.maps[i] == '\021' || this.maps[i] == '+' || this.maps[i] == '3'
							|| this.maps[i] == '?' || this.maps[i] == 'A' || this.maps[i] == 'C' || this.maps[i] == 'D'
							|| this.maps[i] == 'G' || this.maps[i] == 'H' || this.maps[i] == 'S' || this.maps[i] == 'T'
							|| this.maps[i] == 'U' || this.maps[i] == 'W' || this.maps[i] == '[' || this.maps[i] == '^'
							|| this.maps[i] == 'a' || this.maps[i] == 'b' || this.maps[i] == 'j' || this.maps[i] == 'k'
							|| this.maps[i] == 'o' || this.maps[i] == 'q' || this.maps[i] == 'u' || this.maps[i] == 'v'
							|| this.maps[i] == 'w' || this.maps[i] == '}' || this.maps[i] == '~' || this.maps[i] == ''
							|| this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == ''
							|| this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == '') {
						this.types[i] = this.types[i] | T_TOP;
					}
					if (this.maps[i] == '|' || this.maps[i] == 't' || this.maps[i] == '{' || this.maps[i] == ','
							|| this.maps[i] == '\f' || this.maps[i] == '\017' || this.maps[i] == '\020'
							|| this.maps[i] == '-' || this.maps[i] == '\n' || this.maps[i] == '\t') {
						this.types[i] = this.types[i] | T_SOLIDGROUND;
					}
					if (this.maps[i] == '\027') {
						this.types[i] = this.types[i] | T_WATERFALL;
					}
					if (this.maps[i] == '\030') {
						this.types[i] = this.types[i] | T_TOPFALL;
					}
					if (this.maps[i] == '\006' || this.maps[i] == '\017' || this.maps[i] == '3' || this.maps[i] == '_'
							|| this.maps[i] == 'a' || this.maps[i] == 'j' || this.maps[i] == 'o' || this.maps[i] == '{'
							|| this.maps[i] == '}' || this.maps[i] == '' || this.maps[i] == '') {
						this.types[i] = this.types[i] | T_LEFT;
					}
					if (this.maps[i] == '\007' || this.maps[i] == '\020' || this.maps[i] == '3' || this.maps[i] == '`'
							|| this.maps[i] == 'b' || this.maps[i] == 'k' || this.maps[i] == 'o' || this.maps[i] == '|'
							|| this.maps[i] == '~' || this.maps[i] == '' || this.maps[i] == '') {
						this.types[i] = this.types[i] | T_RIGHT;
					}
					if (this.maps[i] == '\031') {
						this.types[i] = this.types[i] | T_WATERFLOW;
						if ((this.types[i - this.tmw] & T_SOLIDGROUND) == T_SOLIDGROUND) {
							this.types[i] = this.types[i] | T_SOLIDGROUND;
						}
					}
					if (this.maps[i] == '"') {
						this.types[i] = this.types[i] | T_UNDERWATER;
					}
					if (this.maps[i] == '\021') {
						this.types[i] = this.types[i] | T_BRIDGE;
					}
					if (this.maps[i] == '!' || this.maps[i] == 'g' || this.maps[i] == 'h' || this.maps[i] == 'i'
							|| this.maps[i] == '\032' || this.maps[i] == '!') {
						this.types[i] = this.types[i] | T_OUTSIDE;
					}
					if (this.maps[i] == '3' || this.maps[i] == 'o' || this.maps[i] == 'D') {
						this.types[i] = this.types[i] | T_BOTTOM;
					}
					if (this.maps[i] == 'R' || this.maps[i] == 'n' || this.maps[i] == '') {
						this.types[i] = this.types[i] | T_DIE;
					}
					if (this.maps[i] == 'q') {
						this.types[i] = this.types[i] | T_BANG;
					}
					if (this.maps[i] == '') {
						this.types[i] = 0x8000 | this.types[i];
					}
					if (this.maps[i] == '(' || this.maps[i] == ')') {
						this.types[i] = this.types[i] | T_JUM8;
					}
					if (this.maps[i] == 'n') {
						this.types[i] = this.types[i] | T_NT0;
					}
					if (this.maps[i] == '') {
						this.types[i] = this.types[i] | T_NT1;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void loadMapFromResource() {
		try {
			if (this.id == 0 || this.id == 56 || (this.id > 72 && this.id < 125) || (this.id > 125 && this.id < 133)
					|| (this.id > 133 && this.id < 139) || this.id > 148) {
				return;
			}
			byte[] ab = NinjaUtil.getFile("Data/Map/" + this.id);
			this.logger.debug("LoadMapFromResource", "mapId: " + this.id + " size: " + ab.length);
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(ab));
			this.tmw = (short) dis.read();
			this.tmh = (short) dis.read();
			this.maps = new char[dis.available()];
			int size = this.tmw * this.tmh;
			for (int i = 0; i < size; i++) {
				this.maps[i] = (char) dis.readByte();
			}
			loadMap();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static class MonsterCoordinate {
		public short templateId;
		public short x;
		public short y;

		public MonsterCoordinate(short templateId, short x, short y) {
			this.templateId = templateId;
			this.x = x;
			this.y = y;
		}
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * object\TileMap.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */