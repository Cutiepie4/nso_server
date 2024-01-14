package com.kitakeyos.util;

import com.kitakeyos.server.Server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NinjaUtil {
	public static Random rand = new Random();
	public static NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi"));

	public static int nextInt(int max) {
		return rand.nextInt(max);
	}

	public static String getCurrency(long number) {
		return numberFormat.format(number);
	}

	public static int nextInt(int[] percen) {
		int next = nextInt(100);
		int i;
		for (i = 0; i < percen.length; i++) {
			if (next < percen[i]) {
				return i;
			}
			next -= percen[i];
		}
		return i;
	}

	public static short[] getXY(int map) {
		short[] xy = new short[2];
		if (map == 22) {
			xy[0] = 228;
			xy[1] = 192;
		} else if (map == 10) {
			xy[0] = 109;
			xy[1] = 264;
		} else if (map == 17) {
			xy[0] = 1480;
			xy[1] = 264;
		} else if (map == 32) {
			xy[0] = 2502;
			xy[1] = 384;
		} else if (map == 38) {
			xy[0] = 397;
			xy[1] = 336;
		} else if (map == 43) {
			xy[0] = 2529;
			xy[1] = 240;
		} else if (map == 48) {
			xy[0] = 131;
			xy[1] = 432;
		} else if (map == 1) {
			xy[0] = 193;
			xy[1] = 384;
		} else if (map == 27) {
			xy[0] = 647;
			xy[1] = 408;
		} else if (map == 72) {
			xy[0] = 1611;
			xy[1] = 672;
		} else {
			xy[0] = 0;
			xy[1] = 0;
		}
		return xy;
	}

	public static int nextInt(int min, int max) {
		if (min >= max) {
			return max;
		}
		return min + rand.nextInt(max - min);
	}

	public static void setOption(int[][] option, int up1, int up2) {
		int num = up2 - up1;
		for (int i = 0; i < option.length; i++) {
			if (option[i][0] == 6 || option[i][0] == 7) {
				option[i][1] = option[i][1] + 15 * num;
			} else if (option[i][0] == 8 || option[i][0] == 9 || option[i][0] == 19) {
				option[i][1] = option[i][1] + 10 * num;
			} else if (option[i][0] == 10 || option[i][0] == 11 || option[i][0] == 12 || option[i][0] == 13
					|| option[i][0] == 14 || option[i][0] == 15 || option[i][0] == 17 || option[i][0] == 18
					|| option[i][0] == 20) {
				option[i][1] = option[i][1] + 5 * num;
			} else if (option[i][0] == 21 || option[i][0] == 22 || option[i][0] == 23 || option[i][0] == 24
					|| option[i][0] == 25 || option[i][0] == 26) {
				option[i][1] = option[i][1] + 150 * num;
			} else if (option[i][0] == 16) {
				option[i][1] = option[i][1] + 3 * num;
			}
		}
	}

	public static int[][] getOptionShop(int[][] option) {
		int[][] result = new int[option.length][2];
		for (int i = 0; i < option.length; i++) {
			if (option[i][0] == 0 || option[i][0] == 1 || option[i][0] == 21 || option[i][0] == 22 || option[i][0] == 23
					|| option[i][0] == 24 || option[i][0] == 25 || option[i][0] == 26) {
				result[i][1] = option[i][1] - 49;
			} else if (option[i][0] == 6 || option[i][0] == 7 || option[i][0] == 8 || option[i][0] == 9
					|| option[i][0] == 19) {
				result[i][1] = option[i][1] - 9;
			} else if (option[i][0] == 2 || option[i][0] == 3 || option[i][0] == 4 || option[i][0] == 5
					|| option[i][0] == 10 || option[i][0] == 11 || option[i][0] == 12 || option[i][0] == 13
					|| option[i][0] == 14 || option[i][0] == 15 || option[i][0] == 17 || option[i][0] == 18) {
				result[i][1] = option[i][1] - 4;
			} else if (option[i][0] == 16) {
				result[i][1] = option[i][1] - 2;
			} else {
				result[i][1] = option[i][1];
			}
		}
		return result;
	}

	public static String getColor(String color) {
		if (color.equals("tahoma_7_white")) {
			return "\nc0";
		}
		if (color.equals("tahoma_7b_yellow")) {
			return "\nc1";
		}
		if (color.equals("tahoma_7b_white")) {
			return "\nc2";
		}
		if (color.equals("tahoma_7_yellow")) {
			return "\nc3";
		}
		if (color.equals("tahoma_7b_red")) {
			return "\nc4";
		}
		if (color.equals("tahoma_7_red")) {
			return "\nc5";
		}
		if (color.equals("tahoma_7_grey")) {
			return "\nc6";
		}
		if (color.equals("tahoma_7b_blue")) {
			return "\nc7";
		}
		if (color.equals("tahoma_7_blue")) {
			return "\nc8";
		}
		if (color.equals("tahoma_7_green")) {
			return "\nc9";
		}
		return "\nc0";
	}

	public static byte[] getFile(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			byte[] ab = new byte[fis.available()];
			fis.read(ab, 0, ab.length);
			fis.close();
			return ab;
		} catch (IOException ex) {
			Logger.getLogger(NinjaUtil.class.getName()).log(Level.SEVERE, (String) null, ex);

			return null;
		}
	}

	public static void saveFile(String url, byte[] ab) {
		try {
			File f = new File(url);
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(url);
			fos.write(ab);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getRange(int x1, int x2) {
		return (int) Math.sqrt(Math.pow((x2 - x1), 2.0D));
	}

	public static int getLevel(long num) {
		for (int i = 0; i < Server.exps.length; i++) {
			if (num < Server.exps[i]) {
				return i;
			}
			num -= Server.exps[i];
		}
		return 1;
	}

	public static long getExp(int level) {
		long exp = 0L;
		for (int i = 0; i < level; i++) {
			exp += Server.exps[i];
		}
		return exp;
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyo\\
 * util\NinjaUtil.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */